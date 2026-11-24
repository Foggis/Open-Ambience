package dev.ambience.features.gui.items.buttons;

import dev.ambience.features.gui.AmbienceGui;
import dev.ambience.features.gui.GuiTheme;
import dev.ambience.features.gui.Widget;
import dev.ambience.features.gui.items.TextBox;
import dev.ambience.features.settings.Setting;
import dev.ambience.util.BlockLists;
import dev.ambience.util.render.RenderUtil;
import dev.ambience.util.render.font.Fonts;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1109;
import net.minecraft.class_1799;
import net.minecraft.class_2248;
import net.minecraft.class_332;
import net.minecraft.class_3417;
import org.joml.Matrix3x2fStack;

public class BlockListButton extends SettingButton<String> {
   private static final int ICON = 8;
   private final TextBox search = (new TextBox()).placeholder("search blocks...").onChange(this::onQuery).onCommit(this::commitSearch).onCancel(this::clearSearch);
   private String query = "";
   private List<class_2248> selected = List.of();
   private List<class_2248> suggestions = List.of();

   public BlockListButton(Setting<String> setting) {
      super(setting);
      this.height = 24;
   }

   public int getHeight() {
      this.refresh();
      return 12 * (2 + this.selected.size() + this.suggestions.size());
   }

   public void drawScreen(class_332 context, int mouseX, int mouseY, float partialTicks) {
      this.refresh();
      Color accent = Widget.currentAccent != null ? Widget.currentAccent : new Color(70, 75, 82);
      int row = 0;
      this.drawAddRow(context, mouseX, mouseY, accent, row++);
      this.drawSearchRow(context, mouseX, mouseY, accent, row++);

      for(class_2248 block : this.selected) {
         this.drawBlockRow(context, mouseX, mouseY, accent, row++, block, true);
      }

      for(class_2248 block : this.suggestions) {
         this.drawBlockRow(context, mouseX, mouseY, accent, row++, block, false);
      }

   }

   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (mouseButton != 0) {
         if (!this.isHovering(mouseX, mouseY) && this.search.isFocused()) {
            this.search.unfocus();
         }

      } else if (!this.isHovering(mouseX, mouseY)) {
         if (this.search.isFocused()) {
            this.search.unfocus();
         }

      } else {
         int row = this.rowAt(mouseY);
         if (row == 0) {
            this.addCandidate();
         } else if (row == 1) {
            if (!this.search.isFocused()) {
               this.search.focus();
            }

         } else {
            int selectedIndex = row - 2;
            if (selectedIndex >= 0 && selectedIndex < this.selected.size()) {
               this.removeBlock((class_2248)this.selected.get(selectedIndex));
            } else {
               int suggestIndex = selectedIndex - this.selected.size();
               if (suggestIndex >= 0 && suggestIndex < this.suggestions.size()) {
                  this.addBlock((class_2248)this.suggestions.get(suggestIndex));
                  this.clearSearch();
               }

            }
         }
      }
   }

   public void onKeyPressed(int key) {
      if (this.search.isFocused() && key == 258 && !this.suggestions.isEmpty()) {
         this.addBlock((class_2248)this.suggestions.get(0));
         this.clearSearch();
      }

   }

   public void keyActivate() {
      this.search.focus();
   }

   private void drawAddRow(class_332 context, int mouseX, int mouseY, Color accent, int row) {
      float y1 = this.rowY(row);
      float y2 = y1 + 12.0F - 1.0F;
      boolean hovering = this.rowHover(mouseX, mouseY, row);
      if (hovering) {
         RenderUtil.rect(context, this.x, y1, this.x + (float)this.width, y2, GuiTheme.withAlpha(accent, 70).getRGB());
      }

      class_2248 candidate = BlockLists.candidate();
      String label;
      int color;
      if (candidate == null) {
         label = "Look at or hold a block";
         color = -3880238;
      } else {
         String var10000 = BlockLists.displayName(candidate);
         label = "+ " + var10000;
         color = hovering ? -1 : -1644050;
         this.drawIcon(context, candidate, this.x + 2.0F, y1 + 2.0F);
      }

      float textX = candidate == null ? this.x + 2.0F : this.x + 8.0F + 5.0F;
      this.drawScrollableString(label, (double)textX, (double)(y1 + 2.0F), color, (int)(this.x + (float)this.width - textX - 2.0F), hovering);
   }

   private void drawSearchRow(class_332 context, int mouseX, int mouseY, Color accent, int row) {
      float y1 = this.rowY(row);
      float y2 = y1 + 12.0F - 1.0F;
      boolean hovering = this.rowHover(mouseX, mouseY, row);
      this.search.renderPill(context, this.x, y1, this.x + (float)this.width, y2, accent, hovering, 3.0F);
   }

   private void drawBlockRow(class_332 context, int mouseX, int mouseY, Color accent, int row, class_2248 block, boolean selectedRow) {
      float y1 = this.rowY(row);
      float y2 = y1 + 12.0F - 1.0F;
      boolean hovering = this.rowHover(mouseX, mouseY, row);
      if (hovering) {
         RenderUtil.rect(context, this.x, y1, this.x + (float)this.width, y2, GuiTheme.withAlpha(accent, 70).getRGB());
      }

      this.drawIcon(context, block, this.x + 2.0F, y1 + 2.0F);
      String name = BlockLists.displayName(block);
      String mark = selectedRow ? "x" : "+";
      int markW = Fonts.width(mark);
      this.drawString(mark, (double)(this.x + (float)this.width - (float)markW - 3.0F), (double)(y1 + 2.0F), hovering ? -1 : -3880238);
      this.drawScrollableString(name, (double)(this.x + 8.0F + 5.0F), (double)(y1 + 2.0F), selectedRow ? -1644050 : -3880238, this.width - 8 - markW - 10, hovering);
   }

   private void drawIcon(class_332 context, class_2248 block, float x, float y) {
      class_1799 stack = BlockLists.icon(block);
      if (!stack.method_7960()) {
         Matrix3x2fStack pose = context.method_51448();
         pose.pushMatrix();
         pose.translate(x, y);
         pose.scale(0.5F, 0.5F);
         context.method_51427(stack, 0, 0);
         pose.popMatrix();
      }
   }

   private void refresh() {
      this.selected = new ArrayList(BlockLists.parse(this.setting.getValue()));
      this.suggestions = new ArrayList();
      if (!this.query.isBlank()) {
         for(class_2248 block : BlockLists.suggest(this.query, BlockLists.suggestLimit())) {
            if (!this.selected.contains(block)) {
               this.suggestions.add(block);
            }
         }

      }
   }

   private void addCandidate() {
      class_2248 candidate = BlockLists.candidate();
      if (candidate != null) {
         this.addBlock(candidate);
      }
   }

   private void addBlock(class_2248 block) {
      List<class_2248> next = new ArrayList(BlockLists.parse(this.setting.getValue()));
      if (!next.contains(block)) {
         next.add(block);
         this.setting.setValue(BlockLists.format(next));
         this.click();
      }
   }

   private void removeBlock(class_2248 block) {
      List<class_2248> next = new ArrayList(BlockLists.parse(this.setting.getValue()));
      if (next.remove(block)) {
         this.setting.setValue(BlockLists.format(next));
         this.click();
      }
   }

   private void onQuery(String text) {
      this.query = text == null ? "" : text;
   }

   private void commitSearch() {
      this.refresh();
      if (!this.suggestions.isEmpty()) {
         this.addBlock((class_2248)this.suggestions.get(0));
      } else {
         class_2248 exact = BlockLists.resolve(this.query);
         if (exact != null) {
            this.addBlock(exact);
         }
      }

      this.clearSearch();
   }

   private void clearSearch() {
      this.query = "";
      this.search.setText("");
      this.search.unfocus();
   }

   private void click() {
      mc.method_1483().method_4873(class_1109.method_47978(class_3417.field_15015, 1.0F));
   }

   public boolean isHovering(int mouseX, int mouseY) {
      for(Widget widget : AmbienceGui.getClickGui().getComponents()) {
         if (widget.drag) {
            return false;
         }
      }

      int h = this.getHeight();
      return (float)mouseX >= this.getX() && (float)mouseX <= this.getX() + (float)this.getWidth() && (float)mouseY >= this.getY() && (float)mouseY < this.getY() + (float)h;
   }

   private float rowY(int row) {
      return this.y + (float)(row * 12);
   }

   private int rowAt(int mouseY) {
      return (int)(((float)mouseY - this.y) / 12.0F);
   }

   private boolean rowHover(int mouseX, int mouseY, int row) {
      return (float)mouseX >= this.x && (float)mouseX <= this.x + (float)this.width && (float)mouseY >= this.rowY(row) && (float)mouseY < this.rowY(row) + 12.0F;
   }
}
