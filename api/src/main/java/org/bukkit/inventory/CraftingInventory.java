package org.bukkit.inventory;

public interface CraftingInventory extends Inventory {
   ItemStack getResult();

   ItemStack[] getMatrix();

   void setResult(ItemStack var1);

   void setMatrix(ItemStack[] var1);

   Recipe getRecipe();
}
