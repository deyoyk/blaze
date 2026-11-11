package org.bukkit.conversations;

public interface Prompt extends Cloneable {
   Prompt END_OF_CONVERSATION = null;

   String getPromptText(ConversationContext var1);

   boolean blocksForInput(ConversationContext var1);

   Prompt acceptInput(ConversationContext var1, String var2);
}
