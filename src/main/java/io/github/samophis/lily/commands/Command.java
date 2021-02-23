package io.github.samophis.lily.commands;


import com.mewna.catnip.entity.util.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {
    // TODO: command categories

    String[] value(); // names/aliases

    String description() default "No available description.";

    String usage() default "No available usage.";

    Permission[] botPermissions() default { };

    Permission[] userPermissions() default { };
}