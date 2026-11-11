package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import lol.deyo.blaze.configs.BlazeConfig;
import lol.deyo.blaze.utils.Utils;
import java.util.ArrayList;
import java.util.LinkedList;

public class BlockSponge extends Block {
   public static final BlockStateBoolean WET = BlockStateBoolean.of("wet");

   protected BlockSponge() {
      super(Material.SPONGE);
      this.j(this.blockStateList.getBlockData().set(WET, false));
      this.a(CreativeModeTab.b);
   }

   @Override
   public String getName() {
      return LocaleI18n.get(this.a() + ".dry.name");
   }

   @Override
   public int getDropData(IBlockData iblockdata) {
      return iblockdata.get(WET) ? 1 : 0;
   }

   @Override
   public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
      this.e(world, blockposition, iblockdata);
   }

   @Override
   public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
      this.e(world, blockposition, iblockdata);
      super.doPhysics(world, blockposition, iblockdata, block);
   }

   protected void e(World world, BlockPosition blockposition, IBlockData iblockdata) {
      if (!iblockdata.get(WET) && this.e(world, blockposition)) {
         world.setTypeAndData(blockposition, iblockdata.set(WET, Boolean.TRUE), 2);
         world.triggerEffect(2001, blockposition, Block.getId(Blocks.WATER));
      }
   }

   @Override
   public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
      super.remove(world, blockposition, iblockdata);
      int radius = (Integer)BlazeConfig.spongeRadius.getValue();
      if (radius > 0 && (Boolean)BlazeConfig.spongePreventWaterFlow.getValue()) {
         for (int searchX = -radius; searchX <= radius; searchX++) {
            for (int searchY = -radius; searchY <= radius; searchY++) {
               for (int searchZ = -radius; searchZ <= radius; searchZ++) {
                  world.applyPhysics(blockposition.a(searchX, searchY, searchZ), Blocks.AIR);
               }
            }
         }
      }
   }

   private boolean e(World world, BlockPosition blockposition) {
      int radius = (Integer)BlazeConfig.spongeRadius.getValue();
      if (radius > 0) {
         boolean found = false;
         Chunk chunk = null;

         for (int searchX = -radius; searchX <= radius; searchX++) {
            for (int searchY = -radius; searchY <= radius; searchY++) {
               for (int searchZ = -radius; searchZ <= radius; searchZ++) {
                  BlockPosition pos = blockposition.a(searchX, searchY, searchZ);
                  int chunkX = pos.getX() >> 4;
                  int chunkZ = pos.getZ() >> 4;
                  if (Utils.isInvalidChunk(chunk, chunkX, chunkZ)) {
                     chunk = world.getChunkAt(chunkX, chunkZ);
                  }

                  if (world.getType(chunk, pos).getBlock().getMaterial() == Material.WATER) {
                     world.setTypeAndData(chunk, pos, Blocks.AIR.getBlockData(), 2, true, false);
                     found = true;
                  }
               }
            }
         }

         return found;
      } else {
         LinkedList<Tuple<BlockPosition, Integer>> linkedlist = Lists.newLinkedList();
         ArrayList<BlockPosition> arraylist = Lists.newArrayList();
         linkedlist.add(new Tuple<>(blockposition, 0));
         int i = 0;

         while (!linkedlist.isEmpty()) {
            Tuple<BlockPosition, Integer> tuple = linkedlist.poll();
            BlockPosition blockposition1 = tuple.a();
            int j = tuple.b();

            EnumDirection[] var13;
            for (EnumDirection enumdirection : var13 = EnumDirection.values()) {
               BlockPosition blockposition2 = blockposition1.shift(enumdirection);
               if (world.getType(blockposition2).getBlock().getMaterial() == Material.WATER) {
                  world.setTypeAndData(blockposition2, Blocks.AIR.getBlockData(), 2);
                  arraylist.add(blockposition2);
                  i++;
                  if (j < 6) {
                     linkedlist.add(new Tuple<>(blockposition2, j + 1));
                  }
               }
            }

            if (i > 64) {
               break;
            }
         }

         for (BlockPosition blockPosition : arraylist) {
            world.applyPhysics(blockPosition, Blocks.AIR);
         }

         return i > 0;
      }
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(WET, (i & 1) == 1);
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      return iblockdata.get(WET) ? 1 : 0;
   }

   @Override
   protected BlockStateList getStateList() {
      return new BlockStateList(this, WET);
   }
}
