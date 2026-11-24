package dev.ambience.features.modules.render;

import dev.ambience.event.impl.render.Render3DEvent;
import dev.ambience.features.modules.Module;
import dev.ambience.features.settings.Setting;
import dev.ambience.nativebridge.NativeModules;
import dev.ambience.util.render.EspDraw;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1923;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2586;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_3481;

public class StorageEspModule extends Module {
   private final Setting<Boolean> chests = this.bool("Chests", true).setPage("General");
   private final Setting<Boolean> enderChests = this.bool("EnderChests", true).setPage("General");
   private final Setting<Boolean> barrels = this.bool("Barrels", true).setPage("General");
   private final Setting<Boolean> shulkers = this.bool("Shulkers", true).setPage("General");
   private final Setting<Boolean> hoppers = this.bool("Hoppers", false).setPage("General");
   private final Setting<Boolean> furnaces = this.bool("Furnaces", false).setPage("General");
   private final Setting<Float> range = this.num("Range", 64.0F, 16.0F, 128.0F).setPage("General");
   private final Setting<Boolean> fill = this.bool("Fill", true).setPage("General");
   private final Setting<Boolean> tracers = this.bool("Tracers", false).setPage("General");
   private final Setting<Boolean> throughWalls = this.bool("ThroughWalls", true).setPage("General");
   private final Setting<Color> chestColor = this.color("Chest", 220, 170, 50, 220).setPage("Colors");
   private final Setting<Color> enderColor = this.color("Ender", 160, 50, 200, 220).setPage("Colors");
   private final Setting<Color> shulkerColor = this.color("Shulker", 200, 80, 180, 220).setPage("Colors");
   private final Setting<Color> otherColor = this.color("Other", 140, 140, 140, 220).setPage("Colors");
   private final List<Entry> cache = new ArrayList();

   public StorageEspModule() {
      super("StorageESP", "Boxes and tracers on chests and other containers.", Module.Category.VISUALS);
   }

   public void onDisable() {
      this.cache.clear();
   }

   public void onTick() {
      this.cache.clear();
      if (!nullCheck()) {
         double max = (double)(Float)this.range.getValue();
         double maxSq = max * max;
         int enabledKinds = ((Boolean)this.chests.getValue() ? 1 : 0) | ((Boolean)this.enderChests.getValue() ? 2 : 0) | ((Boolean)this.shulkers.getValue() ? 4 : 0) | ((Boolean)this.barrels.getValue() ? 8 : 0) | ((Boolean)this.hoppers.getValue() ? 16 : 0) | ((Boolean)this.furnaces.getValue() ? 32 : 0);
         class_243 eyes = mc.field_1724.method_33571();
         int chunkR = Math.max(1, (int)Math.ceil(max / (double)16.0F) + 1);
         class_1923 origin = mc.field_1724.method_31476();

         for(int cx = origin.field_9181 - chunkR; cx <= origin.field_9181 + chunkR; ++cx) {
            for(int cz = origin.field_9180 - chunkR; cz <= origin.field_9180 + chunkR; ++cz) {
               class_2818 chunk = mc.field_1687.method_2935().method_21730(cx, cz);
               if (chunk != null) {
                  for(class_2338 pos : chunk.method_12021()) {
                     class_2586 entity = mc.field_1687.method_8321(pos);
                     if (entity != null) {
                        int kind = NativeModules.storageEspKind(this.kindFor(entity.method_11010()), enabledKinds, eyes.method_1028((double)pos.method_10263() + (double)0.5F, (double)pos.method_10264() + (double)0.5F, (double)pos.method_10260() + (double)0.5F), maxSq);
                        if (kind >= 0) {
                           this.cache.add(new Entry(pos.method_10062(), this.colorForKind(kind)));
                        }
                     }
                  }
               }
            }
         }

      }
   }

   public void onRender3D(Render3DEvent event) {
      if (!nullCheck() && !this.cache.isEmpty()) {
         boolean walls = (Boolean)this.throughWalls.getValue();
         boolean drawFill = (Boolean)this.fill.getValue();
         boolean drawTracers = (Boolean)this.tracers.getValue();

         for(Entry entry : this.cache) {
            class_238 box = new class_238(entry.pos);
            EspDraw.box(box, entry.color, drawFill, walls);
            if (drawTracers) {
               EspDraw.tracer(box.method_1005(), entry.color, walls);
            }
         }

      }
   }

   private int kindFor(class_2680 state) {
      if (state == null) {
         return -1;
      } else if (!state.method_27852(class_2246.field_10034) && !state.method_27852(class_2246.field_10380)) {
         if (state.method_27852(class_2246.field_10443)) {
            return 1;
         } else if (state.method_26164(class_3481.field_21490)) {
            return 2;
         } else if (state.method_27852(class_2246.field_16328)) {
            return 3;
         } else if (!state.method_27852(class_2246.field_10312) && !state.method_27852(class_2246.field_10228) && !state.method_27852(class_2246.field_10200)) {
            return !state.method_27852(class_2246.field_10181) && !state.method_27852(class_2246.field_16333) && !state.method_27852(class_2246.field_16334) && !state.method_27852(class_2246.field_10333) ? -1 : 5;
         } else {
            return 4;
         }
      } else {
         return 0;
      }
   }

   private Color colorForKind(int kind) {
      Color var10000;
      switch (kind) {
         case 0 -> var10000 = this.chestColor.getValue();
         case 1 -> var10000 = this.enderColor.getValue();
         case 2 -> var10000 = this.shulkerColor.getValue();
         default -> var10000 = this.otherColor.getValue();
      }

      return var10000;
   }

   private static record Entry(class_2338 pos, Color color) {
   }
}
