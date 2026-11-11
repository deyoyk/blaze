package org.bukkit.block;

import org.bukkit.entity.CreatureType;
import org.bukkit.entity.EntityType;

public interface CreatureSpawner extends BlockState {
   @Deprecated
   CreatureType getCreatureType();

   EntityType getSpawnedType();

   void setSpawnedType(EntityType var1);

   @Deprecated
   void setCreatureType(CreatureType var1);

   @Deprecated
   String getCreatureTypeId();

   void setCreatureTypeByName(String var1);

   String getCreatureTypeName();

   @Deprecated
   void setCreatureTypeId(String var1);

   int getDelay();

   void setDelay(int var1);
}
