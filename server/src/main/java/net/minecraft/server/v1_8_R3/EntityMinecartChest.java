package net.minecraft.server.v1_8_R3;

public class EntityMinecartChest extends EntityMinecartContainer {
   public EntityMinecartChest(World var1) {
      super(var1);
   }

   public EntityMinecartChest(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   @Override
   public void a(DamageSource var1) {
      super.a(var1);
      if (this.world.getGameRules().getBoolean("doEntityDrops")) {
         this.a(Item.getItemOf(Blocks.CHEST), 1, 0.0F);
      }
   }

   @Override
   public int getSize() {
      return 27;
   }

   @Override
   public EntityMinecartAbstract.EnumMinecartType s() {
      return EntityMinecartAbstract.EnumMinecartType.CHEST;
   }

   @Override
   public IBlockData u() {
      return Blocks.CHEST.getBlockData().set(BlockChest.FACING, EnumDirection.NORTH);
   }

   @Override
   public int w() {
      return 8;
   }

   @Override
   public String getContainerName() {
      return "minecraft:chest";
   }

   @Override
   public Container createContainer(PlayerInventory var1, EntityHuman var2) {
      return new ContainerChest(var1, this, var2);
   }
}
