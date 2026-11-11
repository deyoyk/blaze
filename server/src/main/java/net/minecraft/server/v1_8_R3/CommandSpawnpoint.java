package net.minecraft.server.v1_8_R3;

import java.util.List;

public class CommandSpawnpoint extends CommandAbstract {
   @Override
   public String getCommand() {
      return "spawnpoint";
   }

   @Override
   public int a() {
      return 2;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.spawnpoint.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (var2.length > 1 && var2.length < 4) {
         throw new ExceptionUsage("commands.spawnpoint.usage");
      } else {
         EntityPlayer var3 = var2.length > 0 ? a(var1, var2[0]) : b(var1);
         BlockPosition var4 = var2.length > 3 ? a(var1, var2, 1, true) : var3.getChunkCoordinates();
         if (var3.world != null) {
            var3.setRespawnPosition(var4, true);
            a(var1, this, "commands.spawnpoint.success", new Object[]{var3.getName(), var4.getX(), var4.getY(), var4.getZ()});
         }
      }
   }

   @Override
   public List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3) {
      if (var2.length == 1) {
         return a(var2, MinecraftServer.getServer().getPlayers());
      } else {
         return var2.length > 1 && var2.length <= 4 ? a(var2, 1, var3) : null;
      }
   }

   @Override
   public boolean isListStart(String[] var1, int var2) {
      return var2 == 0;
   }
}
