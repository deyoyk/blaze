package lol.deyo.blaze.combat;

import net.minecraft.server.v1_8_R3.EntityPlayer;

/**
 * Short rolling history of a player's position and hitbox, used for lag compensation.
 */
public final class PlayerPositionHistory {
   private static final int CAPACITY = 40;

   private final CombatSnapshot[] snapshots = new CombatSnapshot[CAPACITY];
   private int next;
   private int size;

   public PlayerPositionHistory() {
      for (int i = 0; i < CAPACITY; i++) {
         this.snapshots[i] = new CombatSnapshot();
      }
   }

   public void record(EntityPlayer player, long timeNanos) {
      this.snapshots[this.next].set(player, timeNanos);
      this.next = (this.next + 1) % CAPACITY;
      if (this.size < CAPACITY) {
         this.size++;
      }
   }

   public void clear() {
      this.next = 0;
      this.size = 0;
      for (CombatSnapshot snapshot : this.snapshots) {
         snapshot.clear();
      }
   }

   /**
    * Fills {@code out} with the interpolated state at {@code timeNanos}.
    * Returns false when no sample exists.
    */
   public boolean snapshotAt(long timeNanos, CombatSnapshot out) {
      if (this.size == 0) {
         out.clear();
         return false;
      }

      CombatSnapshot before = null;
      CombatSnapshot after = null;
      for (int i = 0; i < this.size; i++) {
         CombatSnapshot snapshot = this.snapshots[i];
         if (snapshot.timeNanos <= timeNanos && (before == null || snapshot.timeNanos > before.timeNanos)) {
            before = snapshot;
         }
         if (snapshot.timeNanos >= timeNanos && (after == null || snapshot.timeNanos < after.timeNanos)) {
            after = snapshot;
         }
      }

      if (before == null && after == null) {
         out.clear();
         return false;
      }
      if (before == null) {
         out.copyFrom(after);
         return true;
      }
      if (after == null || before.timeNanos == after.timeNanos) {
         out.copyFrom(before);
         return true;
      }

      double progress = (double)(timeNanos - before.timeNanos) / (double)(after.timeNanos - before.timeNanos);
      out.interpolate(before, after, progress);
      return true;
   }

   public int size() {
      return this.size;
   }
}
