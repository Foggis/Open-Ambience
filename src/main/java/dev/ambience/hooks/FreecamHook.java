package dev.ambience.hooks;

import net.minecraft.class_243;

public final class FreecamHook {
   private static volatile Impl impl;

   private FreecamHook() {
   }

   public static void bind(Impl next) {
      impl = next;
   }

   public static boolean isActive() {
      Impl i = impl;
      return i != null && i.isActive();
   }

   public static boolean blocksInteraction() {
      Impl i = impl;
      return i != null && i.blocksInteraction();
   }

   public static float getYaw() {
      Impl i = impl;
      return i == null ? 0.0F : i.getYaw();
   }

   public static float getPitch() {
      Impl i = impl;
      return i == null ? 0.0F : i.getPitch();
   }

   public static class_243 getCameraPos(float partialTick) {
      Impl i = impl;
      return i == null ? class_243.field_1353 : i.getCameraPos(partialTick);
   }

   public static void onTurn(double yawDelta, double pitchDelta) {
      Impl i = impl;
      if (i != null) {
         i.onTurn(yawDelta, pitchDelta);
      }

   }

   public static void freezeInput() {
      Impl i = impl;
      if (i != null) {
         i.freezeInput();
      }

   }

   public interface Impl {
      boolean isActive();

      boolean blocksInteraction();

      float getYaw();

      float getPitch();

      class_243 getCameraPos(float var1);

      void onTurn(double var1, double var3);

      void freezeInput();
   }
}
