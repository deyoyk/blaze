package org.bukkit;

import java.util.Date;

public interface BanEntry {
   String getTarget();

   Date getCreated();

   void setCreated(Date var1);

   String getSource();

   void setSource(String var1);

   Date getExpiration();

   void setExpiration(Date var1);

   String getReason();

   void setReason(String var1);

   void save();
}
