package dev.ambience.features.modules;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.ambience.Ambience;
import dev.ambience.event.impl.ClientEvent;
import dev.ambience.event.impl.render.Render2DEvent;
import dev.ambience.event.impl.render.Render3DEvent;
import dev.ambience.features.Feature;
import dev.ambience.features.commands.Command;
import dev.ambience.features.modules.client.DebugModule;
import dev.ambience.features.settings.BezierCurve;
import dev.ambience.features.settings.Bind;
import dev.ambience.features.settings.Setting;
import dev.ambience.manager.ConfigManager;
import dev.ambience.util.ModuleDebug;
import dev.ambience.util.traits.Jsonable;
import dev.ambience.util.traits.Toggleable;
import java.awt.Color;
import net.minecraft.class_124;
import org.joml.Vector2f;

public class Module extends Feature implements Jsonable, Toggleable {
   private final String description;
   private final Category category;
   public final Setting<Boolean> enabled = this.bool("Enabled", false).setPage("General");
   public final Setting<Bind> bind = this.key("Keybind", new Bind(-1)).setPage("General");
   public final Setting<BindMode> bindMode;
   public final Setting<String> displayName;
   public boolean hidden;

   public Module(String name, String description, Category category) {
      super(name);
      this.bindMode = this.mode("BindMode", Module.BindMode.TOGGLE).setPage("General");
      this.displayName = this.str("DisplayName", name).setPage("General");
      this.displayName.setVisibility((value) -> false);
      this.description = description;
      this.category = category;
   }

   public void onEnable() {
   }

   public void onDisable() {
   }

   public void onToggle() {
   }

   public void onLoad() {
   }

   public void onTick() {
   }

   public void onPreTick() {
   }

   public void onPostPositionTick() {
   }

   public void onRender2D(Render2DEvent event) {
   }

   public void onRender3D(Render3DEvent event) {
   }

   public void onUnload() {
   }

   public String getDebugState() {
      return null;
   }

   public String getDisplayInfo() {
      return null;
   }

   public String getMeta() {
      return null;
   }

   public void enable() {
      this.enabled.setValue(true);
      EVENT_BUS.register(this);
      EVENT_BUS.post(new ClientEvent(ClientEvent.Type.TOGGLE_MODULE, this));
      this.onToggle();
      this.onEnable();
      if (!(this instanceof DebugModule)) {
         try {
            ModuleDebug.module(this.getName(), "enable", this.getDebugState());
         } catch (Throwable t) {
            Ambience.LOGGER.warn("Debug state for {} failed: {}", this.getName(), t.toString());
         }
      }
   }

   public void disable() {
      this.enabled.setValue(false);
      EVENT_BUS.unregister(this);
      EVENT_BUS.post(new ClientEvent(ClientEvent.Type.TOGGLE_MODULE, this));
      this.onToggle();
      this.onDisable();
      if (!(this instanceof DebugModule)) {
         ModuleDebug.module(this.getName(), "disable", "");
      }

   }

   public String getDisplayName() {
      return this.displayName.getValue();
   }

   public void setDisplayName(String name) {
      Module module = Ambience.moduleManager.getModuleByDisplayName(name);
      Module originalModule = Ambience.moduleManager.getModuleByName(name);
      if (module == null && originalModule == null) {
         String var10000 = this.getDisplayName();
         Command.sendMessage(var10000 + ", name: " + this.getName() + ", has been renamed to: " + name);
         this.displayName.setValue(name);
      } else {
         Command.sendMessage("{red} A module of this name already exists.");
      }
   }

   public boolean isEnabled() {
      return (Boolean)this.enabled.getValue();
   }

   public boolean isToggled() {
      return this.isEnabled();
   }

   public String getDescription() {
      return this.description;
   }

   public Category getCategory() {
      return this.category;
   }

   public String getInfo() {
      return null;
   }

   public Bind getBind() {
      return this.bind.getValue();
   }

   public void setBind(int key) {
      this.bind.setValue(new Bind(key));
   }

   public String getFullArrayString() {
      String var10000 = this.getDisplayName();
      return var10000 + String.valueOf(class_124.field_1080) + (this.getDisplayInfo() != null ? " [" + String.valueOf(class_124.field_1068) + this.getDisplayInfo() + String.valueOf(class_124.field_1080) + "]" : "");
   }

   public JsonElement toJson() {
      JsonObject object = new JsonObject();

      for(Setting<?> setting : this.getSettings()) {
         try {
            Object var8 = setting.getValue();
            if (var8 instanceof Bind keyBind) {
               object.addProperty(setting.getName(), keyBind.getKey());
            } else {
               var8 = setting.getValue();
               if (var8 instanceof Color color) {
                  String var10001 = setting.getName();
                  int var10002 = color.getRed();
                  object.addProperty(var10001, var10002 + "," + color.getGreen() + "," + color.getBlue() + "," + color.getAlpha());
               } else {
                  var8 = setting.getValue();
                  if (var8 instanceof Vector2f pos) {
                     String var13 = setting.getName();
                     float var14 = pos.x();
                     object.addProperty(var13, var14 + "," + pos.y());
                  } else {
                     var8 = setting.getValue();
                     if (var8 instanceof BezierCurve curve) {
                        object.addProperty(setting.getName(), curve.toString());
                     } else {
                        object.addProperty(setting.getName(), setting.getValueAsString());
                     }
                  }
               }
            }
         } catch (Throwable e) {
            Ambience.LOGGER.error("Failed to create JSON field", e);
         }
      }

      return object;
   }

   public void fromJson(JsonElement element) {
      if (element != null && !element.isJsonNull()) {
         JsonObject object = element.getAsJsonObject();
         if (object.has("Enabled")) {
            try {
               boolean shouldEnable = object.get("Enabled").getAsBoolean();
               if (shouldEnable && !this.isEnabled()) {
                  this.enable();
               } else if (!shouldEnable && this.isEnabled()) {
                  this.disable();
               }
            } catch (Throwable e) {
               Ambience.LOGGER.error("Failed to toggle module {} during load", this.getName(), e);
            }
         }

         for(Setting<?> setting : this.getSettings()) {
            if (setting != this.enabled) {
               try {
                  JsonElement settingElement = object.get(setting.getName());
                  if (settingElement != null && !settingElement.isJsonNull()) {
                     ConfigManager.setValueFromJson(this, setting, settingElement);
                  }
               } catch (Throwable throwable) {
                  Ambience.LOGGER.error("Failed to load from JSON", throwable);
               }
            }
         }

      }
   }

   public BindMode getBindMode() {
      return this.bindMode.getValue();
   }

   public static enum BindMode {
      TOGGLE,
      HOLD;

      // $FF: synthetic method
      private static BindMode[] $values() {
         return new BindMode[]{TOGGLE, HOLD};
      }
   }

   public static enum Category {
      COMBAT("Combat"),
      WORLD("World"),
      RENDER("Render"),
      VISUALS("Visuals"),
      MOVEMENT("Movement"),
      PLAYER("Player"),
      FUNNY("Funny"),
      CLIENT("Client"),
      CONFIGS("Configs"),
      HUD("Hud");

      private final String name;

      private Category(String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Category[] $values() {
         return new Category[]{COMBAT, WORLD, RENDER, VISUALS, MOVEMENT, PLAYER, FUNNY, CLIENT, CONFIGS, HUD};
      }
   }
}
