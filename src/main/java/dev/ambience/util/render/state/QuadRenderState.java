package dev.ambience.util.render.state;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.class_11231;
import net.minecraft.class_11244;
import net.minecraft.class_4588;
import net.minecraft.class_8030;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

public record QuadRenderState(RenderPipeline pipeline, class_11231 textureSetup, Matrix3x2f pose, float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3, int color, @Nullable class_8030 scissorArea, @Nullable class_8030 bounds) implements class_11244 {
   public QuadRenderState(RenderPipeline pipeline, class_11231 textureSetup, Matrix3x2f pose, float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3, int color, @Nullable class_8030 scissor) {
      this(pipeline, textureSetup, pose, x0, y0, x1, y1, x2, y2, x3, y3, color, scissor, boundsOf(x0, y0, x1, y1, x2, y2, x3, y3, pose, scissor));
   }

   public void method_70917(class_4588 vertices) {
      this.vert(vertices, this.x0, this.y0);
      this.vert(vertices, this.x1, this.y1);
      this.vert(vertices, this.x2, this.y2);
      this.vert(vertices, this.x3, this.y3);
   }

   private void vert(class_4588 vertices, float x, float y) {
      vertices.method_70815(this.pose, x, y).method_39415(this.color);
   }

   private static @Nullable class_8030 boundsOf(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3, Matrix3x2f pose, @Nullable class_8030 scissor) {
      float minX = Math.min(Math.min(x0, x1), Math.min(x2, x3));
      float minY = Math.min(Math.min(y0, y1), Math.min(y2, y3));
      float maxX = Math.max(Math.max(x0, x1), Math.max(x2, x3));
      float maxY = Math.max(Math.max(y0, y1), Math.max(y2, y3));
      int ix0 = (int)Math.floor((double)minX);
      int iy0 = (int)Math.floor((double)minY);
      int ix1 = (int)Math.ceil((double)maxX);
      int iy1 = (int)Math.ceil((double)maxY);
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
