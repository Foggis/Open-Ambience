package dev.ambience.mixin.entity;

import net.minecraft.class_1297;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_1297.class})
public interface EntityRotationFieldsAccessor {
   @Accessor("field_6031")
   float ambience$getYRot();

   @Accessor("field_5965")
   float ambience$getXRot();

   @Accessor("field_6031")
   void ambience$setYRot(float var1);

   @Accessor("field_5965")
   void ambience$setXRot(float var1);
}
