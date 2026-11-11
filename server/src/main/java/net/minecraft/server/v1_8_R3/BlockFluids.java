package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.BlazeConfig;
import java.util.List;
import java.util.Random;

public abstract class BlockFluids extends Block {
   public static final BlockStateInteger LEVEL = BlockStateInteger.of("level", 0, 15);

   protected BlockFluids(Material material) {
      super(material);
      this.j(this.blockStateList.getBlockData().set(LEVEL, 0));
      this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      this.a(true);
   }

   @Override
   public void a(World world, BlockPosition blockposition, IBlockData iblockdata, AxisAlignedBB axisalignedbb, List<AxisAlignedBB> list, Entity entity) {
      if (!(entity instanceof EntityFallingBlock) && !(entity instanceof EntityTNTPrimed)) {
         super.a(world, blockposition, iblockdata, axisalignedbb, list, entity);
      }
   }

   @Override
   public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition) {
      return this.material != Material.LAVA;
   }

   public static float b(int i) {
      if (i >= 8) {
         i = 0;
      }

      return (i + 1) / 9.0F;
   }

   protected int e(IBlockAccess iblockaccess, int x, int y, int z) {
      IBlockData b = iblockaccess.getType(x, y, z);
      return b.getBlock().getMaterial() == this.material ? b.get(LEVEL) : -1;
   }

   protected int f(IBlockAccess iblockaccess, int x, int y, int z) {
      int i = this.e(iblockaccess, x, y, z);
      return i >= 8 ? 0 : i;
   }

   protected int e(IBlockData b) {
      return b.getBlock().getMaterial() == this.material ? b.get(LEVEL) : -1;
   }

   protected int f(IBlockData b) {
      int i = this.e(b);
      return i >= 8 ? 0 : i;
   }

   @Override
   public boolean d() {
      return false;
   }

   @Override
   public boolean c() {
      return false;
   }

   @Override
   public boolean a(IBlockData iblockdata, boolean flag) {
      return flag && iblockdata.get(LEVEL) == 0;
   }

   public boolean bA(IBlockAccess iblockaccess, int x, int y, int z, EnumDirection enumdirection) {
      Material material = iblockaccess.getType(x, y, z).getBlock().getMaterial();
      return material != this.material && (enumdirection == EnumDirection.UP || material != Material.ICE && material.isBuildable());
   }

   @Override
   public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
      return null;
   }

   @Override
   public int b() {
      return 1;
   }

   @Override
   public Item getDropType(IBlockData iblockdata, Random random, int i) {
      return null;
   }

   @Override
   public int a(Random random) {
      return 0;
   }

   protected Vec3D h(IBlockAccess iblockaccess, BlockPosition blockposition) {
      Vec3D.MutableVec3D mutableVec3D = new Vec3D.MutableVec3D();
      int bX = blockposition.getX();
      int bY = blockposition.getY();
      int bZ = blockposition.getZ();
      int i = this.f(iblockaccess, bX, bY, bZ);

      for (EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
         int x = bX + enumdirection.getAdjacentX();
         int y = bY + enumdirection.getAdjacentY();
         int z = bZ + enumdirection.getAdjacentZ();
         int j = this.f(iblockaccess, x, y, z);
         if (j < 0) {
            if (!iblockaccess.getType(x, y, z).getBlock().getMaterial().isSolid()) {
               j = this.f(iblockaccess, x, y - 1, z);
               if (j >= 0) {
                  int k = j - (i - 8);
                  mutableVec3D.add((double)((x - bX) * k), (double)((y - bY) * k), (double)((z - bZ) * k));
               }
            }
         } else {
            int k = j - i;
            mutableVec3D.add((double)((x - bX) * k), (double)((y - bY) * k), (double)((z - bZ) * k));
         }
      }

      if (iblockaccess.getType(bX, bY, bZ).get(LEVEL) >= 8) {
         for (EnumDirection enumdirectionx : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
            int x = bX + enumdirectionx.getAdjacentX();
            int y = bY + enumdirectionx.getAdjacentY();
            int z = bZ + enumdirectionx.getAdjacentZ();
            if (this.bA(iblockaccess, x, y, z, enumdirectionx) || this.bA(iblockaccess, x, y + 1, z, enumdirectionx)) {
               mutableVec3D.a().add(0.0, -6.0, 0.0);
               break;
            }
         }
      }

      return mutableVec3D.a();
   }

   @Override
   public Vec3D a(World world, BlockPosition blockposition, Entity entity, Vec3D vec3d) {
      return vec3d.e(this.h(world, blockposition));
   }

   @Override
   public int a(World world) {
      return this.material == Material.WATER ? 5 : (this.material == Material.LAVA ? (world.worldProvider.o() ? 10 : 30) : 0);
   }

   @Override
   public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
      this.e(world, blockposition, iblockdata);
   }

   @Override
   public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
      this.e(world, blockposition, iblockdata);
   }

   public boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
      if (this.material == Material.LAVA) {
         boolean flag = false;
         int x = blockposition.getX();
         int y = blockposition.getY();
         int z = blockposition.getZ();

         EnumDirection[] var11;
         for (EnumDirection enumdirection : var11 = EnumDirection.values()) {
            if (enumdirection != EnumDirection.DOWN
               && world.getType(x + enumdirection.getAdjacentX(), y + enumdirection.getAdjacentY(), z + enumdirection.getAdjacentZ()).getBlock().getMaterial()
                  == Material.WATER) {
               flag = true;
               break;
            }
         }

         if (flag) {
            int integer = iblockdata.get(LEVEL);
            if (integer >= 0) {
               if (integer == 0 && (Boolean)BlazeConfig.obsidianGenerating.getValue()) {
                  world.setTypeUpdate(blockposition, Blocks.OBSIDIAN.getBlockData());
               } else if ((Boolean)BlazeConfig.cobbleGenerating.getValue()) {
                  world.setTypeUpdate(blockposition, Blocks.COBBLESTONE.getBlockData());
               }

               this.fizz(world, blockposition);
               return true;
            }
         }
      }

      return false;
   }

   protected void fizz(World world, BlockPosition blockposition) {
      this.fizz(world, blockposition.getX(), blockposition.getY(), blockposition.getZ());
   }

   protected void fizz(World world, int d0, int d1, int d2) {
      if ((Boolean)BlazeConfig.fluidFizzSounds.getValue()) {
         world.makeSound(d0 + 0.5, d1 + 0.5, d2 + 0.5, "random.fizz", 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

         for (int i = 0; i < 8; i++) {
            world.addParticle(EnumParticle.SMOKE_LARGE, d0 + Math.random(), d1 + 1.2, d2 + Math.random(), 0.0, 0.0, 0.0, EnumParticle.EMPTY_ARRAY);
         }
      }
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

   public static BlockFlowing a(Material material) {
      if (material == Material.WATER) {
         return Blocks.FLOWING_WATER;
      } else if (material == Material.LAVA) {
         return Blocks.FLOWING_LAVA;
      } else {
         throw new IllegalArgumentException("Invalid material");
      }
   }

   public static BlockStationary b(Material material) {
      if (material == Material.WATER) {
         return Blocks.WATER;
      } else if (material == Material.LAVA) {
         return Blocks.LAVA;
      } else {
         throw new IllegalArgumentException("Invalid material");
      }
   }
}
