package org.bukkit.block;

public interface Jukebox extends BlockState {
   org.bukkit.Material getPlaying();

   void setPlaying(org.bukkit.Material var1);

   boolean isPlaying();

   boolean eject();
}
