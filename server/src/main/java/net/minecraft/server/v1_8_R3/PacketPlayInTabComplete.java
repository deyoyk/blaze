package net.minecraft.server.v1_8_R3;

import java.io.IOException;
import org.apache.commons.lang3.StringUtils;

public class PacketPlayInTabComplete implements Packet<PacketListenerPlayIn> {
   private String a;
   private BlockPosition b;

   public PacketPlayInTabComplete() {
   }

   public PacketPlayInTabComplete(String var1) {
      this(var1, null);
   }

   public PacketPlayInTabComplete(String var1, BlockPosition var2) {
      this.a = var1;
      this.b = var2;
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.c(32767);
      boolean var2 = var1.readBoolean();
      if (var2) {
         this.b = var1.c();
      }
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.a(StringUtils.substring(this.a, 0, 32767));
      boolean var2 = this.b != null;
      var1.writeBoolean(var2);
      if (var2) {
         var1.a(this.b);
      }
   }

   public void a(PacketListenerPlayIn var1) {
      var1.a(this);
   }

   public String a() {
      return this.a;
   }

   public BlockPosition b() {
      return this.b;
   }
}
