package dev.ambience.mixin.client;

import dev.ambience.hooks.NoBlastHook;
import dev.ambience.hooks.NoSmokeHook;
import net.minecraft.class_2394;
import net.minecraft.class_3937;
import net.minecraft.class_689;
import net.minecraft.class_691;
import net.minecraft.class_696;
import net.minecraft.class_702;
import net.minecraft.class_703;
import net.minecraft.class_717;
import net.minecraft.class_8899;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_702.class})
public class MixinParticleEngine {
   @Inject(
      method = {"method_3056"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$noBlastCreate(class_2394 parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable<class_703> cir) {
      if (NoBlastHook.shouldCancel(parameters)) {
         cir.setReturnValue(null);
      } else {
         if (NoSmokeHook.shouldCancel(parameters)) {
            cir.setReturnValue(null);
         }

      }
   }

   @Inject(
      method = {"method_3058"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$noBlastAdd(class_703 particle, CallbackInfo ci) {
      if (!NoBlastHook.enabled() || !(particle instanceof class_691) && !(particle instanceof class_689)) {
         if (NoSmokeHook.enabled() && (particle instanceof class_717 || particle instanceof class_696 || particle instanceof class_8899 || particle instanceof class_3937)) {
            ci.cancel();
         }

      } else {
         ci.cancel();
      }
   }
}
