package dev.ambience.hooks;

public final class FullbrightHook {
   private static volatile Impl impl;

   private FullbrightHook() {
   }

   public static void bind(Impl next) {
      impl = next;
   }

   public static boolean isGamma() {
      Impl i = impl;
      return i != null && i.isGamma();
   }

   public interface Impl {
      boolean isGamma();
   }
}
