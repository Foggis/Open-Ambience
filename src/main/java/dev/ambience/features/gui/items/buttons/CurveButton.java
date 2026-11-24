package dev.ambience.features.gui.items.buttons;

import dev.ambience.features.gui.AmbienceGui;
import dev.ambience.features.gui.CenteredStyle;
import dev.ambience.features.gui.GuiTheme;
import dev.ambience.features.gui.Widget;
import dev.ambience.features.settings.BezierCurve;
import dev.ambience.features.settings.Setting;
import dev.ambience.util.render.RenderUtil;
import dev.ambience.util.render.font.Fonts;
import java.awt.Color;
import net.minecraft.class_124;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import org.lwjgl.glfw.GLFW;

public class CurveButton extends SettingButton<BezierCurve> {
   private static final int GRAPH_HEIGHT = 72;
   private static final float GRAPH_PAD = 8.0F;
   private static final float PLOT_INSET = 7.0F;
   private static final float HANDLE_SIZE = 4.2F;
   private static final float HANDLE_HIT = 8.0F;
   private Drag dragging;
   private float visualMin;
   private float visualMax;
   private float thumbHot;
   private final CenteredStyle.Motion thumbMotion;

   public CurveButton(Setting<BezierCurve> setting) {
      super(setting);
      this.dragging = CurveButton.Drag.NONE;
      this.visualMin = -1.0F;
      this.visualMax = -1.0F;
      this.thumbMotion = new CenteredStyle.Motion();
      this.height = CenteredStyle.sliderRow() + 72;
   }

   public int getHeight() {
      return CenteredStyle.sliderRow() + 72;
   }

   public int getRowHeight() {
      return CenteredStyle.sliderRow();
   }

