package dev.ambience.features.modules.combat;

import dev.ambience.features.modules.Module;
import dev.ambience.features.settings.Bind;
import dev.ambience.features.settings.Setting;
import dev.ambience.hooks.AnchorMacroHook;
import dev.ambience.util.GrimMovementGuard;
import dev.ambience.util.HotbarUtil;
import dev.ambience.util.RotationHelper;
import dev.ambience.util.SilentRotation;
import net.minecraft.class_1268;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_3965;
import net.minecraft.class_4969;

public class AnchorMacroModule extends Module {
   private static AnchorMacroModule INSTANCE;
   private static boolean macroUse;
   private final Setting<TriggerMode> triggerMode;
   private final Setting<Bind> triggerKey;
   private final Setting<Double> range;
   private final Setting<Boolean> silentAim;
   private MacroStep step = MacroStep.IDLE;
   private class_2338 anchorPos;
   private int savedSlot = -1;
   private int stuckTicks;
   private boolean triggerWasDown;

   public AnchorMacroModule() {
      super("AnchorMacro", "Charges and detonates placed anchors.", Module.Category.COMBAT);
      this.triggerMode = this.mode("TriggerMode", AnchorMacroModule.TriggerMode.Click).setPage("General");
      this.triggerKey = this.key("Trigger", Bind.none()).setPage("General");
      this.range = this.num("Range", (double)4.5F, (double)3.0F, (double)6.0F).setPage("General");
      this.silentAim = this.bool("SilentAim", true).setPage("General");
      this.bindMode.setVisibility((v) -> false);
      INSTANCE = this;
      AnchorMacroHook.bind(new AnchorMacroHook.Impl() {
         public boolean blocksInteraction() {
            return AnchorMacroModule.blocksInteraction();
         }

         public boolean isBusy() {
            return AnchorMacroModule.isBusy();
         }

         public void onMouseTurn(double frameSeconds) {
         }

         public void anchorPlaced(int x, int y, int z) {
         }
      });
   }

   public static AnchorMacroModule getInstance() {
      return INSTANCE;
   }

   public static boolean blocksInteraction() {
      return macroUse ? false : isBusy();
   }

   public static boolean isBusy() {
      AnchorMacroModule module = getInstance();
      return module != null && module.isEnabled() && module.step != AnchorMacroModule.MacroStep.IDLE;
   }

   public String getDebugState() {
      if (this.step == AnchorMacroModule.MacroStep.IDLE) {
         return null;
      } else {
         return String.valueOf(this.step) + " anchor=" + (this.anchorPos == null ? "-" : this.anchorPos.method_23854());
      }
   }

   public void onDisable() {
      this.restoreSlot();
      this.step = AnchorMacroModule.MacroStep.IDLE;
      this.anchorPos = null;
      this.stuckTicks = 0;
      this.triggerWasDown = false;
      SilentRotation.end();
   }

   public void onPreTick() {
      if (!nullCheck() && mc.field_1761 != null && mc.field_1755 == null) {
         if (this.step == AnchorMacroModule.MacroStep.IDLE) {
            boolean down = this.isTriggerDown();
            boolean rising = down && !this.triggerWasDown;
            this.triggerWasDown = down;
            if (this.triggerMode.getValue() == AnchorMacroModule.TriggerMode.Hold ? down : rising) {
               this.tryLockAnchor();
            }
         } else {
            this.tickActive();
         }
      } else {
         this.triggerWasDown = false;
      }
   }

   private boolean isTriggerDown() {
      if (!((Bind) this.triggerKey.getValue()).isEmpty() && ((Bind) this.triggerKey.getValue()).isDown()) {
         return true;
      }
      return mc.field_1690 != null && mc.field_1690.field_1886.method_1434();
   }

   private void tryLockAnchor() {
      class_3965 hit = this.cameraBlockHit();
      if (hit != null) {
         class_2338 pos = hit.method_17777();
         if (this.isAnchor(pos) && this.inRange(class_243.method_24953(pos))) {
            this.anchorPos = pos.method_10062();
            this.savedSlot = mc.field_1724.method_31548().method_67532();
            this.stuckTicks = 0;
            this.step = MacroStep.CHARGE;
         }
      }
   }

