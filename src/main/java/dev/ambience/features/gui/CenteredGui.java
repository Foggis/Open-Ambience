package dev.ambience.features.gui;

import dev.ambience.Ambience;
import dev.ambience.features.gui.items.Item;
import dev.ambience.features.gui.items.SearchBar;
import dev.ambience.features.gui.items.TextBox;
import dev.ambience.features.gui.items.buttons.BindButton;
import dev.ambience.features.gui.items.buttons.SettingButton;
import dev.ambience.features.modules.Module;
import dev.ambience.features.modules.client.ClickGuiModule;
import dev.ambience.features.settings.Bind;
import dev.ambience.features.settings.Setting;
import dev.ambience.util.render.GuiFade;
import dev.ambience.util.render.GuiIcons;
import dev.ambience.util.render.RenderUtil;
import dev.ambience.util.render.ScissorUtil;
import dev.ambience.util.render.font.Fonts;
import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import net.minecraft.class_1109;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_3417;
import net.minecraft.class_437;

public class CenteredGui extends class_437 {
   private static final int WIN_W = 318;
   private static final int WIN_H = 214;
   private static final int HEADER = 28;
   private static final int SIDE_PAD = 7;
   private static final int INSET = 8;
   private static final int CARD_GAP = 4;
   private static final int CARD_H = 30;
   private static final int SWITCHER_HEIGHT = 16;
   private static final int ARROW_HITBOX = 12;
   private static final int LOGO_W = 13;
   private static final int LOGO_H = 18;
   private static final int ICON = 11;
   private static final float BACK_SIZE = 12.0F;
   private static final float WIN_R = 14.0F;
   private static final float CARD_R = 8.0F;
   private static final float PILL_H = 15.0F;
   private static final float PILL_R = 7.5F;
   private static final String DEFAULT_PAGE = "General";
   private static final long FADE_DURATION = 260L;
   private static final long CLOSE_DURATION = 200L;
   private static final int WINDOW_BG = (new Color(16, 17, 20, 228)).getRGB();
   private static final int SIDEBAR_BG = (new Color(255, 255, 255, 22)).getRGB();
   private static final int CARD_BG = (new Color(255, 255, 255, 36)).getRGB();
   private static final int WELL_BG = (new Color(255, 255, 255, 24)).getRGB();
   private static CenteredGui INSTANCE;
   private final Map<Module, ModuleView> views = new IdentityHashMap();
   private final SearchBar searchBar = new SearchBar();
   private final EnumMap<Module.Category, CenteredStyle.Motion> catHover = new EnumMap(Module.Category.class);
   private final IdentityHashMap<Module, CenteredStyle.Motion> modHover = new IdentityHashMap();
   private final IdentityHashMap<Module, CenteredStyle.Motion> modOn = new IdentityHashMap();
   private final CenteredStyle.Motion settingsMotion = new CenteredStyle.Motion();
   private final CenteredStyle.Motion catIndicator = new CenteredStyle.Motion();
   private final CenteredStyle.Motion closeMotion = new CenteredStyle.Motion();
   private final CenteredStyle.Motion backMotion = new CenteredStyle.Motion();
   private final CenteredStyle.Motion moduleScrollMotion = new CenteredStyle.Motion();
   private final CenteredStyle.Motion settingScrollMotion = new CenteredStyle.Motion();
   private Module.Category selectedCategory;
   private Module settingsModule;
   private Module settingsDraw;
   private final ConfigsPanel configsPanel;
   private float settingsAnim;
   private float moduleScroll;
   private float settingScroll;
   private float moduleScrollDrawn;
   private float settingScrollDrawn;
   private float catIndicatorY;
   private float closeHover;
   private float backHover;
   private float alpha;
   private long openTime;
   private boolean closing;
   private long closeTime;
   private long lastFrameNs;
   private float animScale;
   private float animSlide;
   private int winX;
   private int winY;
   private int winW;
   private int winH;
   private int sideW;
   private boolean hoveringPagePrev;
   private boolean hoveringPageNext;
   private boolean hoveringClose;
   private boolean hoveringBack;

   public CenteredGui() {
      super(class_2561.method_43470("Ambience"));
      this.selectedCategory = Module.Category.COMBAT;
      this.configsPanel = new ConfigsPanel();
      this.catIndicatorY = -1.0F;
      this.animScale = 1.0F;
   }

   public static boolean modern() {
      return class_310.method_1551() != null && class_310.method_1551().field_1755 instanceof CenteredGui;
   }

   public static CenteredGui get() {
      if (INSTANCE == null) {
         INSTANCE = new CenteredGui();
      }

      return INSTANCE;
   }

   public void method_25426() {
      super.method_25426();
      this.openTime = System.currentTimeMillis();
      this.closing = false;
      this.alpha = 0.0F;
      this.lastFrameNs = 0L;
      this.catIndicatorY = -1.0F;
      this.catIndicator.value = -1.0F;
      this.catIndicator.vel = 0.0F;
      this.configsPanel.onOpen();
      this.settingsAnim = this.settingsModule == null ? 0.0F : 1.0F;
      this.settingsMotion.value = this.settingsAnim;
      this.settingsMotion.vel = 0.0F;
      this.closeMotion.value = 0.0F;
      this.closeMotion.vel = 0.0F;
      this.backMotion.value = 0.0F;
      this.backMotion.vel = 0.0F;
      this.moduleScrollMotion.value = this.moduleScrollDrawn;
      this.moduleScrollMotion.vel = 0.0F;
      this.settingScrollMotion.value = this.settingScrollDrawn;
      this.settingScrollMotion.vel = 0.0F;
      this.settingsDraw = this.settingsModule;
   }

