package net.minecraft.server.v1_8_R3;

public class BlockLeaves2 extends BlockLeaves {
   public static final BlockStateEnum<BlockWood.EnumLogVariant> VARIANT = BlockStateEnum.a(
      "variant", BlockWood.EnumLogVariant.class, blockwood_enumlogvariant -> blockwood_enumlogvariant.a() >= 4
   );

   public BlockLeaves2() {
      this.j(this.blockStateList.getBlockData().set(VARIANT, BlockWood.EnumLogVariant.ACACIA).set(CHECK_DECAY, Boolean.TRUE).set(DECAYABLE, Boolean.TRUE));
   }

   @Override
   protected void a(World world, BlockPosition blockposition, IBlockData iblockdata, int i) {
      if (iblockdata.get(VARIANT) == BlockWood.EnumLogVariant.DARK_OAK && world.random.nextInt(i) == 0) {
         this.a(world, blockposition, new ItemStack(Items.APPLE, 1, 0));
      }
   }

   @Override
   public int getDropData(IBlockData iblockdata) {
      return iblockdata.get(VARIANT).a();
   }

   @Override
   public int getDropData(World world, BlockPosition blockposition) {
      IBlockData iblockdata = world.getType(blockposition);
      return iblockdata.getBlock().toLegacyData(iblockdata) & 3;
   }

   @Override
   protected ItemStack i(IBlockData iblockdata) {
      return new ItemStack(Item.getItemOf(this), 1, iblockdata.get(VARIANT).a() - 4);
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(VARIANT, this.b(i)).set(DECAYABLE, (i & 4) == 0).set(CHECK_DECAY, (i & 8) > 0);
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      byte b0 = 0;
      int i = b0 | iblockdata.get(VARIANT).a() - 4;
      if (!iblockdata.get(DECAYABLE)) {
         i |= 4;
      }

      if (iblockdata.get(CHECK_DECAY)) {
         i |= 8;
      }

      return i;
   }

   @Override
   public BlockWood.EnumLogVariant b(int i) {
      return BlockWood.EnumLogVariant.a((i & 3) + 4);
   }

   @Override
   protected BlockStateList getStateList() {
      return new BlockStateList(this, VARIANT, CHECK_DECAY, DECAYABLE);
   }

   @Override
   public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, TileEntity tileentity) {
      if (entityhuman.bZ() != null && entityhuman.bZ().getItem() == Items.SHEARS) {
         this.a(world, blockposition, new ItemStack(Item.getItemOf(this), 1, iblockdata.get(VARIANT).a() - 4));
      } else {
         super.a(world, entityhuman, blockposition, iblockdata, tileentity);
      }
   }
}
