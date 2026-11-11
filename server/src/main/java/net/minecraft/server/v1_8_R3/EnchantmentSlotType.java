package net.minecraft.server.v1_8_R3;

public enum EnchantmentSlotType {
   ALL,
   ARMOR,
   ARMOR_FEET,
   ARMOR_LEGS,
   ARMOR_TORSO,
   ARMOR_HEAD,
   WEAPON,
   DIGGER,
   FISHING_ROD,
   BREAKABLE,
   BOW;

   public boolean canEnchant(Item var1) {
      if (this == ALL) {
         return true;
      } else if (this == BREAKABLE && var1.usesDurability()) {
         return true;
      } else if (var1 instanceof ItemArmor) {
         if (this == ARMOR) {
            return true;
         } else {
            ItemArmor var2 = (ItemArmor)var1;
            if (var2.b == 0) {
               return this == ARMOR_HEAD;
            } else if (var2.b == 2) {
               return this == ARMOR_LEGS;
            } else if (var2.b == 1) {
               return this == ARMOR_TORSO;
            } else {
               return var2.b == 3 ? this == ARMOR_FEET : false;
            }
         }
      } else if (var1 instanceof ItemSword) {
         return this == WEAPON;
      } else if (var1 instanceof ItemTool) {
         return this == DIGGER;
      } else if (var1 instanceof ItemBow) {
         return this == BOW;
      } else {
         return var1 instanceof ItemFishingRod ? this == FISHING_ROD : false;
      }
   }
}
