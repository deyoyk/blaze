package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class RemoteControlListener extends RemoteConnectionThread {
   private int h;
   private int i;
   private String j;
   private ServerSocket k;
   private String l;
   private Map<SocketAddress, RemoteControlSession> m;

   public RemoteControlListener(IMinecraftServer var1) {
      super(var1, "RCON Listener");
      this.h = var1.a("rcon.port", 0);
      this.l = var1.a("rcon.password", "");
      this.j = var1.E();
      this.i = var1.F();
      if (0 == this.h) {
         this.h = this.i + 10;
         this.b("Setting default rcon port to " + this.h);
         var1.a("rcon.port", Integer.valueOf(this.h));
         if (0 == this.l.length()) {
            var1.a("rcon.password", "");
         }

         var1.a();
      }

      if (0 == this.j.length()) {
         this.j = "0.0.0.0";
      }

      this.f();
      this.k = null;
   }

   private void f() {
      this.m = Maps.newHashMap();
   }

   private void g() {
      Iterator var1 = this.m.entrySet().iterator();

      while (var1.hasNext()) {
         Entry var2 = (Entry)var1.next();
         if (!((RemoteControlSession)var2.getValue()).c()) {
            var1.remove();
         }
      }
   }

   @Override
   public void run() {
      this.b("RCON running on " + this.j + ":" + this.h);

      try {
         while (this.a) {
            try {
               Socket var1 = this.k.accept();
               var1.setSoTimeout(500);
               RemoteControlSession var2 = new RemoteControlSession(this.b, var1);
               var2.a();
               this.m.put(var1.getRemoteSocketAddress(), var2);
               this.g();
            } catch (SocketTimeoutException var7) {
               this.g();
            } catch (IOException var8) {
               if (this.a) {
                  this.b("IO: " + var8.getMessage());
               }
            }
         }
      } finally {
         this.b(this.k);
      }
   }

   @Override
   public void a() {
      if (0 == this.l.length()) {
         this.c("No rcon password set in '" + this.b.b() + "', rcon disabled!");
      } else if (0 >= this.h || 65535 < this.h) {
         this.c("Invalid rcon port " + this.h + " found in '" + this.b.b() + "', rcon disabled!");
      } else if (!this.a) {
         try {
            this.k = new ServerSocket(this.h, 0, InetAddress.getByName(this.j));
            this.k.setSoTimeout(500);
            super.a();
         } catch (IOException var2) {
            this.c("Unable to initialise rcon on " + this.j + ":" + this.h + " : " + var2.getMessage());
         }
      }
   }
}
