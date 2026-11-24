package dev.ambience.features.gui;

import dev.ambience.features.modules.client.ClickGuiModule;
import dev.ambience.util.render.RenderUtil;
import dev.ambience.util.render.font.Fonts;
import java.awt.Color;
import net.minecraft.class_310;
import net.minecraft.class_332;

public final class CenteredStyle {
   public static final int ROW = 20;
   public static final int SLIDER_ROW = 28;
   public static final float TRACK_H = 3.5F;
   public static final float THUMB_R = 5.0F;
   public static final int THUMB_FILL = -723208;
   public static final int CHIP_BG = (new Color(255, 255, 255, 36)).getRGB();
   public static final int CHIP_BG_HOVER = (new Color(255, 255, 255, 52)).getRGB();
   public static final int FIELD_BG = (new Color(6, 7, 10, 210)).getRGB();
   public static final int SEARCH_H = 15;
   public static final int ROW_BG = (new Color(255, 255, 255, 40)).getRGB();
   public static final int ROW_HOVER = (new Color(255, 255, 255, 54)).getRGB();
   public static final int TOGGLE_OFF = (new Color(255, 255, 255, 22)).getRGB();
   public static final float TOGGLE_W = 24.0F;
   public static final float TOGGLE_H = 14.0F;
   public static final int SETTING_GAP = 5;
   public static final int SHEEN = (new Color(255, 255, 255, 28)).getRGB();
   public static final int RIM = (new Color(255, 255, 255, 36)).getRGB();
   public static final int RIM_SOFT = (new Color(255, 255, 255, 18)).getRGB();
   public static float frameDt;

   private CenteredStyle() {
   }

   public static boolean active() {
      class_310 mc = class_310.method_1551();
      return mc != null && mc.field_1755 instanceof CenteredGui;
   }

   public static int row() {
      return active() ? 20 : 12;
   }

   public static int sliderRow() {
      return active() ? 28 : 12;
   }

   public static Color accent() {
      return Widget.currentAccent != null ? Widget.currentAccent : new Color(172, 80, 22);
   }

   public static float trackLeft(float x) {
      return x + 8.0F;
   }

   public static float trackRight(float x, float width) {
      return x + width - 8.0F;
   }

   public static float trackTop(float y, float height) {
      return y + height - 11.0F;
   }

   public static float labelY(float y, float height) {
      return y + (height - (float)Fonts.lineHeight()) * 0.5F;
   }

   public static float sliderLabelY(float y) {
      return y + 4.0F;
   }

   public static void hoverRow(class_332 g, float x, float y, float width, float height, boolean hover) {
      if (!active()) {
         if (hover) {
            RenderUtil.roundRect(g, x, y, x + width, y + height, 4.0F, GuiTheme.HIGHLIGHT_TOP);
         }

      } else {
         plasticTile(g, x, y, x + width, y + height, 6.0F, hover ? ROW_HOVER : ROW_BG);
      }
   }

   public static void plasticSheet(class_332 g, float x1, float y1, float x2, float y2, float r, int fill) {
      RenderUtil.roundRect(g, x1, y1, x2, y2, r, fill);
      RenderUtil.roundOutline(g, x1, y1, x2, y2, r, RIM, 1.0F);
   }

   public static void plasticTile(class_332 g, float x1, float y1, float x2, float y2, float r, int fill) {
      RenderUtil.roundRect(g, x1, y1, x2, y2, r, fill);
      RenderUtil.roundOutline(g, x1, y1, x2, y2, r, RIM_SOFT, 1.0F);
   }

   public static void slider(class_332 g, float x, float y, float width, float height, float pct, boolean hover) {
      slider(g, x, y, width, height, pct, hover, hover ? 1.0F : 0.0F);
   }

   public static void slider(class_332 g, float x, float y, float width, float height, float pct, boolean hover, float thumbHot) {
      hoverRow(g, x, y, width, height, hover);
      int x1 = Math.round(trackLeft(x));
      int x2 = Math.round(trackRight(x, width));
      int ty = Math.round(trackTop(y, height));
      int th = Math.round(3.5F);
      RenderUtil.roundRect(g, (float)x1, (float)ty, (float)x2, (float)(ty + th), (float)th * 0.5F, GuiTheme.SLIDER_TRACK);
      int fill = Math.round((float)x1 + (float)(x2 - x1) * clamp01(pct));
      if (fill - x1 > 0) {
         RenderUtil.roundRect(g, (float)x1, (float)ty, (float)fill, (float)(ty + th), (float)th * 0.5F, accent().getRGB());
      }

      thumb(g, (float)fill, (float)ty + (float)th * 0.5F, thumbHot);
   }

   public static void range(class_332 g, float x, float y, float width, float height, float minPct, float maxPct, boolean hover) {
      range(g, x, y, width, height, minPct, maxPct, hover, hover ? 1.0F : 0.0F);
   }

   public static void range(class_332 g, float x, float y, float width, float height, float minPct, float maxPct, boolean hover, float thumbHot) {
      hoverRow(g, x, y, width, height, hover);
      rangeTrack(g, x, y, width, height, minPct, maxPct, thumbHot);
   }

