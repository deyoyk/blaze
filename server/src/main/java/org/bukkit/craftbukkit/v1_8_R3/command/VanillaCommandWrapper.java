package org.bukkit.craftbukkit.v1_8_R3.command;

import java.util.List;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.CommandAbstract;
import net.minecraft.server.v1_8_R3.CommandBlockListenerAbstract;
import net.minecraft.server.v1_8_R3.CommandException;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityMinecartCommandBlock;
import net.minecraft.server.v1_8_R3.EnumChatFormat;
import net.minecraft.server.v1_8_R3.ExceptionUsage;
import net.minecraft.server.v1_8_R3.ICommandListener;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PlayerSelector;
import net.minecraft.server.v1_8_R3.RemoteControlCommandListener;
import net.minecraft.server.v1_8_R3.WorldServer;
import net.minecraft.server.v1_8_R3.CommandObjectiveExecutor.EnumCommandResult;
import org.apache.commons.lang.Validate;
import org.apache.logging.log4j.Level;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftMinecartCommand;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;

public final class VanillaCommandWrapper extends VanillaCommand {
   protected final CommandAbstract vanillaCommand;
   public static CommandSender lastSender = null;

   public VanillaCommandWrapper(CommandAbstract vanillaCommand) {
      super(vanillaCommand.getCommand());
      this.vanillaCommand = vanillaCommand;
   }

   public VanillaCommandWrapper(CommandAbstract vanillaCommand, String usage) {
      super(vanillaCommand.getCommand());
      this.vanillaCommand = vanillaCommand;
      this.description = "A Mojang provided command.";
      this.usageMessage = usage;
      this.setPermission("minecraft.command." + vanillaCommand.getCommand());
   }

   @Override
   public boolean execute(CommandSender sender, String commandLabel, String[] args) {
      if (!this.testPermission(sender)) {
         return true;
      } else {
         ICommandListener icommandlistener = this.getListener(sender);
         this.dispatchVanillaCommand(sender, icommandlistener, args);
         return true;
      }
   }

