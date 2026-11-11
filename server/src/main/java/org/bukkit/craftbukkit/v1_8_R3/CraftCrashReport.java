package org.bukkit.craftbukkit.v1_8_R3;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class CraftCrashReport implements Callable<String> {
   public String call() throws Exception {
      StringWriter value = new StringWriter();

      try {
         value.append("\n   Running: ")
            .append(org.bukkit.Bukkit.getName())
            .append(" version ")
            .append(org.bukkit.Bukkit.getVersion())
            .append(" (Implementing API version ")
            .append(org.bukkit.Bukkit.getBukkitVersion())
            .append(") ")
            .append(String.valueOf(MinecraftServer.getServer().getOnlineMode()));
         value.append("\n   Plugins: {");

         Plugin[] var5;
         for (Plugin plugin : var5 = org.bukkit.Bukkit.getPluginManager().getPlugins()) {
            PluginDescriptionFile description = plugin.getDescription();
            value.append(' ')
               .append(description.getFullName())
               .append(' ')
               .append(description.getMain())
               .append(' ')
               .append(Arrays.toString(description.getAuthors().toArray()))
               .append(',');
         }

         value.append("}\n   Warnings: ").append(org.bukkit.Bukkit.getWarningState().name());
         value.append("\n   Reload Count: ").append(String.valueOf(MinecraftServer.getServer().server.reloadCount));
         value.append("\n   Threads: {");

         for (Entry<Thread, ? extends Object[]> entry : Thread.getAllStackTraces().entrySet()) {
            value.append(' ')
               .append(entry.getKey().getState().name())
               .append(' ')
               .append(entry.getKey().getName())
               .append(": ")
               .append(Arrays.toString(entry.getValue()))
               .append(',');
         }

         value.append("}\n   ").append(org.bukkit.Bukkit.getScheduler().toString());
      } catch (Throwable var7) {
         value.append("\n   Failed to handle CraftCrashReport:\n");
         PrintWriter writer = new PrintWriter(value);
         var7.printStackTrace(writer);
         writer.flush();
      }

      return value.toString();
   }
}
