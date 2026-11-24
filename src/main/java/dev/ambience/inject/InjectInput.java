package dev.ambience.inject;

import dev.ambience.Ambience;
import dev.ambience.features.modules.Module;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.class_310;
import net.minecraft.class_3675;
import org.lwjgl.glfw.GLFW;

public final class InjectInput {
   private static final long DEBOUNCE_NS = 80000000L;
   private static boolean guiDown;
   private static boolean pollPrimed;
   private static boolean loggedPrime;
   private static boolean loggedGlfw;
   private static boolean loggedMixin;
   private static boolean loggedPollKey;
   private static volatile long lastGlfwNs;
   private static long lastChatCmdNs;
   private static int lastKey;
   private static int lastAction;
   private static long lastDispatchNs;
   private static final Set<Integer> held = ConcurrentHashMap.newKeySet();

   private InjectInput() {
   }

   public static void onRawKey(int key, int action) {
      if (action == 1 || action == 0) {
         lastGlfwNs = System.nanoTime();
         if (!loggedGlfw) {
            loggedGlfw = true;
            Ambience.LOGGER.info("InjectInput: GLFW callback live (first key {}, action {})", key, action);
         }

         class_310 mc = class_310.method_1551();
         if (mc != null) {
            mc.execute(() -> dispatch(key, action, "glfw"));
         }
      }
   }

   public static void onMixinKey(int key, int action) {
      if (action == 1 || action == 0) {
         if (!loggedMixin) {
            loggedMixin = true;
            Ambience.LOGGER.info("InjectInput: keyPress mixin live (first key {}, action {})", key, action);
         }

      }
   }

   public static boolean take(int key, int action) {
      if (key <= 0) {
         return false;
      } else {
         if (action == 1) {
            held.add(key);
         } else if (action == 0) {
            held.remove(key);
         }

         long now = System.nanoTime();
         if (key == lastKey && action == lastAction && now - lastDispatchNs < 80000000L) {
            return false;
         } else {
            lastKey = key;
            lastAction = action;
            lastDispatchNs = now;
            return true;
         }
      }
   }

   public static boolean keyHeld(int key) {
      return key > 0 && held.contains(key);
   }

   public static void poll(class_310 mc) {
      if (mc != null && mc.method_22683() != null && Ambience.moduleManager != null) {
         Module gui = Ambience.moduleManager.getModuleByName("ClickGui");
         if (gui != null) {
            int key = gui.getBind().getKey();
            if (key <= 0) {
               key = 344;
               if (!loggedPrime) {
                  Ambience.LOGGER.warn("ClickGui bind unbound; polling Right Shift (344)");
               }
            }

            if (!loggedPrime) {
               loggedPrime = true;
               Ambience.LOGGER.info("InjectInput: polling ClickGui bind {} (Right Shift is 344)", key);
            }

            if (System.nanoTime() - lastGlfwNs < 2000000000L) {
               pollPrimed = true;
            } else {
               boolean now = isBindDown(mc, key);
               if (mc.field_1755 != null) {
                  guiDown = now;
                  pollPrimed = true;
               } else if (!pollPrimed) {
                  guiDown = now;
                  pollPrimed = true;
               } else {
                  if (now && !guiDown) {
                     if (!loggedPollKey) {
                        loggedPollKey = true;
                        Ambience.LOGGER.info("InjectInput: poll rising edge on key {}", key);
                     }

                     dispatch(key, 1, "poll");
                  } else if (!now && guiDown) {
                     dispatch(key, 0, "poll");
                  }

                  guiDown = now;
               }
            }
         }
      }
   }

   public static boolean handleChat(String message) {
      if (message == null) {
         return false;
      } else {
         String trimmed = message.trim();
         if (trimmed.isEmpty()) {
            return false;
         } else {
            String prefix = ".";
            if (Ambience.commandManager != null && Ambience.commandManager.getCommandPrefix() != null && !Ambience.commandManager.getCommandPrefix().isEmpty()) {
               prefix = Ambience.commandManager.getCommandPrefix();
            }

            if (!trimmed.startsWith(prefix)) {
               return false;
            } else {
               String cmd = trimmed.substring(prefix.length()).trim().toLowerCase();
               if (!cmd.equals("gui") && !cmd.equals("clickgui")) {
                  return false;
               } else {
                  long now = System.nanoTime();
                  if (now - lastChatCmdNs < 500000000L) {
                     return true;
                  } else {
                     lastChatCmdNs = now;
                     Ambience.LOGGER.info("Opening ClickGui via {}{}", prefix, cmd);
                     toggleClickGui();
                     return true;
                  }
               }
            }
         }
      }
   }

   private static boolean isBindDown(class_310 mc, int key) {
      try {
         return class_3675.method_15987(mc.method_22683(), key);
      } catch (Throwable var3) {
         return GLFW.glfwGetKey(mc.method_22683().method_4490(), key) == 1;
      }
   }

   private static void dispatch(int key, int action, String source) {
      if (Ambience.moduleManager != null && key > 0) {
         try {
            if (action == 1) {
               Ambience.moduleManager.onKeyPressed(key);
            } else {
               Ambience.moduleManager.onKeyReleased(key);
            }
         } catch (Throwable t) {
            Ambience.LOGGER.error("InjectInput {} dispatch failed for key {}", source, key, t);
         }

      }
   }

   private static void toggleClickGui() {
      if (Ambience.moduleManager != null) {
         Module gui = Ambience.moduleManager.getModuleByName("ClickGui");
         if (gui == null) {
            Ambience.LOGGER.error("ClickGui module missing — cannot open menu");
         } else {
            class_310 mc = class_310.method_1551();
            Runnable open = () -> {
               try {
                  gui.toggle();
               } catch (Throwable t) {
                  Ambience.LOGGER.error("ClickGui toggle failed", t);
               }

            };
            if (mc != null && !mc.method_18854()) {
               mc.execute(open);
            } else {
               open.run();
            }
         }
      }
   }
}
