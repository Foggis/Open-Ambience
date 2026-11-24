package dev.ambience.features.gui;

import dev.ambience.Ambience;
import dev.ambience.features.Feature;
import dev.ambience.features.modules.Module;
import dev.ambience.features.modules.client.ClickGuiModule;
import dev.ambience.nativebridge.NativeModules;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_437;

public final class NativeClickGuiScreen extends class_437 {
   private static final String[] CONFIG_CATEGORIES = new String[]{"General", "Crystal PvP", "Anchor PvP", "Sword PvP", "Mace PvP", "SMP"};
   private static NativeClickGuiScreen instance;
   private int pendingClick = -1;
   private int mouseX;
   private int mouseY;
   private long openedAtNs;
   private boolean rendererReported;
   private boolean searchFocused;
   private final StringBuilder searchQuery = new StringBuilder();
   private boolean leftDown;
   private float pendingScroll;
   private boolean closing;
   private boolean configNameFocused;
   private final StringBuilder configName = new StringBuilder();
   private String configStatus = "";
   private boolean configCloud;
   private boolean configPublicTab;
   private boolean cloudVisibility;
   private int configCategory;

   private NativeClickGuiScreen() {
      super(class_2561.method_43470("Ambience Native Overlay"));
   }

   public static NativeClickGuiScreen get() {
      if (instance == null) {
         instance = new NativeClickGuiScreen();
      }

      instance.pendingClick = -1;
      instance.openedAtNs = System.nanoTime();
      instance.rendererReported = false;
      instance.searchFocused = false;
      instance.searchQuery.setLength(0);
      instance.leftDown = false;
      instance.pendingScroll = 0.0F;
      instance.closing = false;
      instance.configNameFocused = false;
      instance.configName.setLength(0);
      instance.configStatus = "";
      instance.configCloud = true;
      instance.configPublicTab = false;
      instance.cloudVisibility = false;
      instance.configCategory = 0;
      NativeModules.clickGuiOpen();
      return instance;
   }

   public void method_25394(class_332 graphics, int mouseX, int mouseY, float delta) {
      this.mouseX = mouseX;
      this.mouseY = mouseY;
   }

   public static void renderNative(float delta) {
      NativeClickGuiScreen screen = instance;
      class_310 minecraft = class_310.method_1551();
      boolean loadedWorld = minecraft != null && minecraft.field_1687 != null && minecraft.field_1724 != null;
      Module pill = Ambience.moduleManager == null ? null : Ambience.moduleManager.getModuleByName("SessionPill");
      boolean showPill = loadedWorld && (pill == null || pill.isEnabled());
      if (showPill) {
         NativeModules.clickGuiSync(NativeGuiModel.hudModel());
      }

      if (screen != null && screen.field_22787 != null && screen.field_22787.field_1755 == screen) {
         if (showPill) {
            String sessionName = minecraft.method_1548() == null ? "" : minecraft.method_1548().method_1676();
            NativeModules.clickGuiHud(minecraft.method_22683().method_4489(), minecraft.method_22683().method_4506(), sessionName);
         }

         screen.drawNative(delta);
      } else if (showPill && minecraft.method_22683() != null) {
         String sessionName = minecraft.method_1548() == null ? "" : minecraft.method_1548().method_1676();
         NativeModules.clickGuiHud(minecraft.method_22683().method_4489(), minecraft.method_22683().method_4506(), sessionName);
      }
   }

   public static boolean handleBindMouse(int button, int action) {
      NativeClickGuiScreen screen = instance;
      return action == 1 && screen != null && !screen.closing && screen.field_22787 != null && screen.field_22787.field_1755 == screen ? NativeGuiModel.mousePressed(button) : false;
   }

