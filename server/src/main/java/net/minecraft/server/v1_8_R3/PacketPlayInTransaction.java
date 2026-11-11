package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayInTransaction implements Packet<PacketListenerPlayIn> {
   private int a;
   private short b;
   private boolean c;

   public void a(PacketListenerPlayIn var1) {
      var1.a(this);
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.readByte();
      this.b = var1.readShort();
      this.c = var1.readByte() != 0;
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.writeByte(this.a);
      var1.writeShort(this.b);
      var1.writeByte(this.c ? 1 : 0);
   }

   public int a() {
      return this.a;
   }

   public short b() {
      return this.b;
   }
}
