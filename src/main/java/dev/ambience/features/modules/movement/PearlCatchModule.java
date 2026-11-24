package dev.ambience.features.modules.movement;

import dev.ambience.features.modules.Module;
import dev.ambience.features.modules.combat.AnchorMacroModule;
import dev.ambience.features.settings.Setting;
import dev.ambience.inject.Access;
import dev.ambience.nativebridge.NativeModules;
import dev.ambience.util.GrimMovementGuard;
import dev.ambience.util.HotbarUtil;
import dev.ambience.util.RotationHelper;
import dev.ambience.util.SilentRotation;
import dev.ambience.util.Timer;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_3532;
import org.joml.Vector2f;

public class PearlCatchModule extends Module {
   private static PearlCatchModule INSTANCE;
   private static final float UP_PITCH = -90.0F;
   private static final float DOWN_PITCH = 90.0F;
   private static final int STUCK_TICKS = 40;
   private final Setting<Boolean> silentAim = this.bool("SilentAim", true).setPage("General");
   private final Setting<Boolean> doubleCatch = this.bool("Double", true).setPage("General");
   private final Setting<Vector2f> hopDelay = this.vec2f("HopMs", 40.0F, 70.0F).setVec2TrackBounds(0.0F, 400.0F).setPage("General");
   private final Setting<Vector2f> throwDelay = this.vec2f("ThrowMs", 0.0F, 0.0F).setVec2TrackBounds(0.0F, 250.0F).setPage("General");
   private Phase phase;
   private int previousSlot;
   private int windWaitTicks;
   private int stuckTicks;
   private float throwYaw;
   private float returnYaw;
   private float returnPitch;
   private boolean startedSilent;
   private final Timer timer;
   private long waitMs;

   public PearlCatchModule() {
      super("PearlCatch", "Hops with a wind charge, then looks up and throws pearl + wind.", Module.Category.MOVEMENT);
      this.phase = PearlCatchModule.Phase.IDLE;
      this.previousSlot = -1;
      this.timer = new Timer();
      this.hopDelay.setVisibility((v) -> (Boolean)this.doubleCatch.getValue());
      INSTANCE = this;
   }

   public String getDebugState() {
      return this.phase == PearlCatchModule.Phase.IDLE ? null : this.phase.name() + ((Boolean)this.silentAim.getValue() ? " silent" : " snap") + " waitMs=" + this.waitMs + String.format(" throwYaw=%.10f", this.throwYaw);
   }

   public static boolean isBusy() {
      PearlCatchModule module = INSTANCE;
      return module != null && module.isEnabled() && module.phase != PearlCatchModule.Phase.IDLE;
   }

   public void onEnable() {
      if (!nullCheck() && mc.field_1761 != null) {
         if (HotbarUtil.find(mc.field_1724, class_1802.field_8634) != -1 && HotbarUtil.find(mc.field_1724, class_1802.field_49098) != -1) {
            this.previousSlot = mc.field_1724.method_31548().method_67532();
            float cameraYaw = SilentRotation.isActive() ? SilentRotation.getClientYaw() : mc.field_1724.method_36454();
            float cameraPitch = SilentRotation.isActive() ? SilentRotation.getClientPitch() : mc.field_1724.method_36455();
            this.returnYaw = cameraYaw;
            this.returnPitch = cameraPitch;
            this.throwYaw = cameraYaw;
            this.startedSilent = false;
            this.windWaitTicks = 0;
            this.stuckTicks = 0;
            if ((Boolean)this.silentAim.getValue()) {
               SilentRotation.begin(cameraYaw, cameraPitch, SilentRotation.Owner.COMBAT);
               this.startedSilent = SilentRotation.ownedBy(SilentRotation.Owner.COMBAT);
            }

            this.phase = (Boolean)this.doubleCatch.getValue() ? PearlCatchModule.Phase.AIM_DOWN : PearlCatchModule.Phase.AIM_UP;
         } else {
            this.disable();
         }
      } else {
         this.disable();
      }
   }

   public void onDisable() {
      this.finish(false);
   }

