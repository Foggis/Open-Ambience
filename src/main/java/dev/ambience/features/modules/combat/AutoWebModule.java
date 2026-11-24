package dev.ambience.features.modules.combat;

import dev.ambience.features.modules.Module;
import dev.ambience.features.modules.movement.LungeSwapModule;
import dev.ambience.features.modules.movement.PearlCatchModule;
import dev.ambience.features.modules.movement.WindHopModule;
import dev.ambience.features.modules.render.FreecamModule;
import dev.ambience.features.settings.Setting;
import dev.ambience.inject.Access;
import dev.ambience.nativebridge.NativeModules;
import dev.ambience.util.GrimMovementGuard;
import dev.ambience.util.HotbarUtil;
import dev.ambience.util.ModuleDebug;
import dev.ambience.util.RotationHelper;
import dev.ambience.util.SilentRotation;
import dev.ambience.util.Timer;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.class_1268;
import net.minecraft.class_1657;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;
import org.joml.Vector2f;

public class AutoWebModule extends Module {
   private static AutoWebModule INSTANCE;
   private static final double BLOCK_REACH_PAD = 0.03;
   private static final class_2350[] SUPPORT_ORDER;
   private final Setting<Vector2f> delay = this.vec2f("Delay", 30.0F, 30.0F).setVec2TrackBounds(0.0F, 250.0F).setPage("General");
   private final Setting<SwitchBack> switchBack;
   private final Setting<Integer> switchSlot;
   private final Timer placeTimer;
   private long nextDelayMs;
   private boolean heldSilent;
   private boolean pendingSilentEnd;
   private class_1657 target;
   private PlacePhase placePhase;
   private PlaceTarget queuedPlace;
   private int queuedWebSlot;
   private int queuedRestoreSlot;
   private int phaseWaitTicks;
   private int queuedTicks;
   private final Map<class_2338, Integer> attemptedPositions;

   public AutoWebModule() {
      super("AutoWeb", "Places cobwebs on nearby opponents.", Module.Category.COMBAT);
      this.switchBack = this.mode("Switch Back", AutoWebModule.SwitchBack.Slot).setPage("General");
      this.switchSlot = this.num("Switch Slot", 1, 1, 9).setPage("General").setVisibility((v) -> this.switchBack.getValue() == AutoWebModule.SwitchBack.Slot);
      this.placeTimer = new Timer();
      this.placePhase = AutoWebModule.PlacePhase.IDLE;
      this.queuedWebSlot = -1;
      this.queuedRestoreSlot = -1;
      this.attemptedPositions = new HashMap();
      INSTANCE = this;
      this.nextDelayMs = this.rollDelayMs();
   }

   public static boolean isBusy() {
      AutoWebModule module = INSTANCE;
      return module != null && module.isEnabled() && (module.heldSilent || module.pendingSilentEnd || module.placePhase != AutoWebModule.PlacePhase.IDLE);
   }

   public String getDisplayInfo() {
      if (this.target != null) {
         return this.target.method_5477().getString();
      } else {
         Vector2f value = this.delay.getValue();
         return Math.round(value.x) == Math.round(value.y) ? Math.round(value.x) + "ms" : String.format("%.0f-%.0fms", value.x(), value.y());
      }
   }

   public String getDebugState() {
      if (this.target == null && !this.heldSilent && !this.pendingSilentEnd) {
         return null;
      } else {
         String var10000 = this.target == null ? "-" : this.target.method_5477().getString();
         return var10000 + " silent=" + this.heldSilent + " pendingEnd=" + this.pendingSilentEnd;
      }
   }

   public void onEnable() {
      this.nextDelayMs = this.rollDelayMs();
      this.heldSilent = false;
      this.pendingSilentEnd = false;
      this.target = null;
      this.clearQueuedPlace();
      this.attemptedPositions.clear();
   }

   public void onDisable() {
      SilentRotation.end();
      this.forceRestoreSlot();
      this.heldSilent = false;
      this.pendingSilentEnd = false;
      this.target = null;
      this.clearQueuedPlace();
   }

