package org.bukkit.conversations;

public interface ConversationCanceller extends Cloneable {
   void setConversation(Conversation var1);

   boolean cancelBasedOnInput(ConversationContext var1, String var2);

   ConversationCanceller clone();
}
