package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutCombatEvent implements Packet<PacketListenerPlayOut> {
   public PacketPlayOutCombatEvent.EnumCombatEventType a;
   public int b;
   public int c;
   public int d;
   public String e;

   public PacketPlayOutCombatEvent() {
   }

   public PacketPlayOutCombatEvent(CombatTracker var1, PacketPlayOutCombatEvent.EnumCombatEventType var2) {
      this.a = var2;
      EntityLiving var3 = var1.c();
      switch (var2) {
         case END_COMBAT:
            this.d = var1.f();
            this.c = var3 == null ? -1 : var3.getId();
            break;
         case ENTITY_DIED:
            this.b = var1.h().getId();
            this.c = var3 == null ? -1 : var3.getId();
            this.e = var1.b().c();
      }
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.a(PacketPlayOutCombatEvent.EnumCombatEventType.class);
      if (this.a == PacketPlayOutCombatEvent.EnumCombatEventType.END_COMBAT) {
         this.d = var1.e();
         this.c = var1.readInt();
      } else if (this.a == PacketPlayOutCombatEvent.EnumCombatEventType.ENTITY_DIED) {
         this.b = var1.e();
         this.c = var1.readInt();
         this.e = var1.c(32767);
      }
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.a(this.a);
      if (this.a == PacketPlayOutCombatEvent.EnumCombatEventType.END_COMBAT) {
         var1.b(this.d);
         var1.writeInt(this.c);
      } else if (this.a == PacketPlayOutCombatEvent.EnumCombatEventType.ENTITY_DIED) {
         var1.b(this.b);
         var1.writeInt(this.c);
         var1.a(this.e);
      }
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }

   public static enum EnumCombatEventType {
      ENTER_COMBAT,
      END_COMBAT,
      ENTITY_DIED;
   }
}
