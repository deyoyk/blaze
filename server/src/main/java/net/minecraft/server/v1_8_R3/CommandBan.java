package net.minecraft.server.v1_8_R3;

import com.mojang.authlib.GameProfile;
import java.util.List;

public class CommandBan extends CommandAbstract {
   @Override
   public String getCommand() {
      return "ban";
   }

   @Override
   public int a() {
      return 3;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.ban.usage";
   }

   @Override
   public boolean canUse(ICommandListener var1) {
      return MinecraftServer.getServer().getPlayerList().getProfileBans().isEnabled() && super.canUse(var1);
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (var2.length >= 1 && var2[0].length() > 0) {
         MinecraftServer var3 = MinecraftServer.getServer();
         GameProfile var4 = var3.getUserCache().getProfile(var2[0]);
         if (var4 == null) {
            throw new CommandException("commands.ban.failed", var2[0]);
         } else {
            String var5 = null;
            if (var2.length >= 2) {
               var5 = a(var1, var2, 1).c();
            }

            GameProfileBanEntry var6 = new GameProfileBanEntry(var4, null, var1.getName(), null, var5);
            var3.getPlayerList().getProfileBans().add(var6);
            EntityPlayer var7 = var3.getPlayerList().getPlayer(var2[0]);
            if (var7 != null) {
               var7.playerConnection.disconnect("You are banned from this server.");
            }

            a(var1, this, "commands.ban.success", new Object[]{var2[0]});
         }
      } else {
         throw new ExceptionUsage("commands.ban.usage");
      }
   }

   @Override
   public List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3) {
      return var2.length >= 1 ? a(var2, MinecraftServer.getServer().getPlayers()) : null;
   }
}
