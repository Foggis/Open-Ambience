package dev.ambience.features.modules.combat;

import dev.ambience.features.modules.Module;
import dev.ambience.features.modules.movement.PearlCatchModule;
import dev.ambience.features.modules.movement.WindHopModule;
import dev.ambience.features.settings.Bind;
import dev.ambience.features.settings.Setting;
import dev.ambience.nativebridge.NativeModules;
import dev.ambience.util.GrimMovementGuard;
import dev.ambience.util.HotbarUtil;
import dev.ambience.util.RotationHelper;
import dev.ambience.util.SilentRotation;
import dev.ambience.util.Timer;
import net.minecraft.class_10590;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1531;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1887;
import net.minecraft.class_1890;
import net.minecraft.class_1893;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_3489;
import net.minecraft.class_3532;
import net.minecraft.class_3966;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import net.minecraft.class_7924;
import net.minecraft.class_9334;

public class AutoMaceModule extends Module {
   private static AutoMaceModule INSTANCE;
   private static final double GRIM_REACH = (double)3.0F;
   private static final long SMASH_COOLDOWN_MS = 400L;
   private final Setting<Double> minFallDistance = this.num("MinFallDistance", (double)1.5F, (double)1.5F, (double)64.0F).setPage("General");
   private final Setting<Double> horizontalTargetDist = this.num("HorizontalTargetDist", (double)5.0F, (double)1.0F, (double)16.0F).setPage("General");
   private final Setting<Boolean> trigger = this.bool("Trigger", true).setPage("General");
   private final Setting<Boolean> silentAim = this.bool("SilentAim", true).setPage("General");
   private final Setting<Double> maxYaw = this.num("MaxYaw", (double)180.0F, (double)10.0F, (double)180.0F).setPage("General");
   private final Setting<Boolean> switchMode = this.bool("Switch", true).setPage("General");
   private final Setting<Boolean> restoreSlot = this.bool("RestoreSlot", true).setPage("General");
   private final Setting<Boolean> breachCheck = this.bool("BreachCheck", false).setPage("General");
   private final Setting<Boolean> takeoffElytra = this.bool("TakeoffElytra", false).setPage("General");
   private final Setting<Boolean> expandHitbox = this.bool("ExpandHitbox", false).setPage("General");
   private final Setting<Double> hitboxExpansion = this.num("HitboxExpansion", (double)0.5F, (double)0.0F, (double)2.0F).setPage("General");
   private final Setting<StunSlamMode> stunSlamMode;
   private final Setting<Double> chance;
   private final Setting<Boolean> scaleWithFall;
   private final Setting<Double> stunMinFall;
   private final Setting<Integer> axeHoldDelay;
   private final Setting<Bind> stunBind;
   private Phase phase;
   private class_1309 target;
   private boolean stunThisHit;
   private Boolean stunRoll;
   private int savedSlot;
   private int maceSlot;
   private int axeSlot;
   private int axeHoldTicks;
   private int restoreSilentTicks;
   private int pendingRestoreSlot;
   private final Timer smashCooldown;

