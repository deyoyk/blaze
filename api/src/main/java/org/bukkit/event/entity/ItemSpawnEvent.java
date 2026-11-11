package org.bukkit.event.entity;

import org.bukkit.entity.Item;

public class ItemSpawnEvent extends EntitySpawnEvent {
   public ItemSpawnEvent(Item spawnee) {
      super(spawnee);
   }

   @Deprecated
   public ItemSpawnEvent(Item spawnee, org.bukkit.Location loc) {
      this(spawnee);
   }

   public Item getEntity() {
      return (Item)this.entity;
   }
}
