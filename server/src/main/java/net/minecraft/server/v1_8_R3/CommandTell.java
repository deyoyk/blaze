package net.minecraft.server.v1_8_R3;

import java.util.Arrays;
import java.util.List;

public class CommandTell extends CommandAbstract {
   @Override
   public List<String> b() {
      return Arrays.asList("w", "msg");
   }

   @Override
   public String getCommand() {
      return "tell";
   }

   @Override
   public int a() {
      return 0;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.message.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (var2.length < 2) {
         throw new ExceptionUsage("commands.message.usage");
      } else {
         EntityPlayer var3 = a(var1, var2[0]);
         if (var3 == var1) {
            throw new ExceptionPlayerNotFound("commands.message.sameTarget");
         } else {
            IChatBaseComponent var4 = b(var1, var2, 1, !(var1 instanceof EntityHuman));
            ChatMessage var5 = new ChatMessage("commands.message.display.incoming", var1.getScoreboardDisplayName(), var4.f());
            ChatMessage var6 = new ChatMessage("commands.message.display.outgoing", var3.getScoreboardDisplayName(), var4.f());
            var5.getChatModifier().setColor(EnumChatFormat.GRAY).setItalic(true);
            var6.getChatModifier().setColor(EnumChatFormat.GRAY).setItalic(true);
            var3.sendMessage(var5);
            var1.sendMessage(var6);
         }
      }
   }

   @Override
   public List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3) {
      return a(var2, MinecraftServer.getServer().getPlayers());
   }

   @Override
   public boolean isListStart(String[] var1, int var2) {
      return var2 == 0;
   }
}
