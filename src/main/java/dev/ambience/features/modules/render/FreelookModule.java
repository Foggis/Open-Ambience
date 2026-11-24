package dev.ambience.features.modules.render;

import dev.ambience.features.modules.Module;
import dev.ambience.hooks.FreelookHook;
import dev.ambience.nativebridge.NativeModules;
import dev.ambience.util.SilentRotation;

public class FreelookModule extends Module {
   private static FreelookModule INSTANCE;

   public FreelookModule() {
      super("Freelook", "Look around without turning your player.", Module.Category.VISUALS);
      INSTANCE = this;
      FreelookHook.bind(new FreelookHook.Impl() {
         public boolean isActive() {
            return FreelookModule.isActive();
         }

         public float getYaw() {
            return FreelookModule.getYaw();
         }

         public float getPitch() {
            return FreelookModule.getPitch();
         }

         public void onTurn(double yawDelta, double pitchDelta) {
            FreelookModule.onTurn(yawDelta, pitchDelta);
         }

         public void absorbCamera(float yaw, float pitch) {
            FreelookModule.absorbCamera(yaw, pitch);
         }
      });
   }

   public static FreelookModule getInstance() {
      return INSTANCE;
   }

   public static boolean isActive() {
      FreelookModule module = INSTANCE;
      return module != null && NativeModules.freelookActive(module.isEnabled(), FreecamModule.isActive());
   }

   public static float getYaw() {
      float[] angles = INSTANCE == null ? null : NativeModules.freelookAngles();
      return angles != null && angles.length == 2 ? angles[0] : 0.0F;
   }

   public static float getPitch() {
      float[] angles = INSTANCE == null ? null : NativeModules.freelookAngles();
      return angles != null && angles.length == 2 ? angles[1] : 0.0F;
   }

   public static void onTurn(double yawDelta, double pitchDelta) {
      FreelookModule module = INSTANCE;
      if (module != null && isActive()) {
         NativeModules.freelookTurn(yawDelta, pitchDelta);
      }
   }

   public static void absorbCamera(float yaw, float pitch) {
      FreelookModule module = INSTANCE;
      if (module != null) {
         NativeModules.freelookAbsorb(module.isEnabled(), yaw, pitch);
      }

   }

   public String getDebugState() {
      if (!isActive()) {
         return null;
      } else {
         float[] angles = NativeModules.freelookAngles();
         return angles != null && angles.length == 2 ? String.format("yaw=%.1f pitch=%.1f", angles[0], angles[1]) : null;
      }
   }

   public void onEnable() {
      this.capture();
   }

   public void onDisable() {
      NativeModules.resetFreelook();
   }

   public void onTick() {
      if (nullCheck()) {
         this.disable();
      }

   }

   private void capture() {
      if (nullCheck()) {
         NativeModules.freelookCapture(this.isEnabled(), false, 0.0F, 0.0F);
      } else {
         float yaw;
         float pitch;
         if (FreecamModule.isActive()) {
            yaw = FreecamModule.getYaw();
            pitch = FreecamModule.getPitch();
         } else if (SilentRotation.isActive()) {
            yaw = SilentRotation.getClientYaw();
            pitch = SilentRotation.getClientPitch();
         } else {
            yaw = mc.field_1724.method_36454();
            pitch = mc.field_1724.method_36455();
         }

         NativeModules.freelookCapture(this.isEnabled(), true, yaw, pitch);
      }
   }
}
