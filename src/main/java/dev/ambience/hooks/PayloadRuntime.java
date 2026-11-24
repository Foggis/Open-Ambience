package dev.ambience.hooks;

import dev.ambience.features.modules.Module;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PayloadRuntime {
   private static final Logger LOGGER = LoggerFactory.getLogger("PayloadLoader");
   private static volatile ClassLoader modules;
   private static volatile Fetcher fetcher;

   private PayloadRuntime() {
   }

   public static void bindLoader(ClassLoader loader) {
      modules = loader;
   }

   public static void bindFetcher(Fetcher next) {
      fetcher = next;
   }

   public static void clear() {
      modules = null;
      fetcher = null;
   }

   public static void fetchModule(String moduleName) {
      Fetcher f = fetcher;
      if (f != null && moduleName != null && !moduleName.isBlank()) {
         f.fetch(moduleName);
      }
   }

   public static void fetchModules(List<String> moduleNames) {
      Fetcher f = fetcher;
      if (f != null && moduleNames != null && !moduleNames.isEmpty()) {
         f.fetchMany(moduleNames);
      }
   }

   public static Module createModule(String className) {
      if (className != null && !className.isBlank()) {
         ClassLoader loader = modules;
         if (loader == null) {
            loader = PayloadRuntime.class.getClassLoader();
         }

         try {
            Class<?> clazz = Class.forName(className, true, loader);
            return (Module)clazz.getDeclaredConstructor().newInstance();
         } catch (Throwable t) {
            LOGGER.error("Failed to instantiate payload class {}", className, t);
            return null;
         }
      } else {
         return null;
      }
   }

   public static Module createModule(String moduleName, String className) {
      fetchModule(moduleName);
      return createModule(className);
   }

   public interface Fetcher {
      void fetch(String var1);

      void fetchMany(List<String> var1);
   }
}
