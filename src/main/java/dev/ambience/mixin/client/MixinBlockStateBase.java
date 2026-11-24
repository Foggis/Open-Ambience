package dev.ambience.mixin.client;

import dev.ambience.hooks.XRayHook;
import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2464;
import net.minecraft.class_259;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_4970;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_4970.class_4971.class})
public abstract class MixinBlockStateBase {
   @Inject(
      method = {"method_26217"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$xrayRenderShape(CallbackInfoReturnable<class_2464> cir) {
      if (XRayHook.hides(this.asState())) {
         cir.setReturnValue(class_2464.field_11455);
      }

   }

   @Inject(
      method = {"method_26225"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$xrayOcclude(CallbackInfoReturnable<Boolean> cir) {
      if (XRayHook.hides(this.asState())) {
         cir.setReturnValue(false);
      }

   }

   @Inject(
      method = {"method_26173"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$xrayFaceOcclusion(class_2350 direction, CallbackInfoReturnable<class_265> cir) {
      if (XRayHook.hides(this.asState())) {
         cir.setReturnValue(class_259.method_1073());
      }

   }

   @Inject(
      method = {"method_26201"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$xrayOcclusionShape(CallbackInfoReturnable<class_265> cir) {
      if (XRayHook.hides(this.asState())) {
         cir.setReturnValue(class_259.method_1073());
      }

   }

   @Inject(
      method = {"method_26216"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$xraySolid(CallbackInfoReturnable<Boolean> cir) {
      if (XRayHook.hides(this.asState())) {
         cir.setReturnValue(false);
      }

   }

   @Inject(
      method = {"method_26193"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$xrayLight(CallbackInfoReturnable<Integer> cir) {
      if (XRayHook.hides(this.asState())) {
         cir.setReturnValue(0);
      }

   }

   @Inject(
      method = {"method_26167"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$xraySky(CallbackInfoReturnable<Boolean> cir) {
      if (XRayHook.hides(this.asState())) {
         cir.setReturnValue(true);
      }

   }

   @Inject(
      method = {"method_26210"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$xrayShade(class_1922 level, class_2338 pos, CallbackInfoReturnable<Float> cir) {
      if (XRayHook.active()) {
         cir.setReturnValue(1.0F);
      }

   }

   private class_2680 asState() {
      return (class_2680)(Object)this;
   }
}
