package org.bukkit.craftbukkit.v1_8_R3.entity;

import net.minecraft.server.v1_8_R3.EntityComplexPart;
import net.minecraft.server.v1_8_R3.EntityEnderDragon;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;

public class CraftComplexPart extends CraftEntity implements ComplexEntityPart {
   public CraftComplexPart(CraftServer server, EntityComplexPart entity) {
      super(server, entity);
   }

   @Override
   public ComplexLivingEntity getParent() {
      return (ComplexLivingEntity)((EntityEnderDragon)this.getHandle().owner).getBukkitEntity();
   }

   @Override
   public void setLastDamageCause(EntityDamageEvent cause) {
      this.getParent().setLastDamageCause(cause);
   }

   @Override
   public EntityDamageEvent getLastDamageCause() {
      return this.getParent().getLastDamageCause();
   }

   public EntityComplexPart getHandle() {
      return (EntityComplexPart)this.entity;
   }

   @Override
   public String toString() {
      return "CraftComplexPart";
   }

   @Override
   public EntityType getType() {
      return EntityType.COMPLEX_PART;
   }
}
