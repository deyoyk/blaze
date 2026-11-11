package net.minecraft.server.v1_8_R3;

import java.util.List;

public class CommandEnchant extends CommandAbstract {
   @Override
   public String getCommand() {
      return "enchant";
   }

   @Override
   public int a() {
      return 2;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.enchant.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (var2.length < 2) {
         throw new ExceptionUsage("commands.enchant.usage");
      } else {
         EntityPlayer var3 = a(var1, var2[0]);
         var1.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_ITEMS, 0);

         int var4;
         try {
            var4 = a(var2[1], 0);
         } catch (ExceptionInvalidNumber var12) {
            Enchantment var6 = Enchantment.getByName(var2[1]);
            if (var6 == null) {
               throw var12;
            }

            var4 = var6.id;
         }

         int var5 = 1;
         ItemStack var13 = var3.bZ();
         if (var13 == null) {
            throw new CommandException("commands.enchant.noItem");
         } else {
            Enchantment var7 = Enchantment.getById(var4);
            if (var7 == null) {
               throw new ExceptionInvalidNumber("commands.enchant.notFound", var4);
            } else if (!var7.canEnchant(var13)) {
               throw new CommandException("commands.enchant.cantEnchant");
            } else {
               if (var2.length >= 3) {
                  var5 = a(var2[2], var7.getStartLevel(), var7.getMaxLevel());
               }

               if (var13.hasTag()) {
                  NBTTagList var8 = var13.getEnchantments();
                  if (var8 != null) {
                     for (int var9 = 0; var9 < var8.size(); var9++) {
                        short var10 = var8.get(var9).getShort("id");
                        if (Enchantment.getById(var10) != null) {
                           Enchantment var11 = Enchantment.getById(var10);
                           if (!var11.a(var7)) {
                              throw new CommandException("commands.enchant.cantCombine", var7.d(var5), var11.d(var8.get(var9).getShort("lvl")));
                           }
                        }
                     }
                  }
               }

               var13.addEnchantment(var7, var5);
               a(var1, this, "commands.enchant.success", new Object[0]);
               var1.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_ITEMS, 1);
            }
         }
      }
   }

   @Override
   public List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3) {
      if (var2.length == 1) {
         return a(var2, this.d());
      } else {
         return var2.length == 2 ? a(var2, Enchantment.getEffects()) : null;
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
