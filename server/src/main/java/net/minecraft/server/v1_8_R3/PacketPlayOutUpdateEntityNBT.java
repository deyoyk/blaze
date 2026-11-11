package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutUpdateEntityNBT implements Packet<PacketListenerPlayOut> {
   private int a;
   private NBTTagCompound b;

   public PacketPlayOutUpdateEntityNBT() {
   }

   public PacketPlayOutUpdateEntityNBT(int var1, NBTTagCompound var2) {
      this.a = var1;
      this.b = var2;
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.e();
      this.b = var1.h();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.b(this.a);
      var1.a(this.b);
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }
}
