package org.bukkit.craftbukkit;

import lol.deyo.blaze.Blaze;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;

public class Main {
   public static boolean useJline = true;
   public static boolean useConsole = true;

   public static void main(String[] args) {
      OptionParser parser = new OptionParser() {
         {
            this.acceptsAll(Main.asList("?", "help"), "Show the help");
            this.acceptsAll(Main.asList("c", "config"), "Properties file to use")
               .withRequiredArg()
               .ofType(File.class)
               .defaultsTo(new File("server.properties"), new File[0])
               .describedAs("Properties file");
            this.acceptsAll(Main.asList("P", "plugins"), "Plugin directory to use")
               .withRequiredArg()
               .ofType(File.class)
               .defaultsTo(new File("plugins"), new File[0])
               .describedAs("Plugin directory");
            this.acceptsAll(Main.asList("h", "host", "server-ip"), "Host to listen on").withRequiredArg().ofType(String.class).describedAs("Hostname or IP");
            this.acceptsAll(Main.asList("W", "world-dir", "universe", "world-container"), "World container")
               .withRequiredArg()
               .ofType(File.class)
               .describedAs("Directory containing worlds");
            this.acceptsAll(Main.asList("w", "world", "level-name"), "World name").withRequiredArg().ofType(String.class).describedAs("World name");
            this.acceptsAll(Main.asList("p", "port", "server-port"), "Port to listen on").withRequiredArg().ofType(Integer.class).describedAs("Port");
            this.acceptsAll(Main.asList("o", "online-mode"), "Whether to use online authentication")
               .withRequiredArg()
               .ofType(Boolean.class)
               .describedAs("Authentication");
            this.acceptsAll(Main.asList("s", "size", "max-players"), "Maximum amount of players")
               .withRequiredArg()
               .ofType(Integer.class)
               .describedAs("Server size");
            this.acceptsAll(Main.asList("d", "date-format"), "Format of the date to display in the console (for log entries)")
               .withRequiredArg()
               .ofType(SimpleDateFormat.class)
               .describedAs("Log date format");
            this.acceptsAll(Main.asList("log-pattern"), "Specfies the log filename pattern")
               .withRequiredArg()
               .ofType(String.class)
               .defaultsTo("server.log", new String[0])
               .describedAs("Log filename");
            this.acceptsAll(Main.asList("log-limit"), "Limits the maximum size of the log file (0 = unlimited)")
               .withRequiredArg()
               .ofType(Integer.class)
               .defaultsTo(0, new Integer[0])
               .describedAs("Max log size");
            this.acceptsAll(Main.asList("log-count"), "Specified how many log files to cycle through")
               .withRequiredArg()
               .ofType(Integer.class)
               .defaultsTo(1, new Integer[0])
               .describedAs("Log count");
            this.acceptsAll(Main.asList("log-append"), "Whether to append to the log file")
               .withRequiredArg()
               .ofType(Boolean.class)
               .defaultsTo(true, new Boolean[0])
               .describedAs("Log append");
            this.acceptsAll(Main.asList("log-strip-color"), "Strips color codes from log file");
            this.acceptsAll(Main.asList("b", "bukkit-settings"), "File for bukkit settings")
               .withRequiredArg()
               .ofType(File.class)
               .defaultsTo(new File("bukkit.yml"), new File[0])
               .describedAs("Yml file");
            this.acceptsAll(Main.asList("C", "commands-settings"), "File for command settings")
               .withRequiredArg()
               .ofType(File.class)
               .defaultsTo(new File("commands.yml"), new File[0])
               .describedAs("Yml file");
            this.acceptsAll(Main.asList("nojline"), "Disables jline and emulates the vanilla console");
            this.acceptsAll(Main.asList("noconsole"), "Disables the console");
            this.acceptsAll(Main.asList("v", "version"), "Show the CraftBukkit Version");
            this.acceptsAll(Main.asList("demo"), "Demo mode");
            this.acceptsAll(Main.asList("S", "spigot-settings"), "File for spigot settings")
               .withRequiredArg()
               .ofType(File.class)
               .defaultsTo(new File("spigot.yml"), new File[0])
               .describedAs("Yml file");
            this.acceptsAll(Main.asList("paper", "paper-settings"), "File for paperspigot settings")
               .withRequiredArg()
               .ofType(File.class)
               .defaultsTo(new File("paper.yml"), new File[0])
               .describedAs("Yml file");
            this.acceptsAll(Main.asList("vortex", "vortex-settings"), "File for vortexspigot settings")
               .withRequiredArg()
               .ofType(File.class)
               .defaultsTo(new File("vortex.yml"), new File[0])
               .describedAs("Yml file");
            this.acceptsAll(Main.asList("lang", "lang-settings"), "File for vortexspigot language")
               .withRequiredArg()
               .ofType(File.class)
               .defaultsTo(new File("lang.yml"), new File[0])
               .describedAs("Yml file");
            this.acceptsAll(Main.asList("pearl", "pearl-settings"), "File for vortexpearls settings")
               .withRequiredArg()
               .ofType(File.class)
               .defaultsTo(new File("pearls.yml"), new File[0])
               .describedAs("Yml file");
            this.acceptsAll(Main.asList("knockback", "knockback-settings"), "File for knockback settings")
               .withRequiredArg()
               .ofType(File.class)
               .defaultsTo(new File("knockback.yml"), new File[0])
               .describedAs("Yml file");
         }
      };
      OptionSet options = null;

      try {
         options = parser.parse(args);
      } catch (OptionException var7) {
         Logger.getLogger(Main.class.getName()).log(Level.SEVERE, var7.getLocalizedMessage());
      }

      if (System.getProperty("jdk.nio.maxCachedBufferSize") == null) {
         System.setProperty("jdk.nio.maxCachedBufferSize", "262144");
      }

      if (options == null || options.has("?")) {
         try {
            parser.printHelpOn(System.out);
         } catch (IOException var6) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, var6);
         }
      } else if (options.has("v")) {
         System.out.println(CraftServer.class.getPackage().getImplementationVersion());
      } else {
         String path = new File(".").getAbsolutePath();
         if (path.contains("!") || path.contains("+")) {
            System.err.println("Cannot run server in a directory with ! or + in the pathname. Please rename the affected folders and try again.");
            return;
         }

         try {
            if (options.has("nojline")) {
               System.setProperty("terminal.jline", "false");
               useJline = false;
            }

            if (options.has("noconsole")) {
               useConsole = false;
               useJline = false;
               System.setProperty("terminal.jline", "false");
            }

            new Blaze();
            System.setProperty("library.jansi.version", "Blaze");
            System.out.println("Loading libraries, please wait...");
            MinecraftServer.main(options);
         } catch (Throwable var5) {
            var5.printStackTrace();
         }
      }
   }

   private static List<String> asList(String... params) {
      return Arrays.asList(params);
   }
}
