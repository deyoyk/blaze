package net.minecraft.server.v1_8_R3;

public class CommandEntityData extends CommandAbstract {
   @Override
   public String getCommand() {
      return "entitydata";
   }

   @Override
   public int a() {
      return 2;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.entitydata.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (var2.length < 2) {
         throw new ExceptionUsage("commands.entitydata.usage");
      } else {
         Entity var3 = b(var1, var2[0]);
         if (var3 instanceof EntityHuman) {
            throw new CommandException("commands.entitydata.noPlayers", var3.getScoreboardDisplayName());
         } else {
            NBTTagCompound var4 = new NBTTagCompound();
            var3.e(var4);
            NBTTagCompound var5 = (NBTTagCompound)var4.clone();

            NBTTagCompound var6;
            try {
               var6 = MojangsonParser.parse(a(var1, var2, 1).c());
            } catch (MojangsonParseException var8) {
               throw new CommandException("commands.entitydata.tagError", var8.getMessage());
            }

            var6.remove("UUIDMost");
            var6.remove("UUIDLeast");
            var4.a(var6);
            if (var4.equals(var5)) {
               throw new CommandException("commands.entitydata.failed", var4.toString());
            } else {
               var3.f(var4);
               a(var1, this, "commands.entitydata.success", new Object[]{var4.toString()});
            }
         }
      }
   }

   @Override
   public boolean isListStart(String[] var1, int var2) {
      return var2 == 0;
   }
}
