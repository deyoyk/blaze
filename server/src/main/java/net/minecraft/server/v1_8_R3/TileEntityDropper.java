package net.minecraft.server.v1_8_R3;

public class TileEntityDropper extends TileEntityDispenser {
   @Override
   public String getName() {
      return this.hasCustomName() ? this.a : "container.dropper";
   }

   @Override
   public String getContainerName() {
      return "minecraft:dropper";
   }
}
