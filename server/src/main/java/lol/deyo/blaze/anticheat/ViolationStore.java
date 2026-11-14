package lol.deyo.blaze.anticheat;

import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.anticheat.AnticheatConfig;
import net.minecraft.server.v1_8_R3.MinecraftServer;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class ViolationStore {
   private final ConcurrentLinkedQueue<Row> queue = new ConcurrentLinkedQueue<>();
   private Connection connection;
   private boolean mysql;
   private long lastFlushNanos;
   private volatile boolean closed;

   public void init() {
      this.mysql = "MYSQL".equalsIgnoreCase((String)AnticheatConfig.storageType.getValue());
      try {
         if (this.mysql) {
            String host = (String)AnticheatConfig.mysqlHost.getValue();
            int port = (Integer)AnticheatConfig.mysqlPort.getValue();
            String db = (String)AnticheatConfig.mysqlDatabase.getValue();
            String user = (String)AnticheatConfig.mysqlUsername.getValue();
            String pass = (String)AnticheatConfig.mysqlPassword.getValue();
            boolean ssl = (Boolean)AnticheatConfig.mysqlUseSsl.getValue();
            this.connection = DriverManager.getConnection(
               "jdbc:mysql://" + host + ":" + port + "/" + db + "?useSSL=" + ssl + "&autoReconnect=true",
               user,
               pass
            );
         } else {
            File dir = new File(ConfigHelper.anticheatDirectory());
            if (!dir.exists()) {
               dir.mkdirs();
            }

            this.connection = DriverManager.getConnection("jdbc:sqlite:" + new File(dir, "violations.db").getAbsolutePath());
         }

         try (Statement statement = this.connection.createStatement()) {
            statement.executeUpdate(
               "CREATE TABLE IF NOT EXISTS ac_violations ("
                  + "id " + (this.mysql ? "BIGINT AUTO_INCREMENT PRIMARY KEY" : "INTEGER PRIMARY KEY AUTOINCREMENT") + ", "
                  + "uuid VARCHAR(36) NOT NULL, "
                  + "name VARCHAR(16) NOT NULL, "
                  + "check_name VARCHAR(32) NOT NULL, "
                  + "vl DOUBLE NOT NULL, "
                  + "detail VARCHAR(255), "
                  + "created_at " + (this.mysql ? "BIGINT NOT NULL" : "INTEGER NOT NULL") + ")"
            );
            statement.executeUpdate("CREATE INDEX IF NOT EXISTS idx_ac_uuid ON ac_violations (uuid)");
         }
      } catch (SQLException e) {
         MinecraftServer.LOGGER.error("Blaze AC storage init failed", e);
         this.connection = null;
      }
   }

   public void queueViolation(UUID uuid, String name, String checkName, double vl, String detail) {
      if (this.connection != null && !this.closed && this.queue.size() < 4096) {
         this.queue.add(new Row(uuid, name, checkName, vl, detail, System.currentTimeMillis()));
      }
   }

   public void loadViolations(UUID uuid, ViolationTracker tracker) {
      if (this.connection == null) {
         return;
      }

      try (PreparedStatement ps = this.connection.prepareStatement(
         "SELECT check_name, vl FROM ac_violations WHERE uuid = ? AND created_at > ?"
      )) {
         ps.setString(1, uuid.toString());
         ps.setLong(2, System.currentTimeMillis() - 86400000L);
         try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
               String checkName = rs.getString(1);
               double vl = rs.getDouble(2);
               for (int i = 0; i < tracker.size(); i++) {
                  if (tracker.nameAt(i).equals(checkName)) {
                     tracker.penalize(checkName, vl, System.nanoTime());
                     break;
                  }
               }
            }
         }
      } catch (SQLException e) {
         MinecraftServer.LOGGER.error("Blaze AC load violations failed", e);
      }
   }

   public void tick(AsyncExecutor executor) {
      long now = System.nanoTime();
      if (now - this.lastFlushNanos >= (long)(Integer)AnticheatConfig.storageFlushSeconds.getValue() * 1000000000L) {
         this.lastFlushNanos = now;
         if (!this.queue.isEmpty()) {
            executor.execute(this::flush);
         }
      }
   }

   public interface AsyncExecutor {
      void execute(Runnable runnable);
   }

   public void flush() {
      if (this.connection == null || this.closed) {
         return;
      }

      List<Row> batch = new ArrayList<>();
      Row row;
      while ((row = this.queue.poll()) != null) {
         batch.add(row);
      }

      if (batch.isEmpty()) {
         return;
      }

      try (PreparedStatement ps = this.connection.prepareStatement(
         "INSERT INTO ac_violations (uuid, name, check_name, vl, detail, created_at) VALUES (?, ?, ?, ?, ?, ?)"
      )) {
         for (Row r : batch) {
            ps.setString(1, r.uuid.toString());
            ps.setString(2, r.name);
            ps.setString(3, r.checkName);
            ps.setDouble(4, r.vl);
            ps.setString(5, r.detail);
            ps.setLong(6, r.createdAt);
            ps.addBatch();
         }

         ps.executeBatch();
      } catch (SQLException e) {
         MinecraftServer.LOGGER.error("Blaze AC flush failed", e);
      }
   }

   public void shutdown() {
      this.closed = true;
      this.flush();
      try {
         if (this.connection != null) {
            this.connection.close();
         }
      } catch (SQLException e) {
         MinecraftServer.LOGGER.error("Blaze AC shutdown failed", e);
      }
   }

   public int pendingCount() {
      return this.queue.size();
   }

   private static final class Row {
      final UUID uuid;
      final String name;
      final String checkName;
      final double vl;
      final String detail;
      final long createdAt;

      Row(UUID uuid, String name, String checkName, double vl, String detail, long createdAt) {
         this.uuid = uuid;
         this.name = name;
         this.checkName = checkName;
         this.vl = vl;
         this.detail = detail;
         this.createdAt = createdAt;
      }
   }
}
