package net.minecraft.server.v1_8_R3;

public class CommandList extends CommandAbstract {
   @Override
   public String getCommand() {
      return "list";
   }

   @Override
   public int a() {
      return 0;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.players.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      int var3 = MinecraftServer.getServer().I();
      var1.sendMessage(new ChatMessage("commands.players.list", var3, MinecraftServer.getServer().J()));
      var1.sendMessage(new ChatComponentText(MinecraftServer.getServer().getPlayerList().b(var2.length > 0 && "uuids".equalsIgnoreCase(var2[0]))));
      var1.a(CommandObjectiveExecutor.EnumCommandResult.QUERY_RESULT, var3);
   }
}
