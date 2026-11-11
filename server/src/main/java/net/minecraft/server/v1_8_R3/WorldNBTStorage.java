package net.minecraft.server.v1_8_R3;

import com.eatthepath.uuid.FastUUID;
import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.async.tasks.PlayerDataSaveJob;
import lol.deyo.blaze.async.tasks.WorldDataSaveJob;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

public class WorldNBTStorage implements IDataManager, IPlayerFileData {
   private static final Logger a = LogManager.getLogger();
   private final File baseDir;
   private final File playerDir;
   private final File dataDir;
   private final long sessionId = MinecraftServer.az();
   private final String f;
   private UUID uuid = null;
   private final PlayerCache<File, NBTTagCompound> dataCache = new PlayerCache<>();

   public WorldNBTStorage(File file, String s, boolean flag) {
      this.baseDir = new File(file, s);
      this.baseDir.mkdirs();
      this.playerDir = new File(this.baseDir, "playerdata");
      this.dataDir = new File(this.baseDir, "data");
      this.dataDir.mkdirs();
      this.f = s;
      if (flag) {
         this.playerDir.mkdirs();
      }

      this.h();
   }

   private void h() {
      try {
         File file = new File(this.baseDir, "session.lock");
         try (DataOutputStream dataoutputstream = new DataOutputStream(Files.newOutputStream(file.toPath()))) {
            dataoutputstream.writeLong(this.sessionId);
         }
      } catch (IOException var13) {
         var13.printStackTrace();
         throw new RuntimeException(
            "Failed to check session lock for world located at "
               + this.baseDir
               + ", aborting. Stop the server and delete the session.lock in this world to prevent further issues."
         );
      }
   }
   @Override
   public File getDirectory() {
      return this.baseDir;
   }

   @Override
   public void checkSession() throws ExceptionWorldConflict {
      try {
         File file = new File(this.baseDir, "session.lock");
         try (DataInputStream datainputstream = new DataInputStream(Files.newInputStream(file.toPath()))) {
            if (datainputstream.readLong() != this.sessionId) {
               throw new ExceptionWorldConflict("The save for world located at " + this.baseDir + " is being accessed from another location, aborting");
            }
         }
      } catch (IOException var13) {
         throw new ExceptionWorldConflict(
            "Failed to check session lock for world located at "
               + this.baseDir
               + ", aborting. Stop the server and delete the session.lock in this world to prevent further issues."
         );
      }
   }
   @Override
   public IChunkLoader createChunkLoader(WorldProvider worldprovider) {
      throw new RuntimeException("Old Chunk Storage is no longer supported.");
   }

   @Override
   public WorldData getWorldData() {
      File file = new File(this.baseDir, "level.dat");
      if (file.exists()) {
         try {
            NBTTagCompound nbttagcompound = NBTCompressedStreamTools.a(Files.newInputStream(file.toPath()));
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Data");
            return new WorldData(nbttagcompound1);
         } catch (Exception var6) {
            var6.printStackTrace();
         }
      }

      file = new File(this.baseDir, "level.dat_old");
      if (file.exists()) {
         try {
            NBTTagCompound nbttagcompound = NBTCompressedStreamTools.a(Files.newInputStream(file.toPath()));
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Data");
            return new WorldData(nbttagcompound1);
         } catch (Exception var5) {
            var5.printStackTrace();
         }
      }

      return null;
   }

   @Override
   public void saveWorldData(WorldData worlddata) {
      this.saveWorldData(worlddata, new NBTTagCompound());
   }

   @Override
   public void saveWorldData(WorldData worlddata, NBTTagCompound nbttagcompound) {
      Blaze.getInstance().getThreadingManager().getAsyncExecutor().queueFileTask(new WorldDataSaveJob(worlddata, nbttagcompound, this.baseDir));
   }

   @Override
   public void save(EntityHuman entityhuman) {
      try {
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         entityhuman.e(nbttagcompound);
         File file1 = new File(this.playerDir, FastUUID.toString(entityhuman.getUniqueID()) + ".dat");
         synchronized (this.dataCache) {
            this.dataCache.put(file1, nbttagcompound);
         }

         Blaze.getInstance().getThreadingManager().getAsyncExecutor().queueFileTask(new PlayerDataSaveJob(file1, nbttagcompound));
      } catch (Exception var6) {
         a.warn("Failed to save player data for {}", new Object[]{entityhuman.getName()});
      }
   }

