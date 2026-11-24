package dev.ambience.mixin.client;

import dev.ambience.hooks.AnchorMacroHook;
import dev.ambience.hooks.AutoEscHook;
import dev.ambience.hooks.AutoTotemHook;
import dev.ambience.hooks.BreachSwapHook;
import dev.ambience.hooks.CrystalOptimizerHook;
import dev.ambience.hooks.FreecamHook;
import dev.ambience.hooks.STapHook;
import dev.ambience.hooks.ShieldStunHook;
import dev.ambience.hooks.WTapHook;
import dev.ambience.util.GrimMovementGuard;
import dev.ambience.util.ModuleDebug;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1713;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_3965;
import net.minecraft.class_636;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_636.class})
public class MixinMultiPlayerGameMode {
   @Inject(
      method = {"method_2896"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$blockUse(class_746 player, class_1268 hand, class_3965 hit, CallbackInfoReturnable<class_1269> cir) {
      if (AnchorMacroHook.blocksInteraction() || AutoEscHook.blocksInteraction() || AutoTotemHook.blocksInteraction() || FreecamHook.blocksInteraction()) {
         cir.setReturnValue(class_1269.field_5814);
      }

   }

   @Inject(
      method = {"method_2896"},
      at = {@At("RETURN")}
   )
   private void ambience$debugUseOn(class_746 player, class_1268 hand, class_3965 hit, CallbackInfoReturnable<class_1269> cir) {
      class_1269 result = (class_1269)cir.getReturnValue();
      if (result != null && result.method_23665() && player.method_5998(hand).method_31574(class_1802.field_23141)) {
         class_2338 placed = hit.method_17777().method_10093(hit.method_17780());
         if (player.method_73183().method_8320(hit.method_17777()).method_27852(class_2246.field_23152)) {
            placed = hit.method_17777();
         }

         if (player.method_73183().method_8320(placed).method_27852(class_2246.field_23152)) {
            AnchorMacroHook.anchorPlaced(placed.method_10263(), placed.method_10264(), placed.method_10260());
         }
      }

      String var10001 = String.valueOf(hand);
      ModuleDebug.interact("useItemOn", var10001 + " pos=" + hit.method_17777().method_23854() + " face=" + String.valueOf(hit.method_17780()) + String.format(" loc=%.10f,%.10f,%.10f", hit.method_17784().field_1352, hit.method_17784().field_1351, hit.method_17784().field_1350) + " result=" + String.valueOf(result));
   }

   @Inject(
      method = {"method_2919"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$blockUseItem(class_1657 player, class_1268 hand, CallbackInfoReturnable<class_1269> cir) {
      if (AutoEscHook.blocksInteraction() || AutoTotemHook.blocksInteraction() || FreecamHook.blocksInteraction()) {
         cir.setReturnValue(class_1269.field_5814);
      }

   }

   @Inject(
      method = {"method_2919"},
      at = {@At("RETURN")}
   )
   private void ambience$debugUseItem(class_1657 player, class_1268 hand, CallbackInfoReturnable<class_1269> cir) {
      if (cir.getReturnValue() != null && ((class_1269)cir.getReturnValue()).method_23665()) {
         GrimMovementGuard.markUseItem();
      }

      String var10001 = String.valueOf(hand);
      ModuleDebug.interact("useItem", var10001 + " result=" + String.valueOf(cir.getReturnValue()) + " item=" + String.valueOf(player.method_5998(hand).method_7909()));
   }

   @Inject(
      method = {"method_2918"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$blockAttack(class_1657 player, class_1297 entity, CallbackInfo ci) {
      if (!AutoTotemHook.blocksInteraction() && !FreecamHook.blocksInteraction() && !AnchorMacroHook.isBusy()) {
         if (ShieldStunHook.beforeAttack(player, entity)) {
            ci.cancel();
         } else {
            BreachSwapHook.beforeAttack(player, entity);
            WTapHook.beforeAttack(player, entity);
            STapHook.beforeAttack(player, entity);
         }
      } else {
         ci.cancel();
      }
   }

   @Inject(
      method = {"method_2918"},
      at = {@At("TAIL")}
   )
   private void ambience$crystalOptimizer(class_1657 player, class_1297 entity, CallbackInfo ci) {
      CrystalOptimizerHook.afterAttack(entity);
   }

   @Inject(
      method = {"method_2918"},
      at = {@At("RETURN")}
   )
   private void ambience$debugAttack(class_1657 player, class_1297 entity, CallbackInfo ci) {
      BreachSwapHook.afterAttack();
      ShieldStunHook.afterAttack();
      WTapHook.afterAttack();
      STapHook.afterAttack();
      String var10001 = entity.method_5477().getString();
      ModuleDebug.interact("attack", var10001 + "#" + entity.method_5628());
   }

   @Inject(
      method = {"method_2905"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$blockInteract(class_1657 player, class_1297 entity, class_1268 hand, CallbackInfoReturnable<class_1269> cir) {
      if (AutoTotemHook.blocksInteraction() || FreecamHook.blocksInteraction() || AnchorMacroHook.isBusy()) {
         cir.setReturnValue(class_1269.field_5814);
      }

   }

   @Inject(
      method = {"method_2910"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$blockStartDestroy(class_2338 pos, class_2350 face, CallbackInfoReturnable<Boolean> cir) {
      if (FreecamHook.blocksInteraction() || AutoTotemHook.blocksInteraction() || AnchorMacroHook.isBusy()) {
         cir.setReturnValue(false);
      }

   }

   @Inject(
      method = {"method_2902"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$blockContinueDestroy(class_2338 pos, class_2350 face, CallbackInfoReturnable<Boolean> cir) {
      if (FreecamHook.blocksInteraction() || AutoTotemHook.blocksInteraction() || AnchorMacroHook.isBusy()) {
         cir.setReturnValue(false);
      }

   }

   @Inject(
      method = {"method_2906"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ambience$blockInventoryDuringTotem(int syncId, int slotId, int button, class_1713 clickType, class_1657 player, CallbackInfo ci) {
      if (AutoTotemHook.blocksInventoryInput()) {
         ci.cancel();
      }

   }
}
