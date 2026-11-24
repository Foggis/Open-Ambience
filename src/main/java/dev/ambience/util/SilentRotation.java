package dev.ambience.util;

import dev.ambience.hooks.FreecamHook;
import dev.ambience.hooks.FreelookHook;
import dev.ambience.inject.Access;
import dev.ambience.util.traits.Util;
import net.minecraft.class_3532;
import net.minecraft.class_746;

public final class SilentRotation implements Util {
   private static float clientYaw;
   private static float clientPitch;
   private static boolean active;
   private static Owner owner;
   private static boolean pendingEnd;
   private static Owner pendingEndWho;
   private static int pendingEndAttempts;

   private SilentRotation() {
   }

   public static boolean isActive() {
      return active;
   }

   public static Owner getOwner() {
      return owner;
   }

   public static boolean ownedBy(Owner who) {
      return active && owner == who;
   }

   public static float getClientYaw() {
      return clientYaw;
   }

   public static float getClientPitch() {
      return clientPitch;
   }

   public static void begin(float yaw, float pitch) {
      begin(yaw, pitch, SilentRotation.Owner.COMBAT);
   }

   public static void begin(float yaw, float pitch, Owner who) {
      if (active) {
         if (who != SilentRotation.Owner.OMNI || owner != SilentRotation.Owner.COMBAT) {
            if (who == SilentRotation.Owner.COMBAT) {
               owner = SilentRotation.Owner.COMBAT;
            }

         }
      } else {
         if (FreecamHook.isActive()) {
            clientYaw = FreecamHook.getYaw();
            clientPitch = FreecamHook.getPitch();
         } else if (FreelookHook.isActive()) {
            clientYaw = FreelookHook.getYaw();
            clientPitch = FreelookHook.getPitch();
         } else {
            clientYaw = yaw;
            clientPitch = pitch;
         }

         active = true;
         owner = who;
         freezePrev();
         String var10001 = who.name();
         ModuleDebug.silent("begin", var10001 + String.format(" camYaw=%.10f camPitch=%.10f", yaw, pitch));
      }
   }

   public static void onTurn(double yawDelta, double pitchDelta) {
      if (active) {
         clientYaw += (float)yawDelta * 0.15F;
         clientPitch += (float)pitchDelta * 0.15F;
         clientYaw = class_3532.method_15393(clientYaw);
         clientPitch = class_3532.method_15363(clientPitch, -90.0F, 90.0F);
      }
   }

   public static void setClientLook(float yaw, float pitch) {
      if (active) {
         clientYaw = class_3532.method_15393(yaw);
         clientPitch = class_3532.method_15363(pitch, -90.0F, 90.0F);
      }
   }

   public static void end() {
      end(SilentRotation.Owner.COMBAT);
   }

   public static void retryDeferredEnd() {
      if (pendingEnd) {
         Owner who = pendingEndWho;
         if (!matches(who)) {
            clearPendingEnd();
         } else if (!GrimMovementGuard.useItemSinceFlying() || pendingEndAttempts++ >= 3) {
            clearPendingEnd();
            finishEnd(who);
         }
      }
   }

   public static void end(Owner who) {
      if (matches(who)) {
         if (GrimMovementGuard.useItemSinceFlying()) {
            pendingEnd = true;
            pendingEndWho = who;
            pendingEndAttempts = 0;
            ModuleDebug.silent("end-defer", who.name());
         } else {
            clearPendingEnd();
            finishEnd(who);
         }
      }
   }

   private static void finishEnd(Owner who) {
      if (!FreelookHook.isActive() && !FreecamHook.isActive()) {
         restorePacketLook();
         ModuleDebug.silent("end-restore", who.name());
      } else {
         ModuleDebug.silent("end-keep-body", who.name());
      }

      detach();
   }

   public static void clear(Owner who) {
      if (matches(who)) {
         ModuleDebug.silent("clear", who.name());
         detach();
      }
   }

   public static void forceClear() {
      if (active) {
         ModuleDebug.silent("force-clear", owner.name());
         active = false;
         owner = SilentRotation.Owner.NONE;
         clearPendingEnd();
      }
   }

   private static boolean matches(Owner who) {
      if (!active) {
         return false;
      } else if (who == SilentRotation.Owner.COMBAT && owner == SilentRotation.Owner.OMNI) {
         return false;
      } else {
         return who != SilentRotation.Owner.OMNI || owner != SilentRotation.Owner.COMBAT;
      }
   }

   private static void restorePacketLook() {
      class_746 player = mc.field_1724;
      if (player != null) {
         float aimedYaw = Access.yRot(player);
         float aimedPitch = Access.xRot(player);
         float yaw = RotationUtil.quantizeYaw(aimedYaw, clientYaw);
         float pitch = RotationUtil.quantizePitch(aimedPitch, clientPitch);
         Access.setYRot(player, yaw);
         Access.setXRot(player, pitch);
         clientYaw = yaw;
         clientPitch = pitch;
         Access.setYRotO(player, yaw);
         Access.setXRotO(player, pitch);
      }
   }

   private static void detach() {
      FreelookHook.absorbCamera(clientYaw, clientPitch);
      active = false;
      owner = SilentRotation.Owner.NONE;
      clearPendingEnd();
   }

   private static void clearPendingEnd() {
      pendingEnd = false;
      pendingEndWho = SilentRotation.Owner.NONE;
      pendingEndAttempts = 0;
   }

   private static void freezePrev() {
      class_746 player = mc.field_1724;
      if (player != null) {
         Access.setYRotO(player, player.method_36454());
         Access.setXRotO(player, player.method_36455());
      }
   }

   static {
      owner = SilentRotation.Owner.NONE;
      pendingEndWho = SilentRotation.Owner.NONE;
   }

   public static enum Owner {
      NONE,
      COMBAT,
      OMNI;

      // $FF: synthetic method
      private static Owner[] $values() {
         return new Owner[]{NONE, COMBAT, OMNI};
      }
   }
}
