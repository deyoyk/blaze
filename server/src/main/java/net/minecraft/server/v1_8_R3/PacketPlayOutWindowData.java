package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutWindowData implements Packet<PacketListenerPlayOut> {
   private int a;
   private int b;
   private int c;

   public PacketPlayOutWindowData() {
   }

   public PacketPlayOutWindowData(int var1, int var2, int var3) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.readUnsignedByte();
      this.b = var1.readShort();
      this.c = var1.readShort();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.writeByte(this.a);
      var1.writeShort(this.b);
      var1.writeShort(this.c);
   }
}
