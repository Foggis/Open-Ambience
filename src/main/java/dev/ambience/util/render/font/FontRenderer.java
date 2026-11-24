package dev.ambience.util.render.font;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.minecraft.class_10799;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_5250;
import net.minecraft.class_5481;
import org.joml.Matrix3x2fStack;

public class FontRenderer implements Closeable {
   private static final class_310 mc = class_310.method_1551();
   private static final float DEFAULT_TEXT_HEIGHT = 8.5F;
   private static final float DEFAULT_ASCENT = 7.25F;
   private final Map<class_2960, ObjectList<DrawEntry>> glyphPages;
   private final ObjectList<GlyphMap> maps;
   private final ReentrantReadWriteLock lock;
   private final int charsPerPage;
   private final int padding;
   private final float textHeight;
   private final float targetAscent;
   private Font[] baseFonts;
   private Font[] fittedFonts;
   private int multiplier;
   private int previousGameScale;
   private int blockPx;
   private boolean initialized;

   public FontRenderer(Font[] fonts) {
      this(fonts, 8.5F, 7.25F);
   }

   public FontRenderer(Font[] fonts, float textHeight, float targetAscent) {
      this(fonts, 256, 4, textHeight, targetAscent);
   }

   public FontRenderer(Font[] fonts, int charactersPerPage, int paddingBetweenCharacters, float textHeight, float targetAscent) {
      this.glyphPages = new HashMap();
      this.maps = new ObjectArrayList();
      this.lock = new ReentrantReadWriteLock();
      this.previousGameScale = -1;
      if (fonts.length == 0) {
         throw new IllegalArgumentException("fonts.length == 0");
      } else if (charactersPerPage <= 4) {
         throw new IllegalArgumentException("charactersPerPage too small");
      } else if (paddingBetweenCharacters < 0) {
         throw new IllegalArgumentException("padding < 0");
      } else {
         this.baseFonts = fonts;
         this.charsPerPage = charactersPerPage;
         this.padding = paddingBetweenCharacters;
         this.textHeight = textHeight;
         this.targetAscent = targetAscent;
         this.init();
      }
   }

   public void drawString(class_332 graphics, String text, float x, float y, int color, boolean dropShadow) {
      this.drawText(graphics, parse(text, color).method_30937(), x, y, color, dropShadow);
   }

   public void drawText(class_332 graphics, class_5481 text, float x, float y, int color, boolean dropShadow) {
      this.rebuildIfScaleChanged();
      if ((color & -67108864) == 0) {
         color |= -16777216;
      }

      int alpha = color >>> 24 & 255;
      int baseRgb = dropShadow ? shadow(color) : color & 16777215;
      float[] pen = new float[]{0.0F};
      synchronized(this.glyphPages) {
         text.accept((index, style, codePoint) -> {
            int rgb;
            if (style.method_10973() != null) {
               int c = style.method_10973().method_27716();
               rgb = dropShadow ? shadow(c) : c & 16777215;
            } else {
               rgb = baseRgb;
            }

            int argb = alpha << 24 | rgb;

            for(char ch : Character.toChars(codePoint)) {
               GlyphMap.Glyph glyph = this.locateGlyph(ch);
               if (glyph != null) {
                  if (glyph.width() > 0 && glyph.height() > 0 && ch != ' ') {
                     class_2960 pageId = glyph.parent().getTextureId();
                     float bleed = 2.0F;
                     ((ObjectList)this.glyphPages.computeIfAbsent(pageId, (k) -> new ObjectArrayList())).add(new DrawEntry(pen[0] - bleed, -bleed, argb, glyph));
                  }

                  pen[0] += glyph.advance();
               }
            }

            return true;
         });
         float blockGui = (float)this.blockPx / (float)this.multiplier;
         float yTop = y + (this.textHeight - blockGui) / 2.0F;
         Matrix3x2fStack pose = graphics.method_51448();
         pose.pushMatrix();
         pose.translate((float)Math.round(x), (float)Math.round(yTop));
         float scale = 1.0F / (float)this.multiplier;
         pose.scale(scale, scale);

         for(Map.Entry<class_2960, ObjectList<DrawEntry>> entry : this.glyphPages.entrySet()) {
            class_2960 pageId = (class_2960)entry.getKey();

            for(DrawEntry de : entry.getValue()) {
               GlyphMap.Glyph g = de.glyph;
               GlyphMap parent = g.parent();
               graphics.method_25293(class_10799.field_56883, pageId, Math.round(de.dx), Math.round(de.dy), (float)g.u(), (float)g.v(), g.width(), g.height(), g.width(), g.height(), parent.getWidth(), parent.getHeight(), de.argb);
            }
         }

         pose.popMatrix();
         this.glyphPages.clear();
      }
   }

