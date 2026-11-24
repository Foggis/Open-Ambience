package dev.ambience.hooks;

public final class AutoEscHook {
   private static volatile Impl impl;

   private AutoEscHook() {
   }

   public static void bind(Impl next) {
      impl = next;
   }

   public static boolean blocksInteraction() {
      Impl i = impl;
      return i != null && i.blocksInteraction();
   }

   public interface Impl {
      boolean blocksInteraction();
   }
}
