package org.bukkit.craftbukkit.v1_8_R3.entity;

import net.minecraft.server.v1_8_R3.EntityCreature;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;

public class CraftCreature extends CraftLivingEntity implements Creature {
   public CraftCreature(CraftServer server, EntityCreature entity) {
      super(server, entity);
   }

   @Override
   public void setTarget(LivingEntity target) {
      EntityCreature entity = this.getHandle();
      if (target == null) {
         entity.setGoalTarget(null, null, false);
      } else if (target instanceof CraftLivingEntity) {
         entity.setGoalTarget(((CraftLivingEntity)target).getHandle(), null, false);
      }
   }

   public CraftLivingEntity getTarget() {
      return this.getHandle().getGoalTarget() == null ? null : (CraftLivingEntity)this.getHandle().getGoalTarget().getBukkitEntity();
   }

   public EntityCreature getHandle() {
      return (EntityCreature)this.entity;
   }

   @Override
   public String toString() {
      return "CraftCreature";
   }
}
