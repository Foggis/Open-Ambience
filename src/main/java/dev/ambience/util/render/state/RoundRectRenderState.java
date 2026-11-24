package dev.ambience.util.render.state;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.ArrayList;
import net.minecraft.class_11231;
import net.minecraft.class_11244;
import net.minecraft.class_4588;
import net.minecraft.class_8030;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

public record RoundRectRenderState(RenderPipeline pipeline, class_11231 textureSetup, Matrix3x2f pose, float x0, float y0, float x1, float y1, float tl, float tr, float br, float bl, int color, float stroke, @Nullable class_8030 scissorArea, @Nullable class_8030 bounds) implements class_11244 {
   public RoundRectRenderState(RenderPipeline pipeline, class_11231 textureSetup, Matrix3x2f pose, float x0, float y0, float x1, float y1, float tl, float tr, float br, float bl, int color, float stroke, @Nullable class_8030 scissor) {
      this(pipeline, textureSetup, pose, x0, y0, x1, y1, tl, tr, br, bl, color, stroke, scissor, boundsOf(x0, y0, x1, y1, pose, scissor));
   }

   public void method_70917(class_4588 vertices) {
      float[] outer = perimeter(this.x0, this.y0, this.x1, this.y1, this.tl, this.tr, this.br, this.bl);
      if (outer.length >= 6) {
         if (this.stroke <= 0.01F) {
            this.fillRows(vertices);
         } else {
            float[] inner = perimeter(this.x0 + this.stroke, this.y0 + this.stroke, this.x1 - this.stroke, this.y1 - this.stroke, Math.max(0.0F, this.tl - this.stroke), Math.max(0.0F, this.tr - this.stroke), Math.max(0.0F, this.br - this.stroke), Math.max(0.0F, this.bl - this.stroke));
            if (inner.length < 6) {
               this.fillRows(vertices);
            } else {
               int n = Math.min(outer.length, inner.length);

               for(int i = 0; i < n; i += 2) {
                  int j = (i + 2) % n;
                  this.vert(vertices, outer[i], outer[i + 1]);
                  this.vert(vertices, inner[i], inner[i + 1]);
                  this.vert(vertices, inner[j], inner[j + 1]);
                  this.vert(vertices, outer[j], outer[j + 1]);
               }

            }
         }
      }
   }

   private void fillRows(class_4588 vertices) {
      int yStart = (int)Math.floor((double)this.y0);
      int yEnd = (int)Math.ceil((double)this.y1);

      for(int y = yStart; y < yEnd; ++y) {
         float rowTop = Math.max(this.y0, (float)y);
         float rowBot = Math.min(this.y1, (float)y + 1.0F);
         if (!(rowBot - rowTop < 0.001F)) {
            float mid = (rowTop + rowBot) * 0.5F;
            float left = this.x0 + this.inset(mid, true);
            float right = this.x1 - this.inset(mid, false);
            if (!(right - left < 0.001F)) {
               this.vert(vertices, left, rowTop);
               this.vert(vertices, left, rowBot);
               this.vert(vertices, right, rowBot);
               this.vert(vertices, right, rowTop);
            }
         }
      }

   }

   private float inset(float y, boolean left) {
      float r;
      float cy;
      if (left) {
         if (this.tl > 0.01F && y < this.y0 + this.tl) {
            r = this.tl;
            cy = this.y0 + this.tl;
         } else {
            if (!(this.bl > 0.01F) || !(y > this.y1 - this.bl)) {
               return 0.0F;
            }

            r = this.bl;
            cy = this.y1 - this.bl;
         }
      } else if (this.tr > 0.01F && y < this.y0 + this.tr) {
         r = this.tr;
         cy = this.y0 + this.tr;
      } else {
         if (!(this.br > 0.01F) || !(y > this.y1 - this.br)) {
            return 0.0F;
         }

         r = this.br;
         cy = this.y1 - this.br;
      }

      float dy = Math.abs(y - cy);
      if (dy >= r) {
         return 0.0F;
      } else {
         return r - (float)Math.sqrt((double)Math.max(0.0F, r * r - dy * dy));
      }
   }

   private void vert(class_4588 vertices, float x, float y) {
      vertices.method_70815(this.pose, x, y).method_39415(this.color);
   }

   private static float[] perimeter(float x0, float y0, float x1, float y1, float tl, float tr, float br, float bl) {
      if (!(x1 <= x0) && !(y1 <= y0)) {
         float w = x1 - x0;
         float h = y1 - y0;
         float cap = Math.min(w, h) * 0.5F;
         tl = clamp(tl, cap);
         tr = clamp(tr, cap);
         br = clamp(br, cap);
         bl = clamp(bl, cap);
         ArrayList<Float> pts = new ArrayList(140);
         int segs = 16;
         arc(pts, x0 + Math.max(tl, 0.001F), y0 + Math.max(tl, 0.001F), tl, (float)Math.PI, ((float)Math.PI * 1.5F), segs);
         arc(pts, x1 - Math.max(tr, 0.001F), y0 + Math.max(tr, 0.001F), tr, ((float)Math.PI * 1.5F), ((float)Math.PI * 2F), segs);
         arc(pts, x1 - Math.max(br, 0.001F), y1 - Math.max(br, 0.001F), br, 0.0F, ((float)Math.PI / 2F), segs);
         arc(pts, x0 + Math.max(bl, 0.001F), y1 - Math.max(bl, 0.001F), bl, ((float)Math.PI / 2F), (float)Math.PI, segs);
         float[] out = new float[pts.size()];

         for(int i = 0; i < pts.size(); ++i) {
            out[i] = (Float)pts.get(i);
         }

         return out;
      } else {
         return new float[0];
      }
   }

   private static void arc(ArrayList<Float> pts, float cx, float cy, float r, float a0, float a1, int segs) {
      float rr = Math.max(r, 0.001F);

      for(int i = 0; i <= segs; ++i) {
         float t = (float)i / (float)segs;
         float a = a0 + (a1 - a0) * t;
         pts.add(cx + (float)Math.cos((double)a) * rr);
         pts.add(cy + (float)Math.sin((double)a) * rr);
      }

   }

   private static float clamp(float radius, float cap) {
      return Math.max(0.0F, Math.min(radius, cap));
   }

   private static @Nullable class_8030 boundsOf(float x0, float y0, float x1, float y1, Matrix3x2f pose, @Nullable class_8030 scissor) {
      int ix0 = (int)Math.floor((double)Math.min(x0, x1));
      int iy0 = (int)Math.floor((double)Math.min(y0, y1));
      int ix1 = (int)Math.ceil((double)Math.max(x0, x1));
      int iy1 = (int)Math.ceil((double)Math.max(y0, y1));
      class_8030 local = (new class_8030(ix0, iy0, Math.max(1, ix1 - ix0), Math.max(1, iy1 - iy0))).method_71523(pose);
      if (local == null) {
         return scissor;
      } else if (scissor == null) {
         return local;
      } else {
         class_8030 hit = scissor.method_49701(local);
         return hit != null ? hit : local;
      }
   }

   public RenderPipeline comp_4055() {
      return this.pipeline;
   }

   public class_11231 comp_4056() {
      return this.textureSetup;
   }

   public @Nullable class_8030 comp_4069() {
      return this.scissorArea;
   }

   public @Nullable class_8030 comp_4274() {
      return this.bounds;
   }
}
