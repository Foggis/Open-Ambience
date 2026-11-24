package dev.ambience.util;

public final class FogmaArt {
   public static final String[] LINES = {
      "Hi From Fogma!"
   };

   private FogmaArt() {
   }

   public static void printConsole() {
      for (String line : LINES) {
         System.out.println(line);
      }
   }
}
