package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.EnchantConfig;

public class EnchantmentWeaponDamage extends Enchantment {
   private static final String[] E = new String[]{"all", "undead", "arthropods"};
   private static final int[] F = new int[]{1, 5, 5};
   private static final int[] G = new int[]{11, 8, 8};
   private static final int[] H = new int[]{20, 20, 20};
   public final int a;

   public EnchantmentWeaponDamage(int var1, MinecraftKey var2, int var3, int var4) {
      super(var1, var2, var3, EnchantmentSlotType.WEAPON);
      this.a = var4;
   }

   @Override
   public int a(int var1) {
      return F[this.a] + (var1 - 1) * G[this.a];
   }

   @Override
   public int b(int var1) {
      return this.a(var1) + H[this.a];
   }

   @Override
   public int getMaxLevel() {
      return Math.max(5, this.getEnchantLimit());
   }

   @Override
   public int getEnchantLimit() {
      String var1;
      switch ((var1 = E[this.a]).hashCode()) {
         case -840690051:
            if (var1.equals("undead")) {
               return (Integer)EnchantConfig.smite.getValue();
            }
            break;
         case -290000592:
            if (var1.equals("arthropods")) {
               return (Integer)EnchantConfig.baneOfArthropods.getValue();
            }
            break;
         case 96673:
            if (var1.equals("all")) {
               return (Integer)EnchantConfig.sharpness.getValue();
            }
      }

      return -1;
   }

   @Override
   public float a(int var1, EnumMonsterType var2) {
      if (this.a == 0) {
         return var1 * 1.25F;
      } else if (this.a == 1 && var2 == EnumMonsterType.UNDEAD) {
         return var1 * 2.5F;
      } else {
         return this.a == 2 && var2 == EnumMonsterType.ARTHROPOD ? var1 * 2.5F : 0.0F;
      }
   }

   @Override
   public String a() {
      return "enchantment.damage." + E[this.a];
   }

   @Override
   public boolean a(Enchantment var1) {
      return !(var1 instanceof EnchantmentWeaponDamage);
   }

   @Override
   public boolean canEnchant(ItemStack var1) {
      return var1.getItem() instanceof ItemAxe || super.canEnchant(var1);
   }

   @Override
   public void a(EntityLiving var1, Entity var2, int var3) {
      if (var2 instanceof EntityLiving) {
         EntityLiving var4 = (EntityLiving)var2;
         if (this.a == 2 && var4.getMonsterType() == EnumMonsterType.ARTHROPOD) {
            int var5 = 20 + var1.bc().nextInt(10 * var3);
            var4.addEffect(new MobEffect(MobEffectList.SLOWER_MOVEMENT.id, var5, 3));
         }
      }
   }
}
