package org.bukkit.craftbukkit.v1_8_R3;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Set;
import java.util.logging.Level;
import net.minecraft.server.v1_8_R3.IpBanEntry;
import net.minecraft.server.v1_8_R3.IpBanList;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

public class CraftIpBanList implements org.bukkit.BanList {
   private final IpBanList list;

   public CraftIpBanList(IpBanList list) {
      this.list = list;
   }

   @Override
   public org.bukkit.BanEntry getBanEntry(String target) {
      Validate.notNull(target, "Target cannot be null");
      IpBanEntry entry = (IpBanEntry)this.list.get(target);
      return entry == null ? null : new CraftIpBanEntry(target, entry, this.list);
   }

   @Override
   public org.bukkit.BanEntry addBan(String target, String reason, Date expires, String source) {
      Validate.notNull(target, "Ban target cannot be null");
      IpBanEntry entry = new IpBanEntry(target, new Date(), StringUtils.isBlank(source) ? null : source, expires, StringUtils.isBlank(reason) ? null : reason);
      this.list.add(entry);

      try {
         this.list.save();
      } catch (IOException var7) {
         org.bukkit.Bukkit.getLogger().log(Level.SEVERE, "Failed to save banned-ips.json, {0}", var7.getMessage());
      }

      return new CraftIpBanEntry(target, entry, this.list);
   }

   @Override
   public Set<org.bukkit.BanEntry> getBanEntries() {
      Builder<org.bukkit.BanEntry> builder = ImmutableSet.builder();

      String[] var5;
      for (String target : var5 = this.list.getEntries()) {
         builder.add(new CraftIpBanEntry(target, (IpBanEntry)this.list.get(target), this.list));
      }

      return builder.build();
   }

   @Override
   public boolean isBanned(String target) {
      Validate.notNull(target, "Target cannot be null");
      return this.list.isBanned(InetSocketAddress.createUnresolved(target, 0));
   }

   @Override
   public void pardon(String target) {
      Validate.notNull(target, "Target cannot be null");
      this.list.remove(target);
   }
}
