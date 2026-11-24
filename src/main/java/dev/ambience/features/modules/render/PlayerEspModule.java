package dev.ambience.features.modules.render;

import dev.ambience.event.impl.render.Render3DEvent;
import dev.ambience.features.modules.Module;
import dev.ambience.features.settings.Setting;
import dev.ambience.hooks.PlayerEspHook;
import dev.ambience.nativebridge.NativeModules;
import dev.ambience.util.render.EspDraw;
import dev.ambience.util.traits.Util;
import java.awt.Color;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_238;
import net.minecraft.class_243;

public class PlayerEspModule extends Module {
   private final Setting<Boolean> self = this.bool("Self", false).setPage("General");
   private final Setting<Boolean> invisibles = this.bool("Invisibles", true).setPage("General");
   private final Setting<Boolean> tracers = this.bool("Tracers", false).setPage("General");
   private final Setting<Boolean> throughWalls = this.bool("ThroughWalls", true).setPage("General");
   private final Setting<Color> color = this.color("Fill Color", 255, 126, 24, 96).setPage("Style");
   private final Setting<Color> outline = this.color("Glow Color", 255, 181, 76, 255).setPage("Style");

   public PlayerEspModule() {
      super("PlayerESP", "Orange player chams with a bright glowing outline.", Module.Category.VISUALS);
      PlayerEspHook.bind(new PlayerEspHook.Impl() {
         public boolean applies(int entityId) {
            if (PlayerEspModule.this.isEnabled() && Util.mc.field_1687 != null) {
               class_1297 var3 = Util.mc.field_1687.method_8469(entityId);
               boolean var10000;
               if (var3 instanceof class_1657) {
                  class_1657 player = (class_1657)var3;
                  if (PlayerEspModule.this.valid(player)) {
                     var10000 = true;
                     return var10000;
                  }
               }

               var10000 = false;
               return var10000;
            } else {
               return false;
            }
         }

         public int fillColor() {
            return ((Color)PlayerEspModule.this.color.getValue()).getRGB();
         }

         public int outlineColor() {
            return ((Color)PlayerEspModule.this.outline.getValue()).getRGB();
         }
      });
   }

   public void onRender3D(Render3DEvent event) {
      if (!nullCheck()) {
         float delta = event.getDelta();
         Color c = this.color.getValue();
         boolean walls = (Boolean)this.throughWalls.getValue();
         boolean drawTracers = (Boolean)this.tracers.getValue();

         for(class_1657 player : mc.field_1687.method_18456()) {
            if (this.valid(player)) {
               class_238 box = interpolatedBox(player, delta);
               if (drawTracers) {
                  EspDraw.tracer(box.method_1005(), c, walls);
               }
            }
         }

      }
   }

   private boolean valid(class_1657 player) {
      return player != null && NativeModules.playerEspValid(player.method_5805(), player.method_7325(), player == mc.field_1724, (Boolean)this.self.getValue(), player.method_5767(), (Boolean)this.invisibles.getValue());
   }

   private static class_238 interpolatedBox(class_1657 player, float delta) {
      class_243 interp = player.method_30950(delta);
      return player.method_5829().method_997(interp.method_1020(player.method_73189()));
   }
}
