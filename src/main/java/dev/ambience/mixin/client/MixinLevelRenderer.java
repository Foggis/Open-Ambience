package dev.ambience.mixin.client;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import dev.ambience.inject.InjectHooks;
import dev.ambience.util.render.EspDraw;
import net.minecraft.class_4184;
import net.minecraft.class_761;
import net.minecraft.class_9779;
import net.minecraft.class_9922;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_761.class})
public class MixinLevelRenderer {
   @Inject(
      method = {"method_22710"},
      at = {@At("HEAD")}
   )
   private void ambience$captureTracerOrigin(class_9922 allocator, class_9779 deltaTracker, boolean renderBlockOutline, class_4184 camera, Matrix4f view, Matrix4f projection, Matrix4f cullProjection, GpuBufferSlice fog, Vector4f shaderFog, boolean noWorldFog, CallbackInfo ci) {
      EspDraw.updateTracerOrigin(projection, view);
   }

   @Inject(
      method = {"method_22710"},
      at = {@At("TAIL")}
   )
   private void ambience$fireRender3D(class_9922 allocator, class_9779 deltaTracker, boolean renderBlockOutline, class_4184 camera, Matrix4f view, Matrix4f projection, Matrix4f cullProjection, GpuBufferSlice fog, Vector4f shaderFog, boolean noWorldFog, CallbackInfo ci) {
      InjectHooks.fireRender3D(deltaTracker.method_60637(false));
   }
}
