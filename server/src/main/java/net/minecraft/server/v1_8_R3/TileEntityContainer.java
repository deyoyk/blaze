package net.minecraft.server.v1_8_R3;

public abstract class TileEntityContainer extends TileEntity implements ITileEntityContainer, ITileInventory {
   private ChestLock a;

   public TileEntityContainer() {
      this.a = ChestLock.a;
   }

   @Override
   public void a(NBTTagCompound var1) {
      super.a(var1);
      this.a = ChestLock.b(var1);
   }

   @Override
   public void b(NBTTagCompound var1) {
      super.b(var1);
      if (this.a != null) {
         this.a.a(var1);
      }
   }

   @Override
   public boolean r_() {
      return this.a != null && !this.a.a();
   }

   @Override
   public ChestLock i() {
      return this.a;
   }

   @Override
   public void a(ChestLock var1) {
      this.a = var1;
   }

   @Override
   public IChatBaseComponent getScoreboardDisplayName() {
      return (IChatBaseComponent)(this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatMessage(this.getName()));
   }
}
