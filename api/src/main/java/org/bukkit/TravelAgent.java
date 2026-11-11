package org.bukkit;

public interface TravelAgent {
   org.bukkit.TravelAgent setSearchRadius(int var1);

   int getSearchRadius();

   org.bukkit.TravelAgent setCreationRadius(int var1);

   int getCreationRadius();

   boolean getCanCreatePortal();

   void setCanCreatePortal(boolean var1);

   org.bukkit.Location findOrCreate(org.bukkit.Location var1);

   org.bukkit.Location findPortal(org.bukkit.Location var1);

   boolean createPortal(org.bukkit.Location var1);
}
