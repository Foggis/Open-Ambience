package dev.ambience.features.modules.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.ambience.Ambience;
import dev.ambience.features.modules.Module;
import net.minecraft.class_2561;

public class UnloadModule extends Module {
   public UnloadModule() {
      super("Unload", "Unload Ambience from RAM (inject also strips mixins; toggle off to load again)", Module.Category.CLIENT);
   }

   public void onEnable() {
      Ambience.unloadFromMemory("module");
      if (mc.field_1724 != null) {
         mc.field_1724.method_7353(class_2561.method_43470("Ambience unloaded from memory"), false);
      }

   }

   public void onDisable() {
      Ambience.reloadIntoMemory();
      if (mc.field_1724 != null && Ambience.isInMemory()) {
         mc.field_1724.method_7353(class_2561.method_43470("Ambience loaded back into memory"), false);
      }

   }

   public void fromJson(JsonElement element) {
      if (element != null && !element.isJsonNull() && element.isJsonObject()) {
         JsonObject object = element.getAsJsonObject().deepCopy();
         object.addProperty("Enabled", false);
         super.fromJson(object);
      }
   }
}
