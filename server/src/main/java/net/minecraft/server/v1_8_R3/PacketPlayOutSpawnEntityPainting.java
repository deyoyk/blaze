package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutSpawnEntityPainting implements Packet<PacketListenerPlayOut> {
   private int a;
   private BlockPosition b;
   private EnumDirection c;
   private String d;

   public PacketPlayOutSpawnEntityPainting() {
   }

   public PacketPlayOutSpawnEntityPainting(EntityPainting var1) {
      this.a = var1.getId();
      this.b = var1.getBlockPosition();
      this.c = var1.direction;
      this.d = var1.art.B;
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.e();
      this.d = var1.c(EntityPainting.EnumArt.A);
      this.b = var1.c();
      this.c = EnumDirection.fromType2(var1.readUnsignedByte());
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.b(this.a);
      var1.a(this.d);
      var1.a(this.b);
      var1.writeByte(this.c.b());
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }
}
