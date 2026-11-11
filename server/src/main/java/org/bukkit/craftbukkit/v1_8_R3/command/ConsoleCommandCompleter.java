package org.bukkit.craftbukkit.v1_8_R3.command;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import net.minecraft.server.v1_8_R3.DedicatedServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.util.Waitable;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

public class ConsoleCommandCompleter implements Completer {
   private final DedicatedServer server;

   public ConsoleCommandCompleter(DedicatedServer server) {
      this.server = server;
   }

   public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
      final CraftServer server = this.server.server;
      final String buffer = line.line();
      Waitable<List<String>> waitable = new Waitable<List<String>>() {
         protected List<String> evaluate() {
            return server.getCommandMap().tabComplete(server.getConsoleSender(), buffer);
         }
      };
      server.getServer().processQueue.add(waitable);

      try {
         List<String> offers = waitable.get();
         if (offers == null) {
            return;
         }

         for (String completion : offers) {
            if (!completion.isEmpty()) {
               candidates.add(new Candidate(completion));
            }
         }
      } catch (ExecutionException var10) {
         server.getLogger().log(Level.WARNING, "Unhandled exception when tab completing", (Throwable)var10);
      } catch (InterruptedException var11) {
         Thread.currentThread().interrupt();
      }
   }
}
