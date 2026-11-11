package org.bukkit.event.player;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PlayerFishEvent extends PlayerEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private final Entity entity;
   private boolean cancel = false;
   private int exp;
   private final PlayerFishEvent.State state;
   private final Fish hookEntity;

   @Deprecated
   public PlayerFishEvent(Player player, Entity entity, PlayerFishEvent.State state) {
      this(player, entity, null, state);
   }

   public PlayerFishEvent(Player player, Entity entity, Fish hookEntity, PlayerFishEvent.State state) {
      super(player);
      this.entity = entity;
      this.hookEntity = hookEntity;
      this.state = state;
   }

   public Entity getCaught() {
      return this.entity;
   }

   public Fish getHook() {
      return this.hookEntity;
   }

   @Override
   public boolean isCancelled() {
      return this.cancel;
   }

   @Override
   public void setCancelled(boolean cancel) {
      this.cancel = cancel;
   }

   public int getExpToDrop() {
      return this.exp;
   }

   public void setExpToDrop(int amount) {
      this.exp = amount;
   }

   public PlayerFishEvent.State getState() {
      return this.state;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public static enum State {
      FISHING,
      CAUGHT_FISH,
      CAUGHT_ENTITY,
      IN_GROUND,
      FAILED_ATTEMPT;
   }
}
