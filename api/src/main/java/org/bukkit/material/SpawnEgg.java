package org.bukkit.material;

import org.bukkit.entity.EntityType;

public class SpawnEgg extends MaterialData {
   public SpawnEgg() {
      super(org.bukkit.Material.MONSTER_EGG);
   }

   @Deprecated
   public SpawnEgg(int type, byte data) {
      super(type, data);
   }

   @Deprecated
   public SpawnEgg(byte data) {
      super(org.bukkit.Material.MONSTER_EGG, data);
   }

   public SpawnEgg(EntityType type) {
      this();
      this.setSpawnedType(type);
   }

   public EntityType getSpawnedType() {
      return EntityType.fromId(this.getData());
   }

   public void setSpawnedType(EntityType type) {
      this.setData((byte)type.getTypeId());
   }

   @Override
   public String toString() {
      return "SPAWN EGG{" + this.getSpawnedType() + "}";
   }

   public SpawnEgg clone() {
      return (SpawnEgg)super.clone();
   }
}
