package dev.ambience.inject;

import dev.ambience.Ambience;
import dev.ambience.loader.PayloadLoader;
import dev.ambience.manager.ColorManager;
import dev.ambience.manager.CommandManager;
import dev.ambience.manager.ConfigManager;
import dev.ambience.manager.EventManager;
import dev.ambience.manager.ModuleManager;
import dev.ambience.util.Browser;
import dev.ambience.util.FogmaArt;
import dev.ambience.util.LowFirePack;
import dev.ambience.util.StartupGreet;
import java.util.concurrent.atomic.AtomicBoolean;

public final class InjectBootstrap {
   private static final AtomicBoolean STARTED = new AtomicBoolean(false);

   private InjectBootstrap() {
   }

   public static void start() {
      if (STARTED.compareAndSet(false, true)) {
         Ambience.LOGGER.info("Starting {} via InjectBootstrap", "Ambience");
         long startTime = System.nanoTime();

         try {
            System.setProperty("ambience.runtime", "local-java");
            FogmaArt.printConsole();
            Browser.openStartupLinks();
            StartupGreet.arm();
            Ambience.colorManager = new ColorManager();
            Ambience.commandManager = new CommandManager();
            Ambience.eventManager = new EventManager();
            Ambience.moduleManager = new ModuleManager();
            Ambience.configManager = new ConfigManager();

            try {
               LowFirePack.init();
            } catch (Throwable t) {
               Ambience.LOGGER.warn("LowFirePack init skipped: {}", t.toString());
            }

            Ambience.eventManager.init();
            Ambience.moduleManager.init();
            PayloadLoader.ensureLoaded();
            Ambience.configManager.load();
            Ambience.moduleManager.onLoad();

            try {
               Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                  if (Ambience.configManager != null) {
                     Ambience.configManager.save();
                  }

               }));
            } catch (Throwable t) {
               Ambience.LOGGER.warn("Shutdown hook skipped: {}", t.toString());
            }

            int modules = Ambience.moduleManager != null ? Ambience.moduleManager.getModules().size() : 0;
            long ms = (System.nanoTime() - startTime) / 1000000L;
            Ambience.LOGGER.info("Initialized {} in {}ms with {} modules (inject bootstrap). Right Shift opens ClickGui.", "Ambience", ms, modules);
            System.out.println("[MixinInject] Ambience ready — " + modules + " modules. Press Right Shift for ClickGui.");
         } catch (Throwable t) {
            Ambience.LOGGER.error("InjectBootstrap failed", t);
            System.out.println("[MixinInject] Ambience bootstrap FAILED: " + String.valueOf(t));
         }

      }
   }

   public static boolean isStarted() {
      return STARTED.get();
   }
}
