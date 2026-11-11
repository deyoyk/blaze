package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.BlazeConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryEnchanting;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;

public class ContainerEnchantTable extends Container {
   public InventorySubcontainer enchantSlots = new InventorySubcontainer("Enchant", true, 2) {
      @Override
      public void update() {
         super.update();
         ContainerEnchantTable.this.a(this);
      }
   };
   private World world;
   private final BlockPosition position;
   private final Random k = new Random();
   public int f;
   public int[] costs = new int[3];
   public int[] h = new int[]{-1, -1, -1};
   private CraftInventoryView bukkitEntity = null;
   private final Player player;

   public ContainerEnchantTable(PlayerInventory playerinventory, World world, BlockPosition blockposition) {
      this.world = world;
      this.position = blockposition;
      if (!(Boolean)BlazeConfig.oldEnchantingSystem.getValue()) {
         this.f = playerinventory.player.cj();
      }

      this.a(new Slot(this.enchantSlots, 0, 15, 47) {
         @Override
         public boolean isAllowed(ItemStack itemstack) {
            return true;
         }

         @Override
         public int getMaxStackSize() {
            return 1;
         }
      });
      this.a(
         new Slot(this.enchantSlots, 1, 35, 47) {
            {
               if ((Boolean)BlazeConfig.oldEnchantingSystem.getValue()) {
                  this.set(new ItemStack(Items.DYE, 64, 4));
               }
            }

            @Override
            public boolean isAllowed(ItemStack itemstack) {
               return !(Boolean)BlazeConfig.oldEnchantingSystem.getValue()
                  && itemstack.getItem() == Items.DYE
                  && EnumColor.fromInvColorIndex(itemstack.getData()) == EnumColor.BLUE;
            }
         }
      );

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.a(new Slot(playerinventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for (int var6 = 0; var6 < 9; var6++) {
         this.a(new Slot(playerinventory, var6, 8 + var6 * 18, 142));
      }

      this.player = (Player)playerinventory.player.getBukkitEntity();
   }

   @Override
   public void addSlotListener(ICrafting icrafting) {
      super.addSlotListener(icrafting);
      icrafting.setContainerData(this, 0, this.costs[0]);
      icrafting.setContainerData(this, 1, this.costs[1]);
      icrafting.setContainerData(this, 2, this.costs[2]);
      if (!(Boolean)BlazeConfig.oldEnchantingSystem.getValue()) {
         icrafting.setContainerData(this, 3, this.f & -16);
         icrafting.setContainerData(this, 4, this.h[0]);
         icrafting.setContainerData(this, 5, this.h[1]);
         icrafting.setContainerData(this, 6, this.h[2]);
      }
   }

   @Override
   public void b() {
      super.b();

      for (ICrafting icrafting : this.listeners) {
         icrafting.setContainerData(this, 0, this.costs[0]);
         icrafting.setContainerData(this, 1, this.costs[1]);
         icrafting.setContainerData(this, 2, this.costs[2]);
         if (!(Boolean)BlazeConfig.oldEnchantingSystem.getValue()) {
            icrafting.setContainerData(this, 3, this.f & -16);
            icrafting.setContainerData(this, 4, this.h[0]);
            icrafting.setContainerData(this, 5, this.h[1]);
            icrafting.setContainerData(this, 6, this.h[2]);
         }
      }
   }

   @Override
   public void a(IInventory iinventory) {
      if (iinventory == this.enchantSlots) {
         ItemStack itemstack = iinventory.getItem(0);
         if (itemstack != null) {
            int i = 0;

            for (int j = -1; j <= 1; j++) {
               for (int k = -1; k <= 1; k++) {
                  if ((j != 0 || k != 0) && this.world.isEmpty(this.position.a(k, 0, j)) && this.world.isEmpty(this.position.a(k, 1, j))) {
                     if (this.world.getType(this.position.a(k * 2, 0, j * 2)).getBlock() == Blocks.BOOKSHELF) {
                        i++;
                     }

                     if (this.world.getType(this.position.a(k * 2, 1, j * 2)).getBlock() == Blocks.BOOKSHELF) {
                        i++;
                     }

                     if (k != 0 && j != 0) {
                        if (this.world.getType(this.position.a(k * 2, 0, j)).getBlock() == Blocks.BOOKSHELF) {
                           i++;
                        }

                        if (this.world.getType(this.position.a(k * 2, 1, j)).getBlock() == Blocks.BOOKSHELF) {
                           i++;
                        }

                        if (this.world.getType(this.position.a(k, 0, j * 2)).getBlock() == Blocks.BOOKSHELF) {
                           i++;
                        }

                        if (this.world.getType(this.position.a(k, 1, j * 2)).getBlock() == Blocks.BOOKSHELF) {
                           i++;
                        }
                     }
                  }
               }
            }

            if (!(Boolean)BlazeConfig.oldEnchantingSystem.getValue()) {
               this.k.setSeed(this.f);
            }

            for (int var11 = 0; var11 < 3; var11++) {
               this.costs[var11] = EnchantmentManager.a(this.k, var11, i, itemstack);
               this.h[var11] = -1;
               if (this.costs[var11] < var11 + 1) {
                  this.costs[var11] = 0;
               }
            }

            CraftItemStack item = CraftItemStack.asCraftMirror(itemstack);
            PrepareItemEnchantEvent event = new PrepareItemEnchantEvent(
               this.player,
               this.getBukkitView(),
               this.world.getWorld().getBlockAt(this.position.getX(), this.position.getY(), this.position.getZ()),
               item,
               this.costs,
               i
            );
            event.setCancelled(!itemstack.v());
            this.world.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
               for (int var9 = 0; var9 < 3; var9++) {
                  this.costs[var9] = 0;
               }

               return;
            }

            for (int var12 = 0; var12 < 3; var12++) {
               if (this.costs[var12] > 0) {
                  List<WeightedRandomEnchant> list = this.a(itemstack, var12, this.costs[var12]);
                  if (list != null && !list.isEmpty()) {
                     WeightedRandomEnchant weightedrandomenchant = list.get(this.k.nextInt(list.size()));
                     this.h[var12] = weightedrandomenchant.enchantment.id | weightedrandomenchant.level << 8;
                  }
               }
            }

            this.b();
         } else {
            for (int i = 0; i < 3; i++) {
               this.costs[i] = 0;
               this.h[i] = -1;
            }
         }
      }
   }

   @Override
   public boolean a(EntityHuman entityhuman, int i) {
      ItemStack itemstack = this.enchantSlots.getItem(0);
      ItemStack itemstack1 = this.enchantSlots.getItem(1);
      int j = i + 1;
      int cost = this.costs[i];
      if (!(Boolean)BlazeConfig.oldEnchantingSystem.getValue() && (itemstack1 == null || itemstack1.count < j) && !entityhuman.abilities.canInstantlyBuild) {
         return false;
      } else if (cost > 0 && itemstack != null && (entityhuman.expLevel >= j && entityhuman.expLevel >= cost || entityhuman.abilities.canInstantlyBuild)) {
         List<WeightedRandomEnchant> list = this.a(itemstack, i, cost);
         if (list == null) {
            list = new ArrayList<>();
         }

         boolean flag = itemstack.getItem() == Items.BOOK;
         Map<org.bukkit.enchantments.Enchantment, Integer> enchants = new HashMap<>();

         for (WeightedRandomEnchant instance : list) {
            enchants.put(org.bukkit.enchantments.Enchantment.getById(instance.enchantment.id), instance.level);
         }

         CraftItemStack item = CraftItemStack.asCraftMirror(itemstack);
         EnchantItemEvent event = new EnchantItemEvent(
            (Player)entityhuman.getBukkitEntity(),
            this.getBukkitView(),
            this.world.getWorld().getBlockAt(this.position.getX(), this.position.getY(), this.position.getZ()),
            item,
            this.costs[i],
            enchants,
            i
         );
         this.world.getServer().getPluginManager().callEvent(event);
         int level = event.getExpLevelCost();
         if (!event.isCancelled() && (level <= entityhuman.expLevel || entityhuman.abilities.canInstantlyBuild) && !event.getEnchantsToAdd().isEmpty()) {
            if (flag) {
               itemstack.setItem(Items.ENCHANTED_BOOK);
            }

            for (Entry<org.bukkit.enchantments.Enchantment, Integer> entry : event.getEnchantsToAdd().entrySet()) {
               try {
                  if (flag) {
                     int enchantId = entry.getKey().getId();
                     Enchantment ench = Enchantment.getById(enchantId);
                     if (ench != null) {
                        WeightedRandomEnchant enchantment = new WeightedRandomEnchant(ench, entry.getValue());
                        Items.ENCHANTED_BOOK.a(itemstack, enchantment);
                     }
                  } else {
                     item.addUnsafeEnchantment(entry.getKey(), entry.getValue());
                  }
               } catch (IllegalArgumentException var18) {
               }
            }

            if (!(Boolean)BlazeConfig.oldEnchantingSystem.getValue()) {
               entityhuman.enchantDone(j);
               if (!entityhuman.abilities.canInstantlyBuild) {
                  itemstack1.count -= j;
                  if (itemstack1.count <= 0) {
                     this.enchantSlots.setItem(1, null);
                  }
               }
            } else {
               entityhuman.levelDown(-level);
            }

            this.enchantSlots.update();
            if (!(Boolean)BlazeConfig.oldEnchantingSystem.getValue()) {
               this.f = entityhuman.cj();
            }

            this.a(this.enchantSlots);
            this.player.updateInventory();
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private List<WeightedRandomEnchant> a(ItemStack itemstack, int i, int j) {
      if (!(Boolean)BlazeConfig.oldEnchantingSystem.getValue()) {
         this.k.setSeed(this.f + i);
      }

      List<WeightedRandomEnchant> list = EnchantmentManager.b(this.k, itemstack, j);
      if (itemstack.getItem() == Items.BOOK && list != null && list.size() > 1) {
         list.remove(this.k.nextInt(list.size()));
      }

      return list;
   }

   @Override
   public void b(EntityHuman entityhuman) {
      super.b(entityhuman);
      if (this.world == null) {
         this.world = entityhuman.getWorld();
      }

      if (!(Boolean)BlazeConfig.oldEnchantingSystem.getValue()) {
         for (int i = 0; i < this.enchantSlots.getSize(); i++) {
            ItemStack itemstack = this.enchantSlots.splitWithoutUpdate(i);
            if (itemstack != null) {
               entityhuman.drop(itemstack, false);
            }
         }
      } else {
         ItemStack itemstack = this.enchantSlots.splitWithoutUpdate(0);
         if (itemstack != null) {
            entityhuman.drop(itemstack, false);
         }

         this.enchantSlots.setItem(1, null);
      }
   }

   @Override
   public boolean a(EntityHuman entityhuman) {
      return !this.checkReachable
         ? true
         : this.world.getType(this.position).getBlock() == Blocks.ENCHANTING_TABLE
            && entityhuman.e(this.position.getX() + 0.5, this.position.getY() + 0.5, this.position.getZ() + 0.5) <= 64.0;
   }

   @Override
   public ItemStack b(EntityHuman entityhuman, int i) {
      ItemStack itemstack = null;
      Slot slot = this.c.get(i);
      if (slot != null && slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.cloneItemStack();
         if (i == 0) {
            if (!this.a(itemstack1, 2, 38, true)) {
               return null;
            }
         } else if (i == 1) {
            if (!this.a(itemstack1, 2, 38, true)) {
               return null;
            }
         } else if (itemstack1.getItem() == Items.DYE && EnumColor.fromInvColorIndex(itemstack1.getData()) == EnumColor.BLUE) {
            if (!this.a(itemstack1, 1, 2, true)) {
               return null;
            }
         } else {
            Slot item = this.c.get(0);
            if (item.hasItem() || !item.isAllowed(itemstack1)) {
               return null;
            }

            if (itemstack1.hasTag() && itemstack1.count == 1) {
               item.set(itemstack1.cloneItemStack());
               itemstack1.count = 0;
            } else if (itemstack1.count >= 1) {
               ItemStack clone = itemstack1.cloneItemStack();
               clone.count = 1;
               item.set(clone);
               itemstack1.count--;
            }
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

   public CraftInventoryView getBukkitView() {
      if (this.bukkitEntity != null) {
         return this.bukkitEntity;
      } else {
         CraftInventoryEnchanting inventory = new CraftInventoryEnchanting(this.enchantSlots);
         this.bukkitEntity = new CraftInventoryView(this.player, inventory, this);
         return this.bukkitEntity;
      }
   }
}
