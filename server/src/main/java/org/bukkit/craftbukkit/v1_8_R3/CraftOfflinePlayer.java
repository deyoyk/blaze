package org.bukkit.craftbukkit.v1_8_R3;

import com.eatthepath.uuid.FastUUID;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.WorldNBTStorage;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

@SerializableAs("Player")
public class CraftOfflinePlayer implements org.bukkit.OfflinePlayer, ConfigurationSerializable {
   private final GameProfile profile;
   private final CraftServer server;
   private final WorldNBTStorage storage;

   protected CraftOfflinePlayer(CraftServer server, GameProfile profile) {
      this.server = server;
      this.profile = profile;
      this.storage = (WorldNBTStorage)((WorldServer)server.console.worlds.get(0)).getDataManager();
   }

   public GameProfile getProfile() {
      return this.profile;
   }

   @Override
   public boolean isOnline() {
      return this.getPlayer() != null;
   }

   @Override
   public String getName() {
      Player player = this.getPlayer();
      if (player != null) {
         return player.getName();
      } else if (this.profile.getName() != null) {
         return this.profile.getName();
      } else {
         NBTTagCompound data = this.getBukkitData();
         return data != null && data.hasKey("lastKnownName") ? data.getString("lastKnownName") : null;
      }
   }

   @Override
   public UUID getUniqueId() {
      return this.profile.getId();
   }

   public org.bukkit.Server getServer() {
      return this.server;
   }

   @Override
   public boolean isOp() {
      return this.server.getHandle().isOp(this.profile);
   }

   @Override
   public void setOp(boolean value) {
      if (value != this.isOp()) {
         if (value) {
            this.server.getHandle().addOp(this.profile);
         } else {
            this.server.getHandle().removeOp(this.profile);
         }
      }
   }

   @Override
   public boolean isBanned() {
      return this.getName() == null ? false : this.server.getBanList(org.bukkit.BanList.Type.NAME).isBanned(this.getName());
   }

   @Override
   public void setBanned(boolean value) {
      if (this.getName() != null) {
         if (value) {
            this.server.getBanList(org.bukkit.BanList.Type.NAME).addBan(this.getName(), null, null, null);
         } else {
            this.server.getBanList(org.bukkit.BanList.Type.NAME).pardon(this.getName());
         }
      }
   }

   @Override
   public boolean isWhitelisted() {
      return this.server.getHandle().getWhitelist().isWhitelisted(this.profile);
   }

   @Override
   public void setWhitelisted(boolean value) {
      if (value) {
         this.server.getHandle().addWhitelist(this.profile);
      } else {
         this.server.getHandle().removeWhitelist(this.profile);
      }
   }

   @Override
   public Map<String, Object> serialize() {
      Map<String, Object> result = new LinkedHashMap<>();
      result.put("UUID", this.profile.getId().toString());
      return result;
   }

   public static org.bukkit.OfflinePlayer deserialize(Map<String, Object> args) {
      return args.get("name") != null
         ? org.bukkit.Bukkit.getServer().getOfflinePlayer((String)args.get("name"))
         : org.bukkit.Bukkit.getServer().getOfflinePlayer(FastUUID.parseUUID((String)args.get("UUID")));
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName() + "[UUID=" + this.profile.getId() + "]";
   }

   @Override
   public Player getPlayer() {
      return this.server.getPlayer(this.getUniqueId());
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof org.bukkit.OfflinePlayer)) {
         return false;
      } else {
         org.bukkit.OfflinePlayer other = (org.bukkit.OfflinePlayer)obj;
         return this.getUniqueId() != null && other.getUniqueId() != null ? this.getUniqueId().equals(other.getUniqueId()) : false;
      }
   }

   @Override
   public int hashCode() {
      int hash = 5;
      return 97 * hash + (this.getUniqueId() != null ? this.getUniqueId().hashCode() : 0);
   }

   private NBTTagCompound getData() {
      return this.storage.getPlayerData(this.getUniqueId().toString());
   }

   private NBTTagCompound getBukkitData() {
      NBTTagCompound result = this.getData();
      if (result != null) {
         if (!result.hasKey("bukkit")) {
            result.set("bukkit", new NBTTagCompound());
         }

         result = result.getCompound("bukkit");
      }

      return result;
   }

   private File getDataFile() {
      return new File(this.storage.getPlayerDir(), this.getUniqueId() + ".dat");
   }

   @Override
   public long getFirstPlayed() {
      Player player = this.getPlayer();
      if (player != null) {
         return player.getFirstPlayed();
      } else {
         NBTTagCompound data = this.getBukkitData();
         if (data != null) {
            if (data.hasKey("firstPlayed")) {
               return data.getLong("firstPlayed");
            } else {
               File file = this.getDataFile();
               return file.lastModified();
            }
         } else {
            return 0L;
         }
      }
   }

   @Override
   public long getLastPlayed() {
      Player player = this.getPlayer();
      if (player != null) {
         return player.getLastPlayed();
      } else {
         NBTTagCompound data = this.getBukkitData();
         if (data != null) {
            if (data.hasKey("lastPlayed")) {
               return data.getLong("lastPlayed");
            } else {
               File file = this.getDataFile();
               return file.lastModified();
            }
         } else {
            return 0L;
         }
      }
   }

   @Override
   public boolean hasPlayedBefore() {
      return this.getData() != null;
   }

   @Override
   public org.bukkit.Location getBedSpawnLocation() {
      NBTTagCompound data = this.getData();
      if (data == null) {
         return null;
      } else if (data.hasKey("SpawnX") && data.hasKey("SpawnY") && data.hasKey("SpawnZ")) {
         String spawnWorld = data.getString("SpawnWorld");
         if (spawnWorld.equals("")) {
            spawnWorld = this.server.getWorlds().get(0).getName();
         }

         return new org.bukkit.Location(this.server.getWorld(spawnWorld), data.getInt("SpawnX"), data.getInt("SpawnY"), data.getInt("SpawnZ"));
      } else {
         return null;
      }
   }

   public void setMetadata(String metadataKey, MetadataValue metadataValue) {
      this.server.getPlayerMetadata().setMetadata(this, metadataKey, metadataValue);
   }

   public List<MetadataValue> getMetadata(String metadataKey) {
      return this.server.getPlayerMetadata().getMetadata(this, metadataKey);
   }

   public boolean hasMetadata(String metadataKey) {
      return this.server.getPlayerMetadata().hasMetadata(this, metadataKey);
   }

   public void removeMetadata(String metadataKey, Plugin plugin) {
      this.server.getPlayerMetadata().removeMetadata(this, metadataKey, plugin);
   }
}
