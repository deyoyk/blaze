package net.minecraft.server.v1_8_R3;

import java.util.List;
import java.util.Random;

public class BlockCauldron extends Block {
   public static final BlockStateInteger LEVEL = BlockStateInteger.of("level", 0, 3);

   public BlockCauldron() {
      super(Material.ORE, MaterialMapColor.m);
      this.j(this.blockStateList.getBlockData().set(LEVEL, 0));
   }

   @Override
   public void a(World world, BlockPosition blockposition, IBlockData iblockdata, AxisAlignedBB axisalignedbb, List<AxisAlignedBB> list, Entity entity) {
      this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.3125F, 1.0F);
      super.a(world, blockposition, iblockdata, axisalignedbb, list, entity);
      this.a(0.0F, 0.0F, 0.0F, 0.125F, 1.0F, 1.0F);
      super.a(world, blockposition, iblockdata, axisalignedbb, list, entity);
      this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.125F);
      super.a(world, blockposition, iblockdata, axisalignedbb, list, entity);
      this.a(0.875F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      super.a(world, blockposition, iblockdata, axisalignedbb, list, entity);
      this.a(0.0F, 0.0F, 0.875F, 1.0F, 1.0F, 1.0F);
      super.a(world, blockposition, iblockdata, axisalignedbb, list, entity);
      this.j();
   }

   @Override
   public void j() {
      this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public boolean c() {
      return false;
   }

   @Override
   public boolean d() {
      return false;
   }

   @Override
   public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity) {
      int i = iblockdata.get(LEVEL);
      float f = blockposition.getY() + (6.0F + 3 * i) / 16.0F;
      if (entity.isBurning() && i > 0 && entity.getBoundingBox().b <= f) {
         entity.extinguish();
         this.a(world, blockposition, iblockdata, i - 1);
      }
   }

   @Override
   public boolean interact(
      World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2
   ) {
      ItemStack itemstack = entityhuman.inventory.getItemInHand();
      if (itemstack == null) {
         return true;
      } else {
         int i = iblockdata.get(LEVEL);
         Item item = itemstack.getItem();
         if (item == Items.WATER_BUCKET) {
            if (i < 3) {
               if (!entityhuman.abilities.canInstantlyBuild) {
                  entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, new ItemStack(Items.BUCKET));
               }

               this.a(world, blockposition, iblockdata, 3);
            }

            return true;
         } else if (item == Items.GLASS_BOTTLE) {
            if (i > 0) {
               if (!entityhuman.abilities.canInstantlyBuild) {
                  ItemStack itemstack1 = new ItemStack(Items.POTION, 1, 0);
                  if (!entityhuman.inventory.pickup(itemstack1)) {
                     world.addEntity(new EntityItem(world, blockposition.getX() + 0.5, blockposition.getY() + 1.5, blockposition.getZ() + 0.5, itemstack1));
                  } else if (entityhuman instanceof EntityPlayer) {
                     ((EntityPlayer)entityhuman).updateInventory(entityhuman.defaultContainer);
                  }

                  itemstack.count--;
                  if (itemstack.count <= 0) {
                     entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, null);
                  }
               }

               this.a(world, blockposition, iblockdata, i - 1);
            }

            return true;
         } else {
            if (i > 0 && item instanceof ItemArmor) {
               ItemArmor itemarmor = (ItemArmor)item;
               if (itemarmor.x_() == ItemArmor.EnumArmorMaterial.LEATHER && itemarmor.d_(itemstack)) {
                  itemarmor.c(itemstack);
                  this.a(world, blockposition, iblockdata, i - 1);
                  return true;
               }
            }

            if (i > 0 && item instanceof ItemBanner && TileEntityBanner.c(itemstack) > 0) {
               ItemStack itemstack1x = itemstack.cloneItemStack();
               itemstack1x.count = 1;
               TileEntityBanner.e(itemstack1x);
               if (itemstack.count <= 1 && !entityhuman.abilities.canInstantlyBuild) {
                  entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, itemstack1x);
               } else {
                  if (!entityhuman.inventory.pickup(itemstack1x)) {
                     world.addEntity(new EntityItem(world, blockposition.getX() + 0.5, blockposition.getY() + 1.5, blockposition.getZ() + 0.5, itemstack1x));
                  } else if (entityhuman instanceof EntityPlayer) {
                     ((EntityPlayer)entityhuman).updateInventory(entityhuman.defaultContainer);
                  }

                  if (!entityhuman.abilities.canInstantlyBuild) {
                     itemstack.count--;
                  }
               }

               if (!entityhuman.abilities.canInstantlyBuild) {
                  this.a(world, blockposition, iblockdata, i - 1);
               }

               return true;
            } else {
               return false;
            }
         }
      }
   }

   public void a(World world, BlockPosition blockposition, IBlockData iblockdata, int i) {
      world.setTypeAndData(blockposition, iblockdata.set(LEVEL, MathHelper.clamp(i, 0, 3)), 2);
      world.updateAdjacentComparators(blockposition, this);
   }

   @Override
   public void k(World world, BlockPosition blockposition) {
      if (world.random.nextInt(20) == 1) {
         IBlockData iblockdata = world.getType(blockposition);
         if (iblockdata.get(LEVEL) < 3) {
            world.setTypeAndData(blockposition, iblockdata.a(LEVEL), 2);
         }
      }
   }

   @Override
   public Item getDropType(IBlockData iblockdata, Random random, int i) {
      return Items.CAULDRON;
   }

   @Override
   public boolean isComplexRedstone() {
      return true;
   }

   @Override
   public int l(World world, BlockPosition blockposition) {
      return world.getType(blockposition).get(LEVEL);
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(LEVEL, i);
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      return iblockdata.get(LEVEL);
   }

   @Override
   protected BlockStateList getStateList() {
      return new BlockStateList(this, LEVEL);
   }
}
