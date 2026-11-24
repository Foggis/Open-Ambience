package dev.ambience.mixin.client;

import dev.ambience.hooks.NoBlastHook;
import net.minecraft.class_11749;
import net.minecraft.class_243;
import net.minecraft.class_6012;
import net.minecraft.class_638;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_638.class})
public class MixinClientLevel {
   @Inject(
      method = {"method_73228"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$noBlastEffects(class_243 center, float radius, int blockCount, class_6012<class_11749> particles, CallbackInfo ci) {
      if (NoBlastHook.enabled()) {
         ci.cancel();
      }

   }
}
