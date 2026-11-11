package org.bukkit.craftbukkit.v1_8_R3.block;

import net.minecraft.server.v1_8_R3.BlockJukeBox;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.BlockJukeBox.TileEntityRecordPlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Jukebox;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;

public class CraftJukebox extends CraftBlockState implements Jukebox {
   private final CraftWorld world;
   private final TileEntityRecordPlayer jukebox;

   public CraftJukebox(Block block) {
      super(block);
      this.world = (CraftWorld)block.getWorld();
      this.jukebox = (TileEntityRecordPlayer)this.world.getTileEntityAt(this.getX(), this.getY(), this.getZ());
   }

   public CraftJukebox(org.bukkit.Material material, TileEntityRecordPlayer te) {
      super(material);
      this.world = null;
      this.jukebox = te;
   }

   @Override
   public org.bukkit.Material getPlaying() {
      ItemStack record = this.jukebox.getRecord();
      return record == null ? org.bukkit.Material.AIR : CraftMagicNumbers.getMaterial(record.getItem());
   }

   @Override
   public void setPlaying(org.bukkit.Material record) {
      if (record != null && CraftMagicNumbers.getItem(record) != null) {
         this.jukebox.setRecord(new ItemStack(CraftMagicNumbers.getItem(record), 1));
      } else {
         record = org.bukkit.Material.AIR;
         this.jukebox.setRecord(null);
      }

      if (this.isPlaced()) {
         this.jukebox.update();
         if (record == org.bukkit.Material.AIR) {
            this.world
               .getHandle()
               .setTypeAndData(new BlockPosition(this.getX(), this.getY(), this.getZ()), Blocks.JUKEBOX.getBlockData().set(BlockJukeBox.HAS_RECORD, false), 3);
         } else {
            this.world
               .getHandle()
               .setTypeAndData(new BlockPosition(this.getX(), this.getY(), this.getZ()), Blocks.JUKEBOX.getBlockData().set(BlockJukeBox.HAS_RECORD, true), 3);
         }

         this.world.playEffect(this.getLocation(), org.bukkit.Effect.RECORD_PLAY, record.getId());
      }
   }

   @Override
   public boolean isPlaying() {
      return this.getRawData() == 1;
   }

   @Override
   public boolean eject() {
      this.requirePlaced();
      boolean result = this.isPlaying();
      ((BlockJukeBox)Blocks.JUKEBOX).dropRecord(this.world.getHandle(), new BlockPosition(this.getX(), this.getY(), this.getZ()), null);
      return result;
   }

   public TileEntityRecordPlayer getTileEntity() {
      return this.jukebox;
   }
}
