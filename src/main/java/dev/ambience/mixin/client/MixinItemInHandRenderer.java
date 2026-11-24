package dev.ambience.mixin.client;

import dev.ambience.hooks.FreecamHook;
import dev.ambience.hooks.ViewModelHook;
import dev.ambience.mixin.entity.EntityRotationAccessor;
import dev.ambience.util.SilentRotation;
import net.minecraft.class_11659;
import net.minecraft.class_1268;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_742;
import net.minecraft.class_746;
import net.minecraft.class_759;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_759.class})
public class MixinItemInHandRenderer {
   @Unique
   private boolean viewModel$scaled;
   @Unique
   private float noSway$savedXBob;
   @Unique
   private float noSway$savedXBobO;
   @Unique
   private float noSway$savedYBob;
   @Unique
   private float noSway$savedYBobO;
   @Shadow
   private float field_4043;
   @Shadow
   private float field_4053;
   @Shadow
   private float field_4052;
   @Shadow
   private float field_4051;

   @Inject(
      method = {"method_22976"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$hideHandsInFreecam(CallbackInfo ci) {
      if (FreecamHook.isActive()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"method_22976"},
      at = {@At("HEAD")}
   )
   private void ambience$noSwayPre(CallbackInfo ci) {
      if (!FreecamHook.isActive()) {
         if (ViewModelHook.noSway()) {
            class_746 player = class_310.method_1551().field_1724;
            if (player != null) {
               EntityRotationAccessor acc = (EntityRotationAccessor)player;
               this.noSway$savedXBob = acc.ambience$getXBob();
               this.noSway$savedXBobO = acc.ambience$getXBobO();
               this.noSway$savedYBob = acc.ambience$getYBob();
               this.noSway$savedYBobO = acc.ambience$getYBobO();
               float xRot = player.method_36455();
               float yRot = player.method_36454();
               acc.ambience$setXBob(xRot);
               acc.ambience$setXBobO(xRot);
               acc.ambience$setYBob(yRot);
               acc.ambience$setYBobO(yRot);
            }
         }
      }
   }

   @Inject(
      method = {"method_22976"},
      at = {@At("RETURN")}
   )
   private void ambience$noSwayPost(CallbackInfo ci) {
      if (!FreecamHook.isActive()) {
         if (ViewModelHook.noSway()) {
            class_746 player = class_310.method_1551().field_1724;
            if (player != null) {
               EntityRotationAccessor acc = (EntityRotationAccessor)player;
               acc.ambience$setXBob(this.noSway$savedXBob);
               acc.ambience$setXBobO(this.noSway$savedXBobO);
               acc.ambience$setYBob(this.noSway$savedYBob);
               acc.ambience$setYBobO(this.noSway$savedYBobO);
            }
         }
      }
   }

   @Inject(
      method = {"method_65910"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$noSwapAnimation(class_1799 visibleStack, class_1799 currentStack, CallbackInfoReturnable<Boolean> cir) {
      if (ViewModelHook.noSwapAnimation()) {
         cir.setReturnValue(true);
      }

   }

   @Inject(
      method = {"method_3220"},
      at = {@At("TAIL")}
   )
   private void ambience$oldAnimation(CallbackInfo ci) {
      if (ViewModelHook.oldAnimation()) {
         this.field_4043 = 1.0F;
         this.field_4053 = 1.0F;
         this.field_4052 = 1.0F;
         this.field_4051 = 1.0F;
      }
   }

   @ModifyVariable(
      method = {"method_3228(Lnet/minecraft/class_742;FFLnet/minecraft/class_1268;FLnet/minecraft/class_1799;FLnet/minecraft/class_4587;Lnet/minecraft/class_11659;I)V"},
      at = @At("HEAD"),
      argsOnly = true,
      ordinal = 1
   )
   private float ambience$silentHandPitch(float interpolatedPitch) {
      return SilentRotation.isActive() ? SilentRotation.getClientPitch() : interpolatedPitch;
   }

   @Inject(
      method = {"method_3228(Lnet/minecraft/class_742;FFLnet/minecraft/class_1268;FLnet/minecraft/class_1799;FLnet/minecraft/class_4587;Lnet/minecraft/class_11659;I)V"},
      at = {@At("HEAD")}
   )
   private void ambience$viewModelPush(class_742 player, float partialTick, float interpolatedPitch, class_1268 hand, float swingProgress, class_1799 stack, float equipProgress, class_4587 poseStack, class_11659 collector, int light, CallbackInfo ci) {
      this.viewModel$scaled = ViewModelHook.applyArm(player, hand, poseStack);
   }

   @Inject(
      method = {"method_3228(Lnet/minecraft/class_742;FFLnet/minecraft/class_1268;FLnet/minecraft/class_1799;FLnet/minecraft/class_4587;Lnet/minecraft/class_11659;I)V"},
      at = {@At("RETURN")}
   )
   private void ambience$viewModelPop(class_742 player, float partialTick, float interpolatedPitch, class_1268 hand, float swingProgress, class_1799 stack, float equipProgress, class_4587 poseStack, class_11659 collector, int light, CallbackInfo ci) {
      if (this.viewModel$scaled) {
         this.viewModel$scaled = false;
         poseStack.method_22909();
      }
   }
}
