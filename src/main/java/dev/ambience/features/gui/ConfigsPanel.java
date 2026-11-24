package dev.ambience.features.gui;

import dev.ambience.Ambience;
import dev.ambience.features.gui.items.TextBox;
import dev.ambience.manager.ConfigManager;
import dev.ambience.util.render.GuiFade;
import dev.ambience.util.render.RenderUtil;
import dev.ambience.util.render.ScissorUtil;
import dev.ambience.util.render.font.Fonts;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_332;

public final class ConfigsPanel {
   private static final float ROW_H = 22.0F;
   private static final float FIELD_H = 15.0F;
   private static final float ROW_GAP = 4.0F;
   private static final float ACTION_GAP = 4.0F;
   private static final float BLOCK_GAP = 6.0F;
   private final TextBox localName = (new TextBox()).placeholder("Local name").maxLength(48);
   private String status;
   private float scroll;
   private long lastRefreshMs;
   private final CenteredStyle.Motion contentFade;
   private final CenteredStyle.Motion contentSlide;
   private final Map<String, CenteredStyle.Motion> rowHover;
   private List<String> local;
   private float layoutX;
   private float layoutW;
   private float listTop;
   private float listBottom;
   private float fieldY;
   private float rowsY;
   private float bodyStartY;

   public ConfigsPanel() {
      this.status = "Local profiles.";
      this.contentFade = new CenteredStyle.Motion(1.0F);
      this.contentSlide = new CenteredStyle.Motion();
      this.rowHover = new HashMap();
      this.local = new ArrayList();
   }

   public void onOpen() {
      this.contentFade.value = 0.35F;
      this.contentSlide.value = 0.35F;
      this.refresh(true);
   }

   public void onCategorySelected() {
      this.contentFade.value = 0.4F;
      this.contentSlide.value = 0.3F;
      this.refresh(true);
   }

   public void render(class_332 g, int left, int top, int right, int bottom, int mx, int my, Color paint) {
      this.layoutX = (float)left + 8.0F;
      this.layoutW = (float)(right - left) - 16.0F;
      this.listTop = (float)top + 2.0F;
      this.listBottom = (float)bottom - 4.0F;
      float fade = this.contentFade.to(1.0F, 130.0F, 22.0F);
      float slide = this.contentSlide.to(0.0F, 140.0F, 24.0F);
      float y = this.listTop;
      Fonts.drawString(g, "Local profiles", this.layoutX, y, -1);
      y += (float)Fonts.lineHeight() + 6.0F;

      this.bodyStartY = y;
      float contentH = this.measureBodyHeight();
      float viewH = Math.max(1.0F, this.listBottom - this.bodyStartY);
      this.scroll = clamp(this.scroll, 0.0F, Math.max(0.0F, contentH - viewH));
      ScissorUtil.enable(g, left, Math.round(this.bodyStartY - 1.0F), right, Math.round(this.listBottom));
      float saved = GuiFade.alpha;
      GuiFade.alpha = saved * clamp01(fade);
      g.method_51448().pushMatrix();
      float ox = this.layoutX + this.layoutW * 0.5F;
      float oy = this.bodyStartY + 20.0F;
      g.method_51448().translate(ox + slide * 14.0F, oy + slide * 3.0F);
      float scale = 0.98F + 0.02F * clamp01(fade);
      g.method_51448().scale(scale, scale);
      g.method_51448().translate(-ox, -oy);
      float by = this.bodyStartY - this.scroll;
      this.renderLocalBody(g, by, mx, my, paint);
      g.method_51448().popMatrix();
      GuiFade.alpha = saved;
      ScissorUtil.disable(g);
      this.cacheBodyPositions(this.bodyStartY);
   }

   public boolean mouseClicked(int mx, int my, int button, int left, int top, int right, int bottom) {
      if (button != 0) {
         return false;
      }
      this.layoutX = (float)left + 8.0F;
      this.layoutW = (float)(right - left) - 16.0F;
      this.listTop = (float)top + 2.0F;
      this.listBottom = (float)bottom - 4.0F;
      float x = this.layoutX;
      float w = this.layoutW;
      float bodyStart = this.listTop + (float)Fonts.lineHeight() + 6.0F;
      this.cacheBodyPositions(bodyStart);
      return this.clickLocalBody(mx, my, x, w);
   }

   public boolean mouseScrolled(double amount, int left, int top, int right, int bottom) {
      float bodyStart = this.listTop + (float)Fonts.lineHeight() + 6.0F;
      float contentH = this.measureBodyHeight();
      float viewH = Math.max(1.0F, (float)bottom - 4.0F - bodyStart);
      this.scroll = clamp(this.scroll - (float)amount * 16.0F, 0.0F, Math.max(0.0F, contentH - viewH));
      return true;
   }

