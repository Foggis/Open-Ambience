package dev.ambience.features.gui.items.buttons;

import dev.ambience.features.gui.AmbienceGui;
import dev.ambience.features.gui.CenteredStyle;
import dev.ambience.features.gui.GuiTheme;
import dev.ambience.features.gui.Widget;
import dev.ambience.features.settings.Setting;
import dev.ambience.util.render.RenderUtil;
import dev.ambience.util.render.font.Fonts;
import java.awt.Color;
import net.minecraft.class_124;
import net.minecraft.class_332;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class RangeSlider extends SettingButton<Vector2f> {
   private static final float DEFAULT_TRACK_MIN = 0.0F;
   private static final float DEFAULT_TRACK_MAX = 500.0F;
   private DragTarget dragging;
   private float visualMin;
   private float visualMax;
   private float thumbHot;
   private final CenteredStyle.Motion thumbMotion;

   public RangeSlider(Setting<Vector2f> setting) {
      super(setting);
      this.dragging = RangeSlider.DragTarget.NONE;
      this.visualMin = -1.0F;
      this.visualMax = -1.0F;
      this.thumbMotion = new CenteredStyle.Motion();
      this.height = 12;
   }

   public int getHeight() {
      return CenteredStyle.sliderRow();
   }

   public void drawScreen(class_332 context, int mouseX, int mouseY, float partialTicks) {
      boolean hovering = this.isHovering(mouseX, mouseY);
      if (this.dragging != RangeSlider.DragTarget.NONE || hovering && GLFW.glfwGetMouseButton(mc.method_22683().method_4490(), 0) == 1) {
         if (this.dragging == RangeSlider.DragTarget.NONE) {
            this.pickDragTarget(mouseX);
         }

         if (this.dragging != RangeSlider.DragTarget.NONE) {
            this.applyMouseX(mouseX);
         }
      }

      Vector2f value = this.setting.getValue();
      float minPct = this.trackSpan() == 0.0F ? 0.0F : (value.x - this.trackMin()) / this.trackSpan();
      float maxPct = this.trackSpan() == 0.0F ? 1.0F : (value.y - this.trackMin()) / this.trackSpan();
      if (CenteredStyle.active()) {
         float mouse = this.mousePct(mouseX);
         float minTarget = this.dragging == RangeSlider.DragTarget.MIN ? mouse : minPct;
         float maxTarget = this.dragging == RangeSlider.DragTarget.MAX ? mouse : maxPct;
         if (this.dragging == RangeSlider.DragTarget.MIN && minTarget > this.visualMax && this.visualMax >= 0.0F) {
            minTarget = this.visualMax;
         }

         if (this.dragging == RangeSlider.DragTarget.MAX && maxTarget < this.visualMin && this.visualMin >= 0.0F) {
            maxTarget = this.visualMin;
         }

         if (this.visualMin < 0.0F) {
            this.visualMin = minPct;
            this.visualMax = maxPct;
         }

         this.visualMin = CenteredStyle.approach(this.visualMin, minTarget, this.dragging != RangeSlider.DragTarget.NONE ? 28.0F : 16.0F);
         this.visualMax = CenteredStyle.approach(this.visualMax, maxTarget, this.dragging != RangeSlider.DragTarget.NONE ? 28.0F : 16.0F);
         this.thumbMotion.value = this.thumbHot;
         this.thumbHot = this.thumbMotion.to(!hovering && this.dragging == RangeSlider.DragTarget.NONE ? 0.0F : 1.0F, 230.0F, 17.0F);
         CenteredStyle.range(context, this.x, this.y, (float)this.width, (float)this.getHeight(), this.visualMin, this.visualMax, hovering, this.thumbHot);
      } else {
         Color accent = Widget.currentAccent != null ? Widget.currentAccent : new Color(120, 175, 220);
         float trackX1 = this.x;
         float trackX2 = this.x + (float)this.width;
         float minX = this.valueToX(value.x, trackX1, trackX2);
         float maxX = this.valueToX(value.y, trackX1, trackX2);
         if (maxX > minX) {
            RenderUtil.rect(context, minX, this.y, maxX, this.y + (float)this.height, accent.getRGB());
         }

         if (hovering) {
            RenderUtil.rect(context, trackX1, this.y, trackX2, this.y + (float)this.height, GuiTheme.HIGHLIGHT_TOP);
         }
      }

      float labelY = CenteredStyle.active() ? CenteredStyle.sliderLabelY(this.y) : CenteredStyle.labelY(this.y, (float)this.getHeight());
      String var10000 = String.valueOf(class_124.field_1080);
      String rangeText = var10000 + this.formatRange(value.x, value.y);
      int valWidth = Fonts.width(rangeText);
      float valueRight = this.x + (float)this.width - 3.0F;
      this.drawString(rangeText, (double)(valueRight - (float)valWidth), (double)labelY, -3880238);
      int labelMax = Math.max(0, (int)(valueRight - (float)valWidth - (this.x + 8.0F) - 4.0F));
      this.drawScrollableString(this.getName(), (double)(this.x + (CenteredStyle.active() ? 8.0F : 2.0F)), (double)labelY, -1644050, labelMax, hovering);
   }

   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
         this.pickDragTarget(mouseX);
         this.applyMouseX(mouseX);
      }

   }

   public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
      if (releaseButton == 0) {
         this.dragging = RangeSlider.DragTarget.NONE;
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

   private void pickDragTarget(int mouseX) {
      Vector2f value = this.setting.getValue();
      float trackX1 = this.trackX1();
      float trackX2 = this.trackX2();
      float minX = this.valueToX(value.x, trackX1, trackX2);
      float maxX = this.valueToX(value.y, trackX1, trackX2);
      this.dragging = Math.abs((float)mouseX - minX) <= Math.abs((float)mouseX - maxX) ? RangeSlider.DragTarget.MIN : RangeSlider.DragTarget.MAX;
   }

   private void applyMouseX(int mouseX) {
      float pct = this.mousePct(mouseX);
      int snapped = Math.round(this.trackMin() + this.trackSpan() * pct);
      Vector2f current = this.setting.getValue();
      float min = current.x;
      float max = current.y;
      if (this.dragging == RangeSlider.DragTarget.MIN) {
         min = (float)snapped;
         if (min > max) {
            max = min;
         }
      } else if (this.dragging == RangeSlider.DragTarget.MAX) {
         max = (float)snapped;
         if (max < min) {
            min = max;
         }
      }

      this.setting.setValue(new Vector2f(min, max));
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
      return Math.max(0.0F, Math.min(1.0F, ((float)mouseX - trackX1) / (trackX2 - trackX1)));
   }

   private float valueToX(float value, float trackX1, float trackX2) {
      float span = this.trackSpan();
      float pct = span == 0.0F ? 0.0F : (value - this.trackMin()) / span;
      return trackX1 + (trackX2 - trackX1) * pct;
   }

   private float trackMin() {
      return this.setting.hasVec2TrackBounds() ? this.setting.getVec2TrackMin() : 0.0F;
   }

   private float trackMax() {
      return this.setting.hasVec2TrackBounds() ? this.setting.getVec2TrackMax() : 500.0F;
   }

   private float trackSpan() {
      return this.trackMax() - this.trackMin();
   }

   private String formatRange(float min, float max) {
      int minMs = Math.round(min);
      int maxMs = Math.round(max);
      return minMs == maxMs ? minMs + "ms" : minMs + "-" + maxMs + "ms";
   }

   private static enum DragTarget {
      NONE,
      MIN,
      MAX;

      // $FF: synthetic method
      private static DragTarget[] $values() {
         return new DragTarget[]{NONE, MIN, MAX};
      }
   }
}
