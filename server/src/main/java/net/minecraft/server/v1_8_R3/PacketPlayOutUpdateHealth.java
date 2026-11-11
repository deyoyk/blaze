package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutUpdateHealth implements Packet<PacketListenerPlayOut> {
   private float a;
   private int b;
   private float c;

   public PacketPlayOutUpdateHealth() {
   }

   public PacketPlayOutUpdateHealth(float var1, int var2, float var3) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.readFloat();
      this.b = var1.e();
      this.c = var1.readFloat();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.writeFloat(this.a);
      var1.b(this.b);
      var1.writeFloat(this.c);
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }
}
