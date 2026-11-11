package net.minecraft.server.v1_8_R3;

public class CommandSaveAll extends CommandAbstract {
   @Override
   public String getCommand() {
      return "save-all";
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.save.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      MinecraftServer var3 = MinecraftServer.getServer();
      var1.sendMessage(new ChatMessage("commands.save.start"));
      if (var3.getPlayerList() != null) {
         var3.getPlayerList().savePlayers();
      }

      try {
         for (int var4 = 0; var4 < var3.worldServer.length; var4++) {
            if (var3.worldServer[var4] != null) {
               WorldServer var5 = var3.worldServer[var4];
               boolean var6 = var5.savingDisabled;
               var5.savingDisabled = false;
               var5.save(true, null);
               var5.savingDisabled = var6;
            }
         }

         if (var2.length > 0 && "flush".equals(var2[0])) {
            var1.sendMessage(new ChatMessage("commands.save.flushStart"));

            for (int var8 = 0; var8 < var3.worldServer.length; var8++) {
               if (var3.worldServer[var8] != null) {
                  WorldServer var9 = var3.worldServer[var8];
                  boolean var10 = var9.savingDisabled;
                  var9.savingDisabled = false;
                  var9.flushSave();
                  var9.savingDisabled = var10;
               }
            }

            var1.sendMessage(new ChatMessage("commands.save.flushEnd"));
         }
      } catch (ExceptionWorldConflict var7) {
         a(var1, this, "commands.save.failed", new Object[]{var7.getMessage()});
         return;
      }

      a(var1, this, "commands.save.success", new Object[0]);
   }
}
