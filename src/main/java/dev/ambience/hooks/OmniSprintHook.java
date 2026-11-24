package dev.ambience.hooks;

import net.minecraft.class_746;

public final class OmniSprintHook {
   private static volatile Impl impl;

   private OmniSprintHook() {
   }

   public static void bind(Impl next) {
      impl = next;
   }

   public static void apply(class_746 player) {
      Impl i = impl;
      if (i != null) {
         i.apply(player);
      }

   }

   public interface Impl {
      void apply(class_746 var1);
   }
}
