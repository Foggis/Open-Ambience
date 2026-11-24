package dev.ambience.hooks;

import net.minecraft.class_1268;
import net.minecraft.class_4587;
import net.minecraft.class_742;

public final class ViewModelHook {
   private static volatile Impl impl;

   private ViewModelHook() {
   }

   public static void bind(Impl next) {
      impl = next;
   }

   public static boolean enabled() {
      Impl i = impl;
      return i != null && i.enabled();
   }

   public static boolean noSway() {
      Impl i = impl;
      return i != null && i.noSway();
   }

   public static boolean noSwapAnimation() {
      Impl i = impl;
      return i != null && i.noSwapAnimation();
   }

   public static boolean oldAnimation() {
      Impl i = impl;
      return i != null && i.oldAnimation();
   }

   public static boolean applyArm(class_742 player, class_1268 hand, class_4587 poseStack) {
      Impl i = impl;
      if (i != null && i.enabled()) {
         i.applyArm(player, hand, poseStack);
         return true;
      } else {
         return false;
      }
   }

   public interface Impl {
      boolean enabled();

      boolean noSway();

      boolean noSwapAnimation();

      boolean oldAnimation();

      void applyArm(class_742 var1, class_1268 var2, class_4587 var3);
   }
}
