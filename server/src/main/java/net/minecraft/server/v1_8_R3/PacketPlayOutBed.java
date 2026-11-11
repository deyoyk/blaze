package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutBed implements Packet<PacketListenerPlayOut> {
   private int a;
   private BlockPosition b;

   public PacketPlayOutBed() {
   }

   public PacketPlayOutBed(EntityHuman var1, BlockPosition var2) {
      this.a = var1.getId();
      this.b = var2;
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.e();
      this.b = var1.c();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.b(this.a);
      var1.a(this.b);
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }
}
