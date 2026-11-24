package dev.ambience.features.modules.player;

import dev.ambience.features.modules.Module;
import dev.ambience.features.modules.combat.AnchorMacroModule;
import dev.ambience.features.settings.Setting;
import dev.ambience.inject.Access;
import dev.ambience.nativebridge.NativeModules;
import dev.ambience.util.GrimMovementGuard;
import dev.ambience.util.Timer;
import net.minecraft.class_1268;
import net.minecraft.class_1802;
import org.joml.Vector2f;

public class FastXpModule extends Module {
   private static final long MIN_THROW_MS = 50L;
   private final Setting<Vector2f> delay = this.vec2f("DelayMs", 50.0F, 100.0F).setVec2TrackBounds(50.0F, 500.0F).setPage("General");
   private final Timer throwTimer = new Timer();
   private int throwCooldownTicks;

   public FastXpModule() {
      super("FastXP", "Throws XP bottles quickly while holding right click.", Module.Category.PLAYER);
   }

   public String getDisplayInfo() {
      Vector2f value = this.delay.getValue();
      return String.format("%.0f-%.0fms", value.x(), value.y());
   }

   public String getDebugState() {
      return this.throwCooldownTicks > 0 ? "cooldown=" + this.throwCooldownTicks : null;
   }

   public void onPreTick() {
      if (!nullCheck() && mc.field_1761 != null && mc.field_1755 == null) {
         if (this.throwCooldownTicks > 0) {
            --this.throwCooldownTicks;
         }

         class_1268 hand = this.bottleHand();
         boolean timerReady = this.throwTimer.passed(this.throwDelayMs());
         int action = NativeModules.fastXpPlan(hand != null, mc.field_1690.field_1904.method_1434(), AnchorMacroModule.blocksInteraction() || GrimMovementGuard.actionThisTick(), this.throwCooldownTicks > 0, mc.field_1724.method_6115(), timerReady);
         if (action == 0) {
            this.throwTimer.reset();
         } else if (action == 1) {
            Access.setRightClickDelay(mc, 4);
         } else {
            Access.setRightClickDelay(mc, 0);
            Access.ensureHasSentCarriedItem(mc.field_1761);
            GrimMovementGuard.markAction();
            mc.field_1761.method_2919(mc.field_1724, hand);
            Access.setRightClickDelay(mc, 4);
            this.throwTimer.reset();
            this.throwCooldownTicks = 1;
         }
      }
   }

   private class_1268 bottleHand() {
      if (mc.field_1724.method_6047().method_31574(class_1802.field_8287)) {
         return class_1268.field_5808;
      } else {
         return mc.field_1724.method_6079().method_31574(class_1802.field_8287) ? class_1268.field_5810 : null;
      }
   }

   private long throwDelayMs() {
      return Math.max(50L, this.rollDelay());
   }

   private long rollDelay() {
      int min = Math.max(Math.round(50.0F), Math.round((this.delay.getValue()).x));
      int max = Math.max(min, Math.round((this.delay.getValue()).y));
      return min == max ? (long)min : NativeModules.rollDelay(min, max + 1, System.nanoTime());
   }
}
