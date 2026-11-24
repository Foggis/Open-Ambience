package dev.ambience.hooks;

public final class AutoCrystalHook {
   private static volatile Impl impl;

   private AutoCrystalHook() {
   }

   public static void bind(Impl next) {
      impl = next;
   }

   public static boolean blocksVanillaUse() {
      Impl current = impl;
      return current != null && current.blocksVanillaUse();
   }

   public interface Impl {
      boolean blocksVanillaUse();
   }
}
