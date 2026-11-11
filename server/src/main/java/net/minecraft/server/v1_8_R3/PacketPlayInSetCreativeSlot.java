package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayInSetCreativeSlot implements Packet<PacketListenerPlayIn> {
   private int slot;
   private ItemStack b;

   public void a(PacketListenerPlayIn var1) {
      var1.a(this);
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.slot = var1.readShort();
      this.b = var1.i();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.writeShort(this.slot);
      var1.a(this.b);
   }

   public int a() {
      return this.slot;
   }

   public ItemStack getItemStack() {
      return this.b;
   }
}
