package dev.ambience.manager;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.ambience.Ambience;
import dev.ambience.event.impl.render.Render2DEvent;
import dev.ambience.event.impl.render.Render3DEvent;
import dev.ambience.event.system.EventBus;
import dev.ambience.features.Feature;
import dev.ambience.features.modules.Module;
import dev.ambience.features.modules.client.DebugModule;
import dev.ambience.features.modules.client.SessionPillModule;
import dev.ambience.features.modules.client.UnloadModule;
import dev.ambience.features.settings.Setting;
import dev.ambience.hooks.CatalogModule;
import dev.ambience.hooks.PayloadRuntime;
import dev.ambience.inject.InjectInput;
import dev.ambience.util.traits.Jsonable;
import dev.ambience.util.traits.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ModuleManager implements Jsonable, Util {
   private final Map<Class<? extends Module>, Module> fastRegistry = new HashMap();
   private final List<Module> modules = new ArrayList();
   private final List<Pending> pending = new ArrayList();
   private boolean restoredEnabled;

   public void init() {
      this.register(new DebugModule());
      this.register(new SessionPillModule());
      this.register(new UnloadModule());
   }

   public void offerCatalog(List<CatalogEntry> entries) {
      for(CatalogEntry entry : entries) {
         if (entry != null && entry.name != null && !entry.name.isBlank() && this.getModuleByName(entry.name) == null && this.pendingNamed(entry.name) == null) {
            this.pending.add(new Pending(entry));
         }
      }

   }

   public void register(Module module) {
      if (!Set.of("AutoSprint", "NoSmoke", "NoBlast", "AntiBlind", "NoInvis", "AnchorMacro", "AutoCrystal", "AutoMace", "AimAssist", "ProjectileAim", "AutoWeb", "AutoEsc", "JumpReset", "WTap", "STap", "FastXP", "CrystalOptimizer", "TBot", "KeyPearl", "ViewModel", "Fullbright", "LowFire", "TotemAnimation", "ShieldStun", "Freelook", "CustomSkybox", "LungeSwap", "PearlCatch", "WindHop", "BlockESP", "Freecam", "PlayerESP", "StorageESP", "WorldParticles", "XRay", "ClickGui", "SessionPill", "Unload").contains(module.getName())) {
         module.hidden = true;
      }

      this.modules.add(module);
      this.fastRegistry.put(module.getClass(), module);
      this.pending.removeIf((p) -> p.entry.name.equalsIgnoreCase(module.getName()));
   }

   public List<Module> guiModules(Module.Category category, String query) {
      boolean searching = query != null && !query.isBlank();
      String needle = searching ? query.toLowerCase() : "";
      List<Module> out = new ArrayList();

      for(Module module : this.modules) {
         if (!module.hidden) {
            if (searching) {
               if (!module.getName().toLowerCase().contains(needle)) {
                  continue;
               }
            } else if (module.getCategory() != category) {
               continue;
            }

            out.add(module);
         }
      }

      for(Pending item : this.pending) {
         if (searching) {
            if (!item.entry.name.toLowerCase().contains(needle)) {
               continue;
            }
         } else if (item.entry.category != category) {
            continue;
         }

         out.add(this.stubOf(item));
      }

      out.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
      return out;
   }

   public boolean pendingEnabled(String name) {
      Pending item = this.pendingNamed(name);
      return item != null && enabledIn(item.config);
   }

   public Module ensureByName(String name) {
      Module live = this.getModuleByName(name);
      if (live != null) {
         return live;
      } else {
         Pending item = this.pendingNamed(name);
         return item == null ? null : this.ensure(item);
      }
   }

   public List<Module> getModules() {
      return this.modules;
   }

   public Stream<Module> stream() {
      return this.modules.stream();
   }

   public <T extends Module> T getModuleByClass(Class<T> clazz) {
      return (T)(this.fastRegistry.get(clazz));
   }

   public Module getModuleByName(String name) {
      return (Module)this.stream().filter((m) -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
   }

   public Module getModuleByDisplayName(String display) {
      return (Module)this.stream().filter((m) -> m.getDisplayName().equalsIgnoreCase(display)).findFirst().orElse(null);
   }

   public List<Module> getModulesByCategory(Module.Category category) {
      return this.stream().filter((m) -> m.getCategory() == category).toList();
   }

   public List<Module.Category> getCategories() {
      return Arrays.asList(Module.Category.values());
   }

   public void onLoad() {
      this.modules.forEach(Module::onLoad);
   }

   private Module ensure(Pending item) {
      Module live = this.getModuleByName(item.entry.name);
      if (live != null) {
         this.pending.remove(item);
         return live;
      } else {
         Module module;
         try {
            module = PayloadRuntime.createModule(item.entry.name, item.entry.className);
         } catch (RuntimeException e) {
            Ambience.LOGGER.error("Failed to materialize {}", item.entry.name, e);
            return null;
         }

         if (module == null) {
            Ambience.LOGGER.error("Payload class missing for {} ({})", item.entry.name, item.entry.className);
            return null;
         } else {
            this.register(module);
            if (item.config != null) {
               module.fromJson(item.config);
            }

            module.onLoad();
            return module;
         }
      }
   }

   private CatalogModule stubOf(Pending item) {
      if (item.stub == null) {
         item.stub = new CatalogModule(item.entry.name, item.entry.description, item.entry.category);
      }

      return item.stub;
   }

   private Pending pendingNamed(String name) {
      for(Pending item : this.pending) {
         if (item.entry.name.equalsIgnoreCase(name)) {
            return item;
         }
      }

      return null;
   }

   private void restoreEnabledInWorld() {
      if (!this.restoredEnabled && !Feature.nullCheck()) {
         this.restoredEnabled = true;
         for(Pending item : List.copyOf(this.pending)) {
            if (enabledIn(item.config)) {
               this.ensure(item);
            }
         }

      }
   }

   private void forEnabled(Consumer<Module> action) {
      for(Module module : this.stream().filter(Feature::isEnabled).toList()) {
         try {
            action.accept(module);
         } catch (NoClassDefFoundError e) {
            Ambience.LOGGER.error("Module {} missing payload class", module.getName(), e);

            try {
               module.disable();
            } catch (RuntimeException var6) {
            }
         } catch (LinkageError e) {
            Ambience.LOGGER.error("Module {} linkage error", module.getName(), e);
         }
      }

   }

   private static boolean enabledIn(JsonElement config) {
      if (config != null && config.isJsonObject() && config.getAsJsonObject().has("Enabled")) {
         try {
            return config.getAsJsonObject().get("Enabled").getAsBoolean();
         } catch (RuntimeException var2) {
            return false;
         }
      } else {
         return false;
      }
   }

   private static int bindIn(JsonElement config) {
      if (config != null && config.isJsonObject() && config.getAsJsonObject().has("Keybind")) {
         try {
            return config.getAsJsonObject().get("Keybind").getAsInt();
         } catch (RuntimeException var2) {
            return 0;
         }
      } else {
         return 0;
      }
   }

   public void onTick() {
      this.restoreEnabledInWorld();
      if (this.payloadLive()) {
         this.forEnabled(Module::onTick);
      }
   }

   public void onPreTick() {
      this.restoreEnabledInWorld();
      if (this.payloadLive()) {
         this.forEnabled(Module::onPreTick);
      }
   }

   private boolean payloadLive() {
      return true;
   }

   public void onPostPositionTick() {
      this.forEnabled(Module::onPostPositionTick);
   }

   public void onRender2D(Render2DEvent event) {
      this.forEnabled((module) -> module.onRender2D(event));
   }

   public void onRender3D(Render3DEvent event) {
      this.forEnabled((module) -> module.onRender3D(event));
   }

   public void onUnload() {
      List var10000 = this.modules;
      EventBus var10001 = EVENT_BUS;
      Objects.requireNonNull(var10001);
      var10000.forEach(var10001::unregister);
      this.modules.forEach(Module::onUnload);
   }

   public void evictPayloadFromMemory() {
      this.restoredEnabled = false;

      for(Module module : List.copyOf(this.modules)) {
         if (!(module instanceof DebugModule) && !(module instanceof UnloadModule)) {
            if (module.isEnabled()) {
               try {
                  module.disable();
               } catch (Throwable var4) {
               }
            }

            try {
               EVENT_BUS.unregister(module);
            } catch (Throwable var6) {
            }

            try {
               module.onUnload();
            } catch (Throwable var5) {
            }

            this.modules.remove(module);
            this.fastRegistry.remove(module.getClass());
         }
      }

      this.pending.clear();
   }

   public void onKeyPressed(int key) {
      if (key > 0) {
         if (InjectInput.take(key, 1)) {
            for(Pending item : List.copyOf(this.pending)) {
               if (bindIn(item.config) == key) {
                  this.ensure(item);
               }
            }

            this.stream().filter((module) -> module.getBind().getKey() == key).forEach((module) -> {
               if (mc.field_1755 == null || !"ClickGui".equals(module.getName())) {
                  if (mc.field_1755 == null || module.getCategory() == Module.Category.CLIENT) {
                     if (module.getBindMode() == Module.BindMode.HOLD) {
                        if (!module.isEnabled()) {
                           module.enable();
                        }
                     } else {
                        module.toggle();
                     }

                  }
               }
            });
            if (key == 344) {
               Module gui = this.getModuleByName("ClickGui");
               if (gui != null && gui.getBind().getKey() <= 0) {
                  Ambience.LOGGER.warn("ClickGui bind unbound; opening from Right Shift");
                  gui.toggle();
               }
            }

         }
      }
   }

   public void onKeyReleased(int key) {
      if (key > 0) {
         if (InjectInput.take(key, 0)) {
            this.stream().filter((module) -> module.getBindMode() == Module.BindMode.HOLD && module.getBind().getKey() == key && module.isEnabled()).forEach(Module::disable);
         }
      }
   }

   public void onMousePressed(int button) {
      if (mc.field_1755 == null) {
         int key = 1000 + button;
         this.onKeyPressed(key);
      }
   }

   public void onMouseReleased(int button) {
      int key = 1000 + button;
      this.onKeyReleased(key);
   }

   public JsonElement toJson() {
      JsonObject object = new JsonObject();

      for(Module module : this.modules) {
         object.add(module.getName(), module.toJson());
      }

      for(Pending item : this.pending) {
         if (!object.has(item.entry.name) && item.config != null) {
            object.add(item.entry.name, item.config);
         }
      }

      return object;
   }

   public void fromJson(JsonElement element) {
      if (element != null && element.isJsonObject()) {
         JsonObject object = element.getAsJsonObject();

         for(Module module : this.modules) {
            resetModule(module);
            if (object.has(module.getName())) {
               module.fromJson(object.get(module.getName()));
            }
         }

         for(Pending item : this.pending) {
            item.config = object.has(item.entry.name) ? object.get(item.entry.name) : null;
         }

      }
   }

   private static void resetModule(Module module) {
      if (module.isEnabled()) {
         module.disable();
      }

      for(Setting<?> setting : module.getSettings()) {
         resetSetting(setting);
      }

   }

   private static void resetSetting(Setting setting) {
      setting.setValueNoEvent(setting.getDefaultValue());
   }

   public static final class CatalogEntry {
      public final String className;
      public final String name;
      public final Module.Category category;
      public final String description;

      public CatalogEntry(String className, String name, Module.Category category, String description) {
         this.className = className;
         this.name = name;
         this.category = category;
         this.description = description;
      }
   }

   private static final class Pending {
      private final CatalogEntry entry;
      private JsonElement config;
      private CatalogModule stub;

      private Pending(CatalogEntry entry) {
         this.entry = entry;
      }
   }
}
