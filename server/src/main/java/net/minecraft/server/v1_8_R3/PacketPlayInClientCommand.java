package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayInClientCommand implements Packet<PacketListenerPlayIn> {
   private PacketPlayInClientCommand.EnumClientCommand a;

   public PacketPlayInClientCommand() {
   }

   public PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand var1) {
      this.a = var1;
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.a(PacketPlayInClientCommand.EnumClientCommand.class);
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.a(this.a);
   }

   public void a(PacketListenerPlayIn var1) {
      var1.a(this);
   }

   public PacketPlayInClientCommand.EnumClientCommand a() {
      return this.a;
   }

   public static enum EnumClientCommand {
      PERFORM_RESPAWN,
      REQUEST_STATS,
      OPEN_INVENTORY_ACHIEVEMENT;
   }
}
