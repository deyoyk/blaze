package org.bukkit.craftbukkit.v1_8_R3.entity;

import java.util.Set;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.Container;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityMinecartHopper;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IInventory;
import net.minecraft.server.v1_8_R3.ITileEntityContainer;
import net.minecraft.server.v1_8_R3.PacketPlayInCloseWindow;
import net.minecraft.server.v1_8_R3.PacketPlayOutOpenWindow;
import net.minecraft.server.v1_8_R3.TileEntity;
import net.minecraft.server.v1_8_R3.TileEntityBeacon;
import net.minecraft.server.v1_8_R3.TileEntityBrewingStand;
import net.minecraft.server.v1_8_R3.TileEntityDispenser;
import net.minecraft.server.v1_8_R3.TileEntityDropper;
import net.minecraft.server.v1_8_R3.TileEntityEnchantTable;
import net.minecraft.server.v1_8_R3.TileEntityFurnace;
import net.minecraft.server.v1_8_R3.TileEntityHopper;
import net.minecraft.server.v1_8_R3.BlockAnvil.TileEntityContainerAnvil;
import net.minecraft.server.v1_8_R3.BlockWorkbench.TileEntityContainerWorkbench;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftContainer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.inventory.InventoryWrapper;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

public class CraftHumanEntity extends CraftLivingEntity implements HumanEntity {
   private CraftInventoryPlayer inventory;
   private final CraftInventory enderChest;
   protected final PermissibleBase perm = new PermissibleBase(this);
   private boolean op;
   private org.bukkit.GameMode mode;

   public CraftHumanEntity(CraftServer server, EntityHuman entity) {
      super(server, entity);
      this.mode = server.getDefaultGameMode();
      this.op = server.getHandle().isOp(entity.getProfile());
      this.inventory = new CraftInventoryPlayer(entity.inventory);
      this.enderChest = new CraftInventory(entity.getEnderChest());
   }

   @Override
   public String getName() {
      return this.getHandle().getName();
   }

   @Override
   public PlayerInventory getInventory() {
      return this.inventory;
   }

   @Override
   public EntityEquipment getEquipment() {
      return this.inventory;
   }

   @Override
   public Inventory getEnderChest() {
      return this.enderChest;
   }

   @Override
   public ItemStack getItemInHand() {
      return this.getInventory().getItemInHand();
   }

   @Override
   public void setItemInHand(ItemStack item) {
      this.getInventory().setItemInHand(item);
   }

   @Override
   public ItemStack getItemOnCursor() {
      return CraftItemStack.asCraftMirror(this.getHandle().inventory.getCarried());
   }

   @Override
   public void setItemOnCursor(ItemStack item) {
      net.minecraft.server.v1_8_R3.ItemStack stack = CraftItemStack.asNMSCopy(item);
      this.getHandle().inventory.setCarried(stack);
      if (this instanceof CraftPlayer) {
         ((EntityPlayer)this.getHandle()).broadcastCarriedItem();
      }
   }

   @Override
   public boolean isSleeping() {
      return this.getHandle().sleeping;
   }

   @Override
   public int getSleepTicks() {
      return this.getHandle().sleepTicks;
   }

   @Override
   public boolean isOp() {
      return this.op;
   }

   @Override
   public boolean isPermissionSet(String name) {
      return this.perm.isPermissionSet(name);
   }

   @Override
   public boolean isPermissionSet(Permission perm) {
      return this.perm.isPermissionSet(perm);
   }

   @Override
   public boolean hasPermission(String name) {
      return this.perm.hasPermission(name);
   }

   @Override
   public boolean hasPermission(Permission perm) {
      return this.perm.hasPermission(perm);
   }

