package net.minecraft.server.v1_8_R3;

import java.util.List;

public class CommandSetWorldSpawn extends CommandAbstract {
   @Override
   public String getCommand() {
      return "setworldspawn";
   }

   @Override
   public int a() {
      return 2;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.setworldspawn.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      BlockPosition var3;
      if (var2.length == 0) {
         var3 = b(var1).getChunkCoordinates();
      } else {
         if (var2.length != 3 || var1.getWorld() == null) {
            throw new ExceptionUsage("commands.setworldspawn.usage");
         }

         var3 = a(var1, var2, 0, true);
      }

      var1.getWorld().B(var3);
      MinecraftServer.getServer().getPlayerList().sendAll(new PacketPlayOutSpawnPosition(var3));
      a(var1, this, "commands.setworldspawn.success", new Object[]{var3.getX(), var3.getY(), var3.getZ()});
   }

   @Override
   public List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3) {
      return var2.length > 0 && var2.length <= 3 ? a(var2, 0, var3) : null;
   }
}
