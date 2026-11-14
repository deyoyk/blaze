package lol.deyo.blaze.anticheat;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;

import java.util.ArrayDeque;
import java.util.Deque;

public final class ACPlayer {
   public final EntityPlayer player;
   public final CheckContainer checks = new CheckContainer();
   public final ViolationTracker violations = new ViolationTracker();

   private long receiveNanos;
   private long receiveNanosLastFlying;
   private long receiveNanosLastAttack;
   private long receiveNanosLastSwing;
   private long receiveNanosLastDig;
   private long receiveNanosLastUse;

   private double x;
   private double y;
   private double z;
   private double lastX;
   private double lastY;
   private double lastZ;
   private float yaw;
   private float pitch;
   private float lastYaw;
   private float lastPitch;
   private boolean onGround;
   private boolean lastOnGround;

   private boolean clientSprinting;
   private boolean clientSneaking;
   private boolean eating;
   private boolean blocking;
   private boolean digging;

   private double deltaX;
   private double deltaY;
   private double deltaZ;
   private double deltaXZ;
   private double lastDeltaY;
   private double lastDeltaXZ;
   private double yawDelta;
   private double pitchDelta;
   private boolean movedThisTick;
   private boolean teleportedSinceLastMove;

   private long flyingPacketCount;
   private long timerLastBalanceNanos;
   private long timerPacketsSinceBalance;
   private long timerElapsedNanosSinceBalance;

   private final Deque<Long> clickIntervals = new ArrayDeque<>(24);
   private int clicksThisSecond;
   private long secondWindowStart;

   private double expectedKnockbackX;
   private double expectedKnockbackY;
   private double expectedKnockbackZ;
   private int expectedKnockbackTicksLeft;
   private double kbHorizontalRatioSum;
   private double kbVerticalRatioSum;
   private int kbSamples;

   private double safeX;
   private double safeY;
   private double safeZ;
   private boolean hasSafePosition;
   private long lastAlertNanos;

   private long pingNanos;
   private int pingMillis;
   private int jitterMillis;

   private double victimX;
   private double victimY;
   private double victimZ;
   private boolean hasVictim;

   public ACPlayer(EntityPlayer player) {
      this.player = player;
      this.x = player.locX;
      this.y = player.locY;
      this.z = player.locZ;
      this.safeX = this.x;
      this.safeY = this.y;
      this.safeZ = this.z;
      this.yaw = player.yaw;
      this.pitch = player.pitch;
      this.timerLastBalanceNanos = System.nanoTime();
      this.secondWindowStart = System.nanoTime();
      ACManager.registerChecks(this);
   }

   public void onFlying(PacketPlayInFlying packet, long nowNanos) {
      this.receiveNanos = nowNanos;
      this.receiveNanosLastFlying = nowNanos;
      this.lastX = this.x;
      this.lastY = this.y;
      this.lastZ = this.z;
      this.lastYaw = this.yaw;
      this.lastPitch = this.pitch;
      this.lastOnGround = this.onGround;
      this.lastDeltaXZ = this.deltaXZ;
      this.lastDeltaY = this.deltaY;

      if (packet.g()) {
         this.x = packet.a();
         this.y = packet.b();
         this.z = packet.c();
      }

      if (packet.h()) {
         this.yaw = packet.d();
         this.pitch = packet.e();
      }

      this.onGround = packet.f();
      this.deltaX = this.x - this.lastX;
      this.deltaY = this.y - this.lastY;
      this.deltaZ = this.z - this.lastZ;
      this.deltaXZ = Math.sqrt(this.deltaX * this.deltaX + this.deltaZ * this.deltaZ);
      this.yawDelta = Math.abs(this.yaw - this.lastYaw);
      this.pitchDelta = Math.abs(this.pitch - this.lastPitch);
      this.movedThisTick = true;
      this.flyingPacketCount++;
      this.timerPacketsSinceBalance++;
      this.timerElapsedNanosSinceBalance = nowNanos - this.timerLastBalanceNanos;

      if (this.expectedKnockbackTicksLeft > 0) {
         this.expectedKnockbackTicksLeft--;
         if (this.expectedKnockbackTicksLeft == 0) {
            this.evaluateKnockback();
         }
      }
   }

