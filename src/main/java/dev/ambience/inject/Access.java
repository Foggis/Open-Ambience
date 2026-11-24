package dev.ambience.inject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.class_11246;
import net.minecraft.class_1297;
import net.minecraft.class_241;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_8030;

public final class Access {
   private static final Map<String, Field> FIELDS = new ConcurrentHashMap();
   private static final Map<String, Method> METHODS = new ConcurrentHashMap();

   private Access() {
   }

   public static float getFloat(Object owner, String intermediary, String named) {
      try {
         return field(owner.getClass(), intermediary, named).getFloat(owner);
      } catch (ReflectiveOperationException e) {
         throw new IllegalStateException(e);
      }
   }

   public static void setFloat(Object owner, String intermediary, String named, float value) {
      try {
         field(owner.getClass(), intermediary, named).setFloat(owner, value);
      } catch (ReflectiveOperationException e) {
         throw new IllegalStateException(e);
      }
   }

   public static int getInt(Object owner, String intermediary, String named) {
      try {
         return field(owner.getClass(), intermediary, named).getInt(owner);
      } catch (ReflectiveOperationException e) {
         throw new IllegalStateException(e);
      }
   }

   public static void setInt(Object owner, String intermediary, String named, int value) {
      try {
         field(owner.getClass(), intermediary, named).setInt(owner, value);
      } catch (ReflectiveOperationException e) {
         throw new IllegalStateException(e);
      }
   }

   public static void setBoolean(Object owner, String intermediary, String named, boolean value) {
      try {
         field(owner.getClass(), intermediary, named).setBoolean(owner, value);
      } catch (ReflectiveOperationException e) {
         throw new IllegalStateException(e);
      }
   }

   public static <T> T get(Object owner, String intermediary, String named) {
      try {
         return (T)field(owner.getClass(), intermediary, named).get(owner);
      } catch (ReflectiveOperationException e) {
         throw new IllegalStateException(e);
      }
   }

   public static void set(Object owner, String intermediary, String named, Object value) {
      try {
         field(owner.getClass(), intermediary, named).set(owner, value);
      } catch (ReflectiveOperationException e) {
         throw new IllegalStateException(e);
      }
   }

   public static void invoke(Object owner, String intermediary, String named, Class<?>[] params, Object... args) {
      try {
         method(owner.getClass(), intermediary, named, params).invoke(owner, args);
      } catch (ReflectiveOperationException e) {
         throw new IllegalStateException(e);
      }
   }

   public static <T> T invokeResult(Object owner, String intermediary, String named, Class<?>[] params, Object... args) {
      try {
         return (T)method(owner.getClass(), intermediary, named, params).invoke(owner, args);
      } catch (ReflectiveOperationException e) {
         throw new IllegalStateException(e);
      }
   }

   private static Field field(Class<?> start, String intermediary, String named) {
      String var10000 = start.getName();
      String key = var10000 + "#" + intermediary;
      return (Field)FIELDS.computeIfAbsent(key, (k) -> {
         for(Class<?> c = start; c != null; c = c.getSuperclass()) {
            for(String n : new String[]{intermediary, named}) {
               if (n != null && !n.isBlank()) {
                  try {
                     Field f = c.getDeclaredField(n);
                     f.setAccessible(true);
                     return f;
                  } catch (NoSuchFieldException var10) {
                  }
               }
            }
         }

         throw new IllegalStateException("No field " + intermediary + "/" + named + " on " + start.getName());
      });
   }

   private static Method method(Class<?> start, String intermediary, String named, Class<?>[] params) {
      String key = start.getName() + "#" + intermediary + paramsKey(params);
      return (Method)METHODS.computeIfAbsent(key, (k) -> {
         for(Class<?> c = start; c != null; c = c.getSuperclass()) {
            for(String n : new String[]{intermediary, named}) {
               if (n != null && !n.isBlank()) {
                  try {
                     Method m = c.getDeclaredMethod(n, params);
                     m.setAccessible(true);
                     return m;
                  } catch (NoSuchMethodException var11) {
                  }
               }
            }
         }

         throw new IllegalStateException("No method " + intermediary + "/" + named + " on " + start.getName());
      });
   }

   private static String paramsKey(Class<?>[] params) {
      StringBuilder sb = new StringBuilder();

      for(Class<?> p : params) {
         sb.append(':').append(p.getName());
      }

      return sb.toString();
   }

   public static float yRot(Object entity) {
      return getFloat(entity, "field_6031", "yRot");
   }

   public static void setYRot(Object entity, float v) {
      setFloat(entity, "field_6031", "yRot", v);
   }

   public static float xRot(Object entity) {
      return getFloat(entity, "field_5965", "xRot");
   }

   public static void setXRot(Object entity, float v) {
      setFloat(entity, "field_5965", "xRot", v);
   }

   public static float yRotO(Object entity) {
      return getFloat(entity, "field_5982", "yRotO");
   }

   public static void setYRotO(Object entity, float v) {
      setFloat(entity, "field_5982", "yRotO", v);
   }

   public static float xRotO(Object entity) {
      return getFloat(entity, "field_6004", "xRotO");
   }

   public static void setXRotO(Object entity, float v) {
      setFloat(entity, "field_6004", "xRotO", v);
   }

