package dev.ambience.nativebridge;

public final class NativeModules {
   public static final int AS_ENABLED = 1;
   public static final int AS_FORWARD = 2;
   public static final int AS_BACKWARD = 4;
   public static final int AS_LEFT = 8;
   public static final int AS_RIGHT = 16;
   public static final int AS_SPRINT = 32;
   public static final int AS_SHIFT = 64;
   public static final int AS_USING_ITEM = 128;
   public static final int AS_PASSENGER = 256;
   public static final int AS_SWIMMING = 512;
   public static final int AS_BLIND = 1024;
   public static final int AS_HUNGRY = 2048;
   public static final int AS_BLOCKED = 4096;
   public static final int AS_OMNI_OWNED = 8192;
   public static final int ACTION_SPRINT = 1;
   public static final int AC_ENABLED = 1;
   public static final int AC_BLOCKED = 2;
   public static final int AC_BREAK_ENABLED = 4;
   public static final int AC_PLACE_ENABLED = 8;
   public static final int AC_HAS_CRYSTAL = 16;
   public static final int AC_CAN_BREAK = 32;
   public static final int AC_LOOKS_AT_CRYSTAL = 64;
   public static final int AC_PENDING_CRYSTAL = 128;
   public static final int AC_CAN_PLACE = 256;
   public static final int AC_LOOKS_AT_BASE = 512;
   public static final int AC_HAS_PLACE_HIT = 1024;
   public static final int AC_END = 0;
   public static final int AC_WAIT = 1;
   public static final int AC_BREAK = 2;
   public static final int AC_AIM_BREAK = 3;
   public static final int AC_PLACE = 4;
   public static final int AC_AIM_PLACE = 5;
   public static final int ANCHOR_SHIELD_SOON = 6;
   public static final int ANCHOR_FINISH = 7;
   public static final int ANCHOR_LOOK_READY = 0;
   public static final int ANCHOR_AIMED = 1;
   public static final int ANCHOR_RETRY = 0;
   public static final int ANCHOR_ABORT = 1;
   public static final int ANCHOR_SKIP_SHIELD = 2;
   public static final int ANCHOR_CHARGE_WAIT = 0;
   public static final int ANCHOR_CHARGE_READY = 1;
   public static final int ANCHOR_CHARGE_RETRY = 2;
   public static final int ANCHOR_CHARGE_ABORT = 3;
   public static final int ANCHOR_DETONATE_WAIT = 0;
   public static final int ANCHOR_DETONATE_RETRY = 1;
   public static final int ANCHOR_DETONATE_DONE = 2;
   public static final int AT_OFFHAND_TOTEM = 1;
   public static final int AT_MAINHAND_TOTEM = 2;
   public static final int AT_MAINHAND_ENABLED = 4;
   public static final int AT_ABOUT_TO_POP = 8;
   public static final int AT_REFILL_MAINHAND = 16;
   public static final int AT_NEEDS_OFFHAND = 32;
   public static final int AT_CAN_MAINHAND = 64;
   public static final int AT_NONE = 0;
   public static final int AT_MAINHAND = 1;
   public static final int AT_OFFHAND = 2;
   public static final int AT_PHASE_NONE = 0;
   public static final int AT_IDLE_MAINHAND = 1;
   public static final int AT_IDLE_OFFHAND = 2;
   public static final int AT_WAIT = 3;
   public static final int AT_SWAP_MAINHAND = 4;
   public static final int AT_SWAP_OFFHAND = 5;
   public static final int XP_RESET = 0;
   public static final int XP_WAIT = 1;
   public static final int XP_THROW = 2;
   public static final int TB_TARGET_PRESENT = 1;
   public static final int TB_TARGET_NOT_SELF = 2;
   public static final int TB_TARGET_ALIVE = 4;
   public static final int TB_TARGET_NOT_SPECTATOR = 8;
   public static final int TB_TARGET_NOT_ARMOR_STAND = 16;
   public static final int TB_TARGET_ALLOWED_TYPE = 32;
   public static final int TB_TARGET_CRYSTAL_ALLOWED = 64;
   public static final int TB_TARGET_GRIM_RANGE = 128;
   public static final int TB_TARGET_VANILLA_RANGE = 256;
   public static final int TB_CRYSTAL = 1;
   public static final int TB_CRYSTAL_MODE = 2;
   public static final int TB_DAMAGE_TICK = 4;
   public static final int TB_SWORD_COOLDOWN = 8;
   public static final int TB_HOLDING_MELEE = 16;
   public static final int TB_ON_GROUND = 32;
   public static final int TB_FLYING = 64;
   public static final int TB_CAN_CRIT = 128;
   public static final int TB_TPS_SYNC = 256;
   public static final int TB_WAIT = 0;
   public static final int TB_ATTACK = 1;
   public static final int KP_NONE = 0;
   public static final int KP_SWITCH = 1;
   public static final int KP_THROW = 2;
   public static final int KP_RESTORE = 3;
   public static final int FB_NONE = 0;
   public static final int FB_ADD = 1;
   public static final int FB_REFRESH = 2;
   public static final int OMNI_NONE = 0;
   public static final int OMNI_STOP = 1;
   public static final int OMNI_REWRITE = 2;
   public static final int OMNI_APPLY = 3;
   public static final int SS_NONE = 0;
   public static final int SS_HOTBAR = 1;
   public static final int SS_INVENTORY = 2;
   public static final int AM_IDLE = 0;
   public static final int AM_AIM = 1;
   public static final int AM_SMASH = 2;
   public static final int AM_WAIT = 0;
   public static final int AM_AIM_TARGET = 1;
   public static final int AM_STUN = 2;
   public static final int AM_SMASH_HIT = 3;
   public static final int AM_DROP_STUN = 4;
   public static final int AM_TARGET_PRESENT = 1;
   public static final int AM_TARGET_NOT_SELF = 2;
   public static final int AM_TARGET_ALIVE = 4;
   public static final int AM_TARGET_PICKABLE = 8;
   public static final int AM_TARGET_NOT_ARMOR_STAND = 16;
   public static final int AM_TARGET_NOT_SPECTATOR = 32;
   public static final int AM_TARGET_NOT_PASSED = 64;
   public static final int PA_END = 0;
   public static final int PA_WAIT = 1;
   public static final int PA_AIM = 2;
   public static final int PA_ACT = 3;
   public static final int PA_HAS_ITEM = 1;
   public static final int PA_HAS_TARGET = 2;
   public static final int PA_PACKET_LOOK_READY = 4;
   public static final int PA_CURRENT_LOOK_READY = 8;
   public static final int PA_TIMER_READY = 16;
   public static final int AE_IDLE = 0;
   public static final int AE_START = 1;
   public static final int AE_CONTINUE = 2;
   public static final int AE_ABORT = 3;
   public static final int LUNGE_WAIT = 0;
   public static final int LUNGE_SWITCH = 1;
   public static final int LUNGE_JUMP_STAB = 2;
   public static final int LUNGE_STAB = 3;
   public static final int LUNGE_HOLD = 4;
   public static final int LUNGE_FINISH = 5;
   public static final int LUNGE_ABORT = 6;
   public static final int WH_WAIT = 0;
   public static final int WH_AIM = 1;
   public static final int WH_THROW = 2;
   public static final int WH_RESTORE = 3;
   public static final int WH_ABORT = 4;
   public static final int PC_WAIT = 0;
   public static final int PC_CONTINUE = 1;
   public static final int PC_ABORT = 2;
   public static final int MLG_WAIT = 0;
   public static final int MLG_SWAP = 1;
   public static final int MLG_PLACE = 2;

