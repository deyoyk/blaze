package org.bukkit.craftbukkit.v1_8_R3.util;

import net.minecraft.server.v1_8_R3.ExceptionWorldConflict;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecrell.terminalconsole.TerminalConsoleAppender;

public class ServerShutdownThread extends Thread {
   private final MinecraftServer server;

   public ServerShutdownThread(MinecraftServer server) {
      this.server = server;
   }

   @Override
   public void run() {
      try {
         this.server.stop();
      } catch (ExceptionWorldConflict var10) {
         var10.printStackTrace();
      } finally {
         try {
            TerminalConsoleAppender.close();
         } catch (Exception var9) {
         }
      }
   }
}
