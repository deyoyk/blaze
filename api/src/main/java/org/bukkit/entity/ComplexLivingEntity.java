package org.bukkit.entity;

import java.util.Set;

public interface ComplexLivingEntity extends LivingEntity {
   Set<ComplexEntityPart> getParts();
}
