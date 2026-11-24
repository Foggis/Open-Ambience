package dev.ambience.features.modules.render;

import dev.ambience.event.impl.render.Render3DEvent;
import dev.ambience.features.modules.Module;
import dev.ambience.features.settings.Bind;
import dev.ambience.features.settings.Setting;
import dev.ambience.nativebridge.NativeModules;
import dev.ambience.util.BlockLists;
import dev.ambience.util.render.EspDraw;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.class_1799;
import net.minecraft.class_1923;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_4076;

public class BlockEspModule extends Module {
   private static final int MAX_BOXES = 512;
   private final Setting<Float> range = this.num("Range", 32.0F, 8.0F, 64.0F).setPage("General");
   private final Setting<String> blocks = this.str("Blocks", "").setPage("General").setBlockList(true);
   private final Setting<Bind> pick = this.key("Pick", Bind.none()).setPage("General");
   private final Setting<Boolean> fill = this.bool("Fill", true).setPage("General");
   private final Setting<Boolean> tracers = this.bool("Tracers", false).setPage("General");
   private final Setting<Boolean> throughWalls = this.bool("ThroughWalls", true).setPage("General");
   private final Setting<Color> color = this.color("Color", 180, 80, 255, 220).setPage("Colors");
   private final Set<class_2248> watched = new LinkedHashSet();
   private final List<class_2338> cache = new ArrayList();
   private String parsedList = "";
   private int scanTick;
   private boolean pickHeld;

   public BlockEspModule() {
      super("BlockESP", "Boxes on block types you add, pick, or search.", Module.Category.VISUALS);
   }

   public String getDisplayInfo() {
      this.refreshWatched();
      if (this.watched.isEmpty()) {
         return "none";
      } else {
         return this.watched.size() == 1 ? displayName((class_2248)this.watched.iterator().next()) : String.valueOf(this.watched.size());
      }
   }

   public void onEnable() {
      this.parsedList = "";
      this.refreshWatched();
      this.rescan();
   }

   public void onDisable() {
      this.cache.clear();
      this.scanTick = 0;
      this.pickHeld = false;
   }

   public void onTick() {
      if (nullCheck()) {
         this.cache.clear();
         this.pickHeld = false;
      } else {
         this.handlePick();
         boolean listChanged = this.refreshWatched();
         if (this.watched.isEmpty()) {
            this.cache.clear();
         } else {
            if (NativeModules.blockEspScan(this.scanTick++, listChanged, this.cache.isEmpty())) {
               this.rescan();
            }

         }
      }
   }

   public void onRender3D(Render3DEvent event) {
      if (!nullCheck() && !this.cache.isEmpty()) {
         Color boxColor = this.color.getValue();
         boolean walls = (Boolean)this.throughWalls.getValue();
         boolean drawFill = (Boolean)this.fill.getValue();
         boolean drawTracers = (Boolean)this.tracers.getValue();

         for(class_2338 pos : this.cache) {
            class_238 box = new class_238(pos);
            EspDraw.box(box, boxColor, drawFill, walls);
            if (drawTracers) {
               EspDraw.tracer(box.method_1005(), boxColor, walls);
            }
         }

      }
   }

   private void handlePick() {
      boolean down = mc.field_1755 == null && ((Bind)this.pick.getValue()).isDown();
      if (!down) {
         this.pickHeld = false;
      } else if (!this.pickHeld) {
         this.pickHeld = true;
         class_2248 target = candidate();
         if (target != null) {
            this.toggleBlock(target);
         }
      }
   }

   private void toggleBlock(class_2248 block) {
      this.refreshWatched();
      boolean removed = this.watched.contains(block);
      if (removed) {
         this.watched.remove(block);
      } else {
         this.watched.add(block);
      }

      this.writeWatched();
      if (mc.field_1724 != null) {
         mc.field_1724.method_7353(class_2561.method_43470((removed ? "- " : "+ ") + displayName(block)), true);
      }

   }

