package net.minecraft.server.v1_8_R3;

import java.util.List;

public class CommandTestFor extends CommandAbstract {
   @Override
   public String getCommand() {
      return "testfor";
   }

   @Override
   public int a() {
      return 2;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.testfor.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (var2.length < 1) {
         throw new ExceptionUsage("commands.testfor.usage");
      } else {
         Entity var3 = b(var1, var2[0]);
         NBTTagCompound var4 = null;
         if (var2.length >= 2) {
            try {
               var4 = MojangsonParser.parse(a(var2, 1));
            } catch (MojangsonParseException var6) {
               throw new CommandException("commands.testfor.tagError", var6.getMessage());
            }
         }

         if (var4 != null) {
            NBTTagCompound var5 = new NBTTagCompound();
            var3.e(var5);
            if (!GameProfileSerializer.a(var4, var5, true)) {
               throw new CommandException("commands.testfor.failure", var3.getName());
            }
         }

         a(var1, this, "commands.testfor.success", new Object[]{var3.getName()});
      }
   }

   @Override
   public boolean isListStart(String[] var1, int var2) {
      return var2 == 0;
   }

   @Override
   public List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3) {
      return var2.length == 1 ? a(var2, MinecraftServer.getServer().getPlayers()) : null;
   }
}
