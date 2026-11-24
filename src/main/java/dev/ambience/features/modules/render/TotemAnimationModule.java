package dev.ambience.features.modules.render;

import dev.ambience.features.modules.Module;
import dev.ambience.features.settings.Setting;
import dev.ambience.hooks.TotemAnimationHook;
import dev.ambience.nativebridge.NativeModules;

public class TotemAnimationModule extends Module {
   private static TotemAnimationModule INSTANCE;
   private final Setting<Float> scale = this.num("Scale", 0.4F, 0.15F, 1.0F).setPage("General");
   private final Setting<Integer> length = this.num("Length", 40, 20, 80).setPage("General");

   public TotemAnimationModule() {
      super("TotemAnimation", "Makes the totem pop overlay smaller.", Module.Category.VISUALS);
      INSTANCE = this;
      TotemAnimationHook.bind(new TotemAnimationHook.Impl() {
         public boolean enabled() {
            return NativeModules.totemAnimationEnabled(TotemAnimationModule.this.isEnabled());
         }

         public int animationLength() {
            return TotemAnimationModule.this.animationLength();
         }

         public float animationScale() {
            return TotemAnimationModule.this.animationScale();
         }
      });
   }

   public static TotemAnimationModule getInstance() {
      return INSTANCE;
   }

   public float animationScale() {
      return NativeModules.totemAnimationScale((Float)this.scale.getValue());
   }

   public int animationLength() {
      return NativeModules.totemAnimationLength((Integer)this.length.getValue());
   }
}
