package org.bukkit.conversations;

public class NullConversationPrefix implements ConversationPrefix {
   @Override
   public String getPrefix(ConversationContext context) {
      return "";
   }
}
