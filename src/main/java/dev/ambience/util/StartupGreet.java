package dev.ambience.util;

import dev.ambience.event.impl.entity.player.TickEvent;
import dev.ambience.event.system.Subscribe;
import dev.ambience.features.Feature;
import dev.ambience.features.commands.Command;
import dev.ambience.util.traits.Util;

public final class StartupGreet implements Util {
   private static volatile boolean done;
   private static volatile StartupGreet instance;

   private StartupGreet() {
   }

   public static void arm() {
      if (instance == null) {
         instance = new StartupGreet();
         EVENT_BUS.register(instance);
      }
   }

   @Subscribe
   public void onTick(TickEvent event) {
      if (!done && !Feature.nullCheck()) {
         done = true;
         for (String line : FogmaArt.LINES) {
            Command.sendMessage(line);
         }
         EVENT_BUS.unregister(instance);
      }
   }
}
