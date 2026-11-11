package org.bukkit.event.world;

import java.util.List;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class StructureGrowEvent extends WorldEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private boolean cancelled = false;
   private final org.bukkit.Location location;
   private final org.bukkit.TreeType species;
   private final boolean bonemeal;
   private final Player player;
   private final List<BlockState> blocks;

   public StructureGrowEvent(org.bukkit.Location location, org.bukkit.TreeType species, boolean bonemeal, Player player, List<BlockState> blocks) {
      super(location.getWorld());
      this.location = location;
      this.species = species;
      this.bonemeal = bonemeal;
      this.player = player;
      this.blocks = blocks;
   }

   public org.bukkit.Location getLocation() {
      return this.location;
   }

   public org.bukkit.TreeType getSpecies() {
      return this.species;
   }

   public boolean isFromBonemeal() {
      return this.bonemeal;
   }

   public Player getPlayer() {
      return this.player;
   }

   public List<BlockState> getBlocks() {
      return this.blocks;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void setCancelled(boolean cancel) {
      this.cancelled = cancel;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
