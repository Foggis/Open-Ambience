package dev.ambience.features.gui.items.buttons;

import dev.ambience.features.gui.AmbienceGui;
import dev.ambience.features.gui.CenteredStyle;
import dev.ambience.features.gui.GuiTheme;
import dev.ambience.features.gui.Widget;
import dev.ambience.features.gui.items.TextBox;
import dev.ambience.features.settings.Setting;
import dev.ambience.util.render.RenderUtil;
import dev.ambience.util.render.font.Fonts;
import java.awt.Color;
import net.minecraft.class_124;
import net.minecraft.class_332;

public class Slider extends SettingButton<Number> {
   private final Number min;
   private final Number max;
   private final float difference;
   private final TextBox textBox;
   private float visualPct = -1.0F;
   private float thumbHot;
   private final CenteredStyle.Motion thumbMotion = new CenteredStyle.Motion();
   private boolean grabbing;

   public Slider(Setting<Number> setting) {
      super(setting);
      this.min = setting.getMin();
      this.max = setting.getMax();
      this.difference = this.max.floatValue() - this.min.floatValue();
      this.height = 12;
      this.textBox = (new TextBox()).filter(this::acceptNumericChar).maxLength(16).onCommit(this::commitInput);
   }

   private boolean acceptNumericChar(int c) {
      if (Character.isDigit(c)) {
         return true;
      } else {
         String cur = this.textBox.getText();
         if (c == 46) {
            return !cur.contains(".");
         } else {
            return c == 45 ? cur.isEmpty() : false;
         }
      }
   }

   public int getHeight() {
      return CenteredStyle.sliderRow();
   }

   public void drawScreen(class_332 context, int mouseX, int mouseY, float partialTicks) {
      boolean hovering = this.isHovering(mouseX, mouseY);
      if (this.grabbing && !this.textBox.isFocused()) {
         this.setSettingFromX(mouseX);
      }

      if (CenteredStyle.active()) {
         this.drawModern(context, mouseX, hovering);
      } else {
         Color accent = Widget.currentAccent != null ? Widget.currentAccent : new Color(120, 175, 220);
         float trackX1 = this.x;
         float trackX2 = this.x + (float)this.width;
         float pct = this.partialMultiplier();
         float fillX = trackX1 + (trackX2 - trackX1) * pct;
         RenderUtil.rect(context, trackX1, this.y, fillX, this.y + (float)this.height, accent.getRGB());
         if (hovering) {
            RenderUtil.rect(context, this.x, this.y, this.x + (float)this.width, this.y + (float)this.height, GuiTheme.HIGHLIGHT_TOP);
         }

         this.drawLabelValue(hovering);
      }
   }

   private void drawModern(class_332 context, int mouseX, boolean hovering) {
      float target = this.grabbing ? this.mousePct(mouseX) : this.partialMultiplier();
      if (this.visualPct < 0.0F) {
         this.visualPct = target;
      }

      this.visualPct = CenteredStyle.approach(this.visualPct, target, this.grabbing ? 28.0F : 16.0F);
      this.thumbMotion.value = this.thumbHot;
      this.thumbHot = this.thumbMotion.to(!hovering && !this.grabbing ? 0.0F : 1.0F, 230.0F, 17.0F);
      CenteredStyle.slider(context, this.x, this.y, (float)this.width, (float)this.getHeight(), this.visualPct, hovering, this.thumbHot);
      this.drawLabelValue(hovering);
   }

