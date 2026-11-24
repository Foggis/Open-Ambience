package dev.ambience.features.gui.items.buttons;

import dev.ambience.features.gui.AmbienceGui;
import dev.ambience.features.gui.CenteredStyle;
import dev.ambience.features.gui.GuiTheme;
import dev.ambience.features.gui.Widget;
import dev.ambience.features.settings.Setting;
import dev.ambience.util.render.RenderUtil;
import dev.ambience.util.render.font.Fonts;
import java.awt.Color;
import net.minecraft.class_1109;
import net.minecraft.class_332;
import net.minecraft.class_3417;
import net.minecraft.class_3532;

public class ColorButton extends SettingButton<Color> {
   private static final int SV_HEIGHT = 44;
   private static final int BAR_HEIGHT = 6;
   private static final int BTN_HEIGHT = 10;
   private static final int GAP = 2;
   private boolean open = false;
   private boolean hoveringHue;
   private boolean hoveringColor;
   private boolean hoveringAlpha;
   private boolean hoveringCopy;
   private boolean hoveringPaste;
   private boolean draggingHue;
   private boolean draggingColor;
   private boolean draggingAlpha;
   private float[] hsb;

   public ColorButton(Setting<Color> setting) {
      super(setting);
      this.height = 12;
      this.hsb = Color.RGBtoHSB(((Color)setting.getValue()).getRed(), ((Color)setting.getValue()).getGreen(), ((Color)setting.getValue()).getBlue(), (float[])null);
   }

   public int getHeight() {
      return !this.open ? this.headerH() : this.headerH() + 2 + 44 + 2 + 6 + 2 + 6 + 2 + 10 + 2;
   }

   public int getRowHeight() {
      return this.headerH();
   }

   private int headerH() {
      return CenteredStyle.row();
   }

   public void keyActivate() {
      this.open = !this.open;
      mc.method_1483().method_4873(class_1109.method_47978(class_3417.field_15015, 1.0F));
   }

   private int pickerWidth() {
      return Math.max(40, this.width - 4);
   }