   private void drawNative(float delta) {
      List<Module> modules = this.snapshot();
      NativeModules.clickGuiSync(NativeGuiModel.encode(modules, this.pendingScroll, this.searchFocused, this.configNameFocused, this.configName.toString(), this.configStatus, true, this.configPublicTab, this.cloudVisibility, CONFIG_CATEGORIES[this.configCategory]));
      this.pendingScroll = 0.0F;
      String[] names = new String[modules.size()];
      String[] descriptions = new String[modules.size()];
      int[] categories = new int[modules.size()];
      boolean[] enabled = new boolean[modules.size()];

      for(int i = 0; i < modules.size(); ++i) {
         Module module = (Module)modules.get(i);
         names[i] = module.getDisplayName();
         descriptions[i] = module.getDescription();
         categories[i] = module.getCategory().ordinal();
         enabled[i] = module.isEnabled();
      }

      String sessionName = this.field_22787.method_1548() == null ? "" : this.field_22787.method_1548().method_1676();
      int nativeButton = this.closing ? 11 : (this.pendingClick >= 0 ? this.pendingClick : (this.leftDown ? 10 : -1));
      int framebufferWidth = this.field_22787.method_22683().method_4489();
      int framebufferHeight = this.field_22787.method_22683().method_4506();
      int framebufferMouseX = (int)Math.round((double)this.mouseX * ((double)framebufferWidth / (double)Math.max(1, this.field_22789)));
      int framebufferMouseY = (int)Math.round((double)this.mouseY * ((double)framebufferHeight / (double)Math.max(1, this.field_22790)));
      int command = NativeModules.clickGuiFrame(framebufferWidth, framebufferHeight, delta, framebufferMouseX, framebufferMouseY, nativeButton, names, descriptions, categories, enabled, sessionName, this.searchQuery.toString());
      if (!this.rendererReported) {
         this.rendererReported = true;
         if (command < 0) {
            Ambience.LOGGER.error("Native ClickGui renderer initialization failed (status {})", command);
         } else {
            Ambience.LOGGER.info("Native ClickGui frame submitted to the window framebuffer");
         }
      }

      this.pendingClick = -1;
      if (command == -10) {
         this.finishClose();
      } else if (command == -20) {
         this.searchFocused = true;
         this.configNameFocused = false;
         NativeGuiModel.cancelEditing();
      } else if (command == -40) {
         this.configNameFocused = true;
         this.searchFocused = false;
         NativeGuiModel.cancelEditing();
      } else if (command == -42) {
         this.configNameFocused = false;
      } else if (command >= -46 && command <= -43) {
         if (command == -43) {
            this.configCloud = false;
         }

         if (command == -44) {
            this.configCloud = true;
         }

         if (command == -45) {
            this.configPublicTab = false;
         }

         if (command == -46) {
            this.configPublicTab = true;
         }

         this.configNameFocused = false;
      } else if (command == -48) {
         this.cloudVisibility = false;
      } else if (command == -50) {
         this.cloudVisibility = true;
      } else if (command == -49) {
         this.configCategory = (this.configCategory + 1) % CONFIG_CATEGORIES.length;
      } else if (command <= -60 && command > -60 - CONFIG_CATEGORIES.length) {
         this.configCategory = -60 - command;
      } else if (command != -47 && (command < 420000000 || command >= 460000000)) {
         if (command == -41 || command >= 400000000 && command < 420000000) {
            this.configStatus = NativeGuiModel.applyConfig(command, this.configName.toString());
            if (command == -41 && this.configStatus.startsWith("Saved")) {
               this.configName.setLength(0);
               this.configNameFocused = false;
            }

         } else {
            NativeGuiModel.apply(modules, command);
            if (command > 0 && command <= modules.size()) {
               Module module = (Module)modules.get(command - 1);
               if (!(module instanceof ClickGuiModule)) {
                  module.toggle();
               }
            }

         }
      } else {
         NativeGuiModel.applyCloud(command, this.configName.toString(), this.cloudVisibility, CONFIG_CATEGORIES[this.configCategory]);
         if (command == -47) {
            this.configName.setLength(0);
            this.configNameFocused = false;
         }

      }
   }

   private List<Module> snapshot() {
      List<Module> out = new ArrayList<>();

      for(Module.Category category : Module.Category.values()) {
         for(Module module : Ambience.moduleManager.guiModules(category, (String)null)) {
            if (!(module instanceof ClickGuiModule) && !module.hidden) {
               out.add(module);
            }
         }
      }

      out.sort(Comparator.comparingInt((Module modulex) -> modulex.getCategory().ordinal()).thenComparing(Feature::getName, String.CASE_INSENSITIVE_ORDER));
      return out;
   }

