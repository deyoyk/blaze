package org.bukkit.block;

public interface Sign extends BlockState {
   String[] getLines();

   String getLine(int var1) throws IndexOutOfBoundsException;

   void setLine(int var1, String var2) throws IndexOutOfBoundsException;
}
