package net.minecraft.server.v1_8_R3;

import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;

public class ItemFishingRod extends Item {
   public ItemFishingRod() {
      this.setMaxDurability(64);
      this.c(1);
      this.a(CreativeModeTab.i);
   }

   @Override
   public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
      if (entityhuman.hookedFish != null) {
         int i = entityhuman.hookedFish.l();
         itemstack.damage(i, entityhuman);
      } else {
         EntityFishingHook hook = new EntityFishingHook(world, entityhuman);
         PlayerFishEvent playerFishEvent = new PlayerFishEvent((Player)entityhuman.getBukkitEntity(), null, (Fish)hook.getBukkitEntity(), State.FISHING);
         world.getServer().getPluginManager().callEvent(playerFishEvent);
         if (playerFishEvent.isCancelled()) {
            entityhuman.hookedFish = null;
            return itemstack;
         }

         world.makeSound(entityhuman, "random.bow", 0.5F, 0.4F / (g.nextFloat() * 0.4F + 0.8F));
         world.addEntity(hook);
      }

      entityhuman.bw();
      return itemstack;
   }

   @Override
   public boolean f_(ItemStack itemstack) {
      return super.f_(itemstack);
   }

   @Override
   public int b() {
      return 1;
   }
}
