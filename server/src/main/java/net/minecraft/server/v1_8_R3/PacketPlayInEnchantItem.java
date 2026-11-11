package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayInEnchantItem implements Packet<PacketListenerPlayIn> {
   private int a;
   private int b;

   public void a(PacketListenerPlayIn var1) {
      var1.a(this);
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.readByte();
      this.b = var1.readByte();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.writeByte(this.a);
      var1.writeByte(this.b);
   }

   public int a() {
      return this.a;
   }

   public int b() {
      return this.b;
   }
}
