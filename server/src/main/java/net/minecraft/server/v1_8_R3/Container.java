package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;

public abstract class Container {
   public List<ItemStack> b = Lists.newArrayList();
   public List<Slot> c = Lists.newArrayList();
   public int windowId;
   private int dragType = -1;
   private int g;
   private final Set<Slot> h = new ArraySet<>();
   protected List<ICrafting> listeners = Lists.newArrayList();
   private final Set<EntityHuman> i = new ArraySet<>();
   private int tickCount;
   public boolean checkReachable = true;

   public abstract InventoryView getBukkitView();

   public void transferTo(Container other, CraftHumanEntity player) {
      InventoryView source = this.getBukkitView();
      InventoryView destination = other.getBukkitView();
      ((CraftInventory)source.getTopInventory()).getInventory().onClose(player);
      ((CraftInventory)source.getBottomInventory()).getInventory().onClose(player);
      ((CraftInventory)destination.getTopInventory()).getInventory().onOpen(player);
      ((CraftInventory)destination.getBottomInventory()).getInventory().onOpen(player);
   }

   protected Slot a(Slot slot) {
      slot.rawSlotIndex = this.c.size();
      this.c.add(slot);
      this.b.add(null);
      return slot;
   }

   public void addSlotListener(ICrafting icrafting) {
      if (this.listeners.contains(icrafting)) {
         throw new IllegalArgumentException("Listener already listening");
      } else {
         this.listeners.add(icrafting);
         icrafting.a(this, this.a());
         this.b();
      }
   }

   public List<ItemStack> a() {
      ArrayList<ItemStack> arraylist = Lists.newArrayList();

      for (Slot slot : this.c) {
         arraylist.add(slot.getItem());
      }

      return arraylist;
   }

   public void b() {
      for (int i = 0; i < this.c.size(); i++) {
         ItemStack itemstack = this.c.get(i).getItem();
         ItemStack itemstack1 = this.b.get(i);
         if (!ItemStack.fastMatches(itemstack1, itemstack) || this.tickCount % 20 == 0 && !ItemStack.matches(itemstack1, itemstack)) {
            itemstack1 = itemstack == null ? null : itemstack.cloneItemStack();
            this.b.set(i, itemstack1);

            for (ICrafting listener : this.listeners) {
               listener.a(this, i, itemstack1);
            }
         }
      }

      this.tickCount++;
   }

   public boolean a(EntityHuman entityhuman, int i) {
      return false;
   }

   public Slot getSlot(IInventory iinventory, int i) {
      for (Slot slot : this.c) {
         if (slot.a(iinventory, i)) {
            return slot;
         }
      }

      return null;
   }

   public Slot getSlot(int i) {
      int lastIndex = this.c.size() - 1;
      if (i > lastIndex) {
         i = lastIndex;
      }

      return this.c.get(i);
   }

   public ItemStack b(EntityHuman entityhuman, int i) {
      Slot slot = this.c.get(i);
      return slot != null ? slot.getItem() : null;
   }

