package dev.ambience.features.modules.combat;

import dev.ambience.features.modules.Module;
import dev.ambience.features.modules.movement.LungeSwapModule;
import dev.ambience.hooks.ShieldStunHook;
import dev.ambience.inject.Access;
import dev.ambience.nativebridge.NativeModules;
import dev.ambience.util.GrimMovementGuard;
import dev.ambience.util.HotbarUtil;
import net.minecraft.class_10590;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1713;
import net.minecraft.class_1799;
import net.minecraft.class_3489;
import net.minecraft.class_9334;

public class ShieldStunModule extends Module {
   private static ShieldStunModule INSTANCE;
   private int restoreSlot = -1;
   private int pulledInv = -1;
   private int pulledHotbar = -1;
   private int routeAction = 0;
   private int routeSource = -1;
   private int routeDestination = -1;
   private int waitTicks;
   private int sequenceTicks;
   private class_1297 queuedTarget;
   private Phase phase;
   private boolean macroAttack;

   public ShieldStunModule() {
      super("ShieldStun", "When you hit a blocking player, swaps to an axe to stun their shield then swaps back.", Module.Category.COMBAT);
      this.phase = ShieldStunModule.Phase.IDLE;
      INSTANCE = this;
      ShieldStunHook.bind(new ShieldStunHook.Impl() {
         public boolean beforeAttack(class_1657 player, class_1297 entity) {
            return ShieldStunModule.beforeAttack(player, entity);
         }

         public void afterAttack() {
            ShieldStunModule.afterAttack();
         }
      });
   }

   public static boolean isBusy() {
      ShieldStunModule module = INSTANCE;
      return module != null && module.isEnabled() && module.phase != ShieldStunModule.Phase.IDLE;
   }

   public static boolean beforeAttack(class_1657 player, class_1297 entity) {
      ShieldStunModule module = INSTANCE;
      return module != null && module.queueStun(player, entity);
   }

   public static void afterAttack() {
      ShieldStunModule module = INSTANCE;
      if (module != null) {
         module.onAttackReturn();
      }

   }

   public String getDebugState() {
      if (this.phase == ShieldStunModule.Phase.IDLE) {
         return null;
      } else {
         String var10000 = String.valueOf(this.phase);
         return "phase=" + var10000 + " restore=" + this.restoreSlot + " inv=" + this.pulledInv + " hotbar=" + this.pulledHotbar + " age=" + this.sequenceTicks;
      }
   }

   public void onDisable() {
      this.forceRestore();
   }

   public void onPreTick() {
      if (this.phase != ShieldStunModule.Phase.IDLE) {
         if (!nullCheck() && mc.field_1761 != null) {
            ++this.sequenceTicks;
            if (this.sequenceTicks <= 20 || this.phase != ShieldStunModule.Phase.SELECT_AXE && this.phase != ShieldStunModule.Phase.SELECT_PULLED_AXE && this.phase != ShieldStunModule.Phase.WAIT_ATTACK) {
               switch (this.phase.ordinal()) {
                  case 1 -> this.selectAxe();
                  case 2 -> this.selectPulledAxe();
                  case 3 -> this.attackWithAxe();
                  case 4 -> this.restoreSelectedSlot();
                  case 5 -> this.restoreInventoryAxe();
               }

            } else {
               this.beginRestore();
            }
         } else {
            this.forceRestore();
         }
      }
   }

   private boolean queueStun(class_1657 player, class_1297 entity) {
      if (this.macroAttack) {
         return false;
      } else if (this.isEnabled() && this.phase != ShieldStunModule.Phase.IDLE) {
         return true;
      } else {
         int var10000;
         byte var10001;
         label90: {
            var10000 = (this.isEnabled() ? 1 : 0) | (this.phase == ShieldStunModule.Phase.IDLE ? 2 : 0) | (!nullCheck() && mc.field_1761 != null && player == mc.field_1724 ? 4 : 0) | (entity instanceof class_1657 ? 8 : 0) | (entity != player ? 16 : 0);
            if (entity instanceof class_1309) {
               class_1309 living = (class_1309)entity;
               if (living.method_6039()) {
                  var10001 = 32;
                  break label90;
               }
            }

            var10001 = 0;
         }

         int flags = var10000 | var10001 | (!AnchorMacroModule.blocksInteraction() && !AutoMaceModule.isBusy() && !LungeSwapModule.isBusy() ? 64 : 0) | (player != null && !this.isStunAxe(player.method_6047()) ? 128 : 0);
         if (!NativeModules.shieldStunAccept(flags)) {
            return false;
         } else {
            int selected = player.method_31548().method_67532();
            int axeHotbar = this.findAxeHotbar();
            int inv = this.findAxeInventory();
            int[] route = NativeModules.shieldStunRoute(axeHotbar, inv, this.emptyHotbarSlot(), selected, mc.field_1755 == null, !GrimMovementGuard.inventoryActionUnsafe(mc.field_1724));
            if (route != null && route.length == 4 && route[0] != 0) {
               this.routeAction = route[0];
               this.routeSource = route[1];
               this.routeDestination = route[2];
               this.restoreSlot = route[3];
               this.queuedTarget = entity;
               this.waitTicks = 0;
               this.sequenceTicks = 0;
               this.phase = ShieldStunModule.Phase.SELECT_AXE;
               return true;
            } else {
               return false;
            }
         }
      }
   }

