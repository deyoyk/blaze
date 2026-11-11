package org.bukkit.entity;

import lol.deyo.blaze.api.knockback.IKnockback;
import java.net.InetSocketAddress;
import java.util.Set;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.map.MapView;
import org.bukkit.plugin.messaging.PluginMessageRecipient;
import org.bukkit.scoreboard.Scoreboard;
import org.github.paperspigot.Title;

public interface Player extends HumanEntity, Conversable, CommandSender, org.bukkit.OfflinePlayer, PluginMessageRecipient {
   String getDisplayName();

   void sendPacket(Object var1);

   void setKnockbackProfile(IKnockback var1);

   IKnockback getKnockbackProfile();

   void sendActionBar(String var1);

   int getPing();

   void setDisplayName(String var1);

   String getPlayerListName();

   void setPlayerListName(String var1);

   void setCompassTarget(org.bukkit.Location var1);

   org.bukkit.Location getCompassTarget();

   InetSocketAddress getAddress();

   @Override
   void sendRawMessage(String var1);

   void kickPlayer(String var1);

   void chat(String var1);

   boolean performCommand(String var1);

   boolean isSneaking();

   void setSneaking(boolean var1);

   boolean isSprinting();

   void setSprinting(boolean var1);

   void saveData();

   void loadData();

   void setSleepingIgnored(boolean var1);

   boolean isSleepingIgnored();

   @Deprecated
   void playNote(org.bukkit.Location var1, byte var2, byte var3);

   void playNote(org.bukkit.Location var1, org.bukkit.Instrument var2, org.bukkit.Note var3);

   void playSound(org.bukkit.Location var1, org.bukkit.Sound var2, float var3, float var4);

   void playSound(org.bukkit.Location var1, String var2, float var3, float var4);

   default void playSound(org.bukkit.Sound sound, float volume, float pitch) {
      this.playSound(this.getLocation(), sound, volume, pitch);
   }

   @Deprecated
   void playEffect(org.bukkit.Location var1, org.bukkit.Effect var2, int var3);

   <T> void playEffect(org.bukkit.Location var1, org.bukkit.Effect var2, T var3);

   @Deprecated
   void sendBlockChange(org.bukkit.Location var1, org.bukkit.Material var2, byte var3);

   @Deprecated
   boolean sendChunkChange(org.bukkit.Location var1, int var2, int var3, int var4, byte[] var5);

   @Deprecated
   void sendBlockChange(org.bukkit.Location var1, int var2, byte var3);

   void sendSignChange(org.bukkit.Location var1, String[] var2) throws IllegalArgumentException;

   void sendMap(MapView var1);

   @Override
   void sendMessage(BaseComponent var1);

   @Override
   void sendMessage(BaseComponent... var1);

   void setPlayerListHeaderFooter(BaseComponent[] var1, BaseComponent[] var2);

   void setPlayerListHeaderFooter(BaseComponent var1, BaseComponent var2);

   @Deprecated
   void setTitleTimes(int var1, int var2, int var3);

   @Deprecated
   void setSubtitle(BaseComponent[] var1);

   @Deprecated
   void setSubtitle(BaseComponent var1);

   @Deprecated
   void showTitle(BaseComponent[] var1);

   @Deprecated
   void showTitle(BaseComponent var1);

   @Deprecated
   void showTitle(BaseComponent[] var1, BaseComponent[] var2, int var3, int var4, int var5);

   @Deprecated
   void showTitle(BaseComponent var1, BaseComponent var2, int var3, int var4, int var5);

   void sendTitle(Title var1);

   void updateTitle(Title var1);

   void hideTitle();

   void updateInventory();

   void awardAchievement(org.bukkit.Achievement var1);

   void removeAchievement(org.bukkit.Achievement var1);

   boolean hasAchievement(org.bukkit.Achievement var1);

   void incrementStatistic(org.bukkit.Statistic var1) throws IllegalArgumentException;

   void decrementStatistic(org.bukkit.Statistic var1) throws IllegalArgumentException;

   void incrementStatistic(org.bukkit.Statistic var1, int var2) throws IllegalArgumentException;

   void decrementStatistic(org.bukkit.Statistic var1, int var2) throws IllegalArgumentException;

   void setStatistic(org.bukkit.Statistic var1, int var2) throws IllegalArgumentException;

   int getStatistic(org.bukkit.Statistic var1) throws IllegalArgumentException;

   void incrementStatistic(org.bukkit.Statistic var1, org.bukkit.Material var2) throws IllegalArgumentException;

