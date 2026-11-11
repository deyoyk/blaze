package org.bukkit.craftbukkit.v1_8_R3.block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.server.v1_8_R3.BiomeBase;
import net.minecraft.server.v1_8_R3.BlockCocoa;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.BlockRedstoneWire;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.EnumDirection;
import net.minecraft.server.v1_8_R3.EnumSkyBlock;
import net.minecraft.server.v1_8_R3.GameProfileSerializer;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.TileEntitySkull;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockVector;

public class CraftBlock implements Block {
   private final CraftChunk chunk;
   private final int x;
   private final int y;
   private final int z;
   private static final Biome[] BIOME_MAPPING = new Biome[BiomeBase.getBiomes().length];
   private static final BiomeBase[] BIOMEBASE_MAPPING = new BiomeBase[Biome.values().length];

   static {
      BIOME_MAPPING[BiomeBase.OCEAN.id] = Biome.OCEAN;
      BIOME_MAPPING[BiomeBase.PLAINS.id] = Biome.PLAINS;
      BIOME_MAPPING[BiomeBase.DESERT.id] = Biome.DESERT;
      BIOME_MAPPING[BiomeBase.EXTREME_HILLS.id] = Biome.EXTREME_HILLS;
      BIOME_MAPPING[BiomeBase.FOREST.id] = Biome.FOREST;
      BIOME_MAPPING[BiomeBase.TAIGA.id] = Biome.TAIGA;
      BIOME_MAPPING[BiomeBase.SWAMPLAND.id] = Biome.SWAMPLAND;
      BIOME_MAPPING[BiomeBase.RIVER.id] = Biome.RIVER;
      BIOME_MAPPING[BiomeBase.HELL.id] = Biome.HELL;
      BIOME_MAPPING[BiomeBase.SKY.id] = Biome.SKY;
      BIOME_MAPPING[BiomeBase.FROZEN_OCEAN.id] = Biome.FROZEN_OCEAN;
      BIOME_MAPPING[BiomeBase.FROZEN_RIVER.id] = Biome.FROZEN_RIVER;
      BIOME_MAPPING[BiomeBase.ICE_PLAINS.id] = Biome.ICE_PLAINS;
      BIOME_MAPPING[BiomeBase.ICE_MOUNTAINS.id] = Biome.ICE_MOUNTAINS;
      BIOME_MAPPING[BiomeBase.MUSHROOM_ISLAND.id] = Biome.MUSHROOM_ISLAND;
      BIOME_MAPPING[BiomeBase.MUSHROOM_SHORE.id] = Biome.MUSHROOM_SHORE;
      BIOME_MAPPING[BiomeBase.BEACH.id] = Biome.BEACH;
      BIOME_MAPPING[BiomeBase.DESERT_HILLS.id] = Biome.DESERT_HILLS;
      BIOME_MAPPING[BiomeBase.FOREST_HILLS.id] = Biome.FOREST_HILLS;
      BIOME_MAPPING[BiomeBase.TAIGA_HILLS.id] = Biome.TAIGA_HILLS;
      BIOME_MAPPING[BiomeBase.SMALL_MOUNTAINS.id] = Biome.SMALL_MOUNTAINS;
      BIOME_MAPPING[BiomeBase.JUNGLE.id] = Biome.JUNGLE;
      BIOME_MAPPING[BiomeBase.JUNGLE_HILLS.id] = Biome.JUNGLE_HILLS;
      BIOME_MAPPING[BiomeBase.JUNGLE_EDGE.id] = Biome.JUNGLE_EDGE;
      BIOME_MAPPING[BiomeBase.DEEP_OCEAN.id] = Biome.DEEP_OCEAN;
      BIOME_MAPPING[BiomeBase.STONE_BEACH.id] = Biome.STONE_BEACH;
      BIOME_MAPPING[BiomeBase.COLD_BEACH.id] = Biome.COLD_BEACH;
      BIOME_MAPPING[BiomeBase.BIRCH_FOREST.id] = Biome.BIRCH_FOREST;
      BIOME_MAPPING[BiomeBase.BIRCH_FOREST_HILLS.id] = Biome.BIRCH_FOREST_HILLS;
      BIOME_MAPPING[BiomeBase.ROOFED_FOREST.id] = Biome.ROOFED_FOREST;
      BIOME_MAPPING[BiomeBase.COLD_TAIGA.id] = Biome.COLD_TAIGA;
      BIOME_MAPPING[BiomeBase.COLD_TAIGA_HILLS.id] = Biome.COLD_TAIGA_HILLS;
      BIOME_MAPPING[BiomeBase.MEGA_TAIGA.id] = Biome.MEGA_TAIGA;
      BIOME_MAPPING[BiomeBase.MEGA_TAIGA_HILLS.id] = Biome.MEGA_TAIGA_HILLS;
      BIOME_MAPPING[BiomeBase.EXTREME_HILLS_PLUS.id] = Biome.EXTREME_HILLS_PLUS;
      BIOME_MAPPING[BiomeBase.SAVANNA.id] = Biome.SAVANNA;
      BIOME_MAPPING[BiomeBase.SAVANNA_PLATEAU.id] = Biome.SAVANNA_PLATEAU;
      BIOME_MAPPING[BiomeBase.MESA.id] = Biome.MESA;
      BIOME_MAPPING[BiomeBase.MESA_PLATEAU_F.id] = Biome.MESA_PLATEAU_FOREST;
      BIOME_MAPPING[BiomeBase.MESA_PLATEAU.id] = Biome.MESA_PLATEAU;
      BIOME_MAPPING[BiomeBase.PLAINS.id + 128] = Biome.SUNFLOWER_PLAINS;
      BIOME_MAPPING[BiomeBase.DESERT.id + 128] = Biome.DESERT_MOUNTAINS;
      BIOME_MAPPING[BiomeBase.FOREST.id + 128] = Biome.FLOWER_FOREST;
      BIOME_MAPPING[BiomeBase.TAIGA.id + 128] = Biome.TAIGA_MOUNTAINS;
      BIOME_MAPPING[BiomeBase.SWAMPLAND.id + 128] = Biome.SWAMPLAND_MOUNTAINS;
      BIOME_MAPPING[BiomeBase.ICE_PLAINS.id + 128] = Biome.ICE_PLAINS_SPIKES;
      BIOME_MAPPING[BiomeBase.JUNGLE.id + 128] = Biome.JUNGLE_MOUNTAINS;
      BIOME_MAPPING[BiomeBase.JUNGLE_EDGE.id + 128] = Biome.JUNGLE_EDGE_MOUNTAINS;
      BIOME_MAPPING[BiomeBase.COLD_TAIGA.id + 128] = Biome.COLD_TAIGA_MOUNTAINS;
      BIOME_MAPPING[BiomeBase.SAVANNA.id + 128] = Biome.SAVANNA_MOUNTAINS;
      BIOME_MAPPING[BiomeBase.SAVANNA_PLATEAU.id + 128] = Biome.SAVANNA_PLATEAU_MOUNTAINS;
      BIOME_MAPPING[BiomeBase.MESA.id + 128] = Biome.MESA_BRYCE;
      BIOME_MAPPING[BiomeBase.MESA_PLATEAU_F.id + 128] = Biome.MESA_PLATEAU_FOREST_MOUNTAINS;
      BIOME_MAPPING[BiomeBase.MESA_PLATEAU.id + 128] = Biome.MESA_PLATEAU_MOUNTAINS;
      BIOME_MAPPING[BiomeBase.BIRCH_FOREST.id + 128] = Biome.BIRCH_FOREST_MOUNTAINS;
      BIOME_MAPPING[BiomeBase.BIRCH_FOREST_HILLS.id + 128] = Biome.BIRCH_FOREST_HILLS_MOUNTAINS;
      BIOME_MAPPING[BiomeBase.ROOFED_FOREST.id + 128] = Biome.ROOFED_FOREST_MOUNTAINS;
      BIOME_MAPPING[BiomeBase.MEGA_TAIGA.id + 128] = Biome.MEGA_SPRUCE_TAIGA;
      BIOME_MAPPING[BiomeBase.EXTREME_HILLS.id + 128] = Biome.EXTREME_HILLS_MOUNTAINS;
      BIOME_MAPPING[BiomeBase.EXTREME_HILLS_PLUS.id + 128] = Biome.EXTREME_HILLS_PLUS_MOUNTAINS;
      BIOME_MAPPING[BiomeBase.MEGA_TAIGA_HILLS.id + 128] = Biome.MEGA_SPRUCE_TAIGA_HILLS;

      for (int i = 0; i < BIOME_MAPPING.length; i++) {
         if (BiomeBase.getBiome(i) != null && BIOME_MAPPING[i] == null) {
            throw new IllegalArgumentException("Missing Biome mapping for BiomeBase[" + i + ", " + BiomeBase.getBiome(i) + "]");
         }

         if (BIOME_MAPPING[i] != null) {
            BIOMEBASE_MAPPING[BIOME_MAPPING[i].ordinal()] = BiomeBase.getBiome(i);
         }
      }
   }