   @Override
   public NBTTagCompound load(EntityHuman entityhuman) {
      NBTTagCompound nbttagcompound = null;

      try {
         File file = new File(this.playerDir, FastUUID.toString(entityhuman.getUniqueID()) + ".dat");
         boolean usingWrongFile = false;
         boolean normalFile = file.isFile();
         NBTTagCompound playerdata;
         synchronized (this.dataCache) {
            playerdata = this.dataCache.get(file);
         }

         if (Bukkit.getOnlineMode() && !normalFile && playerdata == null) {
            file = new File(
               this.playerDir, FastUUID.toString(UUID.nameUUIDFromBytes(("OfflinePlayer:" + entityhuman.getName()).getBytes(StandardCharsets.UTF_8))) + ".dat"
            );
            synchronized (this.dataCache) {
               playerdata = this.dataCache.get(file);
            }

            if (file.exists()) {
               usingWrongFile = true;
               Bukkit.getServer()
                  .getLogger()
                  .warning("Using offline mode UUID file for player " + entityhuman.getName() + " as it is the only copy we can find.");
            }
         }

         if (playerdata != null) {
            nbttagcompound = playerdata;
         } else if (normalFile) {
            nbttagcompound = NBTCompressedStreamTools.a(Files.newInputStream(file.toPath()));
         }

         if (usingWrongFile) {
            file.renameTo(new File(file.getPath() + ".offline-read"));
         }
      } catch (Exception var10) {
         a.warn("Failed to load player data for " + entityhuman.getName());
      }

      if (nbttagcompound != null) {
         if (entityhuman instanceof EntityPlayer) {
            CraftPlayer player = (CraftPlayer)entityhuman.getBukkitEntity();
            long modified = new File(this.playerDir, FastUUID.toString(entityhuman.getUniqueID()) + ".dat").lastModified();
            if (modified < player.getFirstPlayed()) {
               player.setFirstPlayed(modified);
            }
         }

         entityhuman.f(nbttagcompound);
      }

      return nbttagcompound;
   }

   public NBTTagCompound getPlayerData(String s) {
      try {
         File file1 = new File(this.playerDir, s + ".dat");
         if (file1.exists()) {
            return NBTCompressedStreamTools.a(Files.newInputStream(file1.toPath()));
         }
      } catch (Exception var3) {
         a.warn("Failed to load player data for " + s);
      }

      return null;
   }

   @Override
   public IPlayerFileData getPlayerFileData() {
      return this;
   }

   @Override
   public String[] getSeenPlayers() {
      String[] astring = this.playerDir.list();
      if (astring == null) {
         astring = new String[0];
      }

      for (int i = 0; i < astring.length; i++) {
         if (astring[i].endsWith(".dat")) {
            astring[i] = astring[i].substring(0, astring[i].length() - 4);
         }
      }

      return astring;
   }

   @Override
   public void a() {
   }

   @Override
   public File getDataFile(String s) {
      return new File(this.dataDir, s + ".dat");
   }

   @Override
   public String g() {
      return this.f;
   }

   @Override
   public UUID getUUID() {
      if (this.uuid != null) {
         return this.uuid;
      } else {
         File file1 = new File(this.baseDir, "uid.dat");
         if (file1.exists()) {
            try (DataInputStream dis = new DataInputStream(Files.newInputStream(file1.toPath()))) {
               this.uuid = new UUID(dis.readLong(), dis.readLong());
               return this.uuid;
            } catch (IOException var34) {
               a.warn("Failed to read " + file1 + ", generating new random UUID", var34);
            }
         }

         this.uuid = UUID.randomUUID();

         try (DataOutputStream dos = new DataOutputStream(Files.newOutputStream(file1.toPath()))) {
            dos.writeLong(this.uuid.getMostSignificantBits());
            dos.writeLong(this.uuid.getLeastSignificantBits());
         } catch (IOException var31) {
            a.warn("Failed to write " + file1, var31);
         }

         return this.uuid;
      }
   }
   public File getPlayerDir() {
      return this.playerDir;
   }
}
