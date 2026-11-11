package org.bukkit.craftbukkit.v1_8_R3.entity;

import net.minecraft.server.v1_8_R3.EntityMushroomCow;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MushroomCow;

public class CraftMushroomCow extends CraftCow implements MushroomCow {
   public CraftMushroomCow(CraftServer server, EntityMushroomCow entity) {
      super(server, entity);
   }

   public EntityMushroomCow getHandle() {
      return (EntityMushroomCow)this.entity;
   }

   @Override
   public String toString() {
      return "CraftMushroomCow";
   }

   @Override
   public EntityType getType() {
      return EntityType.MUSHROOM_COW;
   }
}