   private NativeModules() {
   }


   private static long positiveEntropy(long entropy) {
      return entropy & Long.MAX_VALUE;
   }

   private static float[] lookAngles(double eyeX, double eyeY, double eyeZ, double pointX, double pointY, double pointZ) {
      double dx = pointX - eyeX;
      double dy = pointY - eyeY;
      double dz = pointZ - eyeZ;
      double horiz = Math.sqrt(dx * dx + dz * dz);
      if (horiz < 1.0E-4 && Math.abs(dy) < 1.0E-4) {
         return null;
      }
      float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
      float pitch = (float) (-Math.toDegrees(Math.atan2(dy, horiz)));
      if (pitch < -90.0F) {
         pitch = -90.0F;
      } else if (pitch > 90.0F) {
         pitch = 90.0F;
      }
      return new float[]{yaw, pitch};
   }


   private static int sprintTicks;

   public static int autoSprintStep(int tick, int delay, int input) {
      if ((input & (AS_BLOCKED | AS_USING_ITEM | AS_PASSENGER | AS_SWIMMING | AS_BLIND | AS_HUNGRY | AS_SHIFT | AS_OMNI_OWNED)) != 0) {
         sprintTicks = 0;
         return 0;
      }
      boolean moving = (input & (AS_FORWARD | AS_BACKWARD | AS_LEFT | AS_RIGHT)) != 0;
      if (!moving) {
         sprintTicks = 0;
         return 0;
      }
      if ((input & AS_SPRINT) != 0) {
         return 0;
      }
      sprintTicks++;
      return sprintTicks >= Math.max(0, delay) ? ACTION_SPRINT : 0;
   }

   public static void resetAutoSprint() {
      sprintTicks = 0;
   }

   public static float omniImpulse(boolean positive, boolean negative) {
      return positive ? 1.0F : (negative ? -1.0F : 0.0F);
   }

   public static float tapImpulse(boolean positive, boolean negative) {
      return positive ? 1.0F : (negative ? -1.0F : 0.0F);
   }

   public static int omniPlan(boolean enabled, boolean valid, boolean paused, boolean ownsRotation, boolean keepStrafe, float forward, float strafe) {
      if (!enabled || !valid || paused || !keepStrafe) {
         return ownsRotation ? OMNI_STOP : OMNI_NONE;
      }
      if (forward == 0.0F && strafe == 0.0F) {
         return ownsRotation ? OMNI_STOP : OMNI_NONE;
      }
      return strafe != 0.0F ? OMNI_APPLY : OMNI_REWRITE;
   }

   public static float omniYaw(float cameraYaw, float forward, float strafe) {
      if (forward == 0.0F && strafe == 0.0F) {
         return cameraYaw;
      }
      return cameraYaw + (float) Math.toDegrees(Math.atan2(strafe, forward));
   }