   public void onPreTick() {
      if (!nullCheck() && mc.field_1761 != null) {
         if (!WindHopModule.isBusy() && !PearlCatchModule.isBusy()) {
            this.attemptedPositions.replaceAll((pos, ticks) -> ticks - 1);
            this.attemptedPositions.values().removeIf((ticks) -> ticks <= 0);
            if (this.placePhase == AutoWebModule.PlacePhase.WAIT_RESTORE) {
               this.tickRestoreSlot();
            } else {
               if (this.pendingSilentEnd) {
                  this.pendingSilentEnd = false;
                  if (this.heldSilent && !AutoMaceModule.isBusy()) {
                     SilentRotation.end();
                     this.heldSilent = false;
                     return;
                  }

                  this.heldSilent = false;
               }

               if (mc.field_1755 == null && !FreecamModule.blocksInteraction() && !AnchorMacroModule.blocksInteraction() && !AutoMaceModule.isBusy() && !AutoEscModule.isBusy() && !ShieldStunModule.isBusy() && !LungeSwapModule.isBusy()) {
                  if (SilentRotation.ownedBy(SilentRotation.Owner.COMBAT) && !this.heldSilent) {
                     this.target = null;
                  } else if (!GrimMovementGuard.actionThisTick()) {
                     if (this.placePhase == AutoWebModule.PlacePhase.WAIT_PLACE) {
                        this.tickQueuedPlace();
                     } else {
                        int webSlot = HotbarUtil.find(mc.field_1724, class_1802.field_8786);
                        PlaceTarget place = webSlot == -1 ? null : this.resolvePlace();
                        int planInput = (webSlot != -1 ? 1 : 0) | (place != null ? 2 : 0);
                        double reach = this.reach();
                        if (place != null) {
                           if (GrimMovementGuard.canInteractBlock(mc.field_1724, place.support, reach)) {
                              planInput |= 4;
                           }

                           if (GrimMovementGuard.currentHitsBlock(mc.field_1724, place.support, reach)) {
                              planInput |= 8;
                           }

                           if (this.placeTimer.passed(this.nextDelayMs)) {
                              planInput |= 16;
                           }
                        }

                        int action = NativeModules.autoWebPlan(planInput);
                        if (action == 0) {
                           this.target = null;
                           this.queueSilentEnd();
                        } else {
                           this.target = place.player;
                           if (action == 3) {
                              this.queuePlace(place, webSlot);
                           } else if (action == 2) {
                              this.aimAt(place.hit.method_17784());
                           }

                        }
                     }
                  }
               } else {
                  this.target = null;
                  this.beginRestoreSlot();
                  this.queueSilentEnd();
               }
            }
         }
      }
   }

   private PlaceTarget resolvePlace() {
      PlaceTarget bestPlace = null;
      double bestDist = Double.MAX_VALUE;

      for(class_1657 player : mc.field_1687.method_18456()) {
         if (this.validTarget(player)) {
            double dist = mc.field_1724.method_5858(player);
            if (!(dist >= bestDist)) {
               PlaceTarget candidate = this.placeFor(player);
               if (candidate != null && !this.attemptedPositions.containsKey(candidate.dest)) {
                  bestDist = dist;
                  bestPlace = candidate;
               }
            }
         }
      }

      return bestPlace;
   }

   private boolean validTarget(class_1657 player) {
      class_2338 self = mc.field_1724.method_24515();
      int flags = (player != mc.field_1724 ? 1 : 0) | (player.method_5805() ? 2 : 0) | (!player.method_7325() ? 4 : 0) | (!player.method_24515().equals(self) ? 8 : 0);
      return NativeModules.autoWebTarget(flags, (double)mc.field_1724.method_5739(player), this.reach() + (double)1.5F);
   }

