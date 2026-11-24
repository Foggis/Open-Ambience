package dev.ambience.features.modules.combat;

import dev.ambience.features.modules.Module;
import dev.ambience.features.modules.movement.PearlCatchModule;
import dev.ambience.features.modules.movement.WindHopModule;
import dev.ambience.features.modules.render.FreecamModule;
import dev.ambience.hooks.AutoEscHook;
import dev.ambience.inject.Access;
import dev.ambience.nativebridge.NativeModules;
import dev.ambience.util.GrimMovementGuard;
import dev.ambience.util.HotbarUtil;
import dev.ambience.util.ModuleDebug;
import dev.ambience.util.SilentRotation;
import net.minecraft.class_1268;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_3612;
import net.minecraft.class_3965;

public class AutoEscModule extends Module {
   private static AutoEscModule INSTANCE;
   private static final double BLOCK_REACH_PAD = 0.03;
   private static boolean macroUse;
   private Phase phase;
   private int previousSlot;
   private int pendingRestoreSlot;
   private boolean heldSilent;
   private class_2338 webPos;
   private class_2338 waterPos;
   private class_3965 lockedPlaceHit;
   private int tries;
   private int cooldownTicks;
   private int lookWait;

   public AutoEscModule() {
      super("AutoEsc", "Places water above your cobweb and scoops it when the web breaks.", Module.Category.COMBAT);
      this.phase = AutoEscModule.Phase.IDLE;
      this.previousSlot = -1;
      this.pendingRestoreSlot = -1;
      INSTANCE = this;
      AutoEscHook.bind(AutoEscModule::blocksInteraction);
   }

   public static boolean isBusy() {
      AutoEscModule module = INSTANCE;
      return module != null && NativeModules.autoEscBusy(module.isEnabled(), module.phase.ordinal());
   }

   public static boolean blocksInteraction() {
      return macroUse ? false : isBusy();
   }

   public static boolean ownsWater(class_2338 pos) {
      AutoEscModule module = INSTANCE;
      return module != null && module.isEnabled() && module.waterPos != null && pos != null ? module.waterPos.equals(pos) : false;
   }

   public String getDisplayInfo() {
      return this.phase == AutoEscModule.Phase.IDLE ? null : this.phase.name();
   }

   public String getDebugState() {
      return this.phase == AutoEscModule.Phase.IDLE ? null : this.phase.name() + " web=" + (this.webPos == null ? "-" : this.webPos.method_23854()) + " water=" + (this.waterPos == null ? "-" : this.waterPos.method_23854());
   }

   public void onDisable() {
      this.finish(false);
   }

