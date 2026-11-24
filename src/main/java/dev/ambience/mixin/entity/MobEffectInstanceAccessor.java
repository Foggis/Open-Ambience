package dev.ambience.mixin.entity;

import net.minecraft.class_1293;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_1293.class})
public interface MobEffectInstanceAccessor {
   @Accessor("field_5895")
   void ambience$setDuration(int var1);
}
