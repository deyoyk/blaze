package org.bukkit.conversations;

public interface Conversable {
   boolean isConversing();

   void acceptConversationInput(String var1);

   boolean beginConversation(Conversation var1);

   void abandonConversation(Conversation var1);

   void abandonConversation(Conversation var1, ConversationAbandonedEvent var2);

   void sendRawMessage(String var1);
}
