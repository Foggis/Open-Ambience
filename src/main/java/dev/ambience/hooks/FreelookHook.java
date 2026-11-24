package dev.ambience.hooks;

public final class FreelookHook {
   private static volatile Impl impl;

   private FreelookHook() {
   }

   public static void bind(Impl next) {
      impl = next;
   }

   public static boolean isActive() {
      Impl i = impl;
      return i != null && i.isActive();
   }

   public static float getYaw() {
      Impl i = impl;
      return i == null ? 0.0F : i.getYaw();
   }

   public static float getPitch() {
      Impl i = impl;
      return i == null ? 0.0F : i.getPitch();
   }

   public static void onTurn(double yawDelta, double pitchDelta) {
      Impl i = impl;
      if (i != null) {
         i.onTurn(yawDelta, pitchDelta);
      }

   }

   public static void absorbCamera(float yaw, float pitch) {
      Impl i = impl;
      if (i != null) {
         i.absorbCamera(yaw, pitch);
      }

   }

   public interface Impl {
      boolean isActive();

      float getYaw();

      float getPitch();

      void onTurn(double var1, double var3);

      void absorbCamera(float var1, float var2);
   }
}