   private PlaceTarget placeFor(class_1657 player) {
      class_2338 dest = player.method_24515();
      if (dest.equals(mc.field_1724.method_24515())) {
         return null;
      } else {
         class_3965 hit = this.placeHitFor(dest);
         if (hit == null) {
            return null;
         } else {
            return mc.field_1724.method_33571().method_1025(class_243.method_24953(hit.method_17777())) > this.reach() * this.reach() ? null : new PlaceTarget(player, dest, hit.method_17777(), hit);
         }
      }
   }

   private class_3965 placeHitFor(class_2338 dest) {
      if (!this.canWeb(dest)) {
         return null;
      } else {
         for(class_2350 towardSupport : SUPPORT_ORDER) {
            class_2338 support = dest.method_10093(towardSupport);
            if (this.isSupport(support)) {
               return RotationHelper.buildPlaceHit(support, towardSupport.method_10153());
            }
         }

         return null;
      }
   }

   private boolean canWeb(class_2338 pos) {
      class_2680 state = mc.field_1687.method_8320(pos);
      return !state.method_27852(class_2246.field_10343) && state.method_45474();
   }

   private boolean isSupport(class_2338 pos) {
      class_2680 state = mc.field_1687.method_8320(pos);
      return !state.method_26215() && !state.method_45474() && state.method_26227().method_15769();
   }

   private void aimAt(class_243 point) {
      float[] angles = GrimMovementGuard.anglesTo(mc.field_1724, point);
      if (angles != null) {
         GrimMovementGuard.applyMacroLook(mc.field_1724, angles[0], angles[1], true);
         this.heldSilent = SilentRotation.ownedBy(SilentRotation.Owner.COMBAT);
      }
   }

   private void queuePlace(PlaceTarget place, int webSlot) {
      int previous = mc.field_1724.method_31548().method_67532();
      this.queuedRestoreSlot = NativeModules.autoWebRestoreSlot(((SwitchBack)this.switchBack.getValue()).ordinal(), (Integer)this.switchSlot.getValue() - 1, previous);
      this.queuedPlace = place;
      this.queuedWebSlot = webSlot;
      this.queuedTicks = 0;
      boolean switched = previous != webSlot;
      if (switched) {
         if (!HotbarUtil.select(mc.field_1724, webSlot) || !HotbarUtil.holding(mc.field_1724, class_1802.field_8786)) {
            this.clearQueuedPlace();
            return;
         }

         GrimMovementGuard.markAction();
      }

      this.phaseWaitTicks = switched ? 1 : 0;
      this.placePhase = AutoWebModule.PlacePhase.WAIT_PLACE;
   }

   private void tickQueuedPlace() {
      if (this.phaseWaitTicks-- <= 0 && !GrimMovementGuard.actionThisTick()) {
         if (++this.queuedTicks <= 10 && this.queuedPlace != null && this.queuedWebSlot >= 0 && this.queuedPlace.player.method_5805() && this.canWeb(this.queuedPlace.dest) && this.isSupport(this.queuedPlace.support) && HotbarUtil.holding(mc.field_1724, class_1802.field_8786)) {
            double reach = this.reach();
            if (!GrimMovementGuard.canInteractBlock(mc.field_1724, this.queuedPlace.support, reach)) {
               if (!GrimMovementGuard.currentHitsBlock(mc.field_1724, this.queuedPlace.support, reach)) {
                  this.aimAt(this.queuedPlace.hit.method_17784());
               }

            } else {
               this.sendQueuedPlace(reach);
               this.attemptedPositions.put(this.queuedPlace.dest.method_10062(), 60);
               this.placeTimer.reset();
               this.nextDelayMs = this.rollDelayMs();
               this.beginRestoreSlot();
            }
         } else {
            this.beginRestoreSlot();
         }
      }
   }

