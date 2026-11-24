package dev.ambience.mixin.client;

import dev.ambience.hooks.FreecamHook;
import dev.ambience.util.RotationHelper;
import dev.ambience.util.SilentRotation;
import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_757;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_757.class})
public class MixinGameRenderer {
   @Shadow
   @Final
   private class_310 field_4015;

   @Inject(
      method = {"method_3190(F)V"},
      at = {@At("RETURN")}
   )
   private void ambience$cameraPick(float tickDelta, CallbackInfo ci) {
      if (this.field_4015.field_1724 != null && this.field_4015.field_1687 != null) {
         if (FreecamHook.isActive()) {
            this.ambience$applyPick(FreecamHook.getCameraPos(tickDelta), FreecamHook.getPitch(), FreecamHook.getYaw());
         } else if (SilentRotation.isActive()) {
            this.ambience$applyPick(this.field_4015.field_1724.method_5836(tickDelta), SilentRotation.getClientPitch(), SilentRotation.getClientYaw());
         }
      }
   }

   @Unique
   private void ambience$applyPick(class_243 eyes, float pitch, float yaw) {
      double blockReach = this.field_4015.field_1724.method_55754();
      double entityReach = this.field_4015.field_1724.method_55755();
      class_239 hit = RotationHelper.pick(this.field_4015.field_1687, this.field_4015.field_1724, eyes, pitch, yaw, blockReach, entityReach);
      if (hit == null) {
         class_243 look = RotationHelper.lookVector(pitch, yaw);
         class_243 end = eyes.method_1019(look.method_1021(blockReach));
         hit = class_3965.method_17778(end, class_2350.method_10142(look.field_1352, look.field_1351, look.field_1350), class_2338.method_49638(end));
      }

      this.field_4015.field_1765 = hit;
      class_310 var10000 = this.field_4015;
      class_1297 var10001;
      if (hit instanceof class_3966 entityHit) {
         var10001 = entityHit.method_17782();
      } else {
         var10001 = null;
      }

      var10000.field_1692 = var10001;
   }
}
