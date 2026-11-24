package dev.ambience.util.render;

import dev.ambience.util.traits.Util;
import java.awt.Color;
import net.minecraft.class_12178;
import net.minecraft.class_12179;
import net.minecraft.class_12180;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_4184;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.joml.Vector4f;

public final class EspDraw {
   private static final float TRACER_WIDTH = 2.5F;
   private static final double TRACER_NEAR = 0.15;
   private static class_243 tracerOrigin;

   private EspDraw() {
   }

   public static void box(class_238 box, Color color, boolean fill, boolean throughWalls) {
      if (box != null && color != null) {
         try {
            int stroke = color.getRGB();
            class_12178 props = fill ? class_12180.method_75541(box, class_12179.method_75537(stroke, 2.5F, fillArgb(color))) : class_12180.method_75541(box, class_12179.method_75535(stroke));
            if (throughWalls) {
               props.method_75533();
            }
         } catch (IllegalStateException var6) {
         }

      }
   }

   public static void tracer(class_243 to, Color color, boolean throughWalls) {
      if (to != null && color != null) {
         class_243 from = tracerOrigin();

         try {
            class_12178 props = class_12180.method_75546(from, to, color.getRGB(), 2.5F);
            if (throughWalls) {
               props.method_75533();
            }
         } catch (IllegalStateException var5) {
         }

      }
   }

   public static void updateTracerOrigin(Matrix4fc projection, Matrix4fc view) {
      if (projection != null && view != null && Util.mc.field_1773 != null) {
         Matrix4f invProjection = (new Matrix4f(projection)).invert();
         Matrix4f invView = (new Matrix4f(view)).invert();
         Vector4f center = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
         invProjection.transform(center);
         invView.transform(center);
         if (center.w != 0.0F) {
            center.div(center.w);
         }

         class_243 camera = Util.mc.field_1773.method_19418().method_71156();
         tracerOrigin = new class_243(camera.field_1352 + (double)center.x, camera.field_1351 + (double)center.y, camera.field_1350 + (double)center.z);
      } else {
         tracerOrigin = null;
      }
   }

   public static class_243 tracerOrigin() {
      class_4184 camera = Util.mc.field_1773.method_19418();
      class_243 pos = camera.method_71156();
      Vector3fc forward = camera.method_19335();
      class_243 alongLook = new class_243(pos.field_1352 + (double)forward.x() * 0.15, pos.field_1351 + (double)forward.y() * 0.15, pos.field_1350 + (double)forward.z() * 0.15);
      if (tracerOrigin == null) {
         return alongLook;
      } else {
         return tracerOrigin.method_1025(pos) < 0.0225 ? alongLook : tracerOrigin;
      }
   }

   private static int fillArgb(Color color) {
      int alpha = Math.clamp((long)(color.getAlpha() / 4), 24, 70);
      return (new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha)).getRGB();
   }
}
