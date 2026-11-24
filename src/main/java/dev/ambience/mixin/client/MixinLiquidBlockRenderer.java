package dev.ambience.mixin.client;

import dev.ambience.hooks.XRayHook;
import net.minecraft.class_1920;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_3610;
import net.minecraft.class_4588;
import net.minecraft.class_775;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_775.class})
public class MixinLiquidBlockRenderer {
   @Inject(
      method = {"method_3347"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$xrayFluids(class_1920 level, class_2338 pos, class_4588 buffer, class_2680 blockState, class_3610 fluidState, CallbackInfo ci) {
      if (XRayHook.hidesFluids()) {
         ci.cancel();
      }

   }
}
