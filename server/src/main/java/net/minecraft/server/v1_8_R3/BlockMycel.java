package net.minecraft.server.v1_8_R3;

import java.util.Random;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockSpreadEvent;

public class BlockMycel extends Block {
   public static final BlockStateBoolean SNOWY = BlockStateBoolean.of("snowy");

   protected BlockMycel() {
      super(Material.GRASS, MaterialMapColor.z);
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
   public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
      int lightLevel = world.getLightLevel(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ());
      if (lightLevel < 4 && world.getType(blockposition.getX(), blockposition.getY() + 1, blockposition.getZ()).getBlock().p() > 2) {
         org.bukkit.World bworld = world.getWorld();
         org.bukkit.block.BlockState blockState = bworld.getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()).getState();
         blockState.setType(CraftMagicNumbers.getMaterial(Blocks.DIRT));
         BlockFadeEvent event = new BlockFadeEvent(blockState.getBlock(), blockState);
         world.getServer().getPluginManager().callEvent(event);
         if (!event.isCancelled()) {
            blockState.update(true);
         }
      } else if (lightLevel >= 9) {
         for (int i = 0; i < Math.min(4, Math.max(20, (int)(400.0F / world.growthOdds))); i++) {
            BlockPosition blockposition1 = blockposition.a(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
            IBlockData iblockdata1 = world.getType(blockposition1);
            Block block = world.getType(blockposition1.getX(), blockposition1.getY() + 1, blockposition1.getZ()).getBlock();
            if (iblockdata1.getBlock() == Blocks.DIRT
               && iblockdata1.get(BlockDirt.VARIANT) == BlockDirt.EnumDirtVariant.DIRT
               && world.getLightLevel(blockposition1.getX(), blockposition1.getY() + 1, blockposition1.getZ()) >= 4
               && block.p() <= 2) {
               org.bukkit.World bworld = world.getWorld();
               org.bukkit.block.BlockState blockState = bworld.getBlockAt(blockposition1.getX(), blockposition1.getY(), blockposition1.getZ()).getState();
               blockState.setType(CraftMagicNumbers.getMaterial(this));
               BlockSpreadEvent event = new BlockSpreadEvent(
                  blockState.getBlock(), bworld.getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()), blockState
               );
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
   public int toLegacyData(IBlockData iblockdata) {
      return 0;
   }

   @Override
   public BlockStateList getStateList() {
      return new BlockStateList(this, SNOWY);
   }
}
