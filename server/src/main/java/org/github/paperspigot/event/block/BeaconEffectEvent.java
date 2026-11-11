package org.github.paperspigot.event.block;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.potion.PotionEffect;

public class BeaconEffectEvent extends BlockEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private boolean cancelled;
   private PotionEffect effect;
   private Player player;
   private boolean primary;

   public BeaconEffectEvent(Block block, PotionEffect effect, Player player, boolean primary) {
      super(block);
      this.effect = effect;
      this.player = player;
      this.primary = primary;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }

   public PotionEffect getEffect() {
      return this.effect;
   }

   public void setEffect(PotionEffect effect) {
      this.effect = effect;
   }

   public Player getPlayer() {
      return this.player;
   }

   public boolean isPrimary() {
      return this.primary;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
