package lol.deyo.blaze.command;

import lol.deyo.blaze.configs.LanguageConfig;
import lol.deyo.blaze.framework.commands.Command;
import java.util.Arrays;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityItemFrame;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.command.CommandSender;

public class ClearLagCommand extends Command {
   public ClearLagCommand() {
      super("clearlag");
      this.setDescription("Remove dropped items and non-player entities");
      this.setPermission("blaze.clearlag");
      this.setAliases(Arrays.asList("killentities", "clearentities"));
   }

   @Override
   public void execute(CommandSender sender, String[] args) {
      int entities = 0;

      for (World world : MinecraftServer.getServer().worlds) {
         for (Entity entity : world.entityList) {
            if (!(entity instanceof EntityItemFrame) && !(entity instanceof EntityPlayer) && !entity.dead && !entity.hasCustomName()) {
               entity.dead = true;
               entities++;
            }
         }
      }

      this.sendMessage(sender, new String[]{LanguageConfig.clearedLag.getValue().replace("%entities%", String.valueOf(entities))});
   }
}
