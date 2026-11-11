package net.minecraft.server.v1_8_R3;

import com.google.gson.JsonParseException;
import java.util.List;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class CommandTellRaw extends CommandAbstract {
   @Override
   public String getCommand() {
      return "tellraw";
   }

   @Override
   public int a() {
      return 2;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.tellraw.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (var2.length < 2) {
         throw new ExceptionUsage("commands.tellraw.usage");
      } else {
         EntityPlayer var3 = a(var1, var2[0]);
         String var4 = a(var2, 1);

         try {
            IChatBaseComponent var5 = IChatBaseComponent.ChatSerializer.a(var4);
            var3.sendMessage(ChatComponentUtils.filterForDisplay(var1, var5, var3));
         } catch (JsonParseException var7) {
            Throwable var6 = ExceptionUtils.getRootCause(var7);
            throw new ExceptionInvalidSyntax("commands.tellraw.jsonException", var6 == null ? "" : var6.getMessage());
         }
      }
   }

   @Override
   public List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3) {
      return var2.length == 1 ? a(var2, MinecraftServer.getServer().getPlayers()) : null;
   }

   @Override
   public boolean isListStart(String[] var1, int var2) {
      return var2 == 0;
   }
}
