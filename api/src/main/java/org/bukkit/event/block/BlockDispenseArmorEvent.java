package org.bukkit.event.block;

import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class BlockDispenseArmorEvent extends BlockDispenseEvent {
   private final LivingEntity target;

   public BlockDispenseArmorEvent(@NotNull Block block, @NotNull ItemStack dispensed, @NotNull LivingEntity target) {
      super(block, dispensed, new Vector(0, 0, 0));
      this.target = target;
   }

   @NotNull
   public LivingEntity getTargetEntity() {
      return this.target;
   }
}
