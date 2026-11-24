package dev.ambience.features.modules.render;

import dev.ambience.features.modules.Module;
import dev.ambience.hooks.NoInvisHook;
import dev.ambience.nativebridge.NativeModules;

public class NoInvisModule extends Module {
   private static NoInvisModule INSTANCE;

   public NoInvisModule() {
      super("NoInvis", "Shows invisible entities.", Module.Category.RENDER);
      INSTANCE = this;
      NoInvisHook.bind(() -> NativeModules.noInvisActive(this.isEnabled()));
   }

   public static NoInvisModule getInstance() {
      return INSTANCE;
   }
}