   public static boolean noSmokeShouldCancel(boolean enabled, String particleId) {
      return enabled && particleId != null && particleId.contains("smoke");
   }

   public static boolean noSmokeActive(boolean enabled) {
      return enabled;
   }

   public static boolean noBlastShouldCancel(boolean enabled, String particleId) {
      if (!enabled || particleId == null) {
         return false;
      }
      return particleId.endsWith(":explosion") || particleId.endsWith(":explosion_emitter") || particleId.endsWith(":sonic_boom");
   }

   public static boolean noBlastActive(boolean enabled) {
      return enabled;
   }

   public static boolean noInvisActive(boolean enabled) {
      return enabled;
   }

   public static boolean antiBlindActive(boolean enabled) {
      return enabled;
   }

   public static boolean antiBlindHides(boolean enabled, String effectId) {
      if (!enabled || effectId == null) {
         return false;
      }
      return effectId.endsWith(":blindness") || effectId.endsWith(":darkness");
   }

   public static int fullbrightAction(boolean enabled, boolean potionMode, boolean hasEffect, int duration) {
      if (!enabled || !potionMode) {
         return FB_NONE;
      }
      if (!hasEffect) {
         return FB_ADD;
      }
      return duration <= 400 ? FB_REFRESH : FB_NONE;
   }

   public static boolean fullbrightGamma(boolean enabled, boolean gammaMode) {
      return enabled && gammaMode;
   }

   public static boolean lowFireEnabled(boolean enabled) {
      return enabled;
   }

   public static float lowFireHeightScale() {
      return 0.5F;
   }

   public static float lowFireYOffset() {
      return -0.25F;
   }

   public static float lowFireUvSlice(float v0, float v1) {
      return v0 + (v1 - v0) * 0.5F;
   }

   public static boolean totemAnimationEnabled(boolean enabled) {
      return enabled;
   }

   public static float totemAnimationScale(float scale) {
      return scale;
   }

   public static int totemAnimationLength(int length) {
      return length;
   }

   public static int viewModelFlags(boolean enabled, boolean noSway, boolean noSwap, boolean oldAnimation) {
      return (enabled ? 1 : 0) | (noSway ? 2 : 0) | (noSwap ? 4 : 0) | (oldAnimation ? 8 : 0);
   }

   public static double[] viewModelTransform(double posX, double posY, double posZ, double scale, boolean rightArm) {
      return new double[]{posX, posY, posZ, 0.0, 0.0, 0.0, scale};
   }

   public static boolean skyboxEnabled(boolean enabled) {
      return enabled;
   }

   public static boolean skyboxHide(boolean enabled, boolean hideSunMoon) {
      return enabled && hideSunMoon;
   }

   private static int argb(int r, int g, int b) {
      return 0xFF000000 | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
   }

   public static int[] skyboxColors(int preset, int customTop, int customHorizon) {
      switch (preset) {
         case 0:
            return new int[]{argb(35, 60, 140), argb(255, 140, 70)};
         case 1:
            return new int[]{argb(25, 12, 60), argb(150, 60, 180)};
         case 2:
            return new int[]{argb(3, 6, 18), argb(12, 25, 55)};
         case 3:
            return new int[]{argb(6, 32, 64), argb(70, 225, 175)};
         case 4:
            return new int[]{argb(250, 150, 200), argb(140, 90, 255)};
         default:
            return new int[]{customTop, customHorizon};
      }
   }

   public static float skyboxStars(float brightness, boolean brightStars, float boost) {
      if (!brightStars) {
         return brightness;
      }
      float out = brightness * boost;
      return out > 1.0F ? 1.0F : out;
   }

   public static boolean xrayVisible(int settings, int traits) {
      boolean air = (traits & 1) != 0;
      boolean ore = (settings & 1) != 0 && (traits & 2) != 0;
      boolean container = (settings & 2) != 0 && (traits & 4) != 0;
      boolean portal = (settings & 4) != 0 && (traits & 8) != 0;
      return air || ore || container || portal;
   }

   public static int storageEspKind(int kind, int enabledKinds, double distanceSq, double maximumSq) {
      if (kind < 0 || kind > 30) {
         return -1;
      }
      if (((enabledKinds >>> kind) & 1) == 0) {
         return -1;
      }
      return distanceSq <= maximumSq ? kind : -1;
   }

   public static boolean blockEspScan(int scanTick, boolean changed, boolean empty) {
      return changed || empty || scanTick % 20 == 0;
   }

   public static boolean blockEspAccept(double distanceSq, double maximumSq, int count, int maximumCount) {
      return distanceSq <= maximumSq && count < maximumCount;
   }

   public static boolean playerEspValid(boolean alive, boolean spectator, boolean self, boolean showSelf, boolean invisible, boolean showInvisible) {
      return alive && !spectator && (!self || showSelf) && (!invisible || showInvisible);
   }

