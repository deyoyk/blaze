package org.bukkit.event.block;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class NotePlayEvent extends BlockEvent implements Cancellable {
   private static HandlerList handlers = new HandlerList();
   private org.bukkit.Instrument instrument;
   private org.bukkit.Note note;
   private boolean cancelled = false;

   public NotePlayEvent(Block block, org.bukkit.Instrument instrument, org.bukkit.Note note) {
      super(block);
      this.instrument = instrument;
      this.note = note;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void setCancelled(boolean cancel) {
      this.cancelled = cancel;
   }

   public org.bukkit.Instrument getInstrument() {
      return this.instrument;
   }

   public org.bukkit.Note getNote() {
      return this.note;
   }

   public void setInstrument(org.bukkit.Instrument instrument) {
      if (instrument != null) {
         this.instrument = instrument;
      }
   }

   public void setNote(org.bukkit.Note note) {
      if (note != null) {
         this.note = note;
      }
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