   public ItemStack clickItem(int i, int j, int k, EntityHuman entityhuman) {
      ItemStack itemstack = null;
      PlayerInventory playerinventory = entityhuman.inventory;
      if (k == 5) {
         int i1 = this.g;
         this.g = c(j);
         if ((i1 != 1 || this.g != 2) && i1 != this.g) {
            this.d();
         } else if (playerinventory.getCarried() == null) {
            this.d();
         } else if (this.g == 0) {
            this.dragType = b(j);
            if (a(this.dragType, entityhuman)) {
               this.g = 1;
               this.h.clear();
            } else {
               this.d();
            }
         } else if (this.g == 1) {
            Slot slot = i < this.c.size() ? this.c.get(i) : null;
            if (slot != null
               && a(slot, playerinventory.getCarried(), true)
               && slot.isAllowed(playerinventory.getCarried())
               && playerinventory.getCarried().count > this.h.size()
               && this.b(slot)) {
               this.h.add(slot);
            }
         } else if (this.g == 2) {
            if (!this.h.isEmpty()) {
               ItemStack itemstack1 = playerinventory.getCarried().cloneItemStack();
               int l = playerinventory.getCarried().count;
               Iterator<Slot> iterator = this.h.iterator();
               Map<Integer, ItemStack> draggedSlots = new HashMap<>();

               while (iterator.hasNext()) {
                  Slot slot1 = iterator.next();
                  if (slot1 != null
                     && a(slot1, playerinventory.getCarried(), true)
                     && slot1.isAllowed(playerinventory.getCarried())
                     && playerinventory.getCarried().count >= this.h.size()
                     && this.b(slot1)) {
                     ItemStack itemstack2 = itemstack1.cloneItemStack();
                     int j1 = slot1.hasItem() ? slot1.getItem().count : 0;
                     a(this.h, this.dragType, itemstack2, j1);
                     if (itemstack2.count > itemstack2.getMaxStackSize()) {
                        itemstack2.count = itemstack2.getMaxStackSize();
                     }

                     if (itemstack2.count > slot1.getMaxStackSize(itemstack2)) {
                        itemstack2.count = slot1.getMaxStackSize(itemstack2);
                     }

                     l -= itemstack2.count - j1;
                     draggedSlots.put(slot1.rawSlotIndex, itemstack2);
                  }
               }

               InventoryView view = this.getBukkitView();
               org.bukkit.inventory.ItemStack newcursor = CraftItemStack.asCraftMirror(itemstack1);
               newcursor.setAmount(l);
               Map<Integer, org.bukkit.inventory.ItemStack> eventmap = new HashMap<>();

               for (Entry<Integer, ItemStack> ditem : draggedSlots.entrySet()) {
                  eventmap.put(ditem.getKey(), CraftItemStack.asBukkitCopy(ditem.getValue()));
               }

               ItemStack oldCursor = playerinventory.getCarried();
               playerinventory.setCarried(CraftItemStack.asNMSCopy(newcursor));
               InventoryDragEvent event = new InventoryDragEvent(
                  view, newcursor.getType() != org.bukkit.Material.AIR ? newcursor : null, CraftItemStack.asBukkitCopy(oldCursor), this.dragType == 1, eventmap
               );
               entityhuman.world.getServer().getPluginManager().callEvent(event);
               boolean needsUpdate = event.getResult() != Result.DEFAULT;
               if (event.getResult() != Result.DENY) {
                  for (Entry<Integer, ItemStack> dslot : draggedSlots.entrySet()) {
                     view.setItem(dslot.getKey(), CraftItemStack.asBukkitCopy(dslot.getValue()));
                  }

                  if (playerinventory.getCarried() != null) {
                     playerinventory.setCarried(CraftItemStack.asNMSCopy(event.getCursor()));
                     needsUpdate = true;
                  }
               } else {
                  playerinventory.setCarried(oldCursor);
               }

               if (needsUpdate) {
                  ((EntityPlayer)entityhuman).updateInventory(this);
               }
            }

            this.d();
         } else {
            this.d();
         }
      } else if (this.g != 0) {
         this.d();
      } else if ((k == 0 || k == 1) && (j == 0 || j == 1)) {
         if (i == -999) {
            if (playerinventory.getCarried() != null) {
               if (j == 0) {
                  entityhuman.drop(playerinventory.getCarried(), true);
                  playerinventory.setCarried(null);
               } else {
                  ItemStack itemstack4 = playerinventory.getCarried();
                  if (itemstack4.count > 0) {
                     entityhuman.drop(itemstack4.cloneAndSubtract(1), true);
                  }

                  if (itemstack4.count == 0) {
                     playerinventory.setCarried(null);
                  }
               }
            }
         } else if (k == 1) {
            if (i < 0) {
               return null;
            }

            Slot slot2 = this.c.get(i);
            if (slot2 != null && slot2.isAllowed(entityhuman)) {
               ItemStack itemstack1 = this.b(entityhuman, i);
               if (itemstack1 != null) {
                  Item item = itemstack1.getItem();
                  itemstack = itemstack1.cloneItemStack();
                  if (slot2.getItem() != null && slot2.getItem().getItem() == item) {
                     this.a(i, j, true, entityhuman);
                  }
               }
            }
         } else {
            if (i < 0) {
               return null;
            }

            Slot slot2 = this.c.get(i);
            if (slot2 != null) {
               ItemStack itemstack1 = slot2.getItem();
               ItemStack itemstack4x = playerinventory.getCarried();
               if (itemstack1 != null) {
                  itemstack = itemstack1.cloneItemStack();
               }

               if (itemstack1 == null) {
                  if (itemstack4x != null && slot2.isAllowed(itemstack4x)) {
                     int k1 = j == 0 ? itemstack4x.count : 1;
                     if (k1 > slot2.getMaxStackSize(itemstack4x)) {
                        k1 = slot2.getMaxStackSize(itemstack4x);
                     }

                     if (itemstack4x.count >= k1) {
                        slot2.set(itemstack4x.cloneAndSubtract(k1));
                     }

                     if (itemstack4x.count == 0) {
                        playerinventory.setCarried(null);
                     } else {
                        entityhuman.sendPacket(new PacketPlayOutSetSlot(-1, -1, entityhuman.inventory.getCarried()));
                     }
                  }
               } else if (slot2.isAllowed(entityhuman)) {
                  if (itemstack4x == null) {
                     int k1x = j == 0 ? itemstack1.count : (itemstack1.count + 1) / 2;
                     ItemStack itemstack3 = slot2.a(k1x);
                     playerinventory.setCarried(itemstack3);
                     if (itemstack1.count == 0) {
                        slot2.set(null);
                     }

                     slot2.a(entityhuman, playerinventory.getCarried());
                  } else if (slot2.isAllowed(itemstack4x)) {
                     if (itemstack1.getItem() == itemstack4x.getItem()
                        && itemstack1.getData() == itemstack4x.getData()
                        && ItemStack.equals(itemstack1, itemstack4x)) {
                        int k1x = j == 0 ? itemstack4x.count : 1;
                        if (k1x > slot2.getMaxStackSize(itemstack4x) - itemstack1.count) {
                           k1x = slot2.getMaxStackSize(itemstack4x) - itemstack1.count;
                        }

                        if (k1x > itemstack4x.getMaxStackSize() - itemstack1.count) {
                           k1x = itemstack4x.getMaxStackSize() - itemstack1.count;
                        }

                        itemstack4x.cloneAndSubtract(k1x);
                        if (itemstack4x.count == 0) {
                           playerinventory.setCarried(null);
                        } else {
                           entityhuman.sendPacket(new PacketPlayOutSetSlot(-1, -1, entityhuman.inventory.getCarried()));
                        }

                        itemstack1.count += k1x;
                     } else if (itemstack4x.count <= slot2.getMaxStackSize(itemstack4x)) {
                        slot2.set(itemstack4x);
                        playerinventory.setCarried(itemstack1);
                     }
                  } else if (itemstack1.getItem() == itemstack4x.getItem()
                     && itemstack4x.getMaxStackSize() > 1
                     && (!itemstack1.usesData() || itemstack1.getData() == itemstack4x.getData())
                     && ItemStack.equals(itemstack1, itemstack4x)) {
                     int k1xx = itemstack1.count;
                     int maxStack = Math.min(itemstack4x.getMaxStackSize(), slot2.getMaxStackSize());
                     if (k1xx > 0 && k1xx + itemstack4x.count <= maxStack) {
                        itemstack4x.count += k1xx;
                        itemstack1 = slot2.a(k1xx);
                        if (itemstack1.count == 0) {
                           slot2.set(null);
                        }

                        slot2.a(entityhuman, playerinventory.getCarried());
                     } else {
                        entityhuman.sendPacket(new PacketPlayOutSetSlot(-1, -1, entityhuman.inventory.getCarried()));
                     }
                  }
               }

               slot2.f();
               if (slot2.getMaxStackSize() != 64) {
                  entityhuman.sendPacket(new PacketPlayOutSetSlot(this.windowId, slot2.rawSlotIndex, slot2.getItem()));
                  if (this.getBukkitView().getType() == InventoryType.WORKBENCH || this.getBukkitView().getType() == InventoryType.CRAFTING) {
                     entityhuman.sendPacket(new PacketPlayOutSetSlot(this.windowId, 0, this.getSlot(0).getItem()));
                  }
               }
            }
         }
      } else if (k == 2 && j >= 0 && j < 9) {
         Slot slot2 = this.c.get(i);
         if (slot2.isAllowed(entityhuman)) {
            ItemStack itemstack1x = playerinventory.getItem(j);
            boolean flag = itemstack1x == null || slot2.inventory == playerinventory && slot2.isAllowed(itemstack1x);
            int k1xx = -1;
            if (!flag) {
               k1xx = playerinventory.getFirstEmptySlotIndex();
               flag = k1xx > -1;
            }

            if (slot2.hasItem() && flag) {
               ItemStack itemstack3 = slot2.getItem();
               playerinventory.setItem(j, itemstack3.cloneItemStack());
               if ((slot2.inventory != playerinventory || !slot2.isAllowed(itemstack1x)) && itemstack1x != null) {
                  if (k1xx > -1) {
                     playerinventory.pickup(itemstack1x);
                     slot2.a(itemstack3.count);
                     slot2.set(null);
                     slot2.a(entityhuman, itemstack3);
                  }
               } else {
                  slot2.a(itemstack3.count);
                  slot2.set(itemstack1x);
                  slot2.a(entityhuman, itemstack3);
               }
            } else if (!slot2.hasItem() && itemstack1x != null && slot2.isAllowed(itemstack1x)) {
               playerinventory.setItem(j, null);
               slot2.set(itemstack1x);
            }
         }
      } else if (k == 3 && entityhuman.abilities.canInstantlyBuild && playerinventory.getCarried() == null && i >= 0) {
         Slot slot2 = this.c.get(i);
         if (slot2 != null && slot2.hasItem()) {
            ItemStack itemstack1xx = slot2.getItem().cloneItemStack();
            itemstack1xx.count = itemstack1xx.getMaxStackSize();
            playerinventory.setCarried(itemstack1xx);
         }
      } else if (k == 4 && playerinventory.getCarried() == null && i >= 0) {
         Slot slot2 = this.c.get(i);
         if (slot2 != null && slot2.hasItem() && slot2.isAllowed(entityhuman)) {
            ItemStack itemstack1xx = slot2.a(j == 0 ? 1 : slot2.getItem().count);
            slot2.a(entityhuman, itemstack1xx);
            entityhuman.drop(itemstack1xx, true);
         }
      } else if (k == 6 && i >= 0) {
         Slot slot2 = this.c.get(i);
         ItemStack itemstack1xx = playerinventory.getCarried();
         if (itemstack1xx != null && (slot2 == null || !slot2.hasItem() || !slot2.isAllowed(entityhuman))) {
            int l = j == 0 ? 0 : this.c.size() - 1;
            int k1xxx = j == 0 ? 1 : -1;

            for (int l1 = 0; l1 < 2; l1++) {
               for (int i2 = l; i2 >= 0 && i2 < this.c.size() && itemstack1xx.count < itemstack1xx.getMaxStackSize(); i2 += k1xxx) {
                  Slot slot3 = this.c.get(i2);
                  if (slot3.hasItem()
                     && a(slot3, itemstack1xx, true)
                     && slot3.isAllowed(entityhuman)
                     && this.a(itemstack1xx, slot3)
                     && (l1 != 0 || slot3.getItem().count != slot3.getItem().getMaxStackSize())) {
                     int j2 = Math.min(itemstack1xx.getMaxStackSize() - itemstack1xx.count, slot3.getItem().count);
                     ItemStack itemstack5 = slot3.a(j2);
                     itemstack1xx.count += j2;
                     if (itemstack5.count <= 0) {
                        slot3.set(null);
                     }

                     slot3.a(entityhuman, itemstack5);
                  }
               }
            }
         }

         this.b();
      }

      return itemstack;
   }

