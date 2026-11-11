package org.bukkit.craftbukkit.v1_8_R3.block;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.TileEntitySkull;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class CraftSkull extends CraftBlockState implements Skull {
   private static final int MAX_OWNER_LENGTH = 16;
   private final TileEntitySkull skull;
   private GameProfile profile;
   private org.bukkit.SkullType skullType;
   private byte rotation;

   public CraftSkull(Block block) {
      super(block);
      CraftWorld world = (CraftWorld)block.getWorld();
      this.skull = (TileEntitySkull)world.getTileEntityAt(this.getX(), this.getY(), this.getZ());
      this.profile = this.skull.getGameProfile();
      this.skullType = getSkullType(this.skull.getSkullType());
      this.rotation = (byte)this.skull.getRotation();
   }

   public CraftSkull(org.bukkit.Material material, TileEntitySkull te) {
      super(material);
      this.skull = te;
      this.profile = this.skull.getGameProfile();
      this.skullType = getSkullType(this.skull.getSkullType());
      this.rotation = (byte)this.skull.getRotation();
   }

   static org.bukkit.SkullType getSkullType(int id) {
      switch (id) {
         case 0:
         default:
            return org.bukkit.SkullType.SKELETON;
         case 1:
            return org.bukkit.SkullType.WITHER;
         case 2:
            return org.bukkit.SkullType.ZOMBIE;
         case 3:
            return org.bukkit.SkullType.PLAYER;
         case 4:
            return org.bukkit.SkullType.CREEPER;
      }
   }

   static int getSkullType(org.bukkit.SkullType type) {
      switch (type) {
         case SKELETON:
         default:
            return 0;
         case WITHER:
            return 1;
         case ZOMBIE:
            return 2;
         case PLAYER:
            return 3;
         case CREEPER:
            return 4;
      }
   }

   static byte getBlockFace(BlockFace rotation) {
      switch (rotation) {
         case NORTH:
            return 0;
         case EAST:
            return 4;
         case SOUTH:
            return 8;
         case WEST:
            return 12;
         case UP:
         case DOWN:
         default:
            throw new IllegalArgumentException("Invalid BlockFace rotation: " + rotation);
         case NORTH_EAST:
            return 2;
         case NORTH_WEST:
            return 14;
         case SOUTH_EAST:
            return 6;
         case SOUTH_WEST:
            return 10;
         case WEST_NORTH_WEST:
            return 13;
         case NORTH_NORTH_WEST:
            return 15;
         case NORTH_NORTH_EAST:
            return 1;
         case EAST_NORTH_EAST:
            return 3;
         case EAST_SOUTH_EAST:
            return 5;
         case SOUTH_SOUTH_EAST:
            return 7;
         case SOUTH_SOUTH_WEST:
            return 9;
         case WEST_SOUTH_WEST:
            return 11;
      }
   }

   static BlockFace getBlockFace(byte rotation) {
      switch (rotation) {
         case 0:
            return BlockFace.NORTH;
         case 1:
            return BlockFace.NORTH_NORTH_EAST;
         case 2:
            return BlockFace.NORTH_EAST;
         case 3:
            return BlockFace.EAST_NORTH_EAST;
         case 4:
            return BlockFace.EAST;
         case 5:
            return BlockFace.EAST_SOUTH_EAST;
         case 6:
            return BlockFace.SOUTH_EAST;
         case 7:
            return BlockFace.SOUTH_SOUTH_EAST;
         case 8:
            return BlockFace.SOUTH;
         case 9:
            return BlockFace.SOUTH_SOUTH_WEST;
         case 10:
            return BlockFace.SOUTH_WEST;
         case 11:
            return BlockFace.WEST_SOUTH_WEST;
         case 12:
            return BlockFace.WEST;
         case 13:
            return BlockFace.WEST_NORTH_WEST;
         case 14:
            return BlockFace.NORTH_WEST;
         case 15:
            return BlockFace.NORTH_NORTH_WEST;
         default:
            throw new AssertionError((int)rotation);
      }
   }

   @Override
   public boolean hasOwner() {
      return this.profile != null;
   }

   @Override
   public String getOwner() {
      return this.hasOwner() ? this.profile.getName() : null;
   }

   @Override
   public boolean setOwner(String name) {
      if (name != null && name.length() <= 16) {
         GameProfile profile = MinecraftServer.getServer().getUserCache().getProfile(name);
         if (profile == null) {
            return false;
         } else {
            if (this.skullType != org.bukkit.SkullType.PLAYER) {
               this.skullType = org.bukkit.SkullType.PLAYER;
            }

            this.profile = profile;
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public BlockFace getRotation() {
      return getBlockFace(this.rotation);
   }

   @Override
   public void setRotation(BlockFace rotation) {
      this.rotation = getBlockFace(rotation);
   }

   @Override
   public org.bukkit.SkullType getSkullType() {
      return this.skullType;
   }

   @Override
   public void setSkullType(org.bukkit.SkullType skullType) {
      this.skullType = skullType;
      if (skullType != org.bukkit.SkullType.PLAYER) {
         this.profile = null;
      }
   }

   @Override
   public boolean update(boolean force, boolean applyPhysics) {
      boolean result = super.update(force, applyPhysics);
      if (result) {
         if (this.skullType == org.bukkit.SkullType.PLAYER) {
            this.skull.setGameProfile(this.profile);
         } else {
            this.skull.setSkullType(getSkullType(this.skullType));
         }

         this.skull.setRotation(this.rotation);
         this.skull.update();
      }

      return result;
   }

   public TileEntitySkull getTileEntity() {
      return this.skull;
   }
}
