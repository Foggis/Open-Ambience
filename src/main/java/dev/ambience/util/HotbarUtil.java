package dev.ambience.util;

import dev.ambience.inject.Access;
import dev.ambience.util.traits.Util;
import net.minecraft.class_1268;
import net.minecraft.class_1792;
import net.minecraft.class_746;

public final class HotbarUtil implements Util {
   private HotbarUtil() {
   }

   public static int find(class_746 player, class_1792 item) {
      int selected = player.method_31548().method_67532();
      if (player.method_31548().method_5438(selected).method_31574(item)) {
         return selected;
      } else {
         for(int i = 0; i < 9; ++i) {
            if (player.method_31548().method_5438(i).method_31574(item)) {
               return i;
            }
         }

         return -1;
      }
   }

   public static boolean select(class_746 player, int slot) {
      if (slot >= 0 && slot <= 8 && mc.field_1761 != null) {
         if (player.method_31548().method_67532() != slot) {
            int from = player.method_31548().method_67532();
            player.method_31548().method_61496(slot);
            ModuleDebug.slot(from, slot, player.method_31548().method_5438(slot).method_7909().toString());
         }

         Access.ensureHasSentCarriedItem(mc.field_1761);
         return player.method_31548().method_67532() == slot;
      } else {
         return false;
      }
   }

   public static boolean holding(class_746 player, class_1792 item) {
      return player.method_6047().method_31574(item);
   }

   public static boolean onCooldown(class_746 player, class_1792 item) {
      int slot = find(player, item);
      return slot == -1 ? true : player.method_7357().method_7904(player.method_31548().method_5438(slot));
   }

   public static boolean useMainHand(class_746 player) {
      if (mc.field_1761 == null) {
         return false;
      } else {
         GrimMovementGuard.markAction();
         mc.field_1761.method_2919(player, class_1268.field_5808);
         player.method_6104(class_1268.field_5808);
         return true;
      }
   }
}
