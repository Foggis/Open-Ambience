package dev.ambience.features.modules.render;

import dev.ambience.features.modules.Module;
import dev.ambience.hooks.AntiBlindHook;
import dev.ambience.nativebridge.NativeModules;
import net.minecraft.class_1291;
import net.minecraft.class_6880;
import net.minecraft.class_7923;

public class AntiBlindModule extends Module {
   private static AntiBlindModule INSTANCE;

   public AntiBlindModule() {
      super("AntiBlind", "Removes darkness and blindness overlays.", Module.Category.VISUALS);
      INSTANCE = this;
      AntiBlindHook.bind(new AntiBlindHook.Impl() {
         public boolean active() {
            return AntiBlindModule.active();
         }

         public boolean hides(class_6880<class_1291> effect) {
            return AntiBlindModule.hides(effect);
         }
      });
   }

   public static AntiBlindModule getInstance() {
      return INSTANCE;
   }

   public static boolean active() {
      AntiBlindModule module = INSTANCE;
      return NativeModules.antiBlindActive(module != null && module.isEnabled());
   }

   public static boolean hides(class_6880<class_1291> effect) {
      String id = effect == null ? null : class_7923.field_41174.method_10221((class_1291)effect.comp_349()).toString();
      AntiBlindModule module = INSTANCE;
      return NativeModules.antiBlindHides(module != null && module.isEnabled(), id);
   }
}
