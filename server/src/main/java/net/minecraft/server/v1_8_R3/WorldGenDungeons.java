package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenDungeons extends WorldGenerator {
   private static final Logger a = LogManager.getLogger();
   private static final String[] b = new String[]{"Skeleton", "Zombie", "Zombie", "Spider"};
   private static final List<StructurePieceTreasure> c = Lists.newArrayList(
      new StructurePieceTreasure[]{
         new StructurePieceTreasure(Items.SADDLE, 0, 1, 1, 10),
         new StructurePieceTreasure(Items.IRON_INGOT, 0, 1, 4, 10),
         new StructurePieceTreasure(Items.BREAD, 0, 1, 1, 10),
         new StructurePieceTreasure(Items.WHEAT, 0, 1, 4, 10),
         new StructurePieceTreasure(Items.GUNPOWDER, 0, 1, 4, 10),
         new StructurePieceTreasure(Items.STRING, 0, 1, 4, 10),
         new StructurePieceTreasure(Items.BUCKET, 0, 1, 1, 10),
         new StructurePieceTreasure(Items.GOLDEN_APPLE, 0, 1, 1, 1),
         new StructurePieceTreasure(Items.REDSTONE, 0, 1, 4, 10),
         new StructurePieceTreasure(Items.RECORD_13, 0, 1, 1, 4),
         new StructurePieceTreasure(Items.RECORD_CAT, 0, 1, 1, 4),
         new StructurePieceTreasure(Items.NAME_TAG, 0, 1, 1, 10),
         new StructurePieceTreasure(Items.GOLDEN_HORSE_ARMOR, 0, 1, 1, 2),
         new StructurePieceTreasure(Items.IRON_HORSE_ARMOR, 0, 1, 1, 5),
         new StructurePieceTreasure(Items.DIAMOND_HORSE_ARMOR, 0, 1, 1, 1)
      }
   );

   @Override
   public boolean generate(World world, Random random, BlockPosition blockposition) {
      int i = random.nextInt(2) + 2;
      int j = -i - 1;
      int k = i + 1;
      int l = random.nextInt(2) + 2;
      int i1 = -l - 1;
      int j1 = l + 1;
      int k1 = 0;

      for (int l1 = j; l1 <= k; l1++) {
         for (int i2 = -1; i2 <= 4; i2++) {
            for (int j2 = i1; j2 <= j1; j2++) {
               BlockPosition blockposition1 = blockposition.a(l1, i2, j2);
               Material material = world.getType(blockposition1).getBlock().getMaterial();
               boolean flag3 = material.isBuildable();
               if (i2 == -1 && !flag3) {
                  return false;
               }

               if (i2 == 4 && !flag3) {
                  return false;
               }

               if ((l1 == j || l1 == k || j2 == i1 || j2 == j1)
                  && i2 == 0
                  && world.isEmpty(blockposition1)
                  && world.isEmpty(blockposition1.getX(), blockposition1.getY() + 1, blockposition1.getZ())) {
                  k1++;
               }
            }
         }
      }

      if (k1 >= 1 && k1 <= 5) {
         for (int var21 = j; var21 <= k; var21++) {
            for (int i2 = 3; i2 >= -1; i2--) {
               for (int j2 = i1; j2 <= j1; j2++) {
                  BlockPosition blockposition1x = blockposition.a(var21, i2, j2);
                  if (var21 != j && i2 != -1 && j2 != i1 && var21 != k && i2 != 4 && j2 != j1) {
                     if (world.getType(blockposition1x).getBlock() != Blocks.CHEST) {
                        world.setAir(blockposition1x);
                     }
                  } else if (blockposition1x.getY() >= 0
                     && !world.getType(blockposition1x.getX(), blockposition1x.getY() - 1, blockposition1x.getZ()).getBlock().getMaterial().isBuildable()) {
                     world.setAir(blockposition1x);
                  } else if (world.getType(blockposition1x).getBlock().getMaterial().isBuildable() && world.getType(blockposition1x).getBlock() != Blocks.CHEST
                     )
                   {
                     if (i2 == -1 && random.nextInt(4) != 0) {
                        world.setTypeAndData(blockposition1x, Blocks.MOSSY_COBBLESTONE.getBlockData(), 2);
                     } else {
                        world.setTypeAndData(blockposition1x, Blocks.COBBLESTONE.getBlockData(), 2);
                     }
                  }
               }
            }
         }

         for (int var22 = 0; var22 < 2; var22++) {
            for (int i2 = 0; i2 < 3; i2++) {
               int j2x = blockposition.getX() + random.nextInt(i * 2 + 1) - i;
               int k2 = blockposition.getY();
               int l2 = blockposition.getZ() + random.nextInt(l * 2 + 1) - l;
               if (world.isEmpty(j2x, k2, l2)) {
                  BlockPosition blockposition2 = new BlockPosition(j2x, k2, l2);
                  int i3 = 0;

                  for (EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
                     if (world.getType(blockposition2.shift(enumdirection)).getBlock().getMaterial().isBuildable()) {
                        i3++;
                     }
                  }

                  if (i3 == 1) {
                     world.setTypeAndData(blockposition2, Blocks.CHEST.f(world, blockposition2, Blocks.CHEST.getBlockData()), 2);
                     List<StructurePieceTreasure> list = StructurePieceTreasure.a(c, Items.ENCHANTED_BOOK.b(random));
                     TileEntity tileentity = world.getTileEntity(blockposition2);
                     if (tileentity instanceof TileEntityChest) {
                        StructurePieceTreasure.a(random, list, (IInventory)tileentity, 8);
                     }
                     break;
                  }
               }
            }
         }

         world.setTypeAndData(blockposition, Blocks.MOB_SPAWNER.getBlockData(), 2);
         TileEntity tileentity1 = world.getTileEntity(blockposition);
         if (tileentity1 instanceof TileEntityMobSpawner) {
            ((TileEntityMobSpawner)tileentity1).getSpawner().setMobName(this.a(random));
         } else {
            a.error("Failed to fetch mob spawner entity at (" + blockposition.getX() + ", " + blockposition.getY() + ", " + blockposition.getZ() + ")");
         }

         return true;
      } else {
         return false;
      }
   }

   private String a(Random random) {
      return b[random.nextInt(b.length)];
   }
}
