package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutWorldEvent implements Packet<PacketListenerPlayOut> {
   private int a;
   private BlockPosition b;
   private int c;
   private boolean d;

   public PacketPlayOutWorldEvent() {
   }

   public PacketPlayOutWorldEvent(int var1, BlockPosition var2, int var3, boolean var4) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.d = var4;
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.readInt();
      this.b = var1.c();
      this.c = var1.readInt();
      this.d = var1.readBoolean();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.writeInt(this.a);
      var1.a(this.b);
      var1.writeInt(this.c);
      var1.writeBoolean(this.d);
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }
}
