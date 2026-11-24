package dev.ambience.mixin.entity;

import dev.ambience.hooks.FreecamHook;
import dev.ambience.hooks.FreelookHook;
import dev.ambience.util.SilentRotation;
import net.minecraft.class_1297;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_1297.class})
public class MixinSilentRotation {
   @Inject(
      method = {"method_5872"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$trackClientTurn(double yawDelta, double pitchDelta, CallbackInfo ci) {
      if ((Object) this instanceof class_746) {
         if (FreecamHook.isActive()) {
            FreecamHook.onTurn(yawDelta, pitchDelta);
            ci.cancel();
         } else if (SilentRotation.isActive()) {
            SilentRotation.onTurn(yawDelta, pitchDelta);
            ci.cancel();
         } else {
            if (FreelookHook.isActive()) {
               FreelookHook.onTurn(yawDelta, pitchDelta);
               ci.cancel();
            }

         }
      }
   }
}
