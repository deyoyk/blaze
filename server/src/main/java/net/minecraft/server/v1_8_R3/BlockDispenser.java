package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.BlazeConfig;
import java.util.Random;

public class BlockDispenser extends BlockContainer {
   public static final BlockStateDirection FACING = BlockStateDirection.of("facing");
   public static final BlockStateBoolean TRIGGERED = BlockStateBoolean.of("triggered");
   public static final RegistryDefault<Item, IDispenseBehavior> REGISTRY = new RegistryDefault<>(new DispenseBehaviorItem());
   public static boolean eventFired = false;
   protected Random O = new Random();

   protected BlockDispenser() {
      super(Material.STONE);
      this.j(this.blockStateList.getBlockData().set(FACING, EnumDirection.NORTH).set(TRIGGERED, Boolean.FALSE));
      this.a(CreativeModeTab.d);
   }

   public static IPosition a(ISourceBlock isourceblock) {
      EnumDirection enumdirection = b(isourceblock.f());
      return new Position(
         isourceblock.getX() + 0.7 * enumdirection.getAdjacentX(),
         isourceblock.getY() + 0.7 * enumdirection.getAdjacentY(),
         isourceblock.getZ() + 0.7 * enumdirection.getAdjacentZ()
      );
   }

   public static EnumDirection b(int i) {
      return EnumDirection.fromType1(i & 7);
   }

   @Override
   public int a(World world) {
      return 4;
   }

   @Override
   public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
      super.onPlace(world, blockposition, iblockdata);
      this.e(world, blockposition, iblockdata);
   }

   private void e(World world, BlockPosition blockposition, IBlockData iblockdata) {
      EnumDirection enumdirection = iblockdata.get(FACING);
      boolean flag = world.getType(blockposition.getX(), blockposition.getY(), blockposition.getZ() - 1).getBlock().o();
      boolean flag1 = world.getType(blockposition.getX(), blockposition.getY(), blockposition.getZ() + 1).getBlock().o();
      if (enumdirection == EnumDirection.NORTH && flag && !flag1) {
         enumdirection = EnumDirection.SOUTH;
      } else if (enumdirection == EnumDirection.SOUTH && flag1 && !flag) {
         enumdirection = EnumDirection.NORTH;
      } else {
         boolean flag2 = world.getType(blockposition.getX() - 1, blockposition.getY(), blockposition.getZ()).getBlock().o();
         boolean flag3 = world.getType(blockposition.getX() + 1, blockposition.getY(), blockposition.getZ()).getBlock().o();
         if (enumdirection == EnumDirection.WEST && flag2 && !flag3) {
            enumdirection = EnumDirection.EAST;
         } else if (enumdirection == EnumDirection.EAST && flag3 && !flag2) {
            enumdirection = EnumDirection.WEST;
         }
      }

      world.setTypeAndData(blockposition, iblockdata.set(FACING, enumdirection).set(TRIGGERED, false), 2, false, false);
   }

   @Override
   public boolean interact(
      World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2
   ) {
      TileEntity tileentity = world.getTileEntity(blockposition);
      if (tileentity instanceof TileEntityDispenser) {
         entityhuman.openContainer((TileEntityDispenser)tileentity);
      }

      return true;
   }

   public void dispense(World world, BlockPosition blockposition) {
      SourceBlock sourceblock = new SourceBlock(world, blockposition);
      TileEntityDispenser tileentitydispenser = sourceblock.getTileEntity();
      if (tileentitydispenser != null) {
         int i = tileentitydispenser.m();
         if (i < 0) {
            if ((Boolean)BlazeConfig.dispenserEffects.getValue()) {
               world.triggerEffect(1001, blockposition, 0);
            }
         } else {
            ItemStack itemstack = tileentitydispenser.getItem(i);
            IDispenseBehavior idispensebehavior = this.a(itemstack);
            if (idispensebehavior != IDispenseBehavior.NONE) {
               ItemStack itemstack1 = idispensebehavior.a(sourceblock, itemstack);
               eventFired = false;
               tileentitydispenser.setItem(i, itemstack1.count <= 0 ? null : itemstack1);
            }
         }
      }
   }

   protected IDispenseBehavior a(ItemStack itemstack) {
      return REGISTRY.get(itemstack == null ? null : itemstack.getItem());
   }

   @Override
   public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
      boolean flag = world.isBlockIndirectlyPowered(blockposition) || world.isBlockIndirectlyPowered(blockposition.up());
      boolean flag1 = iblockdata.get(TRIGGERED);
      if (flag && !flag1) {
         world.a(blockposition, this, this.a(world));
         world.setTypeAndData(blockposition, iblockdata.set(TRIGGERED, true), 4, false, false);
      } else if (!flag && flag1) {
         world.setTypeAndData(blockposition, iblockdata.set(TRIGGERED, false), 4, false, false);
      }
   }

   @Override
   public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
      this.dispense(world, blockposition);
   }

   @Override
   public TileEntity a(World world, int i) {
      return new TileEntityDispenser();
   }

   @Override
   public IBlockData getPlacedState(
      World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving
   ) {
      return this.getBlockData().set(FACING, BlockPiston.a(world, blockposition, entityliving)).set(TRIGGERED, false);
   }

   @Override
   public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
      world.setTypeAndData(blockposition, iblockdata.set(FACING, BlockPiston.a(world, blockposition, entityliving)), 2, false, false);
      if (itemstack.hasName()) {
         TileEntity tileentity = world.getTileEntity(blockposition);
         if (tileentity instanceof TileEntityDispenser) {
            ((TileEntityDispenser)tileentity).a(itemstack.getName());
         }
      }
   }

   @Override
   public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
      TileEntity tileentity = world.getTileEntity(blockposition);
      if (tileentity instanceof TileEntityDispenser) {
         InventoryUtils.dropInventory(world, blockposition, (TileEntityDispenser)tileentity);
         world.updateAdjacentComparators(blockposition, this);
      }

      super.remove(world, blockposition, iblockdata);
   }

   @Override
   public boolean isComplexRedstone() {
      return true;
   }

   @Override
   public int l(World world, BlockPosition blockposition) {
      return Container.a(world.getTileEntity(blockposition));
   }

   @Override
   public int b() {
      return 3;
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(FACING, b(i)).set(TRIGGERED, (i & 8) > 0);
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      byte b0 = 0;
      int i = b0 | iblockdata.get(FACING).a();
      if (iblockdata.get(TRIGGERED)) {
         i |= 8;
      }

      return i;
   }

   @Override
   public BlockStateList getStateList() {
      return new BlockStateList(this, FACING, TRIGGERED);
   }
}
