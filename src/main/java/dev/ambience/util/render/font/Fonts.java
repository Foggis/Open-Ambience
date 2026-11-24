package dev.ambience.util.render.font;

import dev.ambience.hooks.GuiRuntime;
import java.awt.Font;
import java.io.InputStream;
import java.util.Objects;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_5481;

public final class Fonts {
   private static final class_310 mc = class_310.method_1551();
   private static final String BUNDLED_PATH = "/assets/ambience/font/default.ttf";
   private static final float SHADOW_OFFSET = 1.0F;
   private static final float BODY_HEIGHT = 7.75F;
   private static final float BODY_ASCENT = 6.6F;
   private static final float TITLE_HEIGHT = 9.75F;
   private static final float TITLE_ASCENT = 8.25F;
   private static final float HEADER_HEIGHT = 13.25F;
   private static final float HEADER_ASCENT = 11.15F;
   private static volatile FontRenderer renderer;
   private static volatile FontRenderer title;
   private static volatile FontRenderer header;
   private static volatile boolean dirty = true;
   private static volatile boolean hudPass = false;
   private static Font bundledBase;
   private static boolean bundledLoaded;

   private Fonts() {
   }

   private static boolean clickGuiOn() {
      return mc != null && GuiRuntime.isClickGui(mc.field_1755) ? true : GuiRuntime.clickGuiFont();
   }

   private static boolean hudOn() {
      return GuiRuntime.hudFont();
   }

   private static boolean anyOn() {
      return clickGuiOn() || hudOn();
   }

   public static void markDirty() {
      dirty = true;
   }

   public static void beginHudPass() {
      hudPass = true;
   }

   public static void endHudPass() {
      hudPass = false;
   }

   public static void drawString(class_332 graphics, String text, float x, float y, int color) {
      FontRenderer fr = clickGuiOn() ? renderer() : null;
      if (fr == null) {
         graphics.method_25303(mc.field_1772, text, (int)x, (int)y, color);
      } else {
         if (useShadow()) {
            fr.drawString(graphics, text, x + 1.0F, y + 1.0F, color, true);
         }

         fr.drawString(graphics, text, x, y, color, false);
      }
   }

   public static void drawModuleTitle(class_332 graphics, String text, float x, float y, int color) {
      FontRenderer fr = titleRenderer();
      if (fr == null) {
         drawString(graphics, text, x, y, color);
      } else {
         fr.drawString(graphics, text, x, y, color, false);
      }
   }

   public static void drawHeader(class_332 graphics, String text, float x, float y, int color) {
      FontRenderer fr = headerRenderer();
      if (fr == null) {
         drawString(graphics, text, x, y, color);
      } else {
         fr.drawString(graphics, text, x, y, color, false);
      }
   }

   public static int width(String text) {
      FontRenderer fr = clickGuiOn() ? renderer() : null;
      return fr == null ? mc.field_1772.method_1727(text) : Math.round(fr.getTextWidth(text));
   }

   public static int lineHeight() {
      FontRenderer fr = clickGuiOn() ? renderer() : null;
      int var10000;
      if (fr == null) {
         Objects.requireNonNull(mc.field_1772);
         var10000 = 9;
      } else {
         var10000 = Math.max(1, Math.round(fr.getHeight()));
      }

      return var10000;
   }

   public static float visualHeight() {
      FontRenderer fr = clickGuiOn() ? renderer() : null;
      return fr == null ? (float)lineHeight() : fr.visualHeight();
   }

   public static float alignY(float visualTop) {
      FontRenderer fr = clickGuiOn() ? renderer() : null;
      return fr == null ? visualTop : fr.yForVisualTop(visualTop);
   }

   public static int titleHeight() {
      FontRenderer fr = titleRenderer();
      return fr == null ? lineHeight() : Math.max(1, Math.round(fr.getHeight()));
   }

   public static int headerHeight() {
      FontRenderer fr = headerRenderer();
      return fr == null ? lineHeight() : Math.max(1, Math.round(fr.getHeight()));
   }

   public static int titleWidth(String text) {
      FontRenderer fr = titleRenderer();
      return fr == null ? width(text) : Math.round(fr.getTextWidth(text));
   }

