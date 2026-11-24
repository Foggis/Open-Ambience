package dev.ambience.hooks;

import net.minecraft.class_12076;

public final class CustomSkyboxHook {
   private static volatile Impl impl;

   private CustomSkyboxHook() {
   }

   public static void bind(Impl next) {
      impl = next;
   }

   public static boolean enabled() {
      Impl i = impl;
      return i != null && i.enabled();
   }

   public static boolean hideSunMoon() {
      Impl i = impl;
      return i != null && i.enabled() && i.hideSunMoon();
   }

   public static void apply(class_12076 state) {
      Impl i = impl;
      if (i != null && i.enabled()) {
         i.apply(state);
      }

   }

   public interface Impl {
      boolean enabled();

      boolean hideSunMoon();

      void apply(class_12076 var1);
   }
}
