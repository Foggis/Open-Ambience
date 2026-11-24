package dev.ambience.hooks;

public final class TotemAnimationHook {
   private static volatile Impl impl;

   private TotemAnimationHook() {
   }

   public static void bind(Impl next) {
      impl = next;
   }

   public static boolean enabled() {
      Impl i = impl;
      return i != null && i.enabled();
   }

   public static int animationLength() {
      Impl i = impl;
      return i == null ? 40 : i.animationLength();
   }

   public static float animationScale() {
      Impl i = impl;
      return i == null ? 0.8F : i.animationScale();
   }

   public interface Impl {
      boolean enabled();

      int animationLength();

      float animationScale();
   }
}