   public CraftBlock(CraftChunk chunk, int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.chunk = chunk;
   }

   private net.minecraft.server.v1_8_R3.Block getNMSBlock() {
      return CraftMagicNumbers.getBlock(this);
   }

   private static net.minecraft.server.v1_8_R3.Block getNMSBlock(int type) {
      return CraftMagicNumbers.getBlock(type);
   }

   @Override
   public org.bukkit.World getWorld() {
      return this.chunk.getWorld();
   }

   @Override
   public org.bukkit.Location getLocation() {
      return new org.bukkit.Location(this.getWorld(), this.x, this.y, this.z);
   }

   @Override
   public org.bukkit.Location getLocation(org.bukkit.Location loc) {
      if (loc != null) {
         loc.setWorld(this.getWorld());
         loc.setX(this.x);
         loc.setY(this.y);
         loc.setZ(this.z);
         loc.setYaw(0.0F);
         loc.setPitch(0.0F);
      }

      return loc;
   }

   public BlockVector getVector() {
      return new BlockVector(this.x, this.y, this.z);
   }

   @Override
   public int getX() {
      return this.x;
   }

   @Override
   public int getY() {
      return this.y;
   }

   @Override
   public int getZ() {
      return this.z;
   }

   @Override
   public org.bukkit.Chunk getChunk() {
      return this.chunk;
   }

