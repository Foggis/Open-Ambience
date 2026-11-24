package dev.ambience.hooks;

public final class AimAssistHook {
   private static volatile Impl impl;

   private AimAssistHook() {
   }

   public static void bind(Impl next) {
      impl = next;
   }

   public static void onMouseTurn(double frameSeconds) {
      Impl i = impl;
      if (i != null) {
         i.onMouseTurn(frameSeconds);
      }

   }

   public interface Impl {
      void onMouseTurn(double var1);
   }
}
