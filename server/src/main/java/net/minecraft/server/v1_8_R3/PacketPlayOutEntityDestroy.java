package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutEntityDestroy implements Packet<PacketListenerPlayOut> {
   private int[] a;

   public PacketPlayOutEntityDestroy() {
   }

   public PacketPlayOutEntityDestroy(int... var1) {
      this.a = var1;
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = new int[var1.e()];

      for (int var2 = 0; var2 < this.a.length; var2++) {
         this.a[var2] = var1.e();
      }
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.b(this.a.length);

      for (int var2 = 0; var2 < this.a.length; var2++) {
         var1.b(this.a[var2]);
      }
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }
}
