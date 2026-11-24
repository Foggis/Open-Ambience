package dev.ambience.features.gui;

import dev.ambience.Ambience;
import dev.ambience.features.modules.Module;
import dev.ambience.features.modules.client.SessionPillModule;
import dev.ambience.features.settings.BezierCurve;
import dev.ambience.features.settings.Bind;
import dev.ambience.features.settings.Setting;
import dev.ambience.loader.PayloadLoader;
import dev.ambience.manager.ConfigManager;
import dev.ambience.nativebridge.NativeModules;
import java.awt.Color;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Vector2f;

final class NativeGuiModel {
   private static Setting<?> editing;
   private static StringBuilder editBuffer;
   private static String fontPath;
   private static final String[] ICONS = new String[]{"swords", "image", "eye", "move_right", "user", "sliders_horizontal", "folder", "fire", "search", "x", "chevron_left"};
   private static final Map<String, String> ICON_PATHS = new HashMap();
   private static List<String> localConfigs = List.of();
   private static final String CLOUD_STATUS = "Local-only build: cloud disabled.";

   private NativeGuiModel() {
   }

   static String hudModel() {
      StringBuilder out = (new StringBuilder("F\t")).append(clean(fontPath())).append('\n');
      if (Ambience.moduleManager != null) {
         Module var2 = Ambience.moduleManager.getModuleByName("SessionPill");
         if (var2 instanceof SessionPillModule) {
            SessionPillModule pill = (SessionPillModule)var2;
            out.append(pill.hudModel(PayloadLoader.accountUid()));
         }
      }

      return out.toString();
   }

   static String encode(List<Module> modules, float scrollDelta, boolean searchFocused, boolean configFocused, String configName, String configStatus, boolean configCloud, boolean configPublicTab, boolean cloudVisibility, String configCategory) {
      StringBuilder out = new StringBuilder(8192);
      out.append("F\t").append(clean(fontPath())).append('\n');
      out.append("Q\t").append((char)(searchFocused ? '1' : '0')).append('\n');
      out.append("P\t").append((char)(configFocused ? '1' : '0')).append('\t').append(clean(configName)).append('\t').append(clean(configCloud ? CLOUD_STATUS : configStatus)).append('\t').append((char)(configCloud ? '1' : '0')).append('\t').append((char)(configPublicTab ? '1' : '0')).append('\t').append((char)(cloudVisibility ? '1' : '0')).append('\t').append('0').append('\t').append(clean(configCategory)).append('\n');
      if (scrollDelta != 0.0F) {
         out.append("W\t").append(scrollDelta).append('\n');
      }

      for(String icon : ICONS) {
         out.append("I\t").append(icon).append('\t').append(clean(iconPath(icon))).append('\n');
      }

      try {
         localConfigs = Ambience.configManager == null ? List.of() : Ambience.configManager.listLocalProfiles();

         for(String config : localConfigs) {
            out.append("C\t").append(clean(config)).append('\n');
         }
      } catch (Exception var29) {
         localConfigs = List.of();
      }

      for(int moduleIndex = 0; moduleIndex < modules.size(); ++moduleIndex) {
         Module module = (Module)modules.get(moduleIndex);
         List<Setting<?>> settings = module.getSettings();

         for(int settingIndex = 0; settingIndex < settings.size(); ++settingIndex) {
            Setting<?> setting = (Setting)settings.get(settingIndex);
            Object value = setting.getValue();
            Object var19 = setting.getMin();
            String var10000;
            if (var19 instanceof Number n) {
               var10000 = n.toString();
            } else {
               var10000 = "";
            }

            String min = var10000;
            Object var20 = setting.getMax();
            if (var20 instanceof Number n) {
               var10000 = n.toString();
            } else {
               var10000 = "";
            }

            String max = var10000;
            String renderedValue = setting.getValueAsString();
            if (setting == editing && value instanceof Bind) {
               renderedValue = "Press a key or mouse button";
            }

            String meta = "";
            if (!(value instanceof Enum<?> current)) {
               if (value instanceof Color color) {
                  meta = Integer.toUnsignedString(color.getRGB());
               } else if (value instanceof Vector2f range) {
                  renderedValue = range.x + "," + range.y;
                  min = Float.toString(setting.hasVec2TrackBounds() ? setting.getVec2TrackMin() : 0.0F);
                  max = Float.toString(setting.hasVec2TrackBounds() ? setting.getVec2TrackMax() : 500.0F);
               }
            } else {
               StringBuilder choices = new StringBuilder();

               for(Object choice : (Enum[])current.getDeclaringClass().getEnumConstants()) {
                  if (!choices.isEmpty()) {
                     choices.append(',');
                  }

                  choices.append(((Enum)choice).name());
               }

               meta = choices.toString();
            }

            out.append(moduleIndex).append('\t').append(settingIndex).append('\t').append(clean(setting.getType())).append('\t').append((char)(setting.isVisible() ? '1' : '0')).append('\t').append(clean(setting.getName())).append('\t').append(clean(renderedValue)).append('\t').append(clean(min)).append('\t').append(clean(max)).append('\t').append(clean(setting.getPage())).append('\t').append(clean(meta)).append('\n');
         }
      }

      return out.toString();
   }

