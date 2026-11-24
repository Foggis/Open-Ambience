package dev.ambience.util.render;

public class GuiFade {
   public static float alpha = 1.0F;

   public static int apply(int color) {
      int a = (int)((float)(color >> 24 & 255) * alpha);
      return color & 16777215 | a << 24;
   }
}
