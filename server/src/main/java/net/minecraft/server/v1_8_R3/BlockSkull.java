package net.minecraft.server.v1_8_R3;

import com.google.common.base.Predicate;
import java.util.Random;
import org.bukkit.craftbukkit.v1_8_R3.util.BlockStateListPopulator;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class BlockSkull extends BlockContainer {
   public static final BlockStateDirection FACING = BlockStateDirection.of("facing");
   public static final BlockStateBoolean NODROP = BlockStateBoolean.of("nodrop");
   private static final Predicate<ShapeDetectorBlock> N = new Predicate() {
      public boolean a(ShapeDetectorBlock shapedetectorblock) {
         return shapedetectorblock.a() != null
            && shapedetectorblock.a().getBlock() == Blocks.SKULL
            && shapedetectorblock.b() instanceof TileEntitySkull
            && ((TileEntitySkull)shapedetectorblock.b()).getSkullType() == 1;
      }

      public boolean apply(Object object) {
         return this.a((ShapeDetectorBlock)object);
      }
   };
   private ShapeDetector O;
   private ShapeDetector P;

   protected BlockSkull() {
      super(Material.ORIENTABLE);
      this.j(this.blockStateList.getBlockData().set(FACING, EnumDirection.NORTH).set(NODROP, false));
      this.a(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
   }

   @Override
   public String getName() {
      return LocaleI18n.get("tile.skull.skeleton.name");
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
   public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
      switch (BlockSkull.SyntheticClass_1.a[iblockaccess.getType(blockposition).get(FACING).ordinal()]) {
         case 1:
         default:
            this.a(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
            break;
         case 2:
            this.a(0.25F, 0.25F, 0.5F, 0.75F, 0.75F, 1.0F);
            break;
         case 3:
            this.a(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.5F);
            break;
         case 4:
            this.a(0.5F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
            break;
         case 5:
            this.a(0.0F, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F);
      }
   }

   @Override
   public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
      this.updateShape(world, blockposition);
      return super.a(world, blockposition, iblockdata);
   }

   @Override
   public IBlockData getPlacedState(
      World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving
   ) {
      return this.getBlockData().set(FACING, entityliving.getDirection()).set(NODROP, false);
   }

   @Override
   public TileEntity a(World world, int i) {
      return new TileEntitySkull();
   }

   @Override
   public int getDropData(World world, BlockPosition blockposition) {
      TileEntity tileentity = world.getTileEntity(blockposition);
      return tileentity instanceof TileEntitySkull ? ((TileEntitySkull)tileentity).getSkullType() : super.getDropData(world, blockposition);
   }

   @Override
   public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {
      if (world.random.nextFloat() < f) {
         ItemStack itemstack = new ItemStack(Items.SKULL, 1, this.getDropData(world, blockposition));
         TileEntitySkull tileentityskull = (TileEntitySkull)world.getTileEntity(blockposition);
         if (tileentityskull.getSkullType() == 3 && tileentityskull.getGameProfile() != null) {
            itemstack.setTag(new NBTTagCompound());
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            GameProfileSerializer.serialize(nbttagcompound, tileentityskull.getGameProfile());
            itemstack.getTag().set("SkullOwner", nbttagcompound);
         }

         this.a(world, blockposition, itemstack);
      }
   }

   @Override
   public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
      if (entityhuman.abilities.canInstantlyBuild) {
         iblockdata = iblockdata.set(NODROP, true);
         world.setTypeAndData(blockposition, iblockdata, 4);
      }

      super.a(world, blockposition, iblockdata, entityhuman);
   }

   @Override
   public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
      super.remove(world, blockposition, iblockdata);
   }

   @Override
   public Item getDropType(IBlockData iblockdata, Random random, int i) {
      return Items.SKULL;
   }

   public boolean b(World world, BlockPosition blockposition, ItemStack itemstack) {
      return itemstack.getData() == 1
         && blockposition.getY() >= 2
         && world.getDifficulty() != EnumDifficulty.PEACEFUL
         && this.l().a(world, blockposition) != null;
   }

   public void a(World world, BlockPosition blockposition, TileEntitySkull tileentityskull) {
      if (!world.captureBlockStates) {
         if (tileentityskull.getSkullType() == 1 && blockposition.getY() >= 2 && world.getDifficulty() != EnumDifficulty.PEACEFUL) {
            ShapeDetector shapedetector = this.n();
            ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection = shapedetector.a(world, blockposition);
            if (shapedetector_shapedetectorcollection != null) {
               BlockStateListPopulator blockList = new BlockStateListPopulator(world.getWorld());

               for (int i = 0; i < 3; i++) {
                  ShapeDetectorBlock shapedetectorblock = shapedetector_shapedetectorcollection.a(i, 0, 0);
                  BlockPosition pos = shapedetectorblock.getPosition();
                  IBlockData data = shapedetectorblock.a().set(NODROP, true);
                  blockList.setTypeAndData(pos.getX(), pos.getY(), pos.getZ(), data.getBlock(), data.getBlock().toLegacyData(data), 2);
               }

               for (int var14 = 0; var14 < shapedetector.c(); var14++) {
                  for (int j = 0; j < shapedetector.b(); j++) {
                     ShapeDetectorBlock shapedetectorblock1 = shapedetector_shapedetectorcollection.a(var14, j, 0);
                     BlockPosition pos = shapedetectorblock1.getPosition();
                     blockList.setTypeAndData(pos.getX(), pos.getY(), pos.getZ(), Blocks.AIR, 0, 2);
                  }
               }

               BlockPosition blockposition1 = shapedetector_shapedetectorcollection.a(1, 0, 0).getPosition();
               EntityWither entitywither = new EntityWither(world);
               BlockPosition blockposition2 = shapedetector_shapedetectorcollection.a(1, 2, 0).getPosition();
               entitywither.setPositionRotation(
                  blockposition2.getX() + 0.5,
                  blockposition2.getY() + 0.55,
                  blockposition2.getZ() + 0.5,
                  shapedetector_shapedetectorcollection.b().k() == EnumDirection.EnumAxis.X ? 0.0F : 90.0F,
                  0.0F
               );
               entitywither.aI = shapedetector_shapedetectorcollection.b().k() == EnumDirection.EnumAxis.X ? 0.0F : 90.0F;
               entitywither.n();
               if (world.addEntity(entitywither, SpawnReason.BUILD_WITHER)) {
                  blockList.updateList();

                  for (int k = 0; k < 120; k++) {
                     world.addParticle(
                        EnumParticle.SNOWBALL,
                        blockposition1.getX() + world.random.nextDouble(),
                        blockposition1.getY() - 2 + world.random.nextDouble() * 3.9,
                        blockposition1.getZ() + world.random.nextDouble(),
                        0.0,
                        0.0,
                        0.0,
                        EnumParticle.EMPTY_ARRAY
                     );
                  }

                  for (int var21 = 0; var21 < shapedetector.c(); var21++) {
                     for (int l = 0; l < shapedetector.b(); l++) {
                        ShapeDetectorBlock shapedetectorblock2 = shapedetector_shapedetectorcollection.a(var21, l, 0);
                        world.update(shapedetectorblock2.getPosition(), Blocks.AIR);
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(FACING, EnumDirection.fromType1(i & 7)).set(NODROP, (i & 8) > 0);
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      byte b0 = 0;
      int i = b0 | iblockdata.get(FACING).a();
      if (iblockdata.get(NODROP)) {
         i |= 8;
      }

      return i;
   }

   @Override
   public BlockStateList getStateList() {
      return new BlockStateList(this, FACING, NODROP);
   }

   protected ShapeDetector l() {
      if (this.O == null) {
         this.O = ShapeDetectorBuilder.a()
            .a("   ", "###", "~#~")
            .a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.SOUL_SAND)))
            .a('~', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.AIR)))
            .b();
      }

      return this.O;
   }

   protected ShapeDetector n() {
      if (this.P == null) {
         this.P = ShapeDetectorBuilder.a()
            .a("^^^", "###", "~#~")
            .a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.SOUL_SAND)))
            .a('^', N)
            .a('~', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.AIR)))
            .b();
      }

      return this.P;
   }

   static class SyntheticClass_1 {
      static final int[] a = new int[EnumDirection.values().length];

      static {
         try {
            a[EnumDirection.UP.ordinal()] = 1;
         } catch (NoSuchFieldError var5) {
         }

         try {
            a[EnumDirection.NORTH.ordinal()] = 2;
         } catch (NoSuchFieldError var4) {
         }

         try {
            a[EnumDirection.SOUTH.ordinal()] = 3;
         } catch (NoSuchFieldError var3) {
         }

         try {
            a[EnumDirection.WEST.ordinal()] = 4;
         } catch (NoSuchFieldError var2) {
         }

         try {
            a[EnumDirection.EAST.ordinal()] = 5;
         } catch (NoSuchFieldError var1) {
         }
      }
   }
}