   static String applyConfig(int command, String requestedName) {
      if (Ambience.configManager == null) {
         return "Config manager unavailable.";
      } else {
         try {
            if (command == -41) {
               String name = requestedName == null ? "" : requestedName.trim();
               if (!ConfigManager.isValidProfileName(name)) {
                  return "Use 3-48 letters or numbers.";
               } else {
                  Ambience.configManager.saveLocalProfile(name);
                  return "Saved \"" + name + "\".";
               }
            } else {
               int index = command % 10000000;
               if (index >= 0 && index < localConfigs.size()) {
                  String name = (String)localConfigs.get(index);
                  if (command >= 410000000) {
                     Ambience.configManager.deleteLocalProfile(name);
                     return "Deleted \"" + name + "\".";
                  } else {
                     Ambience.configManager.loadLocalProfile(name);
                     return "Loaded \"" + name + "\".";
                  }
               } else {
                  return "Profile no longer exists.";
               }
            }
         } catch (Exception var4) {
            return command == -41 ? "Could not save profile." : "Config action failed.";
         }
      }
   }

   static void applyCloud(int command, String requestedName, boolean makePublic, String category) {
   }

   static void apply(List<Module> modules, int command) {
      if (command >= 400000000 && command < 420000000) {
         int index = command % 10000000;
         if (index >= 0 && index < localConfigs.size() && Ambience.configManager != null) {
            try {
               if (command >= 410000000) {
                  Ambience.configManager.deleteLocalProfile((String)localConfigs.get(index));
               } else {
                  Ambience.configManager.loadLocalProfile((String)localConfigs.get(index));
               }
            } catch (Exception var13) {
            }

         }
      } else if (command == -30) {
         int[] color = NativeModules.clickGuiColor();
         if (color != null && color.length >= 3 && color[0] >= 0 && color[0] < modules.size()) {
            List<Setting<?>> settings = ((Module)modules.get(color[0])).getSettings();
            if (color[1] >= 0 && color[1] < settings.size()) {
               Setting setting = (Setting)settings.get(color[1]);
               if (setting.getValue() instanceof Color) {
                  setting.setValue(new Color(color[2], true));
               }

            }
         }
      } else if (command == -31) {
         float[] curve = NativeModules.clickGuiCurve();
         if (curve != null && curve.length >= 8) {
            int moduleIndex = (int)curve[0];
            int settingIndex = (int)curve[1];
            if (moduleIndex >= 0 && moduleIndex < modules.size()) {
               List<Setting<?>> settings = ((Module)modules.get(moduleIndex)).getSettings();
               if (settingIndex >= 0 && settingIndex < settings.size()) {
                  Setting setting = (Setting)settings.get(settingIndex);
                  if (setting.getValue() instanceof BezierCurve) {
                     setting.setValue((new BezierCurve(curve[2], curve[3], curve[4], curve[5], curve[6], curve[7])).clamped());
                  }

               }
            }
         }
      } else if (command >= 100000000 && command < 300000000) {
         boolean maximum = command >= 200000000;
         int packed = command - (maximum ? 200000000 : 100000000);
         int moduleIndex = packed / 256256;
         packed %= 256256;
         int settingIndex = packed / 1001;
         float pct = (float)(packed % 1001) / 1000.0F;
         if (moduleIndex >= 0 && moduleIndex < modules.size()) {
            List<Setting<?>> settings = ((Module)modules.get(moduleIndex)).getSettings();
            if (settingIndex >= 0 && settingIndex < settings.size()) {
               Setting setting = (Setting)settings.get(settingIndex);
               Object var40 = setting.getValue();
               if (var40 instanceof Vector2f) {
                  Vector2f range = (Vector2f)var40;
                  float low = setting.hasVec2TrackBounds() ? setting.getVec2TrackMin() : 0.0F;
                  float high = setting.hasVec2TrackBounds() ? setting.getVec2TrackMax() : 500.0F;
                  float value = (float)Math.round(low + (high - low) * pct);
                  setting.setValue(maximum ? new Vector2f(range.x, Math.max(range.x, value)) : new Vector2f(Math.min(range.y, value), range.y));
               }
            }
         }
      } else if (command >= 1000000) {
         int packed = command - 1000000;
         int moduleIndex = packed / 256256;
         packed %= 256256;
         int settingIndex = packed / 1001;
         float pct = (float)(packed % 1001) / 1000.0F;
         if (moduleIndex >= 0 && moduleIndex < modules.size()) {
            List<Setting<?>> settings = ((Module)modules.get(moduleIndex)).getSettings();
            if (settingIndex >= 0 && settingIndex < settings.size()) {
               Setting setting = (Setting)settings.get(settingIndex);
               Object high = setting.getValue();
               if (high instanceof Number) {
                  Number current = (Number)high;
                  high = setting.getMin();
                  if (high instanceof Number) {
                     Number min = (Number)high;
                     high = setting.getMax();
                     if (high instanceof Number) {
                        Number max = (Number)high;
                        double var44 = min.doubleValue() + (max.doubleValue() - min.doubleValue()) * (double)pct;
                        if (current instanceof Integer) {
                           setting.setValue((int)Math.round(var44));
                        } else if (current instanceof Float) {
                           setting.setValue((float)((double)Math.round(var44 * (double)10.0F) / (double)10.0F));
                        } else {
                           setting.setValue((double)Math.round(var44 * (double)10.0F) / (double)10.0F);
                        }

                        return;
                     }
                  }
               }

            }
         }
      } else if (command >= 10000 && command < 1000000) {
         int packed = command - 10000;
         int moduleIndex = packed / 256;
         int settingIndex = packed % 256;
         if (moduleIndex >= 0 && moduleIndex < modules.size()) {
            List<Setting<?>> settings = ((Module)modules.get(moduleIndex)).getSettings();
            if (settingIndex >= 0 && settingIndex < settings.size()) {
               Setting setting = (Setting)settings.get(settingIndex);
               Object value = setting.getValue();
               if (value instanceof Boolean) {
                  Boolean state = (Boolean)value;
                  setting.setValue(!state);
               } else if (value instanceof Enum) {
                  setting.increaseEnum();
               } else if (value instanceof Bind || value instanceof String) {
                  editing = setting;
                  editBuffer = new StringBuilder(value instanceof String ? value.toString() : "");
               }

            }
         }
      }
   }

