package org.bukkit.material;

import org.bukkit.block.BlockFace;

public interface Directional {
   void setFacingDirection(BlockFace var1);

   BlockFace getFacing();
}
