package dev.ambience.util.render;

import dev.ambience.inject.Access;
import dev.ambience.util.render.state.QuadRenderState;
import dev.ambience.util.render.state.RectRenderState;
import dev.ambience.util.traits.Util;
import java.awt.Color;
import net.minecraft.class_10799;
import net.minecraft.class_11231;
import net.minecraft.class_332;
import org.joml.Matrix3x2f;

public class RenderUtil implements Util {
   public static void rect(class_332 context, float x1, float y1, float x2, float y2, int color) {
      int ix1 = Math.round(x1);
      int iy1 = Math.round(y1);
      int ix2 = Math.round(x2);
      int iy2 = Math.round(y2);
      context.method_25294(ix1, iy1, ix2, iy2, GuiFade.apply(color));
   }

   public static void line(class_332 context, float x1, float y1, float x2, float y2, int color) {
      line(context, x1, y1, x2, y2, color, 2.0F);
   }

   public static void line(class_332 context, float x1, float y1, float x2, float y2, int color, float thickness) {
      stroke(context, x1, y1, x2, y2, color, thickness);
   }

   public static void stroke(class_332 context, float x1, float y1, float x2, float y2, int color, float thickness) {
      float dx = x2 - x1;
      float dy = y2 - y1;
      float len = (float)Math.sqrt((double)(dx * dx + dy * dy));
      float half = Math.max(0.6F, thickness) * 0.5F;
      if (len < 0.001F) {
         roundRect(context, x1 - half, y1 - half, x1 + half, y1 + half, half, color);
      } else {
         float px = -dy / len * half;
         float py = dx / len * half;
         Access.guiRenderState(context).method_70919(new QuadRenderState(class_10799.field_56879, class_11231.method_70899(), new Matrix3x2f(context.method_51448()), x1 - px, y1 - py, x1 + px, y1 + py, x2 + px, y2 + py, x2 - px, y2 - py, GuiFade.apply(color), Access.scissorPeek(context)));
      }
   }

   public static void roundRect(class_332 context, float x1, float y1, float x2, float y2, float radius, int color) {
      roundRect(context, x1, y1, x2, y2, radius, radius, radius, radius, color);
   }

   public static void roundRect(class_332 context, float x1, float y1, float x2, float y2, float tl, float tr, float br, float bl, int color) {
      submitRound(context, x1, y1, x2, y2, tl, tr, br, bl, color, 0.0F);
   }

   public static void roundOutline(class_332 context, float x1, float y1, float x2, float y2, float radius, int color, float width) {
      roundOutline(context, x1, y1, x2, y2, radius, radius, radius, radius, color, width);
   }

   public static void roundOutline(class_332 context, float x1, float y1, float x2, float y2, float tl, float tr, float br, float bl, int color, float width) {
      submitRound(context, x1, y1, x2, y2, tl, tr, br, bl, color, Math.max(1.0F, width));
   }

   private static void submitRound(class_332 context, float x1, float y1, float x2, float y2, float tl, float tr, float br, float bl, int color, float stroke) {
      if (!(x2 <= x1) && !(y2 <= y1)) {
         if (tl < 0.35F && tr < 0.35F && br < 0.35F && bl < 0.35F && stroke <= 0.01F) {
            rect(context, x1, y1, x2, y2, color);
         } else if (stroke <= 0.01F) {
            SmoothRound.fill(context, x1, y1, x2, y2, tl, tr, br, bl, color);
         } else {
            SmoothRound.outline(context, x1, y1, x2, y2, tl, tr, br, bl, color, stroke);
         }
      }
   }

   public static void rect(class_332 context, float x1, float y1, float x2, float y2, int color, float width) {
      int w = Math.max(1, Math.round(width));
      int c = GuiFade.apply(color);
      context.method_25294(Math.round(x1), Math.round(y1), Math.round(x2), Math.round(y1) + w, c);
      context.method_25294(Math.round(x2) - w, Math.round(y1), Math.round(x2), Math.round(y2), c);
      context.method_25294(Math.round(x1), Math.round(y2) - w, Math.round(x2), Math.round(y2), c);
      context.method_25294(Math.round(x1), Math.round(y1), Math.round(x1) + w, Math.round(y2), c);
   }

   public static void horizontalGradient(class_332 context, float x1, float y1, float x2, float y2, Color left, Color right) {
      gradient(context, Math.round(x1), Math.round(y1), Math.round(x2), Math.round(y2), left.getRGB(), left.getRGB(), right.getRGB(), right.getRGB());
   }

   public static void verticalGradient(class_332 context, float x1, float y1, float x2, float y2, Color top, Color bottom) {
      gradient(context, Math.round(x1), Math.round(y1), Math.round(x2), Math.round(y2), top.getRGB(), bottom.getRGB(), bottom.getRGB(), top.getRGB());
   }

   public static void shadow(class_332 context, float x1, float y1, float x2, float y2, int radius, int alpha) {
      if (radius > 0 && alpha > 0) {
         int ix1 = Math.round(x1);
         int iy1 = Math.round(y1);
         int ix2 = Math.round(x2);
         int iy2 = Math.round(y2);
         int solid = Math.min(255, alpha) << 24;
         int clear = 0;
         gradient(context, ix1, iy1 - radius, ix2, iy1, clear, solid, solid, clear);
         gradient(context, ix1, iy2, ix2, iy2 + radius, solid, clear, clear, solid);
         gradient(context, ix1 - radius, iy1, ix1, iy2, clear, clear, solid, solid);
         gradient(context, ix2, iy1, ix2 + radius, iy2, solid, solid, clear, clear);
         int cornerAlpha = (int)((float)alpha * 0.7F) << 24;
         gradient(context, ix1 - radius, iy1 - radius, ix1, iy1, clear, clear, cornerAlpha, clear);
         gradient(context, ix2, iy1 - radius, ix2 + radius, iy1, clear, clear, clear, cornerAlpha);
         gradient(context, ix1 - radius, iy2, ix1, iy2 + radius, clear, cornerAlpha, clear, clear);
         gradient(context, ix2, iy2, ix2 + radius, iy2 + radius, cornerAlpha, clear, clear, clear);
      }
   }

   public static void gradient(class_332 graphics, int x1, int y1, int x2, int y2, int topLeft, int bottomLeft, int bottomRight, int topRight) {
      Access.guiRenderState(graphics).method_70919(new RectRenderState(class_10799.field_56879, class_11231.method_70899(), new Matrix3x2f(graphics.method_51448()), x1, y1, x2, y2, GuiFade.apply(topLeft), GuiFade.apply(bottomLeft), GuiFade.apply(bottomRight), GuiFade.apply(topRight), Access.scissorPeek(graphics)));
   }
}
