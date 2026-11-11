package net.minecraft.server.v1_8_R3;

public class CommandIdleTimeout extends CommandAbstract {
   @Override
   public String getCommand() {
      return "setidletimeout";
   }

   @Override
   public int a() {
      return 3;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.setidletimeout.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (var2.length != 1) {
         throw new ExceptionUsage("commands.setidletimeout.usage");
      } else {
         int var3 = a(var2[0], 0);
         MinecraftServer.getServer().setIdleTimeout(var3);
         a(var1, this, "commands.setidletimeout.success", new Object[]{var3});
      }
   }
}
