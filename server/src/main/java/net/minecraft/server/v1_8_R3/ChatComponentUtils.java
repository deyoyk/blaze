package net.minecraft.server.v1_8_R3;

import java.util.List;

public class ChatComponentUtils {
   public static IChatBaseComponent filterForDisplay(ICommandListener var0, IChatBaseComponent var1, Entity var2) throws CommandException {
      Object var3 = null;
      if (var1 instanceof ChatComponentScore) {
         ChatComponentScore var4 = (ChatComponentScore)var1;
         String var5 = var4.g();
         if (PlayerSelector.isPattern(var5)) {
            List var6 = PlayerSelector.getPlayers(var0, var5, Entity.class);
            if (var6.size() != 1) {
               throw new ExceptionEntityNotFound();
            }

            var5 = ((Entity)var6.get(0)).getName();
         }

         var3 = var2 != null && var5.equals("*") ? new ChatComponentScore(var2.getName(), var4.h()) : new ChatComponentScore(var5, var4.h());
         ((ChatComponentScore)var3).b(var4.getText());
      } else if (var1 instanceof ChatComponentSelector) {
         String var8 = ((ChatComponentSelector)var1).g();
         var3 = PlayerSelector.getPlayerNames(var0, var8);
         if (var3 == null) {
            var3 = new ChatComponentText("");
         }
      } else if (var1 instanceof ChatComponentText) {
         var3 = new ChatComponentText(((ChatComponentText)var1).g());
      } else {
         if (!(var1 instanceof ChatMessage)) {
            return var1;
         }

         Object[] var9 = ((ChatMessage)var1).j();

         for (int var11 = 0; var11 < var9.length; var11++) {
            Object var13 = var9[var11];
            if (var13 instanceof IChatBaseComponent) {
               var9[var11] = filterForDisplay(var0, (IChatBaseComponent)var13, var2);
            }
         }

         var3 = new ChatMessage(((ChatMessage)var1).i(), var9);
      }

      ChatModifier var10 = var1.getChatModifier();
      if (var10 != null) {
         ((IChatBaseComponent)var3).setChatModifier(var10.clone());
      }

      for (IChatBaseComponent var14 : var1.a()) {
         ((IChatBaseComponent)var3).addSibling(filterForDisplay(var0, var14, var2));
      }

      return (IChatBaseComponent)var3;
   }
}
