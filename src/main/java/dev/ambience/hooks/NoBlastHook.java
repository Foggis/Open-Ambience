package dev.ambience.hooks;

import net.minecraft.class_2394;

public final class NoBlastHook {
   private static volatile Impl impl;

   private NoBlastHook() {
   }

   public static void bind(Impl next) {
      impl = next;
   }

   public static boolean enabled() {
      Impl i = impl;
      return i != null && i.enabled();
   }

   public static boolean shouldCancel(class_2394 options) {
      Impl i = impl;
      return i != null && i.shouldCancel(options);
   }

   public interface Impl {
      boolean enabled();

      boolean shouldCancel(class_2394 var1);
   }
}
