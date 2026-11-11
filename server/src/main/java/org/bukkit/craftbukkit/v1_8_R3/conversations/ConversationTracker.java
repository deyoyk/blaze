package org.bukkit.craftbukkit.v1_8_R3.conversations;

import java.util.LinkedList;
import java.util.logging.Level;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ManuallyAbandonedConversationCanceller;

public class ConversationTracker {
   private LinkedList<Conversation> conversationQueue = new LinkedList<>();

   public synchronized boolean beginConversation(Conversation conversation) {
      if (!this.conversationQueue.contains(conversation)) {
         this.conversationQueue.addLast(conversation);
         if (this.conversationQueue.getFirst() == conversation) {
            conversation.begin();
            conversation.outputNextPrompt();
            return true;
         }
      }

      return true;
   }

   public synchronized void abandonConversation(Conversation conversation, ConversationAbandonedEvent details) {
      if (!this.conversationQueue.isEmpty()) {
         if (this.conversationQueue.getFirst() == conversation) {
            conversation.abandon(details);
         }

         this.conversationQueue.remove(conversation);
         if (!this.conversationQueue.isEmpty()) {
            this.conversationQueue.getFirst().outputNextPrompt();
         }
      }
   }

   public synchronized void abandonAllConversations() {
      LinkedList<Conversation> oldQueue = this.conversationQueue;
      this.conversationQueue = new LinkedList<>();

      for (Conversation conversation : oldQueue) {
         try {
            conversation.abandon(new ConversationAbandonedEvent(conversation, new ManuallyAbandonedConversationCanceller()));
         } catch (Throwable var5) {
            org.bukkit.Bukkit.getLogger().log(Level.SEVERE, "Unexpected exception while abandoning a conversation", var5);
         }
      }
   }

   public synchronized void acceptConversationInput(String input) {
      if (this.isConversing()) {
         this.conversationQueue.getFirst().acceptInput(input);
      }
   }

   public synchronized boolean isConversing() {
      return !this.conversationQueue.isEmpty();
   }

   public synchronized boolean isConversingModaly() {
      return this.isConversing() && this.conversationQueue.getFirst().isModal();
   }
}
