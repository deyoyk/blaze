package lol.deyo.blaze.anticheat;

import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.anticheat.AnticheatConfig;
import lol.deyo.blaze.configs.AnticheatConfigHolder;
import lol.deyo.blaze.utils.CC;
import lol.deyo.blaze.anticheat.checks.SpeedCheck;
import lol.deyo.blaze.anticheat.checks.FlightCheck;
import lol.deyo.blaze.anticheat.checks.StepCheck;
import lol.deyo.blaze.anticheat.checks.GroundSpoofCheck;
import lol.deyo.blaze.anticheat.checks.NoFallCheck;
import lol.deyo.blaze.anticheat.checks.TimerCheck;
import lol.deyo.blaze.anticheat.checks.KillauraCheck;
import lol.deyo.blaze.anticheat.checks.AutoClickerCheck;
import lol.deyo.blaze.anticheat.checks.VelocityCheck;
import lol.deyo.blaze.anticheat.checks.HitboxCheck;
import lol.deyo.blaze.anticheat.checks.BadPacketsCheck;
import lol.deyo.blaze.anticheat.checks.PacketOrderCheck;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ACManager {
   public static final ACManager INSTANCE = new ACManager();
   private static final String PREFIX = "&6[Blaze AC]&e ";

   private final Map<UUID, ACPlayer> players = new ConcurrentHashMap<>();
   private final ViolationStore store = new ViolationStore();
   private volatile boolean alertsToggled;

   public void checksOnFlying(ACPlayer data, long nowNanos) {
      data.checks.onFlying(data, nowNanos);
   }

   public void checksOnAttack(ACPlayer data, long nowNanos) {
      data.checks.onAttack(data, nowNanos);
   }

   public void checksOnSwing(ACPlayer data, long nowNanos) {
      data.checks.onSwing(data, nowNanos);
   }

   public void checksOnDig(ACPlayer data, long nowNanos) {
      data.checks.onDig(data, nowNanos);
   }

   private ACManager() {
   }

   public static void init(Blaze blaze) {
      AnticheatConfig config = new AnticheatConfig(blaze);
      config.load();
      AnticheatConfigHolder.ENABLED = (Boolean)AnticheatConfig.enabled.getValue();
      INSTANCE.store.init();
   }

   public static void shutdown() {
      INSTANCE.store.shutdown();
   }

   public ACPlayer get(EntityPlayer player) {
      return this.players.computeIfAbsent(player.getUniqueID(), id -> new ACPlayer(player));
   }

   public static void registerChecks(ACPlayer data) {
      data.checks.add(new SpeedCheck(INSTANCE));
      data.checks.add(new FlightCheck(INSTANCE));
      data.checks.add(new StepCheck(INSTANCE));
      data.checks.add(new GroundSpoofCheck(INSTANCE));
      data.checks.add(new NoFallCheck(INSTANCE));
      data.checks.add(new TimerCheck(INSTANCE));
      data.checks.add(new KillauraCheck(INSTANCE));
      data.checks.add(new AutoClickerCheck(INSTANCE));
      data.checks.add(new VelocityCheck(INSTANCE));
      data.checks.add(new HitboxCheck(INSTANCE));
      data.checks.add(new BadPacketsCheck(INSTANCE));
      data.checks.add(new PacketOrderCheck(INSTANCE));
      for (Check check : data.checks.all()) {
         data.violations.register(check, 0.4);
      }
   }

   public ACPlayer get(UUID uuid) {
      return this.players.get(uuid);
   }

   public void handleJoin(EntityPlayer player) {
      ACPlayer data = this.get(player);
      this.store.loadViolations(player.getUniqueID(), data.violations);
      if ((Boolean)AnticheatConfig.alertJoinMessage.getValue() && player.getBukkitEntity().hasPermission((String)AnticheatConfig.alertPermission.getValue())) {
         String text = CC.t(PREFIX + "You have alert permission. Use /blazeac alerts.");
         String json = "{\"text\":\"" + text.replace("\\", "\\\\").replace("\"", "\\\"") + "\"}";
         player.playerConnection.sendPacket(new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(json)));
      }
   }

   public void handleQuit(EntityPlayer player) {
      ACPlayer data = this.players.remove(player.getUniqueID());
   }

   public boolean flag(Check check, ACPlayer data, double weight, String detail) {
      if (!(Boolean)AnticheatConfig.enabled.getValue()) {
         return false;
      }

      long now = System.nanoTime();
      double level = data.violations.penalize(check.getName(), weight, now);
      EntityPlayer player = data.player;

      if ((Boolean)AnticheatConfig.debug.getValue()) {
         MinecraftServer.LOGGER.info(
            "[AC] " + player.getName() + " failed " + check.getName() + " (vl=" + String.format("%.1f", level) + ") " + detail
         );
      }

      if (now - data.getLastAlertNanos() >= (long)(Integer)AnticheatConfig.alertCooldownMs.getValue() * 1000000L) {
         data.setLastAlertNanos(now);
         String alert = CC.t(PREFIX + "&f" + player.getName() + " &7failed &c" + check.getName()
            + "&7 vl=" + String.format("%.1f", level) + " &8" + detail);
         if ((Boolean)AnticheatConfig.alertOnConsole.getValue()) {
            MinecraftServer.LOGGER.info(alert);
         }

         if (this.alertsToggled) {
            for (Player online : Bukkit.getOnlinePlayers()) {
               if (online.hasPermission((String)AnticheatConfig.alertPermission.getValue())) {
                  online.sendMessage(alert);
               }
            }
         }
      }

      int maxVl = (Integer)AnticheatConfig.maxViolationLevel.getValue();
      if (level >= maxVl) {
         data.violations.reset();
         String command = ((String)AnticheatConfig.punishmentCommand.getValue()).replace("%player%", player.getName());
         MinecraftServer.getServer().postToMainThread(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
         this.store.queueViolation(player.getUniqueID(), player.getName(), check.getName(), level, detail);
      } else if ((Boolean)AnticheatConfig.storageLogViolations.getValue()) {
         this.store.queueViolation(player.getUniqueID(), player.getName(), check.getName(), level, detail);
      }

      return level >= maxVl;
   }

   public void tick() {
      for (ACPlayer data : this.players.values()) {
         data.tick();
         data.checks.tick(data);
      }

      this.store.tick(Blaze.getInstance().getThreadingManager().getAsyncExecutor()::execute);
   }

   public void toggleAlerts() {
      this.alertsToggled = !this.alertsToggled;
   }

   public boolean alertsToggled() {
      return this.alertsToggled;
   }

   public ViolationStore getStore() {
      return this.store;
   }

   public int getOnlineTracked() {
      return this.players.size();
   }
}
