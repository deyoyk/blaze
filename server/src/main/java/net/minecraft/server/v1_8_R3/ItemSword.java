package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Multimap;

public class ItemSword extends Item {
   private float a;
   private final Item.EnumToolMaterial b;

   public ItemSword(Item.EnumToolMaterial item_enumtoolmaterial) {
      this.b = item_enumtoolmaterial;
      this.maxStackSize = 1;
      this.setMaxDurability(item_enumtoolmaterial.a());
      this.a(CreativeModeTab.j);
      this.a = 4.0F + item_enumtoolmaterial.c();
   }

   public float g() {
      return this.b.c();
   }

   @Override
   public float getDestroySpeed(ItemStack itemstack, Block block) {
      if (block == Blocks.WEB) {
         return 15.0F;
      } else {
         Material material = block.getMaterial();
         return material != Material.PLANT
               && material != Material.REPLACEABLE_PLANT
               && material != Material.CORAL
               && material != Material.LEAVES
               && material != Material.PUMPKIN
            ? 1.0F
            : 1.5F;
      }
   }

   @Override
   public boolean a(ItemStack itemstack, EntityLiving entityliving, EntityLiving entityliving1) {
      itemstack.damage(1, entityliving1);
      return true;
   }

   @Override
   public boolean a(ItemStack itemstack, World world, Block block, BlockPosition blockposition, EntityLiving entityliving) {
      if (block.g(world, blockposition) != 0.0) {
         itemstack.damage(2, entityliving);
      }

      return true;
   }

   @Override
   public EnumAnimation e(ItemStack itemstack) {
      return EnumAnimation.BLOCK;
   }

   @Override
   public int d(ItemStack itemstack) {
      return 72000;
   }

   @Override
   public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
      entityhuman.a(itemstack, this.d(itemstack));
      return itemstack;
   }

   @Override
   public boolean canDestroySpecialBlock(Block block) {
      return block == Blocks.WEB;
   }

   @Override
   public int b() {
      return this.b.e();
   }

   public String h() {
      return this.b.toString();
   }

   @Override
   public boolean a(ItemStack itemstack, ItemStack itemstack1) {
      return this.b.f() == itemstack1.getItem() ? true : super.a(itemstack, itemstack1);
   }

   @Override
   public Multimap<String, AttributeModifier> i() {
      Multimap multimap = super.i();
      multimap.put(GenericAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(f, "Weapon modifier", this.a, 0));
      return multimap;
   }
}
