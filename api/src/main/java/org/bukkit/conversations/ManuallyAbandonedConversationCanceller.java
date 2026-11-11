package org.bukkit.conversations;

public class ManuallyAbandonedConversationCanceller implements ConversationCanceller {
   @Override
   public void setConversation(Conversation conversation) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean cancelBasedOnInput(ConversationContext context, String input) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ConversationCanceller clone() {
      throw new UnsupportedOperationException();
   }
}
