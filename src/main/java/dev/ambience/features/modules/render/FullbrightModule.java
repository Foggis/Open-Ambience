package dev.ambience.features.modules.render;

import dev.ambience.features.modules.Module;
import dev.ambience.features.settings.Setting;
import dev.ambience.hooks.FullbrightHook;
import dev.ambience.inject.Access;
import dev.ambience.nativebridge.NativeModules;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_6880;
import net.minecraft.class_7923;

public class FullbrightModule extends Module {
   private static FullbrightModule INSTANCE;
   private final Setting<Mode> mode;

   public FullbrightModule() {
      super("Fullbright", "Lights up your world.", Module.Category.VISUALS);
      this.mode = this.mode("Mode", FullbrightModule.Mode.Gamma).setPage("General");
      INSTANCE = this;
      FullbrightHook.bind(this::isGamma);
   }

   public static FullbrightModule getInstance() {
      return INSTANCE;
   }

   public void onDisable() {
      if (this.mode.getValue() == FullbrightModule.Mode.Potion) {
         this.disableNightVision();
      }

   }

   public void onTick() {
      if (!nullCheck()) {
         class_6880<class_1291> nightVision = class_7923.field_41174.method_47983((class_1291)class_1294.field_5925.comp_349());
         boolean hasEffect = mc.field_1724.method_6059(nightVision);
         class_1293 instance = hasEffect ? mc.field_1724.method_6112(nightVision) : null;
         int action = NativeModules.fullbrightAction(this.isEnabled(), this.mode.getValue() == FullbrightModule.Mode.Potion, hasEffect, instance == null ? 0 : instance.method_5584());
         if (action == 2) {
            if (instance != null) {
               Access.setEffectDuration(instance, 420);
            }
         } else if (action == 1) {
            mc.field_1724.method_6092(new class_1293(nightVision, 420, 0, false, false, false));
         }

      }
   }

   public boolean isGamma() {
      return NativeModules.fullbrightGamma(this.isEnabled(), this.mode.getValue() == FullbrightModule.Mode.Gamma);
   }

   private void disableNightVision() {
      if (!nullCheck()) {
         class_6880<class_1291> nightVision = class_7923.field_41174.method_47983((class_1291)class_1294.field_5925.comp_349());
         if (mc.field_1724.method_6059(nightVision)) {
            mc.field_1724.method_6016(nightVision);
         }

      }
   }

   public static enum Mode {
      Gamma,
      Potion;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{Gamma, Potion};
      }
   }
}
