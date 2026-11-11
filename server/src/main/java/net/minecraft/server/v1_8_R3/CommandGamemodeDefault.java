package net.minecraft.server.v1_8_R3;

public class CommandGamemodeDefault extends CommandGamemode {
   @Override
   public String getCommand() {
      return "defaultgamemode";
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.defaultgamemode.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (var2.length <= 0) {
         throw new ExceptionUsage("commands.defaultgamemode.usage");
      } else {
         WorldSettings.EnumGamemode var3 = this.h(var1, var2[0]);
         this.a(var3);
         a(var1, this, "commands.defaultgamemode.success", new Object[]{new ChatMessage("gameMode." + var3.b())});
      }
   }

   protected void a(WorldSettings.EnumGamemode var1) {
      MinecraftServer var2 = MinecraftServer.getServer();
      var2.setGamemode(var1);
      if (var2.getForceGamemode()) {
         for (EntityPlayer var4 : MinecraftServer.getServer().getPlayerList().v()) {
            var4.a(var1);
            var4.fallDistance = 0.0F;
         }
      }
   }
}
