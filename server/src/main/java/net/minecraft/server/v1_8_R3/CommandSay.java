package net.minecraft.server.v1_8_R3;

import java.util.List;

public class CommandSay extends CommandAbstract {
   @Override
   public String getCommand() {
      return "say";
   }

   @Override
   public int a() {
      return 1;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.say.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (var2.length > 0 && var2[0].length() > 0) {
         IChatBaseComponent var3 = b(var1, var2, 0, true);
         MinecraftServer.getServer().getPlayerList().sendMessage(new ChatMessage("chat.type.announcement", var1.getScoreboardDisplayName(), var3));
      } else {
         throw new ExceptionUsage("commands.say.usage");
      }
   }

   @Override
   public List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3) {
      return var2.length >= 1 ? a(var2, MinecraftServer.getServer().getPlayers()) : null;
   }
}