   public AutoMaceModule() {
      super("AutoMace", "Fall smash: aim, attribute-swap mace on the hit, restore. Optional axe stun first.", Module.Category.COMBAT);
      this.stunSlamMode = this.mode("StunSlamMode", AutoMaceModule.StunSlamMode.Chance).setPage("Stun Slam");
      this.chance = this.num("Chance", (double)100.0F, (double)0.0F, (double)100.0F).setPage("Stun Slam");
      this.scaleWithFall = this.bool("ScaleWithFall", false).setPage("Stun Slam");
      this.stunMinFall = this.num("StunMinFall", (double)0.0F, (double)0.0F, (double)64.0F).setPage("Stun Slam");
      this.axeHoldDelay = this.num("AxeHoldDelay", 0, 0, 10).setPage("Stun Slam");
      this.stunBind = this.key("StunBind", Bind.none()).setPage("Stun Slam");
      this.phase = AutoMaceModule.Phase.IDLE;
      this.savedSlot = -1;
      this.maceSlot = -1;
      this.axeSlot = -1;
      this.pendingRestoreSlot = -1;
      this.smashCooldown = new Timer();
      INSTANCE = this;
      this.maxYaw.setVisibility((v) -> (Boolean)this.silentAim.getValue());
      this.hitboxExpansion.setVisibility((v) -> (Boolean)this.expandHitbox.getValue());
      this.chance.setVisibility((v) -> this.stunSlamMode.getValue() == AutoMaceModule.StunSlamMode.Chance);
      this.scaleWithFall.setVisibility((v) -> this.stunSlamMode.getValue() == AutoMaceModule.StunSlamMode.Chance);
      this.stunBind.setVisibility((v) -> this.stunSlamMode.getValue() == AutoMaceModule.StunSlamMode.Bind);
      this.stunMinFall.setVisibility((v) -> this.stunSlamMode.getValue() != AutoMaceModule.StunSlamMode.Off);
      this.axeHoldDelay.setVisibility((v) -> this.stunSlamMode.getValue() != AutoMaceModule.StunSlamMode.Off);
   }

   public static boolean isBusy() {
      AutoMaceModule module = INSTANCE;
      return module != null && NativeModules.autoMaceBusy(module.isEnabled(), module.nativePhase(), module.restoreSilentTicks, module.savedSlot, module.pendingRestoreSlot);
   }

   public String getDisplayInfo() {
      return this.target != null ? this.target.method_5477().getString() : String.format("%.1f fall", this.minFallDistance.getValue());
   }

   public String getDebugState() {
      if (this.phase == AutoMaceModule.Phase.IDLE && this.restoreSilentTicks <= 0 && this.target == null) {
         return null;
      } else {
         String var10000 = this.phase.name();
         return var10000 + " stun=" + this.stunThisHit + " look=" + (this.target != null && this.lookReady(this.target)) + " target=" + (this.target == null ? "-" : this.target.method_5477().getString());
      }
   }

   public void onDisable() {
      SilentRotation.end();
      this.resetState();
      this.restoreSilentTicks = 0;
      this.pendingRestoreSlot = -1;
   }

   public void onPreTick() {
      if (!nullCheck() && mc.field_1761 != null) {
         this.flushPendingRestore();
         if (!WindHopModule.isBusy() && !PearlCatchModule.isBusy()) {
            this.tickSilentRestore();
            if (mc.field_1755 == null && !AnchorMacroModule.blocksInteraction()) {
               if (!mc.field_1724.method_24828() && !mc.field_1724.method_31549().field_7479 && !mc.field_1724.method_5765()) {
                  if (mc.field_1724.field_6017 < (double)((Double)this.minFallDistance.getValue()).floatValue()) {
                     this.stopCombo();
                  } else if (!this.tryTakeoffElytra()) {
                     if (mc.field_1724.method_6128()) {
                        this.stopCombo();
                     } else if (this.smashCooldown.passed(400L)) {
                        this.maceSlot = this.findMaceSlot();
                        if (this.maceSlot == -1) {
                           this.stopCombo();
                        } else {
                           class_1309 candidate = this.keepCurrentTarget() ? this.target : this.findTarget();
                           if (candidate != null && candidate.method_5805() && !this.passedTarget(candidate)) {
                              if (this.target != candidate) {
                                 this.stunRoll = null;
                                 this.axeHoldTicks = 0;
                                 this.axeSlot = -1;
                              }

                              this.target = candidate;
                              this.stunThisHit = this.wantsStun(this.target);
                              if (this.stunThisHit && this.axeSlot == -1) {
                                 this.axeSlot = this.findBestAxeSlot();
                                 if (this.axeSlot != -1) {
                                    this.axeHoldTicks = (Integer)this.axeHoldDelay.getValue();
                                 } else {
                                    this.stunThisHit = false;
                                 }
                              }

                              if (this.phase == AutoMaceModule.Phase.IDLE) {
                                 if (this.savedSlot == -1) {
                                    this.savedSlot = mc.field_1724.method_31548().method_67532();
                                 }

                                 if (this.stunThisHit && (this.axeSlot == -1 || !this.prepareSlot(this.axeSlot))) {
                                    this.stunThisHit = false;
                                 }

                                 this.phase = AutoMaceModule.Phase.AIM;
                              }

                              if (this.phase == AutoMaceModule.Phase.AIM) {
                                 this.tickAim();
                              } else {
                                 if (this.phase == AutoMaceModule.Phase.SMASH) {
                                    this.tickSmash();
                                 }

                              }
                           } else {
                              this.stopCombo();
                           }
                        }
                     }
                  }
               } else {
                  this.stopCombo();
               }
            } else {
               this.stopCombo();
            }
         }
      }
   }

