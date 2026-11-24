package dev.ambience.features.settings;

import com.google.common.base.Converter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class EnumConverter<T extends Enum<T>> extends Converter<T, JsonElement> {
   private final Class<T> clazz;

   public EnumConverter(Class<T> clazz) {
      this.clazz = clazz;
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   public static <T extends Enum<?>> int currentEnum(T clazz) {
      Enum[] constants = (Enum[]) ((Enum) clazz).getDeclaringClass().getEnumConstants();
      for(int i = 0; i < constants.length; ++i) {
         Enum e = constants[i];
         if (e.name().equalsIgnoreCase(clazz.name())) {
            return i;
         }
      }

      return -1;
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   public static <T extends Enum<?>> T increaseEnum(T clazz) {
      int index = currentEnum(clazz);
      Enum[] constants = (Enum[]) ((Enum) clazz).getDeclaringClass().getEnumConstants();

      for(int i = 0; i < constants.length; ++i) {
         if (i == index + 1) {
            return (T) constants[i];
         }
      }

      return (T) constants[0];
   }

   public static <T extends Enum<?>> String getProperName(T clazz) {
      String[] parts = clazz.name().toLowerCase().split("_");
      StringBuilder sb = new StringBuilder();

      for(String part : parts) {
         if (!part.isEmpty()) {
            if (sb.length() > 0) {
               sb.append(' ');
            }

            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
         }
      }

      return sb.toString();
   }

   public JsonElement doForward(Enum anEnum) {
      return new JsonPrimitive(anEnum.toString());
   }

   public T doBackward(JsonElement jsonElement) {
      try {
         String raw = jsonElement.getAsString();
         if ("HOMOVORE".equalsIgnoreCase(raw) && this.clazz.getSimpleName().equals("Theme")) {
            raw = "AMBIENCE";
         }

         return (T)Enum.valueOf(this.clazz, raw);
      } catch (IllegalArgumentException var3) {
         return null;
      }
   }
}