   static boolean keyPressed(int key) {
      if (editing == null) {
         return false;
      } else if (!(editing.getValue() instanceof Bind)) {
         if (key == 257) {
            ((Setting) editing).setValue(editBuffer.length() == 0 ? editing.getDefaultValue() : editBuffer.toString());
            editing = null;
         } else if (key == 256) {
            editing = null;
         } else if (key == 259 && editBuffer.length() > 0) {
            editBuffer.deleteCharAt(editBuffer.length() - 1);
         }

         return true;
      } else {
         ((Setting) editing).setValue(new Bind(key != 256 && key != 259 && key != 261 ? key : -1));
         editing = null;
         return true;
      }
   }

   static boolean mousePressed(int button) {
      if (editing != null && editing.getValue() instanceof Bind && button >= 0) {
         ((Setting) editing).setValue(Bind.fromMouseButton(button));
         editing = null;
         editBuffer = null;
         return true;
      } else {
         return false;
      }
   }

   static boolean charTyped(String value) {
      if (editing != null && editing.getValue() instanceof String && value != null) {
         if (editBuffer.length() < 256) {
            editBuffer.append(value);
         }

         return true;
      } else {
         return false;
      }
   }

   static void cancelEditing() {
      editing = null;
      editBuffer = null;
   }

   private static String clean(String value) {
      return value == null ? "" : value.replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
   }

   private static synchronized String fontPath() {
      if (fontPath != null) {
         return fontPath;
      } else {
         try {
            InputStream input = NativeGuiModel.class.getResourceAsStream("/assets/ambience/font/default.ttf");

            String var6;
            label52: {
               String var2;
               try {
                  if (input == null) {
                     fontPath = "";
                     var6 = "";
                     break label52;
                  }

                  Path file = Files.createTempFile("ambience-native-font-", ".ttf");
                  Files.copy(input, file, new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
                  file.toFile().deleteOnExit();
                  var2 = fontPath = file.toAbsolutePath().toString();
               } catch (Throwable var4) {
                  if (input != null) {
                     try {
                        input.close();
                     } catch (Throwable var3) {
                        var4.addSuppressed(var3);
                     }
                  }

                  throw var4;
               }

               if (input != null) {
                  input.close();
               }

               return var2;
            }

            if (input != null) {
               input.close();
            }

            return var6;
         } catch (Exception var5) {
            fontPath = "";
            return "";
         }
      }
   }

   private static synchronized String iconPath(String name) {
      String cached = (String)ICON_PATHS.get(name);
      if (cached != null) {
         return cached;
      } else {
         String resource = "/assets/ambience/textures/gui/icons/" + name + ".png";

         try {
            InputStream input = NativeGuiModel.class.getResourceAsStream(resource);

            String var10;
            label55: {
               String var6;
               try {
                  if (input == null) {
                     var10 = "";
                     break label55;
                  }

                  Path file = Files.createTempFile("ambience-native-icon-" + name + "-", ".png");
                  Files.copy(input, file, new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
                  file.toFile().deleteOnExit();
                  String path = file.toAbsolutePath().toString();
                  ICON_PATHS.put(name, path);
                  var6 = path;
               } catch (Throwable var8) {
                  if (input != null) {
                     try {
                        input.close();
                     } catch (Throwable var7) {
                        var8.addSuppressed(var7);
                     }
                  }

                  throw var8;
               }

               if (input != null) {
                  input.close();
               }

               return var6;
            }

            if (input != null) {
               input.close();
            }

            return var10;
         } catch (Exception var9) {
            return "";
         }
      }
   }
}