   public static boolean drawOverrideActive() {
      return hudPass && hudOn() && renderer() != null;
   }

   public static void renderOverrideString(class_332 graphics, String text, float x, float y, int color, boolean shadow) {
      FontRenderer fr = renderer();
      if (fr != null) {
         if (shadow) {
            fr.drawString(graphics, text, x + 1.0F, y + 1.0F, color, true);
         }

         fr.drawString(graphics, text, x, y, color, false);
      }
   }

   public static void renderOverrideText(class_332 graphics, class_5481 text, float x, float y, int color, boolean shadow) {
      FontRenderer fr = renderer();
      if (fr != null) {
         if (shadow) {
            fr.drawText(graphics, text, x + 1.0F, y + 1.0F, color, true);
         }

         fr.drawText(graphics, text, x, y, color, false);
      }
   }

   public static int overrideWidth(String text) {
      FontRenderer fr = renderer();
      return fr == null ? mc.field_1772.method_1727(text) : Math.round(fr.getTextWidth(text));
   }

   public static int overrideWidth(class_5481 text) {
      FontRenderer fr = renderer();
      return fr == null ? mc.field_1772.method_30880(text) : Math.round(fr.getTextWidth(text));
   }

   private static boolean useShadow() {
      return mc == null || !GuiRuntime.isClickGui(mc.field_1755);
   }

   private static FontRenderer renderer() {
      if (!anyOn()) {
         disposeIfPresent();
         return null;
      } else if (mc.method_22683() == null) {
         return null;
      } else {
         if (dirty || renderer == null) {
            rebuild();
         }

         return renderer;
      }
   }

   private static FontRenderer titleRenderer() {
      if (!clickGuiOn()) {
         return renderer();
      } else if (mc.method_22683() == null) {
         return null;
      } else {
         if (dirty || title == null) {
            rebuild();
         }

         return title;
      }
   }

   private static FontRenderer headerRenderer() {
      if (!clickGuiOn()) {
         return renderer();
      } else if (mc.method_22683() == null) {
         return null;
      } else {
         if (dirty || header == null) {
            rebuild();
         }

         return header;
      }
   }

   private static synchronized void rebuild() {
      if (dirty || renderer == null || title == null || header == null) {
         disposeIfPresent();
         Font base = resolveBaseFont(GuiRuntime.fontName());
         renderer = new FontRenderer(new Font[]{base}, 7.75F, 6.6F);
         title = new FontRenderer(new Font[]{base}, 9.75F, 8.25F);
         header = new FontRenderer(new Font[]{base}, 13.25F, 11.15F);
         dirty = false;
      }
   }

   private static synchronized void disposeIfPresent() {
      FontRenderer old = renderer;
      FontRenderer oldTitle = title;
      FontRenderer oldHeader = header;
      renderer = null;
      title = null;
      header = null;
      if (old != null) {
         old.close();
      }

      if (oldTitle != null) {
         oldTitle.close();
      }

      if (oldHeader != null) {
         oldHeader.close();
      }

   }

   private static Font resolveBaseFont(String familyName) {
      if (familyName != null && !familyName.isBlank()) {
         return new Font(familyName.trim(), 0, 1);
      } else {
         Font bundled = bundled();
         return bundled != null ? bundled : new Font("SansSerif", 0, 1);
      }
   }

   private static Font bundled() {
      if (bundledLoaded) {
         return bundledBase;
      } else {
         bundledLoaded = true;

         try {
            InputStream is = Fonts.class.getResourceAsStream("/assets/ambience/font/default.ttf");

            try {
               if (is != null) {
                  bundledBase = Font.createFont(0, is).deriveFont(1.0F);
               }
            } catch (Throwable var4) {
               if (is != null) {
                  try {
                     is.close();
                  } catch (Throwable var3) {
                     var4.addSuppressed(var3);
                  }
               }

               throw var4;
            }

            if (is != null) {
               is.close();
            }
         } catch (Exception var5) {
            bundledBase = null;
         }

         return bundledBase;
      }
   }
}