   public void onPostPositionTick() {
   }

   private boolean keepCurrentTarget() {
      return this.phase != AutoMaceModule.Phase.IDLE && this.target != null && this.target.method_5805() && !this.passedTarget(this.target) && this.inHorizontalRange(this.target);
   }

   private void tickAim() {
      boolean axeReady = true;
      if (this.stunThisHit) {
         axeReady = this.prepareSlot(this.axeSlot) && this.isStunAxe(mc.field_1724.method_6047());
      }

      int action = NativeModules.autoMacePlan(1, this.stunThisHit, axeReady, this.lookReady(this.target), this.axeHoldTicks);
      if (action == 4) {
         this.stunThisHit = false;
         action = NativeModules.autoMacePlan(1, false, true, this.lookReady(this.target), 0);
      }

      switch (action) {
         case 1:
            this.aimAt(this.target);
            break;
         case 2:
            this.attack(this.target);
            this.stunThisHit = false;
            this.phase = AutoMaceModule.Phase.SMASH;
            break;
         case 3:
            this.smash();
            break;
         default:
            if (this.stunThisHit && this.axeHoldTicks > 0 && axeReady && this.lookReady(this.target)) {
               --this.axeHoldTicks;
            }
      }

   }

   private void tickSmash() {
      int action = NativeModules.autoMacePlan(2, false, true, this.lookReady(this.target), 0);
      if (action == 1) {
         this.aimAt(this.target);
      } else if (action == 3) {
         this.smash();
      }

   }

   private void smash() {
      if ((Boolean)this.switchMode.getValue()) {
         if (!this.prepareSlot(this.maceSlot) || !mc.field_1724.method_6047().method_31574(class_1802.field_49814)) {
            return;
         }
      } else if (!mc.field_1724.method_6047().method_31574(class_1802.field_49814)) {
         this.stopCombo();
         return;
      }

      this.attack(this.target);
      this.smashCooldown.reset();
      this.finish();
   }

   private class_1309 findTarget() {
      class_1309 crosshair = this.crosshairTarget();
      if (crosshair != null && this.validTarget(crosshair)) {
         return crosshair;
      } else {
         class_1309 nearest = this.nearestTarget();
         return nearest != null && this.triggerAllows(nearest) ? nearest : null;
      }
   }

   private boolean triggerAllows(class_1309 living) {
      if (!(Boolean)this.trigger.getValue()) {
         return true;
      } else if ((Boolean)this.silentAim.getValue()) {
         return this.yawToXZ(living) <= ((Double)this.maxYaw.getValue()).floatValue();
      } else {
         return this.cameraRayHits(living);
      }
   }

   private class_1309 crosshairTarget() {
      class_239 var3 = mc.field_1765;
      if (var3 instanceof class_3966 hit) {
         class_1297 var4 = hit.method_17782();
         if (var4 instanceof class_1309 living) {
            return living;
         }
      }

      return null;
   }

   private class_1309 nearestTarget() {
      class_1309 best = null;
      double bestScore = Double.MAX_VALUE;
      double maxHoriz = (Double)this.horizontalTargetDist.getValue();
      class_238 box = mc.field_1724.method_5829().method_1009(maxHoriz, (double)32.0F, maxHoriz);

      for(class_1297 entity : mc.field_1687.method_8333(mc.field_1724, box, class_1297::method_5805)) {
         if (entity instanceof class_1309 living) {
            if (this.validTarget(living) && this.triggerAllows(living)) {
               double score = this.horizontalDistSq(living);
               if (!(living instanceof class_1657)) {
                  score += (double)1000000.0F;
               }

               if (score < bestScore) {
                  bestScore = score;
                  best = living;
               }
            }
         }
      }

      return best;
   }

