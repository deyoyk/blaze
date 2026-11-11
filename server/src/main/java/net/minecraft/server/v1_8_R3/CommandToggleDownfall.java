package net.minecraft.server.v1_8_R3;

public class CommandToggleDownfall extends CommandAbstract {
   @Override
   public String getCommand() {
      return "toggledownfall";
   }

   @Override
   public int a() {
      return 2;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.downfall.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      this.d();
      a(var1, this, "commands.downfall.success", new Object[0]);
   }

   protected void d() {
      WorldData var1 = MinecraftServer.getServer().worldServer[0].getWorldData();
      var1.setStorm(!var1.hasStorm());
   }
}
