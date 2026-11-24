package dev.ambience.util;

import dev.ambience.Ambience;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3283;
import net.minecraft.class_3288;

public final class LowFirePack {
   public static final class_2960 PACK_ID = class_2960.method_60655("ambience", "lowfire");
   private static final String FOLDER_NAME = "ambience-lowfire";
   private static final String MARKER = ".ambience-lowfire-v2";
   private static boolean fabricBuiltin;

   private LowFirePack() {
   }

   public static void init() {
      fabricBuiltin = false;

      try {
         Optional<ModContainer> mod = FabricLoader.getInstance().getModContainer("ambience");
         if (mod.isPresent()) {
            fabricBuiltin = ResourceManagerHelper.registerBuiltinResourcePack((net.minecraft.util.Identifier)(Object)PACK_ID, (ModContainer)mod.get(), (net.minecraft.text.Text)(Object)class_2561.method_43470("Ambience Low Fire"), ResourcePackActivationType.NORMAL);
            Ambience.LOGGER.info("LowFire Fabric builtin pack registered={}", fabricBuiltin);
         } else {
            Ambience.LOGGER.info("LowFire: no ambience ModContainer (inject) — using folder pack");
         }
      } catch (Throwable t) {
         Ambience.LOGGER.warn("LowFire Fabric builtin skipped: {}", t.toString());
      }

   }

   public static void setEnabled(boolean enabled) {
      class_310 mc = class_310.method_1551();
      if (mc != null && mc.method_1520() != null) {
         boolean refreshed;
         try {
            refreshed = materializeFolderPack(mc);
         } catch (Exception e) {
            Ambience.LOGGER.error("Failed to materialize LowFire pack", e);
            return;
         }

         class_3283 repository = mc.method_1520();
         repository.method_14445();
         String packId = resolvePackId(repository);
         if (packId == null) {
            Ambience.LOGGER.warn("Low fire resource pack was not found after materialize. available={}", repository.method_29206());
         } else {
            List<String> selected = new ArrayList(repository.method_29210());
            boolean contains = selected.contains(packId);
            if (enabled && !contains) {
               selected.add(packId);
            } else if (!enabled && contains) {
               selected.remove(packId);
            } else if (!enabled || !refreshed) {
               return;
            }

            repository.method_14447(selected);
            mc.method_1521();
            Ambience.LOGGER.info("Low fire resource pack {} ({})", enabled ? "enabled" : "disabled", packId);
         }
      }
   }

   private static boolean materializeFolderPack(class_310 mc) throws IOException, URISyntaxException {
      Path dest = mc.field_1697.toPath().resolve("resourcepacks").resolve("ambience-lowfire");
      Path marker = dest.resolve(".ambience-lowfire-v2");
      if (Files.isRegularFile(marker, new LinkOption[0]) && Files.isDirectory(dest.resolve("assets"), new LinkOption[0])) {
         try {
            String meta = Files.readString(dest.resolve("pack.mcmeta"));
            if (meta.contains("\"pack_format\": 75") || meta.contains("\"pack_format\":75")) {
               return false;
            }
         } catch (IOException var15) {
         }
      }

      if (Files.exists(dest, new LinkOption[0])) {
         deleteRecursive(dest);
      }

      Files.createDirectories(dest);
      URL rootUrl = LowFirePack.class.getClassLoader().getResource("resourcepacks/lowfire/pack.mcmeta");
      if (rootUrl == null) {
         throw new IOException("classpath resourcepacks/lowfire/pack.mcmeta missing");
      } else {
         URI rootUri = rootUrl.toURI();
         if ("jar".equals(rootUri.getScheme())) {
            String full = rootUri.toString();
            int sep = full.indexOf("!/");
            URI jarUri = URI.create(full.substring(0, sep));
            String inside = full.substring(sep + 2);
            String prefix = inside.substring(0, inside.lastIndexOf(47) + 1);
            FileSystem fs = FileSystems.newFileSystem(jarUri, Collections.emptyMap());

            try {
               copyTree(fs.getPath(prefix), dest);
            } catch (Throwable var14) {
               if (fs != null) {
                  try {
                     fs.close();
                  } catch (Throwable var13) {
                     var14.addSuppressed(var13);
                  }
               }

               throw var14;
            }

            if (fs != null) {
               fs.close();
            }
         } else {
            copyTree(Path.of(rootUri).getParent(), dest);
         }

         Files.writeString(marker, "v2\n");
         Ambience.LOGGER.info("Materialized LowFire pack at {}", dest);
         return true;
      }
   }

   private static void copyTree(final Path from, final Path to) throws IOException {
      Files.walkFileTree(from, new SimpleFileVisitor<Path>() {
         public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            Path rel = from.relativize(dir);
            Files.createDirectories(to.resolve(rel.toString()));
            return FileVisitResult.CONTINUE;
         }

         public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Path rel = from.relativize(file);
            Path target = to.resolve(rel.toString());
            Files.createDirectories(target.getParent());
            InputStream in = Files.newInputStream(file);

            try {
               OutputStream out = Files.newOutputStream(target);

               try {
                  in.transferTo(out);
               } catch (Throwable var11) {
                  if (out != null) {
                     try {
                        out.close();
                     } catch (Throwable var10) {
                        var11.addSuppressed(var10);
                     }
                  }

                  throw var11;
               }

               if (out != null) {
                  out.close();
               }
            } catch (Throwable var12) {
               if (in != null) {
                  try {
                     in.close();
                  } catch (Throwable var9) {
                     var12.addSuppressed(var9);
                  }
               }

               throw var12;
            }

            if (in != null) {
               in.close();
            }

            return FileVisitResult.CONTINUE;
         }
      });
   }

   private static void deleteRecursive(Path root) throws IOException {
      if (Files.exists(root, new LinkOption[0])) {
         Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
               Files.deleteIfExists(file);
               return FileVisitResult.CONTINUE;
            }

            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
               Files.deleteIfExists(dir);
               return FileVisitResult.CONTINUE;
            }
         });
      }
   }

   private static String resolvePackId(class_3283 repository) {
      for(String id : repository.method_29206()) {
         if (id.endsWith("ambience-lowfire") || id.contains("ambience-lowfire")) {
            return id;
         }
      }

      String preferred = PACK_ID.toString();
      if (repository.method_29206().contains(preferred)) {
         return preferred;
      } else {
         for(class_3288 pack : repository.method_14441()) {
            String id = pack.method_14463();
            if (id.contains("lowfire") || id.endsWith("/lowfire")) {
               return id;
            }
         }

         return null;
      }
   }
}