   private boolean validTarget(class_1309 living) {
      int flags = (living != null ? 1 : 0) | (living != mc.field_1724 ? 2 : 0) | (living != null && living.method_5805() ? 4 : 0) | (living != null && living.method_5863() ? 8 : 0) | (!(living instanceof class_1531) ? 16 : 0) | (living != null && !living.method_7325() ? 32 : 0) | (living != null && !this.passedTarget(living) ? 64 : 0);
      return NativeModules.autoMaceTargetValid(flags, living == null ? Double.MAX_VALUE : this.horizontalDistSq(living), (Double)this.horizontalTargetDist.getValue());
   }

   private boolean inHorizontalRange(class_1309 living) {
      return this.horizontalDistSq(living) <= (Double)this.horizontalTargetDist.getValue() * (Double)this.horizontalTargetDist.getValue();
   }

   private double horizontalDistSq(class_1297 entity) {
      double dx = entity.method_23317() - mc.field_1724.method_23317();
      double dz = entity.method_23321() - mc.field_1724.method_23321();
      return dx * dx + dz * dz;
   }

   private float yawToXZ(class_1297 entity) {
      double dx = entity.method_23317() - mc.field_1724.method_23317();
      double dz = entity.method_23321() - mc.field_1724.method_23321();
      if (dx * dx + dz * dz < 1.0E-6) {
         return 0.0F;
      } else {
         float yaw = class_3532.method_15393((float)(Math.toDegrees(Math.atan2(dz, dx)) - (double)90.0F));
         return Math.abs(class_3532.method_15393(yaw - this.cameraYaw()));
      }
   }

   private boolean cameraRayHits(class_1309 living) {
      float yaw = this.cameraYaw();
      float pitch = this.cameraPitch();
      double reach = Math.max((double)16.0F, mc.field_1724.field_6017 + (double)4.0F);
      class_238 box = this.targetBox(living);
      class_243 start = this.cameraEyes();
      if (box.method_1006(start)) {
         return true;
      } else {
         class_243 end = start.method_1019(RotationHelper.lookVector(pitch, yaw).method_1021(reach));
         return box.method_992(start, end).isPresent();
      }
   }

   private class_243 cameraEyes() {
      return mc.field_1724.method_33571();
   }

   private float cameraYaw() {
      return SilentRotation.isActive() ? SilentRotation.getClientYaw() : mc.field_1724.method_36454();
   }

   private float cameraPitch() {
      return SilentRotation.isActive() ? SilentRotation.getClientPitch() : mc.field_1724.method_36455();
   }

   private boolean passedTarget(class_1309 living) {
      return mc.field_1724.method_23318() + 0.2 < living.method_23318();
   }

   private boolean wantsStun(class_1309 living) {
      if (this.stunSlamMode.getValue() != AutoMaceModule.StunSlamMode.Off && living.method_6039()) {
         if (mc.field_1724.field_6017 < (double)((Double)this.stunMinFall.getValue()).floatValue()) {
            return false;
         } else if (this.findBestAxeSlot() == -1) {
            return false;
         } else if (this.stunSlamMode.getValue() == AutoMaceModule.StunSlamMode.Bind) {
            return ((Bind)this.stunBind.getValue()).isDown();
         } else if (this.stunRoll != null) {
            return this.stunRoll;
         } else {
            this.stunRoll = this.rollStunChance();
            return this.stunRoll;
         }
      } else {
         return false;
      }
   }

