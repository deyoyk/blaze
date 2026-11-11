package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutSpawnEntityWeather implements Packet<PacketListenerPlayOut> {
   private int a;
   private int b;
   private int c;
   private int d;
   private int e;

   public PacketPlayOutSpawnEntityWeather() {
   }

   public PacketPlayOutSpawnEntityWeather(Entity var1) {
      this.a = var1.getId();
      this.b = MathHelper.floor(var1.locX * 32.0);
      this.c = MathHelper.floor(var1.locY * 32.0);
      this.d = MathHelper.floor(var1.locZ * 32.0);
      if (var1 instanceof EntityLightning) {
         this.e = 1;
      }
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.e();
      this.e = var1.readByte();
      this.b = var1.readInt();
      this.c = var1.readInt();
      this.d = var1.readInt();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.b(this.a);
      var1.writeByte(this.e);
      var1.writeInt(this.b);
      var1.writeInt(this.c);
      var1.writeInt(this.d);
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }
}
