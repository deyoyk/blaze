package org.bukkit.entity;

public interface Painting extends Hanging {
   org.bukkit.Art getArt();

   boolean setArt(org.bukkit.Art var1);

   boolean setArt(org.bukkit.Art var1, boolean var2);
}