   private boolean rollStunChance() {
      return NativeModules.autoMaceRoll((Double)this.chance.getValue(), (Boolean)this.scaleWithFall.getValue(), (float)mc.field_1724.field_6017, ((Double)this.minFallDistance.getValue()).floatValue(), ((Double)this.stunMinFall.getValue()).floatValue(), System.nanoTime() ^ (long)mc.field_1724.method_5628() << 32 ^ (long)mc.field_1724.field_6012);
   }

   private boolean lookReady(class_1309 living) {
      return GrimMovementGuard.hasLastFlying() && GrimMovementGuard.lastFlyingHitsAabb(mc.field_1724, this.targetBox(living), (double)3.0F);
   }

   private class_238 targetBox(class_1297 entity) {
      class_238 box = entity.method_5829();
      if ((Boolean)this.expandHitbox.getValue()) {
         box = box.method_1014((Double)this.hitboxExpansion.getValue() / (double)5.0F);
      }

      return box;
   }

   private boolean aimAt(class_1309 living) {
      class_243 eyes = mc.field_1724.method_33571();
      class_243 velocity = mc.field_1724.method_18798();
      class_238 box = this.targetBox(living);
      float[] angles = NativeModules.autoMaceSolveAim(eyes.field_1352, eyes.field_1351, eyes.field_1350, velocity.field_1352, velocity.field_1351, velocity.field_1350, box.field_1323, box.field_1322, box.field_1321, box.field_1320, box.field_1325, box.field_1324);
      if (angles == null) {
         return false;
      } else {
         GrimMovementGuard.applyMacroLook(mc.field_1724, angles[0], angles[1], (Boolean)this.silentAim.getValue());
         GrimMovementGuard.markAction();
         mc.field_1773.method_3190(1.0F);
         return true;
      }
   }

   private boolean prepareSlot(int slot) {
      if (slot < 0) {
         return false;
      } else if (mc.field_1724.method_31548().method_67532() == slot) {
         return true;
      } else if (!(Boolean)this.switchMode.getValue()) {
         return false;
      } else {
         return GrimMovementGuard.actionThisTick() ? false : HotbarUtil.select(mc.field_1724, slot);
      }
   }

   private void attack(class_1309 entity) {
      GrimMovementGuard.markAction();
      mc.field_1761.method_2918(mc.field_1724, entity);
      mc.field_1724.method_6104(class_1268.field_5808);
   }

   private boolean tryTakeoffElytra() {
      if ((Boolean)this.takeoffElytra.getValue() && this.wearingElytra() && mc.field_1724.method_6128()) {
         class_1309 nearby = this.nearestTarget();
         if (nearby == null) {
            nearby = this.crosshairTarget();
         }

         if (nearby == null) {
            return false;
         } else {
            double dist = (double)mc.field_1724.method_5739(nearby);
            double need = nearby.method_6039() ? (double)8.0F : (double)6.0F;
            if (dist <= need) {
               return false;
            } else {
               int rocket = HotbarUtil.find(mc.field_1724, class_1802.field_8639);
               if (rocket == -1) {
                  return false;
               } else {
                  if (this.savedSlot == -1) {
                     this.savedSlot = mc.field_1724.method_31548().method_67532();
                  }

                  if (!HotbarUtil.select(mc.field_1724, rocket)) {
                     return false;
                  } else {
                     HotbarUtil.useMainHand(mc.field_1724);
                     return true;
                  }
               }
            }
         }
      } else {
         return false;
      }
   }

   private boolean wearingElytra() {
      return mc.field_1724.method_6118(class_1304.field_6174).method_31574(class_1802.field_8833);
   }

   private int findMaceSlot() {
      return NativeModules.autoMaceSlot((Boolean)this.breachCheck.getValue(), (float)mc.field_1724.field_6017, this.bestMace(class_1893.field_50157, true), this.bestMace(class_1893.field_50158, true), HotbarUtil.find(mc.field_1724, class_1802.field_49814));
   }

   private int bestMace(class_5321<class_1887> key, boolean requireLevel) {
      class_6880<class_1887> holder = this.enchantment(key);
      int best = -1;
      int bestLevel = requireLevel ? 0 : -1;

      for(int i = 0; i < 9; ++i) {
         class_1799 stack = mc.field_1724.method_31548().method_5438(i);
         if (stack.method_31574(class_1802.field_49814)) {
            int level = holder == null ? 0 : class_1890.method_8225(holder, stack);
            if (level > bestLevel) {
               bestLevel = level;
               best = i;
            }
         }
      }

      return best;
   }

