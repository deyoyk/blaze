package net.minecraft.server.v1_8_R3;

import java.util.Random;

public class BlockTallPlant extends BlockPlant implements IBlockFragilePlantElement {
   public static final BlockStateEnum<BlockTallPlant.EnumTallFlowerVariants> VARIANT = BlockStateEnum.of("variant", BlockTallPlant.EnumTallFlowerVariants.class);
   public static final BlockStateEnum<BlockTallPlant.EnumTallPlantHalf> HALF = BlockStateEnum.of("half", BlockTallPlant.EnumTallPlantHalf.class);
   public static final BlockStateEnum<EnumDirection> N = BlockDirectional.FACING;

   public BlockTallPlant() {
      super(Material.REPLACEABLE_PLANT);
      this.j(
         this.blockStateList
            .getBlockData()
            .set(VARIANT, BlockTallPlant.EnumTallFlowerVariants.SUNFLOWER)
            .set(HALF, BlockTallPlant.EnumTallPlantHalf.LOWER)
            .set(N, EnumDirection.NORTH)
      );
      this.c(0.0F);
      this.a(h);
      this.c("doublePlant");
   }

   @Override
   public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
      this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   public BlockTallPlant.EnumTallFlowerVariants e(IBlockAccess iblockaccess, BlockPosition blockposition) {
      IBlockData iblockdata = iblockaccess.getType(blockposition);
      if (iblockdata.getBlock() == this) {
         iblockdata = this.updateState(iblockdata, iblockaccess, blockposition);
         return iblockdata.get(VARIANT);
      } else {
         return BlockTallPlant.EnumTallFlowerVariants.FERN;
      }
   }

   @Override
   public boolean canPlace(World world, BlockPosition blockposition) {
      return super.canPlace(world, blockposition) && world.isEmpty(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ());
   }

   @Override
   public boolean a(World world, BlockPosition blockposition) {
      IBlockData iblockdata = world.getType(blockposition);
      if (iblockdata.getBlock() != this) {
         return true;
      } else {
         BlockTallPlant.EnumTallFlowerVariants blocktallplant_enumtallflowervariants = this.updateState(iblockdata, world, blockposition).get(VARIANT);
         return blocktallplant_enumtallflowervariants == BlockTallPlant.EnumTallFlowerVariants.FERN
            || blocktallplant_enumtallflowervariants == BlockTallPlant.EnumTallFlowerVariants.GRASS;
      }
   }

   @Override
   protected void e(World world, BlockPosition blockposition, IBlockData iblockdata) {
      if (!this.f(world, blockposition, iblockdata)) {
         boolean flag = iblockdata.get(HALF) == BlockTallPlant.EnumTallPlantHalf.UPPER;
         BlockPosition blockposition1 = flag ? blockposition : blockposition.up();
         BlockPosition blockposition2 = flag ? blockposition.down() : blockposition;
         Object object = flag ? this : world.getType(blockposition1).getBlock();
         Object object1 = flag ? world.getType(blockposition2).getBlock() : this;
         if (object == this) {
            world.setTypeAndData(blockposition1, Blocks.AIR.getBlockData(), 2);
         }

         if (object1 == this) {
            world.setTypeAndData(blockposition2, Blocks.AIR.getBlockData(), 3);
            if (!flag) {
               this.b(world, blockposition2, iblockdata, 0);
            }
         }
      }
   }

   @Override
   public boolean f(World world, BlockPosition blockposition, IBlockData iblockdata) {
      if (iblockdata.get(HALF) == BlockTallPlant.EnumTallPlantHalf.UPPER) {
         return world.getType(blockposition.down()).getBlock() == this;
      } else {
         IBlockData iblockdata1 = world.getType(blockposition.up());
         return iblockdata1.getBlock() == this && super.f(world, blockposition, iblockdata1);
      }
   }

