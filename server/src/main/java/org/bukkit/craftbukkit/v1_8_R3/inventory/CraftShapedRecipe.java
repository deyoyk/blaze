package org.bukkit.craftbukkit.v1_8_R3.inventory;

import java.util.Map;
import net.minecraft.server.v1_8_R3.CraftingManager;
import net.minecraft.server.v1_8_R3.ShapedRecipes;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class CraftShapedRecipe extends ShapedRecipe implements CraftRecipe {
   public CraftShapedRecipe(ItemStack result) {
      super(result);
   }

   public CraftShapedRecipe(ItemStack result, ShapedRecipes recipe) {
      this(result);
   }

   public static CraftShapedRecipe fromBukkitRecipe(ShapedRecipe recipe) {
      if (recipe instanceof CraftShapedRecipe) {
         return (CraftShapedRecipe)recipe;
      } else {
         CraftShapedRecipe ret = new CraftShapedRecipe(recipe.getResult());
         String[] shape = recipe.getShape();
         ret.shape(shape);
         Map<Character, ItemStack> ingredientMap = recipe.getIngredientMap();

         for (char c : ingredientMap.keySet()) {
            ItemStack stack = ingredientMap.get(c);
            if (stack != null) {
               ret.setIngredient(c, stack.getType(), stack.getDurability());
            }
         }

         return ret;
      }
   }

   @Override
   public void addToCraftingManager() {
      String[] shape = this.getShape();
      Map<Character, ItemStack> ingred = this.getIngredientMap();
      int datalen = shape.length;
      datalen += ingred.size() * 2;
      int i = 0;

      Object[] data;
      for (data = new Object[datalen]; i < shape.length; i++) {
         data[i] = shape[i];
      }

      for (char c : ingred.keySet()) {
         ItemStack mdata = ingred.get(c);
         if (mdata != null) {
            data[i] = c;
            i++;
            int id = mdata.getTypeId();
            short dmg = mdata.getDurability();
            data[i] = new net.minecraft.server.v1_8_R3.ItemStack(CraftMagicNumbers.getItem(id), 1, dmg);
            i++;
         }
      }

      CraftingManager.getInstance().registerShapedRecipe(CraftItemStack.asNMSCopy(this.getResult()), data);
   }
}
