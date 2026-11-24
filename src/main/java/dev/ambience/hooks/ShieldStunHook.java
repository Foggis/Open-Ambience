package dev.ambience.hooks;

import net.minecraft.class_1297;
import net.minecraft.class_1657;

public final class ShieldStunHook {
   private static volatile Impl impl;

   private ShieldStunHook() {
   }

   public static void bind(Impl next) {
      impl = next;
   }

   public static boolean beforeAttack(class_1657 player, class_1297 entity) {
      Impl i = impl;
      return i != null && i.beforeAttack(player, entity);
   }

   public static void afterAttack() {
      Impl i = impl;
      if (i != null) {
         i.afterAttack();
      }

   }

   public interface Impl {
      boolean beforeAttack(class_1657 var1, class_1297 var2);

      void afterAttack();
   }
}