   public void onPreTick() {
      if (!nullCheck() && mc.field_1761 != null) {
         this.flushPendingRestore();
         boolean blocked = mc.field_1755 != null || FreecamModule.blocksInteraction() || AnchorMacroModule.blocksInteraction() || AutoMaceModule.isBusy() || WindHopModule.isBusy() || PearlCatchModule.isBusy();
         boolean stuck = !blocked && this.phase == AutoEscModule.Phase.IDLE && this.stuckInWeb();
         boolean hasBucket = !blocked && this.phase == AutoEscModule.Phase.IDLE && HotbarUtil.find(mc.field_1724, class_1802.field_8705) != -1;
         int entryAction = NativeModules.autoEscPlan(this.phase.ordinal(), blocked, stuck, hasBucket);
         if (entryAction == 3) {
            this.finish(true);
         } else {
            if (this.phase == AutoEscModule.Phase.IDLE) {
               if (entryAction != 1) {
                  return;
               }

               this.previousSlot = mc.field_1724.method_31548().method_67532();
               this.tries = 0;
               this.webPos = this.cobwebUnderFeet();
               if (this.webPos == null) {
                  return;
               }

               this.waterPos = null;
               this.lockedPlaceHit = null;
               this.cooldownTicks = 0;
               this.lookWait = 0;
               this.phase = AutoEscModule.Phase.CLEAR_INPUT;
            }

            this.suppressUseKey();
            if (!GrimMovementGuard.actionThisTick()) {
               switch (this.phase.ordinal()) {
                  case 1:
                     this.releaseUse();
                     GrimMovementGuard.clearUseItemGate();
                     this.phase = AutoEscModule.Phase.SELECT_PLACE;
                     break;
                  case 2:
                     if (!this.stuckInWeb() || !this.selectOnly(class_1802.field_8705)) {
                        this.finish(true);
                        return;
                     }

                     this.phase = AutoEscModule.Phase.AIM_PLACE;
                     break;
                  case 3:
                     if (!this.stuckInWeb() || !HotbarUtil.holding(mc.field_1724, class_1802.field_8705)) {
                        this.finish(true);
                        return;
                     }

                     this.lockedPlaceHit = this.placeHit();
                     if (this.lockedPlaceHit == null) {
                        this.finish(true);
                        return;
                     }

                     this.aimAt(this.lockedPlaceHit.method_17784());
                     this.lookWait = 1;
                     this.phase = AutoEscModule.Phase.PLACE;
                     break;
                  case 4:
                     if (!HotbarUtil.holding(mc.field_1724, class_1802.field_8705)) {
                        this.finish(true);
                        return;
                     }

                     if (this.lockedPlaceHit == null || !this.validPlaceTarget()) {
                        this.lockedPlaceHit = this.placeHit();
                     }

                     if (this.lockedPlaceHit == null) {
                        this.finish(true);
                        return;
                     }

                     this.aimAt(this.lockedPlaceHit.method_17784());
                     if (this.lookWait > 0) {
                        --this.lookWait;
                        return;
                     }

                     if (!GrimMovementGuard.canInteractBlock(mc.field_1724, this.lockedPlaceHit.method_17777(), this.reach())) {
                        return;
                     }

                     if (!this.placeWater()) {
                        this.lockedPlaceHit = null;
                        if (!NativeModules.autoEscRetry(this.phase.ordinal(), ++this.tries)) {
                           this.finish(true);
                        } else {
                           this.lookWait = 1;
                        }

                        return;
                     }

                     this.tries = 0;
                     this.waterPos = this.webPos.method_10084();
                     this.phase = AutoEscModule.Phase.WAIT;
                     break;
                  case 5:
                     if (this.waterPos != null && isSource(this.waterPos)) {
                        if (this.webStillPresent()) {
                           this.aimAt(this.waterPos);
                           return;
                        }

                        this.phase = AutoEscModule.Phase.SELECT_DRAIN;
                        break;
                     }

                     if (this.webStillPresent() && HotbarUtil.holding(mc.field_1724, class_1802.field_8705) && this.tries < 6) {
                        this.phase = AutoEscModule.Phase.SELECT_PLACE;
                        return;
                     }

                     this.finish(true);
                     return;
                  case 6:
                     if (this.waterPos == null || !isSource(this.waterPos)) {
                        this.finish(true);
                        return;
                     }

                     if (!HotbarUtil.holding(mc.field_1724, class_1802.field_8550) && !this.selectOnly(class_1802.field_8550)) {
                        this.finish(true);
                        return;
                     }

                     this.phase = AutoEscModule.Phase.AIM_DRAIN;
                     break;
                  case 7:
                     if (this.waterPos == null || !isSource(this.waterPos)) {
                        this.finish(true);
                        return;
                     }

                     this.aimAt(this.waterPos);
                     this.lookWait = 1;
                     this.phase = AutoEscModule.Phase.DRAIN;
                     break;
                  case 8:
                     if (this.waterPos == null || !isSource(this.waterPos)) {
                        this.finish(true);
                        return;
                     }

                     if (!HotbarUtil.holding(mc.field_1724, class_1802.field_8550)) {
                        this.phase = AutoEscModule.Phase.SELECT_DRAIN;
                        return;
                     }

                     this.aimAt(this.waterPos);
                     if (this.lookWait > 0) {
                        --this.lookWait;
                        return;
                     }

                     if (!this.scoopWater()) {
                        if (!NativeModules.autoEscRetry(this.phase.ordinal(), ++this.tries)) {
                           this.finish(true);
                        } else {
                           this.lookWait = 1;
                        }

                        return;
                     }

                     ModuleDebug.module(this.getName(), "drain", this.waterPos.method_23854());
                     this.releaseUse();
                     this.cooldownTicks = 2;
                     this.phase = AutoEscModule.Phase.COOLDOWN;
                     break;
                  case 9:
                     this.releaseUse();
                     if (--this.cooldownTicks > 0) {
                        return;
                     }

                     this.phase = AutoEscModule.Phase.RESTORE;
                     break;
                  case 10:
                     this.finish(true);
               }

            }
         }
      }
   }

