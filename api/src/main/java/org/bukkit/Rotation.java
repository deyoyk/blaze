package org.bukkit;

public enum Rotation {
   NONE,
   CLOCKWISE_45,
   CLOCKWISE,
   CLOCKWISE_135,
   FLIPPED,
   FLIPPED_45,
   COUNTER_CLOCKWISE,
   COUNTER_CLOCKWISE_45;

   private static final org.bukkit.Rotation[] rotations = values();

   public org.bukkit.Rotation rotateClockwise() {
      return rotations[this.ordinal() + 1 & 7];
   }

   public org.bukkit.Rotation rotateCounterClockwise() {
      return rotations[this.ordinal() - 1 & 7];
   }
}
