package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.BlazeConfig;
import java.util.Random;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
import org.bukkit.event.block.BlockFromToEvent;

public class BlockFlowing extends BlockFluids {
   int a;

   protected BlockFlowing(Material material) {
      super(material);
   }

   private void f(World world, BlockPosition blockposition, IBlockData iblockdata) {
      world.setTypeAndData(blockposition, b(this.material).getBlockData().set(LEVEL, iblockdata.get(LEVEL)), 2);
   }

   @Override
   public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
      int x = blockposition.getX();
      int y = blockposition.getY();
      int z = blockposition.getZ();
      org.bukkit.block.Block source = world.getWorld().getBlockAt(x, y, z);
      int i = iblockdata.get(LEVEL);
      byte b0 = 1;
      if (this.material == Material.LAVA && !world.worldProvider.n()) {
         b0 = 2;
      }

      BlockPosition pos = new BlockPosition(x, y - 1, z);
      IBlockData iblockdata1 = world.getType(x, y - 1, z);
      int j = this.getFlowSpeed(world, x, y, z);
      if (i > 0) {
         int l = -100;
         this.a = 0;

         for (EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
            l = this.a(world, x + enumdirection.getAdjacentX(), y, z + enumdirection.getAdjacentZ(), l);
         }

         int i1 = l + b0;
         if (i1 >= 8 || l < 0) {
            i1 = -1;
         }

         int eInt = this.e(world, x, y + 1, z);
         if (eInt >= 0) {
            if (eInt >= 8) {
               i1 = eInt;
            } else {
               i1 = eInt + 8;
            }
         }

         if ((Boolean)BlazeConfig.infiniteWaterSources.getValue() && this.a >= 2 && this.material == Material.WATER) {
            Block block = iblockdata1.getBlock();
            if (block.getMaterial().isBuildable()) {
               i1 = 0;
            } else if (block.getMaterial() == this.material && iblockdata1.get(LEVEL) == 0) {
               i1 = 0;
            }
         }

         if (!world.paperSpigotConfig.fastDrainLava && this.material == Material.LAVA && i1 < 8 && i1 > i && random.nextInt(4) != 0) {
            j *= 4;
         }

         if (i1 == i) {
            this.f(world, blockposition, iblockdata);
         } else {
            i = i1;
            if (i1 >= 0 && !this.canFastDrain(world, x, y, z)) {
               iblockdata = iblockdata.set(LEVEL, i1);
               world.setTypeAndData(blockposition, iblockdata, 2);
               world.a(blockposition, this, j);
               world.d(x - 1, y, z, this);
               world.d(x + 1, y, z, this);
               world.d(x, y + 1, z, this);
               world.d(x, y, z - 1, this);
               world.d(x, y, z + 1, this);
            } else {
               world.setAir(blockposition);
            }
         }
      } else {
         this.f(world, blockposition, iblockdata);
      }

