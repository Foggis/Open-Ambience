package dev.ambience.features.modules.render;

import dev.ambience.features.modules.Module;
import dev.ambience.features.settings.Setting;
import dev.ambience.hooks.CustomSkyboxHook;
import dev.ambience.nativebridge.NativeModules;
import java.awt.Color;
import net.minecraft.class_12076;

public class CustomSkyboxModule extends Module {
   private static CustomSkyboxModule INSTANCE;
   private final Setting<Preset> preset;
   private final Setting<Color> topColor;
   private final Setting<Color> horizonColor;
   private final Setting<Boolean> hideSunMoon;
   private final Setting<Boolean> brightStars;
   private final Setting<Double> starBoost;

   public CustomSkyboxModule() {
      super("CustomSkybox", "Replaces the sky with custom color presets.", Module.Category.VISUALS);
      this.preset = this.mode("Preset", CustomSkyboxModule.Preset.Sunset).setPage("General");
      this.topColor = this.color("Top", 35, 60, 140, 255).setPage("General");
      this.horizonColor = this.color("Horizon", 255, 140, 70, 255).setPage("General");
      this.hideSunMoon = this.bool("HideSunMoon", true).setPage("General");
      this.brightStars = this.bool("BrightStars", true).setPage("General");
      this.starBoost = this.num("StarBoost", 1.35, (double)0.5F, (double)3.0F).setPage("General");
      this.topColor.setVisibility((v) -> this.preset.getValue() == CustomSkyboxModule.Preset.Custom);
      this.horizonColor.setVisibility((v) -> this.preset.getValue() == CustomSkyboxModule.Preset.Custom);
      INSTANCE = this;
      CustomSkyboxHook.bind(new CustomSkyboxHook.Impl() {
         public boolean enabled() {
            return NativeModules.skyboxEnabled(CustomSkyboxModule.this.isEnabled());
         }

         public boolean hideSunMoon() {
            return CustomSkyboxModule.this.hideSunMoon();
         }

         public void apply(class_12076 state) {
            CustomSkyboxModule.this.apply(state);
         }
      });
   }

   public static CustomSkyboxModule getInstance() {
      return INSTANCE;
   }

   public boolean hideSunMoon() {
      return NativeModules.skyboxHide(this.isEnabled(), (Boolean)this.hideSunMoon.getValue());
   }

   public void apply(class_12076 state) {
      int[] colors = NativeModules.skyboxColors(((Preset)this.preset.getValue()).ordinal(), ((Color)this.topColor.getValue()).getRGB(), ((Color)this.horizonColor.getValue()).getRGB());
      if (colors != null && colors.length == 2) {
         state.field_63097 = colors[0];
         state.field_63095 = colors[1];
         state.field_63094 = NativeModules.skyboxStars(state.field_63094, (Boolean)this.brightStars.getValue(), ((Double)this.starBoost.getValue()).floatValue());
      }
   }

   public static enum Preset {
      Sunset,
      Nebula,
      Midnight,
      Aurora,
      Candy,
      Custom;

      // $FF: synthetic method
      private static Preset[] $values() {
         return new Preset[]{Sunset, Nebula, Midnight, Aurora, Candy, Custom};
      }
   }
}
