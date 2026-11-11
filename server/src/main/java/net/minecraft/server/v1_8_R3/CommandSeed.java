package net.minecraft.server.v1_8_R3;

public class CommandSeed extends CommandAbstract {
   @Override
   public boolean canUse(ICommandListener var1) {
      return MinecraftServer.getServer().T() || super.canUse(var1);
   }

   @Override
   public String getCommand() {
      return "seed";
   }

   @Override
   public int a() {
      return 2;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.seed.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      Object var3 = var1 instanceof EntityHuman ? ((EntityHuman)var1).world : MinecraftServer.getServer().getWorldServer(0);
      var1.sendMessage(new ChatMessage("commands.seed.success", ((World)var3).getSeed()));
   }
}
