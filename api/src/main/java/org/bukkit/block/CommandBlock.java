package org.bukkit.block;

public interface CommandBlock extends BlockState {
   String getCommand();

   void setCommand(String var1);

   String getName();

   void setName(String var1);
}
