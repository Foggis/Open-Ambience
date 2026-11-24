package dev.ambience.mixin.client;

import net.minecraft.class_4603;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_4603.class})
public interface ScreenEffectRendererAccessor {
   @Accessor("field_59973")
   void ambience$setItemActivationTicks(int var1);
}
