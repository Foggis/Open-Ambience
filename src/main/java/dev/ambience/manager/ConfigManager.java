package dev.ambience.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.ambience.Ambience;
import dev.ambience.features.Feature;
import dev.ambience.features.settings.BezierCurve;
import dev.ambience.features.settings.Bind;
import dev.ambience.features.settings.Setting;
import dev.ambience.util.traits.Jsonable;
import java.awt.Color;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import net.fabricmc.loader.api.FabricLoader;
import org.joml.Vector2f;

public class ConfigManager implements Jsonable {
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("ambience");
   private static final Path CONFIG_PATH;
   private static final Path PROFILES_DIR;
   private static final Pattern PROFILE_NAME;

   public void load() {
      if (Files.exists(CONFIG_PATH, new LinkOption[0])) {
         try {
            this.applyRoot(JsonParser.parseString(Files.readString(CONFIG_PATH)).getAsJsonObject(), false);
         } catch (IOException e) {
            Ambience.LOGGER.error("Failed to read config", e);
         } catch (Exception e) {
            Ambience.LOGGER.error("Failed to load config", e);
         }

      }
   }

   public void save() {
      if (Ambience.moduleManager != null) {
         try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(this.currentRoot()));
         } catch (IOException e) {
            Ambience.LOGGER.error("Failed to save config", e);
         }

      }
   }

   public JsonObject currentRoot() {
      JsonObject root = new JsonObject();
      if (Ambience.moduleManager != null) {
         root.add("Modules", Ambience.moduleManager.toJson());
      }

      if (Ambience.commandManager != null) {
         root.add("Commands", Ambience.commandManager.toJson());
      }

      return root;
   }

   public void applyRoot(JsonObject root) {
      this.applyRoot(root, true);
   }

   public void applyRoot(JsonObject root, boolean persist) {
      if (root != null) {
         if (root.has("Modules") && Ambience.moduleManager != null) {
            Ambience.moduleManager.fromJson(root.get("Modules"));
         }

         if (Ambience.commandManager != null) {
            Ambience.commandManager.fromJson(root.has("Commands") ? root.get("Commands") : null);
         }

         if (persist) {
            this.save();
         }

      }
   }

   public static boolean isValidProfileName(String name) {
      return name != null && PROFILE_NAME.matcher(name.trim()).matches();
   }

   public List<String> listLocalProfiles() {
      List<String> out = new ArrayList();
      if (!Files.isDirectory(PROFILES_DIR, new LinkOption[0])) {
         return out;
      } else {
         try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(PROFILES_DIR, "*.json");

            try {
               for(Path path : stream) {
                  String file = path.getFileName().toString();
                  out.add(file.substring(0, file.length() - 5));
               }
            } catch (Throwable var7) {
               if (stream != null) {
                  try {
                     stream.close();
                  } catch (Throwable var6) {
                     var7.addSuppressed(var6);
                  }
               }

               throw var7;
            }

            if (stream != null) {
               stream.close();
            }
         } catch (IOException e) {
            Ambience.LOGGER.warn("Failed to list local profiles", e);
         }

         out.sort(Comparator.comparing((s) -> s.toLowerCase(Locale.ROOT)));
         return out;
      }
   }

   public void saveLocalProfile(String name) throws IOException {
      name = name == null ? "" : name.trim();
      if (!isValidProfileName(name)) {
         throw new IOException("invalid profile name");
      } else {
         Files.createDirectories(PROFILES_DIR);
         Path path = PROFILES_DIR.resolve(sanitizeFileName(name) + ".json");
         Files.writeString(path, GSON.toJson(this.currentRoot()));
      }
   }

   public void loadLocalProfile(String name) throws IOException {
      name = name == null ? "" : name.trim();
      Path path = PROFILES_DIR.resolve(sanitizeFileName(name) + ".json");
      if (!Files.isRegularFile(path, new LinkOption[0])) {
         throw new IOException("profile not found");
      } else {
         this.applyRoot(JsonParser.parseString(Files.readString(path)).getAsJsonObject());
      }
   }

   public void deleteLocalProfile(String name) throws IOException {
      name = name == null ? "" : name.trim();
      Path path = PROFILES_DIR.resolve(sanitizeFileName(name) + ".json");
      Files.deleteIfExists(path);
   }

   private static String sanitizeFileName(String name) {
      return name.trim().replace('/', '_').replace('\\', '_');
   }

   public static void setValueFromJson(Feature feature, Setting setting, JsonElement element) {
      if (element != null && !element.isJsonNull()) {
         switch (setting.getType()) {
            case "Boolean":
               setting.setValueNoEvent(element.getAsBoolean());
               break;
            case "Double":
               setting.setValueNoEvent(element.getAsDouble());
               break;
            case "Float":
               setting.setValueNoEvent(element.getAsFloat());
               break;
            case "Integer":
               setting.setValueNoEvent(element.getAsInt());
               break;
            case "String":
               setting.setValueNoEvent(element.getAsString().replace("_", " "));
               break;
            case "Bind":
               setting.setValueNoEvent(new Bind(element.getAsInt()));
               break;
            case "Color":
               try {
                  String[] parts = element.getAsString().split(",");
                  if (parts.length == 4) {
                     setting.setValueNoEvent(new Color(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3])));
                  }
               } catch (Exception var7) {
               }
               break;
            case "Pos":
               try {
                  String[] parts = element.getAsString().split(",");
                  if (parts.length == 2) {
                     setting.setValueNoEvent(new Vector2f(Float.parseFloat(parts[0]), Float.parseFloat(parts[1])));
                  }
               } catch (Exception var6) {
               }
               break;
            case "Curve":
               BezierCurve parsed = BezierCurve.parse(element.getAsString());
               if (parsed != null) {
                  setting.setValueNoEvent(parsed);
               }
               break;
            case "Enum":
               setting.setEnumValue(element.getAsString());
         }

      }
   }

   public JsonElement toJson() {
      return null;
   }

   public void fromJson(JsonElement element) {
   }

   static {
      CONFIG_PATH = CONFIG_DIR.resolve("config.json");
      PROFILES_DIR = CONFIG_DIR.resolve("profiles");
      PROFILE_NAME = Pattern.compile("^[A-Za-z0-9][A-Za-z0-9 _.\\-]{1,46}[A-Za-z0-9]$");
   }
}
