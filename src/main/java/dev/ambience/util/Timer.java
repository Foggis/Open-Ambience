package dev.ambience.util;

public class Timer {
   private long last = System.currentTimeMillis();

   public boolean passed(long ms) {
      return System.currentTimeMillis() - this.last >= ms;
   }

   public void reset() {
      this.last = System.currentTimeMillis();
   }

   public long elapsed() {
      return System.currentTimeMillis() - this.last;
   }
}
