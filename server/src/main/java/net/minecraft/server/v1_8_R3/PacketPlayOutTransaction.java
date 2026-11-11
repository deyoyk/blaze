package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutTransaction implements Packet<PacketListenerPlayOut> {
   private int a;
   private short b;
   private boolean c;

   public PacketPlayOutTransaction() {
   }

   public PacketPlayOutTransaction(int var1, short var2, boolean var3) {
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
      this.c = var1.readBoolean();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.writeByte(this.a);
      var1.writeShort(this.b);
      var1.writeBoolean(this.c);
   }
}
