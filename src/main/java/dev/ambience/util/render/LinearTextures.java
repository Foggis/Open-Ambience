package dev.ambience.util.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_1011.class_1012;
import org.lwjgl.system.MemoryUtil;

public final class LinearTextures {
   private static final AtomicInteger IDS = new AtomicInteger();

   private LinearTextures() {
   }

   public static class_2960 register(String name, class_1011 image) {
      int id = IDS.getAndIncrement();
      LinearDynamicTexture texture = new LinearDynamicTexture(() -> "Ambience/" + name + id, image);
      texture.method_4524();
      class_2960 key = class_2960.method_60655("ambience", "linear/" + name + "_" + id);
      class_310.method_1551().method_1531().method_4616(key, texture);
      return key;
   }

   public static class_1011 fromArgb(int[] rgb, int width, int height) {
      class_1011 image = new class_1011(class_1012.field_4997, width, height, true);
      IntBuffer buf = MemoryUtil.memIntBuffer(image.method_67769(), width * height);

      for(int p : rgb) {
         int a = p >>> 24 & 255;
         int r = p >>> 16 & 255;
         int g = p >>> 8 & 255;
         int b = p & 255;
         buf.put(a << 24 | b << 16 | g << 8 | r);
      }

      return image;
   }

   public static class_1011 readPng(InputStream in) throws IOException {
      return class_1011.method_4309(in);
   }

   private static final class LinearDynamicTexture extends class_1043 {
      LinearDynamicTexture(Supplier<String> label, class_1011 image) {
         super(label, image);
         this.field_63613 = RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR);
      }
   }
}
