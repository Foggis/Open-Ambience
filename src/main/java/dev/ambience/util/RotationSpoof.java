package dev.ambience.util;

import dev.ambience.inject.Access;
import net.minecraft.class_3532;
import net.minecraft.class_746;

public final class RotationSpoof {
   private boolean active;
   private float yaw;
   private float pitch;
   private float yawO;
   private float pitchO;
   private float headYaw;
   private float headYawO;
   private float xBob;
   private float xBobO;
   private float yBob;
   private float yBobO;

   public boolean isActive() {
      return this.active;
   }

   public void save(class_746 player) {
      if (!this.active) {
         this.yaw = player.method_36454();
         this.pitch = player.method_36455();
         this.yawO = Access.yRotO(player);
         this.pitchO = Access.xRotO(player);
         this.headYaw = Access.yHeadRot(player);
         this.headYawO = Access.yHeadRotO(player);
         this.xBob = Access.xBob(player);
         this.xBobO = Access.xBobO(player);
         this.yBob = Access.yBob(player);
         this.yBobO = Access.yBobO(player);
         this.active = true;
      }
   }

   public void apply(class_746 player, float targetYaw, float targetPitch, boolean headBob) {
      float currentYaw = player.method_36454();
      float currentPitch = player.method_36455();
      float yaw = RotationUtil.quantizeYaw(currentYaw, targetYaw);
      float pitch = RotationUtil.quantizePitch(currentPitch, targetPitch);
      player.method_36456(yaw);
      player.method_36457(pitch);
      Access.setYRotO(player, yaw);
      Access.setXRotO(player, pitch);
      Access.setYHeadRot(player, yaw);
      Access.setYHeadRotO(player, yaw);
      if (headBob) {
         float bob = class_3532.method_15363(-pitch * 0.1F, -10.0F, 10.0F);
         Access.setXBob(player, bob);
         Access.setXBobO(player, bob);
         Access.setYBob(player, 0.0F);
         Access.setYBobO(player, 0.0F);
      }

   }

   public void restore(class_746 player) {
      if (this.active) {
         player.method_36456(this.yaw);
         player.method_36457(this.pitch);
         Access.setYRotO(player, this.yawO);
         Access.setXRotO(player, this.pitchO);
         Access.setYHeadRot(player, this.headYaw);
         Access.setYHeadRotO(player, this.headYawO);
         Access.setXBob(player, this.xBob);
         Access.setXBobO(player, this.xBobO);
         Access.setYBob(player, this.yBob);
         Access.setYBobO(player, this.yBobO);
         this.active = false;
      }
   }
}
