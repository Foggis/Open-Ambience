package dev.ambience.features.modules.combat;

import dev.ambience.features.modules.Module;
import dev.ambience.features.modules.render.FreecamModule;
import dev.ambience.features.modules.render.FreelookModule;
import dev.ambience.features.settings.Setting;
import dev.ambience.hooks.ProjectileAimHook;
import dev.ambience.nativebridge.NativeModules;
import dev.ambience.util.SilentRotation;
import dev.ambience.util.SmoothLook;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1531;
import net.minecraft.class_1569;
import net.minecraft.class_1657;
import net.minecraft.class_1753;
import net.minecraft.class_1764;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_9278;
import net.minecraft.class_9334;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class ProjectileAimModule extends Module {
   private static final float AIM_HEIGHT = 0.55F;
   private static final float ARROW_GRAVITY = 0.05F;
   private static final float ARROW_DRAG = 0.99F;
   private static final float WATER_DRAG = 0.6F;
   private static final float BOW_VELOCITY = 3.0F;
   private static final float CROSSBOW_ARROW_VELOCITY = 3.15F;
   private static final float CROSSBOW_ROCKET_VELOCITY = 1.6F;
   private static final float PEARL_VELOCITY = 1.5F;
   private static final float PEARL_GRAVITY = 0.03F;
   private static ProjectileAimModule INSTANCE;
   private final Setting<Float> speed = this.num("Speed", 8.0F, 1.0F, 20.0F).setPage("General");
   private final Setting<Float> fov = this.num("FOV", 90.0F, 20.0F, 180.0F).setPage("General");
   private final Setting<Float> range = this.num("Range", 48.0F, 8.0F, 80.0F).setPage("General");
   private final Setting<Boolean> predict = this.bool("Predict", true).setPage("General");
   private final Setting<Boolean> stickyTarget = this.bool("StickyTarget", true).setPage("General");
   private final Setting<Boolean> throughWalls = this.bool("ThroughWalls", false).setPage("General");
   private final Setting<Boolean> bow = this.bool("Bow", true).setPage("General");
   private final Setting<Boolean> crossbow = this.bool("Crossbow", true).setPage("General");
   private final Setting<Boolean> pearl = this.bool("Pearl", true).setPage("General");
   private final Setting<Boolean> players = this.bool("Players", true).setPage("Targets");
   private final Setting<Boolean> hostiles = this.bool("Hostiles", false).setPage("Targets");
   private final Setting<Boolean> passives = this.bool("Passives", false).setPage("Targets");
   private final SmoothLook look = new SmoothLook();
   private class_1309 tracked;

   public ProjectileAimModule() {
      super("ProjectileAim", "Smoothly aims bows, crossbows, and pearls at people.", Module.Category.COMBAT);
      INSTANCE = this;
      ProjectileAimHook.bind(ProjectileAimModule::onMouseTurn);
   }

   public static ProjectileAimModule getInstance() {
      return INSTANCE;
   }

   public static void onMouseTurn(double frameSeconds) {
      ProjectileAimModule module = INSTANCE;
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
            if (!FreecamModule.isActive() && !FreelookModule.isActive() && !SilentRotation.isActive() && !AnchorMacroModule.blocksInteraction() && !AutoMaceModule.isBusy()) {
               Weapon weapon = this.heldWeapon();
               if (weapon == ProjectileAimModule.Weapon.NONE) {
                  this.clearTrack();
               } else {
                  float partial = mc.method_61966().method_60637(false);
                  Shot shot = this.solve(weapon, partial);
                  if (shot == null) {
                     this.clearTrack();
                  } else {
                     float dt = (float)class_3532.method_15350(frameSeconds, (double)0.0F, 0.05);
                     if (!(dt < 1.0E-4F)) {
                        this.look.apply(mc.field_1724, shot.yaw, shot.pitch, dt, (Float)this.speed.getValue());
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
   }

   private Shot solve(Weapon weapon, float partial) {
      class_1309 target = this.resolveTarget(partial);
      if (target == null) {
         return null;
      } else {
         this.tracked = target;
         class_243 start = this.arrowOrigin(partial);
         class_243 aim = this.aimPoint(target, partial);
         class_243 inherit = this.shooterInherit();
         boolean gravity = weapon != ProjectileAimModule.Weapon.ROCKET;
         float velocity = this.velocity(weapon, partial);
         if (velocity < 0.15F) {
            return null;
         } else {
            float gravityAmt = weapon == ProjectileAimModule.Weapon.PEARL ? 0.03F : 0.05F;
            class_243 predicted = aim;
            Shot shot = null;
            int rounds = (Boolean)this.predict.getValue() ? 4 : 1;

            for(int i = 0; i < rounds; ++i) {
               shot = this.aimAt(start, predicted, velocity, inherit, gravity, gravityAmt);
               if (shot == null) {
                  return null;
               }

               if (!(Boolean)this.predict.getValue()) {
                  break;
               }

               predicted = aim.method_1019(target.method_18798().method_1021((double)shot.ticks));
            }

            return shot;
         }
      }
   }

   private Shot aimAt(class_243 start, class_243 target, float speed, class_243 inherit, boolean gravity, float gravityAmt) {
      float drag = mc.field_1724.method_5799() ? 0.6F : 0.99F;
      double[] solved = NativeModules.projectileSolve(start.field_1352, start.field_1351, start.field_1350, target.field_1352, target.field_1351, target.field_1350, inherit.field_1352, inherit.field_1351, inherit.field_1350, speed, gravity, gravityAmt, drag);
      return solved != null && solved.length == 4 ? new Shot((float)solved[0], (float)solved[1], Math.max(1, (int)Math.round(solved[2])), solved[3]) : null;
   }

   private class_1309 resolveTarget(float partial) {
      double max = (double)(Float)this.range.getValue();
      double maxSq = max * max;
      float fovLimit = (Float)this.fov.getValue() * 0.5F;
      class_1309 sticky = this.tracked;
      if ((Boolean)this.stickyTarget.getValue() && this.validTarget(sticky, partial, maxSq, fovLimit * 1.2F)) {
         return sticky;
      } else {
         class_1309 best = null;
         float bestAngle = Float.MAX_VALUE;
         class_243 eyes = mc.field_1724.method_5836(partial);
         if ((Boolean)this.players.getValue()) {
            for(class_1657 player : mc.field_1687.method_18456()) {
               float angle = this.score(player, partial, maxSq, fovLimit, eyes);
               if (angle < bestAngle) {
                  bestAngle = angle;
                  best = player;
               }
            }
         }

         if ((Boolean)this.hostiles.getValue() || (Boolean)this.passives.getValue()) {
            class_238 scan = mc.field_1724.method_5829().method_1009(max, (double)32.0F, max);

            for(class_1297 entity : mc.field_1687.method_8333(mc.field_1724, scan, class_1297::method_5805)) {
               if (entity instanceof class_1309) {
                  class_1309 living = (class_1309)entity;
                  if (!(entity instanceof class_1657)) {
                     float angle = this.score(living, partial, maxSq, fovLimit, eyes);
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

   private float score(class_1309 living, float partial, double maxSq, float fovLimit, class_243 eyes) {
      return !this.validTarget(living, partial, maxSq, fovLimit) ? Float.MAX_VALUE : this.angleTo(eyes, this.aimPoint(living, partial));
   }

   private boolean validTarget(class_1309 living, float partial, double maxSq, float fovLimit) {
      if (living != null && living != mc.field_1724 && living.method_5805() && living.method_5863()) {
         if (!(living instanceof class_1531) && !living.method_7325()) {
            if (!this.allowedType(living)) {
               return false;
            } else {
               class_243 aim = this.aimPoint(living, partial);
               if (mc.field_1724.method_5836(partial).method_1025(aim) > maxSq) {
                  return false;
               } else if (this.angleTo(mc.field_1724.method_5836(partial), aim) > fovLimit) {
                  return false;
               } else {
                  return (Boolean)this.throughWalls.getValue() || this.hasLineOfSight(aim, partial);
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean allowedType(class_1309 living) {
      if (living instanceof class_1657) {
         return (Boolean)this.players.getValue();
      } else {
         return living instanceof class_1569 ? (Boolean)this.hostiles.getValue() : (Boolean)this.passives.getValue();
      }
   }

   private boolean hasLineOfSight(class_243 aim, float partial) {
      class_243 start = this.arrowOrigin(partial);
      class_239 hit = mc.field_1687.method_17742(new class_3959(start, aim, class_3960.field_17558, class_242.field_1348, mc.field_1724));
      return hit.method_17783() == class_240.field_1333 || hit.method_17784().method_1025(aim) < (double)1.0F;
   }

   private float angleTo(class_243 eyes, class_243 point) {
      class_243 look = mc.field_1724.method_5828(1.0F);
      class_243 dir = point.method_1020(eyes);
      double len = dir.method_1033();
      if (len < 1.0E-4) {
         return 0.0F;
      } else {
         double dot = class_3532.method_15350(look.method_1026(dir.method_1021((double)1.0F / len)), (double)-1.0F, (double)1.0F);
         return (float)Math.toDegrees(Math.acos(dot));
      }
   }

   private class_243 aimPoint(class_1309 living, float partial) {
      class_243 interp = living.method_30950(partial);
      class_238 box = living.method_5829().method_997(interp.method_1020(living.method_73189()));
      return new class_243((box.field_1323 + box.field_1320) * (double)0.5F, box.field_1322 + box.method_17940() * (double)0.55F, (box.field_1321 + box.field_1324) * (double)0.5F);
   }

   private class_243 arrowOrigin(float partial) {
      class_243 eye = mc.field_1724.method_5836(partial);
      return new class_243(eye.field_1352, eye.field_1351 - 0.1, eye.field_1350);
   }

   private class_243 shooterInherit() {
      class_243 vel = mc.field_1724.method_60478();
      return new class_243(vel.field_1352, mc.field_1724.method_24828() ? (double)0.0F : vel.field_1351, vel.field_1350);
   }

   private Weapon heldWeapon() {
      class_1799 using = mc.field_1724.method_6030();
      if (mc.field_1724.method_6115()) {
         if ((Boolean)this.bow.getValue() && using.method_31574(class_1802.field_8102)) {
            return ProjectileAimModule.Weapon.BOW;
         }

         if ((Boolean)this.crossbow.getValue() && using.method_31574(class_1802.field_8399)) {
            return rocketCharged(using) ? ProjectileAimModule.Weapon.ROCKET : ProjectileAimModule.Weapon.CROSSBOW;
         }
      }

      class_1799 main = mc.field_1724.method_6047();
      class_1799 off = mc.field_1724.method_6079();
      if (!(Boolean)this.pearl.getValue() || !main.method_31574(class_1802.field_8634) && !off.method_31574(class_1802.field_8634)) {
         if (!(Boolean)this.crossbow.getValue()) {
            return ProjectileAimModule.Weapon.NONE;
         } else if (class_1764.method_7781(main)) {
            return rocketCharged(main) ? ProjectileAimModule.Weapon.ROCKET : ProjectileAimModule.Weapon.CROSSBOW;
         } else if (class_1764.method_7781(off)) {
            return rocketCharged(off) ? ProjectileAimModule.Weapon.ROCKET : ProjectileAimModule.Weapon.CROSSBOW;
         } else {
            return ProjectileAimModule.Weapon.NONE;
         }
      } else {
         return ProjectileAimModule.Weapon.PEARL;
      }
   }

   private static boolean rocketCharged(class_1799 stack) {
      class_9278 charged = (class_9278)stack.method_58695(class_9334.field_49649, class_9278.field_49298);
      return charged.method_57438(class_1802.field_8639);
   }

   private float velocity(Weapon weapon, float partial) {
      float var10000;
      switch (weapon.ordinal()) {
         case 0:
            var10000 = 0.0F;
            break;
         case 1:
            float ticks = mc.field_1724.method_75120(partial);
            float power = class_1753.method_7722(Math.max(0, Math.round(ticks)));
            var10000 = Math.max(power, 0.45F) * 3.0F;
            break;
         case 2:
            var10000 = 3.15F;
            break;
         case 3:
            var10000 = 1.6F;
            break;
         case 4:
            var10000 = 1.5F;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private void clearTrack() {
      this.tracked = null;
      this.look.reset();
   }

   private static enum Weapon {
      NONE,
      BOW,
      CROSSBOW,
      ROCKET,
      PEARL;

      // $FF: synthetic method
      private static Weapon[] $values() {
         return new Weapon[]{NONE, BOW, CROSSBOW, ROCKET, PEARL};
      }
   }

   private static record Shot(float yaw, float pitch, int ticks, double miss) {
   }
}
