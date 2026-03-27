package com.maxwell.tutm.common.util;

import net.minecraft.world.entity.MobCategory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AutoRegisterEntity {
    String name();

    float width() default 0.6f;

    float height() default 1.8f;

    MobCategory category() default MobCategory.MONSTER;

    Class<?> renderer() default void.class;
}