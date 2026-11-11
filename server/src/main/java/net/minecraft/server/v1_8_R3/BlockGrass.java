package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.BlazeConfig;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockSpreadEvent;

public class BlockGrass extends Block implements IBlockFragilePlantElement {
   public static final BlockStateBoolean SNOWY = BlockStateBoolean.of("snowy");

   protected BlockGrass() {
      super(Material.GRASS);
      this.j(this.blockStateList.getBlockData().set(SNOWY, false));
      this.a(true);
      this.a(CreativeModeTab.b);
   }

   @Override
   public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
      Block block = iblockaccess.getType(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ()).getBlock();
      return iblockdata.set(SNOWY, block == Blocks.SNOW || block == Blocks.SNOW_LAYER);
   }

   @Override
   public void optimizedTick(Chunk chunk, int x, int y, int z, IBlockData iblockdata, Random random) {
      int lightLevel = 0;
      boolean grassSpread = (Boolean)BlazeConfig.grassSpread.getValue();
      boolean grassSpreadLight = (Boolean)BlazeConfig.grassSpreadLight.getValue();
      if (grassSpread || grassSpreadLight) {
         lightLevel = chunk.getLightLevelWithNeighbours(x & 15, y + 1, z & 15);
      }

      if (grassSpread && lightLevel < 4 && chunk.getTypeAbs(x, y + 1, z).p() > 2) {
         org.bukkit.World bworld = chunk.world.getWorld();
         org.bukkit.block.BlockState blockState = bworld.getBlockAt(x, y, z).getState();
         blockState.setType(CraftMagicNumbers.getMaterial(Blocks.DIRT));
         BlockFadeEvent event = new BlockFadeEvent(blockState.getBlock(), blockState);
         Bukkit.getServer().getPluginManager().callEvent(event);
         if (!event.isCancelled()) {
            blockState.update(true);
         }
      } else if (!grassSpreadLight || lightLevel >= 9) {
         int growthIterations = Math.min(4, Math.max(20, (int)(400.0F / chunk.world.growthOdds)));
         Chunk cache = null;

         for (int i = 0; i < growthIterations; i++) {
            int offsetX = random.nextInt(3) - 1;
            int offsetY = random.nextInt(5) - 3;
            int offsetZ = random.nextInt(3) - 1;
            int chunkX = x + offsetX >> 4;
            int chunkZ = z + offsetZ >> 4;
            Chunk fetch;
            if (cache != null && cache.locX == chunkX && cache.locZ == chunkZ) {
               fetch = cache;
            } else {
               fetch = chunk.world.getChunkAt(chunkX, chunkZ);
               cache = fetch;
            }

            IBlockData iblockdata1 = fetch.getBlockData(x + offsetX, y + offsetY, z + offsetZ);
            if (iblockdata1.getBlock() == Blocks.DIRT
               && iblockdata1.get(BlockDirt.VARIANT) == BlockDirt.EnumDirtVariant.DIRT
               && fetch.getLightLevelWithNeighbours((x & 15) + offsetX, y + offsetY + 1, (z & 15) + offsetZ) >= 4
               && fetch.getTypePossibleOutOfBounds((x & 15) + offsetX, y + offsetY + 1, (z & 15) + offsetZ).p() <= 2) {
               org.bukkit.World bworld = chunk.world.getWorld();
               org.bukkit.block.BlockState blockState = bworld.getBlockAt(x + offsetX, y + offsetY, z + offsetZ).getState();
               blockState.setType(CraftMagicNumbers.getMaterial(Blocks.GRASS));
               BlockSpreadEvent event = new BlockSpreadEvent(blockState.getBlock(), bworld.getBlockAt(x + offsetX, y + offsetY, z + offsetZ), blockState);
               Bukkit.getServer().getPluginManager().callEvent(event);
               if (!event.isCancelled()) {
                  blockState.update(true);
               }
            }
         }
      }
   }

   @Override
   public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
      int x = blockposition.getX();
      int y = blockposition.getY();
      int z = blockposition.getZ();
      int lightLevel = 0;
      if ((Boolean)BlazeConfig.grassSpread.getValue() || (Boolean)BlazeConfig.grassSpreadLight.getValue()) {
         lightLevel = world.getLightLevel(x, y + 1, z);
      }

      if ((Boolean)BlazeConfig.grassSpread.getValue() && lightLevel < 4 && world.getType(x, y + 1, z).getBlock().p() > 2) {
         org.bukkit.World bworld = world.getWorld();
         org.bukkit.block.BlockState blockState = bworld.getBlockAt(x, y, z).getState();
         blockState.setType(CraftMagicNumbers.getMaterial(Blocks.DIRT));
         BlockFadeEvent event = new BlockFadeEvent(blockState.getBlock(), blockState);
         world.getServer().getPluginManager().callEvent(event);
         if (!event.isCancelled()) {
            blockState.update(true);
         }
      } else if (!(Boolean)BlazeConfig.grassSpreadLight.getValue() || lightLevel >= 9) {
         for (int i = 0; i < Math.min(4, Math.max(20, (int)(400.0F / world.growthOdds))); i++) {
            BlockPosition blockposition1 = blockposition.a(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
            Block block = world.getType(blockposition1.getX(), blockposition1.getY() + 1, blockposition1.getZ()).getBlock();
            IBlockData iblockdata1 = world.getType(blockposition1);
            if (iblockdata1.getBlock() == Blocks.DIRT
               && iblockdata1.get(BlockDirt.VARIANT) == BlockDirt.EnumDirtVariant.DIRT
               && world.getLightLevel(blockposition1.getX(), blockposition1.getY() + 1, blockposition1.getZ()) >= 4
               && block.p() <= 2) {
               org.bukkit.World bworld = world.getWorld();
               org.bukkit.block.BlockState blockState = bworld.getBlockAt(blockposition1.getX(), blockposition1.getY(), blockposition1.getZ()).getState();
               blockState.setType(CraftMagicNumbers.getMaterial(Blocks.GRASS));
               BlockSpreadEvent event = new BlockSpreadEvent(blockState.getBlock(), bworld.getBlockAt(x, y, z), blockState);
               world.getServer().getPluginManager().callEvent(event);
               if (!event.isCancelled()) {
                  blockState.update(true);
               }
            }
         }
      }
   }

   @Override
   public Item getDropType(IBlockData iblockdata, Random random, int i) {
      return Blocks.DIRT.getDropType(Blocks.DIRT.getBlockData().set(BlockDirt.VARIANT, BlockDirt.EnumDirtVariant.DIRT), random, i);
   }

   @Override
   public boolean a(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
      return true;
   }

   @Override
   public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
      return true;
   }

   @Override
   public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
      BlockPosition blockposition1 = blockposition.up();

      label35:
      for (int i = 0; i < 128; i++) {
         BlockPosition blockposition2 = blockposition1;

         for (int j = 0; j < i / 16; j++) {
            blockposition2 = blockposition2.a(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
            if (world.getType(blockposition2.getX(), blockposition2.getY() - 1, blockposition2.getZ()).getBlock() != Blocks.GRASS
               || world.getType(blockposition2).getBlock().isOccluding()) {
               continue label35;
            }
         }

         if (world.getType(blockposition2).getBlock().material == Material.AIR) {
            if (random.nextInt(8) == 0) {
               BlockFlowers.EnumFlowerVarient blockflowers_enumflowervarient = world.getBiome(blockposition2).a(random, blockposition2);
               BlockFlowers blockflowers = blockflowers_enumflowervarient.a().a();
               IBlockData iblockdata1 = blockflowers.getBlockData().set(blockflowers.n(), blockflowers_enumflowervarient);
               if (blockflowers.f(world, blockposition2, iblockdata1)) {
                  CraftEventFactory.handleBlockGrowEvent(
                     world,
                     blockposition2.getX(),
                     blockposition2.getY(),
                     blockposition2.getZ(),
                     iblockdata1.getBlock(),
                     iblockdata1.getBlock().toLegacyData(iblockdata1)
                  );
               }
            } else {
               IBlockData iblockdata2 = Blocks.TALLGRASS.getBlockData().set(BlockLongGrass.TYPE, BlockLongGrass.EnumTallGrassType.GRASS);
               if (Blocks.TALLGRASS.f(world, blockposition2, iblockdata2)) {
                  CraftEventFactory.handleBlockGrowEvent(
                     world,
                     blockposition2.getX(),
                     blockposition2.getY(),
                     blockposition2.getZ(),
                     iblockdata2.getBlock(),
                     iblockdata2.getBlock().toLegacyData(iblockdata2)
                  );
               }
            }
         }
      }
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      return 0;
   }

   @Override
   public BlockStateList getStateList() {
      return new BlockStateList(this, SNOWY);
   }
}
