package lol.deyo.blaze;

import lol.deyo.blaze.api.pearls.PearlHandler;
import lol.deyo.blaze.api.protocol.MovementHandler;
import lol.deyo.blaze.api.protocol.PacketHandler;
import lol.deyo.blaze.async.ThreadingManager;
import lol.deyo.blaze.command.BlazeCommand;
import lol.deyo.blaze.command.ClearLagCommand;
import lol.deyo.blaze.command.KnockbackCommand;
import lol.deyo.blaze.command.PingCommand;
import lol.deyo.blaze.command.SetMaxPlayersCommand;
import lol.deyo.blaze.command.TPSCommand;
import lol.deyo.blaze.command.UnloadChunksCommand;
import lol.deyo.blaze.command.VersionCommand;
import lol.deyo.blaze.configs.BlazeConfig;
import lol.deyo.blaze.configs.EnchantConfig;
import lol.deyo.blaze.configs.LanguageConfig;
import lol.deyo.blaze.configs.PearlConfig;
import lol.deyo.blaze.configs.extra.Config;
import lol.deyo.blaze.configs.extra.ConfigEdit;
import lol.deyo.blaze.framework.commands.Command;
import lol.deyo.blaze.framework.menu.MenuManager;
import lol.deyo.blaze.generator.GeneratorManager;
import lol.deyo.blaze.knockback.KnockbackManager;
import lol.deyo.blaze.redstone.EigencraftRedstoneWire;
import lol.deyo.blaze.redstone.PandaRedstoneWire;
import lol.deyo.blaze.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.BlockRedstoneWire;
import net.minecraft.server.v1_8_R3.GameRules;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.command.defaults.BukkitCommand;

public class Blaze implements IBlaze {
   private static Blaze instance;
   private final Map<UUID, ConfigEdit> configEdits;
   private final List<Config> configs;
   private final List<Command> commands;
   private final List<PacketHandler> packetHandlers;
   private final List<MovementHandler> movementHandlers;
   private final List<PearlHandler> pearlHandlers;
   private final BlazeConfig blazeConfig;
   private final LanguageConfig languageConfig;
   private final PearlConfig pearlConfig;
   private final EnchantConfig enchantConfig;
   private final ThreadingManager threadingManager;
   private final KnockbackManager knockbackManager;
   private final GeneratorManager generatorManager;
   private final MenuManager menuManager;

   public Blaze() {
      instance = this;
      this.configEdits = new HashMap<>();
      this.configs = new ArrayList<>();
      this.commands = new ArrayList<>();
      this.packetHandlers = new ArrayList<>();
      this.movementHandlers = new ArrayList<>();
      this.pearlHandlers = new ArrayList<>();
      this.blazeConfig = new BlazeConfig(this);
      this.languageConfig = new LanguageConfig(this);
      this.pearlConfig = new PearlConfig(this);
      this.enchantConfig = new EnchantConfig(this);
      this.configs.forEach(Config::load);
      this.threadingManager = new ThreadingManager();
      this.knockbackManager = new KnockbackManager();
      this.generatorManager = new GeneratorManager();
      this.menuManager = new MenuManager();
   }

   public void shutdown() {
      this.threadingManager.shutdown();
      this.knockbackManager.save(false);
      this.generatorManager.save(false);
   }

   public void registerCommands(boolean reload) {
      if (BlazeConfig.enableSpigotConfigCommand.getValue()) {
         this.commands.add(new BlazeCommand());
      }

      if (BlazeConfig.enablePingCommand.getValue()) {
         this.commands.add(new PingCommand());
      }

      if (BlazeConfig.enableTPSCommand.getValue()) {
         this.commands.add(new TPSCommand());
      }

      if (BlazeConfig.enableClearLagCommand.getValue()) {
         this.commands.add(new ClearLagCommand());
      }

      if (BlazeConfig.enableSetMaxPlayersCommand.getValue()) {
         this.commands.add(new SetMaxPlayersCommand());
      }

      if (BlazeConfig.enableChunkUnloadCommand.getValue()) {
         this.commands.add(new UnloadChunksCommand());
      }

      this.commands.add(new KnockbackCommand());
      this.commands.add(new VersionCommand());

      for (Command command : this.commands) {
         BukkitCommand bukkitCommand = command.asBukkitCommand();
         String name = bukkitCommand.getName();
         if (name.equals("tps")) {
            Utils.register("spigot", bukkitCommand, reload);
         }

         if (name.equals("version")) {
            Utils.register("minecraft", bukkitCommand, reload);
         }

         Utils.register(BlazeConfig.serverBranding.getValue().toLowerCase(), bukkitCommand, reload);
      }
   }

   public void reload() {
      for (Config config : this.configs) {
         config.reload();
      }

      for (Command command : this.commands) {
         command.unregister();
      }

      for (WorldServer world : MinecraftServer.getServer().worlds) {
         GameRules gameRules = world.worldData.x();
         gameRules.set("randomTickSpeed", BlazeConfig.randomTickSpeed.getValue().toString());
         gameRules.set("doMobSpawning", BlazeConfig.naturalMobSpawn.getValue().toString());
      }

      this.commands.clear();
      this.registerCommands(true);
   }

   public BlockRedstoneWire getRedstoneOptimization() {
      String var1;
      switch ((var1 = BlazeConfig.redstoneOptimization.getValue().toUpperCase()).hashCode()) {
         case -1178535564:
            if (var1.equals("EIGENCRAFT")) {
               return new EigencraftRedstoneWire();
            }
            break;
         case 1266371903:
            if (var1.equals("PANDAWIRE")) {
               return new PandaRedstoneWire();
            }
      }

      return new BlockRedstoneWire();
   }

   public static Blaze getInstance() {
      return instance;
   }

   public Map<UUID, ConfigEdit> getConfigEdits() {
      return this.configEdits;
   }

   public List<Config> getConfigs() {
      return this.configs;
   }

   public List<Command> getCommands() {
      return this.commands;
   }

   public ThreadingManager getThreadingManager() {
      return this.threadingManager;
   }

   public GeneratorManager getGeneratorManager() {
      return this.generatorManager;
   }

   public MenuManager getMenuManager() {
      return this.menuManager;
   }

   public BlazeConfig getBlazeConfig() {
      return this.blazeConfig;
   }

   public LanguageConfig getLanguageConfig() {
      return this.languageConfig;
   }

   public PearlConfig getPearlConfig() {
      return this.pearlConfig;
   }

   public EnchantConfig getEnchantConfig() {
      return this.enchantConfig;
   }

   @Override
   public void addMovementHandler(MovementHandler movementHandler) {
      this.movementHandlers.add(movementHandler);
   }

   @Override
   public void addPacketHandler(PacketHandler packetHandler) {
      this.packetHandlers.add(packetHandler);
   }

   @Override
   public void addPearlHandler(PearlHandler pearlHandler) {
      this.pearlHandlers.add(pearlHandler);
   }

   @Override
   public List<PacketHandler> getPacketHandlers() {
      return this.packetHandlers;
   }

   @Override
   public List<MovementHandler> getMovementHandlers() {
      return this.movementHandlers;
   }

   @Override
   public List<PearlHandler> getPearlHandlers() {
      return this.pearlHandlers;
   }

   public KnockbackManager getKnockbackManager() {
      return this.knockbackManager;
   }
}
