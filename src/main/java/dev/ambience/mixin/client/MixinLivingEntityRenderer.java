package dev.ambience.mixin.client;

import dev.ambience.hooks.PlayerEspHook;
import net.minecraft.class_10042;
import net.minecraft.class_10055;
import net.minecraft.class_12249;
import net.minecraft.class_1921;
import net.minecraft.class_922;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_922.class})
public abstract class MixinLivingEntityRenderer {
   @Inject(
      method = {"method_62484"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$playerEspFill(class_10042 state, CallbackInfoReturnable<Integer> cir) {
      if (ambience$applies(state)) {
         cir.setReturnValue(PlayerEspHook.fillColor());
      }

   }

   @Inject(
      method = {"method_24302"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$playerEspRenderType(class_10042 state, boolean bodyVisible, boolean translucent, boolean glowing, CallbackInfoReturnable<class_1921> cir) {
      if (state instanceof class_10055 avatar) {
         if (ambience$applies(avatar) && avatar.field_53520 != null) {
            cir.setReturnValue(class_12249.method_75980(avatar.field_53520.comp_1626().comp_3627(), true));
         }
      }

   }

   private static boolean ambience$applies(class_10042 state) {
      boolean var10000;
      if (state instanceof class_10055 avatar) {
         if (PlayerEspHook.applies(avatar.field_53528)) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }
}
