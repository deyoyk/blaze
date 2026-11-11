package net.minecraft.server.v1_8_R3;

import java.util.List;

public class CommandGamemode extends CommandAbstract {
   @Override
   public String getCommand() {
      return "gamemode";
   }

   @Override
   public int a() {
      return 2;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.gamemode.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (var2.length <= 0) {
         throw new ExceptionUsage("commands.gamemode.usage");
      } else {
         WorldSettings.EnumGamemode var3 = this.h(var1, var2[0]);
         EntityPlayer var4 = var2.length >= 2 ? a(var1, var2[1]) : b(var1);
         var4.a(var3);
         var4.fallDistance = 0.0F;
         if (var1.getWorld().getGameRules().getBoolean("sendCommandFeedback")) {
            var4.sendMessage(new ChatMessage("gameMode.changed"));
         }

         ChatMessage var5 = new ChatMessage("gameMode." + var3.b());
         if (var4 != var1) {
            a(var1, this, 1, "commands.gamemode.success.other", new Object[]{var4.getName(), var5});
         } else {
            a(var1, this, 1, "commands.gamemode.success.self", new Object[]{var5});
         }
      }
   }

   protected WorldSettings.EnumGamemode h(ICommandListener var1, String var2) throws ExceptionInvalidNumber {
      if (var2.equalsIgnoreCase(WorldSettings.EnumGamemode.SURVIVAL.b()) || var2.equalsIgnoreCase("s")) {
         return WorldSettings.EnumGamemode.SURVIVAL;
      } else if (var2.equalsIgnoreCase(WorldSettings.EnumGamemode.CREATIVE.b()) || var2.equalsIgnoreCase("c")) {
         return WorldSettings.EnumGamemode.CREATIVE;
      } else if (var2.equalsIgnoreCase(WorldSettings.EnumGamemode.ADVENTURE.b()) || var2.equalsIgnoreCase("a")) {
         return WorldSettings.EnumGamemode.ADVENTURE;
      } else {
         return !var2.equalsIgnoreCase(WorldSettings.EnumGamemode.SPECTATOR.b()) && !var2.equalsIgnoreCase("sp")
            ? WorldSettings.a(a(var2, 0, WorldSettings.EnumGamemode.values().length - 2))
            : WorldSettings.EnumGamemode.SPECTATOR;
      }
   }

   @Override
   public List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3) {
      if (var2.length == 1) {
         return a(var2, new String[]{"survival", "creative", "adventure", "spectator"});
      } else {
         return var2.length == 2 ? a(var2, this.d()) : null;
      }
   }

   protected String[] d() {
      return MinecraftServer.getServer().getPlayers();
   }

   @Override
   public boolean isListStart(String[] var1, int var2) {
      return var2 == 1;
   }
}
