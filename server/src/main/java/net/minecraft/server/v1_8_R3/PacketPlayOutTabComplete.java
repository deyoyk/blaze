package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutTabComplete implements Packet<PacketListenerPlayOut> {
   private String[] a;

   public PacketPlayOutTabComplete() {
   }

   public PacketPlayOutTabComplete(String[] var1) {
      this.a = var1;
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = new String[var1.e()];

      for (int var2 = 0; var2 < this.a.length; var2++) {
         this.a[var2] = var1.c(32767);
      }
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.b(this.a.length);

      for (String var5 : this.a) {
         var1.a(var5);
      }
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }
}
