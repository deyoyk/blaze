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
   private volatile AsyncExecutor executor = Runnable::run;

   public void init() {
      this.mysql = "MYSQL".equalsIgnoreCase((String)AnticheatConfig.storageType.getValue());
      try {
         if (this.mysql) {
            try {
               Class.forName("com.mysql.cj.jdbc.Driver");
               Class.forName("org.gjt.mm.mysql.Driver");
            } catch (ClassNotFoundException cnfe) {
               throw new SQLException("MySQL driver not present", cnfe);
            }
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
            try {
               Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException cnfe) {
               throw new SQLException("SQLite driver not present", cnfe);
            }

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
            statement.executeUpdate(
               "CREATE TABLE IF NOT EXISTS ac_punishments ("
                  + "punishment_id BIGINT PRIMARY KEY, "
                  + "uuid VARCHAR(36) NOT NULL, "
                  + "name VARCHAR(16) NOT NULL, "
                  + "check_name VARCHAR(32) NOT NULL, "
                  + "action VARCHAR(8) NOT NULL, "
                  + "reason VARCHAR(255), "
                  + "created_at " + (this.mysql ? "BIGINT NOT NULL" : "INTEGER NOT NULL") + ", "
                  + "expires " + (this.mysql ? "BIGINT NOT NULL" : "INTEGER NOT NULL") + ", "
                  + "active " + (this.mysql ? "TINYINT NOT NULL DEFAULT 1" : "INTEGER NOT NULL DEFAULT 1") + ", "
                  + "lifted_by VARCHAR(16))"
            );
            statement.executeUpdate("CREATE INDEX IF NOT EXISTS idx_acp_uuid ON ac_punishments (uuid)");
            statement.executeUpdate("CREATE INDEX IF NOT EXISTS idx_acp_name ON ac_punishments (name)");
            statement.executeUpdate("CREATE INDEX IF NOT EXISTS idx_acp_created ON ac_punishments (created_at)");
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
   }

   public void queuePunishment(PunishmentManager.Record record) {
      if (this.connection == null || this.closed) {
         return;
      }

      this.executor.execute(() -> {
         try (PreparedStatement ps = this.connection.prepareStatement(
            "INSERT INTO ac_punishments (punishment_id, uuid, name, check_name, action, reason, created_at, expires, active, lifted_by)"
               + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NULL)"
         )) {
            ps.setLong(1, record.id);
            ps.setString(2, record.uuid.toString());
            ps.setString(3, record.name);
            ps.setString(4, record.check);
            ps.setString(5, record.type.name());
            ps.setString(6, record.reason);
            ps.setLong(7, record.createdAt);
            ps.setLong(8, record.expires);
            ps.setInt(9, 1);
            ps.executeUpdate();
         } catch (SQLException e) {
            MinecraftServer.LOGGER.error("Blaze AC punishment insert failed", e);
         }
      });
   }

   public PunishmentManager.Record getPunishment(long id) {
      if (this.connection == null || this.closed) {
         return null;
      }

      try (PreparedStatement ps = this.connection.prepareStatement(
         "SELECT punishment_id, uuid, name, check_name, action, reason, created_at, expires, active"
            + " FROM ac_punishments WHERE punishment_id = ?"
      )) {
         ps.setLong(1, id);
         try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
               return this.readRecord(rs);
            }
         }
      } catch (SQLException e) {
         MinecraftServer.LOGGER.error("Blaze AC punishment lookup failed", e);
      }

      return null;
   }

   public List<PunishmentManager.Record> getPunishments(UUID uuid) {
      return this.queryRecords("uuid = ?", uuid.toString());
   }

   public List<PunishmentManager.Record> getPunishmentsByName(String name) {
      return this.queryRecords("name = ?", name);
   }

   public List<PunishmentManager.Record> getActiveBans() {
      return this.queryRecords("action = 'BAN' AND active = 1", null);
   }

   private List<PunishmentManager.Record> queryRecords(String where, String param) {
      List<PunishmentManager.Record> records = new ArrayList<>();
      if (this.connection == null || this.closed) {
         return records;
      }

      String sql = "SELECT punishment_id, uuid, name, check_name, action, reason, created_at, expires, active"
         + " FROM ac_punishments WHERE " + where + " ORDER BY created_at DESC LIMIT 100";
      try (PreparedStatement ps = this.connection.prepareStatement(sql)) {
         if (param != null) {
            ps.setString(1, param);
         }

         try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
               records.add(this.readRecord(rs));
            }
         }
      } catch (SQLException e) {
         MinecraftServer.LOGGER.error("Blaze AC punishment query failed", e);
      }

      return records;
   }

   private PunishmentManager.Record readRecord(ResultSet rs) throws SQLException {
      return new PunishmentManager.Record(
         rs.getLong(1),
         UUID.fromString(rs.getString(2)),
         rs.getString(3),
         rs.getString(4),
         PunishmentManager.PunishmentType.valueOf(rs.getString(5)),
         rs.getString(6),
         rs.getLong(7),
         rs.getLong(8),
         rs.getInt(9) != 0
      );
   }

   public void markPunishmentInactive(long id, String by) {
      if (this.connection == null || this.closed) {
         return;
      }

      this.executor.execute(() -> {
         try (PreparedStatement ps = this.connection.prepareStatement(
            "UPDATE ac_punishments SET active = 0, lifted_by = ? WHERE punishment_id = ?"
         )) {
            ps.setString(1, by);
            ps.setLong(2, id);
            ps.executeUpdate();
         } catch (SQLException e) {
            MinecraftServer.LOGGER.error("Blaze AC punishment update failed", e);
         }
      });
   }

   public int countPunishments(PunishmentManager.PunishmentType type, long sinceMillis) {
      if (this.connection == null || this.closed) {
         return 0;
      }

      try (PreparedStatement ps = this.connection.prepareStatement(
         "SELECT COUNT(*) FROM ac_punishments WHERE action = ? AND created_at >= ?"
      )) {
         ps.setString(1, type.name());
         ps.setLong(2, sinceMillis);
         try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
               return rs.getInt(1);
            }
         }
      } catch (SQLException e) {
         MinecraftServer.LOGGER.error("Blaze AC punishment count failed", e);
      }

      return 0;
   }

   public String topPunishedCheck(long sinceMillis) {
      if (this.connection == null || this.closed) {
         return null;
      }

      try (PreparedStatement ps = this.connection.prepareStatement(
         "SELECT check_name FROM ac_punishments WHERE created_at >= ? GROUP BY check_name ORDER BY COUNT(*) DESC LIMIT 1"
      )) {
         ps.setLong(1, sinceMillis);
         try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
               return rs.getString(1);
            }
         }
      } catch (SQLException e) {
         MinecraftServer.LOGGER.error("Blaze AC top check query failed", e);
      }

      return null;
   }

   public void tick(AsyncExecutor executor) {
      this.executor = executor;
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
