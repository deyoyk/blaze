package org.bukkit.craftbukkit.v1_8_R3.entity;

import java.util.Random;
import net.minecraft.server.v1_8_R3.EntityFireworks;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.Items;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class CraftFirework extends CraftEntity implements Firework {
   private static final int FIREWORK_ITEM_INDEX = 8;
   private final Random random = new Random();
   private final CraftItemStack item;

   public CraftFirework(CraftServer server, EntityFireworks entity) {
      super(server, entity);
      ItemStack item = this.getHandle().getDataWatcher().getItemStack(8);
      if (item == null) {
         item = new ItemStack(Items.FIREWORKS);
         this.getHandle().getDataWatcher().watch(8, item);
      }

      this.item = CraftItemStack.asCraftMirror(item);
      if (this.item.getType() != org.bukkit.Material.FIREWORK) {
         this.item.setType(org.bukkit.Material.FIREWORK);
      }
   }

   public EntityFireworks getHandle() {
      return (EntityFireworks)this.entity;
   }

   @Override
   public String toString() {
      return "CraftFirework";
   }

   @Override
   public EntityType getType() {
      return EntityType.FIREWORK;
   }

   @Override
   public FireworkMeta getFireworkMeta() {
      return (FireworkMeta)this.item.getItemMeta();
   }

   @Override
   public void setFireworkMeta(FireworkMeta meta) {
      this.item.setItemMeta(meta);
      this.getHandle().expectedLifespan = 10 * (1 + meta.getPower()) + this.random.nextInt(6) + this.random.nextInt(7);
      this.getHandle().getDataWatcher().update(8);
   }

   @Override
   public void detonate() {
      this.getHandle().expectedLifespan = 0;
   }
}
