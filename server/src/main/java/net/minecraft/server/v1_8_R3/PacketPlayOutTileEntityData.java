package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutTileEntityData implements Packet<PacketListenerPlayOut> {
   private BlockPosition a;
   private int b;
   private NBTTagCompound c;

   public PacketPlayOutTileEntityData() {
   }

   public PacketPlayOutTileEntityData(BlockPosition var1, int var2, NBTTagCompound var3) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.c();
      this.b = var1.readUnsignedByte();
      this.c = var1.h();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.a(this.a);
      var1.writeByte((byte)this.b);
      var1.a(this.c);
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }
}
