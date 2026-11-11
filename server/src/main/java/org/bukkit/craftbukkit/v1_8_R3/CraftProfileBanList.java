package org.bukkit.craftbukkit.v1_8_R3;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.Date;
import java.util.Set;
import java.util.logging.Level;
import net.minecraft.server.v1_8_R3.GameProfileBanEntry;
import net.minecraft.server.v1_8_R3.GameProfileBanList;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

public class CraftProfileBanList implements org.bukkit.BanList {
   private final GameProfileBanList list;

   public CraftProfileBanList(GameProfileBanList list) {
      this.list = list;
   }

   @Override
   public org.bukkit.BanEntry getBanEntry(String target) {
      Validate.notNull(target, "Target cannot be null");
      GameProfile profile = MinecraftServer.getServer().getUserCache().getProfile(target);
      if (profile == null) {
         return null;
      } else {
         GameProfileBanEntry entry = (GameProfileBanEntry)this.list.get(profile);
         return entry == null ? null : new CraftProfileBanEntry(profile, entry, this.list);
      }
   }

   @Override
   public org.bukkit.BanEntry addBan(String target, String reason, Date expires, String source) {
      Validate.notNull(target, "Ban target cannot be null");
      GameProfile profile = MinecraftServer.getServer().getUserCache().getProfile(target);
      if (profile == null) {
         return null;
      } else {
         GameProfileBanEntry entry = new GameProfileBanEntry(
            profile, new Date(), StringUtils.isBlank(source) ? null : source, expires, StringUtils.isBlank(reason) ? null : reason
         );
         this.list.add(entry);

         try {
            this.list.save();
         } catch (IOException var8) {
            org.bukkit.Bukkit.getLogger().log(Level.SEVERE, "Failed to save banned-players.json, {0}", var8.getMessage());
         }

         return new CraftProfileBanEntry(profile, entry, this.list);
      }
   }

   @Override
   public Set<org.bukkit.BanEntry> getBanEntries() {
      Builder<org.bukkit.BanEntry> builder = ImmutableSet.builder();

      for (GameProfileBanEntry entry : this.list.getValues()) {
         GameProfile profile = (GameProfile)entry.getKey();
         builder.add(new CraftProfileBanEntry(profile, entry, this.list));
      }

      return builder.build();
   }

   @Override
   public boolean isBanned(String target) {
      Validate.notNull(target, "Target cannot be null");
      GameProfile profile = MinecraftServer.getServer().getUserCache().getProfile(target);
      return profile == null ? false : this.list.isBanned(profile);
   }

   @Override
   public void pardon(String target) {
      Validate.notNull(target, "Target cannot be null");
      GameProfile profile = MinecraftServer.getServer().getUserCache().getProfile(target);
      this.list.remove(profile);
   }
}