   public void onPreTick() {
      if (!nullCheck() && mc.field_1761 != null && this.phase != PearlCatchModule.Phase.IDLE) {
         boolean blocked = mc.field_1755 != null || AnchorMacroModule.blocksInteraction();
         boolean timedOut = ++this.stuckTicks > 40;
         boolean var10000;
         switch (this.phase.ordinal()) {
            case 6 -> var10000 = HotbarUtil.find(mc.field_1724, class_1802.field_49098) != -1;
            case 7 -> var10000 = true;
            default -> var10000 = HotbarUtil.find(mc.field_1724, class_1802.field_8634) != -1 && HotbarUtil.find(mc.field_1724, class_1802.field_49098) != -1;
         }

         boolean itemReady = var10000;
         if (NativeModules.pearlCatchPlan(this.phase.ordinal(), blocked, timedOut, true, true, itemReady, this.windWaitTicks) == 2) {
            this.finish(true);
         } else {
            switch (this.phase.ordinal()) {
               case 1:
                  if (NativeModules.pearlCatchPlan(this.phase.ordinal(), false, false, this.aimDown(), true, true, this.windWaitTicks) != 1) {
                     return;
                  }

                  this.phase = PearlCatchModule.Phase.THROW_HOP;
                  break;
               case 2:
                  if (NativeModules.pearlCatchPlan(this.phase.ordinal(), false, false, this.aimDown(), true, true, this.windWaitTicks) != 1) {
                     return;
                  }

                  if (!this.throwItem(class_1802.field_49098)) {
                     this.finish(true);
                     return;
                  }

                  this.waitMs = Math.max(0L, roll(this.hopDelay.getValue()));
                  this.timer.reset();
                  this.phase = PearlCatchModule.Phase.WAIT_HOP;
                  break;
               case 3:
                  this.aimDown();
                  if (NativeModules.pearlCatchPlan(this.phase.ordinal(), false, false, true, this.timer.passed(this.waitMs), true, this.windWaitTicks) != 1) {
                     return;
                  }

                  this.phase = PearlCatchModule.Phase.AIM_UP;
                  break;
               case 4:
                  if (NativeModules.pearlCatchPlan(this.phase.ordinal(), false, false, this.aimUpReady(), true, true, this.windWaitTicks) != 1) {
                     return;
                  }

                  this.throwPearlThenWind();
                  break;
               case 5:
                  if (NativeModules.pearlCatchPlan(this.phase.ordinal(), false, false, this.aimUpReady(), true, true, this.windWaitTicks) != 1) {
                     return;
                  }

                  this.throwPearlThenWind();
                  break;
               case 6:
                  boolean aimed = this.aimUp();
                  boolean waited = this.waitMs <= 0L || this.timer.passed(this.waitMs);
                  if (waited) {
                     ++this.windWaitTicks;
                  }

                  int plan = NativeModules.pearlCatchPlan(this.phase.ordinal(), false, false, aimed, waited, true, this.windWaitTicks);
                  if (plan == 2) {
                     this.finish(true);
                     return;
                  }

                  if (plan != 1) {
                     return;
                  }

                  if (this.tryThrowWind()) {
                     this.phase = PearlCatchModule.Phase.HOLD;
                  }
                  break;
               case 7:
                  if (!this.tryEndSilent()) {
                     return;
                  }

                  this.finish(true);
            }

         }
      }
   }

   private void throwPearlThenWind() {
      if (HotbarUtil.find(mc.field_1724, class_1802.field_49098) == -1) {
         this.finish(true);
      } else if (HotbarUtil.onCooldown(mc.field_1724, class_1802.field_49098)) {
         this.phase = PearlCatchModule.Phase.THROW_PEARL;
      } else if (!this.throwItem(class_1802.field_8634)) {
         this.finish(true);
      } else {
         this.waitMs = Math.max(0L, roll(this.throwDelay.getValue()));
         this.timer.reset();
         this.windWaitTicks = 0;
         if (this.waitMs <= 0L && this.tryThrowWind()) {
            this.phase = PearlCatchModule.Phase.HOLD;
         } else {
            this.phase = PearlCatchModule.Phase.THROW_WIND;
         }
      }
   }

   private boolean tryThrowWind() {
      int slot = HotbarUtil.find(mc.field_1724, class_1802.field_49098);
      if (slot == -1) {
         this.finish(true);
         return false;
      } else if (HotbarUtil.onCooldown(mc.field_1724, class_1802.field_49098)) {
         HotbarUtil.select(mc.field_1724, slot);
         return false;
      } else {
         return this.throwItem(class_1802.field_49098);
      }
   }

