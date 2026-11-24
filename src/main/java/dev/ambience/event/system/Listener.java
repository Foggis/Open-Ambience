package dev.ambience.event.system;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.function.Consumer;

public record Listener(Object host, int priority, Consumer<Object> consumer) {
   public static Listener of(Object host, int priority, Method method) {
      return new Listener(host, priority, buildLambdaMetafactory(host, method));
   }

   public void invoke(Object event) {
      this.consumer.accept(event);
   }

   public Object getHost() {
      return this.host;
   }

   private static Consumer<Object> buildLambdaMetafactory(Object host, Method method) {
      if (host.getClass().getClassLoader() != Listener.class.getClassLoader()) {
         return bindInvoke(host, method);
      } else {
         try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(host.getClass(), MethodHandles.lookup());
            CallSite site = LambdaMetafactory.metafactory(lookup, "accept", MethodType.methodType(Consumer.class, host.getClass()), MethodType.methodType(Void.TYPE, Object.class), lookup.unreflect(method), MethodType.methodType(Void.TYPE, method.getParameterTypes()[0]));
            MethodHandle target = site.getTarget();
            return (Consumer<Object>) target.invoke(host);
         } catch (Throwable var5) {
            return bindInvoke(host, method);
         }
      }
   }

   private static Consumer<Object> bindInvoke(Object host, Method method) {
      method.setAccessible(true);
      return (event) -> {
         try {
            method.invoke(host, event);
         } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to invoke %s".formatted(method.getName()), e);
         }
      };
   }
}
