package dev.ambience.util;

import net.minecraft.class_10185;
import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_746;

public final class GrimMovementGuard {
   private static final double VELOCITY_EPS = 0.003;
   private static final float INPUT_EPS = 0.02F;
   private static final float LOOK_EPS = 0.01F;
   private static boolean actionThisTick;
   private static boolean useItemSinceFlying;
   private static boolean hasFlying;
   private static float lastFlyingYaw;
   private static float lastFlyingPitch;

   private GrimMovementGuard() {
   }

   public static void beginPreTick() {
      actionThisTick = false;
      SilentRotation.retryDeferredEnd();
   }

   public static void onFlying(class_746 player) {
      if (player != null) {
         lastFlyingYaw = player.method_36454();
         lastFlyingPitch = player.method_36455();
         hasFlying = true;
         useItemSinceFlying = false;
      }
   }

   public static void markAction() {
      actionThisTick = true;
      ModuleDebug.action("markAction", "");
   }

   public static void markUseItem() {
      useItemSinceFlying = true;
      markAction();
   }

   public static boolean actionThisTick() {
      return actionThisTick;
   }

   public static boolean useItemSinceFlying() {
      return useItemSinceFlying;
   }

   public static boolean hasLastFlying() {
      return hasFlying;
   }

   public static float lastFlyingYaw() {
      return lastFlyingYaw;
   }

   public static float lastFlyingPitch() {
      return lastFlyingPitch;
   }

   public static boolean lookMatchesLastFlying(class_746 player) {
      if (hasFlying && player != null) {
         return Math.abs(class_3532.method_15393(player.method_36454() - lastFlyingYaw)) <= 0.01F && Math.abs(player.method_36455() - lastFlyingPitch) <= 0.01F;
      } else {
         return false;
      }
   }

   public static boolean lastFlyingHitsBlock(class_746 player, class_2338 pos, double reach) {
      return hasFlying && player != null && pos != null && RotationHelper.rayHitsBlockAabb(player, lastFlyingPitch, lastFlyingYaw, pos, reach);
   }

   public static boolean currentHitsBlock(class_746 player, class_2338 pos, double reach) {
      return player != null && pos != null && RotationHelper.rayHitsBlockAabb(player, player.method_36455(), player.method_36454(), pos, reach);
   }

   public static boolean canInteractBlock(class_746 player, class_2338 pos, double reach) {
      return lastFlyingHitsBlock(player, pos, reach) && currentHitsBlock(player, pos, reach);
   }

   public static boolean lastFlyingHitsEntity(class_746 player, class_1297 target, double reach) {
      return hasFlying && player != null && target != null && RotationHelper.rayHitsEntityAabb(player, lastFlyingPitch, lastFlyingYaw, target, reach);
   }

   public static boolean lastFlyingHitsAabb(class_746 player, class_238 box, double reach) {
      if (hasFlying && player != null && box != null) {
         class_243 start = player.method_33571();
         if (box.method_1006(start)) {
            return true;
         } else {
            class_243 end = start.method_1019(RotationHelper.lookVector(lastFlyingPitch, lastFlyingYaw).method_1021(reach));
            return box.method_992(start, end).isPresent();
         }
      } else {
         return false;
      }
   }

   public static boolean currentHitsEntity(class_746 player, class_1297 target, double reach) {
      return player != null && target != null && RotationHelper.rayHitsEntityAabb(player, player.method_36455(), player.method_36454(), target, reach);
   }

   public static boolean canInteractEntity(class_746 player, class_1297 target, double reach) {
      return lastFlyingHitsEntity(player, target, reach) && currentHitsEntity(player, target, reach);
   }

   public static boolean isMoving(class_746 player) {
      if (player == null) {
         return false;
      } else if (!player.method_24828()) {
         return true;
      } else if (player.field_3913 != null && player.field_3913.method_3128().method_35587() > 0.02F) {
         return true;
      } else {
         return player.method_18798().method_37268() > 0.003;
      }
   }

   public static boolean inventoryActionUnsafe(class_746 player) {
      if (player == null) {
         return true;
      } else {
         return !player.method_5624() || player.method_5681() && player.method_24828() ? hasMovementInput(player) : true;
      }
   }

   public static boolean hasMovementInput(class_746 player) {
      if (player != null && player.field_3913 != null) {
         class_10185 keys = player.field_3913.field_54155;
         return keys.comp_3159() || keys.comp_3160() || keys.comp_3161() || keys.comp_3162() || keys.comp_3163();
      } else {
         return false;
      }
   }

   public static boolean lastFlyingLooksAt(float yaw, float pitch, float yawEps, float pitchEps) {
      if (!hasFlying) {
         return false;
      } else {
         return Math.abs(class_3532.method_15393(yaw - lastFlyingYaw)) <= yawEps && Math.abs(pitch - lastFlyingPitch) <= pitchEps;
      }
   }

   public static float[] anglesTo(class_746 player, class_243 point) {
      return player == null ? null : anglesFrom(player.method_33571(), point);
   }

   public static float[] anglesFrom(class_243 eyes, class_243 point) {
      if (eyes != null && point != null) {
         double dx = point.field_1352 - eyes.field_1352;
         double dy = point.field_1351 - eyes.field_1351;
         double dz = point.field_1350 - eyes.field_1350;
         double horiz = Math.sqrt(dx * dx + dz * dz);
         if (horiz < 1.0E-4 && Math.abs(dy) < 1.0E-4) {
            return null;
         } else {
            float yaw = class_3532.method_15393((float)(Math.toDegrees(Math.atan2(dz, dx)) - (double)90.0F));
            float pitch = class_3532.method_15363((float)(-Math.toDegrees(Math.atan2(dy, horiz))), -90.0F, 90.0F);
            return new float[]{yaw, pitch};
         }
      } else {
         return null;
      }
   }

   public static boolean alreadyLooking(class_746 player, float yaw, float pitch) {
      if (player == null) {
         return false;
      } else {
         return Math.abs(class_3532.method_15393(yaw - player.method_36454())) <= 0.01F && Math.abs(pitch - player.method_36455()) <= 0.01F;
      }
   }

   public static void applyMacroLook(class_746 player, float yaw, float pitch, boolean silent) {
      applyMacroLook(player, yaw, pitch, silent, false);
   }

   public static void applyMacroLook(class_746 player, float yaw, float pitch, boolean silent, boolean force) {
      if (player != null) {
         if (!useItemSinceFlying || force) {
            if (silent) {
               float cameraYaw = SilentRotation.isActive() ? SilentRotation.getClientYaw() : player.method_36454();
               float cameraPitch = SilentRotation.isActive() ? SilentRotation.getClientPitch() : player.method_36455();
               SilentRotation.begin(cameraYaw, cameraPitch, SilentRotation.Owner.COMBAT);
            }

            if (!alreadyLooking(player, yaw, pitch)) {
               RotationHelper.applyServerLook(player, yaw, pitch);
            }
         }
      }
   }

   public static void clearUseItemGate() {
      useItemSinceFlying = false;
   }
}
