package org.bukkit.conversations;

public class ExactMatchConversationCanceller implements ConversationCanceller {
   private String escapeSequence;

   public ExactMatchConversationCanceller(String escapeSequence) {
      this.escapeSequence = escapeSequence;
   }

   @Override
   public void setConversation(Conversation conversation) {
   }

   @Override
   public boolean cancelBasedOnInput(ConversationContext context, String input) {
      return input.equals(this.escapeSequence);
   }

   @Override
   public ConversationCanceller clone() {
      return new ExactMatchConversationCanceller(this.escapeSequence);
   }
}
