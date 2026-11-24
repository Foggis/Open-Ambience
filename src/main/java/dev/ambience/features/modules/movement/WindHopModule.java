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
import net.minecraft.class_1802;

public class WindHopModule extends Module {
   private static WindHopModule INSTANCE;
   private final Setting<Boolean> silentAim = this.bool("SilentAim", true).setPage("General");
   private Phase phase;
   private int previousSlot;
   private int windSlot;
   private float returnYaw;
   private float returnPitch;
   private boolean startedSilent;

   public WindHopModule() {
      super("WindHop", "Throws a wind charge under you. SilentAim keeps your camera still.", Module.Category.MOVEMENT);
      this.phase = WindHopModule.Phase.IDLE;
      this.previousSlot = -1;
      this.windSlot = -1;
      INSTANCE = this;
   }

   public String getDebugState() {
      return this.phase == WindHopModule.Phase.IDLE ? null : this.phase.name() + ((Boolean)this.silentAim.getValue() ? " silent" : " snap") + String.format(" returnYaw=%.10f returnPitch=%.10f", this.returnYaw, this.returnPitch);
   }

   public static boolean isBusy() {
      WindHopModule module = INSTANCE;
      return module != null && module.isEnabled() && module.phase != WindHopModule.Phase.IDLE;
   }

   public void onEnable() {
      if (!nullCheck() && mc.field_1761 != null) {
         if (SilentRotation.ownedBy(SilentRotation.Owner.COMBAT)) {
            this.disable();
         } else {
            this.windSlot = HotbarUtil.find(mc.field_1724, class_1802.field_49098);
            if (this.windSlot == -1) {
               this.disable();
            } else {
               this.previousSlot = mc.field_1724.method_31548().method_67532();
               this.returnYaw = SilentRotation.isActive() ? SilentRotation.getClientYaw() : mc.field_1724.method_36454();
               this.returnPitch = SilentRotation.isActive() ? SilentRotation.getClientPitch() : mc.field_1724.method_36455();
               this.startedSilent = false;
               this.phase = WindHopModule.Phase.AIM;
            }
         }
      } else {
         this.disable();
      }
   }

   public void onDisable() {
      this.finish(false);
   }

   public void onPreTick() {
      if (!nullCheck() && mc.field_1761 != null && this.phase != WindHopModule.Phase.IDLE) {
         int slot = HotbarUtil.find(mc.field_1724, class_1802.field_49098);
         int action = NativeModules.windHopPlan(this.phase.ordinal(), mc.field_1755 != null || AnchorMacroModule.blocksInteraction(), slot != -1, HotbarUtil.holding(mc.field_1724, class_1802.field_49098));
         if (action == 4) {
            this.finish(true);
         } else if (action == 1) {
            this.lookDown();
            this.phase = WindHopModule.Phase.THROW;
         } else if (action == 0 && this.phase == WindHopModule.Phase.THROW) {
            this.lookDown();
            if (!HotbarUtil.select(mc.field_1724, slot)) {
               this.finish(true);
            }

         } else if (action == 2) {
            this.lookDown();
            Access.setRightClickDelay(mc, 0);
            HotbarUtil.useMainHand(mc.field_1724);
            GrimMovementGuard.markAction();
            this.phase = WindHopModule.Phase.RESTORE;
         } else {
            if (action == 3) {
               this.restoreLook();
               this.finish(true);
            }

         }
      }
   }

   private void lookDown() {
      float yaw = SilentRotation.isActive() ? SilentRotation.getClientYaw() : mc.field_1724.method_36454();
      if ((Boolean)this.silentAim.getValue()) {
         GrimMovementGuard.applyMacroLook(mc.field_1724, yaw, 90.0F, true);
         this.startedSilent = SilentRotation.ownedBy(SilentRotation.Owner.COMBAT);
      } else {
         SilentRotation.clear(SilentRotation.Owner.OMNI);
         SilentRotation.clear(SilentRotation.Owner.COMBAT);
         RotationHelper.applyServerLook(mc.field_1724, yaw, 90.0F);
      }
   }

   private void restoreLook() {
      if (this.startedSilent) {
         SilentRotation.end(SilentRotation.Owner.COMBAT);
         this.startedSilent = false;
      } else {
         RotationHelper.applyServerLook(mc.field_1724, this.returnYaw, this.returnPitch);
      }
   }

   private void finish(boolean disable) {
      if (this.phase != WindHopModule.Phase.IDLE && this.phase != WindHopModule.Phase.RESTORE) {
         this.restoreLook();
      }

      if (this.previousSlot >= 0 && mc.field_1724 != null && mc.field_1761 != null) {
         HotbarUtil.select(mc.field_1724, this.previousSlot);
      }

      this.phase = WindHopModule.Phase.IDLE;
      this.previousSlot = -1;
      this.windSlot = -1;
      this.startedSilent = false;
      if (disable && this.isEnabled()) {
         this.disable();
      }

   }

   private static enum Phase {
      IDLE,
      AIM,
      THROW,
      RESTORE;

      // $FF: synthetic method
      private static Phase[] $values() {
         return new Phase[]{IDLE, AIM, THROW, RESTORE};
      }
   }
}
