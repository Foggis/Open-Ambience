package dev.ambience.features.modules.combat;

import dev.ambience.features.modules.Module;
import dev.ambience.features.modules.render.FreecamModule;
import dev.ambience.features.modules.render.FreelookModule;
import dev.ambience.features.settings.Setting;
import dev.ambience.hooks.AimAssistHook;
import dev.ambience.nativebridge.NativeModules;
import dev.ambience.util.GrimMovementGuard;
import dev.ambience.util.SilentRotation;
import dev.ambience.util.SmoothLook;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1531;
import net.minecraft.class_1569;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_3489;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_9334;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class AimAssistModule extends Module {
   private static AimAssistModule INSTANCE;
   private final Setting<Float> speed = this.num("Speed", 8.0F, 1.0F, 20.0F).setPage("General");
   private final Setting<Float> fov = this.num("FOV", 70.0F, 15.0F, 180.0F).setPage("General");
   private final Setting<Float> range = this.num("Range", 3.5F, 2.5F, 6.0F).setPage("General");
   private final Setting<Boolean> predict = this.bool("Predict", true).setPage("General");
   private final Setting<Boolean> stickyTarget = this.bool("StickyTarget", true).setPage("General");
   private final Setting<Boolean> throughWalls = this.bool("ThroughWalls", false).setPage("General");
   private final Setting<Boolean> horizontal = this.bool("Horizontal", false).setPage("General");
   private final Setting<Boolean> weaponsOnly = this.bool("WeaponsOnly", true).setPage("General");
   private final Setting<Boolean> onAttack = this.bool("OnAttack", false).setPage("General");
   private final Setting<Boolean> players = this.bool("Players", true).setPage("Targets");
   private final Setting<Boolean> hostiles = this.bool("Hostiles", false).setPage("Targets");
   private final Setting<Boolean> passives = this.bool("Passives", false).setPage("Targets");
   private final SmoothLook look = new SmoothLook();
   private class_1309 tracked;

   public AimAssistModule() {
      super("AimAssist", "Smooth close-range aim for swords and UHC.", Module.Category.COMBAT);
      INSTANCE = this;
      AimAssistHook.bind(AimAssistModule::onMouseTurn);
   }

   public static AimAssistModule getInstance() {
      return INSTANCE;
   }

   public static void onMouseTurn(double frameSeconds) {
      AimAssistModule module = INSTANCE;
      if (module != null && module.isEnabled()) {
         module.assist(frameSeconds);
      }

   }

   public String getDisplayInfo() {
      return this.tracked == null ? null : this.tracked.method_5477().getString();
   }

   public String getDebugState() {
      return this.getDisplayInfo();
   }

   public void onDisable() {
      this.clearTrack();
   }

   private void assist(double frameSeconds) {
      if (!nullCheck() && mc.field_1724 != null && mc.field_1687 != null && mc.field_1755 == null) {
         if (!mc.field_1724.method_7325() && mc.field_1724.method_5805()) {
            if (!FreecamModule.isActive() && !FreelookModule.isActive() && !AnchorMacroModule.blocksInteraction() && !AutoMaceModule.isBusy()) {
               if (SilentRotation.isActive() && !SilentRotation.ownedBy(SilentRotation.Owner.OMNI)) {
                  this.clearTrack();
               } else if (!mc.field_1724.method_6115() && !this.holdingRanged()) {
                  if ((Boolean)this.weaponsOnly.getValue() && !this.holdingMelee()) {
                     this.clearTrack();
                  } else if ((Boolean)this.onAttack.getValue() && !mc.field_1690.field_1886.method_1434()) {
                     this.clearTrack();
                  } else {
                     float partial = mc.method_61966().method_60637(false);
                     class_1309 target = this.resolveTarget(partial);
                     if (target == null) {
                        this.clearTrack();
                     } else {
                        this.tracked = target;
                        class_243 eyes = mc.field_1724.method_5836(partial);
                        class_243 point = this.aimPoint(target, partial, eyes);
                        float[] angles = GrimMovementGuard.anglesFrom(eyes, point);
                        if (angles == null) {
                           this.clearTrack();
                        } else {
                           float dt = (float)class_3532.method_15350(frameSeconds, (double)0.0F, 0.05);
                           boolean yawAxis = true;
                           boolean pitchAxis = !(Boolean)this.horizontal.getValue();
                           if (SilentRotation.ownedBy(SilentRotation.Owner.OMNI)) {
                              this.look.applySilentCamera(angles[0], angles[1], dt, (Float)this.speed.getValue(), yawAxis, pitchAxis);
                           } else {
                              this.look.apply(mc.field_1724, angles[0], angles[1], dt, (Float)this.speed.getValue(), yawAxis, pitchAxis);
                           }

                        }
                     }
                  }
               } else {
                  this.clearTrack();
               }
            } else {
               this.clearTrack();
            }
         } else {
            this.clearTrack();
         }
      } else {
         this.clearTrack();
      }
   }

   private class_1309 resolveTarget(float partial) {
      double max = (double)(Float)this.range.getValue();
      double maxSq = max * max;
      float fovLimit = (Float)this.fov.getValue() * 0.5F;
      class_243 eyes = mc.field_1724.method_5836(partial);
      if ((Boolean)this.stickyTarget.getValue() && this.validTarget(this.tracked, partial, eyes, maxSq, fovLimit * 1.2F)) {
         return this.tracked;
      } else {
         class_1309 best = null;
         float bestAngle = Float.MAX_VALUE;
         if ((Boolean)this.players.getValue()) {
            for(class_1657 player : mc.field_1687.method_18456()) {
               float angle = this.score(player, partial, eyes, maxSq, fovLimit);
               if (angle < bestAngle) {
                  bestAngle = angle;
                  best = player;
               }
            }
         }

         if ((Boolean)this.hostiles.getValue() || (Boolean)this.passives.getValue()) {
            class_238 scan = mc.field_1724.method_5829().method_1009(max, (double)8.0F, max);

            for(class_1297 entity : mc.field_1687.method_8333(mc.field_1724, scan, class_1297::method_5805)) {
               if (entity instanceof class_1309) {
                  class_1309 living = (class_1309)entity;
                  if (!(entity instanceof class_1657)) {
                     float angle = this.score(living, partial, eyes, maxSq, fovLimit);
                     if (angle < bestAngle) {
                        bestAngle = angle;
                        best = living;
                     }
                  }
               }
            }
         }

         return best;
      }
   }

   private float score(class_1309 living, float partial, class_243 eyes, double maxSq, float fovLimit) {
      return !this.validTarget(living, partial, eyes, maxSq, fovLimit) ? Float.MAX_VALUE : this.angleTo(eyes, this.aimPoint(living, partial, eyes));
   }

   private boolean validTarget(class_1309 living, float partial, class_243 eyes, double maxSq, float fovLimit) {
      if (living == null) {
         return false;
      } else {
         class_243 point = this.aimPoint(living, partial, eyes);
         float angle = this.angleTo(eyes, point);
         int flags = 1 | (living != mc.field_1724 ? 2 : 0) | (living.method_5805() ? 4 : 0) | (living.method_5863() ? 8 : 0) | (!(living instanceof class_1531) ? 16 : 0) | (!living.method_7325() ? 32 : 0) | (this.allowedType(living) ? 64 : 0) | (!(Boolean)this.throughWalls.getValue() && !this.hasLineOfSight(eyes, point) ? 0 : 128);
         return NativeModules.aimAssistTarget(flags, eyes.method_1025(point), maxSq, angle, fovLimit);
      }
   }

   private boolean allowedType(class_1309 living) {
      if (living instanceof class_1657) {
         return (Boolean)this.players.getValue();
      } else {
         return living instanceof class_1569 ? (Boolean)this.hostiles.getValue() : (Boolean)this.passives.getValue();
      }
   }

   private class_243 aimPoint(class_1309 living, float partial, class_243 eyes) {
      class_238 box = interpolatedBox(living, partial);
      if ((Boolean)this.predict.getValue()) {
         box = box.method_997(living.method_18798());
      }

      return closestPoint(box, eyes);
   }

   private static class_238 interpolatedBox(class_1309 living, float partial) {
      class_243 interp = living.method_30950(partial);
      return living.method_5829().method_997(interp.method_1020(living.method_73189()));
   }

   private static class_243 closestPoint(class_238 box, class_243 point) {
      return new class_243(class_3532.method_15350(point.field_1352, box.field_1323, box.field_1320), class_3532.method_15350(point.field_1351, box.field_1322, box.field_1325), class_3532.method_15350(point.field_1350, box.field_1321, box.field_1324));
   }

   private boolean hasLineOfSight(class_243 start, class_243 end) {
      class_239 hit = mc.field_1687.method_17742(new class_3959(start, end, class_3960.field_17558, class_242.field_1348, mc.field_1724));
      return hit.method_17783() == class_240.field_1333 || hit.method_17784().method_1025(end) < 0.36;
   }

   private float angleTo(class_243 eyes, class_243 point) {
      class_243 look = this.cameraLook();
      return NativeModules.aimAssistScore(eyes.field_1352, eyes.field_1351, eyes.field_1350, point.field_1352, point.field_1351, point.field_1350, look.field_1352, look.field_1351, look.field_1350);
   }

   private class_243 cameraLook() {
      if (SilentRotation.ownedBy(SilentRotation.Owner.OMNI)) {
         float yaw = SilentRotation.getClientYaw();
         float pitch = SilentRotation.getClientPitch();
         float yawRad = yaw * ((float)Math.PI / 180F);
         float pitchRad = pitch * ((float)Math.PI / 180F);
         float cosPitch = class_3532.method_15362((double)pitchRad);
         return new class_243((double)(-class_3532.method_15374((double)yawRad) * cosPitch), (double)(-class_3532.method_15374((double)pitchRad)), (double)(class_3532.method_15362((double)yawRad) * cosPitch));
      } else {
         return mc.field_1724.method_5828(1.0F);
      }
   }

   private boolean holdingMelee() {
      class_1799 stack = mc.field_1724.method_6047();
      return stack.method_31573(class_3489.field_42611) || stack.method_31573(class_3489.field_42612) || stack.method_31574(class_1802.field_49814) || stack.method_31573(class_3489.field_63257) || stack.method_57826(class_9334.field_63631);
   }

   private boolean holdingRanged() {
      class_1799 main = mc.field_1724.method_6047();
      class_1799 off = mc.field_1724.method_6079();
      class_1799 using = mc.field_1724.method_6030();
      return main.method_31574(class_1802.field_8102) || main.method_31574(class_1802.field_8399) || off.method_31574(class_1802.field_8102) || off.method_31574(class_1802.field_8399) || using.method_31574(class_1802.field_8102) || using.method_31574(class_1802.field_8399);
   }

   private void clearTrack() {
      this.tracked = null;
      this.look.reset();
   }
}
