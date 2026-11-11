package org.bukkit.block;

public interface NoteBlock extends BlockState {
   org.bukkit.Note getNote();

   @Deprecated
   byte getRawNote();

   void setNote(org.bukkit.Note var1);

   @Deprecated
   void setRawNote(byte var1);

   boolean play();

   @Deprecated
   boolean play(byte var1, byte var2);

   boolean play(org.bukkit.Instrument var1, org.bukkit.Note var2);
}
