package org.bukkit.material;

import org.bukkit.inventory.ItemStack;

public class MaterialData implements Cloneable {
   private final int type;
   private byte data = 0;

   @Deprecated
   public MaterialData(int type) {
      this(type, (byte)0);
   }

   public MaterialData(org.bukkit.Material type) {
      this(type, (byte)0);
   }

   @Deprecated
   public MaterialData(int type, byte data) {
      this.type = type;
      this.data = data;
   }

   @Deprecated
   public MaterialData(org.bukkit.Material type, byte data) {
      this(type.getId(), data);
   }

   @Deprecated
   public byte getData() {
      return this.data;
   }

   @Deprecated
   public void setData(byte data) {
      this.data = data;
   }

   public org.bukkit.Material getItemType() {
      return org.bukkit.Material.getMaterial(this.type);
   }

   @Deprecated
   public int getItemTypeId() {
      return this.type;
   }

   public ItemStack toItemStack() {
      return new ItemStack(this.type, 0, this.data);
   }

   public ItemStack toItemStack(int amount) {
      return new ItemStack(this.type, amount, this.data);
   }

   @Override
   public String toString() {
      return this.getItemType() + "(" + this.getData() + ")";
   }

   @Override
   public int hashCode() {
      return this.getItemTypeId() << 8 ^ this.getData();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj != null && obj instanceof MaterialData) {
         MaterialData md = (MaterialData)obj;
         return md.getItemTypeId() == this.getItemTypeId() && md.getData() == this.getData();
      } else {
         return false;
      }
   }

   public MaterialData clone() {
      try {
         return (MaterialData)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new Error(var2);
      }
   }
}
