package dev.ambience.features.gui.items;

import dev.ambience.features.gui.CenteredStyle;
import dev.ambience.features.gui.GuiTheme;
import dev.ambience.features.gui.Widget;
import dev.ambience.util.render.GuiIcons;
import java.awt.Color;
import net.minecraft.class_332;

public class SearchBar extends Item {
   public static String QUERY = "";
   private final TextBox textBox = (new TextBox()).placeholder("Search...").onChange((s) -> QUERY = s);

   public SearchBar() {
      super("Search");
      this.height = 13;
   }

   public static boolean isFocused() {
      return TextBox.hasActiveFocus();
   }

   public static boolean hasQuery() {
      return QUERY != null && !QUERY.isEmpty();
   }

   public static boolean matches(String name) {
      return !hasQuery() ? true : name.toLowerCase().contains(QUERY.toLowerCase());
   }

   public static void unfocus() {
      TextBox.unfocusAll();
   }

   public int getHeight() {
      return CenteredStyle.active() ? 15 : 13;
   }

   public void drawScreen(class_332 context, int mouseX, int mouseY, float partialTicks) {
      boolean centered = CenteredStyle.active();
      this.height = this.getHeight();
      boolean hovering = this.isHovering(mouseX, mouseY);
      Color accent = Widget.currentAccent != null ? Widget.currentAccent : new Color(70, 75, 82);
      float x1 = this.x;
      float y1 = this.y;
      float x2 = this.x + (float)this.width;
      float y2 = centered ? this.y + (float)this.height : this.y + (float)this.height - 1.0F;
      float leftPad = centered ? 18.0F : 14.0F;
      this.textBox.renderPill(context, x1, y1, x2, y2, accent, hovering, leftPad);
      float icon = centered ? 9.0F : 9.0F;
      float iconX = this.x + (centered ? 5.0F : 4.0F);
      float iconY = this.y + ((float)this.height - icon) / 2.0F;
      int iconColor = !this.textBox.isFocused() && !hasQuery() ? -3880238 : GuiTheme.brighten(accent, 0.55F).getRGB();
      GuiIcons.draw(context, "search", iconX, iconY, icon, iconColor);
   }

   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (mouseButton == 0) {
         if (this.isHovering(mouseX, mouseY)) {
            this.textBox.focus();
         } else {
            this.textBox.unfocus();
         }

      }
   }

   public void keyActivate() {
      this.textBox.focus();
   }

   public void clear() {
      this.textBox.setText("");
      this.textBox.unfocus();
   }

   public boolean isHovering(int mouseX, int mouseY) {
      return (float)mouseX >= this.x && (float)mouseX <= this.x + (float)this.width && (float)mouseY >= this.y && (float)mouseY < this.y + (float)this.getHeight();
   }
}
