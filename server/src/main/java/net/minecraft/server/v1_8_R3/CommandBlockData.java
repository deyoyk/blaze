package net.minecraft.server.v1_8_R3;

import java.util.List;

public class CommandBlockData extends CommandAbstract {
   @Override
   public String getCommand() {
      return "blockdata";
   }

   @Override
   public int a() {
      return 2;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.blockdata.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (var2.length < 4) {
         throw new ExceptionUsage("commands.blockdata.usage");
      } else {
         var1.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_BLOCKS, 0);
         BlockPosition var3 = a(var1, var2, 0, false);
         World var4 = var1.getWorld();
         if (!var4.isLoaded(var3)) {
            throw new CommandException("commands.blockdata.outOfWorld");
         } else {
            TileEntity var5 = var4.getTileEntity(var3);
            if (var5 == null) {
               throw new CommandException("commands.blockdata.notValid");
            } else {
               NBTTagCompound var6 = new NBTTagCompound();
               var5.b(var6);
               NBTTagCompound var7 = (NBTTagCompound)var6.clone();

               NBTTagCompound var8;
               try {
                  var8 = MojangsonParser.parse(a(var1, var2, 3).c());
               } catch (MojangsonParseException var10) {
                  throw new CommandException("commands.blockdata.tagError", var10.getMessage());
               }

               var6.a(var8);
               var6.setInt("x", var3.getX());
               var6.setInt("y", var3.getY());
               var6.setInt("z", var3.getZ());
               if (var6.equals(var7)) {
                  throw new CommandException("commands.blockdata.failed", var6.toString());
               } else {
                  var5.a(var6);
                  var5.update();
                  var4.notify(var3);
                  var1.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_BLOCKS, 1);
                  a(var1, this, "commands.blockdata.success", new Object[]{var6.toString()});
               }
            }
         }
      }
   }

   @Override
   public List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3) {
      return var2.length > 0 && var2.length <= 3 ? a(var2, 0, var3) : null;
   }
}
