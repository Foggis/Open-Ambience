package dev.ambience.features.modules.combat;

import dev.ambience.Ambience;
import dev.ambience.features.modules.Module;
import dev.ambience.features.modules.movement.LungeSwapModule;
import dev.ambience.features.modules.movement.PearlCatchModule;
import dev.ambience.features.modules.movement.WindHopModule;
import dev.ambience.features.modules.render.FreecamModule;
import dev.ambience.features.settings.Setting;
import dev.ambience.nativebridge.NativeModules;
import dev.ambience.util.GrimMovementGuard;
import dev.ambience.util.ModuleDebug;
import dev.ambience.util.RotationHelper;
import net.minecraft.class_1268;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1511;
import net.minecraft.class_1531;
import net.minecraft.class_1569;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_239;
import net.minecraft.class_3489;
import net.minecraft.class_3966;
import org.joml.Vector2f;

public class TBotModule extends Module {
   private final Setting<Vector2f> delay = this.vec2f("Delay", 0.0F, 0.0F).setVec2TrackBounds(0.0F, 250.0F).setPage("General");
   private final Setting<Boolean> tpsSync = this.bool("TpsSync", true).setPage("General");
   private final Setting<Boolean> stickyTarget = this.bool("StickyTarget", false).setPage("General");
   private final Setting<Boolean> pauseInScreen = this.bool("PauseInScreen", true).setPage("General");
   private final Setting<Float> range = this.num("Range", 3.0F, 2.5F, 3.0F).setPage("General");
   private final Setting<Boolean> players = this.bool("Players", true).setPage("Targets");
   private final Setting<Boolean> hostiles = this.bool("Hostiles", true).setPage("Targets");
   private final Setting<Boolean> passives = this.bool("Passives", false).setPage("Targets");
   private final Setting<Boolean> crystals = this.bool("Crystals", true).setPage("Targets");
   private final Setting<Boolean> swordCooldown = this.bool("SwordCooldown", true).setPage("Sword");
   private final Setting<Float> perfectHit = this.num("PerfectHit", 100.0F, 0.0F, 100.0F).setPage("Sword").setVisibility((v) -> (Boolean)this.swordCooldown.getValue());
   private final Setting<Integer> critWait = this.num("CritWait", 4, 0, 10).setPage("Sword");
   private final Setting<Boolean> crystalMode = this.bool("CrystalMode", false).setPage("General");
   private final Setting<Boolean> duringAnchor = this.bool("DuringAnchor", false).setPage("General");
   private class_1297 tracked;

   public TBotModule() {
      super("TBot", "Attacks the entity under your crosshair when in range.", Module.Category.COMBAT);
   }

   public String getDisplayInfo() {
      Vector2f value = this.delay.getValue();
      String delayInfo;
      if (Math.round(value.x) == Math.round(value.y)) {
         delayInfo = Math.round(value.x) + "ms";
      } else {
         delayInfo = String.format("%.0f-%.0fms", value.x(), value.y());
      }

      return (Boolean)this.swordCooldown.getValue() && (Float)this.perfectHit.getValue() < 100.0F ? delayInfo + " " + Math.round((Float)this.perfectHit.getValue()) + "%" : delayInfo;
   }

   public String getDebugState() {
      if (this.tracked == null) {
         return null;
      } else {
         String base = this.tracked.method_5477().getString();
         int perfect = NativeModules.tbotPerfectState();
         return perfect >= 0 ? base + (perfect != 0 ? " perfect" : " early") : base;
      }
   }

   public void onDisable() {
      this.tracked = null;
      NativeModules.resetTbot(true);
   }

