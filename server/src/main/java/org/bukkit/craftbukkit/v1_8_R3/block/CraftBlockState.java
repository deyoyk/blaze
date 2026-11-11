package org.bukkit.craftbukkit.v1_8_R3.block;

import java.util.List;
import java.util.Objects;
import net.minecraft.server.v1_8_R3.TileEntity;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class CraftBlockState implements BlockState {
   private final CraftWorld world;
   private final CraftChunk chunk;
   private final int x;
   private final int y;
   private final int z;
   protected int type;
   protected MaterialData data;
   protected int flag;
   protected final byte light;

   public CraftBlockState(Block block) {
      this.world = (CraftWorld)block.getWorld();
      this.x = block.getX();
      this.y = block.getY();
      this.z = block.getZ();
      this.type = block.getTypeId();
      this.light = block.getLightLevel();
      this.chunk = (CraftChunk)block.getChunk();
      this.flag = 3;
      this.createData(block.getData());
   }

   public CraftBlockState(Block block, int flag) {
      this(block);
      this.flag = flag;
   }

   public CraftBlockState(org.bukkit.Material material) {
      this.world = null;
      this.type = material.getId();
      this.light = 0;
      this.chunk = null;
      this.x = this.y = this.z = 0;
   }

   public static CraftBlockState getBlockState(net.minecraft.server.v1_8_R3.World world, int x, int y, int z) {
      return new CraftBlockState(world.getWorld().getBlockAt(x, y, z));
   }

   public static CraftBlockState getBlockState(net.minecraft.server.v1_8_R3.World world, int x, int y, int z, int flag) {
      return new CraftBlockState(world.getWorld().getBlockAt(x, y, z), flag);
   }

   @Override
   public org.bukkit.World getWorld() {
      this.requirePlaced();
      return this.world;
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
      this.requirePlaced();
      return this.chunk;
   }

   @Override
   public void setData(MaterialData data) {
      org.bukkit.Material mat = this.getType();
      if (mat == null) {
         this.data = data;
      } else {
         if (data.getClass() != mat.getData() && data.getClass() != MaterialData.class) {
            throw new IllegalArgumentException("Provided data is not of type " + mat.getData().getName() + ", found " + data.getClass().getName());
         }

         this.data = data;
      }
   }

   @Override
   public MaterialData getData() {
      return this.data;
   }

   @Override
   public void setType(org.bukkit.Material type) {
      this.setTypeId(type.getId());
   }

   @Override
   public boolean setTypeId(int type) {
      if (this.type != type) {
         this.type = type;
         this.createData((byte)0);
      }

      return true;
   }

   @Override
   public org.bukkit.Material getType() {
      return org.bukkit.Material.getMaterial(this.getTypeId());
   }

   public void setFlag(int flag) {
      this.flag = flag;
   }

   public int getFlag() {
      return this.flag;
   }

   @Override
   public int getTypeId() {
      return this.type;
   }

   @Override
   public byte getLightLevel() {
      return this.light;
   }

   @Override
   public Block getBlock() {
      this.requirePlaced();
      return this.world.getBlockAt(this.x, this.y, this.z);
   }

   @Override
   public boolean update() {
      return this.update(false);
   }

   @Override
   public boolean update(boolean force) {
      return this.update(force, true);
   }

   @Override
   public boolean update(boolean force, boolean applyPhysics) {
      this.requirePlaced();
      Block block = this.getBlock();
      if (block.getType() != this.getType() && !force) {
         return false;
      } else {
         block.setTypeIdAndData(this.getTypeId(), this.getRawData(), applyPhysics);
         this.world.getHandle().notify(this.x, this.y, this.z);
         return true;
      }
   }

   private void createData(byte data) {
      org.bukkit.Material mat = this.getType();
      if (mat == null) {
         this.data = new MaterialData(this.type, data);
      } else {
         this.data = mat.getNewData(data);
      }
   }

   @Override
   public byte getRawData() {
      return this.data.getData();
   }

   @Override
   public org.bukkit.Location getLocation() {
      return new org.bukkit.Location(this.world, this.x, this.y, this.z);
   }

   @Override
   public org.bukkit.Location getLocation(org.bukkit.Location loc) {
      if (loc != null) {
         loc.setWorld(this.world);
         loc.setX(this.x);
         loc.setY(this.y);
         loc.setZ(this.z);
         loc.setYaw(0.0F);
         loc.setPitch(0.0F);
      }

      return loc;
   }

   @Override
   public void setRawData(byte data) {
      this.data.setData(data);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         CraftBlockState other = (CraftBlockState)obj;
         if (!Objects.equals(this.world, other.world)) {
            return false;
         } else if (this.x != other.x) {
            return false;
         } else if (this.y != other.y) {
            return false;
         } else if (this.z != other.z) {
            return false;
         } else {
            return this.type != other.type ? false : Objects.equals(this.data, other.data);
         }
      }
   }

   @Override
   public int hashCode() {
      int hash = 7;
      hash = 73 * hash + (this.world != null ? this.world.hashCode() : 0);
      hash = 73 * hash + this.x;
      hash = 73 * hash + this.y;
      hash = 73 * hash + this.z;
      hash = 73 * hash + this.type;
      return 73 * hash + (this.data != null ? this.data.hashCode() : 0);
   }

   public TileEntity getTileEntity() {
      return null;
   }

   @Override
   public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
      this.requirePlaced();
      this.chunk.getCraftWorld().getBlockMetadata().setMetadata(this.getBlock(), metadataKey, newMetadataValue);
   }

   @Override
   public List<MetadataValue> getMetadata(String metadataKey) {
      this.requirePlaced();
      return this.chunk.getCraftWorld().getBlockMetadata().getMetadata(this.getBlock(), metadataKey);
   }

   @Override
   public boolean hasMetadata(String metadataKey) {
      this.requirePlaced();
      return this.chunk.getCraftWorld().getBlockMetadata().hasMetadata(this.getBlock(), metadataKey);
   }

   @Override
   public void removeMetadata(String metadataKey, Plugin owningPlugin) {
      this.requirePlaced();
      this.chunk.getCraftWorld().getBlockMetadata().removeMetadata(this.getBlock(), metadataKey, owningPlugin);
   }

   @Override
   public boolean isPlaced() {
      return this.world != null;
   }

   protected void requirePlaced() {
      if (!this.isPlaced()) {
         throw new IllegalStateException("The blockState must be placed to call this method");
      }
   }
}
