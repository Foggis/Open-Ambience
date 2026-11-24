package dev.ambience.features.gui.items.buttons;

import dev.ambience.features.gui.CenteredStyle;
import dev.ambience.features.gui.GuiTheme;
import dev.ambience.features.settings.Setting;
import dev.ambience.util.render.RenderUtil;
import dev.ambience.util.render.font.Fonts;
import net.minecraft.class_1109;
import net.minecraft.class_124;
import net.minecraft.class_332;
import net.minecraft.class_3417;

public class EnumButton extends SettingButton<Enum<?>> {
   public EnumButton(Setting<Enum<?>> setting) {
      super(setting);
      this.height = 12;
   }

   public int getHeight() {
      return CenteredStyle.row();
   }

   public void drawScreen(class_332 context, int mouseX, int mouseY, float partialTicks) {
      boolean hovering = this.isHovering(mouseX, mouseY);
      float ty = CenteredStyle.labelY(this.y, (float)this.getHeight());
      String raw = this.setting.currentEnumName();
      if (CenteredStyle.active()) {
         CenteredStyle.hoverRow(context, this.x, this.y, (float)this.width, (float)this.getHeight(), hovering);
         float chipLeft = CenteredStyle.chip(context, raw, this.x + (float)this.width - 8.0F, this.y, (float)this.getHeight(), hovering);
         int w = Fonts.width(raw);
         this.drawString(raw, (double)(chipLeft + (this.x + (float)this.width - 8.0F - chipLeft - (float)w) / 2.0F), (double)(this.y + (float)(this.getHeight() - Fonts.lineHeight()) / 2.0F), -1644050);
         int labelMax = Math.max(0, (int)(chipLeft - this.x - 12.0F));
         this.drawScrollableString(this.setting.getName(), (double)(this.x + 8.0F), (double)ty, -1644050, labelMax, hovering);
      } else {
         if (hovering) {
            RenderUtil.rect(context, this.x, this.y, this.x + (float)this.width, this.y + (float)this.height, GuiTheme.SLIDER_TRACK);
         }

         String var10000 = String.valueOf(class_124.field_1080);
         String val = var10000 + raw;
         int w = Fonts.width(val);
         this.drawString(val, (double)(this.x + (float)this.width - (float)w - 3.0F), (double)ty, -3880238);
         int labelMax = Math.max(0, this.width - w - 8);
         this.drawScrollableString(this.setting.getName(), (double)(this.x + 2.0F), (double)ty, -1644050, labelMax, hovering);
      }
   }

   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      if (this.isHovering(mouseX, mouseY)) {
         mc.method_1483().method_4873(class_1109.method_47978(class_3417.field_15015, 1.0F));
      }

   }

   public boolean isHovering(int mouseX, int mouseY) {
      return (float)mouseX >= this.getX() && (float)mouseX <= this.getX() + (float)this.getWidth() && (float)mouseY >= this.getY() && (float)mouseY < this.getY() + (float)this.getHeight();
   }

   public void toggle() {
      this.setting.increaseEnum();
   }

   public boolean getState() {
      return true;
   }
}