   public void onPreTick() {
      long now = System.currentTimeMillis();
      NativeModules.tbotObserveTick(now);
      if (!nullCheck() && mc.field_1761 != null) {
         if ((Boolean)this.pauseInScreen.getValue() && mc.field_1755 != null) {
            this.resetTarget();
         } else if (!FreecamModule.blocksInteraction() && (!AnchorMacroModule.blocksInteraction() || (Boolean)this.duringAnchor.getValue()) && !AutoMaceModule.isBusy() && !AutoEscModule.isBusy() && !AutoWebModule.isBusy() && !ShieldStunModule.isBusy() && !LungeSwapModule.isBusy() && !WindHopModule.isBusy() && !PearlCatchModule.isBusy() && !WTapModule.isBusy() && !STapModule.isBusy()) {
            if (mc.field_1724.method_6115()) {
               this.resetTarget();
            } else {
               class_1297 target = this.resolveTarget();
               if (target == null) {
                  this.resetTarget();
               } else {
                  this.tracked = target;
                  int combatInput = 0;
                  if (target instanceof class_1511) {
                     combatInput |= 1;
                  }

                  if ((Boolean)this.crystalMode.getValue()) {
                     combatInput |= 2;
                  }

                  if (mc.field_1724.field_6235 > 0) {
                     combatInput |= 4;
                  }

                  if ((Boolean)this.swordCooldown.getValue()) {
                     combatInput |= 8;
                  }

                  if (this.holdingMelee()) {
                     combatInput |= 16;
                  }

                  if (mc.field_1724.method_24828()) {
                     combatInput |= 32;
                  }

                  if (mc.field_1724.method_31549().field_7479) {
                     combatInput |= 64;
                  }

                  if (this.canCrit()) {
                     combatInput |= 128;
                  }

                  if ((Boolean)this.tpsSync.getValue()) {
                     combatInput |= 256;
                  }

                  int delayMin = Math.max(0, Math.round(Math.min((this.delay.getValue()).x, (this.delay.getValue()).y)));
                  int delayMax = Math.max(delayMin, Math.round(Math.max((this.delay.getValue()).x, (this.delay.getValue()).y)));
                  int action = NativeModules.tbotStep(target.method_5628(), combatInput, mc.field_1724.method_7261(0.5F), (Integer)this.critWait.getValue(), (Float)this.perfectHit.getValue(), delayMin, delayMax, now, System.nanoTime() ^ (long)target.method_5628() << 32);
                  if (action == 1) {
                     GrimMovementGuard.markAction();
                     mc.field_1761.method_2918(mc.field_1724, target);
                     mc.field_1724.method_6104(class_1268.field_5808);
                     ModuleDebug.module(this.getName(), "attack", target.method_5477().getString());
                  }
               }
            }
         } else {
            this.resetTarget();
         }
      } else {
         this.resetTarget();
      }
   }

   private class_1297 resolveTarget() {
      double reach = this.reach();
      class_1297 crosshair = this.entityFromLook(reach);
      if (this.validTarget(crosshair, reach)) {
         return crosshair;
      } else {
         return (Boolean)this.stickyTarget.getValue() && this.validTarget(this.tracked, reach) ? this.tracked : null;
      }
   }

   private class_1297 entityFromLook(double reach) {
      class_239 picked = RotationHelper.pick(mc.field_1687, mc.field_1724, mc.field_1724.method_36455(), mc.field_1724.method_36454(), mc.field_1724.method_55754(), reach);
      if (picked instanceof class_3966 entityHit) {
         return entityHit.method_17782();
      } else {
         class_239 var5 = mc.field_1765;
         if (var5 instanceof class_3966 vanilla) {
            return vanilla.method_17782();
         } else {
            return null;
         }
      }
   }

   private boolean validTarget(class_1297 entity, double reach) {
      int input = 0;
      if (entity != null) {
         input |= 1;
      }

      if (entity != null && entity != mc.field_1724) {
         input |= 2;
      }

      if (entity != null && entity.method_5805()) {
         input |= 4;
      }

      if (entity != null && !entity.method_7325()) {
         input |= 8;
      }

      if (entity != null && !(entity instanceof class_1531)) {
         input |= 16;
      }

      if (entity != null && this.allowedType(entity)) {
         input |= 32;
      }

      if (!(entity instanceof class_1511) || !this.autoCrystalEnabled()) {
         input |= 64;
      }

      if (entity != null && GrimMovementGuard.canInteractEntity(mc.field_1724, entity, reach)) {
         input |= 128;
      }

      if (entity != null && mc.field_1724.method_56094(entity, reach)) {
         input |= 256;
      }

      return NativeModules.tbotTargetValid(input);
   }

   private boolean allowedType(class_1297 entity) {
      if (entity instanceof class_1511) {
         return (Boolean)this.crystals.getValue();
      } else if (!(entity instanceof class_1309)) {
         return false;
      } else if (entity instanceof class_1657) {
         return (Boolean)this.players.getValue();
      } else {
         return entity instanceof class_1569 ? (Boolean)this.hostiles.getValue() : (Boolean)this.passives.getValue();
      }
   }

   private boolean canCrit() {
      return !mc.field_1724.method_24828() && mc.field_1724.field_6017 > (double)0.0F && mc.field_1724.method_18798().field_1351 < (double)0.0F && !mc.field_1724.method_6101() && !mc.field_1724.method_5799() && !mc.field_1724.method_6059(class_1294.field_5919) && !mc.field_1724.method_5765() && !mc.field_1724.method_31549().field_7479 && !mc.field_1724.method_5681();
   }

   private boolean holdingMelee() {
      class_1799 stack = mc.field_1724.method_6047();
      return stack.method_31573(class_3489.field_42611) || stack.method_31573(class_3489.field_42612) || stack.method_31574(class_1802.field_49814);
   }

   private boolean autoCrystalEnabled() {
      if (Ambience.moduleManager == null) {
         return false;
      } else {
         AutoCrystalModule module = (AutoCrystalModule)Ambience.moduleManager.getModuleByClass(AutoCrystalModule.class);
         return module != null && module.isEnabled();
      }
   }

   private double reach() {
      return Math.min((double)(Float)this.range.getValue(), mc.field_1724.method_55755());
   }

   private void resetTarget() {
      this.tracked = null;
      NativeModules.resetTbot(false);
   }
}