   private float renderLocalBody(class_332 g, float y, int mx, int my, Color paint) {
      this.fieldY = y;
      y = this.drawSaveRow(g, this.localName, this.layoutX, y, this.layoutW, mx, my, paint, "Save");
      Fonts.drawString(g, truncate(this.status, (int)this.layoutW), this.layoutX, y, -3880238);
      y += (float)Fonts.lineHeight() + 6.0F;
      this.rowsY = y;
      if (this.local.isEmpty()) {
         Fonts.drawString(g, "No local profiles yet.", this.layoutX, y + 3.0F, -3880238);
         return y + 22.0F + 4.0F;
      } else {
         int i = 0;
         for(String name : this.local) {
            this.drawLocalRow(g, name, this.layoutX, y, this.layoutW, mx, my, paint, i++);
            y += 26.0F;
         }
         return y;
      }
   }

   private void cacheBodyPositions(float contentTop) {
      float y = contentTop - this.scroll;
      this.fieldY = y;
      y += 20.0F + (float)Fonts.lineHeight() + 6.0F;
      this.rowsY = y;
   }

   private float measureBodyHeight() {
      int rows = Math.max(1, this.local.size());
      return 20.0F + (float)Fonts.lineHeight() + 6.0F + (float)rows * 26.0F + 8.0F;
   }

   private boolean clickLocalBody(int mx, int my, float x, float w) {
      float fieldR = x + w - 66.0F;
      if (hit(mx, my, x, this.fieldY, fieldR, this.fieldY + 15.0F)) {
         this.localName.focus();
         return true;
      } else if (hit(mx, my, fieldR + 5.0F, this.fieldY, x + w, this.fieldY + 15.0F)) {
         TextBox.unfocusAll();
         this.saveLocal();
         return true;
      } else {
         float rowY = this.rowsY;
         if (this.local.isEmpty()) {
            TextBox.unfocusAll();
            return true;
         } else {
            for(String name : this.local) {
               if (hit(mx, my, x, rowY, x + w, rowY + 22.0F)) {
                  return this.clickLocalRow(name, mx, x, w);
               }
               rowY += 26.0F;
            }
            TextBox.unfocusAll();
            return true;
         }
      }
   }

   private float drawSaveRow(class_332 g, TextBox box, float x, float y, float w, int mx, int my, Color paint, String action) {
      float fieldR = x + w - 66.0F;
      boolean hoverField = hit(mx, my, x, y, fieldR, y + 15.0F);
      box.renderPill(g, x, y, fieldR, y + 15.0F, paint, hoverField, 6.0F);
      boolean hoverSave = hit(mx, my, fieldR + 5.0F, y, x + w, y + 15.0F);
      this.drawChip(g, fieldR + 5.0F, y, x + w, y + 15.0F, action, paint, hoverSave);
      return y + 15.0F + 5.0F;
   }

   private void drawLocalRow(class_332 g, String name, float x, float y, float w, int mx, int my, Color paint, int index) {
      float enter = this.rowEnter(index);
      if (!(enter <= 0.01F)) {
         float saved = GuiFade.alpha;
         GuiFade.alpha = saved * enter;
         g.method_51448().pushMatrix();
         g.method_51448().translate((1.0F - enter) * 5.0F, (1.0F - enter) * 4.0F);
         boolean hover = hit(mx, my, x, y, x + w, y + 22.0F);
         float hot = motion(this.rowHover, "local:" + name).to(hover ? 1.0F : 0.0F, 200.0F, 26.0F);
         int fill = CenteredStyle.mix(CenteredStyle.ROW_BG, CenteredStyle.ROW_HOVER, clamp01(hot));
         RenderUtil.roundRect(g, x + hot * 1.0F, y, x + w, y + 22.0F, 6.0F, fill);
         Fonts.drawString(g, truncate(name, (int)(w - 78.0F)), x + 6.0F + hot * 1.0F, y + (22.0F - (float)Fonts.lineHeight()) * 0.5F, -1);
         float bx = x + w - 3.0F;
         bx = this.drawAction(g, "Del", bx, y, mx, my, paint) - 4.0F;
         this.drawAction(g, "Load", bx, y, mx, my, paint);
         g.method_51448().popMatrix();
         GuiFade.alpha = saved;
      }
   }

