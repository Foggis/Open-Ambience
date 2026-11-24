package dev.ambience.manager;

import dev.ambience.Ambience;
import dev.ambience.event.impl.entity.player.PostPositionTickEvent;
import dev.ambience.event.impl.entity.player.PreTickEvent;
import dev.ambience.event.impl.entity.player.TickEvent;
import dev.ambience.event.impl.input.KeyInputEvent;
import dev.ambience.event.impl.input.MouseInputEvent;
import dev.ambience.event.system.Subscribe;
import dev.ambience.features.Feature;
import dev.ambience.hooks.OmniSprintHook;
import dev.ambience.util.GrimMovementGuard;
import dev.ambience.util.ModuleDebug;

public class EventManager extends Feature {
   public void init() {
      EVENT_BUS.register(this);
   }

   public void onUnload() {
      EVENT_BUS.unregister(this);
   }

   @Subscribe
   public void onPreTick(PreTickEvent event) {
      if (!nullCheck()) {
         GrimMovementGuard.beginPreTick();
         OmniSprintHook.apply(mc.field_1724);
         Ambience.moduleManager.onPreTick();
         ModuleDebug.onPreTick();
      }
   }

   @Subscribe
   public void onTick(TickEvent event) {
      if (!nullCheck()) {
         Ambience.moduleManager.onTick();
      }
   }

   @Subscribe
   public void onPostPositionTick(PostPositionTickEvent event) {
      if (!nullCheck()) {
         Ambience.moduleManager.onPostPositionTick();
      }
   }

   @Subscribe
   public void onKeyInput(KeyInputEvent event) {
      if (event.getAction() == 1) {
         Ambience.moduleManager.onKeyPressed(event.getKey());
      } else if (event.getAction() == 0) {
         Ambience.moduleManager.onKeyReleased(event.getKey());
      }

   }

   @Subscribe
   public void onMouseInput(MouseInputEvent event) {
      if (event.getAction() == 1) {
         Ambience.moduleManager.onMousePressed(event.getButton());
      } else if (event.getAction() == 0) {
         Ambience.moduleManager.onMouseReleased(event.getButton());
      }

   }
}
