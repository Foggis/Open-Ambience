package dev.ambience.mixin.client;

import dev.ambience.inject.InjectHooks;
import net.minecraft.class_329;
import net.minecraft.class_332;
import net.minecraft.class_9779;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_329.class})
public class MixinInGameHud {
   @Inject(
      method = {"method_1753"},
      at = {@At("TAIL")}
   )
   private void ambience$fireRender2D(class_332 context, class_9779 tickCounter, CallbackInfo ci) {
      InjectHooks.fireRender2D(context, tickCounter.method_60637(false));
   }
}