   private void selectAxe() {
      if (!this.validTarget()) {
         this.clearSequence();
      } else if (!GrimMovementGuard.actionThisTick()) {
         if (this.routeAction == 1) {
            if (this.routeSource >= 0 && this.routeSource <= 8 && this.isStunAxe(mc.field_1724.method_31548().method_5438(this.routeSource))) {
               HotbarUtil.select(mc.field_1724, this.routeSource);
               GrimMovementGuard.markAction();
               this.waitTicks = 1;
               this.phase = ShieldStunModule.Phase.WAIT_ATTACK;
            } else {
               this.forceRestore();
            }
         } else if (this.routeAction == 2) {
            if (!GrimMovementGuard.inventoryActionUnsafe(mc.field_1724) && this.routeSource >= 9 && this.routeSource <= 35 && this.routeDestination >= 0 && this.routeDestination <= 8 && this.isStunAxe(mc.field_1724.method_31548().method_5438(this.routeSource))) {
               this.pulledInv = this.routeSource;
               this.pulledHotbar = this.routeDestination;
               mc.field_1761.method_2906(mc.field_1724.field_7498.field_7763, this.pulledInv, this.pulledHotbar, class_1713.field_7791, mc.field_1724);
               GrimMovementGuard.markAction();
               this.waitTicks = 1;
               this.phase = ShieldStunModule.Phase.SELECT_PULLED_AXE;
            }
         } else {
            this.forceRestore();
         }
      }
   }

   private void selectPulledAxe() {
      if (this.waitTicks-- <= 0 && !GrimMovementGuard.actionThisTick()) {
         if (this.validTarget() && this.pulledHotbar >= 0 && this.pulledHotbar <= 8 && this.isStunAxe(mc.field_1724.method_31548().method_5438(this.pulledHotbar))) {
            if (this.pulledHotbar != mc.field_1724.method_31548().method_67532()) {
               HotbarUtil.select(mc.field_1724, this.pulledHotbar);
            } else {
               Access.ensureHasSentCarriedItem(mc.field_1761);
            }

            GrimMovementGuard.markAction();
            this.waitTicks = 1;
            this.phase = ShieldStunModule.Phase.WAIT_ATTACK;
         } else {
            this.beginRestore();
         }
      }
   }

   private void attackWithAxe() {
      if (this.waitTicks-- <= 0 && !GrimMovementGuard.actionThisTick()) {
         if (this.validTarget() && this.isStunAxe(mc.field_1724.method_6047())) {
            double reach = mc.field_1724.method_55755() + (double)0.25F;
            if (mc.field_1724.method_5858(this.queuedTarget) > reach * reach) {
               this.beginRestore();
            } else {
               this.macroAttack = true;

               try {
                  GrimMovementGuard.markAction();
                  mc.field_1761.method_2918(mc.field_1724, this.queuedTarget);
                  mc.field_1724.method_6104(class_1268.field_5808);
               } finally {
                  this.macroAttack = false;
               }

               this.beginRestore();
            }
         } else {
            this.beginRestore();
         }
      }
   }

   private void beginRestore() {
      this.waitTicks = 1;
      this.phase = this.restoreSlot >= 0 ? ShieldStunModule.Phase.RESTORE_SLOT : (this.pulledInv >= 0 ? ShieldStunModule.Phase.RESTORE_INVENTORY : ShieldStunModule.Phase.IDLE);
      if (this.phase == ShieldStunModule.Phase.IDLE) {
         this.clearSequence();
      }

   }

