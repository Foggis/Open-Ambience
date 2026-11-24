package dev.ambience.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import dev.ambience.hooks.AntiBlindHook;
import dev.ambience.hooks.FullbrightHook;
import dev.ambience.hooks.XRayHook;
import net.minecraft.class_1309;
import net.minecraft.class_765;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_765.class})
public class MixinLightTexture {
   @Shadow
   @Final
   private GpuTexture field_57927;

   @Inject(
      method = {"method_3313"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$fullbright(float partialTick, CallbackInfo ci) {
      if (FullbrightHook.isGamma() || XRayHook.active()) {
         RenderSystem.getDevice().createCommandEncoder().clearColorTexture(this.field_57927, -1);
         ci.cancel();
      }

   }

   @Inject(
      method = {"method_42596"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$antiBlind(class_1309 entity, float darkness, float partialTick, CallbackInfoReturnable<Float> cir) {
      if (AntiBlindHook.active()) {
         cir.setReturnValue(0.0F);
      }

   }
}