   public boolean method_25402(class_11909 click, boolean doubled) {
      if (this.closing) {
         return true;
      } else {
         this.mouseX = (int)click.comp_4798();
         this.mouseY = (int)click.comp_4799();
         if (NativeGuiModel.mousePressed(click.method_74245())) {
            this.pendingClick = -1;
            this.leftDown = false;
            return true;
         } else {
            this.pendingClick = click.method_74245();
            if (click.method_74245() == 0) {
               this.leftDown = true;
            }

            if (click.method_74245() == 0) {
               float windowWidth = Math.min(318.0F, (float)this.field_22789 - 24.0F);
               float windowHeight = Math.min(214.0F, (float)this.field_22790 - 24.0F);
               float windowX = ((float)this.field_22789 - windowWidth) * 0.5F;
               float windowY = ((float)this.field_22790 - windowHeight) * 0.5F;
               float searchX = windowX + windowWidth - 103.0F;
               this.searchFocused = (float)this.mouseX >= searchX && (float)this.mouseX < searchX + 78.0F && (float)this.mouseY >= windowY + 7.0F && (float)this.mouseY < windowY + 22.0F;
               if (this.searchFocused) {
                  NativeGuiModel.cancelEditing();
               }
            }

            return true;
         }
      }
   }

   public boolean method_25406(class_11909 click) {
      this.mouseX = (int)click.comp_4798();
      this.mouseY = (int)click.comp_4799();
      if (click.method_74245() == 0) {
         this.leftDown = false;
      }

      return true;
   }

   public boolean method_25401(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      this.pendingScroll += (float)verticalAmount;
      return true;
   }

   public boolean method_25404(class_11908 input) {
      ClickGuiModule gui = ClickGuiModule.getInstance();
      boolean escape = input.method_74228() == 256;
      if (this.searchFocused) {
         if (input.method_74228() == 259 && this.searchQuery.length() > 0) {
            this.searchQuery.deleteCharAt(this.searchQuery.length() - 1);
            return true;
         }

         if (escape) {
            this.searchFocused = false;
            this.searchQuery.setLength(0);
            return true;
         }
      }

      if (this.configNameFocused) {
         if (input.method_74228() == 259 && this.configName.length() > 0) {
            this.configName.deleteCharAt(this.configName.length() - 1);
            return true;
         }

         if (input.method_74228() == 257) {
            if (this.configCloud) {
               NativeGuiModel.applyCloud(-47, this.configName.toString(), this.cloudVisibility, CONFIG_CATEGORIES[this.configCategory]);
               this.configName.setLength(0);
               this.configNameFocused = false;
            } else {
               this.configStatus = NativeGuiModel.applyConfig(-41, this.configName.toString());
               if (this.configStatus.startsWith("Saved")) {
                  this.configName.setLength(0);
                  this.configNameFocused = false;
               }
            }

            return true;
         }

         if (escape) {
            this.configNameFocused = false;
            return true;
         }
      }

      if (NativeGuiModel.keyPressed(input.method_74228())) {
         return true;
      } else {
         boolean guiBind = gui != null && input.method_74228() == gui.getBind().getKey();
         if (!escape && (!guiBind || System.nanoTime() - this.openedAtNs <= 150000000L)) {
            return guiBind ? true : super.method_25404(input);
         } else {
            this.beginClose();
            return true;
         }
      }
   }

   public boolean method_25400(class_11905 input) {
      if (this.searchFocused && this.searchQuery.length() < 32) {
         String value = input.method_74226();
         if (!value.isEmpty() && value.codePointAt(0) >= 32) {
            this.searchQuery.append(value);
         }

         return true;
      } else if (this.configNameFocused && this.configName.length() < 48) {
         String value = input.method_74226();
         if (!value.isEmpty() && value.codePointAt(0) >= 32 && value.indexOf(9) < 0) {
            this.configName.append(value);
         }

         return true;
      } else {
         return NativeGuiModel.charTyped(input.method_74226()) ? true : super.method_25400(input);
      }
   }

   public void method_25419() {
      this.beginClose();
   }

   private void beginClose() {
      if (!this.closing) {
         this.closing = true;
         this.searchFocused = false;
         this.leftDown = false;
         this.pendingClick = -1;
      }

   }

   private void finishClose() {
      if (this.field_22787 != null) {
         this.field_22787.method_1507((class_437)null);
      }

   }

   public boolean method_25421() {
      return false;
   }

   public void method_25420(class_332 graphics, int mouseX, int mouseY, float delta) {
   }
}
