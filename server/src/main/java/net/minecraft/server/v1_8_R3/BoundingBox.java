package net.minecraft.server.v1_8_R3;

import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.util.Vector;

public class BoundingBox {
   private final Vector max;
   private final Vector min;

   public BoundingBox(org.bukkit.block.Block block) {
      IBlockData blockData = ((CraftWorld)block.getWorld()).getHandle().getType(block.getX(), block.getY(), block.getZ());
      Block blockNative = blockData.getBlock();
      this.min = new Vector(block.getX() + blockNative.B(), block.getY() + blockNative.D(), block.getZ() + blockNative.F());
      this.max = new Vector(block.getX() + blockNative.C(), block.getY() + blockNative.E(), block.getZ() + blockNative.G());
   }

   public Vector getMax() {
      return this.max;
   }

   public Vector getMin() {
      return this.min;
   }
}
