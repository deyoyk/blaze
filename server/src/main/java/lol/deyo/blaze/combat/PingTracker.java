package lol.deyo.blaze.combat;

/**
 * Per-connection RTT and jitter tracker driven by the keep-alive exchange.
 */
public final class PingTracker {
   private static final int NO_ID = Integer.MIN_VALUE;

   private int awaitingId = NO_ID;
   private long sentNanos;
   private long pingNanos;
   private long jitterNanos;
   private int samples;
   private int missed;

   public void onKeepAliveSent(int id, long nowNanos) {
      if (this.awaitingId != NO_ID) {
         this.missed++;
      }
      this.awaitingId = id;
      this.sentNanos = nowNanos;
   }

   public void onKeepAliveResponse(int id, long nowNanos) {
      if (id != this.awaitingId) {
         return;
      }

      long rtt = nowNanos - this.sentNanos;
      if (rtt <= 0L) {
         return;
      }

      if (this.samples == 0) {
         this.pingNanos = rtt;
         this.jitterNanos = 0L;
      } else {
         long difference = Math.abs(rtt - this.pingNanos);
         this.jitterNanos = this.jitterNanos * 3L / 4L + difference / 4L;
         this.pingNanos = this.pingNanos * 3L / 4L + rtt / 4L;
      }

      this.samples++;
      this.awaitingId = NO_ID;
   }

   public boolean hasSample() {
      return this.samples > 0;
   }

   public long getPingNanos() {
      return this.pingNanos;
   }

   public int getPingMillis() {
      return (int)(this.pingNanos / 1000000L);
   }

   public int getJitterMillis() {
      return (int)(this.jitterNanos / 1000000L);
   }

   public int getSamples() {
      return this.samples;
   }

   public int getMissed() {
      return this.missed;
   }
}