   public static String stripControlCodes(String text) {
      if (text.indexOf(167) < 0) {
         return text;
      } else {
         StringBuilder sb = new StringBuilder(text.length());

         for(int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            if (c == 167 && i + 1 < text.length()) {
               ++i;
            } else {
               sb.append(c);
            }
         }

         return sb.toString();
      }
   }

   public float getTextWidth(String text) {
      this.rebuildIfScaleChanged();
      String stripped = stripControlCodes(text);
      float adv = 0.0F;

      for(int i = 0; i < stripped.length(); ++i) {
         GlyphMap.Glyph g = this.locateGlyph(stripped.charAt(i));
         if (g != null) {
            adv += g.advance();
         }
      }

      return adv / (float)this.multiplier;
   }

   public float getTextWidth(class_5481 text) {
      this.rebuildIfScaleChanged();
      float[] adv = new float[]{0.0F};
      text.accept((index, style, codePoint) -> {
         for(char ch : Character.toChars(codePoint)) {
            GlyphMap.Glyph g = this.locateGlyph(ch);
            if (g != null) {
               adv[0] += g.advance();
            }
         }

         return true;
      });
      return adv[0] / (float)this.multiplier;
   }

   public float getHeight() {
      return this.textHeight;
   }

   public float visualHeight() {
      this.rebuildIfScaleChanged();
      return (float)this.blockPx / (float)this.multiplier;
   }

   public float yForVisualTop(float visualTop) {
      return visualTop - (this.textHeight - this.visualHeight()) * 0.5F;
   }

   private void rebuildIfScaleChanged() {
      int current = Math.round((float)mc.method_22683().method_4495());
      if (current != this.previousGameScale) {
         this.close();
         this.init();
      }

   }

   private void init() {
      if (this.initialized) {
         throw new IllegalStateException("Double call to init()");
      } else {
         this.lock.writeLock().lock();

         try {
            int scale = Math.round((float)mc.method_22683().method_4495());
            if (scale < 1) {
               scale = 1;
            }

            this.previousGameScale = scale;
            this.multiplier = scale;
            BufferedImage probe = new BufferedImage(1, 1, 2);
            Graphics2D pg = probe.createGraphics();
            float probeSize = 16.0F * (float)this.multiplier;
            pg.setFont(this.baseFonts[0].deriveFont(probeSize));
            FontMetrics probeFm = pg.getFontMetrics();
            float ascentRatio = (float)probeFm.getAscent() / probeSize;
            float fitSize = (float)Math.round(this.targetAscent * (float)this.multiplier / ascentRatio);
            Font[] fitted = new Font[this.baseFonts.length];

            for(int i = 0; i < this.baseFonts.length; ++i) {
               fitted[i] = this.baseFonts[i].deriveFont(fitSize);
            }

            pg.setFont(fitted[0]);
            FontMetrics fm = pg.getFontMetrics();
            this.blockPx = fm.getAscent() + fm.getDescent();
            pg.dispose();
            this.fittedFonts = fitted;
            this.initialized = true;
         } finally {
            this.lock.writeLock().unlock();
         }

      }
   }

   private GlyphMap.Glyph locateGlyph(char ch) {
      this.lock.readLock().lock();

      try {
         ObjectListIterator var2 = this.maps.iterator();

         while(var2.hasNext()) {
            GlyphMap m = (GlyphMap)var2.next();
            if (m.contains(ch)) {
               GlyphMap.Glyph var4 = m.getGlyph(ch);
               return var4;
            }
         }
      } finally {
         this.lock.readLock().unlock();
      }

      int base = this.charsPerPage * (ch / this.charsPerPage);
      int top = Math.min(base + this.charsPerPage, 65536);
      GlyphMap var15 = new GlyphMap(this.fittedFonts, (char)base, (char)top, this.padding);
      this.lock.writeLock().lock();

      try {
         var15.generate();
         this.maps.add(var15);
      } finally {
         this.lock.writeLock().unlock();
      }

      return var15.getGlyph(ch);
   }

