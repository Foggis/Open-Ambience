package dev.ambience.util;

import dev.ambience.util.traits.Util;
import net.minecraft.class_3532;

public final class RotationUtil implements Util {
   private RotationUtil() {
   }

   public static float quantizeYaw(float from, float to) {
      float delta = class_3532.method_15393(to - from);
      return from + (float)snapGcd((double)delta);
   }

   public static float quantizePitch(float from, float to) {
      float delta = to - from;
      return class_3532.method_15363(from + (float)snapGcd((double)delta), -90.0F, 90.0F);
   }

   private static double snapGcd(double delta) {
      double gcd = mouseGcd();
      return gcd < 1.0E-4 ? delta : (double)Math.round(delta / gcd) * gcd;
   }

   private static double mouseGcd() {
      double sensitivity = (Double)mc.field_1690.method_42495().method_41753() * 0.6 + 0.2;
      return sensitivity * sensitivity * sensitivity * (double)8.0F * 0.15;
   }
}
