package dev.ambience.util;

import dev.ambience.util.traits.Util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.class_1268;
import net.minecraft.class_1799;
import net.minecraft.class_2248;
import net.minecraft.class_239;
import net.minecraft.class_2680;
import net.minecraft.class_2960;
import net.minecraft.class_3965;
import net.minecraft.class_7923;
import net.minecraft.class_239.class_240;

public final class BlockLists implements Util {
   public static final int SUGGEST_LIMIT = 6;

   private BlockLists() {
   }

   public static class_2248 candidate() {
      class_2248 looking = lookingBlock();
      return looking != null ? looking : heldBlock();
   }

   public static class_2248 lookingBlock() {
      if (mc.field_1687 != null && mc.field_1765 != null) {
         class_239 hit = mc.field_1765;
         if (hit instanceof class_3965) {
            class_3965 blockHit = (class_3965)hit;
            if (blockHit.method_17783() == class_240.field_1332) {
               class_2680 state = mc.field_1687.method_8320(blockHit.method_17777());
               if (state.method_26215()) {
                  return null;
               }

               return state.method_26204();
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static class_2248 heldBlock() {
      if (mc.field_1724 == null) {
         return null;
      } else {
         for(class_1268 hand : class_1268.values()) {
            class_2248 block = class_2248.method_9503(mc.field_1724.method_5998(hand).method_7909());
            if (!block.method_9564().method_26215()) {
               return block;
            }
         }

         return null;
      }
   }

   public static Set<class_2248> parse(String raw) {
      Set<class_2248> out = new LinkedHashSet();
      if (raw != null && !raw.isBlank()) {
         for(String token : raw.split("[,;\\n]")) {
            class_2248 block = resolve(token);
            if (block != null) {
               out.add(block);
            }
         }

         return out;
      } else {
         return out;
      }
   }

   public static String format(Collection<class_2248> blocks) {
      StringBuilder out = new StringBuilder();

      for(class_2248 block : blocks) {
         if (out.length() > 0) {
            out.append(", ");
         }

         out.append(id(block));
      }

      return out.toString();
   }

   public static class_2248 resolve(String token) {
      if (token == null) {
         return null;
      } else {
         String raw = token.trim().toLowerCase(Locale.ROOT).replace(' ', '_');
         if (raw.isEmpty()) {
            return null;
         } else {
            class_2960 identifier = class_2960.method_12829(raw);
            if (identifier == null) {
               return null;
            } else {
               class_2248 block = (class_2248)class_7923.field_41175.method_17966(identifier).orElse(null);
               return block != null && !block.method_9564().method_26215() ? block : null;
            }
         }
      }
   }

   public static List<class_2248> suggest(String query, int limit) {
      List<class_2248> out = new ArrayList();
      if (query != null && !query.isBlank() && limit > 0) {
         String needle = query.trim().toLowerCase(Locale.ROOT).replace(' ', '_');
         String prettyNeedle = query.trim().toLowerCase(Locale.ROOT);
         List<class_2248> starts = new ArrayList();
         List<class_2248> contains = new ArrayList();

         for(class_2248 block : class_7923.field_41175) {
            if (!block.method_9564().method_26215()) {
               String path = id(block);
               String pretty = displayName(block).toLowerCase(Locale.ROOT);
               if (!path.equals(needle) && !pretty.equals(prettyNeedle)) {
                  if (!path.startsWith(needle) && !pretty.startsWith(prettyNeedle)) {
                     if (path.contains(needle) || pretty.contains(prettyNeedle)) {
                        contains.add(block);
                     }
                  } else {
                     starts.add(block);
                  }
               } else {
                  out.add(block);
                  if (out.size() >= limit) {
                     return out;
                  }
               }
            }
         }

         for(class_2248 block : starts) {
            if (out.size() >= limit) {
               break;
            }

            out.add(block);
         }

         for(class_2248 block : contains) {
            if (out.size() >= limit) {
               break;
            }

            out.add(block);
         }

         return out;
      } else {
         return out;
      }
   }

   public static String displayName(class_2248 block) {
      return block.method_9518().getString();
   }

   public static String id(class_2248 block) {
      class_2960 identifier = class_7923.field_41175.method_10221(block);
      return "minecraft".equals(identifier.method_12836()) ? identifier.method_12832() : identifier.toString();
   }

   public static class_1799 icon(class_2248 block) {
      class_1799 stack = new class_1799(block.method_8389());
      return stack.method_7960() ? class_1799.field_8037 : stack;
   }

   public static int suggestLimit() {
      return 6;
   }
}
