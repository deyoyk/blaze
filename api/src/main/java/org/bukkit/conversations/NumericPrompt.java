package org.bukkit.conversations;

import org.apache.commons.lang.math.NumberUtils;

public abstract class NumericPrompt extends ValidatingPrompt {
   @Override
   protected boolean isInputValid(ConversationContext context, String input) {
      return NumberUtils.isNumber(input) && this.isNumberValid(context, NumberUtils.createNumber(input));
   }

   protected boolean isNumberValid(ConversationContext context, Number input) {
      return true;
   }

   @Override
   protected Prompt acceptValidatedInput(ConversationContext context, String input) {
      try {
         return this.acceptValidatedInput(context, NumberUtils.createNumber(input));
      } catch (NumberFormatException var4) {
         return this.acceptValidatedInput(context, NumberUtils.INTEGER_ZERO);
      }
   }

   protected abstract Prompt acceptValidatedInput(ConversationContext var1, Number var2);

   @Override
   protected String getFailedValidationText(ConversationContext context, String invalidInput) {
      return NumberUtils.isNumber(invalidInput)
         ? this.getFailedValidationText(context, NumberUtils.createNumber(invalidInput))
         : this.getInputNotNumericText(context, invalidInput);
   }

   protected String getInputNotNumericText(ConversationContext context, String invalidInput) {
      return null;
   }

   protected String getFailedValidationText(ConversationContext context, Number invalidInput) {
      return null;
   }
}