   public static int worldParticlesPlan(double density, int style, long entropy) {
      if (density <= 0.0) {
         return -1;
      }
      int resolvedStyle = style >= 3 ? (int) (positiveEntropy(entropy) % 3) : style;
      int count = (int) Math.round(density);
      if (count < 1) {
         count = 1;
      } else if (count > 16) {
         count = 16;
      }
      return ((resolvedStyle & 255) << 8) | count;
   }

   public static void resetWorldParticles() {
   }


   private static float freelookYaw;
   private static float freelookPitch;
   private static boolean freelookHas;

   public static void freelookCapture(boolean enabled, boolean valid, float yaw, float pitch) {
      if (enabled && valid) {
         freelookYaw = yaw;
         freelookPitch = pitch;
         freelookHas = true;
      }
   }

   public static boolean freelookActive(boolean enabled, boolean freecamActive) {
      return enabled && !freecamActive;
   }

   public static void freelookTurn(double yawDelta, double pitchDelta) {
      freelookYaw += (float) yawDelta;
      float pitch = (float) (freelookPitch + pitchDelta);
      freelookPitch = pitch < -90.0F ? -90.0F : (pitch > 90.0F ? 90.0F : pitch);
      freelookHas = true;
   }

   public static void freelookAbsorb(boolean enabled, float yaw, float pitch) {
      if (enabled) {
         freelookYaw = yaw;
         freelookPitch = pitch;
         freelookHas = true;
      }
   }

   public static float[] freelookAngles() {
      return new float[]{freelookYaw, freelookPitch};
   }

   public static void resetFreelook() {
      freelookYaw = 0.0F;
      freelookPitch = 0.0F;
      freelookHas = false;
   }

   public static double[] freecamDelta(float yaw, float pitch, float forward, float strafe, float vertical, double scale) {
      double yawRad = Math.toRadians(yaw);
      double dx = (forward * -Math.sin(yawRad) + strafe * -Math.cos(yawRad)) * scale;
      double dz = (forward * Math.cos(yawRad) + strafe * -Math.sin(yawRad)) * scale;
      double dy = vertical * scale;
      return new double[]{dx, dy, dz};
   }


   public static long rollDelay(int minimum, int maximum, long entropy) {
      if (maximum <= minimum) {
         return minimum;
      }
      return minimum + (positiveEntropy(entropy) % (maximum - minimum));
   }


   private static double crystalX;
   private static double crystalY;
   private static double crystalZ;
   private static long crystalAt;
   private static boolean crystalHas;

   public static boolean crystalOptimizerAccept(boolean enabled, boolean crystal, boolean alive, boolean validPlayer, boolean attackDamage) {
      return enabled && crystal && alive && validPlayer && attackDamage;
   }

   public static void markCrystalPrediction(double x, double y, double z, long nowMs) {
      crystalX = x;
      crystalY = y;
      crystalZ = z;
      crystalAt = nowMs;
      crystalHas = true;
   }

   public static boolean consumeCrystalPrediction(double x, double y, double z, long nowMs) {
      if (!crystalHas) {
         return false;
      }
      double dx = x - crystalX;
      double dy = y - crystalY;
      double dz = z - crystalZ;
      if (dx * dx + dy * dy + dz * dz > 12.25 || nowMs - crystalAt > 1000L) {
         return false;
      }
      crystalHas = false;
      return true;
   }

   public static void clearCrystalPrediction() {
      crystalHas = false;
   }


   public static boolean jumpResetTick(int hurtTime, int delayTicks, int chance, boolean canReset, long entropy) {
      if (!canReset || hurtTime <= 0 || chance <= 0) {
         return false;
      }
      int clampedDelay = Math.min(9, Math.max(0, delayTicks));
      if (hurtTime != 10 - clampedDelay) {
         return false;
      }
      return positiveEntropy(entropy) % 100L < chance;
   }

   public static void clearJumpReset() {
   }

   public static int jumpResetWaitTicks() {
      return 0;
   }


   public static boolean wtapAccept(boolean enabled, boolean localPlayer, boolean unblocked, boolean validWeapon, boolean validTarget, boolean attackReady) {
      return enabled && localPlayer && unblocked && validWeapon && validTarget && attackReady;
   }

   public static boolean wtapBusy(boolean enabled, long nowMs, long untilMs) {
      return enabled && nowMs < untilMs;
   }

   public static boolean stapApproach(double previousDistance, double distance, double speed, double directionDot, double speedThreshold) {
      return (previousDistance < 0.0 || distance < previousDistance) && speed > speedThreshold && directionDot > -0.5;
   }


   public static int fastXpPlan(boolean hasBottle, boolean useDown, boolean blocked, boolean coolingDown, boolean usingItem, boolean timerReady) {
      if (!hasBottle) {
         return XP_RESET;
      }
      if (blocked || coolingDown || usingItem || !timerReady || !useDown) {
         return XP_WAIT;
      }
      return XP_THROW;
   }


   private static boolean crystalLookedBreak;
   private static boolean crystalLookedPlace;

