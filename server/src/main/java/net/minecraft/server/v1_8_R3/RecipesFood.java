package net.minecraft.server.v1_8_R3;

public class RecipesFood {
   public void a(CraftingManager var1) {
      var1.registerShapelessRecipe(new ItemStack(Items.MUSHROOM_STEW), Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM, Items.BOWL);
      var1.registerShapedRecipe(new ItemStack(Items.COOKIE, 8), "#X#", 'X', new ItemStack(Items.DYE, 1, EnumColor.BROWN.getInvColorIndex()), '#', Items.WHEAT);
      var1.registerShapedRecipe(
         new ItemStack(Items.RABBIT_STEW),
         " R ",
         "CPM",
         " B ",
         'R',
         new ItemStack(Items.COOKED_RABBIT),
         'C',
         Items.CARROT,
         'P',
         Items.BAKED_POTATO,
         'M',
         Blocks.BROWN_MUSHROOM,
         'B',
         Items.BOWL
      );
      var1.registerShapedRecipe(
         new ItemStack(Items.RABBIT_STEW),
         " R ",
         "CPD",
         " B ",
         'R',
         new ItemStack(Items.COOKED_RABBIT),
         'C',
         Items.CARROT,
         'P',
         Items.BAKED_POTATO,
         'D',
         Blocks.RED_MUSHROOM,
         'B',
         Items.BOWL
      );
      var1.registerShapedRecipe(new ItemStack(Blocks.MELON_BLOCK), "MMM", "MMM", "MMM", 'M', Items.MELON);
      var1.registerShapedRecipe(new ItemStack(Items.MELON_SEEDS), "M", 'M', Items.MELON);
      var1.registerShapedRecipe(new ItemStack(Items.PUMPKIN_SEEDS, 4), "M", 'M', Blocks.PUMPKIN);
      var1.registerShapelessRecipe(new ItemStack(Items.PUMPKIN_PIE), Blocks.PUMPKIN, Items.SUGAR, Items.EGG);
      var1.registerShapelessRecipe(new ItemStack(Items.FERMENTED_SPIDER_EYE), Items.SPIDER_EYE, Blocks.BROWN_MUSHROOM, Items.SUGAR);
      var1.registerShapelessRecipe(new ItemStack(Items.BLAZE_POWDER, 2), Items.BLAZE_ROD);
      var1.registerShapelessRecipe(new ItemStack(Items.MAGMA_CREAM), Items.BLAZE_POWDER, Items.SLIME);
   }
}
