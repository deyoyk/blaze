package net.minecraft.server.v1_8_R3;

import java.util.List;

public interface ICommand extends Comparable<ICommand> {
   String getCommand();

   String getUsage(ICommandListener var1);

   List<String> b();

   void execute(ICommandListener var1, String[] var2) throws CommandException;

   boolean canUse(ICommandListener var1);

   List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3);

   boolean isListStart(String[] var1, int var2);
}
