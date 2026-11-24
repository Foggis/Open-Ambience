package dev.ambience.features.modules.combat;

import dev.ambience.features.modules.Module;
import dev.ambience.hooks.CrystalOptimizerHook;
import dev.ambience.nativebridge.NativeModules;
import dev.ambience.util.CrystalOwnership;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_239;
import net.minecraft.class_2398;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_5134;
import net.minecraft.class_5712;
import net.minecraft.class_6012;
import net.minecraft.class_638;
import net.minecraft.class_746;
import net.minecraft.class_1297.class_5529;

public class CrystalOptimizerModule extends Module {
   private static final float BLAST_RADIUS = 6.0F;
   private static CrystalOptimizerModule INSTANCE;

   public CrystalOptimizerModule() {
      super("CrystalOptimizer", "Removes crystals client-side on hit so you don't wait for the server.", Module.Category.COMBAT);
      INSTANCE = this;
      CrystalOptimizerHook.bind(new CrystalOptimizerHook.Impl() {
         public void afterAttack(class_1297 entity) {
            CrystalOptimizerModule.afterAttack(entity);
         }

         public boolean consumePredictedExplosion(class_243 center) {
            return CrystalOptimizerModule.consumePredictedExplosion(center);
         }
      });
   }

   public static CrystalOptimizerModule getInstance() {
      return INSTANCE;
   }

   public static void afterAttack(class_1297 entity) {
      CrystalOptimizerModule module = getInstance();
      if (module != null && module.isEnabled()) {
         module.optimize(entity);
      }
   }

   public static boolean consumePredictedExplosion(class_243 center) {
      return center != null && NativeModules.consumeCrystalPrediction(center.field_1352, center.field_1351, center.field_1350, System.currentTimeMillis());
   }

   public void onDisable() {
      NativeModules.clearCrystalPrediction();
   }

   private void optimize(class_1297 entity) {
      boolean crystalEntity = entity instanceof class_1511;
      class_1511 crystal = crystalEntity ? (class_1511)entity : null;
      class_310 mc = class_310.method_1551();
      class_746 player = mc.field_1724;
      boolean validPlayer = player != null && mc.field_1687 instanceof class_638 && !player.method_7325();
      boolean accepted = NativeModules.crystalOptimizerAccept(this.isEnabled(), crystalEntity, crystal != null && crystal.method_5805(), validPlayer, validPlayer && player.method_45325(class_5134.field_23721) > (double)0.0F);
      if (accepted) {
         class_638 level = mc.field_1687;
         CrystalOwnership.onBroken(CrystalOwnership.baseUnder(crystal));
         playBreakFx(level, crystal);
         crystal.method_32876(class_5712.field_37676);
         crystal.method_5650(class_5529.field_26998);
         retargetBlock(mc, player, crystal);
      }
   }

   private static void playBreakFx(class_638 level, class_1511 crystal) {
      double x = crystal.method_23317();
      double y = crystal.method_23318();
      double z = crystal.method_23321();
      class_243 pos = new class_243(x, y, z);
      float pitch = (1.0F + (level.field_9229.method_43057() - level.field_9229.method_43057()) * 0.2F) * 0.7F;
      level.method_8486(x, y, z, (class_3414)class_3417.field_15152.comp_349(), class_3419.field_15245, 4.0F, pitch, false);
      level.method_8406(class_2398.field_11221, x, y, z, (double)1.0F, (double)0.0F, (double)0.0F);
      level.method_73228(pos, 6.0F, 0, class_6012.method_34990());
      NativeModules.markCrystalPrediction(x, y, z, System.currentTimeMillis());
   }

   private static void retargetBlock(class_310 mc, class_746 player, class_1511 crystal) {
      if (mc.field_1692 == crystal) {
         class_239 hit = player.method_5745(player.method_55754(), 1.0F, false);
         mc.field_1692 = null;
         mc.field_1765 = hit;
      }
   }
}
