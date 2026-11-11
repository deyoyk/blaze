package net.minecraft.server.v1_8_R3;

public class CommandStop extends CommandAbstract {
   @Override
   public String getCommand() {
      return "stop";
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.stop.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (MinecraftServer.getServer().worldServer != null) {
         a(var1, this, "commands.stop.start", new Object[0]);
      }

      MinecraftServer.getServer().safeShutdown();
   }
}
