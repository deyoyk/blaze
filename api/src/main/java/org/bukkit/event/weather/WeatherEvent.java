package org.bukkit.event.weather;

import org.bukkit.event.Event;

public abstract class WeatherEvent extends Event {
   protected org.bukkit.World world;

   public WeatherEvent(org.bukkit.World where) {
      this.world = where;
   }

   public final org.bukkit.World getWorld() {
      return this.world;
   }
}
