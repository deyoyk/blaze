package org.bukkit.craftbukkit.v1_8_R3.block;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.TileEntityNote;
import org.bukkit.block.Block;
import org.bukkit.block.NoteBlock;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;

public class CraftNoteBlock extends CraftBlockState implements NoteBlock {
   private final CraftWorld world;
   private final TileEntityNote note;

   public CraftNoteBlock(Block block) {
      super(block);
      this.world = (CraftWorld)block.getWorld();
      this.note = (TileEntityNote)this.world.getTileEntityAt(this.getX(), this.getY(), this.getZ());
   }

   public CraftNoteBlock(org.bukkit.Material material, TileEntityNote te) {
      super(material);
      this.world = null;
      this.note = te;
   }

   @Override
   public org.bukkit.Note getNote() {
      return new org.bukkit.Note(this.note.note);
   }

   @Override
   public byte getRawNote() {
      return this.note.note;
   }

   @Override
   public void setNote(org.bukkit.Note n) {
      this.note.note = n.getId();
   }

   @Override
   public void setRawNote(byte n) {
      this.note.note = n;
   }

   @Override
   public boolean play() {
      Block block = this.getBlock();
      if (block.getType() == org.bukkit.Material.NOTE_BLOCK) {
         this.note.play(this.world.getHandle(), new BlockPosition(this.getX(), this.getY(), this.getZ()));
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean play(byte instrument, byte note) {
      Block block = this.getBlock();
      if (block.getType() == org.bukkit.Material.NOTE_BLOCK) {
         this.world.getHandle().playBlockAction(new BlockPosition(this.getX(), this.getY(), this.getZ()), CraftMagicNumbers.getBlock(block), instrument, note);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean play(org.bukkit.Instrument instrument, org.bukkit.Note note) {
      Block block = this.getBlock();
      if (block.getType() == org.bukkit.Material.NOTE_BLOCK) {
         this.world
            .getHandle()
            .playBlockAction(new BlockPosition(this.getX(), this.getY(), this.getZ()), CraftMagicNumbers.getBlock(block), instrument.getType(), note.getId());
         return true;
      } else {
         return false;
      }
   }

   public TileEntityNote getTileEntity() {
      return this.note;
   }
}
