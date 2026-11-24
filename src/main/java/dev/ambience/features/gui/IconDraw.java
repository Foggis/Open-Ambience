package dev.ambience.features.gui;

import dev.ambience.features.modules.Module;
import dev.ambience.util.render.GuiIcons;
import net.minecraft.class_332;

public final class IconDraw {
   public static final int SIZE = 18;

   private IconDraw() {
   }

   public static void category(class_332 g, Module.Category cat, float x, float y, int color) {
      category(g, cat, x, y, 18, color);
   }

   public static void category(class_332 g, Module.Category cat, float x, float y, int size, int color) {
      GuiIcons.category(g, cat, x, y, (float)size, color);
   }
}
