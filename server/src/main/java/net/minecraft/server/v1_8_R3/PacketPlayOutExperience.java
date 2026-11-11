package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutExperience implements Packet<PacketListenerPlayOut> {
   private float a;
   private int b;
   private int c;

   public PacketPlayOutExperience() {
   }

   public PacketPlayOutExperience(float var1, int var2, int var3) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.readFloat();
      this.c = var1.e();
      this.b = var1.e();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.writeFloat(this.a);
      var1.b(this.c);
      var1.b(this.b);
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }
}
