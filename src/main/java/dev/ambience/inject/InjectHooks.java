package dev.ambience.inject;

import dev.ambience.Ambience;
import dev.ambience.event.impl.render.Render2DEvent;
import dev.ambience.event.impl.render.Render3DEvent;
import dev.ambience.features.gui.NativeClickGuiScreen;
import dev.ambience.hooks.FreecamHook;
import dev.ambience.util.RotationHelper;
import dev.ambience.util.SilentRotation;
import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_4587;

public final class InjectHooks {
   private InjectHooks() {
   }

   public static void fireRender3D(float delta) {
      if (Ambience.moduleManager != null) {
         try {
            Ambience.moduleManager.onRender3D(new Render3DEvent((class_4587)null, delta));
         } catch (Throwable var2) {
         }

      }
   }

   public static void fireRender2D(class_332 graphics, float delta) {
      if (Ambience.moduleManager != null && graphics != null) {
         try {
            Ambience.moduleManager.onRender2D(new Render2DEvent(graphics, delta));
         } catch (Throwable var3) {
         }

      }
   }

   public static void renderNativeOverlay(float delta) {
      try {
         NativeClickGuiScreen.renderNative(delta);
      } catch (Throwable t) {
         Ambience.LOGGER.error("Native ClickGui render failed", t);
      }

   }

   public static void cameraPick(float tickDelta) {
      class_310 minecraft = class_310.method_1551();
      if (minecraft.field_1724 != null && minecraft.field_1687 != null) {
         if (FreecamHook.isActive()) {
            applyPick(minecraft, FreecamHook.getCameraPos(tickDelta), FreecamHook.getPitch(), FreecamHook.getYaw());
         } else if (SilentRotation.isActive()) {
            applyPick(minecraft, minecraft.field_1724.method_5836(tickDelta), SilentRotation.getClientPitch(), SilentRotation.getClientYaw());
         }
      }
   }

   private static void applyPick(class_310 minecraft, class_243 eyes, float pitch, float yaw) {
      double blockReach = minecraft.field_1724.method_55754();
      double entityReach = minecraft.field_1724.method_55755();
      class_239 hit = RotationHelper.pick(minecraft.field_1687, minecraft.field_1724, eyes, pitch, yaw, blockReach, entityReach);
      if (hit == null) {
         class_243 look = RotationHelper.lookVector(pitch, yaw);
         class_243 end = eyes.method_1019(look.method_1021(blockReach));
         hit = class_3965.method_17778(end, class_2350.method_10142(look.field_1352, look.field_1351, look.field_1350), class_2338.method_49638(end));
      }

      minecraft.field_1765 = hit;
      class_1297 var10001;
      if (hit instanceof class_3966 entityHit) {
         var10001 = entityHit.method_17782();
      } else {
         var10001 = null;
      }

      minecraft.field_1692 = var10001;
   }
}
