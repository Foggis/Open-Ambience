package dev.ambience.hooks;

public final class AnchorMacroHook {
   private static volatile Impl impl;

   private AnchorMacroHook() {
   }

   public static void bind(Impl next) {
      impl = next;
   }

   public static boolean blocksInteraction() {
      Impl i = impl;
      return i != null && i.blocksInteraction();
   }

   public static boolean isBusy() {
      Impl i = impl;
      return i != null && i.isBusy();
   }

   public static void onMouseTurn(double frameSeconds) {
      Impl i = impl;
      if (i != null) {
         i.onMouseTurn(frameSeconds);
      }

   }

   public static void anchorPlaced(int x, int y, int z) {
      Impl i = impl;
      if (i != null) {
         i.anchorPlaced(x, y, z);
      }

   }

   public interface Impl {
      boolean blocksInteraction();

      boolean isBusy();

      void onMouseTurn(double var1);

      void anchorPlaced(int var1, int var2, int var3);
   }
}
