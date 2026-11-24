package dev.ambience.mixin.entity;

import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_746.class})
public interface EntityRotationAccessor {
   @Accessor("field_3916")
   float ambience$getXBob();

   @Accessor("field_3916")
   void ambience$setXBob(float var1);

   @Accessor("field_3914")
   float ambience$getXBobO();

   @Accessor("field_3914")
   void ambience$setXBobO(float var1);

   @Accessor("field_3932")
   float ambience$getYBob();

   @Accessor("field_3932")
   void ambience$setYBob(float var1);

   @Accessor("field_3931")
   float ambience$getYBobO();

   @Accessor("field_3931")
   void ambience$setYBobO(float var1);
}
