package dev.ambience.mixin.client;

import dev.ambience.hooks.AutoTotemHook;
import dev.ambience.hooks.CrystalOptimizerHook;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.class_2663;
import net.minecraft.class_2664;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_634;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_634.class})
public class MixinClientPacketListener {
   @Inject(
      method = {"method_11148"},
      at = {@At("HEAD")}
   )
   private void ambience$localTotemPop(class_2663 packet, CallbackInfo ci) {
      AutoTotemHook.handleEntityEvent(packet);
   }

   @Inject(
      method = {"method_11124"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$skipPredictedCrystalBoom(class_2664 packet, CallbackInfo ci) {
      if (CrystalOptimizerHook.consumePredictedExplosion(packet.comp_2883())) {
         class_310 mc = class_310.method_1551();
         if (mc.field_1724 != null) {
            Optional<class_243> var10000 = packet.comp_2884();
            class_746 var10001 = mc.field_1724;
            Objects.requireNonNull(var10001);
            var10000.ifPresent(var10001::method_45319);
         }

         ci.cancel();
      }
   }
}
