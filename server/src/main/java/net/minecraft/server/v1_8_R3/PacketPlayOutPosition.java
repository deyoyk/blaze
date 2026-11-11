package net.minecraft.server.v1_8_R3;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;

public class PacketPlayOutPosition implements Packet<PacketListenerPlayOut> {
   private double a;
   private double b;
   private double c;
   private float d;
   private float e;
   private Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> f;

   public double getX() {
      return this.a;
   }

   public double getY() {
      return this.b;
   }

   public double getZ() {
      return this.c;
   }

   public Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> getFlags() {
      return this.f;
   }

   public PacketPlayOutPosition() {
   }

   public PacketPlayOutPosition(double var1, double var3, double var5, float var7, float var8, Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> var9) {
      this.a = var1;
      this.b = var3;
      this.c = var5;
      this.d = var7;
      this.e = var8;
      this.f = var9;
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.readDouble();
      this.b = var1.readDouble();
      this.c = var1.readDouble();
      this.d = var1.readFloat();
      this.e = var1.readFloat();
      this.f = PacketPlayOutPosition.EnumPlayerTeleportFlags.a(var1.readUnsignedByte());
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.writeDouble(this.a);
      var1.writeDouble(this.b);
      var1.writeDouble(this.c);
      var1.writeFloat(this.d);
      var1.writeFloat(this.e);
      var1.writeByte(PacketPlayOutPosition.EnumPlayerTeleportFlags.a(this.f));
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }

   public static enum EnumPlayerTeleportFlags {
      X(0),
      Y(1),
      Z(2),
      Y_ROT(3),
      X_ROT(4);

      private final int f;

      private EnumPlayerTeleportFlags(int var3) {
         this.f = var3;
      }

      public static Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> a(int var0) {
         EnumSet<PacketPlayOutPosition.EnumPlayerTeleportFlags> var1 = EnumSet.noneOf(PacketPlayOutPosition.EnumPlayerTeleportFlags.class);

         PacketPlayOutPosition.EnumPlayerTeleportFlags[] var51;
         for (PacketPlayOutPosition.EnumPlayerTeleportFlags var5x : var51 = values()) {
            if (var5x.b(var0)) {
               var1.add(var5x);
            }
         }

         return var1;
      }

      public static int a(Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> var0) {
         int var1 = 0;

         for (PacketPlayOutPosition.EnumPlayerTeleportFlags var3 : var0) {
            var1 |= var3.a();
         }

         return var1;
      }

      private int a() {
         return 1 << this.f;
      }

      private boolean b(int var1) {
         return (var1 & this.a()) == this.a();
      }
   }
}
