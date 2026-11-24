package dev.ambience.loader;

import dev.ambience.Ambience;
import dev.ambience.features.modules.Module;
import dev.ambience.hooks.PayloadRuntime;
import dev.ambience.manager.ModuleManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PayloadLoader {
   private static final Logger LOGGER = LoggerFactory.getLogger("PayloadLoader");
   private static final String MODULE_INDEX = "/payload/modules.index";
   private static final String MODULE_META = "/payload/modules.meta";
   private static boolean loaded;

   private PayloadLoader() {
   }

   public static synchronized void unloadFromMemory() {
      PayloadRuntime.clear();
      loaded = false;
      LOGGER.info("Payload dropped from memory.");
   }

   public static synchronized void ensureLoaded() {
      if (loaded) {
         return;
      }
      loaded = true;
      try {
         ClassLoader own = PayloadLoader.class.getClassLoader();
         PayloadRuntime.bindLoader(own);
         registerModules();
         LOGGER.info("Payload ready (local mode, no auth).");
      } catch (Exception e) {
         loaded = false;
         LOGGER.error("Failed to load local payload", e);
      }
   }

   public static long accountUid() {
      return 69420L;
   }

   private static void registerModules() {
      if (Ambience.moduleManager == null) {
         return;
      }
      String index = readText(MODULE_INDEX);
      Ambience.moduleManager.offerCatalog(parseCatalog(readText(MODULE_META)));
      int registered = 0;
      for (String line : index.split("\n")) {
         String className = line.trim();
         if (className.isEmpty() || className.startsWith("#")) {
            continue;
         }
         Module module = PayloadRuntime.createModule(className);
         if (module != null) {
            Ambience.moduleManager.register(module);
            registered++;
         }
      }
      LOGGER.info("Payload: registered {} module(s) (local classpath).", registered);
   }

   private static String readText(String resource) {
      try {
         InputStream in = PayloadLoader.class.getResourceAsStream(resource);
         if (in == null) {
            LOGGER.error("Payload catalog missing: {}", resource);
            return "";
         }
         try {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
         } finally {
            in.close();
         }
      } catch (IOException e) {
         LOGGER.error("Failed to read {}", resource, e);
         return "";
      }
   }

   private static List<ModuleManager.CatalogEntry> parseCatalog(String meta) {
      List<ModuleManager.CatalogEntry> out = new ArrayList();
      for (String line : meta.split("\n")) {
         String trimmed = line.trim();
         if (trimmed.isEmpty() || trimmed.startsWith("#")) {
            continue;
         }
         String[] parts = trimmed.split("\\|", 4);
         if (parts.length >= 3) {
            try {
               Module.Category category = Module.Category.valueOf(parts[2].trim());
               String description = parts.length > 3 ? parts[3] : "";
               out.add(new ModuleManager.CatalogEntry(parts[0].trim(), parts[1].trim(), category, description));
            } catch (IllegalArgumentException ignored) {
            }
         }
      }
      return out;
   }
}
