package dev.ambience.mixin.client;

import dev.ambience.util.ModuleDebug;
import net.minecraft.class_2561;
import net.minecraft.class_338;
import net.minecraft.class_7469;
import net.minecraft.class_7591;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_338.class})
public class MixinChatComponent {
   @Inject(
      method = {"method_44811(Lnet/minecraft/class_2561;Lnet/minecraft/class_7469;Lnet/minecraft/class_7591;)V"},
      at = {@At("HEAD")}
   )
   private void ambience$grimFlag(class_2561 message, class_7469 signature, class_7591 tag, CallbackInfo ci) {
      if (message != null) {
         ModuleDebug.onChat(message.getString());
      }

   }
}
