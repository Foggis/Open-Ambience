package dev.ambience.mixin.client;

import dev.ambience.hooks.FreecamHook;
import dev.ambience.hooks.FreelookHook;
import dev.ambience.util.SilentRotation;
import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_243;
import net.minecraft.class_4184;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({class_4184.class})
public abstract class MixinCamera {
   @Shadow
   protected abstract void method_19325(float var1, float var2);

   @Shadow
   protected abstract void method_19322(class_243 var1);

   @ModifyVariable(
      method = {"method_19321"},
      at = @At("HEAD"),
      argsOnly = true,
      ordinal = 0
   )
   private boolean ambience$freecamDetach(boolean detached) {
      return detached || FreecamHook.isActive();
   }

   @ModifyArgs(
      method = {"method_19321"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_4184;method_19325(FF)V",
   ordinal = 0
)
   )
   private void ambience$silentCamera(Args args, class_1937 level, class_1297 entity, boolean detached, boolean mirrored, float partialTick) {
      if (entity instanceof class_746) {
         if (FreecamHook.isActive()) {
            args.set(0, FreecamHook.getYaw());
            args.set(1, FreecamHook.getPitch());
         } else if (SilentRotation.isActive()) {
            args.set(0, SilentRotation.getClientYaw());
            args.set(1, SilentRotation.getClientPitch());
         } else {
            if (FreelookHook.isActive()) {
               args.set(0, FreelookHook.getYaw());
               args.set(1, FreelookHook.getPitch());
            }

         }
      }
   }

   @Inject(
      method = {"method_19321"},
      at = {@At("RETURN")}
   )
   private void ambience$freecamPosition(class_1937 level, class_1297 entity, boolean detached, boolean mirrored, float partialTick, CallbackInfo ci) {
      if (FreecamHook.isActive()) {
         this.method_19325(FreecamHook.getYaw(), FreecamHook.getPitch());
         this.method_19322(FreecamHook.getCameraPos(partialTick));
      }
   }
}
