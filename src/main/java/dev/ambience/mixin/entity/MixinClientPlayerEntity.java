package dev.ambience.mixin.entity;

import dev.ambience.event.impl.entity.player.PostPositionTickEvent;
import dev.ambience.event.impl.entity.player.PreTickEvent;
import dev.ambience.event.impl.entity.player.TickEvent;
import dev.ambience.hooks.AutoSprintHook;
import dev.ambience.hooks.OmniSprintHook;
import dev.ambience.util.GrimMovementGuard;
import dev.ambience.util.ModuleDebug;
import dev.ambience.util.SilentRotation;
import dev.ambience.util.traits.Util;
import net.minecraft.class_241;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_746.class})
public class MixinClientPlayerEntity {
   @Inject(
      method = {"method_5773"},
      at = {@At("HEAD")}
   )
   private void ambience$preTickHook(CallbackInfo ci) {
      Util.EVENT_BUS.post(new PreTickEvent());
   }

   @Inject(
      method = {"method_48300"},
      at = {@At("HEAD")}
   )
   private void ambience$autoSprint(CallbackInfoReturnable<Boolean> cir) {
      class_746 player = (class_746)(Object)this;
      OmniSprintHook.apply(player);
      AutoSprintHook.apply(player);
   }

   @Inject(
      method = {"method_67270"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void ambience$omniVector(class_241 input, CallbackInfoReturnable<class_241> cir) {
      if (SilentRotation.ownedBy(SilentRotation.Owner.OMNI)) {
         class_241 vector = (class_241)cir.getReturnValue();
         if (vector != null && !(vector.method_35587() < 1.0E-8F)) {
            if (Math.abs(vector.field_1343) > 1.0E-5F) {
               cir.setReturnValue(new class_241(0.0F, vector.method_35584()));
            }

         }
      }
   }

   @Inject(
      method = {"method_5773"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_746;method_3136()V",
   shift = Shift.BEFORE
)}
   )
   private void ambience$tickHook(CallbackInfo ci) {
      Util.EVENT_BUS.post(new TickEvent());
   }

   @Inject(
      method = {"method_3136"},
      at = {@At("HEAD")}
   )
   private void ambience$flying(CallbackInfo ci) {
      GrimMovementGuard.onFlying((class_746)(Object)this);
      ModuleDebug.onFlying();
   }

   @Inject(
      method = {"method_5773"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_746;method_3136()V",
   shift = Shift.AFTER
)}
   )
   private void ambience$postPositionHook(CallbackInfo ci) {
      Util.EVENT_BUS.post(new PostPositionTickEvent());
   }
}