   public void onUseEntity(PacketPlayInUseEntity packet, long nowNanos, boolean attacked) {
      this.receiveNanos = nowNanos;
      this.receiveNanosLastUse = nowNanos;
      if (attacked) {
         this.receiveNanosLastAttack = nowNanos;
      }
   }

   public void recordAttackVictim(double vx, double vy, double vz) {
      this.victimX = vx;
      this.victimY = vy;
      this.victimZ = vz;
      this.hasVictim = true;
   }

   public void onArmAnimation(PacketPlayInArmAnimation packet, long nowNanos) {
      this.receiveNanos = nowNanos;
      long last = this.receiveNanosLastSwing;
      this.receiveNanosLastSwing = nowNanos;
      if (last > 0L) {
         long interval = nowNanos - last;
         if (interval > 0L && interval < 1000000000L) {
            if (this.clickIntervals.size() >= 20) {
               this.clickIntervals.pollFirst();
            }
            this.clickIntervals.addLast(interval);
         }
      }

      if (nowNanos - this.secondWindowStart >= 1000000000L) {
         this.clicksThisSecond = 0;
         this.secondWindowStart = nowNanos;
      }

      this.clicksThisSecond++;
   }

   public void onBlockDig(PacketPlayInBlockDig packet, long nowNanos) {
      this.receiveNanos = nowNanos;
      this.receiveNanosLastDig = nowNanos;
      switch (packet.c()) {
         case START_DESTROY_BLOCK:
            this.digging = true;
            break;
         case ABORT_DESTROY_BLOCK:
         case STOP_DESTROY_BLOCK:
            this.digging = false;
            break;
         case RELEASE_USE_ITEM:
            this.eating = false;
            this.blocking = false;
            break;
      }
   }

   public void tick() {
      this.movedThisTick = false;
      this.teleportedSinceLastMove = false;
   }

   public void updatePing(long pingNanos, int pingMillis, int jitterMillis) {
      this.pingNanos = pingNanos;
      this.pingMillis = pingMillis;
      this.jitterMillis = jitterMillis;
   }

   public void onServerVelocity(double vX, double vY, double vZ) {
      this.expectedKnockbackX = vX;
      this.expectedKnockbackY = vY;
      this.expectedKnockbackZ = vZ;
      this.expectedKnockbackTicksLeft = 3;
   }

   private void evaluateKnockback() {
      double actualX = this.player.motX;
      double actualY = this.player.motY;
      double actualZ = this.player.motZ;
      double expectedHorizontal = Math.sqrt(
         this.expectedKnockbackX * this.expectedKnockbackX + this.expectedKnockbackZ * this.expectedKnockbackZ
      );
      double actualHorizontal = Math.sqrt(actualX * actualX + actualZ * actualZ);
      if (expectedHorizontal < 0.05 || actualHorizontal < 0.05) {
         return;
      }

      this.kbHorizontalRatioSum += actualHorizontal / expectedHorizontal;
      if (this.expectedKnockbackY > 0.1) {
         this.kbVerticalRatioSum += Math.max(0.0, actualY) / this.expectedKnockbackY;
      } else {
         this.kbVerticalRatioSum += 1.0;
      }

      this.kbSamples++;
   }

   public void markTeleported() {
      this.teleportedSinceLastMove = true;
      this.expectedKnockbackTicksLeft = 0;
      this.deltaX = 0.0;
      this.deltaY = 0.0;
      this.deltaZ = 0.0;
      this.deltaXZ = 0.0;
   }

   public void updateSafePosition() {
      this.safeX = this.x;
      this.safeY = this.y;
      this.safeZ = this.z;
      this.hasSafePosition = true;
   }

   public long getReceiveNanos() {
      return this.receiveNanos;
   }

   public long getReceiveNanosLastFlying() {
      return this.receiveNanosLastFlying;
   }

   public long getReceiveNanosLastAttack() {
      return this.receiveNanosLastAttack;
   }

   public long getReceiveNanosLastSwing() {
      return this.receiveNanosLastSwing;
   }

   public long getReceiveNanosLastDig() {
      return this.receiveNanosLastDig;
   }

