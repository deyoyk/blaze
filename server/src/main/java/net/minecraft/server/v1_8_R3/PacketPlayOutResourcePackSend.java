package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutResourcePackSend implements Packet<PacketListenerPlayOut> {
   private String a;
   private String b;

   public PacketPlayOutResourcePackSend() {
   }

   public PacketPlayOutResourcePackSend(String var1, String var2) {
      this.a = var1;
      this.b = var2;
      if (var2.length() > 40) {
         throw new IllegalArgumentException("Hash is too long (max 40, was " + var2.length() + ")");
      }
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.c(32767);
      this.b = var1.c(40);
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.a(this.a);
      var1.a(this.b);
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }
}
