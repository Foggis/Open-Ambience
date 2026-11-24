package dev.ambience.hooks;

import net.minecraft.class_2680;

public final class XRayHook {
   private static volatile Impl impl;

   private XRayHook() {
   }

   public static void bind(Impl next) {
      impl = next;
   }

   public static boolean active() {
      Impl i = impl;
      return i != null && i.active();
   }

   public static boolean hides(class_2680 state) {
      Impl i = impl;
      return i != null && i.hides(state);
   }

   public static boolean hidesFluids() {
      Impl i = impl;
      return i != null && i.hidesFluids();
   }

   public interface Impl {
      boolean active();

      boolean hides(class_2680 var1);

      boolean hidesFluids();
   }
}
