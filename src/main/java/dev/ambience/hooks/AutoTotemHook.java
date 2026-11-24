package dev.ambience.hooks;

import net.minecraft.class_2663;
import net.minecraft.class_310;

public final class AutoTotemHook {
   private static volatile Impl impl;

   private AutoTotemHook() {
   }

   public static void bind(Impl next) {
      impl = next;
   }

   public static boolean blocksInteraction() {
      Impl i = impl;
      return i != null && i.blocksInteraction();
   }

   public static boolean blocksInventoryInput() {
      Impl i = impl;
      return i != null && i.blocksInventoryInput();
   }

   public static void freezeInput() {
      Impl i = impl;
      if (i != null) {
         i.freezeInput();
      }

   }

   public static void onLocalTotemPop() {
      Impl i = impl;
      if (i != null) {
         i.onLocalTotemPop();
      }

   }

   public static void handleEntityEvent(class_2663 packet) {
      if (packet != null && packet.method_11470() == 35) {
         class_310 mc = class_310.method_1551();
         if (mc.method_18854()) {
            handleLocalPopOnClientThread(mc, packet);
         } else {
            mc.execute(() -> handleLocalPopOnClientThread(mc, packet));
         }

      }
   }

   private static void handleLocalPopOnClientThread(class_310 mc, class_2663 packet) {
      if (mc.field_1687 != null && packet.method_11469(mc.field_1687) == mc.field_1724) {
         onLocalTotemPop();
      }

   }

   public interface Impl {
      boolean blocksInteraction();

      boolean blocksInventoryInput();

      void freezeInput();

      void onLocalTotemPop();
   }
}
