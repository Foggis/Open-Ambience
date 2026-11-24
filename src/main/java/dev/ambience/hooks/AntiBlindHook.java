package dev.ambience.hooks;

import net.minecraft.class_1291;
import net.minecraft.class_6880;

public final class AntiBlindHook {
   private static volatile Impl impl;

   private AntiBlindHook() {
   }

   public static void bind(Impl next) {
      impl = next;
   }

   public static boolean active() {
      Impl i = impl;
      return i != null && i.active();
   }

   public static boolean hides(class_6880<class_1291> effect) {
      Impl i = impl;
      return i != null && i.hides(effect);
   }

   public interface Impl {
      boolean active();

      boolean hides(class_6880<class_1291> var1);
   }
}
