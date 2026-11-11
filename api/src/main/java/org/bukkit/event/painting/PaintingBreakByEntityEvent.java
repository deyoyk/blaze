package org.bukkit.event.painting;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Painting;

@Deprecated
@org.bukkit.Warning(
   reason = "This event has been replaced by HangingBreakByEntityEvent"
)
public class PaintingBreakByEntityEvent extends PaintingBreakEvent {
   private final Entity remover;

   public PaintingBreakByEntityEvent(Painting painting, Entity remover) {
      super(painting, PaintingBreakEvent.RemoveCause.ENTITY);
      this.remover = remover;
   }

   public Entity getRemover() {
      return this.remover;
   }
}
