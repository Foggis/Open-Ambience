package dev.ambience.features.modules.combat;

import dev.ambience.features.modules.Module;
import dev.ambience.features.modules.movement.PearlCatchModule;
import dev.ambience.features.modules.movement.WindHopModule;
import dev.ambience.features.settings.Setting;
import dev.ambience.hooks.AutoCrystalHook;
import dev.ambience.inject.Access;
import dev.ambience.nativebridge.NativeModules;
import dev.ambience.util.CrystalOwnership;
import dev.ambience.util.GrimMovementGuard;
import dev.ambience.util.RotationHelper;
import dev.ambience.util.SilentRotation;
import dev.ambience.util.Timer;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_239.class_240;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class AutoCrystalModule extends Module {
   private static AutoCrystalModule INSTANCE;
   private static boolean moduleUse;
   private static final double CRYSTAL_REACH = 2.88;
   private static final double BLOCK_REACH_PAD = 0.03;
   private final Setting<Vector2f> delay = this.vec2f("Delay", 0.0F, 0.0F).setPage("General");
   private final Setting<Boolean> headBob = this.bool("Head Bob", false).setPage("General");
   private final Setting<Boolean> noHeadBob = this.bool("NoHeadBob", false).setPage("General");
   private final Setting<Boolean> inAir = this.bool("In Air", false).setPage("General");
   private final Setting<Boolean> damageTick = this.bool("Damage Tick", false).setPage("General");
   private final Setting<Boolean> pauseOnKill = this.bool("Pause On Kill", false).setPage("General");
   private final Setting<Boolean> switchMode = this.bool("Switch", false).setPage("General");
   private final Setting<Boolean> holdMode = this.bool("Hold Mode", false).setPage("General");
   private final Setting<Boolean> place = this.bool("Place", true).setPage("General");
   private final Setting<Boolean> breakCrystals = this.bool("Break", true).setPage("General");
   private final Setting<Double> range = this.num("Range", (double)4.5F, (double)1.0F, (double)6.0F).setPage("General");
   private final Timer placeTimer = new Timer();
   private final Timer breakTimer = new Timer();
   private long nextPlaceDelayMs;
   private long nextBreakDelayMs;
   private int pauseTicks;
   private float aimYaw;
   private float aimPitch;
   private int lookWait;
   private boolean pendingSilentEnd;
   private boolean heldSilent;
   private boolean pausedForItemUse;
   private boolean deferredRestoreAfterPop;
   private int slotSyncTicks;
   private int restoreSlot = -1;

   public AutoCrystalModule() {
      super("AutoCrystal", "Places and breaks crystals on crosshair obsidian/bedrock", Module.Category.COMBAT);
      this.switchMode.setVisibility((value) -> false);
      INSTANCE = this;
      AutoCrystalHook.bind(() -> {
         AutoCrystalModule module = INSTANCE;
         return !moduleUse && module != null && module.ownsHeldUse();
      });
   }

   public static AutoCrystalModule getInstance() {
      return INSTANCE;
   }

   public static boolean isEnabledInstance() {
      return INSTANCE != null && INSTANCE.isEnabled();
   }

   public static boolean isBusy() {
      AutoCrystalModule module = INSTANCE;
      return NativeModules.autoCrystalBusy(module != null && module.isEnabled(), module == null ? -1 : module.restoreSlot, module != null && module.heldSilent, module == null ? 0 : module.lookWait, module == null ? 0 : module.pauseTicks, module != null && module.pendingSilentEnd);
   }

   public String getDebugState() {
      return this.lookWait <= 0 && !this.pendingSilentEnd && this.pauseTicks <= 0 ? null : "lookWait=" + this.lookWait + " pendingEnd=" + this.pendingSilentEnd + " pause=" + this.pauseTicks;
   }

   public void onEnable() {
      this.pausedForItemUse = false;
      this.deferredRestoreAfterPop = false;
      this.nextPlaceDelayMs = this.rollDelayMs();
      this.nextBreakDelayMs = this.rollDelayMs();
   }

   public void onDisable() {
      SilentRotation.end();
      this.pauseTicks = 0;
      this.lookWait = 0;
      this.pendingSilentEnd = false;
      this.heldSilent = false;
      this.pausedForItemUse = false;
      this.deferredRestoreAfterPop = false;
      this.slotSyncTicks = 0;
      this.restoreHand();
      CrystalOwnership.clear();
   }

   public void onPreTick() {
      if (mc.field_1724 != null && mc.field_1687 != null && mc.field_1761 != null) {
         if (this.pendingSilentEnd) {
            this.pendingSilentEnd = false;
            if (this.heldSilent && !AutoMaceModule.isBusy() && SilentRotation.ownedBy(SilentRotation.Owner.COMBAT)) {
               SilentRotation.end();
            }

            this.heldSilent = SilentRotation.ownedBy(SilentRotation.Owner.COMBAT);
            if (!this.heldSilent) {
               return;
            }
         }

         if (!mc.field_1724.method_6047().method_31574(class_1802.field_8301)) {
            this.pausedForItemUse = false;
            this.queueSilentEnd();
         } else {
            boolean holdingCrystalTrigger = (Boolean)this.holdMode.getValue() && this.holdTriggerDown();
            if ((Boolean)this.holdMode.getValue() && !holdingCrystalTrigger) {
               this.pausedForItemUse = false;
               this.queueSilentEnd();
            } else {
               class_239 heldCursorHit = holdingCrystalTrigger ? this.cameraHit() : null;
               if (holdingCrystalTrigger && !this.cursorTargetsCrystalBase(heldCursorHit)) {
                  this.pausedForItemUse = false;
                  this.queueSilentEnd();
               } else {
                  if (holdingCrystalTrigger && mc.field_1690 != null) {
                     mc.field_1690.field_1904.method_23481(false);
                  }

                  if (holdingCrystalTrigger && mc.field_1724.method_6115()) {
                     mc.field_1724.method_6021();
                  }

                  if (!holdingCrystalTrigger && mc.field_1724.method_6115()) {
                     this.pausedForItemUse = true;
                  } else if (holdingCrystalTrigger || !this.pausedForItemUse || mc.field_1690 == null || !mc.field_1690.field_1904.method_1434()) {
                     if (!this.pausedForItemUse && !GrimMovementGuard.useItemSinceFlying()) {
                        if (true) {
                           if (this.deferredRestoreAfterPop) {
                              if (!GrimMovementGuard.actionThisTick()) {
                                 this.deferredRestoreAfterPop = false;
                                 this.restoreHand();
                              }
                           } else if (!AnchorMacroModule.blocksInteraction()) {
                              if (!AutoMaceModule.isBusy() && !AutoEscModule.isBusy() && !AutoWebModule.isBusy() && !WindHopModule.isBusy() && !PearlCatchModule.isBusy() && !GrimMovementGuard.actionThisTick()) {
                                 if (this.pauseTicks <= 0) {
                                    if ((Boolean)this.inAir.getValue() && mc.field_1724.method_24828()) {
                                       this.queueSilentEnd();
                                    } else if ((Boolean)this.damageTick.getValue() && mc.field_1724.field_6235 <= 0) {
                                       this.queueSilentEnd();
                                    } else {
                                       if (this.lookWait > 0) {
                                          this.lookWait = 0;
                                       }

                                       double blockReach = (Double)this.range.getValue() + 0.03;
                                       class_239 cursorHit = holdingCrystalTrigger ? heldCursorHit : this.cameraHit();
                                       if (!this.cursorTargetsCrystalBase(cursorHit)) {
                                          this.queueSilentEnd();
                                       } else {
                                          Target cameraTarget = this.resolveTarget(cursorHit);
                                          if (cameraTarget == null) {
                                             this.queueSilentAimEnd();
                                          } else {
                                             class_1511 onBase = cameraTarget.crystal != null ? cameraTarget.crystal : this.findCrystalOnBase(cameraTarget.base);
                                             Target breakTarget = onBase == null ? null : new Target(cameraTarget.base, onBase, cameraTarget.placeHit, true);
                                             boolean pendingCrystal = CrystalOwnership.isPending(cameraTarget.base);
                                             boolean canBreak = onBase != null && GrimMovementGuard.canInteractEntity(mc.field_1724, onBase, 2.88);
                                             boolean looksAtCrystal = onBase != null && GrimMovementGuard.currentHitsEntity(mc.field_1724, onBase, 2.88);
                                             class_3965 liveHit = null;
                                             if (GrimMovementGuard.canInteractBlock(mc.field_1724, cameraTarget.base, blockReach)) {
                                                liveHit = this.livePlaceHit(cameraTarget);
                                             }

                                             boolean looksAtBase = GrimMovementGuard.currentHitsBlock(mc.field_1724, cameraTarget.base, blockReach);
                                             int planInput = 1;
                                             if ((Boolean)this.breakCrystals.getValue()) {
                                                planInput |= 4;
                                             }

                                             if ((Boolean)this.place.getValue()) {
                                                planInput |= 8;
                                             }

                                             if (onBase != null) {
                                                planInput |= 16;
                                             }

                                             if (canBreak) {
                                                planInput |= 32;
                                             }

                                             if (looksAtCrystal) {
                                                planInput |= 64;
                                             }

                                             if (pendingCrystal) {
                                                planInput |= 128;
                                             }

                                             if (liveHit != null) {
                                                planInput |= 256;
                                             }

                                             if (looksAtBase) {
                                                planInput |= 512;
                                             }

                                             if (cameraTarget.placeHit != null) {
                                                planInput |= 1024;
                                             }

                                             switch (NativeModules.autoCrystalPlan(planInput)) {
                                                case 0:
                                                   this.queueSilentAimEnd();
                                                case 1:
                                                default:
                                                   break;
                                                case 2:
                                                   this.handleBreakTarget(breakTarget);
                                                   break;
                                                case 3:
                                                   if (!(Boolean)this.noHeadBob.getValue()) {
                                                      this.aimAt(breakTarget);
                                                   }
                                                   break;
                                                case 4:
                                                   this.handlePlaceTarget(new Target(cameraTarget.base, (class_1511)null, liveHit, false));
                                                   break;
                                                case 5:
                                                   if (!(Boolean)this.noHeadBob.getValue()) {
                                                      this.aimAt(cameraTarget);
                                                   }
                                             }

                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     } else {
                        this.pausedForItemUse = false;
                     }
                  }
               }
            }
         }
      }
   }

   private boolean holdTriggerDown() {
      if (mc.field_1690 != null && mc.method_22683() != null && mc.field_1755 == null) {
         return GLFW.glfwGetMouseButton(mc.method_22683().method_4490(), 1) == 1 || mc.field_1690.field_1904.method_1434();
      } else {
         return false;
      }
   }

   private boolean ownsHeldUse() {
      return this.isEnabled() && (Boolean)this.holdMode.getValue() && mc.field_1724 != null && mc.field_1687 != null && mc.field_1755 == null && mc.field_1724.method_6047().method_31574(class_1802.field_8301) && this.holdTriggerDown() ? this.cursorTargetsCrystalBase(this.cameraHit()) : false;
   }

   public void onTick() {
      if (mc.field_1724 != null && mc.field_1687 != null) {
         CrystalOwnership.tick();
         this.syncPendingCrystals();
         if (this.pauseTicks > 0) {
            --this.pauseTicks;
         }

         if (this.slotSyncTicks > 0) {
            --this.slotSyncTicks;
         }

      }
   }

   public void onPostPositionTick() {
   }

   private void queueSilentEnd() {
      this.lookWait = 0;
      this.restoreHand();
      this.queueSilentAimEnd();
   }

   private void queueSilentAimEnd() {
      this.lookWait = 0;
      if (this.heldSilent) {
         if (!AnchorMacroModule.blocksInteraction() && !AutoMaceModule.isBusy() && !WindHopModule.isBusy() && !PearlCatchModule.isBusy()) {
            if (SilentRotation.ownedBy(SilentRotation.Owner.COMBAT)) {
               this.pendingSilentEnd = true;
            }

         }
      }
   }

   private boolean cursorTargetsCrystalBase(class_239 hit) {
      if (hit instanceof class_3965 blockHit) {
         if (blockHit.method_17783() == class_240.field_1332) {
            return isBaseBlock(mc.field_1687.method_8320(blockHit.method_17777())) && this.inReach(class_243.method_24953(blockHit.method_17777()), false);
         }
      }

      if (hit instanceof class_3966 entityHit) {
         class_1297 var4 = entityHit.method_17782();
         if (var4 instanceof class_1511 crystal) {
            class_2338 base = CrystalOwnership.baseUnder(crystal);
            return isBaseBlock(mc.field_1687.method_8320(base)) && this.inReach(crystal.method_73189(), true);
         }
      }

      return false;
   }

   private void handlePlaceTarget(Target target) {
      if ((Boolean)this.place.getValue() && this.placeTimer.passed(this.nextPlaceDelayMs)) {
         if (!mc.field_1724.method_6128()) {
            class_3965 placeHit = target.placeHit;
            if (placeHit != null && placeHit.method_17783() == class_240.field_1332) {
               float yaw = mc.field_1724.method_36454();
               float pitch = mc.field_1724.method_36455();
               if (this.rayHitsBase(placeHit, yaw, pitch)) {
                  if (GrimMovementGuard.canInteractBlock(mc.field_1724, placeHit.method_17777(), (Double)this.range.getValue() + 0.03)) {
                     if (this.tryPlace(target)) {
                        this.placeTimer.reset();
                        this.nextPlaceDelayMs = this.rollDelayMs();
                     }

                  }
               }
            }
         }
      }
   }

   private void handleBreakTarget(Target target) {
      if ((Boolean)this.breakCrystals.getValue() && this.breakTimer.passed(this.nextBreakDelayMs)) {
         class_1511 crystal = target.crystal;
         if (crystal != null && crystal.method_5805()) {
            boolean looking = GrimMovementGuard.canInteractEntity(mc.field_1724, crystal, 2.88);
            if (looking) {
               if (!(Boolean)this.switchMode.getValue() || this.ensureCrystalSelected()) {
                  if (this.tryBreak(target)) {
                     this.breakTimer.reset();
                     this.nextBreakDelayMs = this.rollDelayMs();
                  }

               }
            }
         }
      }
   }

   private long rollDelayMs() {
      Vector2f d = this.delay.getValue();
      int tick = mc.field_1724 == null ? 0 : mc.field_1724.field_6012;
      return NativeModules.rollDelay((int)Math.min(d.x, d.y), (int)Math.max(d.x, d.y), System.nanoTime() ^ (long)this.pauseTicks << 32 ^ (long)tick);
   }

   private class_239 packetHit() {
      return RotationHelper.pick(mc.field_1687, mc.field_1724, mc.field_1724.method_36455(), mc.field_1724.method_36454(), mc.field_1724.method_55754(), mc.field_1724.method_55755());
   }

   private class_239 cameraHit() {
      return !SilentRotation.isActive() ? this.packetHit() : RotationHelper.pick(mc.field_1687, mc.field_1724, SilentRotation.getClientPitch(), SilentRotation.getClientYaw(), mc.field_1724.method_55754(), mc.field_1724.method_55755());
   }

   private class_3965 livePlaceHit(Target target) {
      if (target != null && target.base != null) {
         class_3965 hit = RotationHelper.raycastBlock(mc.field_1687, mc.field_1724, mc.field_1724.method_33571(), mc.field_1724.method_36455(), mc.field_1724.method_36454(), (Double)this.range.getValue() + 0.03);
         return hit != null && hit.method_17783() == class_240.field_1332 && hit.method_17777().equals(target.base) ? hit : null;
      } else {
         return null;
      }
   }

   private Target resolveTarget(class_239 hit) {
      if (hit == null) {
         return null;
      } else {
         if (hit instanceof class_3966) {
            class_3966 entityHit = (class_3966)hit;
            class_1297 var4 = entityHit.method_17782();
            if (var4 instanceof class_1511) {
               class_1511 crystal = (class_1511)var4;
               if (!this.inReach(crystal.method_73189(), true)) {
                  return null;
               }

               return new Target(CrystalOwnership.baseUnder(crystal), crystal, (class_3965)null, true);
            }
         }

         if (hit instanceof class_3965) {
            class_3965 blockHit = (class_3965)hit;
            if (blockHit.method_17783() == class_240.field_1332) {
               class_2338 pos = blockHit.method_17777();
               class_2680 state = mc.field_1687.method_8320(pos);
               if (!isBaseBlock(state)) {
                  return null;
               }

               if (!this.inReach(class_243.method_24953(pos), false)) {
                  return null;
               }

               class_1511 crystal = this.findCrystalOnBase(pos);
               if (crystal != null) {
                  return new Target(pos, crystal, blockHit, true);
               }

               return new Target(pos, (class_1511)null, blockHit, false);
            }
         }

         return null;
      }
   }

   private class_243 aimPoint(Target target) {
      if (target.crystal != null) {
         class_238 box = RotationHelper.grimCrystalBox(target.crystal);
         return new class_243(box.method_1005().field_1352, box.field_1322 + 0.05, box.method_1005().field_1350);
      } else {
         return target.placeHit != null ? target.placeHit.method_17784() : class_243.method_24953(target.base);
      }
   }

   private boolean aimAt(Target target) {
      class_243 point = this.aimPoint(target);
      if (point == null) {
         return false;
      } else {
         this.snapAim(point);
         this.applyLook(this.aimYaw, this.aimPitch);
         this.lookWait = 1;
         return true;
      }
   }

   private void snapAim(class_243 point) {
      float[] angles = GrimMovementGuard.anglesTo(mc.field_1724, point);
      if (angles != null) {
         this.aimYaw = angles[0];
         this.aimPitch = angles[1];
      }
   }

   private void applyLook(float yaw, float pitch) {
      GrimMovementGuard.applyMacroLook(mc.field_1724, yaw, pitch, true);
      this.heldSilent = SilentRotation.ownedBy(SilentRotation.Owner.COMBAT);
   }

   private class_1511 findCrystalOnBase(class_2338 base) {
      class_238 box = (new class_238(base)).method_1009((double)1.0F, (double)2.0F, (double)1.0F);
      class_1511 found = null;

      for(class_1297 entity : mc.field_1687.method_8335(mc.field_1724, box)) {
         if (entity instanceof class_1511 crystal) {
            if (found == null || mc.field_1724.method_5858(crystal) < mc.field_1724.method_5858(found)) {
               found = crystal;
            }
         }
      }

      return found;
   }

   private class_1511 findOurCrystalOnBase(class_2338 base) {
      class_1511 crystal = this.findCrystalOnBase(base);
      if (crystal == null) {
         return null;
      } else {
         return !CrystalOwnership.isOurs(crystal) && !CrystalOwnership.isPending(base) ? null : crystal;
      }
   }

   private boolean tryBreak(Target target) {
      class_1511 crystal = target.crystal;
      if (crystal != null && crystal.method_5805()) {
         class_243 eyes = mc.field_1724.method_33571();
         class_238 box = RotationHelper.grimCrystalBox(crystal);
         if (!box.method_1006(eyes) && box.method_49271(eyes) > 8.2944) {
            return false;
         } else {
            if ((Boolean)this.headBob.getValue()) {
               class_2338 base = target.base;
               if (mc.field_1724.method_23318() + (double)mc.field_1724.method_5751() < (double)base.method_10264() + (double)0.5F && box.method_49271(eyes) > 11.424399999999999) {
                  return false;
               }
            }

            if (!GrimMovementGuard.canInteractEntity(mc.field_1724, crystal, 2.88)) {
               return false;
            } else {
               GrimMovementGuard.markAction();
               mc.field_1761.method_2918(mc.field_1724, crystal);
               mc.field_1724.method_6104(class_1268.field_5808);
               CrystalOwnership.onBroken(target.base);
               return true;
            }
         }
      } else {
         return false;
      }
   }

   private boolean tryPlace(Target target) {
      if (!isBaseBlock(mc.field_1687.method_8320(target.base))) {
         return false;
      } else if (!CrystalOwnership.isPending(target.base) && this.findCrystalOnBase(target.base) == null) {
         if (this.findCrystalOnBase(target.base) != null) {
            return false;
         } else if (!mc.field_1687.method_8320(target.base.method_10084()).method_26215()) {
            return false;
         } else {
            class_3965 liveHit = RotationHelper.raycastBlock(mc.field_1687, mc.field_1724, mc.field_1724.method_33571(), mc.field_1724.method_36455(), mc.field_1724.method_36454(), (Double)this.range.getValue() + 0.03);
            if (liveHit != null && liveHit.method_17783() == class_240.field_1332 && liveHit.method_17777().equals(target.base)) {
               if (!GrimMovementGuard.canInteractBlock(mc.field_1724, target.base, (Double)this.range.getValue() + 0.03)) {
                  return false;
               } else if (!this.ensureCrystalSelected()) {
                  return false;
               } else {
                  GrimMovementGuard.markAction();
                  moduleUse = true;

                  boolean placedAction;
                  try {
                     placedAction = mc.field_1761.method_2896(mc.field_1724, class_1268.field_5808, liveHit).method_23665();
                  } finally {
                     moduleUse = false;
                  }

                  if (placedAction) {
                     CrystalOwnership.markPlaced(target.base);
                     mc.field_1724.method_6104(class_1268.field_5808);
                     class_1511 placed = this.findCrystalOnBase(target.base);
                     if (placed != null) {
                        CrystalOwnership.bindCrystal(placed);
                     }

                     return true;
                  } else {
                     return false;
                  }
               }
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private boolean ensureCrystalSelected() {
      return mc.field_1724 != null && mc.field_1724.method_6047().method_31574(class_1802.field_8301);
   }

   private void restoreHand() {
      if (((Boolean)this.switchMode.getValue() || (Boolean)this.holdMode.getValue()) && this.restoreSlot >= 0 && mc.field_1724 != null && mc.field_1761 != null) {
         int slot = this.restoreSlot;
         this.restoreSlot = -1;
         if (slot >= 0 && slot <= 8) {
            if (mc.field_1724.method_31548().method_67532() != slot) {
               if (GrimMovementGuard.actionThisTick()) {
                  this.restoreSlot = slot;
               } else {
                  mc.field_1724.method_31548().method_61496(slot);
                  Access.ensureHasSentCarriedItem(mc.field_1761);
               }
            }
         }
      } else {
         this.restoreSlot = -1;
      }
   }

   private boolean rayHitsBase(class_3965 expected, float yaw, float pitch) {
      double reach = (Double)this.range.getValue() + 0.03;
      class_3965 hit = RotationHelper.raycastBlock(mc.field_1687, mc.field_1724, mc.field_1724.method_33571(), pitch, yaw, reach);
      return hit != null && hit.method_17783() == class_240.field_1332 && hit.method_17777().equals(expected.method_17777());
   }

   private boolean rayHitsCrystal(class_1511 crystal, float yaw, float pitch) {
      class_3966 hit = RotationHelper.raycastEntity(mc.field_1724, pitch, yaw, 2.88, (entity) -> entity == crystal);
      return hit != null && hit.method_17782() == crystal;
   }

   private int findCrystalSlot() {
      for(int i = 0; i < 9; ++i) {
         if (mc.field_1724.method_31548().method_5438(i).method_31574(class_1802.field_8301)) {
            return i;
         }
      }

      return -1;
   }

   private void syncPendingCrystals() {
      class_238 box = mc.field_1724.method_5829().method_1014((Double)this.range.getValue());

      for(class_1297 entity : mc.field_1687.method_8335(mc.field_1724, box)) {
         if (entity instanceof class_1511 crystal) {
            class_2338 base = CrystalOwnership.baseUnder(crystal);
            if (CrystalOwnership.isPending(base) || CrystalOwnership.ownsBase(base)) {
               CrystalOwnership.bindCrystal(crystal);
            }
         }
      }

   }

   private boolean inReach(class_243 point, boolean crystal) {
      double reach = (Double)this.range.getValue();
      if (crystal) {
         reach = Math.min(reach, 2.88);
      } else {
         reach += 0.03;
      }

      return mc.field_1724.method_33571().method_1025(point) <= reach * reach;
   }

   private static boolean isBaseBlock(class_2680 state) {
      return state.method_27852(class_2246.field_10540) || state.method_27852(class_2246.field_9987);
   }

   public void onEntityDeath() {
      if ((Boolean)this.pauseOnKill.getValue()) {
         this.pauseTicks = 20;
      }

   }

   public static void onLocalTotemPop() {
      AutoCrystalModule module = INSTANCE;
      if (module != null && module.isEnabled()) {
         module.lookWait = 0;
         module.pendingSilentEnd = false;
         module.pauseTicks = Math.max(module.pauseTicks, 2);
         module.slotSyncTicks = 0;
         module.deferredRestoreAfterPop = module.restoreSlot >= 0;
         if (module.heldSilent && SilentRotation.ownedBy(SilentRotation.Owner.COMBAT)) {
            SilentRotation.end();
         }

         module.heldSilent = false;
      }
   }

   private static final class Target {
      final class_2338 base;
      final class_1511 crystal;
      final class_3965 placeHit;
      final boolean crosshairCrystal;

      Target(class_2338 base, class_1511 crystal, class_3965 placeHit, boolean crosshairCrystal) {
         this.base = base;
         this.crystal = crystal;
         this.placeHit = placeHit;
         this.crosshairCrystal = crosshairCrystal;
      }
   }
}
