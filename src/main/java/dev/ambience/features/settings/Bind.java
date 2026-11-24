package dev.ambience.features.settings;

import com.google.common.base.Converter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.ambience.util.traits.Util;
import net.minecraft.class_11908;
import net.minecraft.class_3675;
import org.lwjgl.glfw.GLFW;

public class Bind implements Util {
   public static final int MOUSE_BUTTON_OFFSET = 1000;
   private int key;

   public Bind(int key) {
      this.key = key;
   }

   public static Bind none() {
      return new Bind(-1);
   }

   public static Bind fromMouseButton(int button) {
      return new Bind(1000 + button);
   }

   public static boolean isMouseButton(int key) {
      return key >= 1000;
   }

   public int getKey() {
      return this.key;
   }

   public void setKey(int key) {
      this.key = key;
   }

   public boolean isEmpty() {
      return this.key < 0;
   }

   public String toString() {
      if (this.isEmpty()) {
         return "None";
      } else if (isMouseButton(this.key)) {
         int var10000 = this.key - 1000;
         return "Mouse " + (var10000 + 1);
      } else {
         String raw = class_3675.method_15985(new class_11908(this.key, 0, 0)).method_1441();
         String cleaned = raw.replace("key.keyboard.", "").replace("key.mouse.", "").replace('.', ' ').trim();
         StringBuilder out = new StringBuilder(cleaned.length());

         for(String part : cleaned.split(" ")) {
            if (!part.isEmpty()) {
               if (out.length() > 0) {
                  out.append(' ');
               }

               out.append(this.capitalise(part));
            }
         }

         return out.toString();
      }
   }

   public boolean isDown() {
      if (this.isEmpty()) {
         return false;
      } else if (isMouseButton(this.key)) {
         return GLFW.glfwGetMouseButton(mc.method_22683().method_4490(), this.key - 1000) == 1;
      } else {
         return GLFW.glfwGetKey(mc.method_22683().method_4490(), this.getKey()) == 1;
      }
   }

   private String capitalise(String str) {
      if (str.isEmpty()) {
         return "";
      } else {
         char var10000 = Character.toUpperCase(str.charAt(0));
         return var10000 + (str.length() != 1 ? str.substring(1).toLowerCase() : "");
      }
   }

   public static class BindConverter extends Converter<Bind, JsonElement> {
      public JsonElement doForward(Bind bind) {
         return new JsonPrimitive(bind.toString());
      }

      public Bind doBackward(JsonElement jsonElement) {
         String s = jsonElement.getAsString();
         if (s.equalsIgnoreCase("None")) {
            return Bind.none();
         } else {
            String mouse = s.toUpperCase().replace("MOUSE", "M").replace(" ", "");
            if (mouse.startsWith("M") && mouse.length() >= 2 && Character.isDigit(mouse.charAt(1))) {
               try {
                  int button = Integer.parseInt(mouse.substring(1)) - 1;
                  if (button >= 0) {
                     return Bind.fromMouseButton(button);
                  }
               } catch (NumberFormatException var7) {
               }
            }

            int key = -1;

            try {
               key = class_3675.method_15981(s.toUpperCase()).method_1444();
            } catch (Exception var6) {
            }

            return key == 0 ? Bind.none() : new Bind(key);
         }
      }
   }
}
