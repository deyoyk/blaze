package org.bukkit.craftbukkit.v1_8_R3.inventory;

import java.util.List;
import net.minecraft.server.v1_8_R3.CraftingManager;
import net.minecraft.server.v1_8_R3.ShapelessRecipes;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

public class CraftShapelessRecipe extends ShapelessRecipe implements CraftRecipe {
   public CraftShapelessRecipe(ItemStack result) {
      super(result);
   }

   public CraftShapelessRecipe(ItemStack result, ShapelessRecipes recipe) {
      this(result);
   }

   public static CraftShapelessRecipe fromBukkitRecipe(ShapelessRecipe recipe) {
      if (recipe instanceof CraftShapelessRecipe) {
         return (CraftShapelessRecipe)recipe;
      } else {
         CraftShapelessRecipe ret = new CraftShapelessRecipe(recipe.getResult());

         for (ItemStack ingred : recipe.getIngredientList()) {
            ret.addIngredient(ingred.getType(), ingred.getDurability());
         }

         return ret;
      }
   }

   @Override
   public void addToCraftingManager() {
      List<ItemStack> ingred = this.getIngredientList();
      Object[] data = new Object[ingred.size()];
      int i = 0;

      for (ItemStack mdata : ingred) {
         int id = mdata.getTypeId();
         short dmg = mdata.getDurability();
         data[i] = new net.minecraft.server.v1_8_R3.ItemStack(CraftMagicNumbers.getItem(id), 1, dmg);
         i++;
      }

      CraftingManager.getInstance().registerShapelessRecipe(CraftItemStack.asNMSCopy(this.getResult()), data);
   }
}
