package dev.ambience.features.modules.client;

import dev.ambience.event.impl.render.Render2DEvent;
import dev.ambience.features.modules.Module;
import dev.ambience.features.settings.Setting;
import dev.ambience.loader.PayloadLoader;
import dev.ambience.util.render.font.Fonts;
import net.minecraft.class_332;

public final class SessionPillModule extends Module {
   private final Setting<Position> position;
   private final Setting<Double> scale;
   private final Setting<Double> opacity;
   private final Setting<Boolean> showBrand;
   private final Setting<Boolean> showUid;
   private final Setting<Boolean> showUsername;

   public SessionPillModule() {
      super("SessionPill", "Shows the Ambience account badge while playing.", Module.Category.CLIENT);
      this.position = this.mode("Position", SessionPillModule.Position.TOP_LEFT).setPage("Layout");
      this.scale = this.num("Scale", (double)1.0F, (double)0.75F, (double)2.0F).setPage("Layout");
      this.opacity = this.num("Opacity", 0.82, (double)0.25F, (double)1.0F).setPage("Style");
      this.showBrand = this.bool("Show Brand", true).setPage("Content");
      this.showUid = this.bool("Show UID", true).setPage("Content");
      this.showUsername = this.bool("Show Username", true).setPage("Content");
      this.enabled.setValue(true);
      this.bindMode.setVisibility((value) -> false);
   }

   public String hudModel(long uid) {
      long var10000 = Math.max(0L, uid);
      return "H\t" + var10000 + "\t" + ((Position)this.position.getValue()).ordinal() + "\t" + String.valueOf(this.scale.getValue()) + "\t" + String.valueOf(this.opacity.getValue()) + "\t" + ((Boolean)this.showBrand.getValue() ? '1' : '0') + "\t" + ((Boolean)this.showUid.getValue() ? '1' : '0') + "\t" + ((Boolean)this.showUsername.getValue() ? '1' : '0') + "\n";
   }

   public void onRender2D(Render2DEvent event) {
      if (!this.isEnabled() || event == null || event.getContext() == null || mc == null || mc.field_1724 == null || mc.field_1687 == null) {
         return;
      }
      StringBuilder sb = new StringBuilder();
      if ((Boolean) this.showBrand.getValue()) {
         sb.append("Ambience");
      }
      if ((Boolean) this.showUid.getValue()) {
         if (sb.length() > 0) {
            sb.append(" | ");
         }
         sb.append("UID ").append(PayloadLoader.accountUid());
      }
      if ((Boolean) this.showUsername.getValue() && mc.method_1548() != null) {
         String username = mc.method_1548().method_1676();
         if (username != null && !username.isEmpty()) {
            if (sb.length() > 0) {
               sb.append(" | ");
            }
            sb.append(username);
         }
      }
      String text = sb.toString();
      if (text.isEmpty()) {
         return;
      }
      float scale = ((Double) this.scale.getValue()).floatValue();
      float opacity = ((Double) this.opacity.getValue()).floatValue();
      int color = ((int) (Math.max(0.0F, Math.min(1.0F, opacity)) * 255.0F) << 24) | 0xFFFFFF;
      class_332 g = event.getContext();
      float w = Fonts.width(text) * scale;
      float h = Fonts.lineHeight() * scale;
      float screenW = mc.method_22683().method_4486();
      float screenH = mc.method_22683().method_4502();
      float x = 8.0F;
      float y = 8.0F;
      switch ((Position) this.position.getValue()) {
         case TOP_RIGHT -> x = screenW - 8.0F - w;
         case BOTTOM_LEFT -> y = screenH - 8.0F - h;
         case BOTTOM_RIGHT -> {
            x = screenW - 8.0F - w;
            y = screenH - 8.0F - h;
         }
         default -> {
         }
      }
      g.method_51448().pushMatrix();
      g.method_51448().translate(x, y);
      g.method_51448().scale(scale, scale);
      Fonts.drawString(g, text, 0.0F, 0.0F, color);
      g.method_51448().popMatrix();
   }

   public static enum Position {
      TOP_LEFT,
      TOP_RIGHT,
      BOTTOM_LEFT,
      BOTTOM_RIGHT;

      // $FF: synthetic method
      private static Position[] $values() {
         return new Position[]{TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT};
      }
   }
}
