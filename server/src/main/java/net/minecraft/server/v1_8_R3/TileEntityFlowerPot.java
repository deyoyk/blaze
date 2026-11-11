package net.minecraft.server.v1_8_R3;

public class TileEntityFlowerPot extends TileEntity {
   private Item a;
   private int f;

   public TileEntityFlowerPot() {
   }

   public TileEntityFlowerPot(Item var1, int var2) {
      this.a = var1;
      this.f = var2;
   }

   @Override
   public void b(NBTTagCompound var1) {
      super.b(var1);
      MinecraftKey var2 = Item.REGISTRY.c(this.a);
      var1.setString("Item", var2 == null ? "" : var2.toString());
      var1.setInt("Data", this.f);
   }

   @Override
   public void a(NBTTagCompound var1) {
      super.a(var1);
      if (var1.hasKeyOfType("Item", 8)) {
         this.a = Item.d(var1.getString("Item"));
      } else {
         this.a = Item.getById(var1.getInt("Item"));
      }

      this.f = var1.getInt("Data");
   }

   @Override
   public Packet getUpdatePacket() {
      NBTTagCompound var1 = new NBTTagCompound();
      this.b(var1);
      var1.remove("Item");
      var1.setInt("Item", Item.getId(this.a));
      return new PacketPlayOutTileEntityData(this.position, 5, var1);
   }

   public void a(Item var1, int var2) {
      this.a = var1;
      this.f = var2;
   }

   public Item b() {
      return this.a;
   }

   public int c() {
      return this.f;
   }
}
