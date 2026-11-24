package dev.ambience.features.gui.items.buttons;

import dev.ambience.features.gui.CenteredStyle;
import dev.ambience.features.gui.Widget;
import dev.ambience.features.gui.items.TextBox;
import dev.ambience.features.settings.Setting;
import dev.ambience.util.render.font.Fonts;
import java.awt.Color;
import net.minecraft.class_124;
import net.minecraft.class_332;

public class StringButton extends SettingButton<String> {
   private final TextBox textBox;

   public StringButton(Setting<String> setting) {
      super(setting);
      this.height = 12;
      this.textBox = (new TextBox()).placeholder(setting.getName()).onCommit(this::commit).onCancel(() -> this.textBox().setText(setting.getValue()));
   }

   private TextBox textBox() {
      return this.textBox;
   }

   public int getHeight() {
      return CenteredStyle.row();
   }

   public void drawScreen(class_332 context, int mouseX, int mouseY, float partialTicks) {
      boolean hovering = this.isHovering(mouseX, mouseY);
      Color accent = Widget.currentAccent != null ? Widget.currentAccent : new Color(70, 75, 82);
      int rowH = this.getHeight();
      if (CenteredStyle.active() && !this.textBox.isFocused()) {
         CenteredStyle.hoverRow(context, this.x, this.y, (float)this.width, (float)rowH, hovering);
         String raw = this.setting.getValue();
         String shown = raw.isEmpty() ? "..." : raw;
         float chipLeft = CenteredStyle.chip(context, shown, this.x + (float)this.width - 8.0F, this.y, (float)rowH, hovering);
         this.drawString(shown, (double)(chipLeft + 6.0F), (double)(this.y + (float)(rowH - Fonts.lineHeight()) / 2.0F), -3880238);
         this.drawScrollableString(this.setting.getName(), (double)(this.x + 8.0F), (double)CenteredStyle.labelY(this.y, (float)rowH), -1644050, Math.max(0, (int)(chipLeft - this.x - 12.0F)), hovering);
      } else {
         float x1 = this.x;
         float y1 = this.y;
         float x2 = this.x + (float)this.width;
         float y2 = this.y + (float)rowH - 1.0F;
         if (this.textBox.isFocused()) {
            this.textBox.renderPill(context, x1, y1, x2, y2, accent, hovering, 3.0F);
         } else {
            float ty = this.y + (float)(this.height - 8) / 2.0F;
            String var10000 = String.valueOf(class_124.field_1080);
            String val = var10000 + (String)this.setting.getValue();
            int w = Fonts.width(val);
            if (hovering) {
               this.textBox.renderPill(context, x1, y1, x2, y2, accent, true, 3.0F);
            }

            this.drawString(val, (double)(this.x + (float)this.width - (float)w - 3.0F), (double)ty, -3880238);
            int labelMax = Math.max(0, this.width - w - 8);
            this.drawScrollableString(this.setting.getName(), (double)(this.x + 2.0F), (double)ty, -1644050, labelMax, hovering);
         }

      }
   }

   public boolean isHovering(int mouseX, int mouseY) {
      return (float)mouseX >= this.getX() && (float)mouseX <= this.getX() + (float)this.getWidth() && (float)mouseY >= this.getY() && (float)mouseY < this.getY() + (float)this.getHeight();
   }

   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
         if (!this.textBox.isFocused()) {
            this.textBox.setText(this.setting.getValue());
            this.textBox.focus();
         }
      } else if (!this.isHovering(mouseX, mouseY) && this.textBox.isFocused()) {
         this.textBox.unfocus();
      }

   }

   public void keyActivate() {
      this.textBox.setText(this.setting.getValue());
      this.textBox.focus();
   }

   private void commit() {
      String s = this.textBox.getText();
      if (s.isEmpty()) {
         this.setting.setValue(this.setting.getDefaultValue());
      } else {
         this.setting.setValue(s);
      }

   }
}
