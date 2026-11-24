package dev.ambience.mixin.input;

import dev.ambience.event.impl.input.MouseInputEvent;
import dev.ambience.hooks.AimAssistHook;
import dev.ambience.hooks.AnchorMacroHook;
import dev.ambience.hooks.GuiRuntime;
import dev.ambience.hooks.ProjectileAimHook;
import dev.ambience.util.traits.Util;
import net.minecraft.class_11910;
import net.minecraft.class_312;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_312.class})
public class MixinMouseHandler {
   @Inject(
      method = {"method_1606"},
      at = {@At("TAIL")}
   )
   private void ambience$aimAssist(double frameSeconds, CallbackInfo ci) {
      ProjectileAimHook.onMouseTurn(frameSeconds);
      AimAssistHook.onMouseTurn(frameSeconds);
      AnchorMacroHook.onMouseTurn(frameSeconds);
   }

   @Inject(
      method = {"method_1601"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$onButton(long window, class_11910 buttonInfo, int action, CallbackInfo ci) {
      if (action == 1 || action == 0) {
         int button = buttonInfo.comp_4801();
         if (GuiRuntime.handleBindMouse(button, action) && action == 1) {
            ci.cancel();
         } else {
            if (Util.EVENT_BUS.post(new MouseInputEvent(button, action))) {
               ci.cancel();
            }

         }
      }
   }
}
