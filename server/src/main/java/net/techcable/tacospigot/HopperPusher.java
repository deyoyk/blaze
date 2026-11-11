package net.techcable.tacospigot;

import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.TileEntityHopper;
import net.minecraft.server.v1_8_R3.World;

public interface HopperPusher {
   default TileEntityHopper findHopper() {
      int y1 = MathHelper.floor(this.getY());

      for (int y = y1; y > y1 - 2; y--) {
         TileEntityHopper hopper = HopperHelper.getHopper(this.getWorld(), new BlockPosition(this.getX(), y, this.getZ()));
         if (hopper != null && y1 - y < 2) {
            return hopper;
         }
      }

      return null;
   }

   boolean acceptItem(TileEntityHopper var1);

   default boolean tryPutInHopper() {
      TileEntityHopper hopper = this.findHopper();
      return hopper != null && hopper.canAcceptItems() && this.acceptItem(hopper);
   }

   AxisAlignedBB getBoundingBox();

   World getWorld();

   default double getX() {
      return ((Entity)this).locX;
   }

   default double getY() {
      return ((Entity)this).locY;
   }

   default double getZ() {
      return ((Entity)this).locZ;
   }
}
