package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayInUpdateSign implements Packet<PacketListenerPlayIn> {
   private BlockPosition a;
   private IChatBaseComponent[] b;

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.c();
      this.b = new IChatBaseComponent[4];

      for (int var2 = 0; var2 < 4; var2++) {
         String var3 = var1.c(384);
         IChatBaseComponent var4 = IChatBaseComponent.ChatSerializer.a(var3);
         this.b[var2] = var4;
      }
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.a(this.a);

      for (int var2 = 0; var2 < 4; var2++) {
         IChatBaseComponent var3 = this.b[var2];
         String var4 = IChatBaseComponent.ChatSerializer.a(var3);
         var1.a(var4);
      }
   }

   public void a(PacketListenerPlayIn var1) {
      var1.a(this);
   }

   public BlockPosition a() {
      return this.a;
   }

   public IChatBaseComponent[] b() {
      return this.b;
   }
}
