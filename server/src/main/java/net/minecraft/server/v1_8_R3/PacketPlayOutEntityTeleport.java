package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutEntityTeleport implements Packet<PacketListenerPlayOut> {
   public int a;
   public int b;
   public int c;
   public int d;
   public boolean g;
   public byte e;
   public byte f;

   public PacketPlayOutEntityTeleport() {
   }

   public PacketPlayOutEntityTeleport(Entity var1) {
      this.a = var1.getId();
      this.b = MathHelper.floor(var1.locX * 32.0);
      this.c = MathHelper.floor(var1.locY * 32.0);
      this.d = MathHelper.floor(var1.locZ * 32.0);
      this.e = (byte)(var1.yaw * 256.0F / 360.0F);
      this.f = (byte)(var1.pitch * 256.0F / 360.0F);
      this.g = var1.onGround;
   }

   public PacketPlayOutEntityTeleport(int var1, int var2, int var3, int var4, byte var5, byte var6, boolean var7) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.d = var4;
      this.e = var5;
      this.f = var6;
      this.g = var7;
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.e();
      this.b = var1.readInt();
      this.c = var1.readInt();
      this.d = var1.readInt();
      this.e = var1.readByte();
      this.f = var1.readByte();
      this.g = var1.readBoolean();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.b(this.a);
      var1.writeInt(this.b);
      var1.writeInt(this.c);
      var1.writeInt(this.d);
      var1.writeByte(this.e);
      var1.writeByte(this.f);
      var1.writeBoolean(this.g);
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }
}
