package dev.ambience.features.modules.render;

import dev.ambience.features.modules.Module;
import dev.ambience.hooks.NoBlastHook;
import dev.ambience.nativebridge.NativeModules;
import net.minecraft.class_2394;
import net.minecraft.class_7923;

public class NoBlastModule extends Module {
   private static NoBlastModule INSTANCE;

   public NoBlastModule() {
      super("NoBlast", "Removes explosion blast particles.", Module.Category.RENDER);
      INSTANCE = this;
      NoBlastHook.bind(new NoBlastHook.Impl() {
         public boolean enabled() {
            return NativeModules.noBlastActive(NoBlastModule.this.isEnabled());
         }

         public boolean shouldCancel(class_2394 options) {
            return NoBlastModule.this.shouldCancel(options);
         }
      });
   }

   public static NoBlastModule getInstance() {
      return INSTANCE;
   }

   public boolean shouldCancel(class_2394 options) {
      String id = options == null ? null : class_7923.field_41180.method_10221(options.method_10295()).toString();
      return NativeModules.noBlastShouldCancel(this.isEnabled(), id);
   }
}