   public void drawScreen(class_332 context, int mouseX, int mouseY, float partialTicks) {
      boolean hovering = this.isHoveringRow(mouseX, mouseY);
      int head = this.headerH();
      if (CenteredStyle.active()) {
         CenteredStyle.hoverRow(context, this.x, this.y, (float)this.width, (float)head, hovering);
         CenteredStyle.swatch(context, ((Color)this.setting.getValue()).getRGB(), this.x + (float)this.width - 8.0F, this.y, (float)head);
         this.drawScrollableString(this.getName(), (double)(this.x + 8.0F), (double)CenteredStyle.labelY(this.y, (float)head), -1644050, this.width - 28, hovering);
      } else {
         if (hovering) {
            RenderUtil.rect(context, this.x, this.y, this.x + (float)this.width, this.y + (float)head, GuiTheme.SLIDER_TRACK);
         }

         this.drawScrollableString(this.getName(), (double)(this.x + 2.0F), (double)(this.y + (float)(head - 8) / 2.0F), -1644050, this.width - 22, hovering);
         float sw1 = this.x + (float)this.width - 16.0F;
         float sw2 = this.x + (float)this.width - 3.0F;
         float sy1 = this.y + 2.0F;
         float sy2 = this.y + (float)head - 2.0F;
         RenderUtil.rect(context, sw1, sy1, sw2, sy2, ((Color)this.setting.getValue()).getRGB());
         RenderUtil.rect(context, sw1, sy1, sw2, sy2, GuiTheme.OUTLINE, 1.0F);
      }

      if (this.open) {
         Color currentColor = this.setting.getValue();
         Color realColor = Color.getHSBColor(this.hsb[0], 1.0F, 1.0F);
         int outlineColor = GuiTheme.OUTLINE;
         int pickerWidth = this.pickerWidth();
         float pickerX = this.x + 2.0F;
         float yOffset = (float)(this.headerH() + 2);
         int dragX = class_3532.method_15340(mouseX - (int)pickerX, 0, pickerWidth);
         int dragY = class_3532.method_15340(mouseY - (int)(this.y + yOffset), 0, 44);
         float dragHue = Math.max((float)pickerWidth * this.hsb[0] - 0.5F, 1.0F);
         float dragSaturation = Math.max((float)pickerWidth * this.hsb[1] - 1.0F, 2.0F);
         float dragBrightness = Math.max(44.0F * (1.0F - this.hsb[2]) - 1.0F, 2.0F);
         float dragAlpha = Math.max((float)pickerWidth * ((float)currentColor.getAlpha() / 255.0F) - 0.5F, 1.0F);
         RenderUtil.horizontalGradient(context, pickerX, this.y + yOffset, pickerX + (float)pickerWidth, this.y + yOffset + 44.0F, Color.WHITE, realColor);
         RenderUtil.verticalGradient(context, pickerX, this.y + yOffset, pickerX + (float)pickerWidth, this.y + yOffset + 44.0F, new Color(0, 0, 0, 0), Color.BLACK);
         RenderUtil.rect(context, pickerX, this.y + yOffset, pickerX + (float)pickerWidth, this.y + yOffset + 44.0F, outlineColor, 1.0F);
         this.hoveringColor = this.isHoveringArea(mouseX, mouseY, pickerX, this.y + yOffset, pickerX + (float)pickerWidth, this.y + yOffset + 44.0F);
         if (dragSaturation < (float)pickerWidth && dragBrightness < 44.0F) {
            RenderUtil.rect(context, pickerX + dragSaturation - 2.0F, this.y + yOffset + dragBrightness - 2.0F, pickerX + dragSaturation + 1.0F, this.y + yOffset + dragBrightness + 1.0F, -16777216);
            RenderUtil.rect(context, pickerX + dragSaturation - 1.0F, this.y + yOffset + dragBrightness - 1.0F, pickerX + dragSaturation, this.y + yOffset + dragBrightness, -1);
         }

         if (this.draggingColor) {
            this.hsb[1] = (float)dragX / (float)pickerWidth;
            this.hsb[2] = 1.0F - (float)dragY / 44.0F;
            this.setColor(this.hsb);
         }

         yOffset += 46.0F;
         RenderUtil.horizontalGradient(context, pickerX, this.y + yOffset, pickerX + (float)pickerWidth, this.y + yOffset + 6.0F, new Color(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), 0), new Color(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), 255));
         RenderUtil.rect(context, pickerX, this.y + yOffset, pickerX + (float)pickerWidth, this.y + yOffset + 6.0F, outlineColor, 1.0F);
         this.hoveringAlpha = this.isHoveringArea(mouseX, mouseY, pickerX, this.y + yOffset, pickerX + (float)pickerWidth, this.y + yOffset + 6.0F);
         RenderUtil.rect(context, pickerX + dragAlpha - 1.5F, this.y + yOffset - 1.0F, pickerX + dragAlpha + 1.5F, this.y + yOffset + 6.0F + 1.0F, -16777216);
         RenderUtil.rect(context, pickerX + dragAlpha - 0.5F, this.y + yOffset, pickerX + dragAlpha + 0.5F, this.y + yOffset + 6.0F, -1);
         if (this.draggingAlpha) {
            this.setColor(this.hsb, (int)(255.0F * (float)dragX / (float)pickerWidth));
         }

         yOffset += 8.0F;

         for(float i = 0.0F; i < (float)pickerWidth; i += 0.5F) {
            RenderUtil.rect(context, pickerX + i, this.y + yOffset, pickerX + i + 0.5F, this.y + yOffset + 6.0F, Color.getHSBColor(i / (float)pickerWidth, 1.0F, 1.0F).getRGB());
         }

         RenderUtil.rect(context, pickerX, this.y + yOffset, pickerX + (float)pickerWidth, this.y + yOffset + 6.0F, outlineColor, 1.0F);
         this.hoveringHue = this.isHoveringArea(mouseX, mouseY, pickerX, this.y + yOffset, pickerX + (float)pickerWidth, this.y + yOffset + 6.0F);
         if (dragHue < (float)pickerWidth) {
            RenderUtil.rect(context, pickerX + dragHue - 1.5F, this.y + yOffset - 1.0F, pickerX + dragHue + 1.5F, this.y + yOffset + 6.0F + 1.0F, -16777216);
            RenderUtil.rect(context, pickerX + dragHue - 0.5F, this.y + yOffset, pickerX + dragHue + 0.5F, this.y + yOffset + 6.0F, -1);
         }

         if (this.draggingHue) {
            this.hsb[0] = (float)dragX / (float)pickerWidth;
            this.setColor(this.hsb);
         }

