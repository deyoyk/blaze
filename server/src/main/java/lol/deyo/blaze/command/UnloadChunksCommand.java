package lol.deyo.blaze.command;

import lol.deyo.blaze.configs.LanguageConfig;
import lol.deyo.blaze.framework.commands.Command;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.event.world.ChunkUnloadEvent;

public class UnloadChunksCommand extends Command {
   public UnloadChunksCommand() {
      super("unloadchunks");
      this.setPermission("blaze.unloadchunks");
      this.setAliases(Arrays.asList("chunkunload", "chunksunload"));
   }

   @Override
   public void execute(CommandSender sender, String[] args) {
      int chunks = 0;

      for (World world : Bukkit.getServer().getWorlds()) {
         Chunk[] var9;
         for (Chunk chunk : var9 = world.getLoadedChunks()) {
            if (!world.isChunkInUse(chunk.getX(), chunk.getZ()) && chunk.unload(true)) {
               ChunkUnloadEvent event = new ChunkUnloadEvent(chunk);
               Bukkit.getPluginManager().callEvent(event);
               chunks++;
            }
         }
      }

      this.sendMessage(sender, new String[]{LanguageConfig.unloadedChunks.getValue().replace("%amount%", String.valueOf(chunks))});
   }
}
