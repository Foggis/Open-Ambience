package dev.ambience.features.gui.items.buttons;

import dev.ambience.features.gui.CenteredStyle;
import dev.ambience.features.gui.GuiTheme;
import dev.ambience.features.gui.Widget;
import dev.ambience.features.settings.Setting;
import dev.ambience.util.render.RenderUtil;
import java.awt.Color;
import net.minecraft.class_1109;
import net.minecraft.class_332;
import net.minecraft.class_3417;

public class BooleanButton extends SettingButton<Boolean> {
   private final CenteredStyle.Motion onMotion = new CenteredStyle.Motion();
   private float onAmt = -1.0F;

   public BooleanButton(Setting<Boolean> setting) {
      super(setting);
      this.height = 12;
   }

   public int getHeight() {
      return CenteredStyle.row();
   }

   public void drawScreen(class_332 context, int mouseX, int mouseY, float partialTicks) {
      boolean hovering = this.isHovering(mouseX, mouseY);
      if (CenteredStyle.active()) {
         if (this.onAmt < 0.0F) {
            this.onAmt = this.getState() ? 1.0F : 0.0F;
            this.onMotion.value = this.onAmt;
         }

         this.onMotion.value = this.onAmt;
         this.onAmt = this.onMotion.to(this.getState() ? 1.0F : 0.0F, 240.0F, 16.0F);
         CenteredStyle.hoverRow(context, this.x, this.y, (float)this.width, (float)this.getHeight(), hovering);
         CenteredStyle.toggle(context, this.x + (float)this.width - 8.0F, this.y, (float)this.getHeight(), this.onAmt);
         this.drawScrollableString(this.getName(), (double)(this.x + 8.0F), (double)CenteredStyle.labelY(this.y, (float)this.getHeight()), -1644050, this.width - 38, hovering);
      } else {
         Color accent = Widget.currentAccent != null ? Widget.currentAccent : new Color(120, 175, 220);
         if (this.getState()) {
            RenderUtil.rect(context, this.x, this.y, this.x + (float)this.width, this.y + (float)this.height, accent.getRGB());
            if (hovering) {
               RenderUtil.rect(context, this.x, this.y, this.x + (float)this.width, this.y + (float)this.height, GuiTheme.HIGHLIGHT_TOP);
            }
         } else if (hovering) {
            RenderUtil.rect(context, this.x, this.y, this.x + (float)this.width, this.y + (float)this.height, GuiTheme.withAlpha(accent, 60).getRGB());
         }

         int textColor = this.getState() ? -1 : -1644050;
         this.drawScrollableString(this.getName(), (double)(this.x + 2.0F), (double)(this.y + (float)(this.height - 8) / 2.0F), textColor, this.width - 4, hovering);
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
      this.setting.setValue(!(Boolean)this.setting.getValue());
   }

   public boolean getState() {
      return (Boolean)this.setting.getValue();
   }
}
