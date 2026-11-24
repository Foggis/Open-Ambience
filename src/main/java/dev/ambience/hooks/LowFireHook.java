package dev.ambience.hooks;

public final class LowFireHook {
   private static volatile Impl impl;

   private LowFireHook() {
   }

   public static void bind(Impl next) {
      impl = next;
   }

   public static boolean enabled() {
      Impl i = impl;
      return i != null && i.enabled();
   }

   public static float fireYOffset() {
      Impl i = impl;
      return i == null ? 0.0F : i.fireYOffset();
   }

   public static float fireHeightScale() {
      Impl i = impl;
      return i == null ? 1.0F : i.fireHeightScale();
   }

   public static float fireUvSlice(float v0, float v1) {
      Impl i = impl;
      return i == null ? v1 - v0 : i.fireUvSlice(v0, v1);
   }

   public interface Impl {
      boolean enabled();

      float fireYOffset();

      float fireHeightScale();

      float fireUvSlice(float var1, float var2);
   }
}