   public static int autoCrystalPlan(int input) {
      boolean breakEnabled = (input & AC_BREAK_ENABLED) != 0;
      boolean placeEnabled = (input & AC_PLACE_ENABLED) != 0;
      boolean hasCrystal = (input & AC_HAS_CRYSTAL) != 0;
      boolean canBreak = (input & AC_CAN_BREAK) != 0;
      boolean looksAtCrystal = (input & AC_LOOKS_AT_CRYSTAL) != 0;
      boolean pendingCrystal = (input & AC_PENDING_CRYSTAL) != 0;
      boolean liveHit = (input & AC_CAN_PLACE) != 0;
      boolean looksAtBase = (input & AC_LOOKS_AT_BASE) != 0;
      int out = AC_END;
      if (breakEnabled && hasCrystal && canBreak) {
         out = looksAtCrystal && crystalLookedBreak ? AC_BREAK : AC_AIM_BREAK;
      } else if (placeEnabled && liveHit && !(pendingCrystal && !hasCrystal)) {
         out = looksAtBase && crystalLookedPlace ? AC_PLACE : AC_AIM_PLACE;
      }
      crystalLookedBreak = looksAtCrystal;
      crystalLookedPlace = looksAtBase;
      return out;
   }

   public static boolean autoCrystalBusy(boolean enabled, int restoreSlot, boolean heldSilent, int lookWait, int pauseTicks, boolean pendingEnd) {
      return enabled && (restoreSlot != -1 || heldSilent || lookWait > 0 || pauseTicks > 0 || pendingEnd);
   }


   private static int anchorX;
   private static int anchorY;
   private static int anchorZ;
   private static boolean anchorHas;

   public static boolean anchorBusy(boolean enabled, int step) {
      return enabled && step != 0;
   }

   public static int anchorRetry(int step, int attempts, int context) {
      return attempts > 30 ? ANCHOR_ABORT : ANCHOR_RETRY;
   }

   public static int anchorAfterCharge(boolean protect, boolean hasGlowstone, boolean shieldReady, boolean acted, boolean doBreak) {
      if (acted) {
         return ANCHOR_SHIELD_SOON;
      }
      if (protect && hasGlowstone) {
         return 4;
      }
      return 5;
   }

   public static int[] anchorShieldOffsets(double playerDx, double playerDz, boolean diagonal) {
      int[][] cardinals = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
      sortByDot(cardinals, playerDx, playerDz);
      int[][] diags = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
      sortByDot(diags, playerDx, playerDz);
      int total = diagonal ? 8 : 4;
      int[] out = new int[total * 2];
      for (int i = 0; i < 4; i++) {
         out[i * 2] = cardinals[i][0];
         out[i * 2 + 1] = cardinals[i][1];
      }
      if (diagonal) {
         for (int i = 0; i < 4; i++) {
            out[8 + i * 2] = diags[i][0];
            out[8 + i * 2 + 1] = diags[i][1];
         }
      }
      return out;
   }

   private static void sortByDot(int[][] cells, double dx, double dz) {
      for (int i = 1; i < cells.length; i++) {
         int[] key = cells[i];
         double keyDot = key[0] * dx + key[1] * dz;
         int j = i - 1;
         while (j >= 0 && cells[j][0] * dx + cells[j][1] * dz < keyDot) {
            cells[j + 1] = cells[j];
            j--;
         }
         cells[j + 1] = key;
      }
   }

   private static long lastAnchorInteractMs;

   public static int anchorChargeTick(boolean anchorPresent, int charge, boolean interactionSent) {
      if (!anchorPresent) {
         return ANCHOR_CHARGE_ABORT;
      }
      if (charge >= 4) {
         return ANCHOR_CHARGE_READY;
      }
      if (!interactionSent && System.currentTimeMillis() - lastAnchorInteractMs < 100L) {
         return ANCHOR_CHARGE_WAIT;
      }
      lastAnchorInteractMs = System.currentTimeMillis();
      return ANCHOR_CHARGE_RETRY;
   }

   public static void resetAnchorCharge() {
   }

   public static void tagPlacedAnchor(int x, int y, int z) {
      anchorX = x;
      anchorY = y;
      anchorZ = z;
      anchorHas = true;
   }

   public static boolean isAnchorTagValid(int x, int y, int z) {
      return anchorHas && anchorX == x && anchorY == y && anchorZ == z;
   }

   public static void clearAnchorTag() {
      anchorHas = false;
   }

   public static int anchorDetonateTick(boolean anchorPresent, boolean interactionSent) {
      return anchorPresent ? ANCHOR_DETONATE_RETRY : ANCHOR_DETONATE_DONE;
   }


   public static int autoTotemPlan(int input) {
      return AT_NONE;
   }

   public static int autoTotemPhaseAction(int route, int phase) {
      return AT_PHASE_NONE;
   }

   public static boolean autoTotemBlocks(boolean enabled, int phase, long nowMs, long lockUntilMs) {
      return enabled && (phase != 0 || nowMs < lockUntilMs);
   }

   public static int autoTotemMenuSlot(int inventorySlot) {
      if (inventorySlot >= 0 && inventorySlot < 9) {
         return inventorySlot + 36;
      }
      return inventorySlot;
   }

   public static boolean autoTotemHealthLow(float totalHealth, float threshold) {
      return totalHealth <= threshold;
   }


   public static boolean tbotTargetValid(int input) {
      return (input & 127) == 127 && (input & 384) != 0;
   }

   private static long tbotLastAttackMs;
   private static long tbotNextDelayMs;

