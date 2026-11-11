package org.bukkit.conversations;

public abstract class ValidatingPrompt implements Prompt {
   @Override
   public Prompt acceptInput(ConversationContext context, String input) {
      if (this.isInputValid(context, input)) {
         return this.acceptValidatedInput(context, input);
      } else {
         String failPrompt = this.getFailedValidationText(context, input);
         if (failPrompt != null) {
            context.getForWhom().sendRawMessage(org.bukkit.ChatColor.RED + failPrompt);
         }

         return this;
      }
   }

   @Override
   public boolean blocksForInput(ConversationContext context) {
      return true;
   }

   protected abstract boolean isInputValid(ConversationContext var1, String var2);

   protected abstract Prompt acceptValidatedInput(ConversationContext var1, String var2);

   protected String getFailedValidationText(ConversationContext context, String invalidInput) {
      return null;
   }
}
