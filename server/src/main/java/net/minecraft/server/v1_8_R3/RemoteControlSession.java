package net.minecraft.server.v1_8_R3;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RemoteControlSession extends RemoteConnectionThread {
   private static final Logger h = LogManager.getLogger();
   private boolean i;
   private Socket j;
   private byte[] k = new byte[1460];
   private String l;

   RemoteControlSession(IMinecraftServer var1, Socket var2) {
      super(var1, "RCON Client");
      this.j = var2;

      try {
         this.j.setSoTimeout(0);
      } catch (Exception var4) {
         this.a = false;
      }

      this.l = var1.a("rcon.password", "");
      this.b("Rcon connection from: " + var2.getInetAddress());
   }

   @Override
   public void run() {
      try {
         try {
            while (this.a) {
               BufferedInputStream var1 = new BufferedInputStream(this.j.getInputStream());
               int var2 = var1.read(this.k, 0, 1460);
               if (10 > var2) {
                  return;
               }

               int var3 = 0;
               int var4 = StatusChallengeUtils.b(this.k, 0, var2);
               if (var4 != var2 - 4) {
                  return;
               }

               var3 += 4;
               int var5 = StatusChallengeUtils.b(this.k, var3, var2);
               var3 += 4;
               int var6 = StatusChallengeUtils.b(this.k, var3);
               var3 += 4;
               switch (var6) {
                  case 2:
                     if (this.i) {
                        String var8 = StatusChallengeUtils.a(this.k, var3, var2);

                        try {
                           this.a(var5, this.b.executeRemoteCommand(var8));
                        } catch (Exception var16) {
                           this.a(var5, "Error executing: " + var8 + " (" + var16.getMessage() + ")");
                        }
                        break;
                     }

                     this.f();
                     break;
                  case 3:
                     String var7 = StatusChallengeUtils.a(this.k, var3, var2);
                     var3 += var7.length();
                     if (0 != var7.length() && var7.equals(this.l)) {
                        this.i = true;
                        this.a(var5, 2, "");
                        break;
                     }

                     this.i = false;
                     this.f();
                     break;
                  default:
                     this.a(var5, String.format("Unknown request %s", Integer.toHexString(var6)));
               }
            }

            return;
         } catch (SocketTimeoutException var17) {
         } catch (IOException var18) {
         } catch (Exception var19) {
            h.error("Exception whilst parsing RCON input", var19);
         }
      } finally {
         this.g();
      }
   }

   private void a(int var1, int var2, String var3) throws IOException {
      ByteArrayOutputStream var4 = new ByteArrayOutputStream(1248);
      DataOutputStream var5 = new DataOutputStream(var4);
      byte[] var6 = var3.getBytes("UTF-8");
      var5.writeInt(Integer.reverseBytes(var6.length + 10));
      var5.writeInt(Integer.reverseBytes(var1));
      var5.writeInt(Integer.reverseBytes(var2));
      var5.write(var6);
      var5.write(0);
      var5.write(0);
      this.j.getOutputStream().write(var4.toByteArray());
   }

   private void f() throws IOException {
      this.a(-1, 2, "");
   }

   private void a(int var1, String var2) throws IOException {
      int var3 = var2.length();

      do {
         int var4 = 4096 <= var3 ? 4096 : var3;
         this.a(var1, 0, var2.substring(0, var4));
         var2 = var2.substring(var4);
         var3 = var2.length();
      } while (0 != var3);
   }

   private void g() {
      if (null != this.j) {
         try {
            this.j.close();
         } catch (IOException var2) {
            this.c("IO: " + var2.getMessage());
         }

         this.j = null;
      }
   }
}
