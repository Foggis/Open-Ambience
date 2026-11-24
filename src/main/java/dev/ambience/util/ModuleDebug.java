package dev.ambience.util;

import dev.ambience.Ambience;
import dev.ambience.features.Feature;
import dev.ambience.features.modules.Module;
import dev.ambience.features.modules.client.DebugModule;
import dev.ambience.inject.Access;
import dev.ambience.util.traits.Util;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.StackWalker.Option;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.class_10185;
import net.minecraft.class_1799;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_3532;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_746;
import net.minecraft.class_239.class_240;

public final class ModuleDebug implements Util {
   private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
   private static final int DEFAULT_HISTORY = 160;
   private static final StackWalker WALKER;
   private static final Deque<String> history;
   private static final Map<String, String> lastStates;
   private static BufferedWriter writer;
   private static Path logFile;
   private static boolean reentrant;
   private static int tick;
   private static float lastFlyingYaw;
   private static float lastFlyingPitch;
   private static boolean hasFlying;
   private static int flagCount;
   private static int writeCount;

   private ModuleDebug() {
   }

   public static boolean isOn() {
      DebugModule debug = DebugModule.getInstance();
      return debug != null && debug.isEnabled();
   }

   public static void startSession() {
      close();
      tick = 0;
      flagCount = 0;
      hasFlying = false;
      history.clear();
      lastStates.clear();
      if (mc.field_1697 != null) {
         Path dir = mc.field_1697.toPath().resolve("logs");
         logFile = dir.resolve("ambience-debug.log");

         try {
            Files.createDirectories(dir);
            writer = Files.newBufferedWriter(logFile, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            writeRaw("===== Ambience debug session " + String.valueOf(LocalDateTime.now()) + " =====");
            writeRaw("file=" + String.valueOf(logFile.toAbsolutePath()));
            flush();
         } catch (IOException e) {
            Ambience.LOGGER.error("Failed to open ambience-debug.log", e);
            writer = null;
         }

      }
   }

   public static void stopSession() {
      writeRaw("===== debug session end flags=" + flagCount + " =====");
      close();
   }

   public static Path logFile() {
      return logFile;
   }

   public static void onPreTick() {
      if (isOn() && mc.field_1724 != null) {
         ++tick;
         if ((Boolean)DebugModule.getInstance().logTicks.getValue()) {
            event("TICK", "pre", snapshot());
         }

         pollModuleStates();
      }
   }

   public static void onFlying() {
      if (isOn() && mc.field_1724 != null) {
         class_746 p = mc.field_1724;
         lastFlyingYaw = p.method_36454();
         lastFlyingPitch = p.method_36455();
         hasFlying = true;
         if ((Boolean)DebugModule.getInstance().logFlying.getValue()) {
            event("FLYING", "sendPosition", snapshot());
         }

      }
   }

   public static void look(float fromYaw, float fromPitch, float toYaw, float toPitch) {
      if (isOn() && (Boolean)DebugModule.getInstance().logLooks.getValue()) {
         if (fromYaw != toYaw || fromPitch != toPitch) {
            event("LOOK", caller(), String.format("fromYaw=%.10f fromPitch=%.10f toYaw=%.10f toPitch=%.10f dYaw=%.10f dPitch=%.10f %s", fromYaw, fromPitch, toYaw, toPitch, wrap(toYaw - fromYaw), toPitch - fromPitch, snapshot()));
         }
      }
   }

   public static void silent(String kind, String detail) {
      if (isOn()) {
         String var10002 = caller();
         event("SILENT", kind, var10002 + " " + detail + " " + snapshot());
      }
   }

   public static void action(String kind, String detail) {
      if (isOn()) {
         event("ACTION", caller(), kind + " " + detail + " " + snapshot());
      }
   }

   public static void interact(String kind, String detail) {
      if (isOn()) {
         event("INTERACT", kind, detail + " vsLastFlying=" + vsLastFlying() + " " + snapshot());
      }
   }

   public static void slot(int from, int to, String item) {
      if (isOn()) {
         event("SLOT", caller(), "from=" + from + " to=" + to + " item=" + item + " " + snapshot());
      }
   }

   public static void omni(String kind, String detail) {
      if (isOn()) {
         event("OMNI", kind, detail + " " + snapshot());
      }
   }

   public static void module(String name, String kind, String detail) {
      if (isOn()) {
         event("MODULE", name, kind + (detail != null && !detail.isEmpty() ? " " + detail : ""));
      }
   }

   public static void onChat(String raw) {
      if (raw != null && !raw.isEmpty()) {
         String text = raw.replace('\n', ' ').trim();
         if (isGrimFlag(text)) {
            if (isOn()) {
               ++flagCount;
               String dump = buildFlagDump(text);
               writeRaw(dump);
               flush();
               DebugModule debug = DebugModule.getInstance();
               if (debug != null && (Boolean)debug.chatNotify.getValue() && mc.field_1724 != null) {
                  mc.field_1724.method_7353(class_2561.method_43470("Ambience debug captured flag #" + flagCount + " → logs/ambience-debug.log"), false);
               }

            }
         }
      }
   }

   private static boolean isGrimFlag(String text) {
      String lower = text.toLowerCase();
      if (!lower.contains("grim")) {
         return false;
      } else {
         return lower.contains("failed") || lower.contains("flag") || lower.contains("vl");
      }
   }

   private static String buildFlagDump(String text) {
      StringBuilder out = new StringBuilder(4096);
      out.append('\n');
      out.append("########## GRIM FLAG #").append(flagCount).append(" ##########\n");
      out.append(stamp()).append(" FLAG ").append(text).append('\n');
      out.append("now ").append(snapshot()).append('\n');
      out.append("--- last ").append(history.size()).append(" events ---\n");

      for(String line : history) {
         out.append(line).append('\n');
      }

      out.append("########## END FLAG #").append(flagCount).append(" ##########\n");
      return out.toString();
   }

   private static void pollModuleStates() {
      if (Ambience.moduleManager != null) {
         for(Module module : Ambience.moduleManager.getModules()) {
            if (!(module instanceof DebugModule)) {
               String key = module.getName();
               String state = module.isEnabled() ? module.getDebugState() : "off";
               if (state == null) {
                  state = module.isEnabled() ? "on" : "off";
               }

               String previous = (String)lastStates.put(key, state);
               if (previous != null && !previous.equals(state)) {
                  event("STATE", key, previous + " -> " + state + " " + snapshot());
               }
            }
         }

      }
   }

   private static void event(String kind, String who, String detail) {
      if (!reentrant) {
         reentrant = true;

         try {
            String line = stamp() + " t=" + tick + " " + kind + " [" + who + "] " + detail;
            push(line);
            writeRaw(line);
         } finally {
            reentrant = false;
         }

      }
   }

   private static String snapshot() {
      class_746 p = mc.field_1724;
      if (p == null) {
         return "player=null";
      } else {
         class_243 pos = p.method_73189();
         class_243 vel = p.method_18798();
         float yaw = p.method_36454();
         float pitch = p.method_36455();
         StringBuilder s = new StringBuilder(512);
         s.append(String.format("pos=%.10f,%.10f,%.10f vel=%.10f,%.10f,%.10f yaw=%.10f pitch=%.10f yawO=%.10f pitchO=%.10f", pos.field_1352, pos.field_1351, pos.field_1350, vel.field_1352, vel.field_1351, vel.field_1350, yaw, pitch, p.field_5982, p.field_6004));
         s.append(" onGround=").append(p.method_24828());
         s.append(" fallFly=").append(p.method_6128());
         s.append(" fall=").append(String.format("%.10f", p.field_6017));
         s.append(" sprint=").append(p.method_5624());
         s.append(" sneak=").append(p.method_5715());
         s.append(" using=").append(p.method_6115());
         s.append(" hurt=").append(p.field_6235);
         s.append(" slot=").append(p.method_31548().method_67532());
         s.append(" main=").append(itemName(p.method_6047()));
         s.append(" off=").append(itemName(p.method_6079()));
         s.append(" silent=").append(SilentRotation.isActive() ? SilentRotation.getOwner() : "NONE");
         if (SilentRotation.isActive()) {
            s.append(String.format("(camYaw=%.10f camPitch=%.10f)", SilentRotation.getClientYaw(), SilentRotation.getClientPitch()));
         }

         s.append(" actionTick=").append(GrimMovementGuard.actionThisTick());
         s.append(" keys=").append(keys());
         if (p.field_3913 != null) {
            s.append(String.format(" move=%.4f,%.4f", Access.moveVector(p.field_3913).field_1343, Access.moveVector(p.field_3913).field_1342));
         }

         s.append(" hit=").append(hit());
         s.append(" vsLastFlying=").append(vsLastFlying());
         s.append(" busy=").append(busy());
         s.append(" enabled=").append(enabledNames());
         return s.toString();
      }
   }

   private static String vsLastFlying() {
      return hasFlying && mc.field_1724 != null ? String.format("dYaw=%.10f dPitch=%.10f lastYaw=%.10f lastPitch=%.10f", wrap(mc.field_1724.method_36454() - lastFlyingYaw), mc.field_1724.method_36455() - lastFlyingPitch, lastFlyingYaw, lastFlyingPitch) : "none";
   }

   private static String keys() {
      if (mc.field_1690 == null) {
         return "-";
      } else {
         StringBuilder k = new StringBuilder();
         if (mc.field_1690.field_1894.method_1434()) {
            k.append('W');
         }

         if (mc.field_1690.field_1913.method_1434()) {
            k.append('A');
         }

         if (mc.field_1690.field_1881.method_1434()) {
            k.append('S');
         }

         if (mc.field_1690.field_1849.method_1434()) {
            k.append('D');
         }

         if (mc.field_1690.field_1903.method_1434()) {
            k.append('J');
         }

         if (mc.field_1690.field_1832.method_1434()) {
            k.append('_');
         }

         if (mc.field_1690.field_1867.method_1434()) {
            k.append('P');
         }

         if (mc.field_1690.field_1904.method_1434()) {
            k.append('R');
         }

         if (mc.field_1690.field_1886.method_1434()) {
            k.append('L');
         }

         if (mc.field_1724 != null && mc.field_1724.field_3913 != null) {
            class_10185 in = mc.field_1724.field_3913.field_54155;
            k.append("/pkt=");
            if (in.comp_3159()) {
               k.append('W');
            }

            if (in.comp_3161()) {
               k.append('A');
            }

            if (in.comp_3160()) {
               k.append('S');
            }

            if (in.comp_3162()) {
               k.append('D');
            }

            if (in.comp_3163()) {
               k.append('J');
            }
         }

         return k.isEmpty() ? "-" : k.toString();
      }
   }

   private static String hit() {
      class_239 hit = mc.field_1765;
      if (hit == null) {
         return "null";
      } else {
         if (hit instanceof class_3965) {
            class_3965 block = (class_3965)hit;
            if (block.method_17783() == class_240.field_1332) {
               String var3 = block.method_17777().method_23854();
               return "block:" + var3 + "/" + String.valueOf(block.method_17780()) + String.format("@%.10f,%.10f,%.10f", block.method_17784().field_1352, block.method_17784().field_1351, block.method_17784().field_1350);
            }
         }

         if (hit instanceof class_3966) {
            class_3966 entity = (class_3966)hit;
            String var10000 = entity.method_17782().method_5477().getString();
            return "entity:" + var10000 + "#" + entity.method_17782().method_5628();
         } else {
            return hit.method_17783().name();
         }
      }
   }

   private static String busy() {
      if (Ambience.moduleManager == null) {
         return "-";
      } else {
         String joined = (String)Ambience.moduleManager.getModules().stream().filter(Module::isEnabled).filter((m) -> !(m instanceof DebugModule)).map((m) -> {
            String state = m.getDebugState();
            return state == null ? null : m.getName() + "=" + state;
         }).filter((s) -> s != null).collect(Collectors.joining(","));
         return joined.isEmpty() ? "-" : joined;
      }
   }

   private static String enabledNames() {
      if (Ambience.moduleManager == null) {
         return "-";
      } else {
         String joined = (String)Ambience.moduleManager.getModules().stream().filter(Module::isEnabled).filter((m) -> m.getCategory() != Module.Category.RENDER && m.getCategory() != Module.Category.VISUALS && !(m instanceof DebugModule) && !"ClickGui".equals(m.getName())).map(Feature::getName).collect(Collectors.joining(","));
         return joined.isEmpty() ? "-" : joined;
      }
   }

   private static String itemName(class_1799 stack) {
      if (stack != null && !stack.method_7960()) {
         String var10000 = stack.method_7909().toString();
         return var10000 + "*" + stack.method_7947();
      } else {
         return "empty";
      }
   }

   private static String caller() {
      try {
         return (String)WALKER.walk((stream) -> (String)stream.filter((f) -> {
               String name = f.getClassName();
               return name.startsWith("dev.ambience") && !name.contains("ModuleDebug");
            }).map((f) -> {
               String var10000 = simple(f.getClassName());
               return var10000 + "." + f.getMethodName();
            }).limit(3L).collect(Collectors.joining(" <- ")));
      } catch (Throwable var1) {
         return "unknown";
      }
   }

   private static String simple(String name) {
      int i = name.lastIndexOf(46);
      return i < 0 ? name : name.substring(i + 1);
   }

   private static float wrap(float yaw) {
      return class_3532.method_15393(yaw);
   }

   private static String stamp() {
      return TIME.format(LocalDateTime.now());
   }

   private static int historySize() {
      DebugModule debug = DebugModule.getInstance();
      return debug == null ? 160 : (Integer)debug.history.getValue();
   }

   private static void push(String line) {
      history.addLast(line);
      int max = historySize();

      while(history.size() > max) {
         history.removeFirst();
      }

   }

   private static void writeRaw(String line) {
      if (writer != null) {
         try {
            writer.write(line);
            writer.newLine();
            if (++writeCount % 25 == 0) {
               writer.flush();
            }
         } catch (IOException e) {
            Ambience.LOGGER.error("Failed to write ambience-debug.log", e);
         }

      }
   }

   private static void flush() {
      if (writer != null) {
         try {
            writer.flush();
         } catch (IOException var1) {
         }

      }
   }

   private static void close() {
      if (writer != null) {
         try {
            writer.flush();
            writer.close();
         } catch (IOException var1) {
         }

         writer = null;
      }
   }

   static {
      WALKER = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);
      history = new ArrayDeque();
      lastStates = new LinkedHashMap();
   }
}