   public long getReceiveNanosLastUse() {
      return this.receiveNanosLastUse;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public double getLastX() {
      return this.lastX;
   }

   public double getLastY() {
      return this.lastY;
   }

   public double getLastZ() {
      return this.lastZ;
   }

   public float getYaw() {
      return this.yaw;
   }

   public float getPitch() {
      return this.pitch;
   }

   public float getLastYaw() {
      return this.lastYaw;
   }

   public float getLastPitch() {
      return this.lastPitch;
   }

   public boolean isOnGround() {
      return this.onGround;
   }

   public boolean wasOnGround() {
      return this.lastOnGround;
   }

   public boolean isClientSprinting() {
      return this.clientSprinting;
   }

   public void setClientSprinting(boolean sprinting) {
      this.clientSprinting = sprinting;
   }

   public boolean isClientSneaking() {
      return this.clientSneaking;
   }

   public void setClientSneaking(boolean sneaking) {
      this.clientSneaking = sneaking;
   }

   public boolean isEating() {
      return this.eating;
   }

   public void setEating(boolean eating) {
      this.eating = eating;
   }

   public boolean isBlocking() {
      return this.blocking;
   }

   public void setBlocking(boolean blocking) {
      this.blocking = blocking;
   }

   public boolean isDigging() {
      return this.digging;
   }

   public double getDeltaX() {
      return this.deltaX;
   }

   public double getDeltaY() {
      return this.deltaY;
   }

   public double getDeltaZ() {
      return this.deltaZ;
   }

   public double getDeltaXZ() {
      return this.deltaXZ;
   }

   public double getLastDeltaY() {
      return this.lastDeltaY;
   }

   public double getLastDeltaXZ() {
      return this.lastDeltaXZ;
   }

   public double getYawDelta() {
      return this.yawDelta;
   }

   public double getPitchDelta() {
      return this.pitchDelta;
   }

   public boolean hasMovedThisTick() {
      return this.movedThisTick;
   }

   public boolean hasTeleportedSinceLastMove() {
      return this.teleportedSinceLastMove;
   }

   public long getFlyingPacketCount() {
      return this.flyingPacketCount;
   }

   public long getTimerPacketsSinceBalance() {
      return this.timerPacketsSinceBalance;
   }

   public long getTimerElapsedNanosSinceBalance() {
      return this.timerElapsedNanosSinceBalance;
   }

   public void resetTimerBalance() {
      this.timerPacketsSinceBalance = 0;
      this.timerLastBalanceNanos = System.nanoTime();
      this.timerElapsedNanosSinceBalance = 0L;
   }

   public Deque<Long> getClickIntervals() {
      return this.clickIntervals;
   }

   public int getClicksThisSecond() {
      return this.clicksThisSecond;
   }

   public void clearClickWindow() {
      this.clicksThisSecond = 0;
      this.secondWindowStart = System.nanoTime();
   }

   public double getExpectedKnockbackX() {
      return this.expectedKnockbackX;
   }

   public double getExpectedKnockbackY() {
      return this.expectedKnockbackY;
   }

   public double getExpectedKnockbackZ() {
      return this.expectedKnockbackZ;
   }

   public double getKbHorizontalRatioSum() {
      return this.kbHorizontalRatioSum;
   }

   public double getKbVerticalRatioSum() {
      return this.kbVerticalRatioSum;
   }

   public int getKbSamples() {
      return this.kbSamples;
   }

   public void resetKbSamples() {
      this.kbHorizontalRatioSum = 0.0;
      this.kbVerticalRatioSum = 0.0;
      this.kbSamples = 0;
   }

   public double getSafeX() {
      return this.safeX;
   }

   public double getSafeY() {
      return this.safeY;
   }

   public double getSafeZ() {
      return this.safeZ;
   }

   public boolean hasSafePosition() {
      return this.hasSafePosition;
   }

   public long getLastAlertNanos() {
      return this.lastAlertNanos;
   }

   public void setLastAlertNanos(long nanos) {
      this.lastAlertNanos = nanos;
   }

   public long getPingNanos() {
      return this.pingNanos;
   }

   public int getPingMillis() {
      return this.pingMillis;
   }

   public int getJitterMillis() {
      return this.jitterMillis;
   }

   public double getVictimX() {
      return this.victimX;
   }

   public double getVictimY() {
      return this.victimY;
   }

   public double getVictimZ() {
      return this.victimZ;
   }

   public boolean hasVictim() {
      return this.hasVictim;
   }
}
