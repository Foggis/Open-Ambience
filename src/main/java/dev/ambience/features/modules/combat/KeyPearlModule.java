package dev.ambience.features.modules.combat;

import dev.ambience.features.modules.Module;
import dev.ambience.features.settings.Bind;
import dev.ambience.features.settings.Setting;
import dev.ambience.nativebridge.NativeModules;
import dev.ambience.util.GrimMovementGuard;
import dev.ambience.util.HotbarUtil;
import dev.ambience.util.SilentRotation;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1531;
import net.minecraft.class_1657;
import net.minecraft.class_1802;
import net.minecraft.class_238;
import net.minecraft.class_243;
import org.joml.Vector2f;

public class KeyPearlModule extends Module {
   private static KeyPearlModule INSTANCE;
   private final Setting<Bind> throwKey = this.key("ThrowKey", Bind.none()).setPage("General");
   private final Setting<Boolean> restoreSlot = this.bool("RestoreSlot", true).setPage("General");
   private final Setting<Vector2f> switchDelay = this.vec2f("SwitchMs", 45.0F, 110.0F).setPage("General");
   private final Setting<Boolean> aimTarget = this.bool("AimTarget", false).setPage("General");
   private final Setting<Float> aimRange = this.num("AimRange", 48.0F, 8.0F, 80.0F).setPage("General").setVisibility((v) -> (Boolean)this.aimTarget.getValue());
   private final Setting<Boolean> silentAim = this.bool("SilentAim", true).setPage("General").setVisibility((v) -> (Boolean)this.aimTarget.getValue());
   private boolean heldSilent;

   public KeyPearlModule() {
      super("KeyPearl", "Throws an ender pearl from your hotbar when you press the bound key.", Module.Category.COMBAT);
      INSTANCE = this;
   }

   public static boolean isBusy() {
      KeyPearlModule module = INSTANCE;
      return module != null && module.isEnabled() && NativeModules.keyPearlPhase() != 0;
   }

   public String getDebugState() {
      String var10000;
      switch (NativeModules.keyPearlPhase()) {
         case 1 -> var10000 = "SWITCHED";
         case 2 -> var10000 = "THROWN";
         default -> var10000 = null;
      }

      return var10000;
   }

   public void onDisable() {
      this.releaseSilentAim();
      NativeModules.resetKeyPearl();
   }

   public void onPreTick() {
      boolean down = ((Bind)this.throwKey.getValue()).isDown();
      boolean canBegin = this.isEnabled() && !nullCheck() && mc.field_1761 != null && mc.field_1755 == null && !((Bind)this.throwKey.getValue()).isEmpty() && !AnchorMacroModule.blocksInteraction() && (!SilentRotation.isActive() || SilentRotation.ownedBy(SilentRotation.Owner.OMNI));
      int pearl = canBegin ? this.findPearlSlot() : -1;
      int current = canBegin ? mc.field_1724.method_31548().method_67532() : -1;
      int min = Math.max(0, Math.round(Math.min((this.switchDelay.getValue()).x, (this.switchDelay.getValue()).y)));
      int max = Math.max(min, Math.round(Math.max((this.switchDelay.getValue()).x, (this.switchDelay.getValue()).y)));
      int action = NativeModules.keyPearlPress(down, canBegin, pearl, current, min, max, System.currentTimeMillis(), System.nanoTime());
      if (action == 1 && pearl != current) {
         HotbarUtil.select(mc.field_1724, pearl);
      }

   }