   public static int tbotStep(int targetId, int input, float attackStrength, int critWait, float perfectChance, int delayMin, int delayMax, long nowMs, long entropy) {
      boolean crystal = (input & TB_CRYSTAL) != 0;
      if (!crystal) {
         if ((input & TB_HOLDING_MELEE) == 0) {
            return TB_WAIT;
         }
         float need = (input & TB_SWORD_COOLDOWN) != 0 ? Math.max(0.0F, Math.min(1.0F, perfectChance / 100.0F)) : 0.5F;
         if (attackStrength < need) {
            return TB_WAIT;
         }
         if ((input & TB_DAMAGE_TICK) != 0) {
            return TB_WAIT;
         }
         if (critWait > 0 && (input & TB_CAN_CRIT) == 0) {
            return TB_WAIT;
         }
      }
      if (nowMs - tbotLastAttackMs < tbotNextDelayMs) {
         return TB_WAIT;
      }
      int lo = Math.max(0, delayMin);
      int hi = Math.max(lo, delayMax);
      tbotNextDelayMs = lo >= hi ? lo : lo + positiveEntropy(entropy) % (hi - lo);
      tbotLastAttackMs = nowMs;
      return TB_ATTACK;
   }

   public static void tbotObserveTick(long nowMs) {
   }

   public static void resetTbot(boolean clearCooldown) {
      tbotLastAttackMs = 0L;
      tbotNextDelayMs = 0L;
   }

   public static int tbotPerfectState() {
      return -1;
   }

   public static int tbotCritWaited() {
      return 0;
   }


   private static int keyPearlPhaseState;
   private static int keyPearlRestoreSlot = -1;
   private static long keyPearlThrowAt;
   private static long keyPearlRestoreAt;

   public static int keyPearlPress(boolean down, boolean canBegin, int pearlSlot, int currentSlot, int delayMin, int delayMax, long nowMs, long entropy) {
      if (!canBegin) {
         keyPearlPhaseState = 0;
         return KP_NONE;
      }
      if (keyPearlPhaseState != 0 || !down || pearlSlot < 0) {
         return KP_NONE;
      }
      keyPearlRestoreSlot = currentSlot;
      int clampedMin = Math.max(0, delayMin);
      int clampedMax = Math.max(clampedMin, delayMax);
      long delay = clampedMax <= clampedMin ? clampedMin : clampedMin + (positiveEntropy(entropy) % (clampedMax - clampedMin));
      keyPearlThrowAt = nowMs + delay * 50L;
      keyPearlPhaseState = 1;
      return KP_SWITCH;
   }

   public static int keyPearlTick(long nowMs) {
      if (keyPearlPhaseState == 1 && nowMs >= keyPearlThrowAt) {
         keyPearlPhaseState = 2;
         return KP_THROW;
      }
      if (keyPearlPhaseState == 2 && nowMs >= keyPearlRestoreAt) {
         return KP_RESTORE;
      }
      return KP_NONE;
   }

   public static void keyPearlThrown(boolean restoreSlot, long nowMs, long entropy) {
      if (keyPearlPhaseState != 2) {
         return;
      }
      if (restoreSlot && keyPearlRestoreSlot >= 0) {
         keyPearlRestoreAt = nowMs + 150L;
      } else {
         keyPearlPhaseState = 0;
      }
   }

   public static void keyPearlRestored() {
      keyPearlPhaseState = 0;
   }

   public static void resetKeyPearl() {
      keyPearlPhaseState = 0;
      keyPearlRestoreSlot = -1;
   }

   public static int keyPearlPhase() {
      return keyPearlPhaseState;
   }

   public static int keyPearlPreviousSlot() {
      return keyPearlRestoreSlot;
   }

   public static float[] solveKeyPearl(double startX, double startY, double startZ, double targetX, double targetY, double targetZ, double inheritX, double inheritY, double inheritZ) {
      double[] solved = projectileSolve(startX, startY, startZ, targetX, targetY, targetZ, inheritX, inheritY, inheritZ, 1.5F, true, 0.03F, 1.0F);
      if (solved == null) {
         return null;
      }
      return new float[]{(float) solved[0], (float) solved[1]};
   }


   public static float aimAssistScore(double eyeX, double eyeY, double eyeZ, double pointX, double pointY, double pointZ, double lookX, double lookY, double lookZ) {
      double dx = pointX - eyeX;
      double dy = pointY - eyeY;
      double dz = pointZ - eyeZ;
      double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
      if (len < 1.0E-6) {
         return 0.0F;
      }
      double lookLen = Math.sqrt(lookX * lookX + lookY * lookY + lookZ * lookZ);
      if (lookLen < 1.0E-9) {
         return 180.0F;
      }
      double dot = (dx * lookX + dy * lookY + dz * lookZ) / (len * lookLen);
      if (dot > 1.0) {
         dot = 1.0;
      } else if (dot < -1.0) {
         dot = -1.0;
      }
      return (float) Math.toDegrees(Math.acos(dot));
   }

   public static boolean aimAssistTarget(int flags, double distanceSq, double maximumDistanceSq, float angle, float fovLimit) {
      return (flags & 255) == 255 && distanceSq <= maximumDistanceSq && angle <= fovLimit;
   }

