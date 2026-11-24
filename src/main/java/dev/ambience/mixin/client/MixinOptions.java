package dev.ambience.mixin.client;

import dev.ambience.hooks.GuiRuntime;
import net.minecraft.class_315;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_315.class})
public class MixinOptions {
   @Inject(
      method = {"method_57703"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void ambience$frostedGui(CallbackInfoReturnable<Integer> cir) {
      if (GuiRuntime.modernBlur()) {
         cir.setReturnValue(0);
      }
   }
}