   private void suppressUseKey() {
      if (mc.field_1690.field_1904.method_1434()) {
         mc.field_1690.field_1904.method_23481(false);
      }

   }

   private void releaseUse() {
      this.suppressUseKey();
      if (mc.field_1724 != null && mc.field_1761 != null && mc.field_1724.method_6115()) {
         mc.field_1761.method_2897(mc.field_1724);
      }

   }

   private void aimAt(class_2338 pos) {
      this.aimAt(class_243.method_24953(pos));
   }

   private void aimAt(class_243 point) {
      float[] angles = GrimMovementGuard.anglesTo(mc.field_1724, point);
      if (angles != null) {
         GrimMovementGuard.applyMacroLook(mc.field_1724, angles[0], angles[1], true, true);
         this.heldSilent = SilentRotation.ownedBy(SilentRotation.Owner.COMBAT);
      }
   }

   private boolean placeWater() {
      return this.lockedPlaceHit != null && this.validPlaceTarget() ? this.useHeldBucket() : false;
   }

   private boolean scoopWater() {
      return this.waterPos != null && isSource(this.waterPos) ? this.useHeldBucket() : false;
   }

   private boolean useHeldBucket() {
      if (mc.field_1761 != null && mc.field_1724 != null) {
         boolean hadWater = HotbarUtil.holding(mc.field_1724, class_1802.field_8705);
         boolean hadEmpty = HotbarUtil.holding(mc.field_1724, class_1802.field_8550);
         if (!hadWater && !hadEmpty) {
            return false;
         } else {
            Access.setRightClickDelay(mc, 0);
            Access.ensureHasSentCarriedItem(mc.field_1761);
            GrimMovementGuard.markAction();
            macroUse = true;

            try {
               mc.field_1761.method_2919(mc.field_1724, class_1268.field_5808);
               mc.field_1724.method_6104(class_1268.field_5808);
            } finally {
               macroUse = false;
            }

            if (hadWater) {
               return !HotbarUtil.holding(mc.field_1724, class_1802.field_8705);
            } else {
               return HotbarUtil.holding(mc.field_1724, class_1802.field_8705) || !isSource(this.waterPos);
            }
         }
      } else {
         return false;
      }
   }

   private class_3965 placeHit() {
      if (this.webPos != null && this.webStillPresent() && this.validWaterDestination(this.webPos.method_10084())) {
         class_243 topCenter = new class_243((double)this.webPos.method_10263() + (double)0.5F, (double)this.webPos.method_10264() + (double)1.0F, (double)this.webPos.method_10260() + (double)0.5F);
         return new class_3965(topCenter, class_2350.field_11036, this.webPos, false);
      } else {
         return null;
      }
   }

   private boolean validWaterDestination(class_2338 pos) {
      class_2680 state = mc.field_1687.method_8320(pos);
      return state.method_26227().method_15769() && state.method_45474() && !state.method_27852(class_2246.field_10343);
   }

   private boolean validPlaceTarget() {
      return this.webStillPresent() && this.validWaterDestination(this.webPos.method_10084());
   }

   private boolean webStillPresent() {
      return this.webPos != null && mc.field_1687.method_8320(this.webPos).method_27852(class_2246.field_10343);
   }