         yOffset += 8.0F;
         int buttonWidth = (pickerWidth - 2) / 2;
         Color accent = Widget.currentAccent != null ? Widget.currentAccent : new Color(120, 175, 220);
         int activeBg = accent.getRGB();
         int idleBg = GuiTheme.SLIDER_TRACK;
         float textY = this.y + yOffset + 1.0F + 1.0F;
         RenderUtil.rect(context, pickerX, this.y + yOffset, pickerX + (float)buttonWidth, this.y + yOffset + 10.0F, this.hoveringCopy ? activeBg : idleBg);
         RenderUtil.rect(context, pickerX, this.y + yOffset, pickerX + (float)buttonWidth, this.y + yOffset + 10.0F, outlineColor, 1.0F);
         this.drawString("Copy", (double)pickerX + (double)buttonWidth / (double)2.0F - (double)Fonts.width("Copy") / (double)2.0F, (double)textY, -1644050);
         this.hoveringCopy = this.isHoveringArea(mouseX, mouseY, pickerX, this.y + yOffset, pickerX + (float)buttonWidth, this.y + yOffset + 10.0F);
         RenderUtil.rect(context, pickerX + (float)buttonWidth + 2.0F, this.y + yOffset, pickerX + (float)(buttonWidth * 2) + 2.0F, this.y + yOffset + 10.0F, this.hoveringPaste ? activeBg : idleBg);
         RenderUtil.rect(context, pickerX + (float)buttonWidth + 2.0F, this.y + yOffset, pickerX + (float)(buttonWidth * 2) + 2.0F, this.y + yOffset + 10.0F, outlineColor, 1.0F);
         this.drawString("Paste", (double)(pickerX + (float)buttonWidth + 2.0F) + (double)buttonWidth / (double)2.0F - (double)Fonts.width("Paste") / (double)2.0F, (double)textY, -1644050);
         this.hoveringPaste = this.isHoveringArea(mouseX, mouseY, pickerX + (float)buttonWidth + 2.0F, this.y + yOffset, pickerX + (float)(buttonWidth * 2) + 2.0F, this.y + yOffset + 10.0F);
      }
   }

   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (this.isHoveringRow(mouseX, mouseY) && mouseButton == 1) {
         this.open = !this.open;
         mc.method_1483().method_4873(class_1109.method_47978(class_3417.field_15015, 1.0F));
      }

      if (mouseButton == 0) {
         if (this.hoveringHue) {
            this.draggingHue = true;
         }

         if (this.hoveringColor) {
            this.draggingColor = true;
         }

         if (this.hoveringAlpha) {
            this.draggingAlpha = true;
         }

         if (this.hoveringCopy) {
            AmbienceGui.setColorClipboard(this.setting.getValue());
            mc.method_1483().method_4873(class_1109.method_47978(class_3417.field_15015, 1.0F));
         }

         if (this.hoveringPaste && AmbienceGui.getColorClipboard() != null) {
            this.setting.setValue(AmbienceGui.getColorClipboard());
            this.hsb = Color.RGBtoHSB(((Color)this.setting.getValue()).getRed(), ((Color)this.setting.getValue()).getGreen(), ((Color)this.setting.getValue()).getBlue(), (float[])null);
            mc.method_1483().method_4873(class_1109.method_47978(class_3417.field_15015, 1.0F));
         }
      }

   }

   public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
      if (releaseButton == 0) {
         this.draggingHue = false;
         this.draggingColor = false;
         this.draggingAlpha = false;
      }

   }

   public boolean isHovering(int mouseX, int mouseY) {
      return this.isHoveringRow(mouseX, mouseY);
   }

   private boolean isHoveringRow(int mouseX, int mouseY) {
      if (!CenteredStyle.active()) {
         for(Widget widget : AmbienceGui.getClickGui().getComponents()) {
            if (widget.drag) {
               return false;
            }
         }
      }

      return (float)mouseX >= this.x && (float)mouseX <= this.x + (float)this.width && (float)mouseY >= this.y && (float)mouseY < this.y + (float)this.headerH();
   }

   private boolean isHoveringArea(int mouseX, int mouseY, float left, float top, float right, float bottom) {
      if (!CenteredStyle.active()) {
         for(Widget widget : AmbienceGui.getClickGui().getComponents()) {
            if (widget.drag) {
               return false;
            }
         }
      }

      return left <= (float)mouseX && top <= (float)mouseY && right > (float)mouseX && bottom > (float)mouseY;
   }

   private void setColor(float[] hsb) {
      this.setColor(hsb, ((Color)this.setting.getValue()).getAlpha());
   }

   private void setColor(float[] hsb, int alpha) {
      Color color = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
      this.setting.setValue(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
   }
}