   private boolean throwItem(class_1792 item) {
      int slot = HotbarUtil.find(mc.field_1724, item);
      if (slot != -1 && HotbarUtil.select(mc.field_1724, slot) && HotbarUtil.holding(mc.field_1724, item)) {
         Access.setRightClickDelay(mc, 0);
         return HotbarUtil.useMainHand(mc.field_1724);
      } else {
         return false;
      }
   }

   private boolean aimDown() {
      this.throwYaw = this.cameraYaw();
      this.lookAt(this.throwYaw, 90.0F);
      return this.lookingAt(this.throwYaw, 90.0F);
   }

   private boolean aimUp() {
      this.throwYaw = this.cameraYaw();
      this.lookAt(this.throwYaw, -90.0F);
      return this.lookingAt(this.throwYaw, -90.0F);
   }

   private boolean aimUpReady() {
      return this.aimUp() && GrimMovementGuard.lastFlyingLooksAt(this.throwYaw, -90.0F, 180.0F, 2.0F);
   }

   private void lookAt(float yaw, float pitch) {
      if (!(Boolean)this.silentAim.getValue()) {
         SilentRotation.clear(SilentRotation.Owner.OMNI);
         SilentRotation.clear(SilentRotation.Owner.COMBAT);
         RotationHelper.applyServerLook(mc.field_1724, yaw, pitch);
      } else {
         GrimMovementGuard.applyMacroLook(mc.field_1724, yaw, pitch, true);
         this.startedSilent = SilentRotation.ownedBy(SilentRotation.Owner.COMBAT) || this.startedSilent;
      }
   }

   private boolean lookingAt(float yaw, float pitch) {
      if (mc.field_1724 == null) {
         return false;
      } else {
         return Math.abs(mc.field_1724.method_36455() - pitch) <= 2.0F && Math.abs(class_3532.method_15393(mc.field_1724.method_36454() - yaw)) <= 8.0F;
      }
   }

   private float cameraYaw() {
      return SilentRotation.isActive() ? SilentRotation.getClientYaw() : mc.field_1724.method_36454();
   }

   private boolean tryEndSilent() {
      if (!this.startedSilent) {
         return true;
      } else {
         SilentRotation.end(SilentRotation.Owner.COMBAT);
         if (SilentRotation.ownedBy(SilentRotation.Owner.COMBAT)) {
            return false;
         } else {
            this.startedSilent = false;
            return true;
         }
      }
   }

   private void restoreLook() {
      if (this.startedSilent) {
         this.tryEndSilent();
      } else {
         if (mc.field_1724 != null && !(Boolean)this.silentAim.getValue()) {
            RotationHelper.applyServerLook(mc.field_1724, this.returnYaw, this.returnPitch);
         }

      }
   }

   private void finish(boolean disable) {
      if (this.phase != PearlCatchModule.Phase.IDLE) {
         this.restoreLook();
      }

      if (this.previousSlot >= 0 && mc.field_1724 != null && mc.field_1761 != null) {
         HotbarUtil.select(mc.field_1724, this.previousSlot);
      }

      this.previousSlot = -1;
      this.windWaitTicks = 0;
      if (disable && this.isEnabled()) {
         if (!this.tryEndSilent()) {
            this.phase = PearlCatchModule.Phase.HOLD;
            return;
         }

         this.phase = PearlCatchModule.Phase.IDLE;
         this.stuckTicks = 0;
         this.startedSilent = false;
         this.disable();
      } else {
         this.phase = PearlCatchModule.Phase.IDLE;
         this.stuckTicks = 0;
         this.startedSilent = false;
      }

   }

   private static long roll(Vector2f range) {
      int min = Math.round(Math.min(range.x, range.y));
      int max = Math.round(Math.max(range.x, range.y));
      return max <= min ? (long)min : (long)(min + ThreadLocalRandom.current().nextInt(max - min + 1));
   }

   private static enum Phase {
      IDLE,
      AIM_DOWN,
      THROW_HOP,
      WAIT_HOP,
      AIM_UP,
      THROW_PEARL,
      THROW_WIND,
      HOLD;

      // $FF: synthetic method
      private static Phase[] $values() {
         return new Phase[]{IDLE, AIM_DOWN, THROW_HOP, WAIT_HOP, AIM_UP, THROW_PEARL, THROW_WIND, HOLD};
      }
   }
}
