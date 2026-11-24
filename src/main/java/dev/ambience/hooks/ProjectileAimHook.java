package dev.ambience.hooks;

public final class ProjectileAimHook {
   private static volatile Impl impl;

   private ProjectileAimHook() {
   }

   public static void bind(Impl next) {
      impl = next;
   }

   public static void onMouseTurn(double frameSeconds) {
      Impl i = impl;
      if (i != null) {
         try {
            i.onMouseTurn(frameSeconds);
         } catch (Throwable var4) {
            impl = null;
         }

      }
   }

   public interface Impl {
      void onMouseTurn(double var1);
   }
}