   @Override
   public void setData(byte data) {
      this.setData(data, 3);
   }

   @Override
   public void setData(byte data, boolean applyPhysics) {
      if (applyPhysics) {
         this.setData(data, 3);
      } else {
         this.setData(data, 2);
      }
   }

   private void setData(byte data, int flag) {
      net.minecraft.server.v1_8_R3.World world = this.chunk.getHandle().getWorld();
      BlockPosition position = new BlockPosition(this.x, this.y, this.z);
      IBlockData blockData = world.getType(position);
      world.setTypeAndData(this.chunk.getHandle(), position, blockData.getBlock().fromLegacyData(data), flag, true, false);
   }

   @Override
   public int getDurability() {
      return this.chunk.getHandle().getWorld().getDurability(this.chunk.getHandle(), this.x, this.y, this.z);
   }

   @Override
   public void setDurability(int durability) {
      this.chunk.getHandle().getWorld().setDurability(this.chunk.getHandle(), this.x, this.y, this.z, durability);
   }

   @Override
   public byte getData() {
      IBlockData blockData = this.chunk.getHandle().getBlockData(this.x, this.y, this.z);
      return (byte)blockData.getBlock().toLegacyData(blockData);
   }

   @Override
   public void setType(org.bukkit.Material type) {
      this.setType(type, true);
   }

   @Override
   public void setType(org.bukkit.Material type, boolean applyPhysics) {
      this.setTypeId(type.getId(), applyPhysics);
   }

   @Override
   public boolean setTypeId(int type) {
      return this.setTypeId(type, true);
   }

   @Override
   public boolean setTypeId(int type, boolean applyPhysics) {
      net.minecraft.server.v1_8_R3.Block block = getNMSBlock(type);
      return this.setTypeIdAndData(type, (byte)block.toLegacyData(block.getBlockData()), applyPhysics);
   }

   @Override
   public boolean setTypeIdAndData(int type, byte data, boolean applyPhysics) {
      IBlockData blockData = getNMSBlock(type).fromLegacyData(data);
      BlockPosition position = new BlockPosition(this.x, this.y, this.z);
      if (applyPhysics) {
         return this.chunk.getHandle().getWorld().setTypeAndData(this.chunk.getHandle(), position, blockData, 3, true, false);
      } else {
         boolean success = this.chunk.getHandle().getWorld().setTypeAndData(this.chunk.getHandle(), position, blockData, 2, true, false);
         if (success) {
            this.chunk.getHandle().getWorld().notify(position);
         }

         return success;
      }
   }

   @Override
   public org.bukkit.Material getType() {
      return org.bukkit.Material.getMaterial(this.getTypeId());
   }

   @Deprecated
   @Override
   public int getTypeId() {
      return CraftMagicNumbers.getId(this.chunk.getHandle().getTypeAbs(this.x, this.y, this.z));
   }