   private class_2338 cobwebUnderFeet() {
      class_238 box = mc.field_1724.method_5829();
      class_2338 min = class_2338.method_49637(box.field_1323, box.field_1322, box.field_1321);
      class_2338 max = class_2338.method_49637(box.field_1320 - 1.0E-4, Math.min(box.field_1325 - 1.0E-4, box.field_1322 + (double)1.0F), box.field_1324 - 1.0E-4);
      class_2338 best = null;
      double bestY = Double.POSITIVE_INFINITY;

      for(class_2338 pos : class_2338.method_10097(min, max)) {
         if (mc.field_1687.method_8320(pos).method_27852(class_2246.field_10343) && (double)pos.method_10264() < bestY) {
            bestY = (double)pos.method_10264();
            best = pos.method_10062();
         }
      }

      return best;
   }

   private boolean selectOnly(class_1792 item) {
      if (HotbarUtil.holding(mc.field_1724, item)) {
         return true;
      } else {
         int slot = HotbarUtil.find(mc.field_1724, item);
         if (slot == -1) {
            return false;
         } else if (GrimMovementGuard.actionThisTick()) {
            return false;
         } else {
            boolean ok = HotbarUtil.select(mc.field_1724, slot) && HotbarUtil.holding(mc.field_1724, item);
            if (ok) {
               GrimMovementGuard.markAction();
            }

            return ok;
         }
      }
   }

   private void flushPendingRestore() {
      if (this.pendingRestoreSlot >= 0 && mc.field_1724 != null && mc.field_1761 != null) {
         if (!GrimMovementGuard.actionThisTick()) {
            int slot = this.pendingRestoreSlot;
            this.pendingRestoreSlot = -1;
            if (slot >= 0 && slot <= 8 && mc.field_1724.method_31548().method_67532() != slot) {
               HotbarUtil.select(mc.field_1724, slot);
               GrimMovementGuard.markAction();
            }

         }
      }
   }

   private boolean stuckInWeb() {
      class_238 box = mc.field_1724.method_5829();
      class_2338 min = class_2338.method_49637(box.field_1323, box.field_1322, box.field_1321);
      class_2338 max = class_2338.method_49637(box.field_1320 - 1.0E-4, box.field_1325 - 1.0E-4, box.field_1324 - 1.0E-4);

      for(class_2338 pos : class_2338.method_10097(min, max)) {
         if (mc.field_1687.method_8320(pos).method_27852(class_2246.field_10343)) {
            return true;
         }
      }

      return false;
   }

   static boolean isSource(class_2338 pos) {
      if (mc.field_1687 != null && pos != null) {
         class_2680 state = mc.field_1687.method_8320(pos);
         return state.method_26227().method_39360(class_3612.field_15910) && state.method_26227().method_15771();
      } else {
         return false;
      }
   }

   private double reach() {
      return mc.field_1724.method_55754() + 0.03;
   }

   private void finish(boolean restore) {
      int restoreTo = this.previousSlot;
      if (this.heldSilent) {
         SilentRotation.end();
      }

      this.phase = AutoEscModule.Phase.IDLE;
      this.previousSlot = -1;
      this.heldSilent = false;
      this.webPos = null;
      this.waterPos = null;
      this.lockedPlaceHit = null;
      this.tries = 0;
      this.cooldownTicks = 0;
      this.lookWait = 0;
      if (restore && restoreTo >= 0 && restoreTo <= 8 && mc.field_1724 != null) {
         if (GrimMovementGuard.actionThisTick()) {
            this.pendingRestoreSlot = restoreTo;
         } else {
            HotbarUtil.select(mc.field_1724, restoreTo);
            GrimMovementGuard.markAction();
         }
      }

   }

   private static enum Phase {
      IDLE,
      CLEAR_INPUT,
      SELECT_PLACE,
      AIM_PLACE,
      PLACE,
      WAIT,
      SELECT_DRAIN,
      AIM_DRAIN,
      DRAIN,
      COOLDOWN,
      RESTORE;

      // $FF: synthetic method
      private static Phase[] $values() {
         return new Phase[]{IDLE, CLEAR_INPUT, SELECT_PLACE, AIM_PLACE, PLACE, WAIT, SELECT_DRAIN, AIM_DRAIN, DRAIN, COOLDOWN, RESTORE};
      }
   }
}