   private void writeWatched() {
      String formatted = format(this.watched);
      this.parsedList = formatted;
      this.blocks.setValue(formatted);
      this.rescan();
   }

   private boolean refreshWatched() {
      String raw = this.blocks.getValue();
      if (raw == null) {
         raw = "";
      }

      if (raw.equals(this.parsedList)) {
         return false;
      } else {
         this.parsedList = raw;
         this.watched.clear();
         this.watched.addAll(parse(raw));
         return true;
      }
   }

   private void rescan() {
      this.cache.clear();
      if (!nullCheck() && !this.watched.isEmpty()) {
         double max = (double)(Float)this.range.getValue();
         double maxSq = max * max;
         class_243 eyes = mc.field_1724.method_33571();
         int chunkR = Math.max(1, (int)Math.ceil(max / (double)16.0F));
         class_1923 origin = mc.field_1724.method_31476();
         int minY = (int)Math.floor(eyes.field_1351 - max);
         int maxY = (int)Math.ceil(eyes.field_1351 + max);

         for(int cx = origin.field_9181 - chunkR; cx <= origin.field_9181 + chunkR && this.cache.size() < 512; ++cx) {
            for(int cz = origin.field_9180 - chunkR; cz <= origin.field_9180 + chunkR && this.cache.size() < 512; ++cz) {
               class_2818 chunk = mc.field_1687.method_2935().method_21730(cx, cz);
               if (chunk != null) {
                  this.scanChunk(chunk, eyes, maxSq, minY, maxY);
               }
            }
         }

      }
   }

   private void scanChunk(class_2818 chunk, class_243 eyes, double maxSq, int minY, int maxY) {
      int baseX = chunk.method_12004().method_8326();
      int baseZ = chunk.method_12004().method_8328();

      for(int i = 0; i < chunk.method_32890() && this.cache.size() < 512; ++i) {
         class_2826 section = chunk.method_38259(i);
         if (section != null && !section.method_38292()) {
            int originY = class_4076.method_18688(chunk.method_31604(i));
            if (originY + 16 >= minY && originY <= maxY) {
               for(int y = 0; y < 16 && this.cache.size() < 512; ++y) {
                  int wy = originY + y;
                  if (wy >= minY && wy <= maxY) {
                     for(int x = 0; x < 16 && this.cache.size() < 512; ++x) {
                        for(int z = 0; z < 16; ++z) {
                           class_2680 state = section.method_12254(x, y, z);
                           if (state != null && !state.method_26215() && this.watched.contains(state.method_26204())) {
                              double px = (double)(baseX + x) + (double)0.5F;
                              double pz = (double)(baseZ + z) + (double)0.5F;
                              if (NativeModules.blockEspAccept(eyes.method_1028(px, (double)wy + (double)0.5F, pz), maxSq, this.cache.size(), 512)) {
                                 this.cache.add(new class_2338(baseX + x, wy, baseZ + z));
                                 if (this.cache.size() >= 512) {
                                    return;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

   }

   public static class_2248 candidate() {
      return BlockLists.candidate();
   }

   public static class_2248 lookingBlock() {
      return BlockLists.lookingBlock();
   }

   public static class_2248 heldBlock() {
      return BlockLists.heldBlock();
   }

   public static Set<class_2248> parse(String raw) {
      return BlockLists.parse(raw);
   }

   public static String format(Collection<class_2248> blocks) {
      return BlockLists.format(blocks);
   }

   public static class_2248 resolve(String token) {
      return BlockLists.resolve(token);
   }

   public static List<class_2248> suggest(String query, int limit) {
      return BlockLists.suggest(query, limit);
   }

   public static String displayName(class_2248 block) {
      return BlockLists.displayName(block);
   }

   public static String id(class_2248 block) {
      return BlockLists.id(block);
   }

   public static class_1799 icon(class_2248 block) {
      return BlockLists.icon(block);
   }

   public static int suggestLimit() {
      return 6;
   }
}
