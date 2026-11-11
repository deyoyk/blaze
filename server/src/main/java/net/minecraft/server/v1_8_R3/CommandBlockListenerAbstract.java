package net.minecraft.server.v1_8_R3;

import com.google.common.base.Joiner;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_8_R3.command.VanillaCommandWrapper;

public abstract class CommandBlockListenerAbstract implements ICommandListener {
   private static final SimpleDateFormat a = new SimpleDateFormat("HH:mm:ss");
   private int b;
   private boolean c = true;
   private IChatBaseComponent d = null;
   private String e = "";
   private String f = "@";
   private final CommandObjectiveExecutor g = new CommandObjectiveExecutor();
   protected CommandSender sender;

   public int j() {
      return this.b;
   }

   public IChatBaseComponent k() {
      return this.d;
   }

   public void a(NBTTagCompound nbttagcompound) {
      nbttagcompound.setString("Command", this.e);
      nbttagcompound.setInt("SuccessCount", this.b);
      nbttagcompound.setString("CustomName", this.f);
      nbttagcompound.setBoolean("TrackOutput", this.c);
      if (this.d != null && this.c) {
         nbttagcompound.setString("LastOutput", IChatBaseComponent.ChatSerializer.a(this.d));
      }

      this.g.b(nbttagcompound);
   }

   public void b(NBTTagCompound nbttagcompound) {
      this.e = nbttagcompound.getString("Command");
      this.b = nbttagcompound.getInt("SuccessCount");
      if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
         this.f = nbttagcompound.getString("CustomName");
      }

      if (nbttagcompound.hasKeyOfType("TrackOutput", 1)) {
         this.c = nbttagcompound.getBoolean("TrackOutput");
      }

      if (nbttagcompound.hasKeyOfType("LastOutput", 8) && this.c) {
         this.d = IChatBaseComponent.ChatSerializer.a(nbttagcompound.getString("LastOutput"));
      }

