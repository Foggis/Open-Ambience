package dev.ambience.mixin.client;

import dev.ambience.hooks.XRayHook;
import net.minecraft.class_2248;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_2248.class})
public class MixinBlock {
   @Inject(
      method = {"method_9607"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void ambience$xrayFaces(class_2680 state, class_2680 neighbor, class_2350 face, CallbackInfoReturnable<Boolean> cir) {
      if (XRayHook.active()) {
         if (XRayHook.hides(state)) {
            cir.setReturnValue(false);
         } else {
            cir.setReturnValue(true);
         }
      }
   }
}
