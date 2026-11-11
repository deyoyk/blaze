package org.bukkit.event.inventory;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockExpEvent;

public class FurnaceExtractEvent extends BlockExpEvent {
   private final Player player;
   private final org.bukkit.Material itemType;
   private final int itemAmount;

   public FurnaceExtractEvent(Player player, Block block, org.bukkit.Material itemType, int itemAmount, int exp) {
      super(block, exp);
      this.player = player;
      this.itemType = itemType;
      this.itemAmount = itemAmount;
   }

   public Player getPlayer() {
      return this.player;
   }

   public org.bukkit.Material getItemType() {
      return this.itemType;
   }

   public int getItemAmount() {
      return this.itemAmount;
   }
}
