package dev.ambience.features.gui.items.buttons;

import dev.ambience.features.gui.CenteredStyle;
import dev.ambience.features.gui.GuiTheme;
import dev.ambience.features.settings.Bind;
import dev.ambience.features.settings.Setting;
import dev.ambience.util.render.RenderUtil;
import dev.ambience.util.render.font.Fonts;
import net.minecraft.class_1109;
import net.minecraft.class_124;
import net.minecraft.class_332;
import net.minecraft.class_3417;
import org.lwjgl.glfw.GLFW;

public class BindButton extends SettingButton<Bind> {
   private static final int MOUSE_BUTTONS = 16;
   public boolean isListening;
   private final boolean[] ignoreMouse = new boolean[16];
   private static BindButton listening;

   public BindButton(Setting<Bind> setting) {
      super(setting);
      this.height = 12;
   }

   public static boolean handleMouse(int button, int action) {
      BindButton current = listening;
      if (current != null && current.isListening) {
         if (button >= 0 && button < current.ignoreMouse.length) {
            if (action == 0) {
               current.ignoreMouse[button] = false;
               return false;
            }

            if (action == 1 && current.ignoreMouse[button]) {
               return false;
            }
         }

         if (action == 1 && button >= 0) {
            current.bindMouse(button);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public int getHeight() {
      return CenteredStyle.row();
   }

   public void drawScreen(class_332 context, int mouseX, int mouseY, float partialTicks) {
      if (this.isListening) {
         this.pollMouse();
      }

      boolean hovering = this.isHovering(mouseX, mouseY);
      float ty = CenteredStyle.labelY(this.y, (float)this.getHeight());
      if (CenteredStyle.active()) {
         CenteredStyle.hoverRow(context, this.x, this.y, (float)this.width, (float)this.getHeight(), hovering || this.isListening);
         if (this.isListening) {
            String wait = "Press a key";
            float chipLeft = CenteredStyle.chip(context, wait, this.x + (float)this.width - 8.0F, this.y, (float)this.getHeight(), true);
            int w = Fonts.width(wait);
            this.drawString(wait, (double)(chipLeft + (this.x + (float)this.width - 8.0F - chipLeft - (float)w) / 2.0F), (double)(this.y + (float)(this.getHeight() - Fonts.lineHeight()) / 2.0F), -1644050);
            this.drawScrollableString(this.setting.getName(), (double)(this.x + 8.0F), (double)ty, -1644050, Math.max(0, (int)(chipLeft - this.x - 12.0F)), hovering);
         } else {
            String str = this.bindLabel();
            float chipLeft = CenteredStyle.chip(context, str, this.x + (float)this.width - 8.0F, this.y, (float)this.getHeight(), hovering);
            int w = Fonts.width(str);
            this.drawString(str, (double)(chipLeft + (this.x + (float)this.width - 8.0F - chipLeft - (float)w) / 2.0F), (double)(this.y + (float)(this.getHeight() - Fonts.lineHeight()) / 2.0F), -1644050);
            this.drawScrollableString(this.setting.getName(), (double)(this.x + 8.0F), (double)ty, -1644050, Math.max(0, (int)(chipLeft - this.x - 12.0F)), hovering);
         }

      } else {
         if (hovering || this.isListening) {
            RenderUtil.rect(context, this.x, this.y, this.x + (float)this.width, this.y + (float)this.height, GuiTheme.SLIDER_TRACK);
         }

         if (this.isListening) {
            this.drawString("Press a key...", (double)(this.x + 2.0F), (double)ty, -1644050);
         } else {
            String var10000 = String.valueOf(class_124.field_1080);
            String str = var10000 + this.bindLabel();
            int w = Fonts.width(str);
            this.drawString(str, (double)(this.x + (float)this.width - (float)w - 3.0F), (double)ty, -3880238);
            int labelMax = Math.max(0, this.width - w - 8);
            this.drawScrollableString(this.setting.getName(), (double)(this.x + 2.0F), (double)ty, -1644050, labelMax, hovering);
         }

      }
   }

   private String bindLabel() {
      return ((Bind)this.setting.getValue()).toString().toUpperCase().replace("KEY.KEYBOARD", "").replace(".", " ").trim();
   }

   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (this.isListening) {
         if (handleMouse(mouseButton, 1)) {
            mc.method_1483().method_4873(class_1109.method_47978(class_3417.field_15015, 1.0F));
         }

      } else {
         super.mouseClicked(mouseX, mouseY, mouseButton);
         if (this.isHovering(mouseX, mouseY)) {
            mc.method_1483().method_4873(class_1109.method_47978(class_3417.field_15015, 1.0F));
         }

      }
   }

   public boolean isHovering(int mouseX, int mouseY) {
      return (float)mouseX >= this.getX() && (float)mouseX <= this.getX() + (float)this.getWidth() && (float)mouseY >= this.getY() && (float)mouseY < this.getY() + (float)this.getHeight();
   }

   public void onKeyPressed(int key) {
      if (this.isListening) {
         Bind bind = new Bind(key);
         if (key == 261 || key == 259 || key == 256) {
            bind = new Bind(-1);
         }

         this.setting.setValue(bind);
         this.onMouseClick();
      }

   }

   public void toggle() {
      this.isListening = !this.isListening;
      if (this.isListening) {
         this.snapshotHeldMouse();
         listening = this;
      } else if (listening == this) {
         listening = null;
      }

   }

   public boolean getState() {
      return !this.isListening;
   }

   private void bindMouse(int button) {
      this.setting.setValue(Bind.fromMouseButton(button));
      if (this.isListening) {
         this.onMouseClick();
      }

   }

   private void snapshotHeldMouse() {
      long window = mc.method_22683().method_4490();

      for(int button = 0; button < this.ignoreMouse.length; ++button) {
         this.ignoreMouse[button] = GLFW.glfwGetMouseButton(window, button) == 1;
      }

   }

   private void pollMouse() {
      long window = mc.method_22683().method_4490();

      for(int button = 0; button < this.ignoreMouse.length; ++button) {
         boolean down = GLFW.glfwGetMouseButton(window, button) == 1;
         if (!down) {
            this.ignoreMouse[button] = false;
         } else if (!this.ignoreMouse[button]) {
            this.bindMouse(button);
            return;
         }
      }

   }
}
