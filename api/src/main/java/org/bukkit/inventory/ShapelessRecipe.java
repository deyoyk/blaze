package org.bukkit.inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.bukkit.material.MaterialData;

public class ShapelessRecipe implements Recipe {
   private ItemStack output;
   private List<ItemStack> ingredients = new ArrayList<>();

   public ShapelessRecipe(ItemStack result) {
      this.output = new ItemStack(result);
   }

   public ShapelessRecipe addIngredient(MaterialData ingredient) {
      return this.addIngredient(1, ingredient);
   }

   public ShapelessRecipe addIngredient(org.bukkit.Material ingredient) {
      return this.addIngredient(1, ingredient, 0);
   }

   @Deprecated
   public ShapelessRecipe addIngredient(org.bukkit.Material ingredient, int rawdata) {
      return this.addIngredient(1, ingredient, rawdata);
   }

   public ShapelessRecipe addIngredient(int count, MaterialData ingredient) {
      return this.addIngredient(count, ingredient.getItemType(), ingredient.getData());
   }

   public ShapelessRecipe addIngredient(int count, org.bukkit.Material ingredient) {
      return this.addIngredient(count, ingredient, 0);
   }

   @Deprecated
   public ShapelessRecipe addIngredient(int count, org.bukkit.Material ingredient, int rawdata) {
      Validate.isTrue(this.ingredients.size() + count <= 9, "Shapeless recipes cannot have more than 9 ingredients");
      if (rawdata == -1) {
         rawdata = 32767;
      }

      while (count-- > 0) {
         this.ingredients.add(new ItemStack(ingredient, 1, (short)rawdata));
      }

      return this;
   }

   public ShapelessRecipe removeIngredient(org.bukkit.Material ingredient) {
      return this.removeIngredient(ingredient, 0);
   }

   public ShapelessRecipe removeIngredient(MaterialData ingredient) {
      return this.removeIngredient(ingredient.getItemType(), ingredient.getData());
   }

   public ShapelessRecipe removeIngredient(int count, org.bukkit.Material ingredient) {
      return this.removeIngredient(count, ingredient, 0);
   }

   public ShapelessRecipe removeIngredient(int count, MaterialData ingredient) {
      return this.removeIngredient(count, ingredient.getItemType(), ingredient.getData());
   }

   @Deprecated
   public ShapelessRecipe removeIngredient(org.bukkit.Material ingredient, int rawdata) {
      return this.removeIngredient(1, ingredient, rawdata);
   }

   @Deprecated
   public ShapelessRecipe removeIngredient(int count, org.bukkit.Material ingredient, int rawdata) {
      Iterator<ItemStack> iterator = this.ingredients.iterator();

      while (count > 0 && iterator.hasNext()) {
         ItemStack stack = iterator.next();
         if (stack.getType() == ingredient && stack.getDurability() == rawdata) {
            iterator.remove();
            count--;
         }
      }

      return this;
   }

   @Override
   public ItemStack getResult() {
      return this.output.clone();
   }

   public List<ItemStack> getIngredientList() {
      ArrayList<ItemStack> result = new ArrayList<>(this.ingredients.size());

      for (ItemStack ingredient : this.ingredients) {
         result.add(ingredient.clone());
      }

      return result;
   }
}
