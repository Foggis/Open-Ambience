package dev.ambience.manager;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.ambience.features.Feature;
import dev.ambience.util.traits.Jsonable;

public class CommandManager extends Feature implements Jsonable {
   private String commandPrefix = ".";

   public CommandManager() {
      super("Commands");
   }

   public void setCommandPrefix(String commandPrefix) {
      this.commandPrefix = commandPrefix;
   }

   public String getCommandPrefix() {
      return this.commandPrefix;
   }

   public boolean isFunnyVisible() {
      return false;
   }

   public JsonElement toJson() {
      JsonObject object = new JsonObject();
      object.addProperty("Prefix", this.commandPrefix);
      return object;
   }

   public void fromJson(JsonElement element) {
      this.commandPrefix = ".";
      if (element != null && !element.isJsonNull()) {
         JsonObject object = element.getAsJsonObject();
         if (object.has("Prefix")) {
            this.commandPrefix = object.get("Prefix").getAsString();
         }

      }
   }
}
