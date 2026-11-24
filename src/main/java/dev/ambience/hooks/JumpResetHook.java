package dev.ambience.hooks;

public final class JumpResetHook {
   private static volatile Impl impl;

   private JumpResetHook() {
   }

   public static void bind(Impl next) {
      impl = next;
   }

   public static void applyJump() {
      Impl i = impl;
      if (i != null) {
         i.applyJump();
      }

   }

   public interface Impl {
      void applyJump();
   }
}
