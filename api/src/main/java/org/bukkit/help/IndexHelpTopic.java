package org.bukkit.help;

import java.util.Collection;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class IndexHelpTopic extends HelpTopic {
   protected String permission;
   protected String preamble;
   protected Collection<HelpTopic> allTopics;

   public IndexHelpTopic(String name, String shortText, String permission, Collection<HelpTopic> topics) {
      this(name, shortText, permission, topics, null);
   }

   public IndexHelpTopic(String name, String shortText, String permission, Collection<HelpTopic> topics, String preamble) {
      this.name = name;
      this.shortText = shortText;
      this.permission = permission;
      this.preamble = preamble;
      this.setTopicsCollection(topics);
   }

   protected void setTopicsCollection(Collection<HelpTopic> topics) {
      this.allTopics = topics;
   }

   @Override
   public boolean canSee(CommandSender sender) {
      if (sender instanceof ConsoleCommandSender) {
         return true;
      } else {
         return this.permission == null ? true : sender.hasPermission(this.permission);
      }
   }

   @Override
   public void amendCanSee(String amendedPermission) {
      this.permission = amendedPermission;
   }

   @Override
   public String getFullText(CommandSender sender) {
      StringBuilder sb = new StringBuilder();
      if (this.preamble != null) {
         sb.append(this.buildPreamble(sender));
         sb.append("\n");
      }

      for (HelpTopic topic : this.allTopics) {
         if (topic.canSee(sender)) {
            String lineStr = this.buildIndexLine(sender, topic).replace("\n", ". ");
            if (sender instanceof Player && lineStr.length() > 55) {
               sb.append(lineStr.substring(0, 52));
               sb.append("...");
            } else {
               sb.append(lineStr);
            }

            sb.append("\n");
         }
      }

      return sb.toString();
   }

   protected String buildPreamble(CommandSender sender) {
      return org.bukkit.ChatColor.GRAY + this.preamble;
   }

   protected String buildIndexLine(CommandSender sender, HelpTopic topic) {
      StringBuilder line = new StringBuilder();
      line.append(org.bukkit.ChatColor.GOLD);
      line.append(topic.getName());
      line.append(": ");
      line.append(org.bukkit.ChatColor.WHITE);
      line.append(topic.getShortText());
      return line.toString();
   }
}
