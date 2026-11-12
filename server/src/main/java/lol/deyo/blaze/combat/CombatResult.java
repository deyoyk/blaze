package lol.deyo.blaze.combat;

/**
 * Result of validating one attack packet.
 */
public final class CombatResult {
   public boolean accepted;
   public String reason = "";
   public long clickNanos;
   public long rewindNanos;
   public double distance;
   public double maxReach;
   public double eyeX;
   public double eyeY;
   public double eyeZ;
   public boolean historical;
   public int attackerPing;
   public int attackerJitter;
   public final CombatSnapshot victim = new CombatSnapshot();

   public void reset() {
      this.accepted = false;
      this.reason = "";
      this.clickNanos = 0L;
      this.rewindNanos = 0L;
      this.distance = 0.0;
      this.maxReach = 0.0;
      this.eyeX = 0.0;
      this.eyeY = 0.0;
      this.eyeZ = 0.0;
      this.historical = false;
      this.attackerPing = 0;
      this.attackerJitter = 0;
      this.victim.clear();
   }
}
