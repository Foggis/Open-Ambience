package dev.ambience.features.modules.render;

import dev.ambience.features.modules.Module;
import dev.ambience.hooks.NoSmokeHook;
import dev.ambience.nativebridge.NativeModules;
import net.minecraft.class_2394;
import net.minecraft.class_7923;

public class NoSmokeModule extends Module {
   private static NoSmokeModule INSTANCE;

   public NoSmokeModule() {
      super("NoSmoke", "Removes smoke particles.", Module.Category.RENDER);
      INSTANCE = this;
      NoSmokeHook.bind(new NoSmokeHook.Impl() {
         public boolean enabled() {
            return NativeModules.noSmokeActive(NoSmokeModule.this.isEnabled());
         }

         public boolean shouldCancel(class_2394 options) {
            return NoSmokeModule.this.shouldCancel(options);
         }
      });
   }

   public static NoSmokeModule getInstance() {
      return INSTANCE;
   }

   public boolean shouldCancel(class_2394 options) {
      String id = options == null ? null : class_7923.field_41180.method_10221(options.method_10295()).toString();
      return NativeModules.noSmokeShouldCancel(this.isEnabled(), id);
   }
}