   @Override
   public byte getLightLevel() {
      return (byte)this.chunk.getHandle().getWorld().getLightLevel(this.x, this.y, this.z);
   }

   @Override
   public byte getLightFromSky() {
      return (byte)this.chunk.getHandle().getBrightness(EnumSkyBlock.SKY, this.x, this.y, this.z);
   }

   @Override
   public byte getLightFromBlocks() {
      return (byte)this.chunk.getHandle().getBrightness(EnumSkyBlock.BLOCK, this.x, this.y, this.z);
   }

   public Block getFace(BlockFace face) {
      return this.getRelative(face, 1);
   }

   public Block getFace(BlockFace face, int distance) {
      return this.getRelative(face, distance);
   }

   @Override
   public Block getRelative(int modX, int modY, int modZ) {
      return this.getWorld().getBlockAt(this.getX() + modX, this.getY() + modY, this.getZ() + modZ);
   }

   @Override
   public Block getRelative(BlockFace face) {
      return this.getRelative(face, 1);
   }

   @Override
   public Block getRelative(BlockFace face, int distance) {
      return this.getRelative(face.getModX() * distance, face.getModY() * distance, face.getModZ() * distance);
   }

   @Override
   public BlockFace getFace(Block block) {
      BlockFace[] values = BlockFace.values();

      for (BlockFace face : values) {
         if (this.getX() + face.getModX() == block.getX() && this.getY() + face.getModY() == block.getY() && this.getZ() + face.getModZ() == block.getZ()) {
            return face;
         }
      }

      return null;
   }

   @Override
   public String toString() {
      return "CraftBlock{chunk=" + this.chunk + ",x=" + this.x + ",y=" + this.y + ",z=" + this.z + ",type=" + this.getType() + ",data=" + this.getData() + '}';
   }

   public static BlockFace notchToBlockFace(EnumDirection notch) {
      if (notch == null) {
         return BlockFace.SELF;
      } else {
         switch (notch) {
            case DOWN:
               return BlockFace.DOWN;
            case UP:
               return BlockFace.UP;
            case NORTH:
               return BlockFace.NORTH;
            case SOUTH:
               return BlockFace.SOUTH;
            case WEST:
               return BlockFace.WEST;
            case EAST:
               return BlockFace.EAST;
            default:
               return BlockFace.SELF;
         }
      }
   }

   public static EnumDirection blockFaceToNotch(BlockFace face) {
      switch (face) {
         case NORTH:
            return EnumDirection.NORTH;
         case EAST:
            return EnumDirection.EAST;
         case SOUTH:
            return EnumDirection.SOUTH;
         case WEST:
            return EnumDirection.WEST;
         case UP:
            return EnumDirection.UP;
         case DOWN:
            return EnumDirection.DOWN;
         default:
            return null;
      }
   }

   @Override
   public BlockState getState() {
      org.bukkit.Material material = this.getType();
      switch (material) {
         case DISPENSER:
            return new CraftDispenser(this);
         case NOTE_BLOCK:
            return new CraftNoteBlock(this);
         case MOB_SPAWNER:
            return new CraftCreatureSpawner(this);
         case CHEST:
         case TRAPPED_CHEST:
            return new CraftChest(this);
         case FURNACE:
         case BURNING_FURNACE:
            return new CraftFurnace(this);
         case SIGN_POST:
         case WALL_SIGN:
         case SIGN:
            return new CraftSign(this);
         case JUKEBOX:
            return new CraftJukebox(this);
         case BREWING_STAND:
            return new CraftBrewingStand(this);
         case COMMAND:
            return new CraftCommandBlock(this);
         case BEACON:
            return new CraftBeacon(this);
         case SKULL:
            return new CraftSkull(this);
         case HOPPER:
            return new CraftHopper(this);
         case DROPPER:
            return new CraftDropper(this);
         case STANDING_BANNER:
         case WALL_BANNER:
         case BANNER:
            return new CraftBanner(this);
         default:
            return new CraftBlockState(this);
      }
   }

   @Override
   public Biome getBiome() {
      return this.getWorld().getBiome(this.x, this.z);
   }

   @Override
   public void setBiome(Biome bio) {
      this.getWorld().setBiome(this.x, this.z, bio);
   }

   public static Biome biomeBaseToBiome(BiomeBase base) {
      return base == null ? null : BIOME_MAPPING[base.id];
   }

