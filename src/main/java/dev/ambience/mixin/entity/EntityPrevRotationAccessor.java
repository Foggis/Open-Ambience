package dev.ambience.mixin.entity;

import net.minecraft.class_1297;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_1297.class})
public interface EntityPrevRotationAccessor {
   @Accessor("field_5982")
   float ambience$getYRotO();

   @Accessor("field_6004")
   float ambience$getXRotO();

   @Accessor("field_5982")
   void ambience$setYRotO(float var1);

   @Accessor("field_6004")
   void ambience$setXRotO(float var1);
}
