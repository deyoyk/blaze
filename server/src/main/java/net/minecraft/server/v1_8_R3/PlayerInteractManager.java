package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.BlazeConfig;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftItem;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDropItemsEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractManager {
   public World world;
   public EntityPlayer player;
   public boolean interactResult = false;
   public boolean firedInteract = false;
   private WorldSettings.EnumGamemode gamemode = WorldSettings.EnumGamemode.NOT_SET;
   private boolean d;
   private int lastDigTick;
   private BlockPosition f = BlockPosition.ZERO;
   private int currentTick;
   private boolean h;
   private BlockPosition i = BlockPosition.ZERO;
   private int j;
   private int k = -1;

   public PlayerInteractManager(World world) {
      this.world = world;
   }

   public WorldSettings.EnumGamemode getGameMode() {
      return this.gamemode;
   }

   public void setGameMode(WorldSettings.EnumGamemode worldsettings_enumgamemode) {
      this.gamemode = worldsettings_enumgamemode;
      worldsettings_enumgamemode.a(this.player.abilities);
      this.player.updateAbilities();
      this.player
         .server
         .getPlayerList()
         .sendAllLazily(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_GAME_MODE, this.player), this.player);
   }

   public boolean c() {
      return this.gamemode.e();
   }

   public boolean isCreative() {
      return this.gamemode.d();
   }

   public void b(WorldSettings.EnumGamemode worldsettings_enumgamemode) {
      if (this.gamemode == WorldSettings.EnumGamemode.NOT_SET) {
         this.gamemode = worldsettings_enumgamemode;
      }

      this.setGameMode(this.gamemode);
   }

   public void a() {
      this.currentTick = MinecraftServer.currentTick;
      if (this.h) {
         int x = this.i.getX();
         int y = this.i.getY();
         int z = this.i.getZ();
         Chunk ch = this.world.getChunkAtWorldCoords(x, z);
         IBlockData iblockdata = this.world.getType(ch, x, y, z, true);
         if (iblockdata.getBlock().getMaterial() == Material.AIR) {
            this.h = false;
         } else {
            float f = iblockdata.getBlock().getDamage(this.player, this.player.world, this.i) * (this.currentTick - this.j + 1);
            int i = (int)(f * 10.0F);
            if (i != this.k) {
               this.world.c(this.player.getId(), this.i, i);
               this.k = i;
            }

            if (f >= 1.0F) {
               this.h = false;
               this.breakBlock(ch.bukkitChunk.getBlock(x, y, z), iblockdata, ch, this.i);
            }
         }
      } else if (this.d) {
         int x = this.f.getX();
         int y = this.f.getY();
         int z = this.f.getZ();
         Block block1 = this.world.getType(x, y, z, true).getBlock();
         if (block1.getMaterial() == Material.AIR) {
            this.world.c(this.player.getId(), this.f, -1);
            this.k = -1;
            this.d = false;
         } else {
            float fx = block1.getDamage(this.player, this.player.world, this.i) * (this.currentTick - this.lastDigTick + 1);
            int ix = (int)(fx * 10.0F);
            if (ix != this.k) {
               this.world.c(this.player.getId(), this.f, ix);
               this.k = ix;
            }
         }
      }
   }

   public void a(BlockPosition blockposition, EnumDirection enumdirection) {
      this.a(this.world.getChunkAtWorldCoords(blockposition), blockposition, enumdirection);
   }

   private boolean c(BlockPosition blockposition) {
      Chunk chunk = this.world.getChunkAtWorldCoords(blockposition);
      return this.c(chunk, this.world.getType(chunk, blockposition), blockposition);
   }

   public boolean breakBlock(BlockPosition blockposition) {
      Chunk chunk = this.world.getChunkAtWorldCoords(blockposition);
      IBlockData iblockdata = this.world.getType(chunk, blockposition);
      return this.breakBlock(chunk.bukkitChunk.getBlock(blockposition.getX(), blockposition.getY(), blockposition.getZ()), iblockdata, chunk, blockposition);
   }

   public void a(BlockPosition blockposition) {
      Chunk chunk = this.world.getChunkAtWorldCoords(blockposition);
      this.a(chunk, this.world.getType(chunk, blockposition), blockposition);
   }

   public void a(Chunk ch, BlockPosition blockposition, EnumDirection enumdirection) {
      org.bukkit.block.Block bukkitBlock = ch.bukkitChunk.getBlock(blockposition.getX(), blockposition.getY(), blockposition.getZ());
      PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent(
         this.player, Action.LEFT_CLICK_BLOCK, bukkitBlock, blockposition, enumdirection, this.player.inventory.getItemInHand(), false
      );
      IBlockData blockdata = this.world.getType(ch, blockposition);
      if (event.isCancelled()) {
         this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(blockposition, blockdata));
         TileEntity tileentity = this.world.getTileEntity(ch, blockposition, blockdata.getBlock());
         if (tileentity != null) {
            this.player.playerConnection.sendPacket(tileentity.getUpdatePacket());
         }
      } else {
         if (this.isCreative()) {
            if (!this.world.douseFire(null, blockposition, enumdirection)) {
               this.breakBlock(bukkitBlock, blockdata, ch, blockposition);
            }
         } else {
            if (this.gamemode.c()) {
               if (this.gamemode == WorldSettings.EnumGamemode.SPECTATOR) {
                  return;
               }

               if (!this.player.cn()) {
                  ItemStack itemstack = this.player.bZ();
                  if (itemstack == null || !itemstack.c(blockdata.getBlock())) {
                     return;
                  }
               }
            }

            this.lastDigTick = this.currentTick;
            float f = 1.0F;
            if (event.useInteractedBlock() == Result.DENY) {
               if (blockdata.getBlock() == Blocks.WOODEN_DOOR) {
                  boolean bottom = blockdata.get(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER;
                  this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(blockposition, blockdata));
                  this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(this.world, bottom ? blockposition.up() : blockposition.down()));
               } else if (blockdata.getBlock() == Blocks.TRAPDOOR) {
                  this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(blockposition, blockdata));
               }
            } else if (blockdata.getBlock().getMaterial() != Material.AIR) {
               blockdata.getBlock().attack(this.world, blockposition, this.player);
               f = blockdata.getBlock().getDamage(this.player, this.player.world, blockposition);
               this.world.douseFire(null, blockposition, enumdirection);
            }

            if (event.useItemInHand() == Result.DENY) {
               if (f >= 1.0F) {
                  this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(blockposition, blockdata));
               }

               return;
            }

            BlockDamageEvent blockEvent = CraftEventFactory.callBlockDamageEvent(this.player, bukkitBlock, this.player.inventory.getItemInHand(), f >= 1.0F);
            blockdata = this.world.getType(ch, blockposition);
            if (blockEvent.isCancelled()) {
               this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(blockposition, blockdata));
               return;
            }

            if (blockEvent.getInstaBreak()) {
               f = 2.0F;
            }

            if (blockdata.getBlock().getMaterial() != Material.AIR && f >= 1.0F) {
               this.breakBlock(bukkitBlock, blockdata, ch, blockposition);
            } else {
               this.d = true;
               this.f = blockposition;
               int i = (int)(f * 10.0F);
               this.world.c(this.player.getId(), blockposition, i);
               this.k = i;
            }
         }

         this.world.spigotConfig.antiXrayInstance.updateNearbyBlocks(this.world, blockposition);
      }
   }

   public void a(Chunk ch, IBlockData iblockdata, BlockPosition blockposition) {
      if (blockposition.equals(this.f)) {
         this.currentTick = MinecraftServer.currentTick;
         if (iblockdata.getBlock().getMaterial() != Material.AIR) {
            if (iblockdata.getBlock().getDamage(this.player, this.player.world, blockposition) * (this.currentTick - this.lastDigTick + 1) >= 0.7F) {
               this.d = false;
               this.world.c(this.player.getId(), blockposition, -1);
               this.breakBlock(ch.bukkitChunk.getBlock(blockposition.getX(), blockposition.getY(), blockposition.getZ()), iblockdata, ch, blockposition);
            } else if (!this.h) {
               this.d = false;
               this.h = true;
               this.i = blockposition;
               this.j = this.lastDigTick;
            }
         }
      } else {
         this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(blockposition, iblockdata));
      }
   }

   public void e() {
      this.d = false;
      this.world.c(this.player.getId(), this.f, -1);
   }

   private boolean c(Chunk chunk, IBlockData iblockdata, BlockPosition blockposition) {
      iblockdata.getBlock().a(this.world, blockposition, iblockdata, (EntityHuman)this.player);
      boolean flag = this.world.setTypeAndData(chunk, blockposition, Blocks.AIR.getBlockData(), 3, true, false);
      if (flag) {
         iblockdata.getBlock().postBreak(this.world, blockposition, iblockdata);
      }

      return flag;
   }

   public boolean breakBlock(org.bukkit.block.Block bukkitBlock, IBlockData nmsData, Chunk ch, BlockPosition blockposition) {
      boolean isSwordNoBreak = this.gamemode.d() && this.player.bA() != null && this.player.bA().getItem() instanceof ItemSword;
      BlockBreakEvent event = new BlockBreakEvent(bukkitBlock, this.player.getBukkitEntity());
      event.setCancelled(isSwordNoBreak);
      if (!event.isCancelled()
         && !this.isCreative()
         && this.player.b(nmsData.getBlock())
         && (!nmsData.getBlock().I() || !EnchantmentManager.hasSilkTouchEnchantment(this.player))) {
         event.setExpToDrop(nmsData.getBlock().getExpDrop(this.world, nmsData, EnchantmentManager.getBonusBlockLootEnchantmentLevel(this.player)));
      }

      this.world.getServer().getPluginManager().callEvent(event);
      nmsData = this.world.getType(ch, blockposition);
      if (event.isCancelled()) {
         if (isSwordNoBreak) {
            return false;
         } else {
            this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(blockposition, nmsData));
            TileEntity tileEntity = this.world.getTileEntity(ch, blockposition, nmsData.getBlock());
            if (tileEntity != null) {
               this.player.playerConnection.sendPacket(tileEntity.getUpdatePacket());
            }

            return false;
         }
      } else {
         TileEntity tileEntity = this.world.getTileEntity(ch, blockposition, nmsData.getBlock());
         if (!isSwordNoBreak && tileEntity == null) {
            this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(blockposition, Blocks.AIR.getBlockData()));
         }

         if (nmsData.getBlock() == Blocks.AIR) {
            return false;
         } else if (nmsData.getBlock() == Blocks.SKULL && !this.isCreative()) {
            nmsData.getBlock().dropNaturally(this.world, blockposition, nmsData, 1.0F, 0);
            return this.c(ch, nmsData, blockposition);
         } else {
            if (this.gamemode.c()) {
               if (this.gamemode == WorldSettings.EnumGamemode.SPECTATOR) {
                  return false;
               }

               if (!this.player.cn()) {
                  ItemStack itemstack = this.player.bZ();
                  if (itemstack == null || !itemstack.c(nmsData.getBlock())) {
                     return false;
                  }
               }
            }

            this.world.a(this.player, 2001, blockposition, Block.getCombinedId(nmsData));
            boolean flag = this.c(ch, nmsData, blockposition);
            if (!this.isCreative()) {
               ItemStack itemstack1 = this.player.bZ();
               boolean flag1 = this.player.b(nmsData.getBlock());
               if (itemstack1 != null) {
                  itemstack1.a(this.world, nmsData.getBlock(), blockposition, this.player);
                  if (itemstack1.count == 0) {
                     this.player.ca();
                  }
               }

               if (flag && flag1) {
                  Block block = nmsData.getBlock();
                  List<org.bukkit.entity.Item> items = new ArrayList<>(1);
                  block.droppedItemsCatcher = items;
                  block.a(this.world, this.player, blockposition, nmsData, tileEntity);
                  block.droppedItemsCatcher = null;
                  BlockDropItemsEvent dropItemsEvent = new BlockDropItemsEvent(
                     this.world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()), this.player.getBukkitEntity(), items
                  );
                  Bukkit.getPluginManager().callEvent(dropItemsEvent);
                  if (!dropItemsEvent.isCancelled() && dropItemsEvent.getToDrop() != null) {
                     for (org.bukkit.entity.Item item : dropItemsEvent.getToDrop()) {
                        this.world.addEntity(((CraftItem)item).getHandle());
                     }
                  }
               }

               if (flag) {
                  nmsData.getBlock().dropExperience(this.world, blockposition, event.getExpToDrop());
               }
            }

            return flag;
         }
      }
   }

   public boolean useItem(EntityHuman entityhuman, World world, ItemStack itemstack) {
      if (this.gamemode == WorldSettings.EnumGamemode.SPECTATOR) {
         return false;
      } else {
         int i = itemstack.count;
         int j = itemstack.getData();
         ItemStack itemstack1 = itemstack.a(world, entityhuman);
         if (itemstack1 != null && (itemstack1 != itemstack || itemstack1.count != i || itemstack1.l() > 0 || itemstack1.getData() != j)) {
            entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex] = itemstack1;
            if (this.isCreative()) {
               itemstack1.count = i;
               if (itemstack1.e()) {
                  itemstack1.setData(j);
               }
            }

            if (itemstack1.count == 0) {
               entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex] = null;
            }

            if (!entityhuman.bS()) {
               ((EntityPlayer)entityhuman).updateInventory(entityhuman.defaultContainer);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public boolean interact(
      EntityHuman entityhuman, World world, ItemStack itemstack, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2
   ) {
      Chunk ch = world.getChunkAtWorldCoords(blockposition);
      IBlockData blockdata = world.getType(ch, blockposition);
      Block block = blockdata.getBlock();
      boolean result = false;
      if (block != Blocks.AIR) {
         boolean cancelledBlock = false;
         TileEntity tileentity = world.getTileEntity(ch, blockposition, block);
         org.bukkit.block.Block bukkitBlock = ch.bukkitChunk.getBlock(blockposition.getX(), blockposition.getY(), blockposition.getZ());
         if (this.gamemode == WorldSettings.EnumGamemode.SPECTATOR) {
            cancelledBlock = !(tileentity instanceof IInventory);
         }

         if (!entityhuman.getBukkitEntity().isOp() && itemstack != null && Block.asBlock(itemstack.getItem()) instanceof BlockCommand) {
            cancelledBlock = true;
         }

         if (itemstack != null
            && itemstack.getData() == 16421
            && block != null
            && ((List)BlazeConfig.potionLaunchOnClick.getValue()).contains(bukkitBlock.getType().name())) {
            cancelledBlock = true;
            this.player.playerInteractManager.useItem(this.player, this.player.world, itemstack);
         }

         PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent(
            entityhuman, Action.RIGHT_CLICK_BLOCK, bukkitBlock, blockposition, enumdirection, itemstack, cancelledBlock
         );
         this.firedInteract = true;
         this.interactResult = event.useItemInHand() == Result.DENY;
         if (event.useInteractedBlock() == Result.DENY) {
            if (block instanceof BlockDoor) {
               boolean bottom = blockdata.get(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER;
               entityhuman.sendPacket(new PacketPlayOutBlockChange(world, bottom ? blockposition.up() : blockposition.down()));
            }

            result = event.useItemInHand() != Result.ALLOW;
         } else {
            if (this.gamemode == WorldSettings.EnumGamemode.SPECTATOR) {
               if (tileentity instanceof ITileInventory) {
                  ITileInventory itileinventory = (ITileInventory)tileentity;
                  if (itileinventory instanceof TileEntityChest && block instanceof BlockChest) {
                     itileinventory = ((BlockChest)block).f(world, blockposition);
                  }

                  if (itileinventory != null) {
                     entityhuman.openContainer(itileinventory);
                     return true;
                  }
               } else if (tileentity instanceof IInventory) {
                  entityhuman.openContainer((IInventory)tileentity);
                  return true;
               }

               return false;
            }

            if (!entityhuman.isSneaking() || itemstack == null) {
               result = block.interact(world, blockposition, blockdata, entityhuman, enumdirection, f, f1, f2);
            }
         }

         if (itemstack != null && !result && !this.interactResult) {
            int j1 = itemstack.getData();
            int k1 = itemstack.count;

            try {
               world.interceptSounds();
               result = itemstack.placeItem(entityhuman, world, blockposition, enumdirection, f, f1, f2);
            } finally {
               if (result) {
                  world.sendInterceptedSounds();
               } else {
                  world.clearInterceptedSounds();
               }
            }

            if (this.isCreative()) {
               itemstack.setData(j1);
               itemstack.count = k1;
            }
         }
      }

      return result;
   }

   public void a(WorldServer worldserver) {
      this.world = worldserver;
   }
}
