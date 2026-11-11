package org.bukkit.block;

import org.bukkit.inventory.FurnaceInventory;

public interface Furnace extends BlockState, ContainerBlock {
   short getBurnTime();

   void setBurnTime(short var1);

   short getCookTime();

   void setCookTime(short var1);

   FurnaceInventory getInventory();
}
