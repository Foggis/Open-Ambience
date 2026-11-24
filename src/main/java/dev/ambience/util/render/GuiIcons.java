package dev.ambience.util.render;

import dev.ambience.features.modules.Module;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.class_1011;
import net.minecraft.class_10799;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;

public final class GuiIcons {
   public static final String SWORDS = "swords";
   public static final String GLOBE = "globe";
   public static final String IMAGE = "image";
   public static final String EYE = "eye";
   public static final String MOVE_RIGHT = "move_right";
   public static final String USER = "user";
   public static final String SPARKLES = "sparkles";
   public static final String SLIDERS = "sliders_horizontal";
   public static final String LAYOUT = "layout_dashboard";
   public static final String FOLDER = "folder";
   public static final String FIRE = "fire";
   public static final String SEARCH = "search";
   public static final String CLOSE = "x";
   public static final String BACK = "chevron_left";
   private static final Map<String, IconTex> CACHE = new HashMap();
   private static final EnumMap<Module.Category, String> CATEGORY = new EnumMap(Module.Category.class);

   private GuiIcons() {
   }

   public static void category(class_332 g, Module.Category cat, float x, float y, float size, int color) {
      String name = cat == null ? "sliders_horizontal" : (String)CATEGORY.getOrDefault(cat, "sliders_horizontal");
      draw(g, name, x, y, size, color);
   }

   public static void draw(class_332 g, String name, float x, float y, float size, int color) {
      draw(g, name, x, y, size, size, color);
   }

   public static void draw(class_332 g, String name, float x, float y, float w, float h, int color) {
      IconTex tex = texture(name);
      if (tex != null) {
         int dw = Math.max(1, Math.round(w));
         int dh = Math.max(1, Math.round(h));
         g.method_25293(class_10799.field_56883, tex.id, Math.round(x), Math.round(y), 0.0F, 0.0F, dw, dh, tex.width, tex.height, tex.width, tex.height, GuiFade.apply(color));
      }
   }

   private static IconTex texture(String name) {
      IconTex cached = (IconTex)CACHE.get(name);
      if (cached != null) {
         return cached;
      } else if (class_310.method_1551() == null) {
         return null;
      } else {
         String path = "/assets/ambience/textures/gui/icons/" + name + ".png";

         try {
            InputStream in = GuiIcons.class.getResourceAsStream(path);

            class_1011 image;
            label59: {
               IconTex var8;
               try {
                  if (in == null) {
                     image = null;
                     break label59;
                  }

                  image = LinearTextures.readPng(in);
                  int width = image.method_4307();
                  int height = image.method_4323();
                  IconTex tex = new IconTex(LinearTextures.register("icon_" + name, image), width, height);
                  CACHE.put(name, tex);
                  var8 = tex;
               } catch (Throwable var10) {
                  if (in != null) {
                     try {
                        in.close();
                     } catch (Throwable var9) {
                        var10.addSuppressed(var9);
                     }
                  }

                  throw var10;
               }

               if (in != null) {
                  in.close();
               }

               return var8;
            }

            if (in != null) {
               in.close();
            }

            return null;
         } catch (Exception var11) {
            return null;
         }
      }
   }

   static {
      CATEGORY.put(Module.Category.COMBAT, "swords");
      CATEGORY.put(Module.Category.WORLD, "globe");
      CATEGORY.put(Module.Category.RENDER, "image");
      CATEGORY.put(Module.Category.VISUALS, "eye");
      CATEGORY.put(Module.Category.MOVEMENT, "move_right");
      CATEGORY.put(Module.Category.PLAYER, "user");
      CATEGORY.put(Module.Category.FUNNY, "sparkles");
      CATEGORY.put(Module.Category.CLIENT, "sliders_horizontal");
      CATEGORY.put(Module.Category.CONFIGS, "folder");
      CATEGORY.put(Module.Category.HUD, "layout_dashboard");
   }

   private static record IconTex(class_2960 id, int width, int height) {
   }
}
