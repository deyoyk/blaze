package net.minecraft.server.v1_8_R3;

public class TileEntityLightDetector extends TileEntity implements IUpdatePlayerListBox {
   @Override
   public void c() {
      if (this.world != null && this.world.getTime() % 20L == 0L) {
         this.e = this.w();
         if (this.e instanceof BlockDaylightDetector) {
            ((BlockDaylightDetector)this.e).f(this.world, this.position);
         }
      }
   }
}
