package dev.ambience.util;

import dev.ambience.inject.Access;
import java.util.function.Predicate;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1675;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_746;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public final class RotationHelper {
   private RotationHelper() {
   }

   public static void applyServerLook(class_746 player, float yaw, float pitch) {
      float currentYaw = player.method_36454();
      float currentPitch = player.method_36455();
      yaw = RotationUtil.quantizeYaw(currentYaw, yaw);
      pitch = RotationUtil.quantizePitch(currentPitch, pitch);
      applyServerLookDirect(player, yaw, pitch);
   }

   public static void applyServerLookDirect(class_746 player, float yaw, float pitch) {
      float fromYaw = player.method_36454();
      float fromPitch = player.method_36455();
      player.method_36456(yaw);
      player.method_36457(pitch);
      Access.setYRotO(player, yaw);
      Access.setXRotO(player, pitch);
      ModuleDebug.look(fromYaw, fromPitch, yaw, pitch);
   }

   public static void applyPacketLook(class_746 player, float yaw, float pitch) {
      float currentYaw = player.method_36454();
      float currentPitch = player.method_36455();
      yaw = RotationUtil.quantizeYaw(currentYaw, yaw);
      pitch = RotationUtil.quantizePitch(currentPitch, pitch);
      Access.setYRotO(player, currentYaw);
      Access.setXRotO(player, currentPitch);
      player.method_36456(yaw);
      player.method_36457(pitch);
   }

   public static class_238 grimCrystalBox(class_1511 crystal) {
      class_243 pos = crystal.method_73189();
      return new class_238(pos.field_1352 - (double)1.0F, pos.field_1351, pos.field_1350 - (double)1.0F, pos.field_1352 + (double)1.0F, pos.field_1351 + (double)2.0F, pos.field_1350 + (double)1.0F);
   }

   public static class_243 lookVector(float pitch, float yaw) {
      return class_243.method_1030(pitch, yaw);
   }

   public static class_3965 buildPlaceHit(class_2338 support, class_2350 face) {
      class_243 hitVec = class_243.method_24953(support).method_1031((double)face.method_10148() * (double)0.5F, (double)face.method_10164() * (double)0.5F, (double)face.method_10165() * (double)0.5F);
      return new class_3965(hitVec, face, support, false);
   }

   public static class_3965 raycastBlock(class_1937 level, class_1297 viewer, class_243 start, float pitch, float yaw, double reach) {
      class_243 look = lookVector(pitch, yaw);
      class_243 end = start.method_1019(look.method_1021(reach));
      class_239 hit = level.method_17742(new class_3959(start, end, class_3960.field_17559, class_242.field_1348, viewer));
      if (hit instanceof class_3965 blockHit) {
         if (hit.method_17783() == class_240.field_1332) {
            return blockHit;
         }
      }

      return null;
   }

   public static class_3966 raycastEntity(class_1297 viewer, float pitch, float yaw, double reach, Predicate<class_1297> filter) {
      return raycastEntity(viewer, viewer.method_33571(), pitch, yaw, reach, filter);
   }

   public static class_3966 raycastEntity(class_1297 viewer, class_243 eyes, float pitch, float yaw, double reach, Predicate<class_1297> filter) {
      class_243 look = lookVector(pitch, yaw);
      class_243 end = eyes.method_1019(look.method_1021(reach));
      return class_1675.method_18075(viewer, eyes, end, (new class_238(eyes, end)).method_1014((double)1.0F), filter, reach * reach);
   }

   public static boolean rayHitsBlockAabb(class_1297 viewer, float pitch, float yaw, class_2338 pos, double reach) {
      if (viewer != null && pos != null) {
         class_243 start = viewer.method_33571();
         class_238 box = new class_238(pos);
         if (box.method_1006(start)) {
            return true;
         } else {
            class_243 end = start.method_1019(lookVector(pitch, yaw).method_1021(reach));
            return box.method_992(start, end).isPresent();
         }
      } else {
         return false;
      }
   }

   public static boolean rayHitsEntityAabb(class_1297 viewer, float pitch, float yaw, class_1297 target, double reach) {
      if (viewer != null && target != null) {
         class_238 var10000;
         if (target instanceof class_1511) {
            class_1511 crystal = (class_1511)target;
            var10000 = grimCrystalBox(crystal);
         } else {
            var10000 = target.method_5829();
         }

         class_238 box = var10000;
         class_243 start = viewer.method_33571();
         if (box.method_1006(start)) {
            return true;
         } else {
            class_243 end = start.method_1019(lookVector(pitch, yaw).method_1021(reach));
            return box.method_992(start, end).isPresent();
         }
      } else {
         return false;
      }
   }

   public static class_239 pick(class_1937 level, class_1297 viewer, float pitch, float yaw, double blockReach, double entityReach) {
      return pick(level, viewer, viewer.method_33571(), pitch, yaw, blockReach, entityReach);
   }

   public static class_239 pick(class_1937 level, class_1297 viewer, class_243 eyes, float pitch, float yaw, double blockReach, double entityReach) {
      class_3965 blockHit = raycastBlock(level, viewer, eyes, pitch, yaw, blockReach);
      class_3966 entityHit = raycastEntity(viewer, eyes, pitch, yaw, entityReach, class_1297::method_5863);
      if (blockHit != null && entityHit != null) {
         double blockDist = blockHit.method_17784().method_1025(eyes);
         double entityDist = entityHit.method_17784().method_1025(eyes);
         return (class_239)(blockDist < entityDist ? blockHit : entityHit);
      } else {
         return (class_239)(blockHit != null ? blockHit : entityHit);
      }
   }
}
