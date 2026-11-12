package net.minecraft.server.v1_8_R3;

import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.properties.Property;
import lol.deyo.blaze.Blaze;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;

public class TileEntitySkull extends TileEntity {
   private int a;
   private int rotation;
   private GameProfile g = null;
   public static final LoadingCache<String, GameProfile> skinCache = CacheBuilder.newBuilder()
      .maximumSize(5000L)
      .expireAfterAccess(60L, TimeUnit.MINUTES)
      .build(new CacheLoader<String, GameProfile>() {
         @Override
         public GameProfile load(String key) {
            final GameProfile[] profiles = new GameProfile[1];
            ProfileLookupCallback gameProfileLookup = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile gp) {
                  profiles[0] = gp;
               }

               public void onProfileLookupFailed(GameProfile gp, Exception excptn) {
                  profiles[0] = gp;
               }
            };
            MinecraftServer.getServer().getGameProfileRepository().findProfilesByNames(new String[]{key}, Agent.MINECRAFT, gameProfileLookup);
            GameProfile profile = profiles[0];
            if (profile == null) {
               UUID uuid = EntityHuman.a(new GameProfile(null, key));
               profile = new GameProfile(uuid, key);
               gameProfileLookup.onProfileLookupSucceeded(profile);
            } else {
               Property property = (Property)Iterables.getFirst(profile.getProperties().get("textures"), null);
               if (property == null) {
                  profile = MinecraftServer.getServer().aD().fillProfileProperties(profile, true);
               }
            }

            return profile;
         }
      });

   @Override
   public void b(NBTTagCompound nbttagcompound) {
      super.b(nbttagcompound);
      nbttagcompound.setByte("SkullType", (byte)(this.a & 0xFF));
      nbttagcompound.setByte("Rot", (byte)(this.rotation & 0xFF));
      if (this.g != null) {
         NBTTagCompound nbttagcompound1 = new NBTTagCompound();
         GameProfileSerializer.serialize(nbttagcompound1, this.g);
         nbttagcompound.set("Owner", nbttagcompound1);
      }
   }

   @Override
   public void a(NBTTagCompound nbttagcompound) {
      super.a(nbttagcompound);
      this.a = nbttagcompound.getByte("SkullType");
      this.rotation = nbttagcompound.getByte("Rot");
      if (this.a == 3) {
         if (nbttagcompound.hasKeyOfType("Owner", 10)) {
            this.g = GameProfileSerializer.deserialize(nbttagcompound.getCompound("Owner"));
         } else if (nbttagcompound.hasKeyOfType("ExtraType", 8)) {
            String s = nbttagcompound.getString("ExtraType");
            if (!StringUtils.isEmpty(s)) {
               this.g = new GameProfile(null, s);
               this.e();
            }
         }
      }
   }

   public GameProfile getGameProfile() {
      return this.g;
   }

   @Override
   public Packet<?> getUpdatePacket() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      this.b(nbttagcompound);
      return new PacketPlayOutTileEntityData(this.position, 4, nbttagcompound);
   }

   public void setSkullType(int i) {
      this.a = i;
      this.g = null;
   }

   public void setGameProfile(GameProfile gameprofile) {
      this.a = 3;
      this.g = gameprofile;
      this.e();
   }

   private void e() {
      GameProfile profile = this.g;
      this.setSkullType(0);
      b(profile, input -> {
         this.setSkullType(3);
         this.g = input;
         this.update();
         if (this.world != null) {
            this.world.notify(this.position);
         }

         return false;
      });
   }

   public static void b(GameProfile gameprofile, Predicate<GameProfile> callback) {
      if (gameprofile != null && !StringUtils.isEmpty(gameprofile.getName())) {
         if (gameprofile.isComplete() && gameprofile.getProperties().containsKey("textures")) {
            callback.apply(gameprofile);
         } else if (MinecraftServer.getServer() == null) {
            callback.apply(gameprofile);
         } else {
            GameProfile profile = (GameProfile)skinCache.getIfPresent(gameprofile.getName());
            if (profile != null && Iterables.getFirst(profile.getProperties().get("textures"), null) != null) {
               callback.apply(profile);
            } else {
               Blaze.getInstance().getThreadingManager().getAsyncExecutor().queueHeadLoading(() -> {
                  GameProfile profile1 = (GameProfile)skinCache.getUnchecked(gameprofile.getName().toLowerCase(Locale.ROOT));
                  MinecraftServer.getServer().processQueue.add(() -> {
                     if (profile1 == null) {
                        callback.apply(gameprofile);
                     } else {
                        callback.apply(profile1);
                     }
                  });
               });
            }
         }
      } else {
         callback.apply(gameprofile);
      }
   }

   public int getSkullType() {
      return this.a;
   }

   public void setRotation(int i) {
      this.rotation = i;
   }

   public int getRotation() {
      return this.rotation;
   }
}