   private void restoreSelectedSlot() {
      if (this.waitTicks-- <= 0 && !GrimMovementGuard.actionThisTick()) {
         if (this.restoreSlot >= 0 && this.restoreSlot <= 8) {
            HotbarUtil.select(mc.field_1724, this.restoreSlot);
            GrimMovementGuard.markAction();
         }

         int inventorySlot = this.pulledInv;
         this.restoreSlot = -1;
         if (inventorySlot >= 0) {
            this.pulledInv = inventorySlot;
            this.waitTicks = 1;
            this.phase = ShieldStunModule.Phase.RESTORE_INVENTORY;
         } else {
            this.clearSequence();
         }

      }
   }

   private void restoreInventoryAxe() {
      if (this.waitTicks-- <= 0 && !GrimMovementGuard.actionThisTick() && !GrimMovementGuard.inventoryActionUnsafe(mc.field_1724)) {
         if (this.pulledInv >= 0 && this.pulledHotbar >= 0) {
            mc.field_1761.method_2906(mc.field_1724.field_7498.field_7763, this.pulledInv, this.pulledHotbar, class_1713.field_7791, mc.field_1724);
            GrimMovementGuard.markAction();
         }

         this.clearSequence();
      }
   }

   private boolean validTarget() {
      class_1297 var2 = this.queuedTarget;
      boolean var10000;
      if (var2 instanceof class_1657 target) {
         if (target != mc.field_1724 && target.method_5805() && target.method_6039()) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   private void onAttackReturn() {
   }

   private void forceRestore() {
      if (mc.field_1724 != null && mc.field_1761 != null) {
         if (this.restoreSlot >= 0 && this.restoreSlot <= 8) {
            HotbarUtil.select(mc.field_1724, this.restoreSlot);
         }

         if (this.pulledInv >= 0 && this.pulledHotbar >= 0) {
            mc.field_1761.method_2906(mc.field_1724.field_7498.field_7763, this.pulledInv, this.pulledHotbar, class_1713.field_7791, mc.field_1724);
         }
      }

      this.clearSequence();
   }

   private void clearSequence() {
      this.restoreSlot = -1;
      this.pulledInv = -1;
      this.pulledHotbar = -1;
      this.routeAction = 0;
      this.routeSource = -1;
      this.routeDestination = -1;
      this.waitTicks = 0;
      this.sequenceTicks = 0;
      this.queuedTarget = null;
      this.phase = ShieldStunModule.Phase.IDLE;
   }

   private int findAxeHotbar() {
      int best = -1;
      float bestStun = 0.0F;

      for(int i = 0; i < 9; ++i) {
         class_1799 stack = mc.field_1724.method_31548().method_5438(i);
         float stun = this.stunSeconds(stack);
         if (stun > bestStun) {
            bestStun = stun;
            best = i;
         }
      }

      return best;
   }

   private int findAxeInventory() {
      int best = -1;
      float bestStun = 0.0F;

      for(int i = 9; i < 36; ++i) {
         class_1799 stack = mc.field_1724.method_31548().method_5438(i);
         float stun = this.stunSeconds(stack);
         if (stun > bestStun) {
            bestStun = stun;
            best = i;
         }
      }

      return best;
   }

   private int emptyHotbarSlot() {
      for(int i = 0; i < 9; ++i) {
         if (mc.field_1724.method_31548().method_5438(i).method_7960()) {
            return i;
         }
      }

      return -1;
   }

   private boolean isStunAxe(class_1799 stack) {
      return this.stunSeconds(stack) > 0.0F;
   }

   private float stunSeconds(class_1799 stack) {
      if (stack != null && !stack.method_7960()) {
         class_10590 weapon = (class_10590)stack.method_58694(class_9334.field_55878);
         if (weapon != null && weapon.comp_3602() > 0.0F) {
            return weapon.comp_3602();
         } else {
            return stack.method_31573(class_3489.field_42612) ? 5.0F : 0.0F;
         }
      } else {
         return 0.0F;
      }
   }

   private static enum Phase {
      IDLE,
      SELECT_AXE,
      SELECT_PULLED_AXE,
      WAIT_ATTACK,
      RESTORE_SLOT,
      RESTORE_INVENTORY;

      // $FF: synthetic method
      private static Phase[] $values() {
         return new Phase[]{IDLE, SELECT_AXE, SELECT_PULLED_AXE, WAIT_ATTACK, RESTORE_SLOT, RESTORE_INVENTORY};
      }
   }
}
