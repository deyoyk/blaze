package lol.deyo.blaze.console;

import net.minecraft.server.v1_8_R3.DedicatedServer;
import net.minecrell.terminalconsole.SimpleTerminalConsole;
import org.bukkit.craftbukkit.v1_8_R3.command.ConsoleCommandCompleter;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

public final class BlazeConsole extends SimpleTerminalConsole {
   private final DedicatedServer server;

   public BlazeConsole(DedicatedServer server) {
      this.server = server;
   }

   protected LineReader buildReader(LineReaderBuilder builder) {
      return super.buildReader(builder.appName("Blaze").completer(new ConsoleCommandCompleter(this.server)));
   }

   protected boolean isRunning() {
      return !this.server.isStopped() && this.server.isRunning();
   }

   protected void runCommand(String command) {
      this.server.issueCommand(command, this.server);
   }

   protected void shutdown() {
      this.server.safeShutdown();
   }
}
