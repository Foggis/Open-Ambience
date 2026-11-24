package dev.ambience.hooks;

public final class HookClear {
   private HookClear() {
   }

   public static void clearAll() {
      AimAssistHook.bind((AimAssistHook.Impl)null);
      AnchorMacroHook.bind((AnchorMacroHook.Impl)null);
      AntiBlindHook.bind((AntiBlindHook.Impl)null);
      AutoEscHook.bind((AutoEscHook.Impl)null);
      AutoSprintHook.bind((AutoSprintHook.Impl)null);
      AutoTotemHook.bind((AutoTotemHook.Impl)null);
      BreachSwapHook.bind((BreachSwapHook.Impl)null);
      CrystalOptimizerHook.bind((CrystalOptimizerHook.Impl)null);
      CustomSkyboxHook.bind((CustomSkyboxHook.Impl)null);
      FreecamHook.bind((FreecamHook.Impl)null);
      FreelookHook.bind((FreelookHook.Impl)null);
      FullbrightHook.bind((FullbrightHook.Impl)null);
      GuiRuntime.bind((GuiRuntime.Host)null);
      JumpResetHook.bind((JumpResetHook.Impl)null);
      LowFireHook.bind((LowFireHook.Impl)null);
      NoBlastHook.bind((NoBlastHook.Impl)null);
      NoInvisHook.bind((NoInvisHook.Impl)null);
      NoSmokeHook.bind((NoSmokeHook.Impl)null);
      OmniSprintHook.bind((OmniSprintHook.Impl)null);
      PlayerEspHook.bind((PlayerEspHook.Impl)null);
      ProjectileAimHook.bind((ProjectileAimHook.Impl)null);
      ShieldStunHook.bind((ShieldStunHook.Impl)null);
      TotemAnimationHook.bind((TotemAnimationHook.Impl)null);
      ViewModelHook.bind((ViewModelHook.Impl)null);
      WTapHook.bind((WTapHook.Impl)null);
      STapHook.bind((STapHook.Impl)null);
      XRayHook.bind((XRayHook.Impl)null);
   }
}