   public static double[] projectileSolve(double startX, double startY, double startZ, double targetX, double targetY, double targetZ, double inheritX, double inheritY, double inheritZ, float speed, boolean gravity, float gravityAmount, float drag) {
      double dx = targetX - startX;
      double dy = targetY - startY;
      double dz = targetZ - startZ;
      double distXZ = Math.sqrt(dx * dx + dz * dz);
      if (distXZ < 1.0E-4 && Math.abs(dy) < 1.0E-4) {
         return null;
      }
      if (speed < 1.0E-4) {
         return null;
      }
      double baseYaw = Math.toDegrees(Math.atan2(dz, dx)) - 90.0;
      double bestPitch = -Math.toDegrees(Math.atan2(dy, distXZ));
      double bestMiss = Double.MAX_VALUE;
      int bestTicks = 1;
      double lo = -89.0;
      double hi = 89.0;
      for (int pass = 0; pass < 3; pass++) {
         double step = (hi - lo) / 24.0;
         if (step < 0.05) {
            step = 0.05;
         }
         for (double pitch = lo; pitch <= hi + 1.0E-9; pitch += step) {
            double[] sampled = simulatePitch(startX, startY, startZ, dx, dy, dz, inheritX, inheritY, inheritZ, speed, gravity, gravityAmount, drag, baseYaw, pitch);
            if (sampled[0] < bestMiss) {
               bestMiss = sampled[0];
               bestPitch = pitch;
               bestTicks = (int) sampled[1];
            }
         }
         double window = (hi - lo) / 24.0;
         lo = bestPitch - window;
         hi = bestPitch + window;
      }
      return new double[]{baseYaw, bestPitch, bestTicks, bestMiss};
   }

   private static double[] simulatePitch(double startX, double startY, double startZ, double dx, double dy, double dz, double inheritX, double inheritY, double inheritZ, float speed, boolean gravity, float gravityAmount, float drag, double yawDeg, double pitchDeg) {
      double yawRad = Math.toRadians(yawDeg + 90.0);
      double pitchRad = Math.toRadians(pitchDeg);
      double cosPitch = Math.cos(pitchRad);
      double vx = Math.cos(yawRad) * cosPitch * speed + inheritX;
      double vy = -Math.sin(pitchRad) * speed + inheritY;
      double vz = Math.sin(yawRad) * cosPitch * speed + inheritZ;
      double px = startX;
      double py = startY;
      double pz = startZ;
      double best = Double.MAX_VALUE;
      int bestTick = 0;
      for (int tick = 1; tick <= 150; tick++) {
         px += vx;
         py += vy;
         pz += vz;
         double ex = px - (startX + dx);
         double ey = py - (startY + dy);
         double ez = pz - (startZ + dz);
         double miss = ex * ex + ey * ey + ez * ez;
         if (miss < best) {
            best = miss;
            bestTick = tick;
         }
         vx *= drag;
         vy *= drag;
         vz *= drag;
         if (gravity) {
            vy -= gravityAmount;
         }
      }
      return new double[]{Math.sqrt(best), bestTick};
   }

   public static float[] autoMaceSolveAim(double eyeX, double eyeY, double eyeZ, double velocityX, double velocityY, double velocityZ, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      return lookAngles(eyeX, eyeY, eyeZ, (minX + maxX) * 0.5, (minY + maxY) * 0.5, (minZ + maxZ) * 0.5);
   }


   public static int autoWebPlan(int input) {
      return 0;
   }

   public static boolean autoWebTarget(int flags, double distance, double maximumDistance) {
      return (flags & 15) == 15 && distance <= maximumDistance;
   }

   public static int autoWebRestoreSlot(int mode, int configuredSlot, int previousSlot) {
      if (mode == 1) {
         return previousSlot;
      }
      if (mode == 2) {
         return -1;
      }
      return configuredSlot;
   }

   public static int autoDrainPlan(int input) {
      return 0;
   }

   public static boolean autoDrainCandidate(int playerFlags, double playerDistance, double playerRange, double waterDistanceSq, double bestDistanceSq, double reach) {
      return playerDistance <= playerRange && waterDistanceSq <= bestDistanceSq && waterDistanceSq <= reach * reach;
   }


   public static int autoEscPlan(int phase, boolean blocked, boolean stuckInWeb, boolean hasWaterBucket) {
      if (phase == 0) {
         if (blocked) {
            return 0;
         }
         return stuckInWeb && hasWaterBucket ? 1 : 0;
      }
      return blocked ? 3 : 0;
   }

   public static boolean autoEscRetry(int phase, int attempts) {
      return attempts < 3;
   }

   public static boolean autoEscBusy(boolean enabled, int phase) {
      return enabled && phase != 0;
   }


   public static int autoMacePlan(int phase, boolean stunRequested, boolean axeReady, boolean lookReady, int axeHoldTicks) {
      if (phase == 1) {
         if (stunRequested && !axeReady) {
            return AM_WAIT;
         }
         if (!lookReady) {
            return AM_AIM;
         }
         return AM_SMASH;
      }
      if (phase == 2) {
         return lookReady ? AM_SMASH_HIT : AM_AIM;
      }
      return AM_WAIT;
   }