   private float drawAction(class_332 g, String label, float right, float rowY, int mx, int my, Color paint) {
      float tw = (float)Fonts.width(label) + 10.0F;
      float x1 = right - tw;
      float y1 = rowY + 4.0F;
      float y2 = rowY + 22.0F - 4.0F;
      boolean hover = hit(mx, my, x1, y1, right, y2);
      float hot = motion(this.rowHover, "act:" + label + ":" + (int)x1).to(hover ? 1.0F : 0.0F, 210.0F, 26.0F);
      int fill = CenteredStyle.mix(CenteredStyle.CHIP_BG, GuiTheme.withAlpha(paint, 130).getRGB(), clamp01(hot));
      float lift = hot * 0.35F;
      RenderUtil.roundRect(g, x1, y1 - lift, right, y2 - lift, 5.0F, fill);
      Fonts.drawString(g, label, x1 + 5.0F, y1 - lift + (y2 - y1 - (float)Fonts.lineHeight()) * 0.5F, -1644050);
      return x1;
   }

   private float rowEnter(int index) {
      return 1.0F;
   }

   private void drawChip(class_332 g, float x1, float y1, float x2, float y2, String label, Color paint, boolean hot) {
      RenderUtil.roundRect(g, x1, y1, x2, y2, 6.0F, hot ? GuiTheme.withAlpha(paint, 130).getRGB() : CenteredStyle.CHIP_BG);
      float tw = (float)Fonts.width(label);
      Fonts.drawString(g, label, x1 + (x2 - x1 - tw) * 0.5F, y1 + (y2 - y1 - (float)Fonts.lineHeight()) * 0.5F, -1);
   }

   private boolean clickLocalRow(String name, int mx, float x, float w) {
      float bx = x + w - 3.0F;
      float delW = (float)Fonts.width("Del") + 10.0F;
      if ((float)mx >= bx - delW && (float)mx < bx) {
         this.deleteLocal(name);
         return true;
      } else {
         bx -= delW + 4.0F;
         float loadW = (float)Fonts.width("Load") + 10.0F;
         if ((float)mx >= bx - loadW && (float)mx < bx) {
            this.loadLocal(name);
            return true;
         } else {
            return true;
         }
      }
   }

   private void saveLocal() {
      String name = this.localName.getText().trim();
      if (!ConfigManager.isValidProfileName(name)) {
         this.status = "Local name: 3–48 letters/numbers.";
      } else {
         try {
            Ambience.configManager.saveLocalProfile(name);
            this.status = "Saved local \"" + name + "\".";
            this.localName.clear();
            this.refresh(true);
         } catch (Exception var3) {
            this.status = "Local save failed.";
         }
      }
   }

   private void loadLocal(String name) {
      try {
         Ambience.configManager.loadLocalProfile(name);
         this.status = "Loaded local \"" + name + "\".";
      } catch (Exception var3) {
         this.status = "Failed to load local profile.";
      }
   }

   private void deleteLocal(String name) {
      try {
         Ambience.configManager.deleteLocalProfile(name);
         this.status = "Deleted local \"" + name + "\".";
         this.refresh(true);
      } catch (Exception var3) {
         this.status = "Failed to delete local profile.";
      }
   }

   private void refresh(boolean force) {
      long now = System.currentTimeMillis();
      if (force || now - this.lastRefreshMs >= 600L) {
         this.lastRefreshMs = now;
         this.local = Ambience.configManager != null ? Ambience.configManager.listLocalProfiles() : List.of();
         this.status = this.local.isEmpty() ? "No local profiles yet." : this.local.size() + " local profile" + (this.local.size() == 1 ? "" : "s") + ".";
      }
   }

   private static boolean hit(int mx, int my, float x1, float y1, float x2, float y2) {
      return (float)mx >= x1 && (float)mx < x2 && (float)my >= y1 && (float)my < y2;
   }

   private static CenteredStyle.Motion motion(Map<String, CenteredStyle.Motion> map, String key) {
      return (CenteredStyle.Motion)map.computeIfAbsent(key, (ignored) -> new CenteredStyle.Motion());
   }

   private static float clamp01(float v) {
      return Math.max(0.0F, Math.min(1.0F, v));
   }

   private static String truncate(String s, int maxPx) {
      if (s == null) {
         return "";
      } else if (Fonts.width(s) <= maxPx) {
         return s;
      } else {
         String out;
         for(out = s; out.length() > 1 && Fonts.width(out + "…") > maxPx; out = out.substring(0, out.length() - 1)) {
         }
         return out + "…";
      }
   }

   private static float clamp(float v, float min, float max) {
      return Math.max(min, Math.min(max, v));
   }
}
