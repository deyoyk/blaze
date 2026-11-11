package org.bukkit.conversations;

public abstract class StringPrompt implements Prompt {
   @Override
   public boolean blocksForInput(ConversationContext context) {
      return true;
   }
}