   private void drawLabelValue(boolean hovering) {
      float labelY = CenteredStyle.active() ? CenteredStyle.sliderLabelY(this.y) : CenteredStyle.labelY(this.y, (float)this.getHeight());
      float centerY = CenteredStyle.active() ? CenteredStyle.sliderLabelY(this.y) + 4.0F : this.y + (float)this.getHeight() / 2.0F;
      float valueRight = this.x + (float)this.width - (CenteredStyle.active() ? 8.0F : 3.0F);
      int valueColor = -3880238;
      float valueLeft;
      if (this.textBox.isFocused()) {
         valueLeft = this.textBox.renderInlineRight(context, valueRight, centerY, -1);
      } else {
         String var10000 = String.valueOf(class_124.field_1080);
         String value = var10000 + this.formatCurrentValue();
         int valWidth = Fonts.width(value);
         this.drawString(value, (double)(valueRight - (float)valWidth), (double)labelY, valueColor);
         valueLeft = valueRight - (float)valWidth;
      }

      int labelMax = Math.max(0, (int)(valueLeft - (this.x + 8.0F) - 4.0F));
      this.drawScrollableString(this.getName(), (double)(this.x + (CenteredStyle.active() ? 8.0F : 2.0F)), (double)labelY, -1644050, labelMax, hovering);
   }

   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (this.textBox.isFocused()) {
         if (!this.isHovering(mouseX, mouseY)) {
            this.textBox.unfocus();
         }

      } else if (mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
         this.textBox.setText(this.formatCurrentValue());
         this.textBox.focus();
      } else {
         super.mouseClicked(mouseX, mouseY, mouseButton);
         if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.grabbing = true;
            this.setSettingFromX(mouseX);
         }

      }
   }

   public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
      if (releaseButton == 0) {
         this.grabbing = false;
      }

   }

   public void keyActivate() {
      this.textBox.setText(this.formatCurrentValue());
      this.textBox.focus();
   }

   private void commitInput() {
      String input = this.textBox.getText();
      if (!input.isEmpty() && !input.equals("-")) {
         try {
            if (this.setting.getValue() instanceof Double) {
               double val = Double.parseDouble(input);
               this.setting.setValue(Math.max(this.min.doubleValue(), Math.min(this.max.doubleValue(), val)));
            } else if (this.setting.getValue() instanceof Float) {
               float val = Float.parseFloat(input);
               this.setting.setValue(Math.max(this.min.floatValue(), Math.min(this.max.floatValue(), val)));
            } else if (this.setting.getValue() instanceof Integer) {
               int val = Integer.parseInt(input);
               this.setting.setValue(Math.max(this.min.intValue(), Math.min(this.max.intValue(), val)));
            }
         } catch (NumberFormatException var4) {
         }

      }
   }

   private String formatCurrentValue() {
      if (this.setting.getValue() instanceof Integer) {
         return String.valueOf(((Number)this.setting.getValue()).intValue());
      } else {
         return this.setting.getValue() instanceof Float ? ((Number)this.setting.getValue()).toString() : String.valueOf(((Number)this.setting.getValue()).doubleValue());
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

   private void setSettingFromX(int mouseX) {
      float pct = this.mousePct(mouseX);
      if (this.setting.getValue() instanceof Double) {
         double result = this.min.doubleValue() + (double)(this.difference * pct);
         this.setting.setValue((double)Math.round((double)10.0F * result) / (double)10.0F);
      } else if (this.setting.getValue() instanceof Float) {
         float result = this.min.floatValue() + this.difference * pct;
         this.setting.setValue((float)Math.round(10.0F * result) / 10.0F);
      } else if (this.setting.getValue() instanceof Integer) {
         this.setting.setValue(this.min.intValue() + Math.round(this.difference * pct));
      }

   }

   private float mousePct(int mouseX) {
      float trackX1 = CenteredStyle.active() ? CenteredStyle.trackLeft(this.x) : this.x;
      float trackX2 = CenteredStyle.active() ? CenteredStyle.trackRight(this.x, (float)this.width) : this.x + (float)this.width;
      return Math.max(0.0F, Math.min(1.0F, ((float)mouseX - trackX1) / (trackX2 - trackX1)));
   }

   private float partialMultiplier() {
      return this.difference == 0.0F ? 0.0F : (((Number)this.setting.getValue()).floatValue() - this.min.floatValue()) / this.difference;
   }
}
