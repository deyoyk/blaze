package net.minecraft.server.v1_8_R3;

import java.util.Arrays;
import java.util.Collections;

public class RecipesBanner {
   void a(CraftingManager craftingmanager) {
      EnumColor[] var5;
      for (EnumColor enumcolor : var5 = EnumColor.values()) {
         craftingmanager.registerShapedRecipe(
            new ItemStack(Items.BANNER, 1, enumcolor.getInvColorIndex()),
            "###",
            "###",
            " | ",
            '#',
            new ItemStack(Blocks.WOOL, 1, enumcolor.getColorIndex()),
            '|',
            Items.STICK
         );
      }

      craftingmanager.a(new RecipesBanner.DuplicateRecipe(null));
      craftingmanager.a(new RecipesBanner.AddRecipe(null));
   }

   static class AddRecipe extends ShapelessRecipes implements IRecipe {
      private AddRecipe() {
         super(new ItemStack(Items.BANNER, 0, 0), Collections.singletonList(new ItemStack(Items.BANNER)));
      }

      @Override
      public boolean a(InventoryCrafting inventorycrafting, World world) {
         boolean flag = false;

         for (int i = 0; i < inventorycrafting.getSize(); i++) {
            ItemStack itemstack = inventorycrafting.getItem(i);
            if (itemstack != null && itemstack.getItem() == Items.BANNER) {
               if (flag) {
                  return false;
               }

               if (TileEntityBanner.c(itemstack) >= 6) {
                  return false;
               }

               flag = true;
            }
         }

         return !flag ? false : this.c(inventorycrafting) != null;
      }

      @Override
      public ItemStack craftItem(InventoryCrafting inventorycrafting) {
         ItemStack itemstack = null;

         for (int i = 0; i < inventorycrafting.getSize(); i++) {
            ItemStack itemstack1 = inventorycrafting.getItem(i);
            if (itemstack1 != null && itemstack1.getItem() == Items.BANNER) {
               itemstack = itemstack1.cloneItemStack();
               itemstack.count = 1;
               break;
            }
         }

         TileEntityBanner.EnumBannerPatternType tileentitybanner_enumbannerpatterntype = this.c(inventorycrafting);
         if (tileentitybanner_enumbannerpatterntype != null) {
            int j = 0;

            for (int k = 0; k < inventorycrafting.getSize(); k++) {
               ItemStack itemstack2 = inventorycrafting.getItem(k);
               if (itemstack2 != null && itemstack2.getItem() == Items.DYE) {
                  j = itemstack2.getData();
                  break;
               }
            }

            NBTTagCompound nbttagcompound = itemstack.a("BlockEntityTag", true);
            NBTTagList nbttaglist;
            if (nbttagcompound.hasKeyOfType("Patterns", 9)) {
               nbttaglist = nbttagcompound.getList("Patterns", 10);
            } else {
               nbttaglist = new NBTTagList();
               nbttagcompound.set("Patterns", nbttaglist);
            }

            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.setString("Pattern", tileentitybanner_enumbannerpatterntype.b());
            nbttagcompound1.setInt("Color", j);
            nbttaglist.add(nbttagcompound1);
         }

         return itemstack;
      }

      @Override
      public int a() {
         return 10;
      }

      @Override
      public ItemStack b() {
         return null;
      }

      @Override
      public ItemStack[] b(InventoryCrafting inventorycrafting) {
         ItemStack[] aitemstack = new ItemStack[inventorycrafting.getSize()];

         for (int i = 0; i < aitemstack.length; i++) {
            ItemStack itemstack = inventorycrafting.getItem(i);
            if (itemstack != null && itemstack.getItem().r()) {
               aitemstack[i] = new ItemStack(itemstack.getItem().q());
            }
         }

         return aitemstack;
      }