   @Override
   public Item getDropType(IBlockData iblockdata, Random random, int i) {
      if (iblockdata.get(HALF) == BlockTallPlant.EnumTallPlantHalf.UPPER) {
         return null;
      } else {
         BlockTallPlant.EnumTallFlowerVariants blocktallplant_enumtallflowervariants = iblockdata.get(VARIANT);
         return blocktallplant_enumtallflowervariants == BlockTallPlant.EnumTallFlowerVariants.FERN
            ? null
            : (
               blocktallplant_enumtallflowervariants == BlockTallPlant.EnumTallFlowerVariants.GRASS
                  ? (random.nextInt(8) == 0 ? Items.WHEAT_SEEDS : null)
                  : Item.getItemOf(this)
            );
      }
   }

   @Override
   public int getDropData(IBlockData iblockdata) {
      return iblockdata.get(HALF) != BlockTallPlant.EnumTallPlantHalf.UPPER && iblockdata.get(VARIANT) != BlockTallPlant.EnumTallFlowerVariants.GRASS
         ? iblockdata.get(VARIANT).a()
         : 0;
   }

   public void a(World world, BlockPosition blockposition, BlockTallPlant.EnumTallFlowerVariants blocktallplant_enumtallflowervariants, int i) {
      world.setTypeAndData(
         blockposition, this.getBlockData().set(HALF, BlockTallPlant.EnumTallPlantHalf.LOWER).set(VARIANT, blocktallplant_enumtallflowervariants), i
      );
      world.setTypeAndData(blockposition.up(), this.getBlockData().set(HALF, BlockTallPlant.EnumTallPlantHalf.UPPER), i);
   }