   private boolean sendQueuedPlace(double reach) {
      PlaceTarget place = this.queuedPlace;
      class_3965 liveHit = RotationHelper.raycastBlock(mc.field_1687, mc.field_1724, mc.field_1724.method_33571(), mc.field_1724.method_36455(), mc.field_1724.method_36454(), reach);
      if (liveHit != null && liveHit.method_17783() == class_240.field_1332 && liveHit.method_17777().equals(place.support)) {
         Access.setRightClickDelay(mc, 0);
         GrimMovementGuard.markAction();
         boolean placed = mc.field_1761.method_2896(mc.field_1724, class_1268.field_5808, liveHit).method_23665();
         if (placed) {
            mc.field_1724.method_6104(class_1268.field_5808);
            String var10000 = this.getName();
            String var10002 = place.dest.method_23854();
            ModuleDebug.module(var10000, "place", var10002 + " on=" + place.player.method_5477().getString());
         }

         return placed;
      } else {
         return false;
      }
   }

   private void beginRestoreSlot() {
      this.queuedPlace = null;
      this.queuedWebSlot = -1;
      this.queuedTicks = 0;
      if (this.queuedRestoreSlot >= 0 && mc.field_1724 != null && mc.field_1724.method_31548().method_67532() != this.queuedRestoreSlot) {
         this.phaseWaitTicks = 1;
         this.placePhase = AutoWebModule.PlacePhase.WAIT_RESTORE;
      } else {
         this.clearQueuedPlace();
         this.queueSilentEnd();
      }

   }

   private void tickRestoreSlot() {
      if (this.phaseWaitTicks-- <= 0 && !GrimMovementGuard.actionThisTick()) {
         if (this.queuedRestoreSlot >= 0 && this.queuedRestoreSlot <= 8) {
            HotbarUtil.select(mc.field_1724, this.queuedRestoreSlot);
            GrimMovementGuard.markAction();
         }

         this.clearQueuedPlace();
         this.target = null;
         this.queueSilentEnd();
      }
   }

   private void forceRestoreSlot() {
      if (this.queuedRestoreSlot >= 0 && this.queuedRestoreSlot <= 8 && mc.field_1724 != null && mc.field_1761 != null) {
         HotbarUtil.select(mc.field_1724, this.queuedRestoreSlot);
      }

   }

   private void clearQueuedPlace() {
      this.placePhase = AutoWebModule.PlacePhase.IDLE;
      this.queuedPlace = null;
      this.queuedWebSlot = -1;
      this.queuedRestoreSlot = -1;
      this.phaseWaitTicks = 0;
      this.queuedTicks = 0;
   }

   private void queueSilentEnd() {
      if (this.placePhase == AutoWebModule.PlacePhase.IDLE) {
         if (this.heldSilent) {
            if (!AutoMaceModule.isBusy() && !AnchorMacroModule.blocksInteraction()) {
               if (SilentRotation.ownedBy(SilentRotation.Owner.COMBAT)) {
                  this.pendingSilentEnd = true;
               }

            }
         }
      }
   }

   private double reach() {
      return mc.field_1724.method_55754() + 0.03;
   }

   private long rollDelayMs() {
      Vector2f d = this.delay.getValue();
      int min = Math.max(0, Math.round(Math.min(d.x, d.y)));
      int max = Math.max(min, Math.round(Math.max(d.x, d.y)));
      return NativeModules.rollDelay(min, max == Integer.MAX_VALUE ? max : max + 1, System.nanoTime() ^ (mc.field_1724 == null ? 0L : (long)mc.field_1724.field_6012));
   }

   static {
      SUPPORT_ORDER = new class_2350[]{class_2350.field_11033, class_2350.field_11043, class_2350.field_11035, class_2350.field_11034, class_2350.field_11039, class_2350.field_11036};
   }

   private static enum PlacePhase {
      IDLE,
      WAIT_PLACE,
      WAIT_RESTORE;

      // $FF: synthetic method
      private static PlacePhase[] $values() {
         return new PlacePhase[]{IDLE, WAIT_PLACE, WAIT_RESTORE};
      }
   }

   public static enum SwitchBack {
      Slot,
      Previous,
      None;

      // $FF: synthetic method
      private static SwitchBack[] $values() {
         return new SwitchBack[]{Slot, Previous, None};
      }
   }

   private static record PlaceTarget(class_1657 player, class_2338 dest, class_2338 support, class_3965 hit) {
   }
}
