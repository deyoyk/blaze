package net.minecraft.server.v1_8_R3;

import java.util.List;

public class CommandGive extends CommandAbstract {
   @Override
   public String getCommand() {
      return "give";
   }

   @Override
   public int a() {
      return 2;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.give.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (var2.length < 2) {
         throw new ExceptionUsage("commands.give.usage");
      } else {
         EntityPlayer var3 = a(var1, var2[0]);
         Item var4 = f(var1, var2[1]);
         int var5 = var2.length >= 3 ? a(var2[2], 1, 64) : 1;
         int var6 = var2.length >= 4 ? a(var2[3]) : 0;
         ItemStack var7 = new ItemStack(var4, var5, var6);
         if (var2.length >= 5) {
            String var8 = a(var1, var2, 4).c();

            try {
               var7.setTag(MojangsonParser.parse(var8));
            } catch (MojangsonParseException var10) {
               throw new CommandException("commands.give.tagError", var10.getMessage());
            }
         }

         boolean var11 = var3.inventory.pickup(var7);
         if (var11) {
            var3.world.makeSound(var3, "random.pop", 0.2F, ((var3.bc().nextFloat() - var3.bc().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            var3.defaultContainer.b();
         }

         if (var11 && var7.count <= 0) {
            var7.count = 1;
            var1.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_ITEMS, var5);
            EntityItem var12 = var3.drop(var7, false);
            if (var12 != null) {
               var12.v();
            }
         } else {
            var1.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_ITEMS, var5 - var7.count);
            EntityItem var9 = var3.drop(var7, false);
            if (var9 != null) {
               var9.q();
               var9.b(var3.getName());
            }
         }

         a(var1, this, "commands.give.success", new Object[]{var7.C(), var5, var3.getName()});
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
