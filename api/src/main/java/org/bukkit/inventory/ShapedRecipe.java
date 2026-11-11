package org.bukkit.inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang.Validate;
import org.bukkit.material.MaterialData;

public class ShapedRecipe implements Recipe {
   private ItemStack output;
   private String[] rows;
   private Map<Character, ItemStack> ingredients = new HashMap<>();

   public ShapedRecipe(ItemStack result) {
      this.output = new ItemStack(result);
   }

   public ShapedRecipe shape(String... shape) {
      Validate.notNull(shape, "Must provide a shape");
      Validate.isTrue(shape.length > 0 && shape.length < 4, "Crafting recipes should be 1, 2, 3 rows, not ", shape.length);

      for (String row : shape) {
         Validate.notNull(row, "Shape cannot have null rows");
         Validate.isTrue(row.length() > 0 && row.length() < 4, "Crafting rows should be 1, 2, or 3 characters, not ", row.length());
      }

      this.rows = new String[shape.length];

      for (int i = 0; i < shape.length; i++) {
         this.rows[i] = shape[i];
      }

      HashMap<Character, ItemStack> newIngredients = new HashMap<>();

      for (String row : shape) {
         char[] var7 = row.toCharArray();
         int var8 = var7.length;

         for (int var9 = 0; var9 < var8; var9++) {
            Character c = var7[var9];
            newIngredients.put(c, this.ingredients.get(c));
         }
      }

      this.ingredients = newIngredients;
      return this;
   }

   public ShapedRecipe setIngredient(char key, MaterialData ingredient) {
      return this.setIngredient(key, ingredient.getItemType(), ingredient.getData());
   }

   public ShapedRecipe setIngredient(char key, org.bukkit.Material ingredient) {
      return this.setIngredient(key, ingredient, 0);
   }

   @Deprecated
   public ShapedRecipe setIngredient(char key, org.bukkit.Material ingredient, int raw) {
      Validate.isTrue(this.ingredients.containsKey(key), "Symbol does not appear in the shape:", key);
      if (raw == -1) {
         raw = 32767;
      }

      this.ingredients.put(key, new ItemStack(ingredient, 1, (short)raw));
      return this;
   }

   public Map<Character, ItemStack> getIngredientMap() {
      HashMap<Character, ItemStack> result = new HashMap<>();

      for (Entry<Character, ItemStack> ingredient : this.ingredients.entrySet()) {
         if (ingredient.getValue() == null) {
            result.put(ingredient.getKey(), null);
         } else {
            result.put(ingredient.getKey(), ingredient.getValue().clone());
         }
      }

      return result;
   }

   public String[] getShape() {
      return (String[])this.rows.clone();
   }

   @Override
   public ItemStack getResult() {
      return this.output.clone();
   }
}
