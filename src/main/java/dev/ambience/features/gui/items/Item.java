package dev.ambience.features.gui.items;

import dev.ambience.features.Feature;
import dev.ambience.util.render.GuiFade;
import dev.ambience.util.render.font.Fonts;
import java.awt.Color;
import net.minecraft.class_332;

public class Item extends Feature {
   public static class_332 context;
   protected float x;
   protected float y;
   protected int width;
   protected int height;
   private boolean hidden;
   private long scrollStartTime = 0L;

   public Item(String name) {
      super(name);
   }

   public void setLocation(float x, float y) {
      this.x = x;
      this.y = y;
   }

   public void drawScreen(class_332 context, int mouseX, int mouseY, float partialTicks) {
   }

   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
   }

   public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
   }

   public void update() {
   }

   public void onKeyTyped(String typedChar, int keyCode) {
   }

   public void onKeyPressed(int key) {
   }

   public void keyActivate() {
   }

   public int getRowHeight() {
      return this.getHeight();
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public int getWidth() {
      return this.width;
   }

   public void setWidth(int width) {
      this.width = width;
   }

   public int getHeight() {
      return this.height;
   }

   public void setHeight(int height) {
      this.height = height;
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public boolean setHidden(boolean hidden) {
      this.hidden = hidden;
      return this.hidden;
   }

   protected void drawScrollableString(String text, double x, double y, int color, int maxWidth, boolean isHovering) {
      int textWidth = Fonts.width(text);
      if (textWidth <= maxWidth) {
         this.scrollStartTime = 0L;
         this.drawString(text, x, y, color);
      } else if (!isHovering) {
         this.scrollStartTime = 0L;
         this.drawString(this.truncateText(text, maxWidth), x, y, color);
      } else {
         if (this.scrollStartTime == 0L) {
            this.scrollStartTime = System.currentTimeMillis();
         }

         long elapsed = System.currentTimeMillis() - this.scrollStartTime;
         float overflow = (float)(textWidth - maxWidth);
         long scrollDurationMs = (long)(overflow / 0.03F);
         long pause = 600L;
         long cycle = pause + scrollDurationMs + pause + scrollDurationMs;
         long t = elapsed % cycle;
         float offset;
         if (t < pause) {
            offset = 0.0F;
         } else if (t < pause + scrollDurationMs) {
            offset = (float)(t - pause) * 0.03F;
         } else if (t < pause + scrollDurationMs + pause) {
            offset = overflow;
         } else {
            offset = overflow - (float)(t - pause - scrollDurationMs - pause) * 0.03F;
         }

         context.method_44379((int)x, (int)y - 2, (int)(x + (double)maxWidth + (double)2.0F), (int)(y + (double)Fonts.lineHeight() + (double)2.0F));
         this.drawString(text, x - (double)offset, y, color);
         context.method_44380();
      }
   }

   private String truncateText(String text, int maxWidth) {
      String ellipsis = "...";
      int ellipsisWidth = Fonts.width(ellipsis);
      if (maxWidth <= ellipsisWidth) {
         return ellipsis;
      } else {
         int targetWidth = maxWidth - ellipsisWidth;
         StringBuilder sb = new StringBuilder();

         for(char c : text.toCharArray()) {
            String var10000 = sb.toString();
            if (Fonts.width(var10000 + c) > targetWidth) {
               break;
            }

            sb.append(c);
         }

         String var11 = String.valueOf(sb);
         return var11 + ellipsis;
      }
   }

   protected void drawString(String text, double x, double y, Color color) {
      this.drawString(text, x, y, color.hashCode());
   }

   protected void drawString(String text, double x, double y, int color) {
      Fonts.drawString(context, text, (float)x, (float)y, GuiFade.apply(color));
   }

   public boolean isHovering(int mouseX, int mouseY) {
      return false;
   }

   public String getPage() {
      return "General";
   }
}