   private void tickActive() {
      if (this.anchorPos == null || !this.isAnchor(this.anchorPos)) {
         this.finishCycle();
         return;
      }
      if (++this.stuckTicks > 60) {
         this.finishCycle();
         return;
      }
      if (GrimMovementGuard.actionThisTick()) {
         return;
      }
      class_243 aimPoint = class_243.method_24953(this.anchorPos);
      float[] angles = GrimMovementGuard.anglesTo(mc.field_1724, aimPoint);
      if (angles == null) {
         return;
      }
      GrimMovementGuard.applyMacroLook(mc.field_1724, angles[0], angles[1], (Boolean) this.silentAim.getValue());
      double reach = (Double) this.range.getValue();
      class_3965 hit = RotationHelper.raycastBlock(mc.field_1687, mc.field_1724, mc.field_1724.method_33571(), mc.field_1724.method_36455(), mc.field_1724.method_36454(), reach);
      if (hit == null || !hit.method_17777().equals(this.anchorPos)) {
         return;
      }
      if (!GrimMovementGuard.canInteractBlock(mc.field_1724, this.anchorPos, reach)) {
         return;
      }
      if (this.step == MacroStep.CHARGE) {
         int charge = this.anchorCharge(this.anchorPos);
         if (charge >= 4) {
            this.step = MacroStep.DETONATE;
            this.stuckTicks = 0;
            return;
         }
         if (charge < 0) {
            this.finishCycle();
            return;
         }
         int glowSlot = HotbarUtil.find(mc.field_1724, class_1802.field_8801);
         if (glowSlot == -1) {
            this.finishCycle();
            return;
         }
         if (!HotbarUtil.holding(mc.field_1724, class_1802.field_8801)) {
            if (HotbarUtil.select(mc.field_1724, glowSlot)) {
               GrimMovementGuard.markAction();
            }
            return;
         }
         if (this.useItemOn(hit)) {
            this.stuckTicks = 0;
         }
      } else if (this.useItemOn(hit)) {
         class_2680 after = mc.field_1687.method_8320(this.anchorPos);
         if (!after.method_27852(class_2246.field_23152)) {
            this.finishCycle();
         } else {
            this.stuckTicks = 0;
         }
      }
   }

   private void finishCycle() {
      this.restoreSlot();
      this.step = MacroStep.IDLE;
      this.anchorPos = null;
      this.stuckTicks = 0;
      if (SilentRotation.ownedBy(SilentRotation.Owner.COMBAT)) {
         SilentRotation.end();
      }
   }

   private void restoreSlot() {
      if (this.savedSlot >= 0 && mc.field_1724 != null && mc.field_1761 != null && mc.field_1724.method_31548().method_67532() != this.savedSlot) {
         HotbarUtil.select(mc.field_1724, this.savedSlot);
      }
      this.savedSlot = -1;
   }

   private boolean isAnchor(class_2338 pos) {
      return pos != null && mc.field_1687.method_8320(pos).method_27852(class_2246.field_23152);
   }

   private int anchorCharge(class_2338 pos) {
      class_2680 state = mc.field_1687.method_8320(pos);
      if (!state.method_27852(class_2246.field_23152)) {
         return -1;
      }
      return (Integer) state.method_11654(class_4969.field_23153);
   }

   private boolean inRange(class_243 point) {
      return mc.field_1724.method_33571().method_1025(point) <= (Double) this.range.getValue() * (Double) this.range.getValue();
   }

   private class_3965 cameraBlockHit() {
      float yaw = mc.field_1724.method_36454();
      float pitch = mc.field_1724.method_36455();
      return RotationHelper.raycastBlock(mc.field_1687, mc.field_1724, mc.field_1724.method_33571(), pitch, yaw, (Double) this.range.getValue());
   }

   private boolean useItemOn(class_3965 hit) {
      macroUse = true;
      boolean sent;
      try {
         sent = mc.field_1761.method_2896(mc.field_1724, class_1268.field_5808, hit).method_23665();
         if (sent) {
            mc.field_1724.method_6104(class_1268.field_5808);
         }
      } finally {
         macroUse = false;
      }
      if (sent) {
         GrimMovementGuard.markAction();
      }
      return sent;
   }

   private static enum MacroStep {
      IDLE,
      CHARGE,
      DETONATE;
   }

   public static enum TriggerMode {
      Click,
      Hold;
   }
}
