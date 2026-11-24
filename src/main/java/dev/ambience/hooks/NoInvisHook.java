package dev.ambience.hooks;

public final class NoInvisHook {
   private static volatile Impl impl;

   private NoInvisHook() {
   }

   public static void bind(Impl next) {
      impl = next;
   }

   public static boolean enabled() {
      Impl i = impl;
      return i != null && i.enabled();
   }

   public interface Impl {
      boolean enabled();
   }
}
