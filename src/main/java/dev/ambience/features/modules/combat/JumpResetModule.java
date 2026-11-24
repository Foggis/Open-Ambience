package dev.ambience.features.modules.combat;

import dev.ambience.features.modules.Module;
import dev.ambience.features.modules.movement.LungeSwapModule;
import dev.ambience.features.modules.movement.PearlCatchModule;
import dev.ambience.features.modules.movement.WindHopModule;
import dev.ambience.features.modules.render.FreecamModule;
import dev.ambience.features.settings.Setting;
import dev.ambience.hooks.JumpResetHook;
import dev.ambience.inject.Access;
import dev.ambience.nativebridge.NativeModules;
import net.minecraft.class_1657;

public class JumpResetModule extends Module {
   private static JumpResetModule INSTANCE;
   private final Setting<Integer> chance = this.num("Chance", 100, 0, 100).setPage("General");
   private final Setting<Integer> delay = this.num("Delay", 0, 0, 3).setPage("General");
   private final Setting<Float> range = this.num("Range", 8.0F, 2.0F, 16.0F).setPage("General");
   private int jumpTicks;

   public JumpResetModule() {
      super("JumpReset", "Jumps the tick you take knockback so you stay in combo range.", Module.Category.COMBAT);
      INSTANCE = this;
      JumpResetHook.bind(JumpResetModule::applyJump);
   }

   public static JumpResetModule getInstance() {
      return INSTANCE;
   }

   public static void applyJump() {
      JumpResetModule module = INSTANCE;
      if (module != null && module.isEnabled() && module.jumpTicks > 0) {
         if (mc.field_1724 != null && mc.field_1724.field_3913 != null) {
            if (!FreecamModule.isActive()) {
               mc.field_1724.field_3913.method_64054();
               Access.setJumping(mc.field_1724, true);
               --module.jumpTicks;
            }
         }
      }
   }

   public String getDisplayInfo() {
      return String.valueOf(this.chance.getValue()) + "%";
   }

   public String getDebugState() {
      if (this.jumpTicks > 0) {
         return "jump";
      } else {
         int waiting = NativeModules.jumpResetWaitTicks();
         return waiting > 0 ? "wait=" + waiting : null;
      }
   }

   public void onDisable() {
      NativeModules.clearJumpReset();
      this.jumpTicks = 0;
   }

   public void onPreTick() {
      if (nullCheck()) {
         NativeModules.clearJumpReset();
         this.jumpTicks = 0;
      } else {
         if (NativeModules.jumpResetTick(mc.field_1724.field_6235, (Integer)this.delay.getValue(), (Integer)this.chance.getValue(), this.canReset(), System.nanoTime() ^ (long)mc.field_1724.field_6012)) {
            this.jumpTicks = 1;
         }

      }
   }

   private boolean canReset() {
      if (mc.field_1755 == null && mc.field_1724.method_24828() && !mc.field_1724.method_7325() && mc.field_1724.method_5805()) {
         if (!mc.field_1724.method_31549().field_7479 && !mc.field_1724.method_6128() && !mc.field_1724.method_5799() && !mc.field_1724.method_6101()) {
            return !FreecamModule.isActive() && !AnchorMacroModule.blocksInteraction() && !AutoMaceModule.isBusy() && !WindHopModule.isBusy() && !PearlCatchModule.isBusy() && !LungeSwapModule.isBusy() ? this.nearbyFighter() : false;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean nearbyFighter() {
      double max = (double)(Float)this.range.getValue();
      double maxSq = max * max;

      for(class_1657 player : mc.field_1687.method_18456()) {
         if (player != mc.field_1724 && player.method_5805() && !player.method_7325() && mc.field_1724.method_5858(player) <= maxSq) {
            return true;
         }
      }

      return false;
   }
}
