package lol.deyo.blaze.command;

import lol.deyo.blaze.configs.LanguageConfig;
import lol.deyo.blaze.framework.commands.Command;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import net.jafama.FastMath;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class TPSCommand extends Command {
   private static final long STARTUP_TIME = System.currentTimeMillis();

   public TPSCommand() {
      super("tps");
      this.setPermission("blaze.tps");
      this.setAliases(Arrays.asList("tickspersecond", "stps", "servertps"));
   }

   @Override
   public void execute(CommandSender sender, String[] args) {
      Set<Thread> threads = Thread.getAllStackTraces().keySet();
      double[] tps = MinecraftServer.getServer().recentTps;
      long totalMemory = Runtime.getRuntime().totalMemory() / 1048576L;
      long freeMemory = Runtime.getRuntime().freeMemory() / 1048576L;
      long aliveThreads = threads.stream().filter(Thread::isAlive).count();
      long interruptedThreads = threads.stream().filter(Thread::isInterrupted).count();
      long daemonThreads = threads.stream().filter(Thread::isDaemon).count();
      int chunks = 0;
      int entities = 0;
      int tiles = 0;

      for (WorldServer world : MinecraftServer.getServer().worlds) {
         entities += world.entityList.size();
         tiles += world.tileEntityList.size();
         chunks += world.chunkProviderServer.chunks.size();
      }

      for (String string : LanguageConfig.tpsMessage.getValue()) {
         this.sendMessage(
            sender,
            new String[]{
               string.replace("%server_tps%", Arrays.stream(tps).mapToObj(this::format).collect(Collectors.joining(", ")))
                  .replace("%uptime%", this.formatTime(System.currentTimeMillis() - STARTUP_TIME).trim())
                  .replace("%players%", String.valueOf(MinecraftServer.getServer().getPlayerList().players.size()))
                  .replace("%max_players%", String.valueOf(MinecraftServer.getServer().getPlayerList().maxPlayers))
                  .replace("%threads%", String.valueOf(threads.size()))
                  .replace("%alive%", String.valueOf(aliveThreads))
                  .replace("%interrupted%", String.valueOf(interruptedThreads))
                  .replace("%daemon%", String.valueOf(daemonThreads))
                  .replace("%entities%", Integer.toString(entities))
                  .replace("%tiles%", Integer.toString(tiles))
                  .replace("%chunks%", Integer.toString(chunks))
                  .replace("%free_memory%", Long.toString(freeMemory))
                  .replace("%max_memory%", Long.toString(totalMemory))
            }
         );
      }
   }

   private String formatTime(long millis) {
      long days = TimeUnit.MILLISECONDS.toDays(millis);
      millis -= TimeUnit.DAYS.toMillis(days);
      long hours = TimeUnit.MILLISECONDS.toHours(millis);
      millis -= TimeUnit.HOURS.toMillis(hours);
      long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
      millis -= TimeUnit.MINUTES.toMillis(minutes);
      long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
      StringBuilder sb = new StringBuilder(64);
      if (days > 0L) {
         sb.append(days).append("d ");
      }

      if (hours > 0L) {
         sb.append(hours).append("h ");
      }

      if (minutes > 0L) {
         sb.append(minutes).append("m ");
      }

      if (seconds > 0L) {
         sb.append(seconds).append("s ");
      }

      return sb.toString();
   }

   private String format(double tps) {
      return (tps > 18.0 ? ChatColor.GREEN : (tps > 16.0 ? ChatColor.YELLOW : ChatColor.RED))
         + (tps > 20.0 ? "*" : "")
         + Math.min(FastMath.round(tps * 100.0) / 100.0, 20.0);
   }
}
