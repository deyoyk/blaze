package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class BlockLongGrass extends BlockPlant implements IBlockFragilePlantElement {
   public static final BlockStateEnum<BlockLongGrass.EnumTallGrassType> TYPE = BlockStateEnum.of("type", BlockLongGrass.EnumTallGrassType.class);

   protected BlockLongGrass() {
      super(Material.REPLACEABLE_PLANT);
      this.j(this.blockStateList.getBlockData().set(TYPE, BlockLongGrass.EnumTallGrassType.DEAD_BUSH));
      float f = 0.4F;
      this.a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.8F, 0.5F + f);
   }

   @Override
   public boolean f(World world, BlockPosition blockposition, IBlockData iblockdata) {
      return this.c(world.getType(blockposition.down()).getBlock());
   }

   @Override
   public boolean a(World world, BlockPosition blockposition) {
      return true;
   }

   @Override
   public Item getDropType(IBlockData iblockdata, Random random, int i) {
      return random.nextInt(8) == 0 ? Items.WHEAT_SEEDS : null;
   }

   @Override
   public int getDropCount(int i, Random random) {
      return 1 + random.nextInt(i * 2 + 1);
   }

   @Override
   public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, TileEntity tileentity) {
      if (entityhuman.bZ() != null && entityhuman.bZ().getItem() == Items.SHEARS) {
         this.a(world, blockposition, new ItemStack(Blocks.TALLGRASS, 1, iblockdata.get(TYPE).a()));
      } else {
         super.a(world, entityhuman, blockposition, iblockdata, tileentity);
      }
   }

   @Override
   public int getDropData(World world, BlockPosition blockposition) {
      IBlockData iblockdata = world.getType(blockposition);
      return iblockdata.getBlock().toLegacyData(iblockdata);
   }

   @Override
   public boolean a(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
      return iblockdata.get(TYPE) != BlockLongGrass.EnumTallGrassType.DEAD_BUSH;
   }

   @Override
   public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
      return true;
   }

   @Override
   public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
      BlockTallPlant.EnumTallFlowerVariants blocktallplant_enumtallflowervariants = BlockTallPlant.EnumTallFlowerVariants.GRASS;
      if (iblockdata.get(TYPE) == BlockLongGrass.EnumTallGrassType.FERN) {
         blocktallplant_enumtallflowervariants = BlockTallPlant.EnumTallFlowerVariants.FERN;
      }

      if (Blocks.DOUBLE_PLANT.canPlace(world, blockposition)) {
         Blocks.DOUBLE_PLANT.a(world, blockposition, blocktallplant_enumtallflowervariants, 2);
      }
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(TYPE, BlockLongGrass.EnumTallGrassType.a(i));
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      return iblockdata.get(TYPE).a();
   }

   @Override
   protected BlockStateList getStateList() {
      return new BlockStateList(this, TYPE);
   }

   public static enum EnumTallGrassType implements INamable {
      DEAD_BUSH(0, "dead_bush"),
      GRASS(1, "tall_grass"),
      FERN(2, "fern");

      private static final BlockLongGrass.EnumTallGrassType[] d = new BlockLongGrass.EnumTallGrassType[values().length];
      private final int e;
      private final String f;

      static {
         for (BlockLongGrass.EnumTallGrassType blocklonggrass_enumtallgrasstype : values()) {
            d[blocklonggrass_enumtallgrasstype.a()] = blocklonggrass_enumtallgrasstype;
         }
      }

      private EnumTallGrassType(int i, String s) {
         this.e = i;
         this.f = s;
      }

      public int a() {
         return this.e;
      }

      @Override
      public String toString() {
         return this.f;
      }

      public static BlockLongGrass.EnumTallGrassType a(int i) {
         if (i < 0 || i >= d.length) {
            i = 0;
         }

         return d[i];
      }

      @Override
      public String getName() {
         return this.f;
      }
   }
}
