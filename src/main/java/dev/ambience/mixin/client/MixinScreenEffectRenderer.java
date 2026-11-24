package dev.ambience.mixin.client;

import dev.ambience.hooks.LowFireHook;
import dev.ambience.hooks.TotemAnimationHook;
import net.minecraft.class_1058;
import net.minecraft.class_11659;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4603;
import net.minecraft.class_5819;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({class_4603.class})
public class MixinScreenEffectRenderer {
   @Inject(
      method = {"method_23070"},
      at = {@At("HEAD")}
   )
   private static void ambience$lowFireTransform(class_4587 poseStack, class_4597 buffers, class_1058 sprite, CallbackInfo ci) {
      if (LowFireHook.enabled()) {
         poseStack.method_22903();
         poseStack.method_46416(0.0F, LowFireHook.fireYOffset(), 0.0F);
         poseStack.method_22905(1.0F, LowFireHook.fireHeightScale(), 1.0F);
      }
   }

   @Inject(
      method = {"method_23070"},
      at = {@At("RETURN")}
   )
   private static void ambience$lowFirePop(class_4587 poseStack, class_4597 buffers, class_1058 sprite, CallbackInfo ci) {
      if (LowFireHook.enabled()) {
         poseStack.method_22909();
      }
   }

   @Redirect(
      method = {"method_23070"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1058;method_4593()F"
)
   )
   private static float ambience$clipFireUv(class_1058 sprite) {
      float v0 = sprite.method_4593();
      float v1 = sprite.method_4575();
      return !LowFireHook.enabled() ? v0 : LowFireHook.fireUvSlice(v0, v1);
   }

   @ModifyArgs(
      method = {"method_23070"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_4588;method_22918(Lorg/joml/Matrix4fc;FFF)Lnet/minecraft/class_4588;"
)
   )
   private static void ambience$squashFireVertices(Args args) {
   }

   @Inject(
      method = {"method_70938"},
      at = {@At("TAIL")}
   )
   private void ambience$totemActivationLength(class_1799 stack, class_5819 random, CallbackInfo ci) {
      if (TotemAnimationHook.enabled() && stack.method_31574(class_1802.field_8288)) {
         ((ScreenEffectRendererAccessor)this).ambience$setItemActivationTicks(TotemAnimationHook.animationLength());
      }
   }

   @Inject(
      method = {"method_70939"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_4587;method_22905(FFF)V",
   shift = Shift.AFTER
)}
   )
   private void ambience$smallerTotem(class_4587 poseStack, float tickDelta, class_11659 collector, CallbackInfo ci) {
      if (TotemAnimationHook.enabled()) {
         float factor = TotemAnimationHook.animationScale() / 0.8F;
         poseStack.method_22905(factor, factor, factor);
      }
   }

   @ModifyConstant(
      method = {"method_70939"},
      constant = {@Constant(
   intValue = 40
)}
   )
   private int ambience$totemAnimationLengthInt(int original) {
      return !TotemAnimationHook.enabled() ? original : TotemAnimationHook.animationLength();
   }

   @ModifyConstant(
      method = {"method_70939"},
      constant = {@Constant(
   floatValue = 40.0F
)}
   )
   private float ambience$totemAnimationLengthFloat(float original) {
      return !TotemAnimationHook.enabled() ? original : (float)TotemAnimationHook.animationLength();
   }
}
