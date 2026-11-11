package net.minecraft.server.v1_8_R3;

import java.util.List;

public class CommandMe extends CommandAbstract {
   @Override
   public String getCommand() {
      return "me";
   }

   @Override
   public int a() {
      return 0;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.me.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (var2.length <= 0) {
         throw new ExceptionUsage("commands.me.usage");
      } else {
         IChatBaseComponent var3 = b(var1, var2, 0, !(var1 instanceof EntityHuman));
         MinecraftServer.getServer().getPlayerList().sendMessage(new ChatMessage("chat.type.emote", var1.getScoreboardDisplayName(), var3));
      }
   }

   @Override
   public List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3) {
      return a(var2, MinecraftServer.getServer().getPlayers());
   }
}
