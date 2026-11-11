package net.minecraft.server.v1_8_R3;

public class CommandPublish extends CommandAbstract {
   @Override
   public String getCommand() {
      return "publish";
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.publish.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      String var3 = MinecraftServer.getServer().a(WorldSettings.EnumGamemode.SURVIVAL, false);
      if (var3 != null) {
         a(var1, this, "commands.publish.started", new Object[]{var3});
      } else {
         a(var1, this, "commands.publish.failed", new Object[0]);
      }
   }
}
