package dev.ambience.features.modules.render;

import dev.ambience.features.modules.Module;
import dev.ambience.features.settings.Setting;
import dev.ambience.nativebridge.NativeModules;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_2394;
import net.minecraft.class_2398;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import org.joml.Vector2f;

public class WorldParticlesModule extends Module {
   private static WorldParticlesModule INSTANCE;
   private final Setting<Style> style;
   private final Setting<Double> density;
   private final Setting<Double> range;
   private final Setting<Double> height;
   private final Setting<Vector2f> speed;
   private final Setting<Double> circleSize;

   public WorldParticlesModule() {
      super("WorldParticles", "Ambient stars, rings, and falling particles around you.", Module.Category.VISUALS);
      this.style = this.mode("Style", WorldParticlesModule.Style.Mix).setPage("General");
      this.density = this.num("Density", (double)4.0F, (double)1.0F, (double)10.0F).setPage("General");
      this.range = this.num("Range", (double)18.0F, (double)6.0F, (double)48.0F).setPage("General");
      this.height = this.num("Height", (double)10.0F, (double)3.0F, (double)24.0F).setPage("General");
      this.speed = this.vec2f("Speed", 0.02F, 0.06F).setPage("General");
      this.circleSize = this.num("CircleSize", (double)2.5F, (double)1.0F, (double)6.0F).setPage("General");
      this.circleSize.setVisibility((v) -> this.style.getValue() == WorldParticlesModule.Style.Circles || this.style.getValue() == WorldParticlesModule.Style.Mix);
      INSTANCE = this;
   }

   public static WorldParticlesModule getInstance() {
      return INSTANCE;
   }

   public void onDisable() {
      NativeModules.resetWorldParticles();
   }

   public void onTick() {
      if (!nullCheck()) {
         if (!mc.field_1687.method_8597().comp_643()) {
            int plan = NativeModules.worldParticlesPlan((Double)this.density.getValue(), ((Style)this.style.getValue()).ordinal(), ThreadLocalRandom.current().nextLong());
            if (plan >= 0) {
               Style active = WorldParticlesModule.Style.values()[plan >>> 8 & 255];
               int count = plan & 255;
               switch (active.ordinal()) {
                  case 0 -> this.spawnStars(count);
                  case 1 -> this.spawnCircle();
                  case 2 -> this.spawnFalling(count);
               }

            }
         }
      }
   }

   private void spawnStars(int count) {
      class_243 origin = mc.field_1724.method_73189();
      double radius = (Double)this.range.getValue();
      double maxHeight = (Double)this.height.getValue();

      for(int i = 0; i < count; ++i) {
         double x = origin.field_1352 + this.randomOffset(radius);
         double y = origin.field_1351 + ThreadLocalRandom.current().nextDouble((double)2.0F, maxHeight);
         double z = origin.field_1350 + this.randomOffset(radius);
         this.spawn(class_2398.field_11207, x, y, z, this.jitter(), this.randomUp(), this.jitter());
         if (ThreadLocalRandom.current().nextBoolean()) {
            this.spawn(class_2398.field_28479, x, y, z, (double)0.0F, (double)0.0F, (double)0.0F);
         }
      }

   }

   private void spawnCircle() {
      class_243 origin = mc.field_1724.method_73189();
      double radius = (Double)this.circleSize.getValue();
      double y = origin.field_1351 + (double)1.5F + ThreadLocalRandom.current().nextDouble((Double)this.height.getValue() * 0.35);
      int points = 10 + (int)Math.round((Double)this.density.getValue() * (double)2.0F);
      double angleOffset = ThreadLocalRandom.current().nextDouble((Math.PI * 2D));

      for(int i = 0; i < points; ++i) {
         double angle = angleOffset + (Math.PI * 2D) * (double)i / (double)points;
         double x = origin.field_1352 + Math.cos(angle) * radius;
         double z = origin.field_1350 + Math.sin(angle) * radius;
         this.spawn(class_2398.field_29644, x, y, z, (double)0.0F, 0.01, (double)0.0F);
         this.spawn(class_2398.field_28479, x, y + 0.05, z, (double)0.0F, (double)0.0F, (double)0.0F);
      }

   }

   private void spawnFalling(int count) {
      class_243 origin = mc.field_1724.method_73189();
      double radius = (Double)this.range.getValue();
      double fallSpeed = (double)(this.speed.getValue()).y;

      for(int i = 0; i < count; ++i) {
         double x = origin.field_1352 + this.randomOffset(radius);
         double y = origin.field_1351 + (Double)this.height.getValue() + ThreadLocalRandom.current().nextDouble((double)2.0F, (double)6.0F);
         double z = origin.field_1350 + this.randomOffset(radius);
         class_2394 particle = ThreadLocalRandom.current().nextBoolean() ? class_2398.field_28802 : class_2398.field_20537;
         this.spawn(particle, x, y, z, this.jitter() * 0.2, -fallSpeed - ThreadLocalRandom.current().nextDouble(0.01), this.jitter() * 0.2);
      }

   }

   private void spawn(class_2394 type, double x, double y, double z, double vx, double vy, double vz) {
      mc.field_1687.method_17452(type, true, x, y, z, vx, vy, vz);
   }

   private double randomOffset(double radius) {
      return ThreadLocalRandom.current().nextDouble(-radius, radius);
   }

   private double jitter() {
      return class_3532.method_15350(ThreadLocalRandom.current().nextGaussian() * (double)(this.speed.getValue()).x, -0.08, 0.08);
   }

   private double randomUp() {
      return (double)(this.speed.getValue()).x + ThreadLocalRandom.current().nextDouble((double)0.0F, (double)(this.speed.getValue()).y);
   }

   public static enum Style {
      Stars,
      Circles,
      Fall,
      Mix;

      // $FF: synthetic method
      private static Style[] $values() {
         return new Style[]{Stars, Circles, Fall, Mix};
      }
   }
}