   public boolean a(ItemStack itemstack, Slot slot) {
      return true;
   }

   protected void a(int i, int j, boolean flag, EntityHuman entityhuman) {
      this.clickItem(i, j, 1, entityhuman);
   }

   public void b(EntityHuman entityhuman) {
      PlayerInventory playerinventory = entityhuman.inventory;
      if (playerinventory.getCarried() != null) {
         entityhuman.drop(playerinventory.getCarried(), false);
         playerinventory.setCarried(null);
      }
   }

   public void a(IInventory iinventory) {
      this.b();
   }

   public void setItem(int i, ItemStack itemstack) {
      this.getSlot(i).set(itemstack);
   }

   public boolean c(EntityHuman entityhuman) {
      return !this.i.contains(entityhuman);
   }

   public void a(EntityHuman entityhuman, boolean flag) {
      if (flag) {
         this.i.remove(entityhuman);
      } else {
         this.i.add(entityhuman);
      }
   }

   public abstract boolean a(EntityHuman var1);

   protected boolean a(ItemStack itemstack, int i, int j, boolean flag) {
      boolean flag1 = false;
      int k = i;
      if (flag) {
         k = j - 1;
      }

      if (itemstack.isStackable()) {
         while (itemstack.count > 0 && (!flag && k < j || flag && k >= i)) {
            Slot slot = this.c.get(k);
            ItemStack itemstack1 = slot.getItem();
            if (itemstack1 != null
               && itemstack1.getItem() == itemstack.getItem()
               && (!itemstack.usesData() || itemstack.getData() == itemstack1.getData())
               && ItemStack.equals(itemstack, itemstack1)) {
               int l = itemstack1.count + itemstack.count;
               int maxStack = Math.min(itemstack.getMaxStackSize(), slot.getMaxStackSize());
               if (l <= maxStack) {
                  itemstack.count = 0;
                  itemstack1.count = l;
                  slot.f();
                  flag1 = true;
               } else if (itemstack1.count < maxStack) {
                  itemstack.count = itemstack.count - (maxStack - itemstack1.count);
                  itemstack1.count = maxStack;
                  slot.f();
                  flag1 = true;
               }
            }

            if (flag) {
               k--;
            } else {
               k++;
            }
         }
      }

      if (itemstack.count > 0) {
         if (flag) {
            k = j - 1;
         } else {
            k = i;
         }

         while (!flag && k < j || flag && k >= i) {
            Slot slotx = this.c.get(k);
            ItemStack itemstack1x = slotx.getItem();
            if (itemstack1x == null) {
               slotx.set(itemstack.cloneItemStack());
               slotx.f();
               itemstack.count = 0;
               flag1 = true;
               break;
            }

            if (flag) {
               k--;
            } else {
               k++;
            }
         }
      }

      return flag1;
   }