   private class_6880<class_1887> enchantment(class_5321<class_1887> key) {
      return mc.field_1687 == null ? null : (class_6880)mc.field_1687.method_30349().method_30530(class_7924.field_41265).method_46746(key).orElse(null);
   }

   private int findBestAxeSlot() {
      int best = -1;
      float bestStun = 0.0F;

      for(int i = 0; i < 9; ++i) {
         float stun = this.stunSeconds(mc.field_1724.method_31548().method_5438(i));
         if (stun > bestStun) {
            bestStun = stun;
            best = i;
         }
      }

      return best;
   }

   private boolean isStunAxe(class_1799 stack) {
      return this.stunSeconds(stack) > 0.0F;
   }

   private float stunSeconds(class_1799 stack) {
      if (stack != null && !stack.method_7960()) {
         class_10590 weapon = (class_10590)stack.method_58694(class_9334.field_55878);
         if (weapon != null && weapon.comp_3602() > 0.0F) {
            return weapon.comp_3602();
         } else {
            return stack.method_31573(class_3489.field_42612) ? 5.0F : 0.0F;
         }
      } else {
         return 0.0F;
      }
   }

   private void stopCombo() {
      this.stunRoll = null;
      if (this.phase == AutoMaceModule.Phase.IDLE && this.savedSlot == -1) {
         this.target = null;
      } else {
         this.finish();
      }
   }

   private void finish() {
      boolean heldSilent = SilentRotation.ownedBy(SilentRotation.Owner.COMBAT);
      int restore = -1;
      if ((Boolean)this.restoreSlot.getValue() && this.savedSlot != -1 && mc.field_1724.method_31548().method_67532() != this.savedSlot) {
         restore = this.savedSlot;
      }

      this.resetState();
      if (restore != -1) {
         if (GrimMovementGuard.actionThisTick()) {
            this.pendingRestoreSlot = restore;
         } else {
            HotbarUtil.select(mc.field_1724, restore);
         }
      }

      if (heldSilent) {
         this.restoreSilentTicks = Math.max(this.restoreSilentTicks, 2);
      }

   }

   private void flushPendingRestore() {
      if (this.pendingRestoreSlot >= 0 && mc.field_1724 != null) {
         if (!GrimMovementGuard.actionThisTick()) {
            int slot = this.pendingRestoreSlot;
            this.pendingRestoreSlot = -1;
            if (mc.field_1724.method_31548().method_67532() != slot) {
               HotbarUtil.select(mc.field_1724, slot);
            }

         }
      }
   }

   private void tickSilentRestore() {
      if (this.restoreSilentTicks > 0) {
         --this.restoreSilentTicks;
         if (this.restoreSilentTicks == 0) {
            SilentRotation.end();
         }

      }
   }

   private void resetState() {
      this.phase = AutoMaceModule.Phase.IDLE;
      this.target = null;
      this.stunThisHit = false;
      this.stunRoll = null;
      this.savedSlot = -1;
      this.maceSlot = -1;
      this.axeSlot = -1;
      this.axeHoldTicks = 0;
   }

   private int nativePhase() {
      byte var10000;
      switch (this.phase.ordinal()) {
         case 1 -> var10000 = 1;
         case 2 -> var10000 = 2;
         default -> var10000 = 0;
      }

      return var10000;
   }

   private static enum Phase {
      IDLE,
      AIM,
      SMASH;

      // $FF: synthetic method
      private static Phase[] $values() {
         return new Phase[]{IDLE, AIM, SMASH};
      }
   }

   public static enum StunSlamMode {
      Off,
      Chance,
      Bind;

      // $FF: synthetic method
      private static StunSlamMode[] $values() {
         return new StunSlamMode[]{Off, Chance, Bind};
      }
   }
}
