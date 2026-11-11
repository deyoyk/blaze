package org.bukkit;

import com.google.common.collect.ImmutableMap;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Warning {
   boolean value() default false;

   String reason() default "";

   public static enum WarningState {
      ON,
      OFF,
      DEFAULT;

      private static final Map<String, org.bukkit.Warning.WarningState> values = ImmutableMap.<String, org.bukkit.Warning.WarningState>builder()
         .put("off", OFF)
         .put("false", OFF)
         .put("f", OFF)
         .put("no", OFF)
         .put("n", OFF)
         .put("on", ON)
         .put("true", ON)
         .put("t", ON)
         .put("yes", ON)
         .put("y", ON)
         .put("", DEFAULT)
         .put("d", DEFAULT)
         .put("default", DEFAULT)
         .build();

      public boolean printFor(org.bukkit.Warning warning) {
         return this != DEFAULT ? this == ON : warning == null || warning.value();
      }

      public static org.bukkit.Warning.WarningState value(String value) {
         if (value == null) {
            return DEFAULT;
         } else {
            org.bukkit.Warning.WarningState state = values.get(value.toLowerCase());
            return state == null ? DEFAULT : state;
         }
      }
   }
}
