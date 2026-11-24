package dev.ambience.features.gui.items.buttons;

import dev.ambience.features.settings.BezierCurve;
import dev.ambience.features.settings.Bind;
import dev.ambience.features.settings.Setting;
import java.awt.Color;
import org.joml.Vector2f;

public abstract class SettingButton<T> extends Button {
   protected final Setting<T> setting;

   protected SettingButton(Setting<T> setting) {
      super(setting.getName());
      this.setting = setting;
   }

   public void update() {
      this.setHidden(!this.setting.isVisible());
   }

   public String getPage() {
      return this.setting.getPage();
   }

   public Setting<T> getSetting() {
      return this.setting;
   }

   @SuppressWarnings("unchecked")
   public static SettingButton<?> create(Setting<?> setting) {
      if (setting.isColorSetting()) {
         return new ColorButton((Setting<Color>) setting);
      } else if (setting.isBezierSetting()) {
         return new CurveButton((Setting<BezierCurve>) setting);
      } else if (setting.isVec2fSetting()) {
         return new RangeSlider((Setting<Vector2f>) setting);
      } else if (setting.isNumberSetting() && setting.hasRestriction()) {
         return new Slider((Setting<Number>) setting);
      } else if (setting.isEnumSetting()) {
         return new EnumButton((Setting<Enum<?>>) setting);
      } else {
         Object v = setting.getValue();
         if (v instanceof Boolean) {
            return new BooleanButton((Setting<Boolean>) setting);
         } else if (v instanceof Bind) {
            return new BindButton((Setting<Bind>) setting);
         } else if (v instanceof String) {
            return (SettingButton<?>)(setting.isBlockList() ? new BlockListButton((Setting<String>) setting) : new StringButton((Setting<String>) setting));
         } else {
            return null;
         }
      }
   }
}
