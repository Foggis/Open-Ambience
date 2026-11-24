package dev.ambience.mixin.client;

import dev.ambience.hooks.CustomSkyboxHook;
import net.minecraft.class_12076;
import net.minecraft.class_12131;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_638;
import net.minecraft.class_9975;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_9975.class})
public class MixinSkyRenderer {
   @Inject(
      method = {"method_74926"},
      at = {@At("RETURN")}
   )
   private void ambience$customSky(class_638 level, float tickDelta, class_4184 camera, class_12076 state, CallbackInfo ci) {
      CustomSkyboxHook.apply(state);
   }

   @Inject(
      method = {"method_62307"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$hideSunMoon(class_4587 poseStack, float tickDelta, float rainBrightness, float starBrightness, class_12131 moonPhase, float sunAngle, float moonAngle, CallbackInfo ci) {
      if (CustomSkyboxHook.hideSunMoon()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"method_62306"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$hideSunrise(class_4587 poseStack, float tickDelta, int sunriseColor, CallbackInfo ci) {
      if (CustomSkyboxHook.hideSunMoon()) {
         ci.cancel();
      }

   }
}
