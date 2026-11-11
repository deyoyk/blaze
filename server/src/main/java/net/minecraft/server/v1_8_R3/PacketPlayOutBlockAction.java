package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutBlockAction implements Packet<PacketListenerPlayOut> {
   private BlockPosition a;
   private int b;
   private int c;
   private Block d;

   public PacketPlayOutBlockAction() {
   }

   public PacketPlayOutBlockAction(BlockPosition var1, Block var2, int var3, int var4) {
      this.a = var1;
      this.b = var3;
      this.c = var4;
      this.d = var2;
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.c();
      this.b = var1.readUnsignedByte();
      this.c = var1.readUnsignedByte();
      this.d = Block.getById(var1.e() & 4095);
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.a(this.a);
      var1.writeByte(this.b);
      var1.writeByte(this.c);
      var1.b(Block.getId(this.d) & 4095);
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }
}
