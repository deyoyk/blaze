package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutHeldItemSlot implements Packet<PacketListenerPlayOut> {
   private int a;

   public PacketPlayOutHeldItemSlot() {
   }

   public PacketPlayOutHeldItemSlot(int var1) {
      this.a = var1;
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.readByte();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.writeByte(this.a);
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }
}
