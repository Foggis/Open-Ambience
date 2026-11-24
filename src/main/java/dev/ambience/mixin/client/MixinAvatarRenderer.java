package dev.ambience.mixin.client;

import dev.ambience.hooks.PlayerEspHook;
import net.minecraft.class_10055;
import net.minecraft.class_1007;
import net.minecraft.class_11890;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_1007.class})
public abstract class MixinAvatarRenderer {
   @Inject(
      method = {"method_62604(Lnet/minecraft/class_11890;Lnet/minecraft/class_10055;F)V"},
      at = {@At("RETURN")}
   )
   private void ambience$playerEspState(class_11890 entity, class_10055 state, float partialTick, CallbackInfo ci) {
      if (PlayerEspHook.applies(state.field_53528)) {
         state.field_61820 = 15728880;
         state.field_61821 = PlayerEspHook.outlineColor();
      }

   }
}