   public void close() {
      this.lock.writeLock().lock();

      try {
         ObjectListIterator var1 = this.maps.iterator();

         while(var1.hasNext()) {
            GlyphMap m = (GlyphMap)var1.next();
            m.destroy();
         }

         this.maps.clear();
         this.initialized = false;
      } finally {
         this.lock.writeLock().unlock();
      }

   }

   private static int shadow(int rgb) {
      return (rgb & 16579836) >> 2;
   }

   private static class_2561 parse(String text, int defaultColor) {
      if (text.indexOf(167) < 0) {
         return class_2561.method_43470(text).method_27696(class_2583.field_24360.method_36139(defaultColor));
      } else {
         class_5250 root = class_2561.method_43470("").method_27696(class_2583.field_24360.method_36139(defaultColor));
         class_2583 style = class_2583.field_24360.method_36139(defaultColor);
         StringBuilder run = new StringBuilder();

         for(int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            if (c == 167 && i + 1 < text.length()) {
               if (run.length() > 0) {
                  root.method_10852(class_2561.method_43470(run.toString()).method_27696(style));
                  run.setLength(0);
               }

               ++i;
               char code = Character.toLowerCase(text.charAt(i));
               style = applyCode(style, code, defaultColor);
            } else {
               run.append(c);
            }
         }

         if (run.length() > 0) {
            root.method_10852(class_2561.method_43470(run.toString()).method_27696(style));
         }

         return root;
      }
   }

   private static class_2583 applyCode(class_2583 s, char code, int defaultColor) {
      class_2583 var10000;
      switch (code) {
         case '0':
            var10000 = s.method_36139(-16777216);
            break;
         case '1':
            var10000 = s.method_36139(-16777046);
            break;
         case '2':
            var10000 = s.method_36139(-16733696);
            break;
         case '3':
            var10000 = s.method_36139(-16733526);
            break;
         case '4':
            var10000 = s.method_36139(-5636096);
            break;
         case '5':
            var10000 = s.method_36139(-5635926);
            break;
         case '6':
            var10000 = s.method_36139(-22016);
            break;
         case '7':
            var10000 = s.method_36139(-5592406);
            break;
         case '8':
            var10000 = s.method_36139(-11184811);
            break;
         case '9':
            var10000 = s.method_36139(-11184641);
            break;
         case ':':
         case ';':
         case '<':
         case '=':
         case '>':
         case '?':
         case '@':
         case 'A':
         case 'B':
         case 'C':
         case 'D':
         case 'E':
         case 'F':
         case 'G':
         case 'H':
         case 'I':
         case 'J':
         case 'K':
         case 'L':
         case 'M':
         case 'N':
         case 'O':
         case 'P':
         case 'Q':
         case 'R':
         case 'S':
         case 'T':
         case 'U':
         case 'V':
         case 'W':
         case 'X':
         case 'Y':
         case 'Z':
         case '[':
         case '\\':
         case ']':
         case '^':
         case '_':
         case '`':
         case 'g':
         case 'h':
         case 'i':
         case 'j':
         case 'k':
         case 'l':
         case 'm':
         case 'n':
         case 'o':
         case 'p':
         case 'q':
         default:
            var10000 = s;
            break;
         case 'a':
            var10000 = s.method_36139(-11141291);
            break;
         case 'b':
            var10000 = s.method_36139(-11141121);
            break;
         case 'c':
            var10000 = s.method_36139(-43691);
            break;
         case 'd':
            var10000 = s.method_36139(-43521);
            break;
         case 'e':
            var10000 = s.method_36139(-171);
            break;
         case 'f':
            var10000 = s.method_36139(-1);
            break;
         case 'r':
            var10000 = class_2583.field_24360.method_36139(defaultColor);
      }

      return var10000;
   }

   private static record DrawEntry(float dx, float dy, int argb, GlyphMap.Glyph glyph) {
   }
}
