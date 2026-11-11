package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.BlazeConfig;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.event.world.StructureGrowEvent;

public class BlockSapling extends BlockPlant implements IBlockFragilePlantElement {
   public static final BlockStateEnum<BlockWood.EnumLogVariant> TYPE = BlockStateEnum.of("type", BlockWood.EnumLogVariant.class);
   public static final BlockStateInteger STAGE = BlockStateInteger.of("stage", 0, 1);
   public static TreeType treeType;

   protected BlockSapling() {
      this.j(this.blockStateList.getBlockData().set(TYPE, BlockWood.EnumLogVariant.OAK).set(STAGE, 0));
      float f = 0.4F;
      this.a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
      this.a(CreativeModeTab.c);
   }

   @Override
   public String getName() {
      return LocaleI18n.get(this.a() + "." + BlockWood.EnumLogVariant.OAK.d() + ".name");
   }

   @Override
   public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
      if ((Boolean)BlazeConfig.dropFloatingPlants.getValue()) {
         super.b(world, blockposition, iblockdata, random);
      } else if (!this.f(world, blockposition, iblockdata)) {
         return;
      }

      if (world.getLightLevel(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ()) >= 9
         && random.nextInt(Math.max(2, (int)(world.growthOdds / world.spigotConfig.saplingModifier * 7.0F + 0.5F))) == 0) {
         world.captureTreeGeneration = true;
         this.grow(world, blockposition, iblockdata, random);
         world.captureTreeGeneration = false;
         if (!world.capturedBlockStates.isEmpty()) {
            TreeType treeType = BlockSapling.treeType;
            BlockSapling.treeType = null;
            Location location = new Location(world.getWorld(), blockposition.getX(), blockposition.getY(), blockposition.getZ());
            List<org.bukkit.block.BlockState> blocks = (List<org.bukkit.block.BlockState>)world.capturedBlockStates.clone();
            world.capturedBlockStates.clear();
            StructureGrowEvent event = null;
            if (treeType != null) {
               event = new StructureGrowEvent(location, treeType, false, null, blocks);
               Bukkit.getPluginManager().callEvent(event);
            }

            if (event == null || !event.isCancelled()) {
               for (org.bukkit.block.BlockState blockstate : blocks) {
                  blockstate.update(true);
               }
            }
         }
      }
   }

   public void grow(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
      if (iblockdata.get(STAGE) == 0) {
         world.setTypeAndData(blockposition, iblockdata.a(STAGE), 4);
      } else {
         this.e(world, blockposition, iblockdata, random);
      }
   }

   public void e(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
      Object object;
      if (random.nextInt(10) == 0) {
         treeType = TreeType.BIG_TREE;
         object = new WorldGenBigTree(true);
      } else {
         treeType = TreeType.TREE;
         object = new WorldGenTrees(true);
      }

      int i = 0;
      int j = 0;
      boolean flag = false;
      switch (BlockSapling.SyntheticClass_1.a[iblockdata.get(TYPE).ordinal()]) {
         case 1:
            label68:
            for (i = 0; i >= -1; i--) {
               for (j = 0; j >= -1; j--) {
                  if (this.a(world, blockposition, i, j, BlockWood.EnumLogVariant.SPRUCE)) {
                     treeType = TreeType.MEGA_REDWOOD;
                     object = new WorldGenMegaTree(false, random.nextBoolean());
                     flag = true;
                     break label68;
                  }
               }
            }

            if (!flag) {
               j = 0;
               i = 0;
               treeType = TreeType.REDWOOD;
               object = new WorldGenTaiga2(true);
            }
            break;
         case 2:
            treeType = TreeType.BIRCH;
            object = new WorldGenForest(true, false);
            break;
         case 3:
            IBlockData iblockdata1 = Blocks.LOG.getBlockData().set(BlockLog1.VARIANT, BlockWood.EnumLogVariant.JUNGLE);
            IBlockData iblockdata2 = Blocks.LEAVES
               .getBlockData()
               .set(BlockLeaves1.VARIANT, BlockWood.EnumLogVariant.JUNGLE)
               .set(BlockLeaves.CHECK_DECAY, false);

            label82:
            for (i = 0; i >= -1; i--) {
               for (j = 0; j >= -1; j--) {
                  if (this.a(world, blockposition, i, j, BlockWood.EnumLogVariant.JUNGLE)) {
                     treeType = TreeType.JUNGLE;
                     object = new WorldGenJungleTree(true, 10, 20, iblockdata1, iblockdata2);
                     flag = true;
                     break label82;
                  }
               }
            }

            if (!flag) {
               j = 0;
               i = 0;
               treeType = TreeType.SMALL_JUNGLE;
               object = new WorldGenTrees(true, 4 + random.nextInt(7), iblockdata1, iblockdata2, false);
            }
            break;
         case 4:
            treeType = TreeType.ACACIA;
            object = new WorldGenAcaciaTree(true);
            break;
         case 5:
            label96:
            for (i = 0; i >= -1; i--) {
               for (j = 0; j >= -1; j--) {
                  if (this.a(world, blockposition, i, j, BlockWood.EnumLogVariant.DARK_OAK)) {
                     treeType = TreeType.DARK_OAK;
                     object = new WorldGenForestTree(true);
                     flag = true;
                     break label96;
                  }
               }
            }

            if (!flag) {
               return;
            }
         case 6:
      }

      IBlockData iblockdata1 = Blocks.AIR.getBlockData();
      if (flag) {
         world.setTypeAndData(blockposition.a(i, 0, j), iblockdata1, 4);
         world.setTypeAndData(blockposition.a(i + 1, 0, j), iblockdata1, 4);
         world.setTypeAndData(blockposition.a(i, 0, j + 1), iblockdata1, 4);
         world.setTypeAndData(blockposition.a(i + 1, 0, j + 1), iblockdata1, 4);
      } else {
         world.setTypeAndData(blockposition, iblockdata1, 4);
      }

      if (!((WorldGenerator)object).generate(world, random, blockposition.a(i, 0, j))) {
         if (flag) {
            world.setTypeAndData(blockposition.a(i, 0, j), iblockdata, 4);
            world.setTypeAndData(blockposition.a(i + 1, 0, j), iblockdata, 4);
            world.setTypeAndData(blockposition.a(i, 0, j + 1), iblockdata, 4);
            world.setTypeAndData(blockposition.a(i + 1, 0, j + 1), iblockdata, 4);
         } else {
            world.setTypeAndData(blockposition, iblockdata, 4);
         }
      }
   }

   private boolean a(World world, BlockPosition blockposition, int i, int j, BlockWood.EnumLogVariant blockwood_enumlogvariant) {
      return this.a(world, blockposition.a(i, 0, j), blockwood_enumlogvariant)
         && this.a(world, blockposition.a(i + 1, 0, j), blockwood_enumlogvariant)
         && this.a(world, blockposition.a(i, 0, j + 1), blockwood_enumlogvariant)
         && this.a(world, blockposition.a(i + 1, 0, j + 1), blockwood_enumlogvariant);
   }

   public boolean a(World world, BlockPosition blockposition, BlockWood.EnumLogVariant blockwood_enumlogvariant) {
      IBlockData iblockdata = world.getType(blockposition);
      return iblockdata.getBlock() == this && iblockdata.get(TYPE) == blockwood_enumlogvariant;
   }

   @Override
   public int getDropData(IBlockData iblockdata) {
      return iblockdata.get(TYPE).a();
   }

   @Override
   public boolean a(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
      return true;
   }

   @Override
   public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
      return world.random.nextFloat() < 0.45;
   }

   @Override
   public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
      this.grow(world, blockposition, iblockdata, random);
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(TYPE, BlockWood.EnumLogVariant.a(i & 7)).set(STAGE, (i & 8) >> 3);
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      byte b0 = 0;
      int i = b0 | iblockdata.get(TYPE).a();
      return i | iblockdata.get(STAGE) << 3;
   }

   @Override
   public BlockStateList getStateList() {
      return new BlockStateList(this, TYPE, STAGE);
   }

   static class SyntheticClass_1 {
      static final int[] a = new int[BlockWood.EnumLogVariant.values().length];

      static {
         try {
            a[BlockWood.EnumLogVariant.SPRUCE.ordinal()] = 1;
         } catch (NoSuchFieldError var6) {
         }

         try {
            a[BlockWood.EnumLogVariant.BIRCH.ordinal()] = 2;
         } catch (NoSuchFieldError var5) {
         }

         try {
            a[BlockWood.EnumLogVariant.JUNGLE.ordinal()] = 3;
         } catch (NoSuchFieldError var4) {
         }

         try {
            a[BlockWood.EnumLogVariant.ACACIA.ordinal()] = 4;
         } catch (NoSuchFieldError var3) {
         }

         try {
            a[BlockWood.EnumLogVariant.DARK_OAK.ordinal()] = 5;
         } catch (NoSuchFieldError var2) {
         }

         try {
            a[BlockWood.EnumLogVariant.OAK.ordinal()] = 6;
         } catch (NoSuchFieldError var1) {
         }
      }
   }
}
