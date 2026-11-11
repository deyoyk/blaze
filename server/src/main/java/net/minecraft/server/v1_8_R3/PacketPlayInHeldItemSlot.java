package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayInHeldItemSlot implements Packet<PacketListenerPlayIn> {
   private int itemInHandIndex;

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.itemInHandIndex = var1.readShort();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.writeShort(this.itemInHandIndex);
   }

   public void a(PacketListenerPlayIn var1) {
      var1.a(this);
   }

   public int a() {
      return this.itemInHandIndex;
   }
}
