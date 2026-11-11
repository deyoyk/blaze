package org.bukkit.help;

import org.bukkit.command.Command;

public interface HelpTopicFactory<TCommand extends Command> {
   HelpTopic createTopic(TCommand var1);
}
