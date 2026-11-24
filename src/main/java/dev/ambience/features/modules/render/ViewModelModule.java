package dev.ambience.features.modules.render;

import dev.ambience.features.modules.Module;
import dev.ambience.features.settings.Setting;
import dev.ambience.hooks.ViewModelHook;
import dev.ambience.nativebridge.NativeModules;
import java.util.function.Predicate;
import net.minecraft.class_1268;
import net.minecraft.class_1306;
import net.minecraft.class_4587;
import net.minecraft.class_742;

public class ViewModelModule extends Module {
   private static ViewModelModule INSTANCE;
   public final Setting<Double> scale = this.num("Scale", 0.85, (double)0.0F, (double)3.0F).setPage("General");
   public final Setting<Double> posX = this.num("PosX", (double)0.0F, (double)-1.0F, (double)1.0F).setPage("General");
   public final Setting<Double> posY = this.num("PosY", (double)0.0F, (double)-1.0F, (double)1.0F).setPage("General");
   public final Setting<Double> posZ = this.num("PosZ", (double)0.0F, (double)-1.0F, (double)1.0F).setPage("General");
   public final Setting<Boolean> noSway = this.bool("NoSway", false).setPage("General");
   public final Setting<Boolean> noSwapAnimation = this.bool("NoSwapAnimation", false).setPage("General");
   public final Setting<Boolean> oldAnimation = this.bool("OldAnimation", false).setPage("General");

   public ViewModelModule() {
      super("ViewModel", "Scale and position first-person hands.", Module.Category.VISUALS);
      INSTANCE = this;
      ViewModelHook.bind(new ViewModelHook.Impl() {
         public boolean enabled() {
            return (ViewModelModule.this.nativeFlags() & 1) != 0;
         }

         public boolean noSway() {
            return (ViewModelModule.this.nativeFlags() & 2) != 0;
         }

         public boolean noSwapAnimation() {
            return (ViewModelModule.this.nativeFlags() & 4) != 0;
         }

         public boolean oldAnimation() {
            return (ViewModelModule.this.nativeFlags() & 8) != 0;
         }

         public void applyArm(class_742 player, class_1268 hand, class_4587 poseStack) {
            ViewModelModule.this.applyArmTransform(player, hand, poseStack);
         }
      });
   }

   private void applyArmTransform(class_742 player, class_1268 hand, class_4587 poseStack) {
      class_1306 arm = hand == class_1268.field_5808 ? player.method_6068() : player.method_6068().method_5928();
      double[] transform = NativeModules.viewModelTransform((Double)this.posX.getValue(), (Double)this.posY.getValue(), (Double)this.posZ.getValue(), (Double)this.scale.getValue(), arm == class_1306.field_6183);
      if (transform != null && transform.length == 7) {
         poseStack.method_22903();
         poseStack.method_22904(transform[0], transform[1], transform[2]);
         poseStack.method_22904(transform[3], transform[4], transform[5]);
         float s = (float)transform[6];
         poseStack.method_22905(s, s, s);
         poseStack.method_22904(-transform[3], -transform[4], -transform[5]);
      }
   }

   private int nativeFlags() {
      return NativeModules.viewModelFlags(this.isEnabled(), (Boolean)this.noSway.getValue(), (Boolean)this.noSwapAnimation.getValue(), (Boolean)this.oldAnimation.getValue());
   }

   public static ViewModelModule getInstance() {
      return INSTANCE;
   }

   public static boolean isActive(Predicate<ViewModelModule> predicate) {
      ViewModelModule module = getInstance();
      return module != null && module.isEnabled() && predicate.test(module);
   }
}
