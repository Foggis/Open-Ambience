package dev.ambience.util;

import net.minecraft.class_3532;
import net.minecraft.class_746;

public final class SmoothLook {
   private static final float TURN_SCALE = 0.15F;
   private static final float DEADZONE = 0.08F;
   private float velYaw;
   private float velPitch;

   public void reset() {
      this.velYaw = 0.0F;
      this.velPitch = 0.0F;
   }

   public void apply(class_746 player, float targetYaw, float targetPitch, float dt, float speed) {
      this.apply(player, targetYaw, targetPitch, dt, speed, true, true);
   }

   public void apply(class_746 player, float targetYaw, float targetPitch, float dt, float speed, boolean yawAxis, boolean pitchAxis) {
      if (player != null && !(dt < 1.0E-4F)) {
         float yaw = player.method_36454();
         float pitch = player.method_36455();
         float[] next = this.step(yaw, pitch, targetYaw, targetPitch, dt, speed, yawAxis, pitchAxis);
         if (next != null) {
            float turnYaw = class_3532.method_15393(next[0] - yaw);
            float turnPitch = next[1] - pitch;
            if (!(Math.abs(turnYaw) < 1.0E-4F) || !(Math.abs(turnPitch) < 1.0E-4F)) {
               player.method_5872((double)(turnYaw / 0.15F), (double)(turnPitch / 0.15F));
            }
         }
      }
   }

   public void applySilentCamera(float targetYaw, float targetPitch, float dt, float speed, boolean yawAxis, boolean pitchAxis) {
      if (SilentRotation.isActive() && !(dt < 1.0E-4F)) {
         float yaw = SilentRotation.getClientYaw();
         float pitch = SilentRotation.getClientPitch();
         float[] next = this.step(yaw, pitch, targetYaw, targetPitch, dt, speed, yawAxis, pitchAxis);
         if (next != null) {
            SilentRotation.setClientLook(next[0], next[1]);
         }
      }
   }

   private float[] step(float yaw, float pitch, float targetYaw, float targetPitch, float dt, float speed, boolean yawAxis, boolean pitchAxis) {
      float dyaw = yawAxis ? class_3532.method_15393(targetYaw - yaw) : 0.0F;
      float dpitch = pitchAxis ? targetPitch - pitch : 0.0F;
      float error = (float)Math.hypot((double)dyaw, (double)dpitch);
      if (error < 0.08F) {
         this.reset();
         return null;
      } else {
         float omega = 5.5F + speed * 1.35F;
         float damp = 2.15F * omega;
         if (yawAxis) {
            this.velYaw += (dyaw * omega * omega - this.velYaw * damp) * dt;
         } else {
            this.velYaw = 0.0F;
         }

         if (pitchAxis) {
            this.velPitch += (dpitch * omega * omega - this.velPitch * damp) * dt;
         } else {
            this.velPitch = 0.0F;
         }

         float maxSpeed = 90.0F + speed * 18.0F;
         float speedNow = (float)Math.hypot((double)this.velYaw, (double)this.velPitch);
         if (speedNow > maxSpeed) {
            float scale = maxSpeed / speedNow;
            this.velYaw *= scale;
            this.velPitch *= scale;
         }

         float stepYaw = this.velYaw * dt;
         float stepPitch = this.velPitch * dt;
         if (Math.abs(stepYaw) > Math.abs(dyaw)) {
            stepYaw = dyaw;
            this.velYaw = 0.0F;
         }

         if (Math.abs(stepPitch) > Math.abs(dpitch)) {
            stepPitch = dpitch;
            this.velPitch = 0.0F;
         }

         float nextYaw = RotationUtil.quantizeYaw(yaw, yaw + stepYaw);
         float nextPitch = RotationUtil.quantizePitch(pitch, pitch + stepPitch);
         return new float[]{nextYaw, nextPitch};
      }
   }
}
