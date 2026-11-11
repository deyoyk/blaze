package org.spigotmc;

import java.io.File;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class RestartCommand extends Command {
   public RestartCommand(String name) {
      super(name);
      this.description = "Restarts the server";
      this.usageMessage = "/restart";
      this.setPermission("bukkit.command.restart");
   }

   public boolean execute(CommandSender sender, String currentAlias, String[] args) {
      if (this.testPermission(sender)) {
         MinecraftServer.getServer().postToMainThread(org.spigotmc.RestartCommand::restart);
      }

      return true;
   }

   public static void restart() {
      restart(new File(org.spigotmc.SpigotConfig.restartScript));
   }

   public static void restart(File script) {
      org.spigotmc.AsyncCatcher.enabled = false;

      try {
         if (!script.isFile()) {
            System.out.println("Startup script '" + org.spigotmc.SpigotConfig.restartScript + "' does not exist! Stopping server.");
         } else {
            System.out.println("Attempting to restart with " + org.spigotmc.SpigotConfig.restartScript);
            org.spigotmc.WatchdogThread.doStop();

            for (EntityPlayer p : MinecraftServer.getServer().getPlayerList().players) {
               p.playerConnection.disconnect(org.spigotmc.SpigotConfig.restartMessage);
            }

            try {
               Thread.sleep(100L);
            } catch (InterruptedException var5) {
            }

            MinecraftServer.getServer().getServerConnection().b();

            try {
               Thread.sleep(100L);
            } catch (InterruptedException var4) {
            }

            try {
               MinecraftServer.getServer().stop();
            } catch (Throwable var3) {
            }

            Thread shutdownHook = new Thread(() -> {
               try {
                  String os = System.getProperty("os.name").toLowerCase();
                  if (os.contains("win")) {
                     Runtime.getRuntime().exec("cmd /c start " + script.getPath());
                  } else {
                     Runtime.getRuntime().exec(new String[]{"sh", script.getPath()});
                  }
               } catch (Exception var2) {
                  var2.printStackTrace();
               }
            });
            shutdownHook.setDaemon(true);
            Runtime.getRuntime().addShutdownHook(shutdownHook);
         }

         System.exit(0);
      } catch (Exception var6) {
         var6.printStackTrace();
      }
   }
}
