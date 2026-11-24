package dev.ambience.event.impl;

import dev.ambience.event.Event;
import dev.ambience.features.Feature;
import dev.ambience.features.settings.Setting;

public class ClientEvent extends Event {
   private final Type type;
   private final Feature feature;
   private Setting<?> setting;

   public ClientEvent(Type type, Feature feature) {
      this.type = type;
      this.feature = feature;
   }

   public ClientEvent(Setting<?> setting) {
      this(ClientEvent.Type.SETTING_UPDATE, setting.getFeature());
      this.setting = setting;
   }

   public Type getType() {
      return this.type;
   }

   public Feature getFeature() {
      return this.feature;
   }

   public Setting<?> getSetting() {
      return this.setting;
   }

   public static enum Type {
      TOGGLE_MODULE,
      SETTING_UPDATE;

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{TOGGLE_MODULE, SETTING_UPDATE};
      }
   }
}