   public void onTick() {
      if (!nullCheck() && mc.field_1761 != null) {
         long now = System.currentTimeMillis();
         int action = NativeModules.keyPearlTick(now);
         if (action == 2) {
            if ((Boolean)this.aimTarget.getValue()) {
               this.aimAtNearest();
            }

            GrimMovementGuard.markAction();
            mc.field_1761.method_2919(mc.field_1724, class_1268.field_5808);
            NativeModules.keyPearlThrown((Boolean)this.restoreSlot.getValue(), now, System.nanoTime());
            this.releaseSilentAim();
         } else {
            if (action == 3) {
               int previous = NativeModules.keyPearlPreviousSlot();
               if (previous >= 0 && previous <= 8) {
                  HotbarUtil.select(mc.field_1724, previous);
               }

               NativeModules.keyPearlRestored();
            }

         }
      }
   }

   private void aimAtNearest() {
      class_1309 target = this.nearestPlayer();
      if (target != null) {
         float partial = 1.0F;
         class_243 start = mc.field_1724.method_5836(partial).method_1031((double)0.0F, -0.1, (double)0.0F);
         class_243 aim = bodyPoint(target, partial);
         class_243 inherit = this.shooterInherit();
         float[] angles = NativeModules.solveKeyPearl(start.field_1352, start.field_1351, start.field_1350, aim.field_1352, aim.field_1351, aim.field_1350, inherit.field_1352, inherit.field_1351, inherit.field_1350);
         if (angles == null) {
            angles = GrimMovementGuard.anglesFrom(start, aim);
         }

         if (angles != null) {
            GrimMovementGuard.applyMacroLook(mc.field_1724, angles[0], angles[1], (Boolean)this.silentAim.getValue(), true);
            this.heldSilent = (Boolean)this.silentAim.getValue() && SilentRotation.ownedBy(SilentRotation.Owner.COMBAT);
         }
      }
   }

   private void releaseSilentAim() {
      if (this.heldSilent && SilentRotation.ownedBy(SilentRotation.Owner.COMBAT)) {
         SilentRotation.end(SilentRotation.Owner.COMBAT);
      }

      this.heldSilent = false;
   }

   private class_1309 nearestPlayer() {
      class_1309 best = null;
      double bestSq = (double)((Float)this.aimRange.getValue() * (Float)this.aimRange.getValue());

      for(class_1657 player : mc.field_1687.method_18456()) {
         if (player != mc.field_1724 && player.method_5805() && !player.method_7325()) {
            double d = mc.field_1724.method_5858(player);
            if (d < bestSq) {
               bestSq = d;
               best = player;
            }
         }
      }

      class_238 scan = mc.field_1724.method_5829().method_1014((double)(Float)this.aimRange.getValue());

      for(class_1297 entity : mc.field_1687.method_8333(mc.field_1724, scan, class_1297::method_5805)) {
         if (entity instanceof class_1309 living) {
            if (!(living instanceof class_1657) && !(living instanceof class_1531) && living.method_5805() && !living.method_7325()) {
               double d = mc.field_1724.method_5858(living);
               if (d < bestSq) {
                  bestSq = d;
                  best = living;
               }
            }
         }
      }

      return best;
   }

   private static class_243 bodyPoint(class_1309 living, float partial) {
      class_243 interp = living.method_30950(partial);
      class_238 box = living.method_5829().method_997(interp.method_1020(living.method_73189()));
      return new class_243((box.field_1323 + box.field_1320) * (double)0.5F, box.field_1322 + box.method_17940() * 0.45, (box.field_1321 + box.field_1324) * (double)0.5F);
   }

   private class_243 shooterInherit() {
      class_243 vel = mc.field_1724.method_60478();
      return new class_243(vel.field_1352, mc.field_1724.method_24828() ? (double)0.0F : vel.field_1351, vel.field_1350);
   }

   private int findPearlSlot() {
      int selected = mc.field_1724.method_31548().method_67532();
      if (mc.field_1724.method_31548().method_5438(selected).method_31574(class_1802.field_8634)) {
         return selected;
      } else {
         for(int i = 0; i < 9; ++i) {
            if (mc.field_1724.method_31548().method_5438(i).method_31574(class_1802.field_8634)) {
               return i;
            }
         }

         return -1;
      }
   }
}
