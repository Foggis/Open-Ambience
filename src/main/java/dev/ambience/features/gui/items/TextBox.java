package dev.ambience.features.gui.items;

import dev.ambience.features.gui.CenteredStyle;
import dev.ambience.features.gui.GuiTheme;
import dev.ambience.util.render.GuiFade;
import dev.ambience.util.render.RenderUtil;
import dev.ambience.util.render.font.Fonts;
import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import net.minecraft.class_1109;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_3417;

public class TextBox {
   private static final class_310 mc = class_310.method_1551();
   private static TextBox ACTIVE;
   public static final IntPredicate ANY_PRINTABLE = (c) -> c >= 32;
   private final StringBuilder buffer = new StringBuilder();
   private String placeholder = "";
   private IntPredicate filter;
   private int maxLength;
   private Consumer<String> onChange;
   private Runnable onCommit;
   private Runnable onCancel;

   public TextBox() {
      this.filter = ANY_PRINTABLE;
      this.maxLength = 256;
   }

   public TextBox placeholder(String s) {
      this.placeholder = s == null ? "" : s;
      return this;
   }

   public TextBox filter(IntPredicate f) {
      this.filter = f == null ? ANY_PRINTABLE : f;
      return this;
   }

   public TextBox maxLength(int n) {
      this.maxLength = Math.max(0, n);
      return this;
   }

   public TextBox onChange(Consumer<String> c) {
      this.onChange = c;
      return this;
   }

   public TextBox onCommit(Runnable r) {
      this.onCommit = r;
      return this;
   }

   public TextBox onCancel(Runnable r) {
      this.onCancel = r;
      return this;
   }

   public String getText() {
      return this.buffer.toString();
   }

   public boolean isEmpty() {
      return this.buffer.length() == 0;
   }

   public void setText(String s) {
      this.buffer.setLength(0);
      if (s != null) {
         this.buffer.append(s);
      }

      this.fireChange();
   }

   public void clear() {
      this.buffer.setLength(0);
      this.unfocus();
      this.fireChange();
   }

   public boolean isFocused() {
      return ACTIVE == this;
   }

   public static boolean hasActiveFocus() {
      return ACTIVE != null;
   }

   public static TextBox getActive() {
      return ACTIVE;
   }

   public static void unfocusAll() {
      ACTIVE = null;
   }

   public static void routeKeyPressed(int key) {
      if (ACTIVE != null) {
         ACTIVE.keyPressed(key);
      }

   }

   public static void routeKeyTyped(String typed) {
      if (ACTIVE != null) {
         ACTIVE.keyTyped(typed);
      }

   }

   public void focus() {
      if (ACTIVE != this) {
         if (ACTIVE != null) {
            ACTIVE.unfocus();
         }

         ACTIVE = this;
         mc.method_1483().method_4873(class_1109.method_47978(class_3417.field_15015, 1.0F));
      }
   }

   public void unfocus() {
      if (ACTIVE == this) {
         ACTIVE = null;
      }

   }

   private void fireChange() {
      if (this.onChange != null) {
         this.onChange.accept(this.buffer.toString());
      }

   }

   public void renderPill(class_332 ctx, float x1, float y1, float x2, float y2, Color accent, boolean hovering, float leftPad) {
      float r = Math.max(1.0F, (y2 - y1) * 0.5F);
      boolean active = this.isFocused() || this.buffer.length() > 0;
      int field = CenteredStyle.active() ? CenteredStyle.FIELD_BG : GuiTheme.BODY_BG;
      if (active) {
         int wash = GuiTheme.withAlpha(accent, 70).getRGB();
         RenderUtil.roundRect(ctx, x1, y1, x2, y2, r, field);
         RenderUtil.roundRect(ctx, x1, y1, x2, y2, r, wash);
         int border = CenteredStyle.active() ? GuiTheme.withAlpha(GuiTheme.brighten(accent, 0.35F), 160).getRGB() : GuiTheme.brighten(accent, 0.25F).getRGB();
         RenderUtil.roundOutline(ctx, x1, y1, x2, y2, r, border, 1.0F);
      } else {
         RenderUtil.roundRect(ctx, x1, y1, x2, y2, r, field);
         if (hovering) {
            RenderUtil.roundRect(ctx, x1, y1, x2, y2, r, GuiTheme.withAlpha(accent, 40).getRGB());
         }

         RenderUtil.roundOutline(ctx, x1, y1, x2, y2, r, CenteredStyle.active() ? CenteredStyle.RIM_SOFT : GuiTheme.OUTLINE_INNER, 1.0F);
      }

      float textX = x1 + leftPad;
      float textY = y1 + (y2 - y1 - (float)Fonts.lineHeight()) / 2.0F;
      int textColor = this.isFocused() ? -1 : -1644050;
      if (this.buffer.length() == 0 && !this.isFocused()) {
         this.drawString(ctx, this.placeholder, textX, textY, -3880238);
      } else {
         String shown = this.buffer.toString();
         this.drawString(ctx, shown, textX, textY, textColor);
         this.drawCaret(ctx, textX + (float)Fonts.width(shown), textY, textColor);
      }

   }

   public void renderAt(class_332 ctx, float x1, float y1, float x2, float y2) {
      float textY = y1 + (y2 - y1 - (float)Fonts.lineHeight()) / 2.0F;
      int textColor = -1;
      if (this.buffer.length() == 0) {
         this.drawString(ctx, this.placeholder, x1, textY, -3880238);
      } else {
         this.drawString(ctx, this.buffer.toString(), x1, textY, textColor);
         this.drawCaret(ctx, x1 + (float)Fonts.width(this.buffer.toString()), textY, textColor);
      }

   }

   public float renderInlineRight(class_332 ctx, float rightX, float centerY, int color) {
      String shown = this.buffer.toString();
      int w = Fonts.width(shown);
      float textX = rightX - (float)w;
      float textY = centerY - 4.0F;
      this.drawString(ctx, shown, textX, textY, color);
      this.drawCaret(ctx, rightX, textY, color);
      return textX;
   }

   private void drawCaret(class_332 ctx, float cx, float textY, int color) {
      if (this.isFocused()) {
         boolean blink = System.currentTimeMillis() / 500L % 2L == 0L;
         if (blink) {
            RenderUtil.rect(ctx, cx, textY - 1.0F, cx + 1.0F, textY + (float)Fonts.lineHeight(), color);
         }
      }
   }

   public void keyPressed(int key) {
      if (this.isFocused()) {
         switch (key) {
            case 256:
               if (this.onCancel != null) {
                  this.onCancel.run();
               }

               this.unfocus();
               break;
            case 257:
            case 335:
               if (this.onCommit != null) {
                  this.onCommit.run();
               }

               this.unfocus();
               break;
            case 259:
               if (this.buffer.length() > 0) {
                  this.buffer.deleteCharAt(this.buffer.length() - 1);
                  this.fireChange();
               }
         }

      }
   }

   public void keyTyped(String typed) {
      if (this.isFocused() && typed != null && !typed.isEmpty()) {
         boolean changed = false;

         for(int i = 0; i < typed.length(); ++i) {
            char c = typed.charAt(i);
            if (this.filter.test(c)) {
               if (this.buffer.length() >= this.maxLength) {
                  break;
               }

               this.buffer.append(c);
               changed = true;
            }
         }

         if (changed) {
            this.fireChange();
         }

      }
   }

   private void drawString(class_332 ctx, String text, float x, float y, int color) {
      Fonts.drawString(ctx, text, x, y, GuiFade.apply(color));
   }
}
