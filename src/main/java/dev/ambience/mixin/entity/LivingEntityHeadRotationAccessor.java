package dev.ambience.mixin.entity;

import net.minecraft.class_1309;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_1309.class})
public interface LivingEntityHeadRotationAccessor {
   @Accessor("field_6241")
   float ambience$getYHeadRot();

   @Accessor("field_6241")
   void ambience$setYHeadRot(float var1);

   @Accessor("field_6259")
   float ambience$getYHeadRotO();

   @Accessor("field_6259")
   void ambience$setYHeadRotO(float var1);
}
