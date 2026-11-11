package net.minecraft.server.v1_8_R3;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommandHelp extends CommandAbstract {
   @Override
   public String getCommand() {
      return "help";
   }

   @Override
   public int a() {
      return 0;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.help.usage";
   }

   @Override
   public List<String> b() {
      return Arrays.asList("?");
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      List var3 = this.d(var1);
      byte var4 = 7;
      int var5 = (var3.size() - 1) / 7;
      int var6 = 0;

      try {
         var6 = var2.length == 0 ? 0 : a(var2[0], 1, var5 + 1) - 1;
      } catch (ExceptionInvalidNumber var12) {
         Map var8 = this.d();
         ICommand var9 = (ICommand)var8.get(var2[0]);
         if (var9 != null) {
            throw new ExceptionUsage(var9.getUsage(var1));
         }

         if (MathHelper.a(var2[0], -1) != -1) {
            throw var12;
         }

         throw new ExceptionUnknownCommand();
      }

      int var7 = Math.min((var6 + 1) * 7, var3.size());
      ChatMessage var14 = new ChatMessage("commands.help.header", var6 + 1, var5 + 1);
      var14.getChatModifier().setColor(EnumChatFormat.DARK_GREEN);
      var1.sendMessage(var14);

      for (int var15 = var6 * 7; var15 < var7; var15++) {
         ICommand var10 = (ICommand)var3.get(var15);
         ChatMessage var11 = new ChatMessage(var10.getUsage(var1));
         var11.getChatModifier().setChatClickable(new ChatClickable(ChatClickable.EnumClickAction.SUGGEST_COMMAND, "/" + var10.getCommand() + " "));
         var1.sendMessage(var11);
      }

      if (var6 == 0 && var1 instanceof EntityHuman) {
         ChatMessage var16 = new ChatMessage("commands.help.footer");
         var16.getChatModifier().setColor(EnumChatFormat.GREEN);
         var1.sendMessage(var16);
      }
   }

   protected List<ICommand> d(ICommandListener var1) {
      List var2 = MinecraftServer.getServer().getCommandHandler().a(var1);
      Collections.sort(var2);
      return var2;
   }

   protected Map<String, ICommand> d() {
      return MinecraftServer.getServer().getCommandHandler().getCommands();
   }

   @Override
   public List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3) {
      if (var2.length == 1) {
         Set<String> var4 = this.d().keySet();
         return a(var2, var4.toArray(new String[var4.size()]));
      } else {
         return null;
      }
   }
}
