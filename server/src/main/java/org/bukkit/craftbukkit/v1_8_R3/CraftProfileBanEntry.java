package org.bukkit.craftbukkit.v1_8_R3;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import net.minecraft.server.v1_8_R3.GameProfileBanEntry;
import net.minecraft.server.v1_8_R3.GameProfileBanList;

public final class CraftProfileBanEntry implements org.bukkit.BanEntry {
   private final GameProfileBanList list;
   private final GameProfile profile;
   private Date created;
   private String source;
   private Date expiration;
   private String reason;

   public CraftProfileBanEntry(GameProfile profile, GameProfileBanEntry entry, GameProfileBanList list) {
      this.list = list;
      this.profile = profile;
      this.created = entry.getCreated() != null ? new Date(entry.getCreated().getTime()) : null;
      this.source = entry.getSource();
      this.expiration = entry.getExpires() != null ? new Date(entry.getExpires().getTime()) : null;
      this.reason = entry.getReason();
   }

   @Override
   public String getTarget() {
      return this.profile.getName();
   }

   @Override
   public Date getCreated() {
      return this.created == null ? null : (Date)this.created.clone();
   }

   @Override
   public void setCreated(Date created) {
      this.created = created;
   }

   @Override
   public String getSource() {
      return this.source;
   }

   @Override
   public void setSource(String source) {
      this.source = source;
   }

   @Override
   public Date getExpiration() {
      return this.expiration == null ? null : (Date)this.expiration.clone();
   }

   @Override
   public void setExpiration(Date expiration) {
      if (expiration != null && expiration.getTime() == new Date(0, 0, 0, 0, 0, 0).getTime()) {
         expiration = null;
      }

      this.expiration = expiration;
   }

   @Override
   public String getReason() {
      return this.reason;
   }

   @Override
   public void setReason(String reason) {
      this.reason = reason;
   }

   @Override
   public void save() {
      GameProfileBanEntry entry = new GameProfileBanEntry(this.profile, this.created, this.source, this.expiration, this.reason);
      this.list.add(entry);

      try {
         this.list.save();
      } catch (IOException var3) {
         org.bukkit.Bukkit.getLogger().log(Level.SEVERE, "Failed to save banned-players.json, {0}", var3.getMessage());
      }
   }
}
