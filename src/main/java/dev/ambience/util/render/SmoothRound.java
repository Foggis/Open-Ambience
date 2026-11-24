package dev.ambience.util.render;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.class_10799;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;

public final class SmoothRound {
   private static final int TEX = 256;
   private static class_2960 fillId;
   private static final Map<Integer, class_2960> RING = new HashMap();

   private SmoothRound() {
   }

   public static void fill(class_332 g, float x0, float y0, float x1, float y1, float radius, int color) {
      fill(g, x0, y0, x1, y1, radius, radius, radius, radius, color);
   }

   public static void fill(class_332 g, float x0, float y0, float x1, float y1, float tl, float tr, float br, float bl, int color) {
      if (!(x1 <= x0) && !(y1 <= y0)) {
         int ix0 = Math.round(x0);
         int iy0 = Math.round(y0);
         int ix1 = Math.round(x1);
         int iy1 = Math.round(y1);
         int maxR = Math.max(0, Math.min(ix1 - ix0, iy1 - iy0) / 2);
         int rtl = clampI(Math.round(tl), maxR);
         int rtr = clampI(Math.round(tr), maxR);
         int rbr = clampI(Math.round(br), maxR);
         int rbl = clampI(Math.round(bl), maxR);
         if (rtl == 0 && rtr == 0 && rbr == 0 && rbl == 0) {
            RenderUtil.rect(g, (float)ix0, (float)iy0, (float)ix1, (float)iy1, color);
         } else {
            ensureFill();
            int blit = GuiFade.apply(color);
            int midL = ix0 + Math.max(rtl, rbl);
            int midR = ix1 - Math.max(rtr, rbr);
            if (midR > midL) {
               RenderUtil.rect(g, (float)midL, (float)iy0, (float)midR, (float)iy1, color);
            }

            if (iy1 - rbl > iy0 + rtl) {
               RenderUtil.rect(g, (float)ix0, (float)(iy0 + rtl), (float)(ix0 + Math.max(rtl, rbl)), (float)(iy1 - rbl), color);
            }

            if (iy1 - rbr > iy0 + rtr) {
               RenderUtil.rect(g, (float)(ix1 - Math.max(rtr, rbr)), (float)(iy0 + rtr), (float)ix1, (float)(iy1 - rbr), color);
            }

            corner(g, ix0, iy0, rtl, 0, blit);
            corner(g, ix1 - rtr, iy0, rtr, 1, blit);
            corner(g, ix1 - rbr, iy1 - rbr, rbr, 2, blit);
            corner(g, ix0, iy1 - rbl, rbl, 3, blit);
         }
      }
   }

   public static void outline(class_332 g, float x0, float y0, float x1, float y1, float radius, int color, float width) {
      outline(g, x0, y0, x1, y1, radius, radius, radius, radius, color, width);
   }

   public static void outline(class_332 g, float x0, float y0, float x1, float y1, float tl, float tr, float br, float bl, int color, float width) {
      if (!(x1 <= x0) && !(y1 <= y0)) {
         int ix0 = Math.round(x0);
         int iy0 = Math.round(y0);
         int ix1 = Math.round(x1);
         int iy1 = Math.round(y1);
         int maxR = Math.max(0, Math.min(ix1 - ix0, iy1 - iy0) / 2);
         int rtl = clampI(Math.round(tl), maxR);
         int rtr = clampI(Math.round(tr), maxR);
         int rbr = clampI(Math.round(br), maxR);
         int rbl = clampI(Math.round(bl), maxR);
         int sw = Math.max(1, Math.round(width));
         int blit = GuiFade.apply(color);
         if (ix1 - rtr > ix0 + rtl) {
            RenderUtil.rect(g, (float)(ix0 + rtl), (float)iy0, (float)(ix1 - rtr), (float)(iy0 + sw), color);
         }

         if (ix1 - rbr > ix0 + rbl) {
            RenderUtil.rect(g, (float)(ix0 + rbl), (float)(iy1 - sw), (float)(ix1 - rbr), (float)iy1, color);
         }

         if (iy1 - rbl > iy0 + rtl) {
            RenderUtil.rect(g, (float)ix0, (float)(iy0 + rtl), (float)(ix0 + sw), (float)(iy1 - rbl), color);
         }

         if (iy1 - rbr > iy0 + rtr) {
            RenderUtil.rect(g, (float)(ix1 - sw), (float)(iy0 + rtr), (float)ix1, (float)(iy1 - rbr), color);
         }

         ringCorner(g, ix0, iy0, rtl, sw, 0, blit);
         ringCorner(g, ix1 - rtr, iy0, rtr, sw, 1, blit);
         ringCorner(g, ix1 - rbr, iy1 - rbr, rbr, sw, 2, blit);
         ringCorner(g, ix0, iy1 - rbl, rbl, sw, 3, blit);
      }
   }

   private static void corner(class_332 g, int x, int y, int r, int quad, int color) {
      if (r > 0 && fillId != null) {
         blitQuad(g, fillId, x, y, r, quad, color);
      }
   }

   private static void ringCorner(class_332 g, int x, int y, int r, int stroke, int quad, int color) {
      if (r > 0) {
         class_2960 id = ring(r, stroke);
         if (id != null) {
            blitQuad(g, id, x, y, r, quad, color);
         }
      }
   }

   private static void blitQuad(class_332 g, class_2960 id, int x, int y, int r, int quad, int color) {
      int half = 128;
      float u = quad != 1 && quad != 2 ? 0.0F : (float)half;
      float v = quad != 2 && quad != 3 ? 0.0F : (float)half;
      g.method_25293(class_10799.field_56883, id, x, y, u, v, r, r, half, half, 256, 256, color);
   }

   private static void ensureFill() {
      if (fillId == null && class_310.method_1551() != null) {
         fillId = LinearTextures.register("circle_fill", LinearTextures.fromArgb(sdfCircle(false, 0.0F), 256, 256));
      }
   }

   private static class_2960 ring(int radius, int stroke) {
      if (class_310.method_1551() == null) {
         return null;
      } else {
         int key = Math.round((float)stroke / (float)Math.max(1, radius) * 1000.0F);
         class_2960 cached = (class_2960)RING.get(key);
         if (cached != null) {
            return cached;
         } else {
            float strokeTex = Math.max(2.0F, (float)stroke * 128.0F / (float)Math.max(1, radius));
            class_2960 id = LinearTextures.register("circle_ring_" + key, LinearTextures.fromArgb(sdfCircle(true, strokeTex), 256, 256));
            RING.put(key, id);
            return id;
         }
      }
   }

   private static int[] sdfCircle(boolean ring, float strokeTex) {
      int[] rgb = new int[65536];
      float c = 128.0F;
      float outer = 127.5F;
      float inner = ring ? Math.max(0.0F, outer - strokeTex) : -256.0F;

      for(int y = 0; y < 256; ++y) {
         for(int x = 0; x < 256; ++x) {
            float d = (float)Math.hypot((double)((float)x + 0.5F - c), (double)((float)y + 0.5F - c));
            float aOuter = clamp01(0.5F - (d - outer));
            float a = aOuter;
            if (ring) {
               float aHole = clamp01(0.5F - (inner - d));
               a = aOuter * aHole;
            }

            rgb[y * 256 + x] = (int)(a * 255.0F) << 24 | 16777215;
         }
      }

      return rgb;
   }

   private static int clampI(int radius, int cap) {
      return Math.max(0, Math.min(radius, cap));
   }

   private static float clamp01(float v) {
      return Math.max(0.0F, Math.min(1.0F, v));
   }
}
