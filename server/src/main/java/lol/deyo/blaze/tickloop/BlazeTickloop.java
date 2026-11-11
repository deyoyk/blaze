package lol.deyo.blaze.tickloop;

import java.util.Arrays;
import java.util.concurrent.locks.LockSupport;
import net.minecraft.server.v1_8_R3.CrashReport;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.ServerPing.ServerData;
import net.minecrell.terminalconsole.TerminalConsoleAppender;
import org.spigotmc.WatchdogThread;

public class BlazeTickloop implements Runnable {
   private static final long TICK_TIME_IN_NANO = 50000000L;
   private static final long TIME_SLEEP = 100000L;
   private static final long SLEEP_DRIFT = 500000L;
   private static final long SLEEP_MINIMUM_TIME = 600000L;
   private final MinecraftServer server;

   public BlazeTickloop(MinecraftServer server) {
      this.server = server;
   }

   @Override
   public void run() {
      try {
         if (this.server.init()) {
            this.server.ab = MinecraftServer.az();
            this.server.r.setServerInfo(new ServerData("1.8.8", 47));
            Arrays.fill(this.server.recentTps, 20.0);
            long nextTickTime = System.nanoTime();
            this.server.tickSection = nextTickTime;
            this.server.lastTick = nextTickTime - 50000000L;

            while (this.server.isRunning()) {
               long timeLeftForTick = nextTickTime - System.nanoTime();
               if (timeLeftForTick <= 0L) {
                  nextTickTime += 50000000L;
                  this.server.startTick();
                  this.server.processPackets();
                  this.server.A();
                  this.server.processPackets();
                  this.server.endTick();
                  timeLeftForTick = nextTickTime - System.nanoTime();
               } else {
                  this.server.processPackets();
               }

               if (timeLeftForTick > 600000L && this.server.j.isEmpty()) {
                  Thread.yield();
                  LockSupport.parkNanos(100000L);
               }
            }
         } else {
            this.server.a((CrashReport)null);
         }
      } catch (Throwable var68) {
         this.server.crash(var68);
      } finally {
         try {
            WatchdogThread.doStop();
            this.server.isStopped = true;
            this.server.stop();
         } catch (Throwable var66) {
            MinecraftServer.LOGGER.error("Exception stopping the server", var66);
         } finally {
            try {
               TerminalConsoleAppender.close();
            } catch (Exception var65) {
            }

            this.server.z();
         }
      }
   }
}