      this.g.a(nbttagcompound);
   }

   @Override
   public boolean a(int i, String s) {
      return i <= 2;
   }

   public void setCommand(String s) {
      this.e = s;
      this.b = 0;
   }

   public String getCommand() {
      return this.e;
   }

   public void a(World world) {
      MinecraftServer minecraftserver = MinecraftServer.getServer();
      if (minecraftserver != null && minecraftserver.O() && minecraftserver.getEnableCommandBlock()) {
         ICommandHandler icommandhandler = minecraftserver.getCommandHandler();

         try {
            this.d = null;
            this.b = executeCommand(this, this.sender, this.e);
         } catch (Throwable var7) {
            CrashReport crashreport = CrashReport.a(var7, "Executing command block");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Command to be executed");
            crashreportsystemdetails.a("Command", new Callable() {
               public String a() throws Exception {
                  return CommandBlockListenerAbstract.this.getCommand();
               }

               @Override
               public Object call() throws Exception {
                  return this.a();
               }
            });
            crashreportsystemdetails.a("Name", new Callable() {
               public String a() throws Exception {
                  return CommandBlockListenerAbstract.this.getName();
               }

               @Override
               public Object call() throws Exception {
                  return this.a();
               }
            });
            throw new ReportedException(crashreport);
         }
      } else {
         this.b = 0;
      }
   }

   public static int executeCommand(ICommandListener sender, CommandSender bSender, String command) {
      SimpleCommandMap commandMap = sender.getWorld().getServer().getCommandMap();
      Joiner joiner = Joiner.on(" ");
      if (command.startsWith("/")) {
         command = command.substring(1);
      }

      String[] args = command.split(" ");
      ArrayList<String[]> commands = new ArrayList<>();
      String cmd = args[0];
      if (cmd.startsWith("minecraft:")) {
         cmd = cmd.substring("minecraft:".length());
      }

      if (cmd.startsWith("bukkit:")) {
         cmd = cmd.substring("bukkit:".length());
      }

      if (!cmd.equalsIgnoreCase("stop")
         && !cmd.equalsIgnoreCase("kick")
         && !cmd.equalsIgnoreCase("op")
         && !cmd.equalsIgnoreCase("deop")
         && !cmd.equalsIgnoreCase("ban")
         && !cmd.equalsIgnoreCase("ban-ip")
         && !cmd.equalsIgnoreCase("pardon")
         && !cmd.equalsIgnoreCase("pardon-ip")
         && !cmd.equalsIgnoreCase("reload")) {
         if (sender.getWorld().players.isEmpty()) {
            return 0;
         } else {
            Command commandBlockCommand = commandMap.getCommand(args[0]);
            if (sender.getWorld().getServer().getCommandBlockOverride(args[0])) {
               commandBlockCommand = commandMap.getCommand("minecraft:" + args[0]);
            }

            if (commandBlockCommand instanceof VanillaCommandWrapper) {
               command = command.trim();
               if (command.startsWith("/")) {
                  command = command.substring(1);
               }

               String[] as = command.split(" ");
               as = VanillaCommandWrapper.dropFirstArgument(as);
               return !((VanillaCommandWrapper)commandBlockCommand).testPermission(bSender)
                  ? 0
                  : ((VanillaCommandWrapper)commandBlockCommand).dispatchVanillaCommand(bSender, sender, as);
            } else if (commandMap.getCommand(args[0]) == null) {
               return 0;
            } else {
               commands.add(args);
               WorldServer[] prev = MinecraftServer.getServer().worldServer;
               MinecraftServer server = MinecraftServer.getServer();
               server.worldServer = new WorldServer[server.worlds.size()];
               server.worldServer[0] = (WorldServer)sender.getWorld();
               int bpos = 0;

               for (int pos = 1; pos < server.worldServer.length; pos++) {
                  WorldServer world = server.worlds.get(bpos++);
                  if (server.worldServer[0] == world) {
                     pos--;
                  } else {
                     server.worldServer[pos] = world;
                  }
               }

               try {
                  ArrayList<String[]> newCommands = new ArrayList<>();

                  for (int i = 0; i < args.length; i++) {
                     if (PlayerSelector.isPattern(args[i])) {
                        for (int j = 0; j < commands.size(); j++) {
                           newCommands.addAll(buildCommands(sender, commands.get(j), i));
                        }

                        ArrayList<String[]> temp = commands;
                        commands = newCommands;
                        newCommands = temp;
                        temp.clear();
                     }
                  }
               } finally {
                  MinecraftServer.getServer().worldServer = prev;
               }

               int completed = 0;

               for (int ix = 0; ix < commands.size(); ix++) {
                  try {
                     if (commandMap.dispatch(bSender, joiner.join(Arrays.asList(commands.get(ix))))) {
                        completed++;
                     }
                  } catch (Throwable var18) {
                     if (sender.f() instanceof EntityMinecartCommandBlock) {
                        MinecraftServer.getServer()
                           .server
                           .getLogger()
                           .log(
                              Level.WARNING,
                              String.format(
                                 "MinecartCommandBlock at (%d,%d,%d) failed to handle command",
                                 sender.getChunkCoordinates().getX(),
                                 sender.getChunkCoordinates().getY(),
                                 sender.getChunkCoordinates().getZ()
                              ),
                              var18
                           );
                     } else if (sender instanceof CommandBlockListenerAbstract) {
                        CommandBlockListenerAbstract listener = (CommandBlockListenerAbstract)sender;
                        MinecraftServer.getServer()
                           .server
                           .getLogger()
                           .log(
                              Level.WARNING,
                              String.format(
                                 "CommandBlock at (%d,%d,%d) failed to handle command",
                                 listener.getChunkCoordinates().getX(),
                                 listener.getChunkCoordinates().getY(),
                                 listener.getChunkCoordinates().getZ()
                              ),
                              var18
                           );
                     } else {
                        MinecraftServer.getServer()
                           .server
                           .getLogger()
                           .log(Level.WARNING, String.format("Unknown CommandBlock failed to handle command"), var18);
                     }
                  }
               }

               return completed;
            }
         }
      } else {
         return 0;
      }
   }

   private static ArrayList<String[]> buildCommands(ICommandListener sender, String[] args, int pos) {
      ArrayList<String[]> commands = new ArrayList<>();
      List<EntityPlayer> players = PlayerSelector.getPlayers(sender, args[pos], EntityPlayer.class);
      if (players != null) {
         for (EntityPlayer player : players) {
            if (player.world == sender.getWorld()) {
               String[] command = (String[])args.clone();
               command[pos] = player.getName();
               commands.add(command);
            }
         }
      }

      return commands;
   }

   @Override
   public String getName() {
      return this.f;
   }

   @Override
   public IChatBaseComponent getScoreboardDisplayName() {
      return new ChatComponentText(this.getName());
   }

   public void setName(String s) {
      this.f = s;
   }

   @Override
   public void sendMessage(IChatBaseComponent ichatbasecomponent) {
      if (this.c && this.getWorld() != null) {
         this.d = new ChatComponentText("[" + a.format(new Date()) + "] ").addSibling(ichatbasecomponent);
         this.h();
      }
   }

   @Override
   public boolean getSendCommandFeedback() {
      MinecraftServer minecraftserver = MinecraftServer.getServer();
      return minecraftserver == null || !minecraftserver.O() || minecraftserver.worldServer[0].getGameRules().getBoolean("commandBlockOutput");
   }

   @Override
   public void a(CommandObjectiveExecutor.EnumCommandResult commandobjectiveexecutor_enumcommandresult, int i) {
      this.g.a(this, commandobjectiveexecutor_enumcommandresult, i);
   }

   public abstract void h();

   public void b(IChatBaseComponent ichatbasecomponent) {
      this.d = ichatbasecomponent;
   }

   public void a(boolean flag) {
      this.c = flag;
   }

   public boolean m() {
      return this.c;
   }

   public boolean a(EntityHuman entityhuman) {
      return entityhuman.abilities.canInstantlyBuild;
   }

   public CommandObjectiveExecutor n() {
      return this.g;
   }
}
