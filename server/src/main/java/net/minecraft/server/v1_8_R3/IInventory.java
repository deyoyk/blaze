package net.minecraft.server.v1_8_R3;

import java.util.List;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;

public interface IInventory extends INamableTileEntity {
   int MAX_STACK = 64;

   int getSize();

   ItemStack getItem(int var1);

   ItemStack splitStack(int var1, int var2);

   ItemStack splitWithoutUpdate(int var1);

   void setItem(int var1, ItemStack var2);

   int getMaxStackSize();

   void update();

   boolean a(EntityHuman var1);

   void startOpen(EntityHuman var1);

   void closeContainer(EntityHuman var1);

   boolean b(int var1, ItemStack var2);

   int getProperty(int var1);

   void b(int var1, int var2);

   int g();

   void l();

   ItemStack[] getContents();

   void onOpen(CraftHumanEntity var1);

   void onClose(CraftHumanEntity var1);

   List<HumanEntity> getViewers();

   InventoryHolder getOwner();

   void setMaxStackSize(int var1);
}