   public static boolean autoMaceTargetValid(int flags, double horizontalDistanceSq, double maximumHorizontalDistance) {
      return (flags & 127) == 127 && horizontalDistanceSq <= maximumHorizontalDistance * maximumHorizontalDistance;
   }

   public static boolean autoMaceRoll(double chance, boolean scaleWithFall, float fallDistance, float minimumFall, float stunMinimumFall, long entropy) {
      if (chance <= 0.0) {
         return false;
      }
      if (fallDistance < (scaleWithFall ? stunMinimumFall : minimumFall)) {
         return false;
      }
      if (chance >= 100.0) {
         return true;
      }
      return positiveEntropy(entropy) % 100L < (long) chance;
   }

   public static int autoMaceSlot(boolean breachCheck, float fallDistance, int densitySlot, int breachSlot, int fallbackSlot) {
      return fallbackSlot;
   }

   public static boolean autoMaceBusy(boolean enabled, int phase, int silentRestoreTicks, int savedSlot, int pendingRestoreSlot) {
      return enabled && (phase != 0 || savedSlot != -1 || pendingRestoreSlot != -1);
   }


   public static boolean breachWantsCrit(boolean enabled, boolean valid, boolean onGround, boolean flying) {
      return enabled && valid && onGround && !flying;
   }

   public static boolean breachIsCrit(float fallDistance, boolean onGround, boolean climbing, boolean inWater, boolean passenger, boolean flying, boolean livingTarget) {
      return livingTarget && fallDistance > 0.0F && !onGround && !climbing && !inWater && !passenger && !flying;
   }

   public static boolean breachAccept(int flags, int maceSlot, int selectedSlot) {
      return (flags & 255) == 255 && maceSlot >= 0 && maceSlot != selectedSlot;
   }

   public static boolean shieldStunAccept(int flags) {
      return (flags & 255) == 255;
   }

   public static int[] shieldStunRoute(int hotbarSlot, int inventorySlot, int emptySlot, int selectedSlot, boolean screenClear, boolean inventoryAllowed) {
      if (hotbarSlot >= 0 && hotbarSlot <= 8 && screenClear) {
         return new int[]{SS_HOTBAR, hotbarSlot, hotbarSlot, selectedSlot};
      }
      if (inventorySlot >= 9 && inventorySlot <= 35 && emptySlot >= 0 && emptySlot <= 8 && inventoryAllowed) {
         return new int[]{SS_INVENTORY, inventorySlot, emptySlot, selectedSlot};
      }
      return new int[]{SS_NONE, 0, 0, 0};
   }


   public static int lungePlan(boolean blocked, boolean spearFound, boolean restorePending, boolean selectedSpear, boolean attackReady, boolean onGround, boolean holdBound) {
      if (blocked || !spearFound) {
         return LUNGE_ABORT;
      }
      if (restorePending) {
         return onGround ? LUNGE_FINISH : LUNGE_HOLD;
      }
      if (!selectedSpear) {
         return LUNGE_SWITCH;
      }
      if (!attackReady) {
         return LUNGE_WAIT;
      }
      long now = System.currentTimeMillis();
      if (now - lastLungeStabMs < 600L) {
         return LUNGE_WAIT;
      }
      lastLungeStabMs = now;
      return onGround ? LUNGE_JUMP_STAB : LUNGE_STAB;
   }

   private static long lastLungeStabMs;

   public static int windHopPlan(int phase, boolean blocked, boolean windFound, boolean holdingWind) {
      if (blocked || !windFound) {
         return WH_ABORT;
      }
      if (phase == 1) {
         return WH_AIM;
      }
      if (phase == 2) {
         return holdingWind ? WH_THROW : WH_WAIT;
      }
      if (phase == 3) {
         return WH_RESTORE;
      }
      return WH_WAIT;
   }

   public static int pearlCatchPlan(int phase, boolean blocked, boolean timedOut, boolean aimReady, boolean timerReady, boolean itemReady, int waitTicks) {
      if (blocked || timedOut || !itemReady) {
         return PC_ABORT;
      }
      if (!aimReady || !timerReady) {
         return PC_WAIT;
      }
      return PC_CONTINUE;
   }

   public static int autoMlgPlan(double ticks, double distance, double speed, double placeDistance, double predictedDistance, int swapTicks, boolean holdingBucket) {
      return MLG_WAIT;
   }

   public static boolean autoMlgBusy(boolean enabled, int savedSlot, boolean used, boolean drainPending, int swapTicks) {
      return enabled && (used || drainPending);
   }


   public static int clickGuiFrame(int width, int height, float delta, int mouseX, int mouseY, int clickButton, String[] names, String[] descriptions, int[] categories, boolean[] enabled, String sessionName, String searchQuery) {
      return 0;
   }

   public static void clickGuiOpen() {
   }

   public static boolean clickGuiAvailable() {
      return false;
   }

   public static void clickGuiSync(String model) {
   }

   public static int[] clickGuiColor() {
      return null;
   }

   public static float[] clickGuiCurve() {
      return null;
   }

   public static void clickGuiHud(int width, int height, String sessionName) {
   }
}
