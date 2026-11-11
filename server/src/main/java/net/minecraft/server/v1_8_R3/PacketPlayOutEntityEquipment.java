package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public class PacketPlayOutEntityEquipment implements Packet<PacketListenerPlayOut> {
   private int a;
   private int b;
   private ItemStack c;

   public PacketPlayOutEntityEquipment() {
   }

   public PacketPlayOutEntityEquipment(int var1, int var2, ItemStack var3) {
      this.a = var1;
      this.b = var2;
      this.c = var3 == null ? null : var3.cloneItemStack();
   }

   public PacketPlayOutEntityEquipment(int var1, int var2, ItemStack var3, boolean obfuscate) {
      this.a = var1;
      this.b = var2;
      this.c = var3 == null ? null : var3.cloneItemStack();
      if (obfuscate && this.c != null) {
         boolean hadEnchants = this.c.hasEnchantments();
         if (this.c.e()) {
            this.c.setData(this.c.j());
         }

         this.c.r();
         if (this.c.getTag() == null) {
            this.c.setTag(new NBTTagCompound());
         }

         if (hadEnchants) {
            NBTTagList obfuscatedEnchantments = new NBTTagList();
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setShort("id", (short)0);
            nbttagcompound.setShort("lvl", (short)0);
            obfuscatedEnchantments.add(nbttagcompound);
            this.c.getTag().set("ench", obfuscatedEnchantments);
         }

         this.c.count = 1;
      }
   }

   @Override
   public void a(PacketDataSerializer var1) throws IOException {
      this.a = var1.e();
      this.b = var1.readShort();
      this.c = var1.i();
   }

   @Override
   public void b(PacketDataSerializer var1) throws IOException {
      var1.b(this.a);
      var1.writeShort(this.b);
      var1.a(this.c);
   }

   public void a(PacketListenerPlayOut var1) {
      var1.a(this);
   }
}
