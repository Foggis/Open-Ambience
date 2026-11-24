package dev.ambience.features.modules.client;

import dev.ambience.Ambience;
import dev.ambience.event.impl.ClientEvent;
import dev.ambience.event.system.Subscribe;
import dev.ambience.features.commands.Command;
import dev.ambience.features.gui.AmbienceGui;
import dev.ambience.features.gui.CenteredGui;
import dev.ambience.features.gui.NativeClickGuiScreen;
import dev.ambience.features.gui.items.buttons.BindButton;
import dev.ambience.features.modules.Module;
import dev.ambience.features.settings.Bind;
import dev.ambience.features.settings.Setting;
import dev.ambience.hooks.GuiRuntime;
import dev.ambience.util.ColorUtil;
import dev.ambience.util.render.font.Fonts;
import java.awt.Color;
import net.minecraft.class_437;

public class ClickGuiModule extends Module implements GuiRuntime.Host {
   private static ClickGuiModule INSTANCE;
   public Setting<String> prefix = this.str("Prefix", ".");
   public Setting<Theme> theme;
   public Setting<Layout> layout;
   public Setting<Color> customColor;
   public Setting<Boolean> smooth;
   public Setting<Integer> rainbowHue;
   public Setting<Float> rainbowBrightness;
   public Setting<Float> rainbowSaturation;
   public Setting<Boolean> clickGuiFont;
   public Setting<Boolean> hudFont;
   public Setting<String> fontName;
   private static final Color CAT_COMBAT = new Color(172, 80, 22);
   private static final Color CAT_WORLD = new Color(118, 168, 118);
   private static final Color CAT_RENDER = new Color(98, 166, 200);
   private static final Color CAT_VISUALS = new Color(148, 118, 210);
   private static final Color CAT_MOVEMENT = new Color(212, 142, 78);
   private static final Color CAT_PLAYER = new Color(202, 180, 92);
   private static final Color CAT_FUNNY = new Color(198, 124, 178);
   private static final Color CAT_CLIENT = new Color(70, 75, 82);
   private static final Color CAT_CONFIGS = new Color(92, 148, 132);
   private static final Color CAT_HUD = new Color(96, 142, 200);
   private static final Color AMBIENCE_ACCENT = new Color(144, 64, 16);
   private static final Color AMBIENCE_MODULE = new Color(172, 80, 22);

   public Color categoryAccent(Module.Category cat) {
      return this.categoryAccent(cat, 0.0F);
   }

   public Color categoryAccent(Module.Category cat, float yOffset) {
      Theme t = this.theme.getValue();
      if (t == ClickGuiModule.Theme.RAINBOW) {
         return this.rainbowAt(yOffset);
      } else if (t == ClickGuiModule.Theme.CUSTOM) {
         return this.customColor.getValue();
      } else if (t == ClickGuiModule.Theme.AMBIENCE) {
         return AMBIENCE_ACCENT;
      } else if (cat == null) {
         return CAT_CLIENT;
      } else {
         switch (cat) {
            case COMBAT -> {
               return CAT_COMBAT;
            }
            case WORLD -> {
               return CAT_WORLD;
            }
            case RENDER -> {
               return CAT_RENDER;
            }
            case VISUALS -> {
               return CAT_VISUALS;
            }
            case MOVEMENT -> {
               return CAT_MOVEMENT;
            }
            case PLAYER -> {
               return CAT_PLAYER;
            }
            case FUNNY -> {
               return CAT_FUNNY;
            }
            case CLIENT -> {
               return CAT_CLIENT;
            }
            case CONFIGS -> {
               return CAT_CONFIGS;
            }
            case HUD -> {
               return CAT_HUD;
            }
            default -> {
               return CAT_CLIENT;
            }
         }
      }
   }

   public Color moduleAccent(Module.Category cat, float yOffset) {
      return this.theme.getValue() == ClickGuiModule.Theme.AMBIENCE ? AMBIENCE_MODULE : this.categoryAccent(cat, yOffset);
   }

   public Color chatAccent() {
      Theme t = this.theme.getValue();
      if (t == ClickGuiModule.Theme.RAINBOW) {
         return this.rainbowAt(0.0F);
      } else if (t == ClickGuiModule.Theme.CUSTOM) {
         return this.customColor.getValue();
      } else {
         return t == ClickGuiModule.Theme.AMBIENCE ? AMBIENCE_ACCENT : Color.WHITE;
      }
   }

   public Color rainbowAt(float yOffset) {
      return ColorUtil.rainbow((int)(yOffset / 10.0F * (float)(Integer)this.rainbowHue.getValue()));
   }

   public float getExpandSpeed() {
      return (Boolean)this.smooth.getValue() ? 0.5F : 1.0F;
   }

