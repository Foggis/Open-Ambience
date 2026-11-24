package dev.ambience.mixin.entity;

import dev.ambience.hooks.NoInvisHook;
import dev.ambience.util.traits.Util;
import net.minecraft.class_1297;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1297.class})
public class MixinEntityNoInvis {
   @Inject(
      method = {"method_5767"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$showInvisibleEntities(CallbackInfoReturnable<Boolean> cir) {
      if (NoInvisHook.enabled() && Util.mc.field_1724 != null) {
         if ((Object) this != Util.mc.field_1724) {
            cir.setReturnValue(false);
         }

      }
   }
}