   @Override
   public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
      return this.perm.addAttachment(plugin, name, value);
   }

   @Override
   public PermissionAttachment addAttachment(Plugin plugin) {
      return this.perm.addAttachment(plugin);
   }

   @Override
   public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
      return this.perm.addAttachment(plugin, name, value, ticks);
   }

   @Override
   public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
      return this.perm.addAttachment(plugin, ticks);
   }

   @Override
   public void removeAttachment(PermissionAttachment attachment) {
      this.perm.removeAttachment(attachment);
   }

   @Override
   public void recalculatePermissions() {
      this.perm.recalculatePermissions();
   }

   @Override
   public void setOp(boolean value) {
      this.op = value;
      this.perm.recalculatePermissions();
   }

   @Override
   public Set<PermissionAttachmentInfo> getEffectivePermissions() {
      return this.perm.getEffectivePermissions();
   }

   @Override
   public org.bukkit.GameMode getGameMode() {
      return this.mode;
   }

   @Override
   public void setGameMode(org.bukkit.GameMode mode) {
      if (mode == null) {
         throw new IllegalArgumentException("Mode cannot be null");
      } else {
         this.mode = mode;
      }
   }

   public EntityHuman getHandle() {
      return (EntityHuman)this.entity;
   }

   public void setHandle(EntityHuman entity) {
      super.setHandle(entity);
      this.inventory = new CraftInventoryPlayer(entity.inventory);
   }

   @Override
   public String toString() {
      return "CraftHumanEntity{id=" + this.getEntityId() + "name=" + this.getName() + '}';
   }

   @Override
   public InventoryView getOpenInventory() {
      return this.getHandle().activeContainer.getBukkitView();
   }

   @Override
   public InventoryView openInventory(Inventory inventory) {
      if (!(this.getHandle() instanceof EntityPlayer)) {
         return null;
      } else {
         EntityPlayer player = (EntityPlayer)this.getHandle();
         InventoryType type = inventory.getType();
         Container formerContainer = this.getHandle().activeContainer;
         IInventory iinventory = (IInventory)(inventory instanceof CraftInventory
            ? ((CraftInventory)inventory).getInventory()
            : new InventoryWrapper(inventory));
         switch (type) {
            case CHEST:
            case PLAYER:
            case ENDER_CHEST:
               this.getHandle().openContainer(iinventory);
               break;
            case DISPENSER:
               if (iinventory instanceof TileEntityDispenser) {
                  this.getHandle().openContainer(iinventory);
               } else {
                  this.openCustomInventory(inventory, player, "minecraft:dispenser");
               }
               break;
            case DROPPER:
               if (iinventory instanceof TileEntityDropper) {
                  this.getHandle().openContainer(iinventory);
               } else {
                  this.openCustomInventory(inventory, player, "minecraft:dropper");
               }
               break;
            case FURNACE:
               if (iinventory instanceof TileEntityFurnace) {
                  this.getHandle().openContainer(iinventory);
               } else {
                  this.openCustomInventory(inventory, player, "minecraft:furnace");
               }
               break;
            case WORKBENCH:
               this.openCustomInventory(inventory, player, "minecraft:crafting_table");
               break;
            case CRAFTING:
            case CREATIVE:
               throw new IllegalArgumentException("Can't open a " + type + " inventory!");
            case ENCHANTING:
               this.openCustomInventory(inventory, player, "minecraft:enchanting_table");
               break;
            case BREWING:
               if (iinventory instanceof TileEntityBrewingStand) {
                  this.getHandle().openContainer(iinventory);
               } else {
                  this.openCustomInventory(inventory, player, "minecraft:brewing_stand");
               }
            case MERCHANT:
            default:
               break;
            case ANVIL:
               if (iinventory instanceof TileEntityContainerAnvil) {
                  this.getHandle().openTileEntity((TileEntityContainerAnvil)iinventory);
               } else {
                  this.openCustomInventory(inventory, player, "minecraft:anvil");
               }
               break;
            case BEACON:
               if (iinventory instanceof TileEntityBeacon) {
                  this.getHandle().openContainer(iinventory);
               } else {
                  this.openCustomInventory(inventory, player, "minecraft:beacon");
               }
               break;
            case HOPPER:
               if (iinventory instanceof TileEntityHopper) {
                  this.getHandle().openContainer(iinventory);
               } else if (iinventory instanceof EntityMinecartHopper) {
                  this.getHandle().openContainer(iinventory);
               } else {
                  this.openCustomInventory(inventory, player, "minecraft:hopper");
               }
         }

         if (this.getHandle().activeContainer == formerContainer) {
            return null;
         } else {
            this.getHandle().activeContainer.checkReachable = false;
            return this.getHandle().activeContainer.getBukkitView();
         }
      }
   }

   private void openCustomInventory(Inventory inventory, EntityPlayer player, String windowType) {
      if (player.playerConnection != null) {
         Container container = new CraftContainer(inventory, this, player.nextContainerCounter());
         container = CraftEventFactory.callInventoryOpenEvent(player, container);
         if (container != null) {
            String title = container.getBukkitView().getTitle();
            int size = container.getBukkitView().getTopInventory().getSize();
            if (windowType.equals("minecraft:crafting_table") || windowType.equals("minecraft:anvil") || windowType.equals("minecraft:enchanting_table")) {
               size = 0;
            }

            player.playerConnection.sendPacket(new PacketPlayOutOpenWindow(container.windowId, windowType, new ChatComponentText(title), size));
            this.getHandle().activeContainer = container;
            this.getHandle().activeContainer.addSlotListener(player);
         }
      }
   }

   @Override
   public InventoryView openWorkbench(org.bukkit.Location location, boolean force) {
      if (!force) {
         Block block = location.getBlock();
         if (block.getType() != org.bukkit.Material.WORKBENCH) {
            return null;
         }
      }

      if (location == null) {
         location = this.getLocation();
      }

      this.getHandle()
         .openTileEntity(
            new TileEntityContainerWorkbench(this.getHandle().world, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()))
         );
      if (force) {
         this.getHandle().activeContainer.checkReachable = false;
      }

      return this.getHandle().activeContainer.getBukkitView();
   }

   @Override
   public InventoryView openEnchanting(org.bukkit.Location location, boolean force) {
      if (!force) {
         Block block = location.getBlock();
         if (block.getType() != org.bukkit.Material.ENCHANTMENT_TABLE) {
            return null;
         }
      }

      if (location == null) {
         location = this.getLocation();
      }

      TileEntity container = this.getHandle().world.getTileEntity(new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
      if (container == null && force) {
         container = new TileEntityEnchantTable();
      }

      this.getHandle().openTileEntity((ITileEntityContainer)container);
      if (force) {
         this.getHandle().activeContainer.checkReachable = false;
      }

      return this.getHandle().activeContainer.getBukkitView();
   }

   @Override
   public void openInventory(InventoryView inventory) {
      if (this.getHandle() instanceof EntityPlayer) {
         if (((EntityPlayer)this.getHandle()).playerConnection != null) {
            if (this.getHandle().activeContainer != this.getHandle().defaultContainer) {
               ((EntityPlayer)this.getHandle()).playerConnection.a(new PacketPlayInCloseWindow(this.getHandle().activeContainer.windowId));
            }

            EntityPlayer player = (EntityPlayer)this.getHandle();
            Container container;
            if (inventory instanceof CraftInventoryView) {
               container = ((CraftInventoryView)inventory).getHandle();
            } else {
               container = new CraftContainer(inventory, player.nextContainerCounter());
            }

            container = CraftEventFactory.callInventoryOpenEvent(player, container);
            if (container != null) {
               InventoryType type = inventory.getType();
               String windowType = CraftContainer.getNotchInventoryType(type);
               String title = inventory.getTitle();
               int size = inventory.getTopInventory().getSize();
               player.playerConnection.sendPacket(new PacketPlayOutOpenWindow(container.windowId, windowType, new ChatComponentText(title), size));
               player.activeContainer = container;
               player.activeContainer.addSlotListener(player);
            }
         }
      }
   }

   @Override
   public void closeInventory() {
      this.getHandle().closeInventory();
   }

   @Override
   public boolean isBlocking() {
      return this.getHandle().isBlocking();
   }

   @Override
   public boolean setWindowProperty(InventoryView.Property prop, int value) {
      return false;
   }

   @Override
   public int getExpToLevel() {
      return this.getHandle().getExpToLevel();
   }
}
