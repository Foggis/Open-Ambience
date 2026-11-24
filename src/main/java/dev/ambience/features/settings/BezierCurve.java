package dev.ambience.features.settings;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_3532;

public final class BezierCurve {
   public static final float DELAY_TRACK_MIN = 0.0F;
   public static final float DELAY_TRACK_MAX = 400.0F;
   public float x1;
   public float y1;
   public float x2;
   public float y2;
   public float minMs;
   public float maxMs;

   public BezierCurve(float x1, float y1, float x2, float y2, float minMs, float maxMs) {
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
      this.minMs = minMs;
      this.maxMs = maxMs;
   }

   public BezierCurve copy() {
      return new BezierCurve(this.x1, this.y1, this.x2, this.y2, this.minMs, this.maxMs);
   }

   public BezierCurve clamped() {
      BezierCurve out = this.copy();
      out.x1 = class_3532.method_15363(out.x1, 0.0F, 1.0F);
      out.y1 = class_3532.method_15363(out.y1, 0.0F, 1.0F);
      out.x2 = class_3532.method_15363(out.x2, 0.0F, 1.0F);
      out.y2 = class_3532.method_15363(out.y2, 0.0F, 1.0F);
      float min = class_3532.method_15363(Math.min(out.minMs, out.maxMs), 0.0F, 400.0F);
      float max = class_3532.method_15363(Math.max(out.minMs, out.maxMs), 0.0F, 400.0F);
      out.minMs = min;
      out.maxMs = max;
      return out;
   }

   public float ease(float x) {
      x = class_3532.method_15363(x, 0.0F, 1.0F);
      if (x != 0.0F && x != 1.0F) {
         return Math.abs(this.x1 - this.y1) < 1.0E-4F && Math.abs(this.x2 - this.y2) < 1.0E-4F ? x : bezier(this.solveT(x), this.y1, this.y2);
      } else {
         return x;
      }
   }

   public int sampleMs() {
      BezierCurve curve = this.clamped();
      if (curve.maxMs <= curve.minMs) {
         return Math.round(curve.minMs);
      } else {
         float eased = curve.ease(ThreadLocalRandom.current().nextFloat());
         if (!Float.isFinite(eased)) {
            eased = ThreadLocalRandom.current().nextFloat();
         }

         return Math.max(0, Math.round(curve.minMs + (curve.maxMs - curve.minMs) * eased));
      }
   }

   public static BezierCurve parse(String raw) {
      if (raw != null && !raw.isBlank()) {
         String[] parts = raw.split(",");
         if (parts.length != 6) {
            return null;
         } else {
            try {
               return (new BezierCurve(Float.parseFloat(parts[0].trim()), Float.parseFloat(parts[1].trim()), Float.parseFloat(parts[2].trim()), Float.parseFloat(parts[3].trim()), Float.parseFloat(parts[4].trim()), Float.parseFloat(parts[5].trim()))).clamped();
            } catch (NumberFormatException var3) {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   public String toString() {
      return this.x1 + "," + this.y1 + "," + this.x2 + "," + this.y2 + "," + this.minMs + "," + this.maxMs;
   }

   private float solveT(float x) {
      float t = x;
      int i = 0;

      while(true) {
         if (i < 8) {
            float xEst = bezier(t, this.x1, this.x2);
            float dx = bezierDerivative(t, this.x1, this.x2);
            if (!(Math.abs(dx) < 1.0E-6F)) {
               float next = t - (xEst - x) / dx;
               if (!(next < 0.0F) && !(next > 1.0F)) {
                  t = next;
                  ++i;
                  continue;
               }

               return this.solveTBisection(x);
            }
         }

         return class_3532.method_15363(t, 0.0F, 1.0F);
      }
   }

   private float solveTBisection(float x) {
      float lo = 0.0F;
      float hi = 1.0F;
      float t = x;

      for(int i = 0; i < 12; ++i) {
         t = (lo + hi) * 0.5F;
         if (bezier(t, this.x1, this.x2) < x) {
            lo = t;
         } else {
            hi = t;
         }
      }

      return t;
   }

   private static float bezier(float t, float p1, float p2) {
      float u = 1.0F - t;
      return 3.0F * u * u * t * p1 + 3.0F * u * t * t * p2 + t * t * t;
   }

   private static float bezierDerivative(float t, float p1, float p2) {
      float u = 1.0F - t;
      return 3.0F * u * u * p1 + 6.0F * u * t * (p2 - p1) + 3.0F * t * t * (1.0F - p2);
   }
}
