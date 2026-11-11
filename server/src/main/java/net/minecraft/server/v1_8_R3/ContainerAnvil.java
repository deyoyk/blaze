package net.minecraft.server.v1_8_R3;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryAnvil;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareItemAnvilRepairEvent;

public class ContainerAnvil extends Container {
   private final IInventory g = new InventoryCraftResult();
   private final IInventory h = new InventorySubcontainer("Repair", true, 2) {
      @Override
      public void update() {
         super.update();
         ContainerAnvil.this.a(this);
      }
   };
   private final World i;
   private final BlockPosition j;
   public int a;
   private int k;
   private String l;
   private final EntityHuman m;
   private CraftInventoryView bukkitEntity = null;
   private final PlayerInventory player;

   public ContainerAnvil(PlayerInventory playerinventory, final World world, final BlockPosition blockposition, EntityHuman entityhuman) {
      this.player = playerinventory;
      this.j = blockposition;
      this.i = world;
      this.m = entityhuman;
      this.a(new Slot(this.h, 0, 27, 47));
      this.a(new Slot(this.h, 1, 76, 47));
      this.a(new Slot(this.g, 2, 134, 47) {
         @Override
         public boolean isAllowed(ItemStack itemstack) {
            return false;
         }

         @Override
         public boolean isAllowed(EntityHuman entityhuman) {
            return (entityhuman.abilities.canInstantlyBuild || entityhuman.expLevel >= ContainerAnvil.this.a) && ContainerAnvil.this.a > 0 && this.hasItem();
         }

         @Override
         public void a(EntityHuman entityhuman, ItemStack itemstack) {
            if (!entityhuman.abilities.canInstantlyBuild) {
               entityhuman.levelDown(-ContainerAnvil.this.a);
            }

            ContainerAnvil.this.h.setItem(0, null);
            if (ContainerAnvil.this.k > 0) {
               ItemStack itemstack1 = ContainerAnvil.this.h.getItem(1);
               if (itemstack1 != null && itemstack1.count > ContainerAnvil.this.k) {
                  itemstack1.count = itemstack1.count - ContainerAnvil.this.k;
                  ContainerAnvil.this.h.setItem(1, itemstack1);
               } else {
                  ContainerAnvil.this.h.setItem(1, null);
               }
            } else {
               ContainerAnvil.this.h.setItem(1, null);
            }

            ContainerAnvil.this.a = 0;
            IBlockData iblockdata = world.getType(blockposition);
            if (!entityhuman.abilities.canInstantlyBuild && iblockdata.getBlock() == Blocks.ANVIL && entityhuman.bc().nextFloat() < 0.12F) {
               int i = iblockdata.get(BlockAnvil.DAMAGE);
               if (++i > 2) {
                  world.setAir(blockposition);
                  world.triggerEffect(1020, blockposition, 0);
               } else {
                  world.setTypeAndData(blockposition, iblockdata.set(BlockAnvil.DAMAGE, i), 2);
                  world.triggerEffect(1021, blockposition, 0);
               }
            } else {
               world.triggerEffect(1021, blockposition, 0);
            }

            ((Player)ContainerAnvil.this.m.getBukkitEntity()).updateInventory();
         }
      });

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.a(new Slot(playerinventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for (int var7 = 0; var7 < 9; var7++) {
         this.a(new Slot(playerinventory, var7, 8 + var7 * 18, 142));
      }
   }

   @Override
   public void a(IInventory iinventory) {
      super.a(iinventory);
      if (iinventory == this.h) {
         this.e();
      }
   }

   public void e() {
      ItemStack item = this.h.getItem(0);
      this.a = 1;
      int i = 0;
      byte b0 = 0;
      byte b1 = 0;
      if (item == null) {
         this.g.setItem(0, null);
         this.a = 0;
      } else {
         ItemStack result = item.cloneItemStack();
         ItemStack second = this.h.getItem(1);
         Map<Integer, Integer> map = EnchantmentManager.a(result);
         int j = b0 + item.getRepairCost() + (second == null ? 0 : second.getRepairCost());
         this.k = 0;
         if (second != null) {
            boolean flag7 = second.getItem() == Items.ENCHANTED_BOOK && !Items.ENCHANTED_BOOK.h(second).isEmpty();
            if (result.e() && result.getItem().a(item, second)) {
               int k = Math.min(result.h(), result.j() / 4);
               if (k <= 0) {
                  this.g.setItem(0, null);
                  this.a = 0;
                  return;
               }

               int l;
               for (l = 0; k > 0 && l < second.count; l++) {
                  int i1 = result.h() - k;
                  result.setData(i1);
                  i++;
                  k = Math.min(result.h(), result.j() / 4);
               }

               this.k = l;
            } else {
               if (!flag7 && (result.getItem() != second.getItem() || !result.e())) {
                  this.g.setItem(0, null);
                  this.a = 0;
                  return;
               }

               if (result.e() && !flag7) {
                  int kx = item.j() - item.h();
                  int l = second.j() - second.h();
                  int i1 = l + result.j() * 12 / 100;
                  int k1 = kx + i1;
                  int j1 = result.j() - k1;
                  if (j1 < 0) {
                     j1 = 0;
                  }

                  if (j1 < result.getData()) {
                     result.setData(j1);
                     i += 2;
                  }
               }

               Map<Integer, Integer> map1 = EnchantmentManager.a(second);

               for (Integer i1x : map1.keySet()) {
                  Enchantment enchantment = Enchantment.getById(i1x);
                  if (enchantment != null) {
                     int j1x = map.getOrDefault(i1x, 0);
                     int l1 = map1.get(i1x);
                     int i2;
                     if (j1x == l1) {
                        i2 = ++l1;
                     } else {
                        i2 = Math.max(l1, j1x);
                     }

                     l1 = i2;
                     boolean flag8 = enchantment.canEnchant(item) && enchantment.getEnchantLimit() != 0;
                     if (this.m.abilities.canInstantlyBuild || item.getItem() == Items.ENCHANTED_BOOK) {
                        flag8 = true;
                     }

                     for (int j2 : map.keySet()) {
                        if (j2 != i1x && !enchantment.a(Enchantment.getById(j2))) {
                           flag8 = false;
                           i++;
                        }
                     }

                     if (flag8) {
                        int maxLevel = Math.min(enchantment.getMaxLevel(), enchantment.getEnchantLimit());
                        if (i2 > maxLevel) {
                           l1 = maxLevel;
                        }

                        map.put(i1x, l1);
                        int k2 = getK2(enchantment, flag7);
                        i += k2 * l1;
                     }
                  }
               }
            }
         }

         if (StringUtils.isBlank(this.l)) {
            if (item.hasName()) {
               b1 = 1;
               i += b1;
               result.r();
            }
         } else if (!this.l.equals(item.getName())) {
            b1 = 1;
            i += b1;
            result.c(this.l);
         }

         this.a = j + i;
         if (i <= 0) {
            result = null;
         }

         if (b1 == i && b1 > 0 && this.a >= 40) {
            this.a = 39;
         }

         if (this.a >= 40 && !this.m.abilities.canInstantlyBuild) {
            result = null;
         }

         if (result != null) {
            int kxx = result.getRepairCost();
            if (second != null && kxx < second.getRepairCost()) {
               kxx = second.getRepairCost();
            }

            kxx = kxx * 2 + 1;
            result.setRepairCost(kxx);
            EnchantmentManager.a(map, result);
            PrepareItemAnvilRepairEvent event = new PrepareItemAnvilRepairEvent(
               this.getBukkitView(),
               this.m.getBukkitEntity(),
               this.m.world.getWorld().getBlockAt(this.j.getX(), this.j.getY(), this.j.getZ()),
               this.a,
               CraftItemStack.asBukkitCopy(item),
               CraftItemStack.asBukkitCopy(second),
               CraftItemStack.asBukkitCopy(result)
            );
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled() || event.getResult().getType() == org.bukkit.Material.AIR) {
               return;
            }

            this.a = event.getRepairCost();
            result = CraftItemStack.asNMSCopy(event.getResult());
         }

         this.g.setItem(0, result);
         this.b();
      }
   }

   private static int getK2(Enchantment enchantment, boolean flag7) {
      int k2 = 0;
      switch (enchantment.getRandomWeight()) {
         case 1:
            k2 = 8;
            break;
         case 2:
            k2 = 4;
         case 3:
         case 4:
         case 6:
         case 7:
         case 8:
         case 9:
         default:
            break;
         case 5:
            k2 = 2;
            break;
         case 10:
            k2 = 1;
      }

      if (flag7) {
         k2 = Math.max(1, k2 / 2);
      }

      return k2;
   }

   @Override
   public void addSlotListener(ICrafting icrafting) {
      super.addSlotListener(icrafting);
      icrafting.setContainerData(this, 0, this.a);
   }

   @Override
   public void b(EntityHuman entityhuman) {
      super.b(entityhuman);

      for (int i = 0; i < this.h.getSize(); i++) {
         ItemStack itemstack = this.h.splitWithoutUpdate(i);
         if (itemstack != null) {
            entityhuman.drop(itemstack, false);
         }
      }
   }

   @Override
   public boolean a(EntityHuman entityhuman) {
      return !this.checkReachable
         ? true
         : this.i.getType(this.j).getBlock() == Blocks.ANVIL && entityhuman.e(this.j.getX() + 0.5, this.j.getY() + 0.5, this.j.getZ() + 0.5) <= 64.0;
   }

   @Override
   public ItemStack b(EntityHuman entityhuman, int i) {
      ItemStack itemstack = null;
      Slot slot = this.c.get(i);
      if (slot != null && slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.cloneItemStack();
         if (i == 2) {
            if (!this.a(itemstack1, 3, 39, true)) {
               return null;
            }

            slot.a(itemstack1, itemstack);
         } else if (i != 0 && i != 1) {
            if (i < 39 && !this.a(itemstack1, 0, 2, false)) {
               return null;
            }
         } else if (!this.a(itemstack1, 3, 39, false)) {
            return null;
         }

         if (itemstack1.count == 0) {
            slot.set(null);
         } else {
            slot.f();
         }

         if (itemstack1.count == itemstack.count) {
            return null;
         }

         slot.a(entityhuman, itemstack1);
      }

      return itemstack;
   }

   public void a(String s) {
      this.l = s;
      if (this.getSlot(2).hasItem()) {
         ItemStack itemstack = this.getSlot(2).getItem();
         if (StringUtils.isBlank(s)) {
            itemstack.r();
         } else {
            itemstack.c(this.l);
         }
      }

      this.e();
   }

   public CraftInventoryView getBukkitView() {
      if (this.bukkitEntity != null) {
         return this.bukkitEntity;
      } else {
         CraftInventory inventory = new CraftInventoryAnvil(this.h, this.g);
         this.bukkitEntity = new CraftInventoryView(this.player.player.getBukkitEntity(), inventory, this);
         return this.bukkitEntity;
      }
   }
}