      private TileEntityBanner.EnumBannerPatternType c(InventoryCrafting inventorycrafting) {
         TileEntityBanner.EnumBannerPatternType[] var5;
         for (TileEntityBanner.EnumBannerPatternType tileentitybanner_enumbannerpatterntype : var5 = TileEntityBanner.EnumBannerPatternType.values()) {
            if (tileentitybanner_enumbannerpatterntype.d()) {
               boolean flag = true;
               if (tileentitybanner_enumbannerpatterntype.e()) {
                  boolean flag1 = false;
                  boolean flag2 = false;

                  for (int k = 0; k < inventorycrafting.getSize(); k++) {
                     ItemStack itemstack = inventorycrafting.getItem(k);
                     if (itemstack != null && itemstack.getItem() != Items.BANNER) {
                        if (itemstack.getItem() == Items.DYE) {
                           if (flag2) {
                              flag = false;
                              break;
                           }

                           flag2 = true;
                        } else {
                           if (flag1 || !itemstack.doMaterialsMatch(tileentitybanner_enumbannerpatterntype.f())) {
                              flag = false;
                              break;
                           }

                           flag1 = true;
                        }
                     }
                  }

                  if (!flag1) {
                     flag = false;
                  }
               } else if (inventorycrafting.getSize()
                  != tileentitybanner_enumbannerpatterntype.c().length * tileentitybanner_enumbannerpatterntype.c()[0].length()) {
                  flag = false;
               } else {
                  int l = -1;

                  for (int i1 = 0; i1 < inventorycrafting.getSize(); i1++) {
                     int kx = i1 / 3;
                     int j1 = i1 % 3;
                     ItemStack itemstack1 = inventorycrafting.getItem(i1);
                     if (itemstack1 != null && itemstack1.getItem() != Items.BANNER) {
                        if (itemstack1.getItem() != Items.DYE) {
                           flag = false;
                           break;
                        }

                        if (l != -1 && l != itemstack1.getData()) {
                           flag = false;
                           break;
                        }

                        if (tileentitybanner_enumbannerpatterntype.c()[kx].charAt(j1) == ' ') {
                           flag = false;
                           break;
                        }

                        l = itemstack1.getData();
                     } else if (tileentitybanner_enumbannerpatterntype.c()[kx].charAt(j1) != ' ') {
                        flag = false;
                        break;
                     }
                  }
               }

               if (flag) {
                  return tileentitybanner_enumbannerpatterntype;
               }
            }
         }

         return null;
      }

      AddRecipe(RecipesBanner.SyntheticClass_1 recipesbanner_syntheticclass_1) {
         this();
      }
   }

   static class DuplicateRecipe extends ShapelessRecipes implements IRecipe {
      private DuplicateRecipe() {
         super(new ItemStack(Items.BANNER, 0, 0), Arrays.asList(new ItemStack(Items.DYE, 0, 5)));
      }

      @Override
      public boolean a(InventoryCrafting inventorycrafting, World world) {
         ItemStack itemstack = null;
         ItemStack itemstack1 = null;

         for (int i = 0; i < inventorycrafting.getSize(); i++) {
            ItemStack itemstack2 = inventorycrafting.getItem(i);
            if (itemstack2 != null) {
               if (itemstack2.getItem() != Items.BANNER) {
                  return false;
               }

               if (itemstack != null && itemstack1 != null) {
                  return false;
               }

               int j = TileEntityBanner.b(itemstack2);
               boolean flag = TileEntityBanner.c(itemstack2) > 0;
               if (itemstack != null) {
                  if (flag) {
                     return false;
                  }

                  if (j != TileEntityBanner.b(itemstack)) {
                     return false;
                  }

                  itemstack1 = itemstack2;
               } else if (itemstack1 != null) {
                  if (!flag) {
                     return false;
                  }

                  if (j != TileEntityBanner.b(itemstack1)) {
                     return false;
                  }

                  itemstack = itemstack2;
               } else if (flag) {
                  itemstack = itemstack2;
               } else {
                  itemstack1 = itemstack2;
               }
            }
         }

         return itemstack != null && itemstack1 != null;
      }

      @Override
      public ItemStack craftItem(InventoryCrafting inventorycrafting) {
         for (int i = 0; i < inventorycrafting.getSize(); i++) {
            ItemStack itemstack = inventorycrafting.getItem(i);
            if (itemstack != null && TileEntityBanner.c(itemstack) > 0) {
               ItemStack itemstack1 = itemstack.cloneItemStack();
               itemstack1.count = 1;
               return itemstack1;
            }
         }

         return null;
      }

      @Override
      public int a() {
         return 2;
      }

      @Override
      public ItemStack b() {
         return null;
      }

      @Override
      public ItemStack[] b(InventoryCrafting inventorycrafting) {
         ItemStack[] aitemstack = new ItemStack[inventorycrafting.getSize()];

         for (int i = 0; i < aitemstack.length; i++) {
            ItemStack itemstack = inventorycrafting.getItem(i);
            if (itemstack != null) {
               if (itemstack.getItem().r()) {
                  aitemstack[i] = new ItemStack(itemstack.getItem().q());
               } else if (itemstack.hasTag() && TileEntityBanner.c(itemstack) > 0) {
                  aitemstack[i] = itemstack.cloneItemStack();
                  aitemstack[i].count = 1;
               }
            }
         }

         return aitemstack;
      }

      DuplicateRecipe(RecipesBanner.SyntheticClass_1 recipesbanner_syntheticclass_1) {
         this();
      }
   }

   static class SyntheticClass_1 {
   }
}