   public void drawScreen(class_332 context, int mouseX, int mouseY, float partialTicks) {
      if (this.dragging != CurveButton.Drag.NONE || GLFW.glfwGetMouseButton(mc.method_22683().method_4490(), 0) == 1 && this.isHovering(mouseX, mouseY)) {
         if (this.dragging == CurveButton.Drag.NONE) {
            this.pickDrag(mouseX, mouseY);
         }

         if (this.dragging != CurveButton.Drag.NONE) {
            this.applyMouse(mouseX, mouseY);
         }
      }

      Color accent = Widget.currentAccent != null ? Widget.currentAccent : new Color(120, 175, 220);
      BezierCurve curve = this.setting.getValue();
      int head = this.headerH();
      boolean hoveringRow = this.isHoveringRow(mouseX, mouseY);
      float span = 400.0F;
      float minPct = span == 0.0F ? 0.0F : (curve.minMs - 0.0F) / span;
      float maxPct = span == 0.0F ? 1.0F : (curve.maxMs - 0.0F) / span;
      if (CenteredStyle.active()) {
         float mouse = this.mousePct(mouseX);
         float minTarget = this.dragging == CurveButton.Drag.MIN ? mouse : minPct;
         float maxTarget = this.dragging == CurveButton.Drag.MAX ? mouse : maxPct;
         if (this.dragging == CurveButton.Drag.MIN && this.visualMax >= 0.0F && minTarget > this.visualMax) {
            minTarget = this.visualMax;
         }

         if (this.dragging == CurveButton.Drag.MAX && this.visualMin >= 0.0F && maxTarget < this.visualMin) {
            maxTarget = this.visualMin;
         }

         if (this.visualMin < 0.0F) {
            this.visualMin = minPct;
            this.visualMax = maxPct;
         }

         boolean trackDrag = this.dragging == CurveButton.Drag.MIN || this.dragging == CurveButton.Drag.MAX;
         this.visualMin = CenteredStyle.approach(this.visualMin, minTarget, trackDrag ? 28.0F : 16.0F);
         this.visualMax = CenteredStyle.approach(this.visualMax, maxTarget, trackDrag ? 28.0F : 16.0F);
         this.thumbMotion.value = this.thumbHot;
         this.thumbHot = this.thumbMotion.to(!hoveringRow && !trackDrag ? 0.0F : 1.0F, 230.0F, 17.0F);
         CenteredStyle.hoverRow(context, this.x, this.y, (float)this.width, (float)this.getHeight(), hoveringRow);
         CenteredStyle.rangeTrack(context, this.x, this.y, (float)this.width, (float)head, this.visualMin, this.visualMax, this.thumbHot);
      } else {
         float trackX1 = this.x;
         float trackX2 = this.x + (float)this.width;
         float minX = valueToX(curve.minMs, trackX1, trackX2);
         float maxX = valueToX(curve.maxMs, trackX1, trackX2);
         if (maxX > minX) {
            RenderUtil.rect(context, minX, this.y, maxX, this.y + (float)head, accent.getRGB());
         }

         if (hoveringRow) {
            RenderUtil.rect(context, trackX1, this.y, trackX2, this.y + (float)head, GuiTheme.HIGHLIGHT_TOP);
         }
      }

      float labelY = CenteredStyle.active() ? CenteredStyle.sliderLabelY(this.y) : CenteredStyle.labelY(this.y, (float)head);
      String var10000 = String.valueOf(class_124.field_1080);
      String rangeText = var10000 + formatRange(curve.minMs, curve.maxMs);
      int valWidth = Fonts.width(rangeText);
      float valueRight = this.x + (float)this.width - (CenteredStyle.active() ? 8.0F : 3.0F);
      this.drawString(rangeText, (double)(valueRight - (float)valWidth), (double)labelY, -3880238);
      int labelMax = Math.max(0, (int)(valueRight - (float)valWidth - (this.x + 8.0F) - 4.0F));
      this.drawScrollableString(this.getName(), (double)(this.x + (CenteredStyle.active() ? 8.0F : 2.0F)), (double)labelY, -1644050, labelMax, hoveringRow);
      this.drawGraph(context, curve, accent, mouseX, mouseY);
   }

   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
         this.pickDrag(mouseX, mouseY);
         this.applyMouse(mouseX, mouseY);
      }

   }

   public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
      if (releaseButton == 0) {
         this.dragging = CurveButton.Drag.NONE;
      }

   }

   public boolean isHovering(int mouseX, int mouseY) {
      if (!CenteredStyle.active()) {
         for(Widget widget : AmbienceGui.getClickGui().getComponents()) {
            if (widget.drag) {
               return false;
            }
         }
      }

      return (float)mouseX >= this.getX() && (float)mouseX <= this.getX() + (float)this.getWidth() && (float)mouseY >= this.getY() && (float)mouseY < this.getY() + (float)this.getHeight();
   }

   private int headerH() {
      return CenteredStyle.sliderRow();
   }

   private boolean isHoveringRow(int mouseX, int mouseY) {
      return (float)mouseX >= this.getX() && (float)mouseX <= this.getX() + (float)this.getWidth() && (float)mouseY >= this.getY() && (float)mouseY < this.getY() + (float)this.headerH();
   }

   private void drawGraph(class_332 context, BezierCurve curve, Color accent, int mouseX, int mouseY) {
      float[] box = this.plotBox();
      float gx1 = box[0];
      float gy1 = box[1];
      float gx2 = box[2];
      float gy2 = box[3];
      float px1 = box[4];
      float py1 = box[5];
      float px2 = box[6];
      float py2 = box[7];
      if (CenteredStyle.active()) {
         RenderUtil.roundRect(context, gx1, gy1, gx2, gy2, 6.0F, CenteredStyle.FIELD_BG);
         RenderUtil.roundOutline(context, gx1, gy1, gx2, gy2, 6.0F, GuiTheme.OUTLINE_INNER, 1.0F);
      } else {
         RenderUtil.rect(context, gx1, gy1, gx2, gy2, GuiTheme.withAlpha(accent, 28).getRGB());
         RenderUtil.rect(context, gx1, gy1, gx2, gy2, GuiTheme.OUTLINE_INNER, 1.0F);
      }

      int grid = (new Color(255, 255, 255, 18)).getRGB();

      for(int i = 1; i < 4; ++i) {
         float x = px1 + (px2 - px1) * ((float)i / 4.0F);
         float y = py1 + (py2 - py1) * ((float)i / 4.0F);
         RenderUtil.rect(context, x, py1, x + 1.0F, py2, grid);
         RenderUtil.rect(context, px1, y, px2, y + 1.0F, grid);
      }

      RenderUtil.line(context, px1, py2, px2, py1, (new Color(255, 255, 255, 22)).getRGB(), 1.0F);
      int curveColor = GuiTheme.withAlpha(GuiTheme.brighten(accent, 0.25F), 240).getRGB();
      float prevX = px1;
      float prevY = plotY(py1, py2, 0.0F);
      int steps = Math.max(32, Math.round((px2 - px1) * 1.5F));

      for(int i = 1; i <= steps; ++i) {
         float t = (float)i / (float)steps;
         float x = px1 + (px2 - px1) * t;
         float y = plotY(py1, py2, curve.ease(t));
         RenderUtil.line(context, prevX, prevY, x, y, curveColor, 1.8F);
         prevX = x;
         prevY = y;
      }

      float p1x = plotX(px1, px2, curve.x1);
      float p1y = plotY(py1, py2, curve.y1);
      float p2x = plotX(px1, px2, curve.x2);
      float p2y = plotY(py1, py2, curve.y2);
      int stem = (new Color(255, 255, 255, 40)).getRGB();
      RenderUtil.line(context, px1, py2, p1x, p1y, stem, 1.1F);
      RenderUtil.line(context, px2, py1, p2x, p2y, stem, 1.1F);
      this.drawHandle(context, p1x, p1y, accent, this.dragging == CurveButton.Drag.P1 || near(mouseX, mouseY, p1x, p1y));
      this.drawHandle(context, p2x, p2y, accent, this.dragging == CurveButton.Drag.P2 || near(mouseX, mouseY, p2x, p2y));
   }

   private void drawHandle(class_332 context, float cx, float cy, Color accent, boolean active) {
      float s = active ? 5.0F : 4.2F;
      RenderUtil.roundRect(context, cx - s, cy - s + 1.1F, cx + s, cy + s + 1.1F, s, 1711276032);
      RenderUtil.roundRect(context, cx - s, cy - s, cx + s, cy + s, s, active ? GuiTheme.brighten(accent, 0.35F).getRGB() : -723208);
   }

   private float[] plotBox() {
      float gx1 = this.x + 8.0F;
      float gy1 = this.y + (float)this.headerH() + 2.0F;
      float gx2 = this.x + (float)this.width - 8.0F;
      float gy2 = this.y + (float)this.getHeight() - 8.0F;
      return new float[]{gx1, gy1, gx2, gy2, gx1 + 7.0F, gy1 + 7.0F, gx2 - 7.0F, gy2 - 7.0F};
   }

   private void pickDrag(int mouseX, int mouseY) {
      if ((float)mouseY < this.y + (float)this.headerH()) {
         BezierCurve curve = this.setting.getValue();
         float trackX1 = this.trackX1();
         float trackX2 = this.trackX2();
         float minX = valueToX(curve.minMs, trackX1, trackX2);
         float maxX = valueToX(curve.maxMs, trackX1, trackX2);
         this.dragging = Math.abs((float)mouseX - minX) <= Math.abs((float)mouseX - maxX) ? CurveButton.Drag.MIN : CurveButton.Drag.MAX;
      } else {
         float[] box = this.plotBox();
         BezierCurve curve = this.setting.getValue();
         float p1x = plotX(box[4], box[6], curve.x1);
         float p1y = plotY(box[5], box[7], curve.y1);
         float p2x = plotX(box[4], box[6], curve.x2);
         float p2y = plotY(box[5], box[7], curve.y2);
         float d1 = dist((float)mouseX, (float)mouseY, p1x, p1y);
         float d2 = dist((float)mouseX, (float)mouseY, p2x, p2y);
         if (!(d1 <= 8.0F) && !(d2 <= 8.0F)) {
            this.dragging = CurveButton.Drag.NONE;
         } else {
            this.dragging = d1 <= d2 ? CurveButton.Drag.P1 : CurveButton.Drag.P2;
         }

      }
   }

   private void applyMouse(int mouseX, int mouseY) {
      BezierCurve next = ((BezierCurve)this.setting.getValue()).copy();
      if (this.dragging != CurveButton.Drag.MIN && this.dragging != CurveButton.Drag.MAX) {
         float[] box = this.plotBox();
         float nx = class_3532.method_15363(((float)mouseX - box[4]) / (box[6] - box[4]), 0.0F, 1.0F);
         float ny = class_3532.method_15363(1.0F - ((float)mouseY - box[5]) / (box[7] - box[5]), 0.0F, 1.0F);
         if (this.dragging == CurveButton.Drag.P1) {
            next.x1 = nx;
            next.y1 = ny;
         } else if (this.dragging == CurveButton.Drag.P2) {
            next.x2 = nx;
            next.y2 = ny;
         }

         this.setting.setValue(next.clamped());
      } else {
         float pct = this.mousePct(mouseX);
         int snapped = Math.round(0.0F + 400.0F * pct);
         if (this.dragging == CurveButton.Drag.MIN) {
            next.minMs = (float)snapped;
            if (next.minMs > next.maxMs) {
               next.maxMs = next.minMs;
            }
         } else {
            next.maxMs = (float)snapped;
            if (next.maxMs < next.minMs) {
               next.minMs = next.maxMs;
            }
         }

         this.setting.setValue(next.clamped());
      }
   }

   private float trackX1() {
      return CenteredStyle.active() ? CenteredStyle.trackLeft(this.x) : this.x;
   }

   private float trackX2() {
      return CenteredStyle.active() ? CenteredStyle.trackRight(this.x, (float)this.width) : this.x + (float)this.width;
   }

   private float mousePct(int mouseX) {
      float trackX1 = this.trackX1();
      float trackX2 = this.trackX2();
      return class_3532.method_15363(((float)mouseX - trackX1) / (trackX2 - trackX1), 0.0F, 1.0F);
   }

   private static float plotX(float gx1, float gx2, float t) {
      return gx1 + (gx2 - gx1) * t;
   }

   private static float plotY(float gy1, float gy2, float t) {
      return gy2 - (gy2 - gy1) * t;
   }

   private static float valueToX(float value, float trackX1, float trackX2) {
      float span = 400.0F;
      float pct = span == 0.0F ? 0.0F : (value - 0.0F) / span;
      return trackX1 + (trackX2 - trackX1) * pct;
   }

   private static String formatRange(float min, float max) {
      int minMs = Math.round(min);
      int maxMs = Math.round(max);
      return minMs == maxMs ? minMs + "ms" : minMs + "-" + maxMs + "ms";
   }

   private static boolean near(int mouseX, int mouseY, float x, float y) {
      return dist((float)mouseX, (float)mouseY, x, y) <= 8.0F;
   }

   private static float dist(float ax, float ay, float bx, float by) {
      float dx = ax - bx;
      float dy = ay - by;
      return (float)Math.sqrt((double)(dx * dx + dy * dy));
   }

   private static enum Drag {
      NONE,
      MIN,
      MAX,
      P1,
      P2;

      // $FF: synthetic method
      private static Drag[] $values() {
         return new Drag[]{NONE, MIN, MAX, P1, P2};
      }
   }
}
