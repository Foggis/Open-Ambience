package dev.ambience.hooks;

import net.minecraft.class_1297;
import net.minecraft.class_243;

public final class CrystalOptimizerHook {
   private static volatile Impl impl;

   private CrystalOptimizerHook() {
   }

   public static void bind(Impl next) {
      impl = next;
   }

   public static void afterAttack(class_1297 entity) {
      Impl i = impl;
      if (i != null) {
         i.afterAttack(entity);
      }

   }

   public static boolean consumePredictedExplosion(class_243 center) {
      Impl i = impl;
      return i != null && i.consumePredictedExplosion(center);
   }

   public interface Impl {
      void afterAttack(class_1297 var1);

      boolean consumePredictedExplosion(class_243 var1);
   }
}
