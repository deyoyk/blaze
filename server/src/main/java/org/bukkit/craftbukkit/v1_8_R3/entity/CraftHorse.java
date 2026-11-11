package org.bukkit.craftbukkit.v1_8_R3.entity;

import com.eatthepath.uuid.FastUUID;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.EntityHorse;
import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryHorse;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.HorseInventory;

public class CraftHorse extends CraftAnimals implements Horse {
   public CraftHorse(CraftServer server, EntityHorse entity) {
      super(server, entity);
   }

   public EntityHorse getHandle() {
      return (EntityHorse)this.entity;
   }

   @Override
   public Horse.Variant getVariant() {
      return Horse.Variant.values()[this.getHandle().getType()];
   }

   @Override
   public void setVariant(Horse.Variant variant) {
      Validate.notNull(variant, "Variant cannot be null");
      this.getHandle().setType(variant.ordinal());
   }

   @Override
   public Horse.Color getColor() {
      return Horse.Color.values()[this.getHandle().getVariant() & 0xFF];
   }

   @Override
   public void setColor(Horse.Color color) {
      Validate.notNull(color, "Color cannot be null");
      this.getHandle().setVariant(color.ordinal() & 0xFF | this.getStyle().ordinal() << 8);
   }

   @Override
   public Horse.Style getStyle() {
      return Horse.Style.values()[this.getHandle().getVariant() >>> 8];
   }

   @Override
   public void setStyle(Horse.Style style) {
      Validate.notNull(style, "Style cannot be null");
      this.getHandle().setVariant(this.getColor().ordinal() & 0xFF | style.ordinal() << 8);
   }

   @Override
   public boolean isCarryingChest() {
      return this.getHandle().hasChest();
   }

   @Override
   public void setCarryingChest(boolean chest) {
      if (chest != this.isCarryingChest()) {
         this.getHandle().setHasChest(chest);
         this.getHandle().loadChest();
      }
   }

   @Override
   public int getDomestication() {
      return this.getHandle().getTemper();
   }

   @Override
   public void setDomestication(int value) {
      Validate.isTrue(value >= 0, "Domestication cannot be less than zero");
      Validate.isTrue(value <= this.getMaxDomestication(), "Domestication cannot be greater than the max domestication");
      this.getHandle().setTemper(value);
   }

   @Override
   public int getMaxDomestication() {
      return this.getHandle().getMaxDomestication();
   }

   @Override
   public void setMaxDomestication(int value) {
      Validate.isTrue(value > 0, "Max domestication cannot be zero or less");
      this.getHandle().maxDomestication = value;
   }

   @Override
   public double getJumpStrength() {
      return this.getHandle().getJumpStrength();
   }

   @Override
   public void setJumpStrength(double strength) {
      Validate.isTrue(strength >= 0.0, "Jump strength cannot be less than zero");
      this.getHandle().getAttributeInstance(EntityHorse.attributeJumpStrength).setValue(strength);
   }

   @Override
   public boolean isTamed() {
      return this.getHandle().isTame();
   }

   @Override
   public void setTamed(boolean tamed) {
      this.getHandle().setTame(tamed);
   }

   @Override
   public AnimalTamer getOwner() {
      return this.getOwnerUUID() == null ? null : this.getServer().getOfflinePlayer(this.getOwnerUUID());
   }

   @Override
   public void setOwner(AnimalTamer owner) {
      if (owner != null) {
         this.setTamed(true);
         this.getHandle().setGoalTarget(null, null, false);
         this.setOwnerUUID(owner.getUniqueId());
      } else {
         this.setTamed(false);
         this.setOwnerUUID(null);
      }
   }

   public UUID getOwnerUUID() {
      try {
         return FastUUID.parseUUID(this.getHandle().getOwnerUUID());
      } catch (IllegalArgumentException var2) {
         return null;
      }
   }

   public void setOwnerUUID(UUID uuid) {
      if (uuid == null) {
         this.getHandle().setOwnerUUID("");
      } else {
         this.getHandle().setOwnerUUID(FastUUID.toString(uuid));
      }
   }

   @Override
   public HorseInventory getInventory() {
      return new CraftInventoryHorse(this.getHandle().inventoryChest);
   }

   @Override
   public String toString() {
      return "CraftHorse{variant=" + this.getVariant() + ", owner=" + this.getOwner() + '}';
   }

   @Override
   public EntityType getType() {
      return EntityType.HORSE;
   }
}
