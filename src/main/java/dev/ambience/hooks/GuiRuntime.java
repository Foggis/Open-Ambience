package dev.ambience.hooks;

import net.minecraft.class_437;

public final class GuiRuntime {
   private static volatile Host host;

   private GuiRuntime() {
   }

   public static void bind(Host next) {
      host = next;
   }

   public static Host get() {
      return host;
   }

   public static boolean modernBlur() {
      Host h = host;
      return h != null && h.modernBlur();
   }

   public static boolean isClickGui(class_437 screen) {
      Host h = host;
      return h != null && h.isClickGui(screen);
   }

   public static boolean handleBindMouse(int button, int action) {
      Host h = host;
      return h != null && h.handleBindMouse(button, action);
   }

   public static boolean clickGuiFont() {
      Host h = host;
      return h != null && h.clickGuiFont();
   }

   public static boolean hudFont() {
      Host h = host;
      return h != null && h.hudFont();
   }

   public static String fontName() {
      Host h = host;
      return h == null ? "" : h.fontName();
   }

   public static float rainbowSaturation() {
      Host h = host;
      return h == null ? 140.0F : h.rainbowSaturation();
   }

   public static float rainbowBrightness() {
      Host h = host;
      return h == null ? 200.0F : h.rainbowBrightness();
   }

   public static int rainbowHue() {
      Host h = host;
      return h == null ? 240 : h.rainbowHue();
   }

   public static boolean rainbowTheme() {
      Host h = host;
      return h != null && h.rainbowTheme();
   }

   public interface Host {
      boolean modernBlur();

      boolean isClickGui(class_437 var1);

      boolean handleBindMouse(int var1, int var2);

      boolean clickGuiFont();

      boolean hudFont();

      String fontName();

      float rainbowSaturation();

      float rainbowBrightness();

      int rainbowHue();

      boolean rainbowTheme();
   }
}
