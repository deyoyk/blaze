package org.bukkit.help;

import java.util.Collection;
import java.util.List;

public interface HelpMap {
   HelpTopic getHelpTopic(String var1);

   Collection<HelpTopic> getHelpTopics();

   void addTopic(HelpTopic var1);

   void clear();

   void registerHelpTopicFactory(Class<?> var1, HelpTopicFactory<?> var2);

   List<String> getIgnoredPlugins();
}
