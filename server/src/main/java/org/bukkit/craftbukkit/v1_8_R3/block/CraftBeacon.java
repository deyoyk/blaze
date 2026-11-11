package org.bukkit.craftbukkit.v1_8_R3.block;

import net.minecraft.server.v1_8_R3.TileEntityBeacon;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryBeacon;
import org.bukkit.inventory.Inventory;

public class CraftBeacon extends CraftBlockState implements Beacon {
   private final CraftWorld world;
   private final TileEntityBeacon beacon;

   public CraftBeacon(Block block) {
      super(block);
      this.world = (CraftWorld)block.getWorld();
      this.beacon = (TileEntityBeacon)this.world.getTileEntityAt(this.getX(), this.getY(), this.getZ());
   }

   public CraftBeacon(org.bukkit.Material material, TileEntityBeacon te) {
      super(material);
      this.world = null;
      this.beacon = te;
   }

   @Override
   public Inventory getInventory() {
      return new CraftInventoryBeacon(this.beacon);
   }

   @Override
   public boolean update(boolean force, boolean applyPhysics) {
      boolean result = super.update(force, applyPhysics);
      if (result) {
         this.beacon.update();
      }

      return result;
   }

   public TileEntityBeacon getTileEntity() {
      return this.beacon;
   }
}
