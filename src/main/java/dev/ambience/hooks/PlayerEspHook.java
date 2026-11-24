package dev.ambience.hooks;

public final class PlayerEspHook {
   private static volatile Impl impl;

   private PlayerEspHook() {
   }

   public static void bind(Impl next) {
      impl = next;
   }

   public static boolean applies(int entityId) {
      Impl current = impl;
      return current != null && current.applies(entityId);
   }

   public static int fillColor() {
      Impl current = impl;
      return current == null ? 1895793951 : current.fillColor();
   }

   public static int outlineColor() {
      Impl current = impl;
      return current == null ? -19620 : current.outlineColor();
   }

   public interface Impl {
      boolean applies(int var1);

      int fillColor();

      int outlineColor();
   }
}
