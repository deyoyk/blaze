package net.minecraft.server.v1_8_R3;

import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.event.server.MapInitializeEvent;

public class ItemMapEmpty extends ItemWorldMapBase {
   protected ItemMapEmpty() {
      this.a(CreativeModeTab.f);
   }

   @Override
   public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
      World worldMain = world.getServer().getServer().worlds.get(0);
      ItemStack itemstack1 = new ItemStack(Items.FILLED_MAP, 1, worldMain.b("map"));
      String s = "map_" + itemstack1.getData();
      WorldMap worldmap = new WorldMap(s);
      worldMain.a(s, worldmap);
      worldmap.scale = 0;
      worldmap.a(entityhuman.locX, entityhuman.locZ, worldmap.scale);
      worldmap.map = (byte)((WorldServer)world).dimension;
      worldmap.c();
      CraftEventFactory.callEvent(new MapInitializeEvent(worldmap.mapView));
      itemstack.count--;
      if (itemstack.count <= 0) {
         return itemstack1;
      } else {
         if (!entityhuman.inventory.pickup(itemstack1.cloneItemStack())) {
            entityhuman.drop(itemstack1, false);
         }

         return itemstack;
      }
   }
}
