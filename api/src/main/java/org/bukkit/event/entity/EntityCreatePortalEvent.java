package org.bukkit.event.entity;

import java.util.List;
import org.bukkit.block.BlockState;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class EntityCreatePortalEvent extends EntityEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private final List<BlockState> blocks;
   private boolean cancelled = false;
   private org.bukkit.PortalType type = org.bukkit.PortalType.CUSTOM;

   public EntityCreatePortalEvent(LivingEntity what, List<BlockState> blocks, org.bukkit.PortalType type) {
      super(what);
      this.blocks = blocks;
      this.type = type;
   }

   public LivingEntity getEntity() {
      return (LivingEntity)this.entity;
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

   public org.bukkit.PortalType getPortalType() {
      return this.type;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