   public ClickGuiModule() {
      super("ClickGui", "Opens the ClickGui", Module.Category.CLIENT);
      this.theme = this.mode("Theme", ClickGuiModule.Theme.AMBIENCE);
      this.layout = this.mode("Layout", ClickGuiModule.Layout.CENTER);
      this.customColor = this.color("Custom Color", 120, 170, 210, 220);
      this.smooth = this.bool("Smooth", true);
      this.rainbowHue = this.num("Delay", 240, 0, 600);
      this.rainbowBrightness = this.num("Brightness", 200.0F, 1.0F, 255.0F);
      this.rainbowSaturation = this.num("Saturation", 140.0F, 1.0F, 255.0F);
      this.clickGuiFont = this.bool("ClickGUI Font", true);
      this.hudFont = this.bool("HUD Font", false);
      this.fontName = this.str("Font Name", "");
      this.bind.setValue(new Bind(344));
      this.bindMode.setVisibility((v) -> false);
      this.prefix.setVisibility((v) -> false);
      this.layout.setVisibility((v) -> false);
      this.rainbowHue.setVisibility((v) -> this.theme.getValue() == ClickGuiModule.Theme.RAINBOW);
      this.rainbowBrightness.setVisibility((v) -> this.theme.getValue() == ClickGuiModule.Theme.RAINBOW);
      this.rainbowSaturation.setVisibility((v) -> this.theme.getValue() == ClickGuiModule.Theme.RAINBOW);
      this.customColor.setVisibility((v) -> this.theme.getValue() == ClickGuiModule.Theme.CUSTOM);
      this.fontName.setVisibility((v) -> (Boolean)this.clickGuiFont.getValue() || (Boolean)this.hudFont.getValue());
      INSTANCE = this;
      GuiRuntime.bind(this);
   }

   @Subscribe
   public void onSettingChange(ClientEvent event) {
      if (event.getType() == ClientEvent.Type.SETTING_UPDATE && event.getSetting().getFeature().equals(this)) {
         if (event.getSetting().equals(this.prefix)) {
            Ambience.commandManager.setCommandPrefix(this.prefix.getPlannedValue());
            Command.sendMessage("Prefix set to {global} %s", Ambience.commandManager.getCommandPrefix());
         }

         if (event.getSetting().equals(this.clickGuiFont) || event.getSetting().equals(this.hudFont) || event.getSetting().equals(this.fontName)) {
            Fonts.markDirty();
         }
      }

   }

   public void onEnable() {
      if (mc.field_1755 != null && !isGuiScreen(mc.field_1755)) {
         this.enabled.setValue(false);
      } else {
         Ambience.LOGGER.info("Opening ClickGui");
         mc.method_1507(CenteredGui.get());
      }
   }

   public void onLoad() {
      Color fixedAccent = new Color(120, 175, 220, 220);
      Ambience.colorManager.register("ui", () -> fixedAccent);
      Ambience.colorManager.register("chat", this::chatAccent);
      Ambience.colorManager.register("chatBracket", this::chatAccent);
      Ambience.commandManager.setCommandPrefix(this.prefix.getValue());
      if (this.getBind() != null && this.getBind().getKey() > 0) {
         Ambience.LOGGER.info("ClickGui bind after config load: {}", this.getBind().getKey());
      } else {
         this.bind.setValue(new Bind(344));
         Ambience.LOGGER.info("ClickGui bind was unbound after config load; restored Right Shift (344)");
      }

   }

   public void onTick() {
      if (!isGuiScreen(mc.field_1755)) {
         this.disable();
      }

   }

   public static boolean isGuiScreen(class_437 screen) {
      return screen instanceof NativeClickGuiScreen || screen instanceof CenteredGui || screen instanceof AmbienceGui;
   }

   public boolean modernBlur() {
      return CenteredGui.modern();
   }

   public boolean isClickGui(class_437 screen) {
      return isGuiScreen(screen);
   }

   public boolean handleBindMouse(int button, int action) {
      return NativeClickGuiScreen.handleBindMouse(button, action) || BindButton.handleMouse(button, action);
   }

   public boolean clickGuiFont() {
      return (Boolean)this.clickGuiFont.getValue();
   }

   public boolean hudFont() {
      return (Boolean)this.hudFont.getValue();
   }

   public String fontName() {
      return this.fontName.getValue();
   }

   public float rainbowSaturation() {
      return (Float)this.rainbowSaturation.getValue();
   }

   public float rainbowBrightness() {
      return (Float)this.rainbowBrightness.getValue();
   }

   public int rainbowHue() {
      return (Integer)this.rainbowHue.getValue();
   }

   public boolean rainbowTheme() {
      return this.theme.getValue() == ClickGuiModule.Theme.RAINBOW;
   }

   public static ClickGuiModule getInstance() {
      return INSTANCE;
   }

   public static enum Theme {
      AMBIENCE,
      COLORS,
      CUSTOM,
      RAINBOW;

      // $FF: synthetic method
      private static Theme[] $values() {
         return new Theme[]{AMBIENCE, COLORS, CUSTOM, RAINBOW};
      }
   }

   public static enum Layout {
      CENTER,
      PANELS;

      // $FF: synthetic method
      private static Layout[] $values() {
         return new Layout[]{CENTER, PANELS};
      }
   }
}
