package dev.ambience.features.modules.movement;

import dev.ambience.features.modules.Module;
import dev.ambience.features.settings.Setting;
import dev.ambience.hooks.AutoSprintHook;
import dev.ambience.nativebridge.NativeModules;
import dev.ambience.util.SilentRotation;
import net.minecraft.class_10185;
import net.minecraft.class_1294;
import net.minecraft.class_1702;
import net.minecraft.class_746;

public class AutoSprintModule extends Module {
   private static AutoSprintModule INSTANCE;
   private final Setting<Integer> delay = this.num("Delay", 2, 0, 8).setPage("General");

   public AutoSprintModule() {
      super("AutoSprint", "Sprints after a short delay when you start moving.", Module.Category.MOVEMENT);
      INSTANCE = this;
      AutoSprintHook.bind(AutoSprintModule::apply);
   }

   public static AutoSprintModule getInstance() {
      return INSTANCE;
   }

   public static void apply(class_746 player) {
      if (player != null && player.field_3913 != null) {
         AutoSprintModule module = getInstance();
         if (module != null && module.isEnabled()) {
            class_10185 keys = player.field_3913.field_54155;
            int actions = NativeModules.autoSprintStep(player.field_6012, (Integer)module.delay.getValue(), snapshot(player, keys));
            if ((actions & 1) != 0) {
               player.field_3913.field_54155 = new class_10185(keys.comp_3159(), keys.comp_3160(), keys.comp_3161(), keys.comp_3162(), keys.comp_3163(), keys.comp_3164(), true);
            }
         } else {
            if (module != null) {
               NativeModules.resetAutoSprint();
            }

         }
      }
   }

   public String getDisplayInfo() {
      return String.valueOf(this.delay.getValue()) + "t";
   }

   public void onDisable() {
      NativeModules.resetAutoSprint();
   }

   public void onPreTick() {
      if (!nullCheck()) {
         apply(mc.field_1724);
      }
   }

   private static int snapshot(class_746 player, class_10185 keys) {
      int input = 1;
      if (keys.comp_3159()) {
         input |= 2;
      }

      if (keys.comp_3160()) {
         input |= 4;
      }

      if (keys.comp_3161()) {
         input |= 8;
      }

      if (keys.comp_3162()) {
         input |= 16;
      }

      if (keys.comp_3165()) {
         input |= 32;
      }

      if (player.method_5715()) {
         input |= 64;
      }

      if (player.method_6115()) {
         input |= 128;
      }

      if (player.method_5765()) {
         input |= 256;
      }

      if (player.method_5681()) {
         input |= 512;
      }

      if (player.method_6059(class_1294.field_5919)) {
         input |= 1024;
      }

      class_1702 food = player.method_7344();
      if (food != null && food.method_7586() <= 6) {
         input |= 2048;
      }

      return input;
   }
}
