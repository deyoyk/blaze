package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutAnimation implements Packet<PacketListenerPlayOut> {
   private int a;
   private int b;

   public PacketPlayOutAnimation() {
   }

   public PacketPlayOutAnimation(Entity var1, int var2) {
      this.a = var1.getId();
      this.b = var2;
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.e();
      this.b = var1.readUnsignedByte();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.b(this.a);
      var1.writeByte(this.b);
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }
}
