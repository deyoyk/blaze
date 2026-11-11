package org.bukkit.inventory.meta;

import java.util.List;

public interface BookMeta extends ItemMeta {
   boolean hasTitle();

   String getTitle();

   boolean setTitle(String var1);

   boolean hasAuthor();

   String getAuthor();

   void setAuthor(String var1);

   boolean hasPages();

   String getPage(int var1);

   void setPage(int var1, String var2);

   List<String> getPages();

   void setPages(List<String> var1);

   void setPages(String... var1);

   void addPage(String... var1);

   int getPageCount();

   BookMeta clone();
}