   public static void rangeTrack(class_332 g, float x, float y, float width, float height, float minPct, float maxPct, float thumbHot) {
      int x1 = Math.round(trackLeft(x));
      int x2 = Math.round(trackRight(x, width));
      int ty = Math.round(trackTop(y, height));
      int th = Math.round(3.5F);
      RenderUtil.roundRect(g, (float)x1, (float)ty, (float)x2, (float)(ty + th), (float)th * 0.5F, GuiTheme.SLIDER_TRACK);
      int minX = Math.round((float)x1 + (float)(x2 - x1) * clamp01(minPct));
      int maxX = Math.round((float)x1 + (float)(x2 - x1) * clamp01(maxPct));
      if (maxX > minX) {
         RenderUtil.roundRect(g, (float)minX, (float)ty, (float)maxX, (float)(ty + th), (float)th * 0.5F, accent().getRGB());
      }

      thumb(g, (float)minX, (float)ty + (float)th * 0.5F, thumbHot);
      thumb(g, (float)maxX, (float)ty + (float)th * 0.5F, thumbHot);
   }

   public static void thumb(class_332 g, float cx, float cy) {
      thumb(g, cx, cy, 0.0F);
   }

   public static void thumb(class_332 g, float cx, float cy, float hot) {
      hot = clamp(hot, 0.0F, 1.12F);
      int r = Math.round(5.0F + 1.15F * hot);
      int icx = Math.round(cx);
      int icy = Math.round(cy);
      RenderUtil.roundRect(g, (float)(icx - r), (float)(icy - r), (float)(icx + r), (float)(icy + r), (float)r, -723208);
      if (hot > 0.04F) {
         RenderUtil.roundOutline(g, (float)(icx - r), (float)(icy - r), (float)(icx + r), (float)(icy + r), (float)r, mix(16777215, GuiTheme.withAlpha(accent(), 160).getRGB(), hot), 1.0F);
      }

   }

   public static void toggle(class_332 g, float right, float y, float height, boolean on) {
      toggle(g, right, y, height, on ? 1.0F : 0.0F);
   }

   public static void toggle(class_332 g, float right, float y, float height, float on) {
      float amount = clamp(on, -0.07F, 1.07F);
      int w = Math.round(24.0F);
      int h = Math.round(14.0F);
      int x = Math.round(right - (float)w);
      int ty = Math.round(y + (height - (float)h) * 0.5F);
      int pad = 2;
      int knob = h - pad * 2;
      int travel = Math.max(0, w - knob - pad * 2);
      int knobX = x + pad + Math.round(amount * (float)travel);
      int bg = mix(TOGGLE_OFF, accent().getRGB(), clamp01(on));
      RenderUtil.roundRect(g, (float)x, (float)ty, (float)(x + w), (float)(ty + h), (float)h * 0.5F, bg);
      RenderUtil.roundRect(g, (float)knobX, (float)(ty + pad), (float)(knobX + knob), (float)(ty + h - pad), (float)knob * 0.5F, -723208);
   }

   public static float chip(class_332 g, String text, float right, float y, float height, boolean hover) {
      int tw = Fonts.width(text);
      float pad = 7.0F;
      float ch = 15.0F;
      float cw = (float)tw + pad * 2.0F;
      float x1 = right - cw;
      float y1 = y + (height - ch) * 0.5F;
      RenderUtil.roundRect(g, x1, y1, right, y1 + ch, ch * 0.5F, hover ? CHIP_BG_HOVER : CHIP_BG);
      return x1;
   }

   public static void swatch(class_332 g, int color, float right, float y, float height) {
      float s = 12.0F;
      float x1 = right - s;
      float y1 = y + (height - s) * 0.5F;
      RenderUtil.roundRect(g, x1, y1, right, y1 + s, 4.0F, color);
      RenderUtil.roundOutline(g, x1, y1, right, y1 + s, 4.0F, GuiTheme.OUTLINE_INNER, 1.0F);
   }

   public static float approach(float current, float target, float speed) {
      if (!smooth()) {
         return target;
      } else {
         float t = 1.0F - (float)Math.exp((double)(-speed * Math.max(0.0F, frameDt)));
         return current + (target - current) * t;
      }
   }

   public static float spring(float current, float target, Motion motion, float stiffness, float damping) {
      if (!smooth()) {
         motion.vel = 0.0F;
         motion.value = target;
         return target;
      } else {
         float dt = frameDt > 0.0F ? Math.min(0.05F, frameDt) : 0.016F;
         float vel = motion.vel + (target - current) * stiffness * dt;
         vel *= (float)Math.exp((double)(-damping * dt));
         float next = current + vel * dt;
         if (Math.abs(target - next) < 8.0E-4F && Math.abs(vel) < 0.04F) {
            motion.vel = 0.0F;
            motion.value = target;
            return target;
         } else {
            motion.vel = vel;
            motion.value = next;
            return next;
         }
      }
   }

   public static int mix(int a, int b, float t) {
      t = clamp01(t);
      int ar = a >> 16 & 255;
      int ag = a >> 8 & 255;
      int ab = a & 255;
      int aa = a >> 24 & 255;
      int br = b >> 16 & 255;
      int bg = b >> 8 & 255;
      int bb = b & 255;
      int ba = b >> 24 & 255;
      return (int)((float)aa + (float)(ba - aa) * t) << 24 | (int)((float)ar + (float)(br - ar) * t) << 16 | (int)((float)ag + (float)(bg - ag) * t) << 8 | (int)((float)ab + (float)(bb - ab) * t);
   }

   private static boolean smooth() {
      ClickGuiModule gui = ClickGuiModule.getInstance();
      return gui == null || (Boolean)gui.smooth.getValue();
   }

   private static float clamp01(float v) {
      return clamp(v, 0.0F, 1.0F);
   }

   private static float clamp(float v, float min, float max) {
      return Math.max(min, Math.min(max, v));
   }

   public static final class Motion {
      public float value;
      public float vel;

      public Motion() {
      }

      public Motion(float value) {
         this.value = value;
      }

      public float to(float target, float stiffness, float damping) {
         return CenteredStyle.spring(this.value, target, this, stiffness, damping);
      }
   }
}