   public static BiomeBase biomeToBiomeBase(Biome bio) {
      return bio == null ? null : BIOMEBASE_MAPPING[bio.ordinal()];
   }

   @Override
   public double getTemperature() {
      return this.getWorld().getTemperature(this.x, this.z);
   }

   @Override
   public double getHumidity() {
      return this.getWorld().getHumidity(this.x, this.z);
   }

   @Override
   public boolean isBlockPowered() {
      return this.chunk.getHandle().getWorld().getBlockPower(new BlockPosition(this.x, this.y, this.z)) > 0;
   }

   @Override
   public boolean isBlockIndirectlyPowered() {
      return this.chunk.getHandle().getWorld().isBlockIndirectlyPowered(new BlockPosition(this.x, this.y, this.z));
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof CraftBlock)) {
         return false;
      } else {
         CraftBlock other = (CraftBlock)o;
         return this.x == other.x && this.y == other.y && this.z == other.z && this.getWorld().equals(other.getWorld());
      }
   }

   @Override
   public int hashCode() {
      return this.y << 24 ^ this.x ^ this.z ^ this.getWorld().hashCode();
   }

   @Override
   public boolean isBlockFacePowered(BlockFace face) {
      return this.chunk.getHandle().getWorld().isBlockFacePowered(new BlockPosition(this.x, this.y, this.z), blockFaceToNotch(face));
   }

   @Override
   public boolean isBlockFaceIndirectlyPowered(BlockFace face) {
      int power = this.chunk.getHandle().getWorld().getBlockFacePower(new BlockPosition(this.x, this.y, this.z), blockFaceToNotch(face));
      Block relative = this.getRelative(face);
      return relative.getType() == org.bukkit.Material.REDSTONE_WIRE ? Math.max(power, relative.getData()) > 0 : power > 0;
   }

   @Override
   public int getBlockPower(BlockFace face) {
      int power = 0;
      BlockRedstoneWire wire = Blocks.REDSTONE_WIRE;
      net.minecraft.server.v1_8_R3.World world = this.chunk.getHandle().getWorld();
      if ((face == BlockFace.DOWN || face == BlockFace.SELF) && world.isBlockFacePowered(new BlockPosition(this.x, this.y - 1, this.z), EnumDirection.DOWN)) {
         power = wire.getPower(world, new BlockPosition(this.x, this.y - 1, this.z), power);
      }

      if ((face == BlockFace.UP || face == BlockFace.SELF) && world.isBlockFacePowered(new BlockPosition(this.x, this.y + 1, this.z), EnumDirection.UP)) {
         power = wire.getPower(world, new BlockPosition(this.x, this.y + 1, this.z), power);
      }

      if ((face == BlockFace.EAST || face == BlockFace.SELF) && world.isBlockFacePowered(new BlockPosition(this.x + 1, this.y, this.z), EnumDirection.EAST)) {
         power = wire.getPower(world, new BlockPosition(this.x + 1, this.y, this.z), power);
      }

      if ((face == BlockFace.WEST || face == BlockFace.SELF) && world.isBlockFacePowered(new BlockPosition(this.x - 1, this.y, this.z), EnumDirection.WEST)) {
         power = wire.getPower(world, new BlockPosition(this.x - 1, this.y, this.z), power);
      }

      if ((face == BlockFace.NORTH || face == BlockFace.SELF) && world.isBlockFacePowered(new BlockPosition(this.x, this.y, this.z - 1), EnumDirection.NORTH)) {
         power = wire.getPower(world, new BlockPosition(this.x, this.y, this.z - 1), power);
      }

      if ((face == BlockFace.SOUTH || face == BlockFace.SELF) && world.isBlockFacePowered(new BlockPosition(this.x, this.y, this.z + 1), EnumDirection.SOUTH)) {
         power = wire.getPower(world, new BlockPosition(this.x, this.y, this.z - 1), power);
      }

      return power > 0 ? power : ((face == BlockFace.SELF ? !this.isBlockIndirectlyPowered() : !this.isBlockFaceIndirectlyPowered(face)) ? 0 : 15);
   }

   @Override
   public int getBlockPower() {
      return this.getBlockPower(BlockFace.SELF);
   }

   @Override
   public boolean isEmpty() {
      return this.getType() == org.bukkit.Material.AIR;
   }

   @Override
   public boolean isLiquid() {
      return this.getType() == org.bukkit.Material.WATER
         || this.getType() == org.bukkit.Material.STATIONARY_WATER
         || this.getType() == org.bukkit.Material.LAVA
         || this.getType() == org.bukkit.Material.STATIONARY_LAVA;
   }

   @Override
   public PistonMoveReaction getPistonMoveReaction() {
      return PistonMoveReaction.getById(this.getNMSBlock().getMaterial().getPushReaction());
   }

   private boolean itemCausesDrops(ItemStack item) {
      net.minecraft.server.v1_8_R3.Block block = this.getNMSBlock();
      Item itemType = item != null ? Item.getById(item.getTypeId()) : null;
      return block != null && (block.getMaterial().isAlwaysDestroyable() || itemType != null && itemType.canDestroySpecialBlock(block));
   }

   @Override
   public boolean breakNaturally() {
      net.minecraft.server.v1_8_R3.Block block = this.getNMSBlock();
      byte data = this.getData();
      boolean result = false;
      if (block != null && block != Blocks.AIR) {
         block.dropNaturally(this.chunk.getHandle().getWorld(), new BlockPosition(this.x, this.y, this.z), block.fromLegacyData(data), 1.0F, 0);
         result = true;
      }

      this.setTypeId(org.bukkit.Material.AIR.getId());
      return result;
   }

   @Override
   public boolean breakNaturally(ItemStack item) {
      return this.itemCausesDrops(item) ? this.breakNaturally() : this.setTypeId(org.bukkit.Material.AIR.getId());
   }

   @Override
   public Collection<ItemStack> getDrops() {
      List<ItemStack> drops = new ArrayList<>();
      net.minecraft.server.v1_8_R3.Block block = this.getNMSBlock();
      if (block != Blocks.AIR) {
         byte data = this.getData();
         int count = block.getDropCount(0, this.chunk.getHandle().getWorld().random);

         for (int i = 0; i < count; i++) {
            Item item = block.getDropType(block.fromLegacyData(data), this.chunk.getHandle().getWorld().random, 0);
            if (item != null) {
               if (Blocks.SKULL == block) {
                  BlockPosition position = new BlockPosition(this.x, this.y, this.z);
                  net.minecraft.server.v1_8_R3.ItemStack nmsStack = new net.minecraft.server.v1_8_R3.ItemStack(
                     item, 1, block.getDropData(this.chunk.getHandle().getWorld(), position)
                  );
                  TileEntitySkull tileentityskull = (TileEntitySkull)this.chunk.getHandle().getWorld().getTileEntity(position);
                  if (tileentityskull.getSkullType() == 3 && tileentityskull.getGameProfile() != null) {
                     nmsStack.setTag(new NBTTagCompound());
                     NBTTagCompound nbttagcompound = new NBTTagCompound();
                     GameProfileSerializer.serialize(nbttagcompound, tileentityskull.getGameProfile());
                     nmsStack.getTag().set("SkullOwner", nbttagcompound);
                  }

                  drops.add(CraftItemStack.asBukkitCopy(nmsStack));
               } else if (Blocks.COCOA == block) {
                  int age = (Integer)block.fromLegacyData(data).get(BlockCocoa.AGE);
                  int dropAmount = age >= 2 ? 3 : 1;

                  for (int j = 0; j < dropAmount; j++) {
                     drops.add(new ItemStack(org.bukkit.Material.INK_SACK, 1, (short)3));
                  }
               } else {
                  drops.add(new ItemStack(CraftMagicNumbers.getMaterial(item), 1, (short)block.getDropData(block.fromLegacyData(data))));
               }
            }
         }
      }

      return drops;
   }

   @Override
   public Collection<ItemStack> getDrops(ItemStack item) {
      return (Collection<ItemStack>)(this.itemCausesDrops(item) ? this.getDrops() : Collections.emptyList());
   }

   @Override
   public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
      this.chunk.getCraftWorld().getBlockMetadata().setMetadata((Block)this, metadataKey, newMetadataValue);
   }

   @Override
   public List<MetadataValue> getMetadata(String metadataKey) {
      return this.chunk.getCraftWorld().getBlockMetadata().getMetadata((Block)this, metadataKey);
   }

   @Override
   public boolean hasMetadata(String metadataKey) {
      return this.chunk.getCraftWorld().getBlockMetadata().hasMetadata((Block)this, metadataKey);
   }

   @Override
   public void removeMetadata(String metadataKey, Plugin owningPlugin) {
      this.chunk.getCraftWorld().getBlockMetadata().removeMetadata((Block)this, metadataKey, owningPlugin);
   }
}
