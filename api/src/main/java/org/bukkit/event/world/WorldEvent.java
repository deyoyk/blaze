package org.bukkit.event.world;

import org.bukkit.event.Event;

public abstract class WorldEvent extends Event {
   private final org.bukkit.World world;

   public WorldEvent(org.bukkit.World world) {
      this.world = world;
   }

   public org.bukkit.World getWorld() {
      return this.world;
   }
}
