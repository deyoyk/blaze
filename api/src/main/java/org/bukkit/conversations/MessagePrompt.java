package org.bukkit.conversations;

public abstract class MessagePrompt implements Prompt {
   @Override
   public boolean blocksForInput(ConversationContext context) {
      return false;
   }

   @Override
   public Prompt acceptInput(ConversationContext context, String input) {
      return this.getNextPrompt(context);
   }

   protected abstract Prompt getNextPrompt(ConversationContext var1);
}