   public void method_25419() {
      if (!this.closing) {
         this.closing = true;
         this.closeTime = System.currentTimeMillis();
         this.searchBar.clear();
      }

   }

   public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
      long now = System.currentTimeMillis();
      if (!this.closing) {
         this.alpha = Math.min(1.0F, (float)(now - this.openTime) / 260.0F);
      } else {
         this.alpha = Math.max(0.0F, 1.0F - (float)(now - this.closeTime) / 200.0F);
         if (this.alpha <= 0.0F) {
            this.field_22787.method_1507((class_437)null);
            return;
         }
      }

      float fade = easeOutQuart(this.alpha);
      float pop = this.closing ? easeInBack(1.0F - this.alpha) : easeOutBack(this.alpha);
      boolean smooth = this.smooth();
      this.animScale = smooth ? (this.closing ? 1.0F - 0.05F * pop : 0.9F + 0.1F * pop) : 1.0F;
      this.animSlide = smooth ? (this.closing ? pop * 7.0F : (1.0F - pop) * 12.0F) : 0.0F;
      GuiFade.alpha = fade;
      Item.context = context;
      this.layoutWindow(context.method_51421(), context.method_51443());
      this.ensureSelection();
      float dt = this.tickDelta();
      CenteredStyle.frameDt = dt;
      int mx = this.localX((double)mouseX);
      int my = this.localY((double)mouseY);
      Color paint = GuiTheme.moduleColor(this.selectedCategory, 0.0F);
      float cx = (float)this.winX + (float)this.winW / 2.0F;
      float cy = (float)this.winY + (float)this.winH / 2.0F;
      context.method_51448().pushMatrix();
      context.method_51448().translate(cx, cy + this.animSlide);
      context.method_51448().scale(this.animScale, this.animScale);
      context.method_51448().translate(-cx, -cy);
      CenteredStyle.plasticSheet(context, (float)this.winX, (float)this.winY, (float)(this.winX + this.winW), (float)(this.winY + this.winH), 14.0F, WINDOW_BG);
      RenderUtil.roundRect(context, (float)this.winX, (float)(this.winY + 1), (float)(this.winX + this.sideW), (float)(this.winY + this.winH - 1), 13.0F, 0.0F, 0.0F, 13.0F, SIDEBAR_BG);
      int fireX = this.winX + 7 + 5;
      int fireY = this.winY + 8;
      GuiIcons.draw(context, "fire", (float)fireX, (float)fireY, 13.0F, 18.0F, -1);
      this.drawString(context, "Ambience", (float)(fireX + 13) + 4.0F, (float)fireY + (float)(18 - Fonts.lineHeight()) / 2.0F, -1);
      this.drawString(context, "(Free Edition!)", (float)(fireX + 13) + 4.0F, (float)fireY + (float)(18 - Fonts.lineHeight()) / 2.0F + (float)Fonts.lineHeight() + 2.0F, -1);
      this.tickSettingsAnim(dt);
      this.drawHeader(context, mx, my, paint, dt);
      this.drawCategories(context, mx, my, dt, paint);
      this.drawSidebarUser(context);
      this.drawContent(context, mx, my, paint, dt);
      context.method_51448().popMatrix();
      GuiFade.alpha = 1.0F;
   }

   private void layoutWindow(int guiW, int guiH) {
      this.sideW = this.sidebarWidth();
      this.winW = Math.min(318, Math.max(this.sideW + 210, guiW - 48));
      this.winH = Math.min(214, Math.max(176, guiH - 48));
      this.winX = (guiW - this.winW) / 2;
      this.winY = (guiH - this.winH) / 2;
   }

   private int contentLeft() {
      return this.winX + this.sideW;
   }

   private int contentTop() {
      return this.winY + 28;
   }

   private int contentRight() {
      return this.winX + this.winW - 8;
   }

   private int contentBottom() {
      return this.winY + this.winH - 8;
   }

   private int settingsLeft() {
      return this.contentLeft() + 8;
   }

   private int settingsRight() {
      return this.winX + this.winW - 6;
   }

   private int settingsBottom() {
      return this.winY + this.winH - 10;
   }

   private int settingsClipLeft() {
      return Math.max(this.contentLeft(), this.settingsLeft() - 4);
   }

   private int settingsClipRight() {
      return this.winX + this.winW;
   }

   private float cardsLeft() {
      return (float)this.contentLeft() + 8.0F;
   }

   private float cardsRight() {
      return (float)this.contentRight() - 6.0F;
   }

   private float cardWidth() {
      return (this.cardsRight() - this.cardsLeft() - 4.0F) / 2.0F;
   }

   private float cardX(int col) {
      return this.cardsLeft() + (float)col * (this.cardWidth() + 4.0F);
   }

   private float cardY(int row) {
      return (float)this.contentTop() + 5.0F + (float)(row * 34) - this.moduleScrollDrawn;
   }

   private float catOrigin() {
      return (float)this.winY + 8.0F + 18.0F + 4.0F;
   }

   private float catStep() {
      return 18.0F;
   }

   private float configsPillY() {
      float userTop = (float)(this.winY + this.winH - 10 - 11);
      return userTop - 6.0F - this.catStep();
   }

   private float categoryPillWidth() {
      float max = 0.0F;

      for(Module.Category cat : this.visibleCategories()) {
         max = Math.max(max, (float)Fonts.width(cat.getName()));
      }

      max = Math.max(max, (float)Fonts.width(Module.Category.CONFIGS.getName()));
      return 20.0F + max + 7.0F;
   }

   private int sidebarWidth() {
      int brand = 29 + Math.max(Fonts.width("Ambience"), Fonts.width("(Free Edition!)")) + 7;
      int nav = Math.round(this.categoryPillWidth()) + 14;
      int user = 27 + Fonts.width(this.sessionName()) + 7;
      return Math.max(68, Math.max(brand, Math.max(nav, user)));
   }

   private void tickSettingsAnim(float dt) {
      this.settingsMotion.value = this.settingsAnim;
      if (this.settingsModule != null) {
         this.settingsDraw = this.settingsModule;
         this.settingsAnim = this.settingsMotion.to(1.0F, 140.0F, 16.0F);
      } else {
         this.settingsAnim = this.settingsMotion.to(0.0F, 150.0F, 17.0F);
         if (this.settingsAnim < 0.02F && Math.abs(this.settingsMotion.vel) < 0.08F) {
            this.settingsDraw = null;
            this.settingsAnim = 0.0F;
            this.settingsMotion.value = 0.0F;
            this.settingsMotion.vel = 0.0F;
         }
      }

   }

   private void drawContent(class_332 context, int mx, int my, Color paint, float dt) {
      float vis = this.settingsAnim;
      float savedFade = GuiFade.alpha;
      int clipL = this.contentLeft();
      int clipT = this.winY + 28;
      int clipR = this.winX + this.winW;
      int clipB = this.winY + this.winH - 6;
      float ox = (float)(clipL + clipR) * 0.5F;
      float oy = (float)(clipT + clipB) * 0.5F;
      ScissorUtil.enable(context, clipL, clipT, clipR, clipB);
      float fadeVis = clamp01(vis);
      if (1.0F - fadeVis > 0.02F) {
         GuiFade.alpha = savedFade * (1.0F - fadeVis);
         context.method_51448().pushMatrix();
         context.method_51448().translate(ox - 22.0F * vis, oy);
         float scale = 1.0F - 0.04F * vis;
         context.method_51448().scale(scale, scale);
         context.method_51448().translate(-ox, -oy);
         if (this.selectedCategory == Module.Category.CONFIGS) {
            this.configsPanel.render(context, this.contentLeft(), this.contentTop(), this.contentRight(), this.contentBottom(), mx, my, paint);
         } else {
            this.drawCards(context, mx, my, paint, dt);
         }

         context.method_51448().popMatrix();
      }

      if (fadeVis > 0.02F && this.settingsDraw != null) {
         GuiFade.alpha = savedFade * fadeVis;
         context.method_51448().pushMatrix();
         float incoming = 1.0F - vis;
         context.method_51448().translate(ox + 24.0F * incoming, oy);
         float scale = 0.95F + 0.05F * vis;
         context.method_51448().scale(scale, scale);
         context.method_51448().translate(-ox, -oy);
         this.drawSettings(context, mx, my, paint, dt, this.settingsDraw);
         context.method_51448().popMatrix();
      }

      ScissorUtil.disable(context);
      GuiFade.alpha = savedFade;
   }

   private void drawHeader(class_332 context, int mouseX, int mouseY, Color paint, float dt) {
      String gridTitle = SearchBar.hasQuery() ? "Search" : this.selectedCategory.getName();
      float gridTitleX = (float)this.contentLeft() + 12.0F;
      float backX = (float)this.contentLeft() + 6.0F;
      float titleH = (float)Fonts.headerHeight();
      float titleY = (float)this.winY + (28.0F - titleH) / 2.0F;
      float savedFade = GuiFade.alpha;
      float fadeVis = clamp01(this.settingsAnim);
      if (fadeVis < 0.98F) {
         GuiFade.alpha = savedFade * (1.0F - fadeVis);
         this.drawTitle(context, gridTitle, gridTitleX - 10.0F * this.settingsAnim, titleY, -1);
      }

      if (fadeVis > 0.02F && this.settingsDraw != null) {
         GuiFade.alpha = savedFade * fadeVis;
         float backY = (float)this.winY + 8.0F;
         this.hoveringBack = this.settingsModule != null && (float)mouseX >= backX && (float)mouseX < backX + 12.0F + 6.0F && mouseY >= this.winY && mouseY < this.winY + 28;
         this.backHover = this.backMotion.to(this.hoveringBack ? 1.0F : 0.0F, 220.0F, 18.0F);
         GuiIcons.draw(context, "chevron_left", backX, backY, 12.0F, mix(-3880238, -1, clamp01(this.backHover)));
         this.drawTitle(context, this.settingsDraw.getName(), backX + 12.0F + 10.0F + 12.0F * (1.0F - this.settingsAnim), titleY, -1);
      } else {
         this.hoveringBack = false;
         this.backHover = 0.0F;
         this.backMotion.value = 0.0F;
         this.backMotion.vel = 0.0F;
      }

      GuiFade.alpha = savedFade;
      int close = 12;
      int closeX = this.winX + this.winW - close - 6;
      int closeY = this.winY + (28 - close) / 2;
      this.hoveringClose = mouseX >= closeX && mouseX < closeX + close && mouseY >= closeY && mouseY < closeY + close;
      this.closeHover = this.closeMotion.to(this.hoveringClose ? 1.0F : 0.0F, 220.0F, 18.0F);
      RenderUtil.roundRect(context, (float)closeX, (float)closeY, (float)(closeX + close), (float)(closeY + close), (float)close / 2.0F, GuiTheme.withAlpha(paint, (int)(16.0F + 70.0F * clamp01(this.closeHover))).getRGB());
      int xColor = mix(-3880238, -1, clamp01(this.closeHover));
      float cx = (float)closeX + (float)close * 0.5F;
      float cy = (float)closeY + (float)close * 0.5F;
      float arm = 2.4F;
      RenderUtil.stroke(context, cx - arm, cy - arm, cx + arm, cy + arm, xColor, 1.25F);
      RenderUtil.stroke(context, cx + arm, cy - arm, cx - arm, cy + arm, xColor, 1.25F);
      int searchW = 78;
      int searchH = this.searchBar.getHeight();
      int searchX = closeX - searchW - 8;
      int searchY = this.winY + (28 - searchH) / 2;
      this.searchBar.setLocation((float)searchX, (float)searchY);
      this.searchBar.setWidth(searchW);
      Widget.currentAccent = paint;
      this.searchBar.drawScreen(context, mouseX, mouseY, 0.0F);
      Widget.currentAccent = null;
   }

   private void drawCategories(class_332 context, int mouseX, int mouseY, float dt, Color paint) {
      List<Module.Category> cats = this.navCategories();
      float origin = this.catOrigin();
      float step = this.catStep();
      float selectedY = origin;
      float y = origin;

      for(Module.Category cat : cats) {
         if (cat == this.selectedCategory) {
            selectedY = y;
         }

         y += step;
      }

      if (this.selectedCategory == Module.Category.CONFIGS) {
         selectedY = this.configsPillY();
      }

      if (this.catIndicatorY < 0.0F) {
         this.catIndicatorY = selectedY;
         this.catIndicator.value = selectedY;
         this.catIndicator.vel = 0.0F;
      } else {
         this.catIndicator.value = this.catIndicatorY;
         this.catIndicatorY = this.catIndicator.to(selectedY, 160.0F, 16.0F);
      }

      float pillX = (float)(this.winX + 7);
      float pillW = this.categoryPillWidth();
      float selectedTop = this.catIndicatorY + (step - 15.0F) / 2.0F;
      int selectedPy = Math.round(selectedTop);
      RenderUtil.roundRect(context, pillX, (float)selectedPy, pillX + pillW, (float)selectedPy + 15.0F, 7.5F, GuiTheme.withAlpha(paint, 220).getRGB());
      y = origin;

      for(Module.Category cat : cats) {
         this.drawCategoryPill(context, cat, pillX, y, step, pillW, mouseX, mouseY, paint);
         y += step;
      }

      this.drawCategoryPill(context, Module.Category.CONFIGS, pillX, this.configsPillY(), step, pillW, mouseX, mouseY, paint);
   }

   private void drawCategoryPill(class_332 context, Module.Category cat, float pillX, float y, float step, float pillW, int mouseX, int mouseY, Color paint) {
      boolean hover = mouseX >= this.winX && mouseX < this.contentLeft() && (float)mouseY >= y && (float)mouseY < y + step;
      float hoverAmt = this.motion(this.catHover, cat).to(hover && cat != this.selectedCategory ? 1.0F : 0.0F, 220.0F, 18.0F);
      float pillY = y + (step - 15.0F) / 2.0F;
      if (cat != this.selectedCategory && hoverAmt > 0.02F) {
         RenderUtil.roundRect(context, pillX, pillY, pillX + pillW, pillY + 15.0F, 7.5F, mix(0, WELL_BG, clamp01(hoverAmt)));
      }

      boolean selected = cat == this.selectedCategory;
      int iconColor = selected ? -1 : mix(-7630696, -1512723, clamp01(hoverAmt));
      int textColor = selected ? -1 : mix(-6643544, -1512723, clamp01(hoverAmt));
      int top = Math.round(pillY);
      int h = Math.round(15.0F);
      int mid = top + h / 2;
      int iconY = top + (h - 11) / 2;
      float textH = Fonts.visualHeight();
      float textTop = (float)mid - textH * 0.5F;
      IconDraw.category(context, cat, (float)(Math.round(pillX) + 5), (float)iconY, 11, iconColor);
      this.drawString(context, cat.getName(), (float)(Math.round(pillX) + 18), Fonts.alignY(textTop), textColor);
   }

   private void drawCards(class_332 context, int mouseX, int mouseY, Color paint, float dt) {
      List<Module> modules = this.listedModules();
      float cardW = this.cardWidth();
      int rows = (modules.size() + 1) / 2;
      float contentH = (float)(rows * 34);
      float viewH = (float)(this.contentBottom() - this.contentTop());
      float maxScroll = Math.max(0.0F, contentH - viewH + 8.0F);
      this.moduleScroll = Math.max(0.0F, Math.min(this.moduleScroll, maxScroll));
      this.moduleScrollMotion.value = this.moduleScrollDrawn;
      this.moduleScrollDrawn = this.moduleScrollMotion.to(this.moduleScroll, 200.0F, 20.0F);
      ScissorUtil.enable(context, this.contentLeft(), this.contentTop(), this.contentRight(), this.contentBottom());

      for(int i = 0; i < modules.size(); ++i) {
         Module module = (Module)modules.get(i);
         int col = i % 2;
         int row = i / 2;
         float x = this.cardX(col);
         float y = this.cardY(row);
         boolean hover = (float)mouseX >= x && (float)mouseX < x + cardW && (float)mouseY >= y && (float)mouseY < y + 30.0F && mouseY >= this.contentTop() && mouseY < this.contentBottom();
         float hoverAmt = this.motion(this.modHover, module).to(hover ? 1.0F : 0.0F, 220.0F, 18.0F);
         float onAmt = this.motion(this.modOn, module).to(module.isEnabled() ? 1.0F : 0.0F, 200.0F, 16.0F);
         float pop = 1.0F + 0.025F * hoverAmt;
         context.method_51448().pushMatrix();
         context.method_51448().translate(x + cardW * 0.5F, y + 15.0F);
         context.method_51448().scale(pop, pop);
         context.method_51448().translate(-(x + cardW * 0.5F), -(y + 15.0F));
         int fill = mix(CARD_BG, (new Color(255, 255, 255, 16)).getRGB(), clamp01(hoverAmt));
         CenteredStyle.plasticTile(context, x, y, x + cardW, y + 30.0F, 8.0F, fill);
         int accent = -16777216 | paint.getRGB() & 16777215;
         int title = mix(-2828066, accent, clamp01(onAmt));
         int titleH = Fonts.titleHeight();
         float textY = y + 5.0F;
         Fonts.drawModuleTitle(context, this.truncateTitle(module.getName(), (int)cardW - 18), x + 10.0F, textY, GuiFade.apply(title));
         String desc = module.getDescription();
         if (desc != null && !desc.isEmpty()) {
            this.drawString(context, this.truncate(desc, (int)cardW - 18), x + 10.0F, textY + (float)titleH + 2.0F, -3880238);
         }

         context.method_51448().popMatrix();
      }

      ScissorUtil.disable(context);
   }

   private void drawSettings(class_332 context, int mouseX, int mouseY, Color accent, float dt, Module module) {
      ModuleView view = this.view(module);

      for(SettingButton<?> item : view.items) {
         item.update();
      }

      List<String> pages = view.visiblePages();
      String page = view.resolvePage(pages);
      List<SettingButton<?>> visible = view.itemsOn(page);
      int left = this.settingsLeft();
      int right = this.settingsRight();
      int top = this.contentTop();
      int bottom = this.settingsBottom();
      float yCursor = (float)top + 8.0F;
      if (pages.size() > 1) {
         this.drawPageSwitcher(context, page, left, yCursor, right, mouseX, mouseY);
         yCursor += 22.0F;
      } else {
         this.hoveringPagePrev = false;
         this.hoveringPageNext = false;
      }

      float contentH = 0.0F;

      for(SettingButton<?> item : visible) {
         contentH += (float)(item.getHeight() + 5);
      }

      float viewH = (float)bottom - yCursor;
      float maxScroll = Math.max(0.0F, contentH - viewH + 18.0F);
      this.settingScroll = Math.max(0.0F, Math.min(this.settingScroll, maxScroll));
      this.settingScrollMotion.value = this.settingScrollDrawn;
      this.settingScrollDrawn = this.settingScrollMotion.to(this.settingScroll, 200.0F, 20.0F);
      Widget.currentCategory = module.getCategory();
      int clipL = this.settingsClipLeft();
      int clipR = this.settingsClipRight();
      ScissorUtil.enable(context, clipL, (int)yCursor, clipR, bottom);
      float y = yCursor - (float)Math.round(this.settingScrollDrawn);
      int settingWidth = Math.max(40, right - left);

      for(SettingButton<?> item : visible) {
         item.setLocation((float)left, (float)Math.round(y));
         item.setWidth(settingWidth);
         Widget.currentAccent = GuiTheme.moduleColor(module.getCategory(), y);
         item.drawScreen(context, mouseX, mouseY, 0.0F);
         y += (float)(item.getHeight() + 5);
      }

      Widget.currentAccent = null;
      Widget.currentCategory = null;
      ScissorUtil.disable(context);
      if (visible.isEmpty()) {
         this.drawString(context, "No settings", (float)left, yCursor + 6.0F, -3880238);
      }

   }

   private void drawPageSwitcher(class_332 context, String page, int left, float top, int right, int mouseX, int mouseY) {
      float bottom = top + 16.0F;
      CenteredStyle.plasticTile(context, (float)left, top, (float)right, bottom, 8.0F, CenteredStyle.ROW_BG);
      this.hoveringPagePrev = mouseX >= left && mouseX < left + 12 && (float)mouseY >= top && (float)mouseY < bottom;
      this.hoveringPageNext = mouseX >= right - 12 && mouseX < right && (float)mouseY >= top && (float)mouseY < bottom;
      float textY = top + (float)(16 - Fonts.lineHeight()) / 2.0F;
      this.drawString(context, "<", (float)left + 8.0F, textY, this.hoveringPagePrev ? -1 : -3880238);
      this.drawString(context, ">", (float)right - 12.0F, textY, this.hoveringPageNext ? -1 : -3880238);
      int tw = Fonts.width(page);
      this.drawString(context, page, (float)left + (float)(right - left - tw) / 2.0F, textY, -1);
   }

   public boolean method_25402(class_11909 click, boolean doubled) {
      if (this.closing) {
         return true;
      } else {
         this.layoutWindow(this.field_22789, this.field_22790);
         int mx = this.localX(click.comp_4798());
         int my = this.localY(click.comp_4799());
         int button = click.method_74245();
         this.searchBar.mouseClicked(mx, my, button);
         if (button == 0 && this.hoveringClose) {
            this.method_25419();
            return true;
         } else if (button == 0 && this.hoveringBack) {
            this.settingsModule = null;
            this.click();
            return true;
         } else if (this.handleCategoryClick(mx, my, button)) {
            return true;
         } else if (this.settingsModule == null) {
            if (this.selectedCategory == Module.Category.CONFIGS && this.configsPanel.mouseClicked(mx, my, button, this.contentLeft(), this.contentTop(), this.contentRight(), this.contentBottom())) {
               this.click();
               return true;
            } else {
               return this.handleCardClick(mx, my, button) ? true : super.method_25402(click, doubled);
            }
         } else {
            return this.handleSettingsClick(mx, my, button) || super.method_25402(click, doubled);
         }
      }
   }

   public boolean method_25406(class_11909 click) {
      if (this.closing) {
         return true;
      } else {
         this.layoutWindow(this.field_22789, this.field_22790);
         int mx = this.localX(click.comp_4798());
         int my = this.localY(click.comp_4799());
         if (this.settingsModule != null) {
            ModuleView view = this.view(this.settingsModule);

            for(SettingButton<?> item : view.itemsOn(view.resolvePage(view.visiblePages()))) {
               item.mouseReleased(mx, my, click.method_74245());
            }
         }

         return super.method_25406(click);
      }
   }

   public boolean method_25401(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (this.closing) {
         return true;
      } else {
         this.layoutWindow(this.field_22789, this.field_22790);
         int mx = this.localX(mouseX);
         int my = this.localY(mouseY);
         if (this.settingsModule != null) {
            if (mx >= this.settingsClipLeft() && mx < this.settingsClipRight() && my >= this.contentTop() && my < this.settingsBottom()) {
               this.settingScroll = Math.max(0.0F, this.settingScroll - (float)verticalAmount * 16.0F);
               return true;
            } else {
               return super.method_25401(mouseX, mouseY, horizontalAmount, verticalAmount);
            }
         } else if (mx >= this.contentLeft() && mx < this.contentRight() && my >= this.contentTop() && my < this.contentBottom()) {
            if (this.selectedCategory == Module.Category.CONFIGS) {
               return this.configsPanel.mouseScrolled(verticalAmount, this.contentLeft(), this.contentTop(), this.contentRight(), this.contentBottom());
            } else {
               this.moduleScroll = Math.max(0.0F, this.moduleScroll - (float)verticalAmount * 16.0F);
               return true;
            }
         } else {
            return super.method_25401(mouseX, mouseY, horizontalAmount, verticalAmount);
         }
      }
   }

   public boolean method_25404(class_11908 input) {
      if (this.closing) {
         return true;
      } else if (TextBox.hasActiveFocus()) {
         TextBox.routeKeyPressed(input.method_74228());
         return true;
      } else if (this.routeBindKey(input.method_74228())) {
         return true;
      } else if (input.method_74228() == ((Bind)ClickGuiModule.getInstance().bind.getValue()).getKey()) {
         if (System.currentTimeMillis() - this.openTime > 50L) {
            this.method_25419();
         }

         return true;
      } else {
         if (this.settingsModule != null) {
            ModuleView view = this.view(this.settingsModule);

            for(SettingButton<?> item : view.itemsOn(view.resolvePage(view.visiblePages()))) {
               item.onKeyPressed(input.method_74228());
            }
         }

         return super.method_25404(input);
      }
   }

   public boolean method_25400(class_11905 input) {
      if (this.closing) {
         return true;
      } else if (TextBox.hasActiveFocus()) {
         TextBox.routeKeyTyped(input.method_74226());
         return true;
      } else {
         if (this.settingsModule != null) {
            ModuleView view = this.view(this.settingsModule);

            for(SettingButton<?> item : view.itemsOn(view.resolvePage(view.visiblePages()))) {
               item.onKeyTyped(input.method_74226(), input.comp_4794());
            }
         }

         return super.method_25400(input);
      }
   }

   public boolean method_25421() {
      return false;
   }

   public void method_25420(class_332 context, int mouseX, int mouseY, float delta) {
      context.method_25294(0, 0, context.method_51421(), context.method_51443(), GuiFade.apply((new Color(4, 5, 7, 72)).getRGB()));
   }

   private boolean handleCategoryClick(int mx, int my, int button) {
      if (button != 0) {
         return false;
      } else {
         float y = this.catOrigin();
         float step = this.catStep();

         for(Module.Category cat : this.navCategories()) {
            if (mx >= this.winX && mx < this.contentLeft() && (float)my >= y && (float)my < y + step) {
               this.selectCategory(cat);
               return true;
            }

            y += step;
         }

         float configsY = this.configsPillY();
         if (mx >= this.winX && mx < this.contentLeft() && (float)my >= configsY && (float)my < configsY + step) {
            this.selectCategory(Module.Category.CONFIGS);
            return true;
         } else {
            return false;
         }
      }
   }

   private void selectCategory(Module.Category cat) {
      this.selectedCategory = cat;
      this.settingsModule = null;
      this.moduleScroll = 0.0F;
      if (cat == Module.Category.CONFIGS) {
         this.configsPanel.onCategorySelected();
      }

      this.click();
   }

   private boolean handleCardClick(int mx, int my, int button) {
      if (this.settingsAnim > 0.12F) {
         return false;
      } else {
         List<Module> modules = this.listedModules();
         float cardW = this.cardWidth();

         for(int i = 0; i < modules.size(); ++i) {
            int col = i % 2;
            int row = i / 2;
            float x = this.cardX(col);
            float y = this.cardY(row);
            if (!((float)mx < x) && !((float)mx >= x + cardW) && !((float)my < y) && !((float)my >= y + 30.0F)) {
               if (my >= this.contentTop() && my < this.contentBottom()) {
                  Module module = (Module)modules.get(i);
                  if (button == 0) {
                     module.toggle();
                     this.click();
                     return true;
                  }

                  if (button == 1) {
                     Module live = Ambience.moduleManager.ensureByName(module.getName());
                     if (live != null) {
                        module = live;
                     }

                     this.settingsModule = module;
                     this.selectedCategory = module.getCategory();
                     this.settingScroll = 0.0F;
                     this.settingScrollDrawn = 0.0F;
                     this.settingScrollMotion.value = 0.0F;
                     this.settingScrollMotion.vel = 0.0F;
                     this.click();
                     return true;
                  }

                  return true;
               }

               return false;
            }
         }

         return false;
      }
   }

   private boolean handleSettingsClick(int mx, int my, int button) {
      if (this.settingsModule != null && !(this.settingsAnim < 0.88F)) {
         int left = this.settingsLeft();
         int right = this.settingsRight();
         int top = this.contentTop();
         int bottom = this.settingsBottom();
         if (mx >= left && mx < right && my >= top && my < bottom) {
            ModuleView view = this.view(this.settingsModule);
            List<String> pages = view.visiblePages();
            String page = view.resolvePage(pages);
            if (button == 0 && pages.size() > 1) {
               int idx = pages.indexOf(page);
               if (this.hoveringPagePrev) {
                  view.page = (String)pages.get((idx - 1 + pages.size()) % pages.size());
                  this.click();
                  return true;
               }

               if (this.hoveringPageNext) {
                  view.page = (String)pages.get((idx + 1) % pages.size());
                  this.click();
                  return true;
               }
            }

            for(SettingButton<?> item : view.itemsOn(page)) {
               item.mouseClicked(mx, my, button);
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean routeBindKey(int key) {
      if (this.settingsModule == null) {
         return false;
      } else {
         for(SettingButton<?> item : this.view(this.settingsModule).items) {
            if (item instanceof BindButton) {
               BindButton bind = (BindButton)item;
               if (bind.isListening) {
                  bind.onKeyPressed(key);
                  return true;
               }
            }
         }

         return false;
      }
   }

   private void ensureSelection() {
      List<Module.Category> cats = this.visibleCategories();
      if (!cats.isEmpty()) {
         if (!cats.contains(this.selectedCategory) && this.selectedCategory != Module.Category.CONFIGS) {
            this.selectedCategory = (Module.Category)cats.get(0);
         }

         if (this.settingsModule != null && (this.settingsModule.hidden || !cats.contains(this.settingsModule.getCategory()))) {
            this.settingsModule = null;
         }

      }
   }

   private List<Module.Category> navCategories() {
      List<Module.Category> out = new ArrayList();

      for(Module.Category cat : this.visibleCategories()) {
         if (cat != Module.Category.CONFIGS) {
            out.add(cat);
         }
      }

      return out;
   }

   private List<Module.Category> visibleCategories() {
      List<Module.Category> out = new ArrayList();

      for(Module.Category cat : Ambience.moduleManager.getCategories()) {
         if (cat != Module.Category.HUD && cat != Module.Category.WORLD && (cat != Module.Category.FUNNY || Ambience.commandManager.isFunnyVisible())) {
            out.add(cat);
         }
      }

      return out;
   }

   private List<Module> listedModules() {
      boolean searching = SearchBar.hasQuery();
      return Ambience.moduleManager.guiModules(this.selectedCategory, searching ? SearchBar.QUERY : null).stream().filter((m) -> m.getCategory() != Module.Category.HUD && m.getCategory() != Module.Category.WORLD).filter((m) -> m.getCategory() != Module.Category.FUNNY || Ambience.commandManager.isFunnyVisible()).toList();
   }

   private ModuleView view(Module module) {
      return (ModuleView)this.views.computeIfAbsent(module, ModuleView::new);
   }

   private void click() {
      this.field_22787.method_1483().method_4873(class_1109.method_47978(class_3417.field_15015, 1.0F));
   }

   private void drawSidebarUser(class_332 context) {
      String name = this.sessionName();
      if (!name.isEmpty()) {
         int icon = 11;
         int x = this.winX + 7 + 5;
         int y = this.winY + this.winH - 10 - icon;
         GuiIcons.draw(context, "user", (float)x, (float)y, (float)icon, -7630696);
         this.drawString(context, name, (float)(x + icon + 4), Fonts.alignY((float)y + ((float)icon - Fonts.visualHeight()) * 0.5F), -6643544);
      }
   }

   private String sessionName() {
      if (this.field_22787.method_1548() != null && this.field_22787.method_1548().method_1676() != null) {
         return this.field_22787.method_1548().method_1676();
      } else {
         return this.field_22787.field_1724 != null ? this.field_22787.field_1724.method_7334().name() : "";
      }
   }

   private void drawString(class_332 context, String text, float x, float y, int color) {
      Fonts.drawString(context, text, x, y, GuiFade.apply(color));
   }

   private void drawTitle(class_332 context, String text, float x, float y, int color) {
      Fonts.drawHeader(context, text, x, y, GuiFade.apply(color));
   }

   private int localX(double mouseX) {
      float cx = (float)this.winX + (float)this.winW / 2.0F;
      return (int)Math.round((mouseX - (double)cx) / (double)this.animScale + (double)cx);
   }

   private int localY(double mouseY) {
      float cy = (float)this.winY + (float)this.winH / 2.0F;
      return (int)Math.round((mouseY - (double)cy - (double)this.animSlide) / (double)this.animScale + (double)cy);
   }

   private float tickDelta() {
      long now = System.nanoTime();
      float dt = this.lastFrameNs == 0L ? 0.016F : (float)(now - this.lastFrameNs) / 1.0E9F;
      this.lastFrameNs = now;
      return Math.max(0.0F, Math.min(0.05F, dt));
   }

   private CenteredStyle.Motion motion(EnumMap<Module.Category, CenteredStyle.Motion> map, Module.Category key) {
      return (CenteredStyle.Motion)map.computeIfAbsent(key, (ignored) -> new CenteredStyle.Motion());
   }

   private CenteredStyle.Motion motion(IdentityHashMap<Module, CenteredStyle.Motion> map, Module key) {
      return (CenteredStyle.Motion)map.computeIfAbsent(key, (ignored) -> new CenteredStyle.Motion());
   }

   private boolean smooth() {
      ClickGuiModule gui = ClickGuiModule.getInstance();
      return gui == null || (Boolean)gui.smooth.getValue();
   }

   private static float easeOutQuart(float t) {
      t = clamp01(t);
      float u = 1.0F - t;
      return 1.0F - u * u * u * u;
   }

   private static float easeOutBack(float t) {
      t = clamp01(t);
      float s = 1.55F;
      float u = t - 1.0F;
      return u * u * ((s + 1.0F) * u + s) + 1.0F;
   }

   private static float easeInBack(float t) {
      t = clamp01(t);
      float s = 1.4F;
      return t * t * ((s + 1.0F) * t - s);
   }

   private static float clamp01(float v) {
      return Math.max(0.0F, Math.min(1.0F, v));
   }

   private static int mix(int a, int b, float t) {
      t = Math.max(0.0F, Math.min(1.0F, t));
      int ar = a >> 16 & 255;
      int ag = a >> 8 & 255;
      int ab = a & 255;
      int aa = a >> 24 & 255;
      int br = b >> 16 & 255;
      int bg = b >> 8 & 255;
      int bb = b & 255;
      int ba = b >> 24 & 255;
      return (int)((float)aa + (float)(ba - aa) * t) << 24 | (int)((float)ar + (float)(br - ar) * t) << 16 | (int)((float)ag + (float)(bg - ag) * t) << 8 | (int)((float)ab + (float)(bb - ab) * t);
   }

   private String truncate(String text, int maxWidth) {
      return this.truncate(text, maxWidth, false);
   }

   private String truncateTitle(String text, int maxWidth) {
      return this.truncate(text, maxWidth, true);
   }

   private String truncate(String text, int maxWidth, boolean moduleTitle) {
      if (text == null) {
         return "";
      } else {
         int width = moduleTitle ? Fonts.titleWidth(text) : Fonts.width(text);
         if (width <= maxWidth) {
            return text;
         } else {
            String ellipsis = "...";
            int ellipsisWidth = moduleTitle ? Fonts.titleWidth(ellipsis) : Fonts.width(ellipsis);
            if (maxWidth <= ellipsisWidth) {
               return ellipsis;
            } else {
               int target = maxWidth - ellipsisWidth;
               StringBuilder sb = new StringBuilder();

               for(char c : text.toCharArray()) {
                  String var10000 = sb.toString();
                  String next = var10000 + c;
                  int nextW = moduleTitle ? Fonts.titleWidth(next) : Fonts.width(next);
                  if (nextW > target) {
                     break;
                  }

                  sb.append(c);
               }

               String var15 = String.valueOf(sb);
               return var15 + ellipsis;
            }
         }
      }
   }

   private static final class ModuleView {
      private final List<SettingButton<?>> items = new ArrayList();
      private String page = "General";

      private ModuleView(Module module) {
         List<Setting<?>> tail = new ArrayList();

         for(Setting<?> setting : module.getSettings()) {
            if (setting != module.enabled && setting != module.displayName) {
               if (setting != module.bind && setting != module.bindMode) {
                  SettingButton<?> btn = SettingButton.create(setting);
                  if (btn != null) {
                     this.items.add(btn);
                  }
               } else {
                  tail.add(setting);
               }
            }
         }

         for(Setting<?> setting : tail) {
            SettingButton<?> btn = SettingButton.create(setting);
            if (btn != null) {
               this.items.add(btn);
            }
         }

      }

      private List<String> visiblePages() {
         LinkedHashSet<String> seen = new LinkedHashSet();

         for(SettingButton<?> item : this.items) {
            if (!item.isHidden()) {
               seen.add(item.getPage());
            }
         }

         boolean hadDefault = seen.remove("General");
         List<String> out = new ArrayList(seen);
         if (hadDefault) {
            out.add("General");
         }

         if (out.isEmpty()) {
            out.add("General");
         }

         return out;
      }

      private String resolvePage(List<String> pages) {
         for(String p : pages) {
            if (p.equalsIgnoreCase(this.page)) {
               return p;
            }
         }

         return (String)pages.get(0);
      }

      private List<SettingButton<?>> itemsOn(String current) {
         List<SettingButton<?>> out = new ArrayList();

         for(SettingButton<?> item : this.items) {
            if (!item.isHidden() && item.getPage().equalsIgnoreCase(current)) {
               out.add(item);
            }
         }

         return out;
      }
   }
}
