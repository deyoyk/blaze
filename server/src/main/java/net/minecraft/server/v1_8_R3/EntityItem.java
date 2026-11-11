package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.BlazeConfig;
import net.techcable.tacospigot.HopperPusher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class EntityItem extends Entity implements HopperPusher {
   private static final Logger b = LogManager.getLogger();
   public int pickupDelay;
   public float a;
   public Entity owner;
   private int age;
   private int e;
   private String f;
   private String g;
   private int lastTick = MinecraftServer.currentTick;

   public EntityItem(World world, double d0, double d1, double d2) {
      super(world);
      this.e = 5;
      this.a = (float)(Math.random() * Math.PI * 2.0);
      this.setSize(0.25F, 0.25F);
      this.setPosition(d0, d1, d2);
      this.yaw = (float)(Math.random() * 360.0);
      this.motX = (float)(Math.random() * 0.2F - 0.1F);
      this.motY = 0.2F;
      this.motZ = (float)(Math.random() * 0.2F - 0.1F);
   }

   public EntityItem(World world, double d0, double d1, double d2, ItemStack itemstack) {
      this(world, d0, d1, d2);
      if (itemstack != null && itemstack.getItem() != null) {
         this.setItemStack(itemstack);
      }
   }

   public EntityItem(World world) {
      super(world);
      this.e = 5;
      this.a = (float)(Math.random() * Math.PI * 2.0);
      this.setSize(0.25F, 0.25F);
      this.setItemStack(new ItemStack(Blocks.AIR, 0));
   }

   public boolean acceptItem(TileEntityHopper hopper) {
      return TileEntityHopper.a(hopper, this);
   }

   @Override
   protected boolean s_() {
      return false;
   }

   @Override
   protected void h() {
      this.getDataWatcher().add(10, 5);
   }

   @Override
   public void t_() {
      ItemStack itemStack = this.getItemStack();
      if (itemStack == null) {
         this.die();
      } else {
         super.t_();
         if (this.ticksLived % 15 == 0 && this.tryPutInHopper()) {
            return;
         }

         int elapsedTicks = MinecraftServer.currentTick - this.lastTick;
         if (this.pickupDelay != 32767) {
            this.pickupDelay -= elapsedTicks;
         }

         if (this.age != -32768) {
            this.age += elapsedTicks;
         }

         this.lastTick = MinecraftServer.currentTick;
         this.lastX = this.locX;
         this.lastY = this.locY;
         this.lastZ = this.locZ;
         this.motY -= 0.04F;
         this.noclip = this.j(this.locX, (this.boundingBox.b + this.boundingBox.e) / 2.0, this.locZ);
         this.move(this.motX, this.motY, this.motZ);
         if ((int)this.lastX != (int)this.locX || (int)this.lastY != (int)this.locY || (int)this.lastZ != (int)this.locZ || this.ticksLived % 25 == 0) {
            if (this.world.getType(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ)).getBlock().getMaterial()
               == Material.LAVA) {
               this.motY = 0.2F;
               this.motX = (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
               this.motZ = (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
               if ((Boolean)BlazeConfig.itemFizzSounds.getValue()) {
                  this.makeSound("random.fizz", 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
               }
            }

            this.w(itemStack);
         }

         float f = 0.98F;
         if (this.onGround) {
            f = this.world.getType(MathHelper.floor(this.locX), MathHelper.floor(this.boundingBox.b) - 1, MathHelper.floor(this.locZ)).getBlock().frictionFactor
               * 0.98F;
         }

         this.motX *= f;
         this.motY *= 0.98F;
         this.motZ *= f;
         if (this.onGround) {
            this.motY *= -0.5;
         }

         if (this.age >= this.world.spigotConfig.itemDespawnRate) {
            if (CraftEventFactory.callItemDespawnEvent(this).isCancelled()) {
               this.age = 0;
               return;
            }

            this.die();
         }
      }
   }

   @Override
   public void inactiveTick() {
      if (this.ticksLived % 15 != 0 || !this.tryPutInHopper()) {
         int elapsedTicks = MinecraftServer.currentTick - this.lastTick;
         if (this.pickupDelay != 32767) {
            this.pickupDelay -= elapsedTicks;
         }

         if (this.age != -32768) {
            this.age += elapsedTicks;
         }

         this.lastTick = MinecraftServer.currentTick;
         if (this.age >= this.world.spigotConfig.itemDespawnRate) {
            if (CraftEventFactory.callItemDespawnEvent(this).isCancelled()) {
               this.age = 0;
               return;
            }

            this.die();
         }
      }
   }

   private void w(ItemStack itemStack) {
      if (itemStack.count < itemStack.getMaxStackSize()) {
         double radius = this.world.spigotConfig.itemMerge;

         for (EntityItem entityItem : this.world.a(EntityItem.class, this.boundingBox.grow(radius, radius, radius))) {
            this.a(entityItem);
         }
      }
   }

   private boolean a(EntityItem entityitem) {
      if (entityitem == this) {
         return false;
      } else if (entityitem.isAlive() && this.isAlive()) {
         ItemStack itemstack = this.getItemStack();
         ItemStack itemstack1 = entityitem.getItemStack();
         if (this.pickupDelay == 32767 || entityitem.pickupDelay == 32767) {
            return false;
         } else if (this.age != -32768 && entityitem.age != -32768) {
            if (itemstack1.getItem() != itemstack.getItem()) {
               return false;
            } else if (itemstack1.hasTag() ^ itemstack.hasTag()) {
               return false;
            } else if (itemstack1.hasTag() && !itemstack1.getTag().equals(itemstack.getTag())) {
               return false;
            } else if (itemstack1.getItem() == null) {
               return false;
            } else if (itemstack1.getItem().k() && itemstack1.getData() != itemstack.getData()) {
               return false;
            } else if (itemstack1.count < itemstack.count) {
               return entityitem.a(this);
            } else if (itemstack1.count + itemstack.count > itemstack1.getMaxStackSize()) {
               return false;
            } else if (CraftEventFactory.callItemMergeEvent(entityitem, this).isCancelled()) {
               return false;
            } else {
               itemstack.count = itemstack.count + itemstack1.count;
               this.pickupDelay = Math.max(entityitem.pickupDelay, this.pickupDelay);
               this.age = Math.min(entityitem.age, this.age);
               this.setItemStack(itemstack);
               entityitem.die();
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void j() {
      this.age = 4800;
   }

   @Override
   public boolean W() {
      this.inWater = this.world.a(this.boundingBox, Material.WATER, this);
      return this.inWater;
   }

   protected void burn(int i) {
      this.damageEntity(DamageSource.FIRE, i);
   }

   @Override
   public boolean damageEntity(DamageSource damagesource, float f) {
      if (this.isInvulnerable(damagesource)) {
         return false;
      } else if (this.getItemStack() != null && this.getItemStack().getItem() == Items.NETHER_STAR && damagesource.isExplosion()) {
         return false;
      } else if (CraftEventFactory.handleNonLivingEntityDamageEvent(this, damagesource, f)) {
         return false;
      } else {
         this.ac();
         this.e = (int)(this.e - f);
         if (this.e <= 0) {
            this.die();
         }

         return false;
      }
   }

   @Override
   public void b(NBTTagCompound nbttagcompound) {
      nbttagcompound.setShort("Health", (short)this.e);
      nbttagcompound.setShort("Age", (short)this.age);
      nbttagcompound.setShort("PickupDelay", (short)this.pickupDelay);
      if (this.n() != null) {
         nbttagcompound.setString("Thrower", this.f);
      }

      if (this.m() != null) {
         nbttagcompound.setString("Owner", this.g);
      }

      if (this.getItemStack() != null) {
         nbttagcompound.set("Item", this.getItemStack().save(new NBTTagCompound()));
      }
   }

   @Override
   public void a(NBTTagCompound nbttagcompound) {
      this.e = nbttagcompound.getShort("Health") & 255;
      this.age = nbttagcompound.getShort("Age");
      if (nbttagcompound.hasKey("PickupDelay")) {
         this.pickupDelay = nbttagcompound.getShort("PickupDelay");
      }

      if (nbttagcompound.hasKey("Owner")) {
         this.g = nbttagcompound.getString("Owner");
      }

      if (nbttagcompound.hasKey("Thrower")) {
         this.f = nbttagcompound.getString("Thrower");
      }

      NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Item");
      if (nbttagcompound1 != null) {
         ItemStack itemstack = ItemStack.createStack(nbttagcompound1);
         if (itemstack != null) {
            this.setItemStack(itemstack);
         } else {
            this.die();
         }
      } else {
         this.die();
      }

      if (this.getItemStack() == null) {
         this.die();
      }
   }

   @Override
   public void d(EntityHuman entityhuman) {
      if (((EntityPlayer)entityhuman).canSee(this)) {
         ItemStack itemstack = this.getItemStack();
         int i = itemstack.count;
         int canHold = entityhuman.inventory.canHold(itemstack);
         int remaining = itemstack.count - canHold;
         if (this.pickupDelay <= 0 && canHold > 0) {
            itemstack.count = canHold;
            PlayerPickupItemEvent event = new PlayerPickupItemEvent(
               (Player)entityhuman.getBukkitEntity(), (org.bukkit.entity.Item)this.getBukkitEntity(), remaining
            );
            this.world.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
               return;
            }

            EntityPickupItemEvent entityEvent = new EntityPickupItemEvent(
               entityhuman.getBukkitEntity(), (org.bukkit.entity.Item)this.getBukkitEntity(), remaining
            );
            this.world.getServer().getPluginManager().callEvent(entityEvent);
            if (entityEvent.isCancelled()) {
               return;
            }

            itemstack.count = canHold + remaining;
            this.pickupDelay = 0;
         }

         if (this.pickupDelay == 0
            && (this.g == null || 6000 - this.age <= 200 || this.g.equals(entityhuman.getName()))
            && entityhuman.inventory.pickup(itemstack)) {
            if (!this.R()) {
               this.world.makeSound(entityhuman, "random.pop", 0.2F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }

            entityhuman.receive(this, i);
            if (itemstack.count <= 0) {
               this.die();
            }
         }
      }
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.getCustomName() : LocaleI18n.get("item." + this.getItemStack().a());
   }

   @Override
   public boolean aD() {
      return false;
   }

   @Override
   public void c(int i) {
      super.c(i);
      ItemStack itemStack = this.getItemStack();
      if (itemStack != null) {
         this.w(itemStack);
      }
   }

   public ItemStack getItemStack() {
      ItemStack itemstack = this.getDataWatcher().getItemStack(10);
      if (itemstack == null) {
         if (this.world != null) {
            b.error("Item entity {} has no item?!", new Object[]{this.getId()});
         }

         return new ItemStack(Blocks.STONE);
      } else {
         return itemstack;
      }
   }

   public void setItemStack(ItemStack itemstack) {
      this.getDataWatcher().watch(10, itemstack);
      this.getDataWatcher().update(10);
   }

   public String m() {
      return this.g;
   }

   public void b(String s) {
      this.g = s;
   }

   public String n() {
      return this.f;
   }

   public void c(String s) {
      this.f = s;
   }

   public void p() {
      this.pickupDelay = 10;
   }

   public void q() {
      this.pickupDelay = 0;
   }

   public void r() {
      this.pickupDelay = 32767;
   }

   public void a(int i) {
      this.pickupDelay = i;
   }

   public boolean s() {
      return this.pickupDelay > 0;
   }

   public void u() {
      this.age = -6000;
   }

   public void v() {
      this.r();
      this.age = 5999;
   }
}
