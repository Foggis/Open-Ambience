package dev.ambience.features.modules.movement;

import dev.ambience.features.modules.Module;
import dev.ambience.features.modules.combat.AnchorMacroModule;
import dev.ambience.inject.Access;
import dev.ambience.nativebridge.NativeModules;
import dev.ambience.util.GrimMovementGuard;
import dev.ambience.util.HotbarUtil;
import dev.ambience.util.ModuleDebug;
import net.minecraft.class_12125;
import net.minecraft.class_1268;
import net.minecraft.class_1799;
import net.minecraft.class_1887;
import net.minecraft.class_1890;
import net.minecraft.class_1893;
import net.minecraft.class_3489;
import net.minecraft.class_6880;
import net.minecraft.class_7924;
import net.minecraft.class_9334;

public class LungeSwapModule extends Module {
   private static LungeSwapModule INSTANCE;
   private int fastSlot = -1;
   private int spearSlot = -1;
   private boolean restorePending;
   private boolean heldJumpKey;

   public LungeSwapModule() {
      super("LungeSwap", "Jumps and stabs a Lunge spear in the same tick, then stays on the spear.", Module.Category.MOVEMENT);
      INSTANCE = this;
   }

   public static boolean isBusy() {
      LungeSwapModule module = INSTANCE;
      return module != null && module.isEnabled();
   }

   public String getDebugState() {
      return "spear=" + this.spearSlot + " hand=" + this.fastSlot + " restore=" + this.restorePending;
   }

   public void onEnable() {
      if (!nullCheck() && mc.field_1761 != null) {
         this.spearSlot = this.findLungeSpearSlot();
         if (this.spearSlot == -1) {
            this.disable();
         } else {
            this.fastSlot = this.findHandSlot(this.spearSlot);
            this.restorePending = false;
            this.heldJumpKey = false;
         }
      } else {
         this.disable();
      }
   }

   public void onDisable() {
      this.releaseJumpKey();
      this.restorePending = false;
      this.fastSlot = -1;
      this.spearSlot = -1;
   }

   public void onPreTick() {
      if (!nullCheck() && mc.field_1761 != null) {
         this.spearSlot = this.findLungeSpearSlot();
         if (this.fastSlot < 0 || !this.isHandSlot(this.fastSlot, this.spearSlot)) {
            this.fastSlot = this.findHandSlot(this.spearSlot);
         }

         class_1799 spear = mc.field_1724.method_6047();
         int action = NativeModules.lungePlan(mc.field_1755 != null || AnchorMacroModule.blocksInteraction(), this.spearSlot != -1, this.restorePending, mc.field_1724.method_31548().method_67532() == this.spearSlot, this.isLungeSpear(spear) && !mc.field_1724.method_75202(spear, 5), mc.field_1724.method_24828(), this.getBindMode() == Module.BindMode.HOLD && this.getBind().isDown());
         if (action == 6) {
            this.disable();
         } else if (action == 1) {
            HotbarUtil.select(mc.field_1724, this.spearSlot);
         } else if (action != 4 && action != 5) {
            if (action == 2) {
               this.pressJump();
            }

            if (action == 2 || action == 3) {
               if (!this.stabSpear(spear)) {
                  this.releaseJumpKey();
                  this.disable();
                  return;
               }

               this.restorePending = true;
            }

         } else {
            HotbarUtil.select(mc.field_1724, this.spearSlot);
            this.releaseJumpKey();
            if (action == 4) {
               this.restorePending = false;
            } else {
               this.disable();
            }

         }
      }
   }

   private boolean stabSpear(class_1799 spear) {
      class_12125 piercing = (class_12125)spear.method_58694(class_9334.field_63631);
      if (piercing == null) {
         return false;
      } else {
         GrimMovementGuard.markAction();
         Access.ensureHasSentCarriedItem(mc.field_1761);
         mc.field_1761.method_75407(piercing);
         mc.field_1724.method_6104(class_1268.field_5808);
         int var10001 = this.lungeLevel(spear);
         ModuleDebug.interact("stab", "lunge=" + var10001);
         return true;
      }
   }

   private void pressJump() {
      if (mc.field_1724.field_3913 != null) {
         mc.field_1724.field_3913.method_64054();
      }

      Access.setJumping(mc.field_1724, true);
      if (mc.field_1690 != null && !mc.field_1690.field_1903.method_1434()) {
         mc.field_1690.field_1903.method_23481(true);
         this.heldJumpKey = true;
      }

   }

   private void releaseJumpKey() {
      if (this.heldJumpKey && mc.field_1690 != null) {
         mc.field_1690.field_1903.method_23481(false);
         this.heldJumpKey = false;
      }
   }

   private int findLungeSpearSlot() {
      int selected = mc.field_1724.method_31548().method_67532();
      if (this.isLungeSpear(mc.field_1724.method_31548().method_5438(selected))) {
         return selected;
      } else {
         for(int i = 0; i < 9; ++i) {
            if (this.isLungeSpear(mc.field_1724.method_31548().method_5438(i))) {
               return i;
            }
         }

         return -1;
      }
   }

   private int findHandSlot(int spear) {
      int selected = mc.field_1724.method_31548().method_67532();
      if (selected != spear && this.isEmpty(mc.field_1724.method_31548().method_5438(selected))) {
         return selected;
      } else {
         for(int i = 0; i < 9; ++i) {
            if (i != spear && this.isEmpty(mc.field_1724.method_31548().method_5438(i))) {
               return i;
            }
         }

         int best = -1;
         float bestCharge = Float.MAX_VALUE;

         for(int i = 0; i < 9; ++i) {
            if (i != spear) {
               class_1799 stack = mc.field_1724.method_31548().method_5438(i);
               if (!this.isSpear(stack)) {
                  float charge = (Float)stack.method_58695(class_9334.field_63635, 0.0F);
                  if (charge < bestCharge) {
                     bestCharge = charge;
                     best = i;
                  }
               }
            }
         }

         return best;
      }
   }

   private boolean isHandSlot(int slot, int spear) {
      if (slot >= 0 && slot <= 8 && slot != spear) {
         return !this.isSpear(mc.field_1724.method_31548().method_5438(slot));
      } else {
         return false;
      }
   }

   private boolean isEmpty(class_1799 stack) {
      return stack == null || stack.method_7960();
   }

   private boolean isSpear(class_1799 stack) {
      if (stack != null && !stack.method_7960()) {
         return stack.method_57826(class_9334.field_63631) || stack.method_31573(class_3489.field_63257);
      } else {
         return false;
      }
   }

   private boolean isLungeSpear(class_1799 stack) {
      return this.isSpear(stack) && this.lungeLevel(stack) > 0;
   }

   private int lungeLevel(class_1799 stack) {
      class_6880<class_1887> lunge = this.lungeHolder();
      return lunge == null ? 0 : class_1890.method_8225(lunge, stack);
   }

   private class_6880<class_1887> lungeHolder() {
      return mc.field_1687 == null ? null : (class_6880)mc.field_1687.method_30349().method_30530(class_7924.field_41265).method_46746(class_1893.field_63420).orElse(null);
   }
}
