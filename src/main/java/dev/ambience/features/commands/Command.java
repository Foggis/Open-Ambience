package dev.ambience.features.commands;

import dev.ambience.features.Feature;
import net.minecraft.class_2561;

public abstract class Command extends Feature {
   public static void sendMessage(String message, Object... format) {
      if (message != null && mc.field_1724 != null) {
         String text = format.length == 0 ? message : String.format(message, format);
         mc.field_1724.method_7353(class_2561.method_43470(text), false);
      }
   }
}
