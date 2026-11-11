package org.bukkit.craftbukkit.v1_8_R3.inventory;

import java.util.Iterator;
import net.minecraft.server.v1_8_R3.CraftingManager;
import net.minecraft.server.v1_8_R3.IRecipe;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.RecipesFurnace;
import org.bukkit.inventory.Recipe;

public class RecipeIterator implements Iterator<Recipe> {
   private final Iterator<IRecipe> recipes;
   private final Iterator<ItemStack> smeltingCustom;
   private final Iterator<ItemStack> smeltingVanilla;
   private Iterator<?> removeFrom = null;

   public RecipeIterator() {
      this.recipes = CraftingManager.getInstance().getRecipes().iterator();
      this.smeltingCustom = RecipesFurnace.getInstance().customRecipes.keySet().iterator();
      this.smeltingVanilla = RecipesFurnace.getInstance().recipes.keySet().iterator();
   }

   @Override
   public boolean hasNext() {
      return this.recipes.hasNext() || this.smeltingCustom.hasNext() || this.smeltingVanilla.hasNext();
   }

   public Recipe next() {
      if (this.recipes.hasNext()) {
         this.removeFrom = this.recipes;
         return this.recipes.next().toBukkitRecipe();
      } else {
         ItemStack item;
         if (this.smeltingCustom.hasNext()) {
            this.removeFrom = this.smeltingCustom;
            item = this.smeltingCustom.next();
         } else {
            this.removeFrom = this.smeltingVanilla;
            item = this.smeltingVanilla.next();
         }

         CraftItemStack stack = CraftItemStack.asCraftMirror(RecipesFurnace.getInstance().getResult(item));
         return new CraftFurnaceRecipe(stack, CraftItemStack.asCraftMirror(item));
      }
   }

   @Override
   public void remove() {
      if (this.removeFrom == null) {
         throw new IllegalStateException();
      } else {
         this.removeFrom.remove();
      }
   }
}
