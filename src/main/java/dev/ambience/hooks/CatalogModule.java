package dev.ambience.hooks;

import dev.ambience.Ambience;
import dev.ambience.features.modules.Module;

public final class CatalogModule extends Module {
   public CatalogModule(String name, String description, Module.Category category) {
      super(name, description, category);
   }

   public boolean isEnabled() {
      if (Ambience.moduleManager != null) {
         Module live = Ambience.moduleManager.getModuleByName(this.getName());
         return live != null && live != this ? live.isEnabled() : Ambience.moduleManager.pendingEnabled(this.getName());
      } else {
         return super.isEnabled();
      }
   }

   public void enable() {
      Module live = this.materialize();
      if (live == null) {
         Ambience.LOGGER.error("Failed to load module {}", this.getName());
      } else {
         if (live != this) {
            live.enable();
         }

      }
   }

   public void disable() {
      Module live = this.materialize();
      if (live != null && live != this) {
         live.disable();
      }

   }

   private Module materialize() {
      return Ambience.moduleManager == null ? null : Ambience.moduleManager.ensureByName(this.getName());
   }
}
