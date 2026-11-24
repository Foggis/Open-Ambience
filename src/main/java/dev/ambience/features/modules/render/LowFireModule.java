package dev.ambience.features.modules.render;

import dev.ambience.features.modules.Module;
import dev.ambience.hooks.LowFireHook;
import dev.ambience.nativebridge.NativeModules;
import dev.ambience.util.LowFirePack;

public class LowFireModule extends Module {
   private static LowFireModule INSTANCE;

   public LowFireModule() {
      super("LowFire", "Better PvP style low fire overlay.", Module.Category.VISUALS);
      INSTANCE = this;
      LowFireHook.bind(new LowFireHook.Impl() {
         public boolean enabled() {
            return NativeModules.lowFireEnabled(LowFireModule.this.isEnabled());
         }

         public float fireYOffset() {
            return LowFireModule.this.fireYOffset();
         }

         public float fireHeightScale() {
            return LowFireModule.this.fireHeightScale();
         }

         public float fireUvSlice(float v0, float v1) {
            return LowFireModule.this.fireUvSlice(v0, v1);
         }
      });
   }

   public static LowFireModule getInstance() {
      return INSTANCE;
   }

   public void onLoad() {
      LowFirePack.setEnabled(false);
   }

   public void onEnable() {
      LowFirePack.setEnabled(false);
   }

   public void onDisable() {
      LowFirePack.setEnabled(false);
   }

   public float fireHeightScale() {
      return NativeModules.lowFireHeightScale();
   }

   public float fireYOffset() {
      return NativeModules.lowFireYOffset();
   }

   public float fireUvSlice(float v0, float v1) {
      return NativeModules.lowFireUvSlice(v0, v1);
   }
}