   public static int b(int i) {
      return i >> 2 & 3;
   }

   public static int c(int i) {
      return i & 3;
   }

   public static boolean a(int i, EntityHuman entityhuman) {
      return i == 0 || i == 1 || i == 2 && entityhuman.abilities.canInstantlyBuild;
   }

   protected void d() {
      this.g = 0;
      this.h.clear();
   }

   public static boolean a(Slot slot, ItemStack itemstack, boolean flag) {
      boolean flag1 = slot == null || !slot.hasItem();
      if (slot != null && slot.hasItem() && itemstack != null && itemstack.doMaterialsMatch(slot.getItem()) && ItemStack.equals(slot.getItem(), itemstack)) {
         int var10002 = flag ? 0 : itemstack.count;
         flag1 |= slot.getItem().count + var10002 <= itemstack.getMaxStackSize();
      }

      return flag1;
   }

   public static void a(Set<Slot> set, int i, ItemStack itemstack, int j) {
      switch (i) {
         case 0:
            itemstack.count = MathHelper.d((float)itemstack.count / set.size());
            break;
         case 1:
            itemstack.count = 1;
            break;
         case 2:
            itemstack.count = itemstack.getItem().getMaxStackSize();
      }

      itemstack.count += j;
   }

   public boolean b(Slot slot) {
      return true;
   }

   public static int a(TileEntity tileentity) {
      return tileentity instanceof IInventory ? b((IInventory)tileentity) : 0;
   }

   public static int b(IInventory iinventory) {
      if (iinventory == null) {
         return 0;
      } else {
         int i = 0;
         float f = 0.0F;

         for (int j = 0; j < iinventory.getSize(); j++) {
            ItemStack itemstack = iinventory.getItem(j);
            if (itemstack != null) {
               f += (float)itemstack.count / Math.min(iinventory.getMaxStackSize(), itemstack.getMaxStackSize());
               i++;
            }
         }

         f /= iinventory.getSize();
         return MathHelper.d(f * 14.0F) + (i > 0 ? 1 : 0);
      }
   }
}