   public static float yHeadRot(Object living) {
      return getFloat(living, "field_6241", "yHeadRot");
   }

   public static void setYHeadRot(Object living, float v) {
      setFloat(living, "field_6241", "yHeadRot", v);
   }

   public static float yHeadRotO(Object living) {
      return getFloat(living, "field_6259", "yHeadRotO");
   }

   public static void setYHeadRotO(Object living, float v) {
      setFloat(living, "field_6259", "yHeadRotO", v);
   }

   public static float xBob(Object player) {
      return getFloat(player, "field_3916", "xBob");
   }

   public static void setXBob(Object player, float v) {
      setFloat(player, "field_3916", "xBob", v);
   }

   public static float xBobO(Object player) {
      return getFloat(player, "field_3914", "xBobO");
   }

   public static void setXBobO(Object player, float v) {
      setFloat(player, "field_3914", "xBobO", v);
   }

   public static float yBob(Object player) {
      return getFloat(player, "field_3932", "yBob");
   }

   public static void setYBob(Object player, float v) {
      setFloat(player, "field_3932", "yBob", v);
   }

   public static float yBobO(Object player) {
      return getFloat(player, "field_3931", "yBobO");
   }

   public static void setYBobO(Object player, float v) {
      setFloat(player, "field_3931", "yBobO", v);
   }

   public static void setEffectDuration(Object instance, int ticks) {
      setInt(instance, "field_5895", "duration", ticks);
   }

   public static void setItemActivationTicks(Object screenEffects, int ticks) {
      setInt(screenEffects, "field_59973", "itemActivationTicks", ticks);
   }

   public static void setMainHandHeight(Object hands, float v) {
      setFloat(hands, "field_4043", "mainHandHeight", v);
   }

   public static void setOMainHandHeight(Object hands, float v) {
      setFloat(hands, "field_4053", "oMainHandHeight", v);
   }

   public static void setOffHandHeight(Object hands, float v) {
      setFloat(hands, "field_4052", "offHandHeight", v);
   }

   public static void setOOffHandHeight(Object hands, float v) {
      setFloat(hands, "field_4051", "oOffHandHeight", v);
   }

   public static <T> T lightTexture(Object light) {
      return (T)get(light, "field_57927", "texture");
   }

   public static void cameraSetRotation(Object camera, float yaw, float pitch) {
      invoke(camera, "method_19325", "setRotation", new Class[]{Float.TYPE, Float.TYPE}, yaw, pitch);
   }

   public static void cameraSetPosition(Object camera, Object vec3) {
      invoke(camera, "method_19322", "setPosition", new Class[]{class_243.class}, vec3);
   }

   public static float cameraMaxZoom(Object camera, float distance) {
      Number value = (Number)invokeResult(camera, "method_19318", "getMaxZoom", new Class[]{Float.TYPE}, distance);
      return value.floatValue();
   }

   public static void cameraMove(Object camera, float forward, float up, float left) {
      invoke(camera, "method_19324", "move", new Class[]{Float.TYPE, Float.TYPE, Float.TYPE}, forward, up, left);
   }

   public static void applyClientCamera(class_4184 camera, class_1297 entity, boolean detached, boolean mirrored, float partialTick, float yaw, float pitch) {
      if (!detached) {
         cameraSetRotation(camera, yaw, pitch);
      } else {
         float eyeHeight = class_3532.method_16439(partialTick, getFloat(camera, "field_18722", "eyeHeightOld"), getFloat(camera, "field_18721", "eyeHeight"));
         cameraSetPosition(camera, entity.method_30950(partialTick).method_1031((double)0.0F, (double)eyeHeight, (double)0.0F));
         if (mirrored) {
            yaw += 180.0F;
            pitch = -pitch;
         }

         cameraSetRotation(camera, yaw, pitch);
         float distance = cameraMaxZoom(camera, 4.0F);
         cameraMove(camera, -distance, 0.0F, 0.0F);
      }
   }

   public static void ensureHasSentCarriedItem(Object gameMode) {
      if (gameMode != null) {
         invoke(gameMode, "method_2911", "ensureHasSentCarriedItem", new Class[0]);
      }
   }

   public static int rightClickDelay(Object minecraft) {
      return getInt(minecraft, "field_1752", "rightClickDelay");
   }

   public static void setRightClickDelay(Object minecraft, int ticks) {
      setInt(minecraft, "field_1752", "rightClickDelay", ticks);
   }

   public static void setJumping(Object living, boolean jumping) {
      setBoolean(living, "field_6282", "jumping", jumping);
   }

   public static class_241 moveVector(Object input) {
      return (class_241)get(input, "field_55868", "moveVector");
   }

   public static void setMoveVector(Object input, class_241 vector) {
      set(input, "field_55868", "moveVector", vector);
   }

   public static class_11246 guiRenderState(Object graphics) {
      return (class_11246)get(graphics, "field_59826", "guiRenderState");
   }

   public static class_8030 scissorPeek(Object graphics) {
      Object stack = get(graphics, "field_44659", "scissorStack");

      try {
         return (class_8030)method(stack.getClass(), "method_70863", "peek", new Class[0]).invoke(stack);
      } catch (ReflectiveOperationException e) {
         throw new IllegalStateException(e);
      }
   }
}