      if (world.getType(x, y, z).getBlock().getMaterial() == this.material) {
         IBlockData iblockdata2 = world.getType(x, y - 1, z);
         if (this.h(iblockdata2.getBlock())) {
            if (this.checkSponge(source)) {
               return;
            }

            BlockFromToEvent event = new BlockFromToEvent(source, BlockFace.DOWN);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
               if ((Boolean)BlazeConfig.stoneGenerating.getValue()
                  && this.material == Material.LAVA
                  && world.getType(x, y - 1, z).getBlock().getMaterial() == Material.WATER) {
                  world.setTypeUpdate(pos, Blocks.STONE.getBlockData());
                  this.fizz(world, x, y - 1, z);
                  return;
               }

               if (i >= 8) {
                  this.flow(world, pos, iblockdata2, i);
               } else {
                  this.flow(world, pos, iblockdata2, i + 8);
               }
            }
         } else if (i >= 0 && (i == 0 || this.g(iblockdata2.getBlock()))) {
            Set<EnumDirection> set = this.f(world, x, y, z);
            int k = i + b0;
            if (i >= 8) {
               k = 1;
            }

            if (k >= 8) {
               return;
            }

            for (EnumDirection enumdirection1 : set) {
               BlockFace blockFace = CraftBlock.notchToBlockFace(enumdirection1);
               if (this.checkSponge(source)) {
                  return;
               }

               BlockFromToEvent event = new BlockFromToEvent(source, blockFace);
               Bukkit.getPluginManager().callEvent(event);
               if (!event.isCancelled()) {
                  BlockPosition shifted = blockposition.shift(enumdirection1);
                  this.flow(world, shifted, world.getType(shifted), k);
               }
            }
         }
      }
   }

   public boolean checkSponge(org.bukkit.block.Block source) {
      int radius = (Integer)BlazeConfig.spongeRadius.getValue();
      if (radius > 0 && (Boolean)BlazeConfig.spongePreventWaterFlow.getValue()) {
         for (int searchX = -(++radius); searchX <= radius; searchX++) {
            for (int searchY = -radius; searchY <= radius; searchY++) {
               for (int searchZ = -radius; searchZ <= radius; searchZ++) {
                  if (source.getRelative(searchX, searchY, searchZ).getType() == org.bukkit.Material.SPONGE) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   private void flow(World world, BlockPosition blockposition, IBlockData iblockdata, int i) {
      int x = blockposition.getX();
      int y = blockposition.getY();
      int z = blockposition.getZ();
      Block block = iblockdata.getBlock();
      if (world.isLoaded(x, y, z) && this.h(block)) {
         if (block != Blocks.AIR && this.material != Material.LAVA) {
            block.b(world, blockposition, iblockdata, 0);
         }

         world.setTypeAndData(blockposition, this.getBlockData().set(LEVEL, i), 3, false, false);
      }
   }

   private int a(World world, int x, int y, int z, int i, EnumDirection enumdirection) {
      int j = 1000;

      for (EnumDirection enumdirection1 : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
         if (enumdirection1 != enumdirection) {
            int x1 = x + enumdirection.getAdjacentX();
            int y1 = y + enumdirection.getAdjacentY();
            int z1 = z + enumdirection.getAdjacentZ();
            IBlockData iblockdata = world.getType(x1, y1, z1);
            Block block = iblockdata.getBlock();
            if (!this.g(block) && (block.getMaterial() != this.material || iblockdata.get(LEVEL) > 0)) {
               if (!this.g(world.getType(x1, y1 - 1, z1).getBlock())) {
                  return i;
               }

               if (i < 4) {
                  int k = this.a(world, x1, y1, z1, i + 1, enumdirection1.opposite());
                  if (k < j) {
                     j = k;
                  }
               }
            }
         }
      }

      return j;
   }

   private Set<EnumDirection> f(World world, int x, int y, int z) {
      int i = 1000;
      Set<EnumDirection> enumset = new ArraySet<>();

      for (EnumDirection enumdirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
         int x1 = x + enumdirection.getAdjacentX();
         int y1 = y + enumdirection.getAdjacentY();
         int z1 = z + enumdirection.getAdjacentZ();
         IBlockData iblockdata = world.getType(x1, y1, z1);
         Block block = iblockdata.getBlock();
         if (!this.g(block) && (block.getMaterial() != this.material || iblockdata.get(LEVEL) > 0)) {
            int j;
            if (this.g(world.getType(x1, y1 - 1, z1).getBlock())) {
               j = this.a(world, x1, y1, z1, 4, enumdirection.opposite());
            } else {
               j = 0;
            }

            if (j < i) {
               enumset.clear();
            }

            if (j <= i) {
               enumset.add(enumdirection);
               i = j;
            }
         }
      }

      return enumset;
   }

   private boolean g(Block block) {
      return block instanceof BlockDoor
         || !(Boolean)BlazeConfig.waterDestroyRedstone.getValue()
            && (
               block instanceof BlockButtonAbstract
                  || block instanceof BlockRedstoneWire
                  || block instanceof BlockDiodeAbstract
                  || block instanceof BlockLever
                  || block instanceof BlockRedstoneTorch
            )
         || block == Blocks.STANDING_SIGN
         || block == Blocks.LADDER
         || block == Blocks.REEDS
         || block.material == Material.PORTAL
         || block.material.isSolid();
   }

   protected int a(World world, int x, int y, int z, int i) {
      int j = this.e(world, x, y, z);
      if (j < 0) {
         return i;
      } else {
         if (j == 0) {
            this.a++;
         }

         if (j >= 8) {
            j = 0;
         }

         return i >= 0 && j >= i ? i : j;
      }
   }

   private boolean h(Block block) {
      Material material = block.getMaterial();
      return material != this.material && material != Material.LAVA && !this.g(block);
   }

   public boolean lookUpData(World world, int x, int y, int z, Material material, int data) {
      IBlockData state = world.getType(x, y, z);
      return state.getBlock().getMaterial() == material && this.getData(state) < data;
   }

   @Override
   public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
      if (!this.e(world, blockposition, iblockdata)) {
         world.a(blockposition, this, this.getFlowSpeed(world, blockposition.getX(), blockposition.getY(), blockposition.getZ()));
      }
   }

   public int getFlowSpeed(World world, int x, int y, int z) {
      if (this.getMaterial() == Material.LAVA) {
         return world.worldProvider.o() ? world.paperSpigotConfig.lavaFlowSpeedNether : world.paperSpigotConfig.lavaFlowSpeedNormal;
      } else if (this.getMaterial() == Material.WATER) {
         return world.getType(x, y, z - 1).getBlock().getMaterial() != Material.LAVA
               && world.getType(x, y, z + 1).getBlock().getMaterial() != Material.LAVA
               && world.getType(x - 1, y, z).getBlock().getMaterial() != Material.LAVA
               && world.getType(x + 1, y, z).getBlock().getMaterial() != Material.LAVA
            ? 5
            : world.paperSpigotConfig.waterOverLavaFlowSpeed;
      } else {
         return super.a(world);
      }
   }

   public int getData(World world, int x, int y, int z) {
      int data = this.e(world, x, y, z);
      return data < 8 ? data : 0;
   }

   public int getData(IBlockData block) {
      int data = block.getBlock().getMaterial() == this.material ? block.get(BlockFluids.LEVEL) : -1;
      return data < 8 ? data : 0;
   }

   public boolean canFastDrain(World world, int x, int y, int z) {
      boolean result = false;
      int data = this.getData(world, x, y, z);
      if (this.material == Material.WATER) {
         if (world.paperSpigotConfig.fastDrainWater) {
            result = true;
            if (this.getData(world, x, y - 1, z) < 0) {
               result = false;
            } else if (this.lookUpData(world, x, y, z - 1, Material.WATER, data)) {
               result = false;
            } else if (this.lookUpData(world, x, y, z + 1, Material.WATER, data)) {
               result = false;
            } else if (this.lookUpData(world, x - 1, y, z, Material.WATER, data)) {
               result = false;
            } else if (this.lookUpData(world, x + 1, y, z, Material.WATER, data)) {
               result = false;
            }
         }
      } else if (this.material == Material.LAVA && world.paperSpigotConfig.fastDrainLava) {
         result = true;
         if (this.getData(world, x, y - 1, z) < 0 || world.getType(x, y + 1, z).getBlock().getMaterial() != Material.AIR) {
            result = false;
         } else if (this.lookUpData(world, x, y, z - 1, Material.LAVA, data)) {
            result = false;
         } else if (this.lookUpData(world, x, y, z + 1, Material.LAVA, data)) {
            result = false;
         } else if (this.lookUpData(world, x - 1, y, z, Material.LAVA, data)) {
            result = false;
         } else if (this.lookUpData(world, x + 1, y, z, Material.LAVA, data)) {
            result = false;
         }
      }

      return result;
   }
}
