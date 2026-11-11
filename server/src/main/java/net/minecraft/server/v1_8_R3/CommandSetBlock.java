package net.minecraft.server.v1_8_R3;

import java.util.List;

public class CommandSetBlock extends CommandAbstract {
   @Override
   public String getCommand() {
      return "setblock";
   }

   @Override
   public int a() {
      return 2;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.setblock.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (var2.length < 4) {
         throw new ExceptionUsage("commands.setblock.usage");
      } else {
         var1.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_BLOCKS, 0);
         BlockPosition var3 = a(var1, var2, 0, false);
         Block var4 = CommandAbstract.g(var1, var2[3]);
         int var5 = 0;
         if (var2.length >= 5) {
            var5 = a(var2[4], 0, 15);
         }

         World var6 = var1.getWorld();
         if (!var6.isLoaded(var3)) {
            throw new CommandException("commands.setblock.outOfWorld");
         } else {
            NBTTagCompound var7 = new NBTTagCompound();
            boolean var8 = false;
            if (var2.length >= 7 && var4.isTileEntity()) {
               String var9 = a(var1, var2, 6).c();

               try {
                  var7 = MojangsonParser.parse(var9);
                  var8 = true;
               } catch (MojangsonParseException var12) {
                  throw new CommandException("commands.setblock.tagError", var12.getMessage());
               }
            }

            if (var2.length >= 6) {
               if (var2[5].equals("destroy")) {
                  var6.setAir(var3, true);
                  if (var4 == Blocks.AIR) {
                     a(var1, this, "commands.setblock.success", new Object[0]);
                     return;
                  }
               } else if (var2[5].equals("keep") && !var6.isEmpty(var3)) {
                  throw new CommandException("commands.setblock.noChange");
               }
            }

            TileEntity var13 = var6.getTileEntity(var3);
            if (var13 != null) {
               if (var13 instanceof IInventory) {
                  ((IInventory)var13).l();
               }

               var6.setTypeAndData(var3, Blocks.AIR.getBlockData(), var4 == Blocks.AIR ? 2 : 4);
            }

            IBlockData var10 = var4.fromLegacyData(var5);
            if (!var6.setTypeAndData(var3, var10, 2)) {
               throw new CommandException("commands.setblock.noChange");
            } else {
               if (var8) {
                  TileEntity var11 = var6.getTileEntity(var3);
                  if (var11 != null) {
                     var7.setInt("x", var3.getX());
                     var7.setInt("y", var3.getY());
                     var7.setInt("z", var3.getZ());
                     var11.a(var7);
                  }
               }

               var6.update(var3, var10.getBlock());
               var1.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_BLOCKS, 1);
               a(var1, this, "commands.setblock.success", new Object[0]);
            }
         }
      }
   }

   @Override
   public List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3) {
      if (var2.length > 0 && var2.length <= 3) {
         return a(var2, 0, var3);
      } else if (var2.length == 4) {
         return a(var2, Block.REGISTRY.keySet());
      } else {
         return var2.length == 6 ? a(var2, new String[]{"replace", "destroy", "keep"}) : null;
      }
   }
}
