package dev.ambience.features.modules.render;

import dev.ambience.features.modules.Module;
import dev.ambience.features.settings.Setting;
import dev.ambience.hooks.XRayHook;
import dev.ambience.nativebridge.NativeModules;
import net.minecraft.class_2246;
import net.minecraft.class_2680;
import net.minecraft.class_3481;

public class XRayModule extends Module {
   private static XRayModule INSTANCE;
   private final Setting<Boolean> ores = this.bool("Ores", true).setPage("General");
   private final Setting<Boolean> containers = this.bool("Containers", true).setPage("General");
   private final Setting<Boolean> portals = this.bool("Portals", true).setPage("General");
   private final Setting<Boolean> hideFluids = this.bool("HideFluids", true).setPage("General");
   private int meshKey = Integer.MIN_VALUE;

   public XRayModule() {
      super("XRay", "Hides most blocks so ores and chests show through the world.", Module.Category.VISUALS);
      INSTANCE = this;
      XRayHook.bind(new XRayHook.Impl() {
         public boolean active() {
            return XRayModule.active();
         }

         public boolean hides(class_2680 state) {
            return XRayModule.hides(state);
         }

         public boolean hidesFluids() {
            return XRayModule.hidesFluids();
         }
      });
   }

   public static XRayModule getInstance() {
      return INSTANCE;
   }

   public static boolean active() {
      XRayModule module = INSTANCE;
      return module != null && module.isEnabled();
   }

   public static boolean hides(class_2680 state) {
      if (active() && state != null && !state.method_26215()) {
         return !INSTANCE.isVisible(state);
      } else {
         return false;
      }
   }

   public static boolean hidesFluids() {
      XRayModule module = INSTANCE;
      return module != null && module.isEnabled() && (Boolean)module.hideFluids.getValue();
   }

   public void onEnable() {
      this.meshKey = this.meshSignature();
      this.reloadChunks();
   }

   public void onDisable() {
      this.reloadChunks();
   }

   public void onTick() {
      int key = this.meshSignature();
      if (key != this.meshKey) {
         this.meshKey = key;
         this.reloadChunks();
      }

   }

   public boolean isVisible(class_2680 state) {
      if (state == null) {
         return true;
      } else {
         int settings = ((Boolean)this.ores.getValue() ? 1 : 0) | ((Boolean)this.containers.getValue() ? 2 : 0) | ((Boolean)this.portals.getValue() ? 4 : 0);
         int traits = (state.method_26215() ? 1 : 0) | (this.isOre(state) ? 2 : 0) | (this.isContainer(state) ? 4 : 0) | (this.isPortalOrSpecial(state) ? 8 : 0);
         return NativeModules.xrayVisible(settings, traits);
      }
   }

   private boolean isOre(class_2680 state) {
      return state.method_26164(class_3481.field_29193) || state.method_26164(class_3481.field_28988) || state.method_26164(class_3481.field_23062) || state.method_26164(class_3481.field_28989) || state.method_26164(class_3481.field_29194) || state.method_26164(class_3481.field_28991) || state.method_26164(class_3481.field_28990) || state.method_26164(class_3481.field_29195) || state.method_27852(class_2246.field_22109) || state.method_27852(class_2246.field_23077) || state.method_27852(class_2246.field_10213) || state.method_27852(class_2246.field_33508) || state.method_27852(class_2246.field_33510) || state.method_27852(class_2246.field_33509) || state.method_27852(class_2246.field_22108) || state.method_27852(class_2246.field_10201) || state.method_27852(class_2246.field_10234) || state.method_27852(class_2246.field_10205) || state.method_27852(class_2246.field_10085) || state.method_27852(class_2246.field_27119) || state.method_27852(class_2246.field_10002) || state.method_27852(class_2246.field_10441) || state.method_27852(class_2246.field_10381);
   }

   private boolean isContainer(class_2680 state) {
      return state.method_27852(class_2246.field_10034) || state.method_27852(class_2246.field_10380) || state.method_27852(class_2246.field_10443) || state.method_27852(class_2246.field_16328) || state.method_26164(class_3481.field_21490) || state.method_27852(class_2246.field_10312) || state.method_27852(class_2246.field_10228) || state.method_27852(class_2246.field_10200) || state.method_27852(class_2246.field_10181) || state.method_27852(class_2246.field_16333) || state.method_27852(class_2246.field_16334) || state.method_27852(class_2246.field_10333) || state.method_27852(class_2246.field_46797) || state.method_27852(class_2246.field_10260) || state.method_27852(class_2246.field_47336) || state.method_27852(class_2246.field_48851);
   }

   private boolean isPortalOrSpecial(class_2680 state) {
      return state.method_26164(class_3481.field_21780) || state.method_27852(class_2246.field_10540) || state.method_27852(class_2246.field_22423) || state.method_27852(class_2246.field_9987) || state.method_27852(class_2246.field_10398) || state.method_27852(class_2246.field_10027) || state.method_27852(class_2246.field_10316) || state.method_27852(class_2246.field_10613) || state.method_27852(class_2246.field_10327) || state.method_27852(class_2246.field_10485) || state.method_27852(class_2246.field_23152) || state.method_27852(class_2246.field_10504) || state.method_27852(class_2246.field_10375);
   }

   private int meshSignature() {
      return ((Boolean)this.ores.getValue() ? 1 : 0) | ((Boolean)this.containers.getValue() ? 2 : 0) | ((Boolean)this.portals.getValue() ? 4 : 0) | ((Boolean)this.hideFluids.getValue() ? 8 : 0);
   }

   private void reloadChunks() {
      if (mc.field_1769 != null) {
         mc.field_1769.method_3279();
      }

   }
}
