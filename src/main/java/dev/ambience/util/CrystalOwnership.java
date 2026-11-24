package dev.ambience.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.class_1511;
import net.minecraft.class_2338;

public final class CrystalOwnership {
   private static final int PENDING_TICKS = 6;
   private static final Set<class_2338> ownedBases = new HashSet();
   private static final Set<Integer> ownedEntityIds = new HashSet();
   private static final Map<class_2338, Integer> baseEntityIds = new HashMap();
   private static final Map<class_2338, Integer> pendingTicks = new HashMap();

   private CrystalOwnership() {
   }

   public static void tick() {
      pendingTicks.replaceAll((base, ticks) -> ticks - 1);
      pendingTicks.entrySet().removeIf((entry) -> (Integer)entry.getValue() <= 0);
   }

   public static void markPlaced(class_2338 base) {
      class_2338 key = base.method_10062();
      ownedBases.add(key);
      pendingTicks.put(key, 6);
   }

   public static void bindCrystal(class_1511 crystal) {
      class_2338 base = baseUnder(crystal);
      if (ownedBases.contains(base)) {
         ownedEntityIds.add(crystal.method_5628());
         baseEntityIds.put(base, crystal.method_5628());
         pendingTicks.remove(base);
      }
   }

   public static boolean isOurs(class_1511 crystal) {
      if (crystal == null) {
         return false;
      } else if (ownedEntityIds.contains(crystal.method_5628())) {
         return true;
      } else {
         class_2338 base = baseUnder(crystal);
         return ownedBases.contains(base) || isPending(base) || baseEntityIds.containsKey(base);
      }
   }

   public static boolean ownsBase(class_2338 base) {
      return ownedBases.contains(base.method_10062());
   }

   public static boolean isPending(class_2338 base) {
      Integer ticks = (Integer)pendingTicks.get(base.method_10062());
      return ticks != null && ticks > 0;
   }

   public static void onBroken(class_2338 base) {
      onCrystalGone(base);
   }

   public static void onCrystalGone(class_2338 base) {
      class_2338 key = base.method_10062();
      ownedBases.remove(key);
      pendingTicks.remove(key);
      Integer id = (Integer)baseEntityIds.remove(key);
      if (id != null) {
         ownedEntityIds.remove(id);
      }

   }

   public static void clear() {
      ownedBases.clear();
      ownedEntityIds.clear();
      baseEntityIds.clear();
      pendingTicks.clear();
   }

   public static class_2338 baseUnder(class_1511 crystal) {
      return class_2338.method_49637(crystal.method_23317(), crystal.method_23318() - (double)1.0F, crystal.method_23321());
   }
}
