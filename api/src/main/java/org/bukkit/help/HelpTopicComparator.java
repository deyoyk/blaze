package org.bukkit.help;

import java.util.Comparator;

public class HelpTopicComparator implements Comparator<HelpTopic> {
   private static final HelpTopicComparator.TopicNameComparator tnc = new HelpTopicComparator.TopicNameComparator();
   private static final HelpTopicComparator htc = new HelpTopicComparator();

   public static HelpTopicComparator.TopicNameComparator topicNameComparatorInstance() {
      return tnc;
   }

   public static HelpTopicComparator helpTopicComparatorInstance() {
      return htc;
   }

   private HelpTopicComparator() {
   }

   public int compare(HelpTopic lhs, HelpTopic rhs) {
      return tnc.compare(lhs.getName(), rhs.getName());
   }

   public static class TopicNameComparator implements Comparator<String> {
      private TopicNameComparator() {
      }

      public int compare(String lhs, String rhs) {
         boolean lhsStartSlash = lhs.startsWith("/");
         boolean rhsStartSlash = rhs.startsWith("/");
         if (lhsStartSlash && !rhsStartSlash) {
            return 1;
         } else {
            return !lhsStartSlash && rhsStartSlash ? -1 : lhs.compareToIgnoreCase(rhs);
         }
      }
   }
}