   @Override
   public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
      return this.tabComplete(sender, alias, args, null);
   }

   @Override
   public List<String> tabComplete(CommandSender sender, String alias, String[] args, org.bukkit.Location location) throws IllegalArgumentException {
      Validate.notNull(sender, "Sender cannot be null");
      Validate.notNull(args, "Arguments cannot be null");
      Validate.notNull(alias, "Alias cannot be null");
      if (location == null) {
         BlockPosition blockPos;
         if (sender instanceof Player) {
            Player player = (Player)sender;
            blockPos = new BlockPosition(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
         } else {
            blockPos = new BlockPosition(0, 0, 0);
         }

         return this.vanillaCommand.tabComplete(this.getListener(sender), args, blockPos);
      } else {
         return this.vanillaCommand
            .tabComplete(this.getListener(sender), args, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
      }
   }

   public int dispatchVanillaCommand(CommandSender bSender, ICommandListener icommandlistener, String[] as) {
      int i = this.getPlayerListSize(as);
      int j = 0;
      WorldServer[] prev = MinecraftServer.getServer().worldServer;
      MinecraftServer server = MinecraftServer.getServer();
      server.worldServer = new WorldServer[server.worlds.size()];
      server.worldServer[0] = (WorldServer)icommandlistener.getWorld();
      int bpos = 0;

      for (int pos = 1; pos < server.worldServer.length; pos++) {
         WorldServer world = (WorldServer)server.worlds.get(bpos++);
         if (server.worldServer[0] == world) {
            pos--;
         } else {
            server.worldServer[pos] = world;
         }
      }

      try {
         if (this.vanillaCommand.canUse(icommandlistener)) {
            if (i > -1) {
               List<Entity> list = PlayerSelector.getPlayers(icommandlistener, as[i], Entity.class);
               String s2 = as[i];
               icommandlistener.a(EnumCommandResult.AFFECTED_ENTITIES, list.size());

               for (Entity entity : list) {
                  CommandSender oldSender = lastSender;
                  lastSender = bSender;

                  try {
                     as[i] = entity.getUniqueID().toString();
                     this.vanillaCommand.execute(icommandlistener, as);
                     j++;
                  } catch (ExceptionUsage var32) {
                     ChatMessage chatmessage = new ChatMessage("commands.generic.usage", new Object[]{new ChatMessage(var32.getMessage(), var32.getArgs())});
                     chatmessage.getChatModifier().setColor(EnumChatFormat.RED);
                     icommandlistener.sendMessage(chatmessage);
                  } catch (CommandException var33) {
                     CommandAbstract.a(icommandlistener, this.vanillaCommand, 1, var33.getMessage(), var33.getArgs());
                  } finally {
                     lastSender = oldSender;
                  }
               }

               as[i] = s2;
            } else {
               icommandlistener.a(EnumCommandResult.AFFECTED_ENTITIES, 1);
               this.vanillaCommand.execute(icommandlistener, as);
               j++;
            }
         } else {
            ChatMessage chatmessage = new ChatMessage("commands.generic.permission", new Object[0]);
            chatmessage.getChatModifier().setColor(EnumChatFormat.RED);
            icommandlistener.sendMessage(chatmessage);
         }
      } catch (ExceptionUsage var35) {
         ChatMessage chatmessage1 = new ChatMessage("commands.generic.usage", new Object[]{new ChatMessage(var35.getMessage(), var35.getArgs())});
         chatmessage1.getChatModifier().setColor(EnumChatFormat.RED);
         icommandlistener.sendMessage(chatmessage1);
      } catch (CommandException var36) {
         CommandAbstract.a(icommandlistener, this.vanillaCommand, 0, var36.getMessage(), var36.getArgs());
      } catch (Throwable var37) {
         ChatMessage chatmessage3 = new ChatMessage("commands.generic.exception", new Object[0]);
         chatmessage3.getChatModifier().setColor(EnumChatFormat.RED);
         icommandlistener.sendMessage(chatmessage3);
         if (icommandlistener.f() instanceof EntityMinecartCommandBlock) {
            MinecraftServer.LOGGER
               .log(
                  Level.WARN,
                  String.format(
                     "MinecartCommandBlock at (%d,%d,%d) failed to handle command",
                     icommandlistener.getChunkCoordinates().getX(),
                     icommandlistener.getChunkCoordinates().getY(),
                     icommandlistener.getChunkCoordinates().getZ()
                  ),
                  var37
               );
         } else if (icommandlistener instanceof CommandBlockListenerAbstract) {
            CommandBlockListenerAbstract listener = (CommandBlockListenerAbstract)icommandlistener;
            MinecraftServer.LOGGER
               .log(
                  Level.WARN,
                  String.format(
                     "CommandBlock at (%d,%d,%d) failed to handle command",
                     listener.getChunkCoordinates().getX(),
                     listener.getChunkCoordinates().getY(),
                     listener.getChunkCoordinates().getZ()
                  ),
                  var37
               );
         } else {
            MinecraftServer.LOGGER.log(Level.WARN, "Unknown CommandBlock failed to handle command", var37);
         }
      } finally {
         MinecraftServer.getServer().worldServer = prev;
      }

      icommandlistener.a(EnumCommandResult.SUCCESS_COUNT, j);
      return j;
   }

   private ICommandListener getListener(CommandSender sender) {
      if (sender instanceof Player) {
         return ((CraftPlayer)sender).getHandle();
      } else if (sender instanceof BlockCommandSender) {
         return ((CraftBlockCommandSender)sender).getTileEntity();
      } else if (sender instanceof CommandMinecart) {
         return ((EntityMinecartCommandBlock)((CraftMinecartCommand)sender).getHandle()).getCommandBlock();
      } else if (sender instanceof RemoteConsoleCommandSender) {
         return RemoteControlCommandListener.getInstance();
      } else if (sender instanceof ConsoleCommandSender) {
         return ((CraftServer)sender.getServer()).getServer();
      } else if (sender instanceof ProxiedCommandSender) {
         return ((ProxiedNativeCommandSender)sender).getHandle();
      } else {
         throw new IllegalArgumentException("Cannot make " + sender + " a vanilla command listener");
      }
   }

   private int getPlayerListSize(String[] as) {
      for (int i = 0; i < as.length; i++) {
         if (this.vanillaCommand.isListStart(as, i) && PlayerSelector.isList(as[i])) {
            return i;
         }
      }

      return -1;
   }

   public static String[] dropFirstArgument(String[] as) {
      String[] as1 = new String[as.length - 1];
      System.arraycopy(as, 1, as1, 0, as.length - 1);
      return as1;
   }
}