   void decrementStatistic(org.bukkit.Statistic var1, org.bukkit.Material var2) throws IllegalArgumentException;

   int getStatistic(org.bukkit.Statistic var1, org.bukkit.Material var2) throws IllegalArgumentException;

   void incrementStatistic(org.bukkit.Statistic var1, org.bukkit.Material var2, int var3) throws IllegalArgumentException;

   void decrementStatistic(org.bukkit.Statistic var1, org.bukkit.Material var2, int var3) throws IllegalArgumentException;

   void setStatistic(org.bukkit.Statistic var1, org.bukkit.Material var2, int var3) throws IllegalArgumentException;

   void incrementStatistic(org.bukkit.Statistic var1, EntityType var2) throws IllegalArgumentException;

   void decrementStatistic(org.bukkit.Statistic var1, EntityType var2) throws IllegalArgumentException;

   int getStatistic(org.bukkit.Statistic var1, EntityType var2) throws IllegalArgumentException;

   void incrementStatistic(org.bukkit.Statistic var1, EntityType var2, int var3) throws IllegalArgumentException;

   void decrementStatistic(org.bukkit.Statistic var1, EntityType var2, int var3);

   void setStatistic(org.bukkit.Statistic var1, EntityType var2, int var3);

   void setPlayerTime(long var1, boolean var3);

   long getPlayerTime();

   long getPlayerTimeOffset();

   boolean isPlayerTimeRelative();

   void resetPlayerTime();

   void setPlayerWeather(org.bukkit.WeatherType var1);

   org.bukkit.WeatherType getPlayerWeather();

   void resetPlayerWeather();

   void giveExp(int var1);

   void giveExpLevels(int var1);

   float getExp();

   void setExp(float var1);

   int getLevel();

   void setLevel(int var1);

   int getTotalExperience();

   void setTotalExperience(int var1);

   float getExhaustion();

   void setExhaustion(float var1);

   float getSaturation();

   void setSaturation(float var1);

   int getFoodLevel();

   void setFoodLevel(int var1);

   @Override
   org.bukkit.Location getBedSpawnLocation();

   void setBedSpawnLocation(org.bukkit.Location var1);

   void setBedSpawnLocation(org.bukkit.Location var1, boolean var2);

   boolean getAllowFlight();

   void setAllowFlight(boolean var1);

   void hidePlayer(Player var1);

   void showPlayer(Player var1);

   boolean canSee(Player var1);

   @Deprecated
   @Override
   boolean isOnGround();

   boolean isFlying();

   void setFlying(boolean var1);

   void setFlySpeed(float var1) throws IllegalArgumentException;

   void setWalkSpeed(float var1) throws IllegalArgumentException;

   float getFlySpeed();

   float getWalkSpeed();

   @Deprecated
   void setTexturePack(String var1);

   @Deprecated
   void setResourcePack(String var1);

   Scoreboard getScoreboard();

   void setScoreboard(Scoreboard var1) throws IllegalArgumentException, IllegalStateException;

   boolean isHealthScaled();

   void setHealthScaled(boolean var1);

   void setHealthScale(double var1) throws IllegalArgumentException;

   double getHealthScale();

   Entity getSpectatorTarget();

   void setSpectatorTarget(Entity var1);

   @Deprecated
   void sendTitle(String var1, String var2);

   void resetTitle();

   Player.Spigot spigot();

   public static class Spigot extends Entity.Spigot {
      public InetSocketAddress getRawAddress() {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void playEffect(
         org.bukkit.Location location,
         org.bukkit.Effect effect,
         int id,
         int data,
         float offsetX,
         float offsetY,
         float offsetZ,
         float speed,
         int particleCount,
         int radius
      ) {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public boolean getCollidesWithEntities() {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void setCollidesWithEntities(boolean collides) {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void respawn() {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public String getLocale() {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public Set<Player> getHiddenPlayers() {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void sendMessage(BaseComponent component) {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void sendMessage(BaseComponent... components) {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public boolean getAffectsSpawning() {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void setAffectsSpawning(boolean affects) {
         throw new UnsupportedOperationException("Not supported yet");
      }

      public int getViewDistance() {
         throw new UnsupportedOperationException("Not supported yet");
      }

      public void setViewDistance(int viewDistance) {
         throw new UnsupportedOperationException("Not supported yet");
      }

      public int getPing() {
         throw new UnsupportedOperationException("Not supported yet.");
      }
   }
}
