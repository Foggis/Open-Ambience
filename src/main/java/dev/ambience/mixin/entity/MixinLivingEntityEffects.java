package dev.ambience.mixin.entity;

import dev.ambience.hooks.AntiBlindHook;
import dev.ambience.util.traits.Util;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_1309;
import net.minecraft.class_6880;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1309.class})
public class MixinLivingEntityEffects {
   @Inject(
      method = {"method_6059"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$antiBlindHas(class_6880<class_1291> effect, CallbackInfoReturnable<Boolean> cir) {
      if ((Object) this == Util.mc.field_1724) {
         if (AntiBlindHook.hides(effect)) {
            cir.setReturnValue(false);
         }

      }
   }

   @Inject(
      method = {"method_6112"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$antiBlindGet(class_6880<class_1291> effect, CallbackInfoReturnable<class_1293> cir) {
      if ((Object) this == Util.mc.field_1724) {
         if (AntiBlindHook.hides(effect)) {
            cir.setReturnValue(null);
         }

      }
   }
}
