package dev.ambience;

import dev.ambience.hooks.GuiRuntime;
import dev.ambience.hooks.HookClear;
import dev.ambience.inject.InjectBootstrap;
import dev.ambience.loader.PayloadLoader;
import dev.ambience.manager.ColorManager;
import dev.ambience.manager.CommandManager;
import dev.ambience.manager.ConfigManager;
import dev.ambience.manager.EventManager;
import dev.ambience.manager.ModuleManager;
import dev.ambience.util.SilentRotation;
import java.util.concurrent.atomic.AtomicBoolean;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.class_310;
import net.minecraft.class_437;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Ambience implements ModInitializer, ClientModInitializer {
   public static final Logger LOGGER = LogManager.getLogger("Ambience");
   public static ColorManager colorManager;
   public static EventManager eventManager;
   public static CommandManager commandManager;
   public static ModuleManager moduleManager;
   public static ConfigManager configManager;
   private static final AtomicBoolean IN_MEMORY = new AtomicBoolean(true);

   public static boolean isInMemory() {
      return IN_MEMORY.get();
   }

   public static void unloadFromMemory(String reason) {
      if (IN_MEMORY.compareAndSet(true, false)) {
         LOGGER.info("Unloading {} from memory ({})", "Ambience", reason == null ? "manual" : reason);

         try {
            class_310 mc = class_310.method_1551();
            if (mc != null && mc.field_1755 != null && GuiRuntime.isClickGui(mc.field_1755)) {
               mc.method_1507((class_437)null);
            }
         } catch (Throwable var7) {
         }

         try {
            if (configManager != null) {
               configManager.save();
            }
         } catch (Throwable t) {
            LOGGER.warn("Config save during unload failed: {}", t.toString());
         }

         try {
            if (moduleManager != null) {
               moduleManager.evictPayloadFromMemory();
            }
         } catch (Throwable t) {
            LOGGER.warn("Module evict failed: {}", t.toString());
         }

         try {
            PayloadLoader.unloadFromMemory();
         } catch (Throwable t) {
            LOGGER.warn("Payload unload failed: {}", t.toString());
         }

         try {
            HookClear.clearAll();
         } catch (Throwable t) {
            LOGGER.warn("Hook clear failed: {}", t.toString());
         }

         try {
            SilentRotation.forceClear();
         } catch (Throwable var2) {
         }

         boolean stripped = tryAgentUnload();
         LOGGER.info("{} features dropped from memory{} (disable Unload to reload).", "Ambience", stripped ? " and inject mixins stripped" : "");
      }
   }

   public static void reloadIntoMemory() {
      if (IN_MEMORY.compareAndSet(false, true)) {
         LOGGER.info("Loading {} back into memory", "Ambience");

         try {
            tryAgentReapply();
            PayloadLoader.ensureLoaded();
            if (configManager != null) {
               configManager.load();
            }

            if (moduleManager != null) {
               moduleManager.onLoad();
            }
         } catch (Throwable t) {
            IN_MEMORY.set(false);
            LOGGER.error("Failed to reload {} into memory", "Ambience", t);
         }

      }
   }

   private static boolean tryAgentUnload() {
      return false;
   }

   private static void tryAgentReapply() {
   }

   public void onInitialize() {
      InjectBootstrap.start();
   }

   public void onInitializeClient() {
      InjectBootstrap.start();
   }
}
