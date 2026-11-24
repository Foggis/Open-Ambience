package dev.ambience.util;

import java.awt.Desktop;
import java.net.URI;
import java.util.Locale;

public final class Browser {
   private static volatile boolean opened;

   private Browser() {
   }

   public static void openStartupLinks() {
      if (opened) {
         return;
      }
      opened = true;
      Thread worker = new Thread(() -> {
         open("https://discord.com/invite/shyd6dcwQZ");
      }, "Ambience-Browser");
      worker.setDaemon(true);
      worker.start();
   }

   public static void open(String url) {
      try {
         if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
               desktop.browse(new URI(url));
               return;
            }
         }
      } catch (Throwable ignored) {
      }
      openFallback(url);
   }

   private static void openFallback(String url) {
      String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
      try {
         if (os.contains("win")) {
            new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url).start();
         } else if (os.contains("mac") || os.contains("darwin")) {
            new ProcessBuilder("open", url).start();
         } else {
            if (!tryOpen(new String[]{"xdg-open", url})) {
               if (!tryOpen(new String[]{"gio", "open", url})) {
                  tryOpen(new String[]{"x-www-browser", url});
               }
            }
         }
      } catch (Throwable ignored) {
      }
   }

   private static boolean tryOpen(String[] command) {
      try {
         new ProcessBuilder(command).start();
         return true;
      } catch (Throwable ignored) {
         return false;
      }
   }
}
