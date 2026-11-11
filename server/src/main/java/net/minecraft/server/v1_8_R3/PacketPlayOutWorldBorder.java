package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutWorldBorder implements Packet<PacketListenerPlayOut> {
   private PacketPlayOutWorldBorder.EnumWorldBorderAction a;
   private int b;
   private double c;
   private double d;
   private double e;
   private double f;
   private long g;
   private int h;
   private int i;

   public PacketPlayOutWorldBorder() {
   }

   public PacketPlayOutWorldBorder(WorldBorder var1, PacketPlayOutWorldBorder.EnumWorldBorderAction var2) {
      this.a = var2;
      this.c = var1.getCenterX();
      this.d = var1.getCenterZ();
      this.f = var1.getSize();
      this.e = var1.j();
      this.g = var1.i();
      this.b = var1.l();
      this.i = var1.getWarningDistance();
      this.h = var1.getWarningTime();
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.a(PacketPlayOutWorldBorder.EnumWorldBorderAction.class);
      switch (this.a) {
         case SET_SIZE:
            this.e = var1.readDouble();
            break;
         case LERP_SIZE:
            this.f = var1.readDouble();
            this.e = var1.readDouble();
            this.g = var1.f();
            break;
         case SET_CENTER:
            this.c = var1.readDouble();
            this.d = var1.readDouble();
            break;
         case SET_WARNING_BLOCKS:
            this.i = var1.e();
            break;
         case SET_WARNING_TIME:
            this.h = var1.e();
            break;
         case INITIALIZE:
            this.c = var1.readDouble();
            this.d = var1.readDouble();
            this.f = var1.readDouble();
            this.e = var1.readDouble();
            this.g = var1.f();
            this.b = var1.e();
            this.i = var1.e();
            this.h = var1.e();
      }
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.a(this.a);
      switch (this.a) {
         case SET_SIZE:
            var1.writeDouble(this.e);
            break;
         case LERP_SIZE:
            var1.writeDouble(this.f);
            var1.writeDouble(this.e);
            var1.b(this.g);
            break;
         case SET_CENTER:
            var1.writeDouble(this.c);
            var1.writeDouble(this.d);
            break;
         case SET_WARNING_BLOCKS:
            var1.b(this.i);
            break;
         case SET_WARNING_TIME:
            var1.b(this.h);
            break;
         case INITIALIZE:
            var1.writeDouble(this.c);
            var1.writeDouble(this.d);
            var1.writeDouble(this.f);
            var1.writeDouble(this.e);
            var1.b(this.g);
            var1.b(this.b);
            var1.b(this.i);
            var1.b(this.h);
      }
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }

   public static enum EnumWorldBorderAction {
      SET_SIZE,
      LERP_SIZE,
      SET_CENTER,
      INITIALIZE,
      SET_WARNING_TIME,
      SET_WARNING_BLOCKS;
   }
}
