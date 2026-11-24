package dev.ambience.features.modules.render;

import dev.ambience.features.modules.Module;
import dev.ambience.features.settings.Setting;
import dev.ambience.hooks.FreecamHook;
import dev.ambience.inject.Access;
import dev.ambience.nativebridge.NativeModules;
import dev.ambience.util.SilentRotation;
import net.minecraft.class_10185;
import net.minecraft.class_241;
import net.minecraft.class_243;
import net.minecraft.class_3532;

public class FreecamModule extends Module {
   private static FreecamModule INSTANCE;
   private static final class_10185 NO_INPUT = new class_10185(false, false, false, false, false, false, false);
   private final Setting<Double> speed = this.num("Speed", (double)1.0F, 0.1, (double)5.0F).setPage("General");
   private double x;
   private double y;
   private double z;
   private double prevX;
   private double prevY;
   private double prevZ;
   private float yaw;
   private float pitch;
   private boolean flying;

   public FreecamModule() {
      super("Freecam", "Detach the camera and fly around. Your player stays put.", Module.Category.VISUALS);
      INSTANCE = this;
      FreecamHook.bind(new FreecamHook.Impl() {
         public boolean isActive() {
            return FreecamModule.isActive();
         }

         public boolean blocksInteraction() {
            return FreecamModule.blocksInteraction();
         }

         public float getYaw() {
            return FreecamModule.getYaw();
         }

         public float getPitch() {
            return FreecamModule.getPitch();
         }

         public class_243 getCameraPos(float partialTick) {
            return FreecamModule.getCameraPos(partialTick);
         }

         public void onTurn(double yawDelta, double pitchDelta) {
            FreecamModule.onTurn(yawDelta, pitchDelta);
         }

         public void freezeInput() {
            FreecamModule.freezeInput();
         }
      });
   }

   public static FreecamModule getInstance() {
      return INSTANCE;
   }

   public static boolean isActive() {
      FreecamModule module = INSTANCE;
      return module != null && module.isEnabled() && module.flying;
   }

   public static boolean blocksInteraction() {
      return isActive();
   }

   public static float getYaw() {
      return INSTANCE == null ? 0.0F : INSTANCE.yaw;
   }

   public static float getPitch() {
      return INSTANCE == null ? 0.0F : INSTANCE.pitch;
   }

   public static class_243 getCameraPos(float partialTick) {
      FreecamModule module = INSTANCE;
      return module == null ? class_243.field_1353 : new class_243(class_3532.method_16436((double)partialTick, module.prevX, module.x), class_3532.method_16436((double)partialTick, module.prevY, module.y), class_3532.method_16436((double)partialTick, module.prevZ, module.z));
   }

   public static void onTurn(double yawDelta, double pitchDelta) {
      FreecamModule module = INSTANCE;
      if (module != null && isActive()) {
         module.yaw += (float)yawDelta * 0.15F;
         module.pitch += (float)pitchDelta * 0.15F;
         module.yaw = class_3532.method_15393(module.yaw);
         module.pitch = class_3532.method_15363(module.pitch, -90.0F, 90.0F);
      }
   }

   public static void freezeInput() {
      if (isActive() && mc.field_1724 != null && mc.field_1724.field_3913 != null) {
         mc.field_1724.field_3913.field_54155 = NO_INPUT;
         Access.setMoveVector(mc.field_1724.field_3913, class_241.field_1340);
      }
   }

   public String getDebugState() {
      return this.flying ? String.format("%.1f %.1f %.1f", this.x, this.y, this.z) : null;
   }

   public String getDisplayInfo() {
      return String.format("%.1f", this.speed.getValue());
   }

   public void onEnable() {
      if (nullCheck()) {
         this.flying = false;
      } else {
         class_243 eyes = mc.field_1724.method_33571();
         this.prevX = this.x = eyes.field_1352;
         this.prevY = this.y = eyes.field_1351;
         this.prevZ = this.z = eyes.field_1350;
         if (FreelookModule.getInstance() != null && FreelookModule.getInstance().isEnabled()) {
            this.yaw = FreelookModule.getYaw();
            this.pitch = FreelookModule.getPitch();
         } else if (SilentRotation.isActive()) {
            this.yaw = SilentRotation.getClientYaw();
            this.pitch = SilentRotation.getClientPitch();
         } else {
            this.yaw = mc.field_1724.method_36454();
            this.pitch = mc.field_1724.method_36455();
         }

         this.flying = true;
      }
   }

   public void onDisable() {
      this.flying = false;
   }

   public void onPreTick() {
      if (nullCheck()) {
         this.disable();
      } else {
         this.prevX = this.x;
         this.prevY = this.y;
         this.prevZ = this.z;
         if (mc.field_1755 == null) {
            float forward = NativeModules.omniImpulse(mc.field_1690.field_1894.method_1434(), mc.field_1690.field_1881.method_1434());
            float strafe = NativeModules.omniImpulse(mc.field_1690.field_1913.method_1434(), mc.field_1690.field_1849.method_1434());
            float vertical = NativeModules.omniImpulse(mc.field_1690.field_1903.method_1434(), mc.field_1690.field_1832.method_1434());
            if (forward != 0.0F || strafe != 0.0F || vertical != 0.0F) {
               double scale = (Double)this.speed.getValue() * (double)0.5F;
               if (mc.field_1690.field_1867.method_1434()) {
                  scale *= (double)2.0F;
               }

               double[] delta = NativeModules.freecamDelta(this.yaw, this.pitch, forward, strafe, vertical, scale);
               if (delta != null && delta.length == 3) {
                  this.x += delta[0];
                  this.y += delta[1];
                  this.z += delta[2];
               }

            }
         }
      }
   }
}
