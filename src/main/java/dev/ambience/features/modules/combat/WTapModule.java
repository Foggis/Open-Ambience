package dev.ambience.features.modules.combat;

import dev.ambience.features.modules.Module;
import dev.ambience.features.modules.render.FreecamModule;
import dev.ambience.features.settings.Setting;
import dev.ambience.hooks.WTapHook;
import dev.ambience.inject.Access;
import dev.ambience.nativebridge.NativeModules;
import net.minecraft.class_10185;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1531;
import net.minecraft.class_1569;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_241;
import net.minecraft.class_3489;
import net.minecraft.class_9334;
import org.joml.Vector2f;

public class WTapModule extends Module {
   private static WTapModule INSTANCE;
   private final Setting<Vector2f> stopMs = this.vec2f("StopMs", 325.0F, 400.0F).setVec2TrackBounds(0.0F, 4000.0F).setPage("General");
   private final Setting<Boolean> strafe = this.bool("Strafe", true).setPage("General");
   private final Setting<Boolean> weaponsOnly = this.bool("WeaponsOnly", true).setPage("General");
   private final Setting<Boolean> players = this.bool("Players", true).setPage("Targets");
   private final Setting<Boolean> hostiles = this.bool("Hostiles", false).setPage("Targets");
   private final Setting<Boolean> passives = this.bool("Passives", false).setPage("Targets");
   private boolean pendingHit;
   private long untilMs;

   public WTapModule() {
      super("WTap", "Stops walking after a sword hit so combos stay at range.", Module.Category.COMBAT);
      INSTANCE = this;
      WTapHook.bind(new WTapHook.Impl() {
         public void beforeAttack(class_1657 player, class_1297 entity) {
            WTapModule.beforeAttack(player, entity);
         }

         public void afterAttack() {
            WTapModule.afterAttack();
         }

         public void freezeInput() {
            WTapModule.freezeInput();
         }
      });
   }

   public static WTapModule getInstance() {
      return INSTANCE;
   }

   public static boolean isBusy() {
      WTapModule module = INSTANCE;
      return NativeModules.wtapBusy(module != null && module.isEnabled(), System.currentTimeMillis(), module == null ? 0L : module.untilMs);
   }

   public static void beforeAttack(class_1657 player, class_1297 entity) {
      WTapModule module = INSTANCE;
      if (module != null) {
         module.markHit(player, entity);
      }

   }

   public static void afterAttack() {
      WTapModule module = INSTANCE;
      if (module != null) {
         module.startStop();
      }

   }

   public static void freezeInput() {
      if (isBusy() && !FreecamModule.isActive()) {
         WTapModule module = INSTANCE;
         if (module != null && mc.field_1724 != null && mc.field_1724.field_3913 != null) {
            module.applyFreeze();
         }
      }
   }

   public String getDisplayInfo() {
      if (!isBusy()) {
         Vector2f value = this.stopMs.getValue();
         return Math.round(value.x) == Math.round(value.y) ? Math.round(value.x) + "ms" : String.format("%.0f-%.0fms", value.x(), value.y());
      } else {
         long var10001 = this.untilMs;
         return Math.max(0L, var10001 - System.currentTimeMillis()) + "ms";
      }
   }

   public String getDebugState() {
      return isBusy() ? "stop" : null;
   }

   public void onDisable() {
      this.pendingHit = false;
      this.untilMs = 0L;
   }

   public void onPreTick() {
      if (!isBusy()) {
         this.untilMs = 0L;
      } else {
         freezeInput();
      }
   }

   private void markHit(class_1657 player, class_1297 entity) {
      boolean validContext = !nullCheck() && player != null;
      this.pendingHit = NativeModules.wtapAccept(this.isEnabled(), player == mc.field_1724, validContext && !FreecamModule.isActive() && !AnchorMacroModule.blocksInteraction(), validContext && (!(Boolean)this.weaponsOnly.getValue() || holdingMelee(player)), validContext && this.validTarget(entity), validContext && player.method_7261(0.5F) >= 0.85F);
   }

   private void startStop() {
      if (this.pendingHit) {
         this.pendingHit = false;
         this.untilMs = System.currentTimeMillis() + this.rollStopMs();
         freezeInput();
      }
   }

   private void applyFreeze() {
      class_10185 keys = mc.field_1724.field_3913.field_54155;
      boolean left = (Boolean)this.strafe.getValue() && keys.comp_3161();
      boolean right = (Boolean)this.strafe.getValue() && keys.comp_3162();
      mc.field_1724.field_3913.field_54155 = new class_10185(false, false, left, right, keys.comp_3163(), keys.comp_3164(), false);
      float x = NativeModules.tapImpulse(left, right);
      Access.setMoveVector(mc.field_1724.field_3913, x == 0.0F ? class_241.field_1340 : new class_241(x, 0.0F));
      mc.field_1724.method_5728(false);
   }

   private boolean validTarget(class_1297 entity) {
      if (entity instanceof class_1309 living) {
         if (living != mc.field_1724 && living.method_5805() && !living.method_7325()) {
            if (living instanceof class_1531) {
               return false;
            }

            if (living instanceof class_1657) {
               return (Boolean)this.players.getValue();
            }

            if (living instanceof class_1569) {
               return (Boolean)this.hostiles.getValue();
            }

            return (Boolean)this.passives.getValue();
         }
      }

      return false;
   }

   private static boolean holdingMelee(class_1657 player) {
      class_1799 stack = player.method_6047();
      return stack.method_31573(class_3489.field_42611) || stack.method_31573(class_3489.field_42612) || stack.method_31574(class_1802.field_49814) || stack.method_31573(class_3489.field_63257) || stack.method_57826(class_9334.field_63631);
   }

   private long rollStopMs() {
      int min = Math.max(0, Math.round((this.stopMs.getValue()).x));
      int max = Math.max(min, Math.round((this.stopMs.getValue()).y));
      return min == max ? (long)min : NativeModules.rollDelay(min, max + 1, System.nanoTime());
   }
}
