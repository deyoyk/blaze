package net.minecraft.server.v1_8_R3;

import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.event.hanging.HangingPlaceEvent;

public class ItemLeash extends Item {
   public ItemLeash() {
      this.a(CreativeModeTab.i);
   }

   public static boolean a(EntityHuman entityhuman, World world, BlockPosition blockposition) {
      EntityLeash entityleash = EntityLeash.b(world, blockposition);
      boolean flag = false;
      double d0 = 7.0;
      int i = blockposition.getX();
      int j = blockposition.getY();
      int k = blockposition.getZ();

      for (EntityInsentient entityinsentient : world.a(EntityInsentient.class, new AxisAlignedBB(i - d0, j - d0, k - d0, i + d0, j + d0, k + d0))) {
         if (entityinsentient.cc() && entityinsentient.getLeashHolder() == entityhuman) {
            if (entityleash == null) {
               entityleash = EntityLeash.a(world, blockposition);
               HangingPlaceEvent event = new HangingPlaceEvent(
                  (Hanging)entityleash.getBukkitEntity(),
                  entityhuman != null ? (Player)entityhuman.getBukkitEntity() : null,
                  world.getWorld().getBlockAt(i, j, k),
                  BlockFace.SELF
               );
               world.getServer().getPluginManager().callEvent(event);
               if (event.isCancelled()) {
                  entityleash.die();
                  return false;
               }
            }

            if (!CraftEventFactory.callPlayerLeashEntityEvent(entityinsentient, entityleash, entityhuman).isCancelled()) {
               entityinsentient.setLeashHolder(entityleash, true);
               flag = true;
            }
         }
      }

      return flag;
   }

   @Override
   public boolean interactWith(
      ItemStack itemstack, EntityHuman entityhuman, World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2
   ) {
      if (world.getType(blockposition).getBlock() instanceof BlockFence) {
         a(entityhuman, world, blockposition);
         return true;
      } else {
         return false;
      }
   }
}
