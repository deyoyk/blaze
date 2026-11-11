package org.bukkit;

public interface BlockChangeDelegate {
   @Deprecated
   boolean setRawTypeId(int var1, int var2, int var3, int var4);

   @Deprecated
   boolean setRawTypeIdAndData(int var1, int var2, int var3, int var4, int var5);

   @Deprecated
   boolean setTypeId(int var1, int var2, int var3, int var4);

   @Deprecated
   boolean setTypeIdAndData(int var1, int var2, int var3, int var4, int var5);

   @Deprecated
   int getTypeId(int var1, int var2, int var3);

   int getHeight();

   boolean isEmpty(int var1, int var2, int var3);
}