   @Override
   public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
      world.setTypeAndData(blockposition.up(), this.getBlockData().set(HALF, BlockTallPlant.EnumTallPlantHalf.UPPER), 2);
   }

   @Override
   public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, TileEntity tileentity) {
      if (entityhuman.bZ() == null
         || entityhuman.bZ().getItem() != Items.SHEARS
         || iblockdata.get(HALF) != BlockTallPlant.EnumTallPlantHalf.LOWER
         || !this.b(world, blockposition, iblockdata, entityhuman)) {
         super.a(world, entityhuman, blockposition, iblockdata, tileentity);
      }
   }

   @Override
   public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
      if (iblockdata.get(HALF) == BlockTallPlant.EnumTallPlantHalf.UPPER) {
         if (world.getType(blockposition.down()).getBlock() == this) {
            if (!entityhuman.abilities.canInstantlyBuild) {
               IBlockData iblockdata1 = world.getType(blockposition.down());
               BlockTallPlant.EnumTallFlowerVariants blocktallplant_enumtallflowervariants = iblockdata1.get(VARIANT);
               if (blocktallplant_enumtallflowervariants != BlockTallPlant.EnumTallFlowerVariants.FERN
                  && blocktallplant_enumtallflowervariants != BlockTallPlant.EnumTallFlowerVariants.GRASS) {
                  world.setAir(blockposition.down(), true);
               } else if (entityhuman.bZ() != null && entityhuman.bZ().getItem() == Items.SHEARS) {
                  this.b(world, blockposition, iblockdata1, entityhuman);
                  world.setAir(blockposition.down());
               } else {
                  world.setAir(blockposition.down(), true);
               }
            } else {
               world.setAir(blockposition.down());
            }
         }
      } else if (entityhuman.abilities.canInstantlyBuild && world.getType(blockposition.up()).getBlock() == this) {
         world.setTypeAndData(blockposition.up(), Blocks.AIR.getBlockData(), 2);
      }

      super.a(world, blockposition, iblockdata, entityhuman);
   }

   private boolean b(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
      BlockTallPlant.EnumTallFlowerVariants blocktallplant_enumtallflowervariants = iblockdata.get(VARIANT);
      if (blocktallplant_enumtallflowervariants != BlockTallPlant.EnumTallFlowerVariants.FERN
         && blocktallplant_enumtallflowervariants != BlockTallPlant.EnumTallFlowerVariants.GRASS) {
         return false;
      } else {
         int i = (blocktallplant_enumtallflowervariants == BlockTallPlant.EnumTallFlowerVariants.GRASS
               ? BlockLongGrass.EnumTallGrassType.GRASS
               : BlockLongGrass.EnumTallGrassType.FERN)
            .a();
         this.a(world, blockposition, new ItemStack(Blocks.TALLGRASS, 2, i));
         return true;
      }
   }

   @Override
   public int getDropData(World world, BlockPosition blockposition) {
      return this.e(world, blockposition).a();
   }

   @Override
   public boolean a(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
      BlockTallPlant.EnumTallFlowerVariants blocktallplant_enumtallflowervariants = this.e(world, blockposition);
      return blocktallplant_enumtallflowervariants != BlockTallPlant.EnumTallFlowerVariants.GRASS
         && blocktallplant_enumtallflowervariants != BlockTallPlant.EnumTallFlowerVariants.FERN;
   }

   @Override
   public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
      return true;
   }

   @Override
   public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
      this.a(world, blockposition, new ItemStack(this, 1, this.e(world, blockposition).a()));
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return (i & 8) > 0
         ? this.getBlockData().set(HALF, BlockTallPlant.EnumTallPlantHalf.UPPER)
         : this.getBlockData().set(HALF, BlockTallPlant.EnumTallPlantHalf.LOWER).set(VARIANT, BlockTallPlant.EnumTallFlowerVariants.a(i & 7));
   }

   @Override
   public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
      if (iblockdata.get(HALF) == BlockTallPlant.EnumTallPlantHalf.UPPER) {
         IBlockData iblockdata1 = iblockaccess.getType(blockposition.down());
         if (iblockdata1.getBlock() == this) {
            iblockdata = iblockdata.set(VARIANT, iblockdata1.get(VARIANT));
         }
      }

      return iblockdata;
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      return iblockdata.get(HALF) == BlockTallPlant.EnumTallPlantHalf.UPPER ? 8 | iblockdata.get(N).b() : iblockdata.get(VARIANT).a();
   }

   @Override
   protected BlockStateList getStateList() {
      return new BlockStateList(this, HALF, VARIANT, N);
   }

   public static enum EnumTallFlowerVariants implements INamable {
      SUNFLOWER(0, "sunflower"),
      SYRINGA(1, "syringa"),
      GRASS(2, "double_grass", "grass"),
      FERN(3, "double_fern", "fern"),
      ROSE(4, "double_rose", "rose"),
      PAEONIA(5, "paeonia");

      private static final BlockTallPlant.EnumTallFlowerVariants[] g = new BlockTallPlant.EnumTallFlowerVariants[values().length];
      private final int h;
      private final String i;
      private final String j;

      static {
         for (BlockTallPlant.EnumTallFlowerVariants blocktallplant_enumtallflowervariants : values()) {
            g[blocktallplant_enumtallflowervariants.a()] = blocktallplant_enumtallflowervariants;
         }
      }

      private EnumTallFlowerVariants(int i, String s) {
         this(i, s, s);
      }

      private EnumTallFlowerVariants(int i, String s, String s1) {
         this.h = i;
         this.i = s;
         this.j = s1;
      }

      public int a() {
         return this.h;
      }

      @Override
      public String toString() {
         return this.i;
      }

      public static BlockTallPlant.EnumTallFlowerVariants a(int i) {
         if (i < 0 || i >= g.length) {
            i = 0;
         }

         return g[i];
      }

      @Override
      public String getName() {
         return this.i;
      }

      public String c() {
         return this.j;
      }
   }

   public static enum EnumTallPlantHalf implements INamable {
      UPPER,
      LOWER;

      @Override
      public String toString() {
         return this.getName();
      }

      @Override
      public String getName() {
         return this == UPPER ? "upper" : "lower";
      }
   }
}
