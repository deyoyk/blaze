package net.minecraft.server.v1_8_R3;

import java.util.List;

public class CommandClear extends CommandAbstract {
   @Override
   public String getCommand() {
      return "clear";
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.clear.usage";
   }

   @Override
   public int a() {
      return 2;
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      EntityPlayer var3 = var2.length == 0 ? b(var1) : a(var1, var2[0]);
      Item var4 = var2.length >= 2 ? f(var1, var2[1]) : null;
      int var5 = var2.length >= 3 ? a(var2[2], -1) : -1;
      int var6 = var2.length >= 4 ? a(var2[3], -1) : -1;
      NBTTagCompound var7 = null;
      if (var2.length >= 5) {
         try {
            var7 = MojangsonParser.parse(a(var2, 4));
         } catch (MojangsonParseException var9) {
            throw new CommandException("commands.clear.tagError", var9.getMessage());
         }
      }

      if (var2.length >= 2 && var4 == null) {
         throw new CommandException("commands.clear.failure", var3.getName());
      } else {
         int var8 = var3.inventory.a(var4, var5, var6, var7);
         var3.defaultContainer.b();
         if (!var3.abilities.canInstantlyBuild) {
            var3.broadcastCarriedItem();
         }

         var1.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_ITEMS, var8);
         if (var8 == 0) {
            throw new CommandException("commands.clear.failure", var3.getName());
         } else {
            if (var6 == 0) {
               var1.sendMessage(new ChatMessage("commands.clear.testing", var3.getName(), var8));
            } else {
               a(var1, this, "commands.clear.success", new Object[]{var3.getName(), var8});
            }
         }
      }
   }

   @Override
   public List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3) {
      if (var2.length == 1) {
         return a(var2, this.d());
      } else {
         return var2.length == 2 ? a(var2, Item.REGISTRY.keySet()) : null;
      }
   }

   protected String[] d() {
      return MinecraftServer.getServer().getPlayers();
   }

   @Override
   public boolean isListStart(String[] var1, int var2) {
      return var2 == 0;
   }
}
