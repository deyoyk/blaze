package net.minecraft.server.v1_8_R3;

import org.bukkit.Bukkit;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.PrePearlLaunchEvent;

public class ItemEnderPearl extends Item {
   public ItemEnderPearl() {
      this.maxStackSize = 16;
      this.a(CreativeModeTab.f);
   }

   @Override
   public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
      if (entityhuman.abilities.canInstantlyBuild) {
         return itemstack;
      } else {
         Entity en = new EntityEnderPearl(world, entityhuman);
         if (PrePearlLaunchEvent.getHandlerList().getRegisteredListeners().length != 0) {
            Projectile projectile = (Projectile)en.getBukkitEntity();
            PrePearlLaunchEvent event = new PrePearlLaunchEvent(projectile);
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
               return itemstack;
            }
         }

         itemstack.count--;
         world.makeSound(entityhuman, "random.bow", 0.5F, 0.4F / (g.nextFloat() * 0.4F + 0.8F));
         world.addEntity(en);
         return itemstack;
      }
   }
}
