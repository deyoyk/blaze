package net.minecraft.server.v1_8_R3;

public class PathfinderGoalJumpOnBlock extends PathfinderGoalGotoTarget {
   private final EntityOcelot c;

   public PathfinderGoalJumpOnBlock(EntityOcelot entityocelot, double d0) {
      super(entityocelot, d0, 8);
      this.c = entityocelot;
   }

   @Override
   public boolean a() {
      return this.c.isTamed() && !this.c.isSitting() && super.a();
   }

   @Override
   public boolean b() {
      return super.b();
   }

   @Override
   public void c() {
      super.c();
      this.c.getGoalSit().setSitting(false);
   }

   @Override
   public void d() {
      super.d();
      this.c.setSitting(false);
   }

   @Override
   public void e() {
      super.e();
      this.c.getGoalSit().setSitting(false);
      if (!this.f()) {
         this.c.setSitting(false);
      } else if (!this.c.isSitting()) {
         this.c.setSitting(true);
      }
   }

   @Override
   protected boolean a(World world, BlockPosition blockposition) {
      if (!world.isEmpty(blockposition.up())) {
         return false;
      } else {
         IBlockData iblockdata = world.getType(blockposition);
         Block block = iblockdata.getBlock();
         if (block == Blocks.CHEST) {
            TileEntity tileentity = world.getTileEntity(blockposition);
            return tileentity instanceof TileEntityChest && ((TileEntityChest)tileentity).l < 1;
         } else {
            return block == Blocks.LIT_FURNACE ? true : block == Blocks.BED && iblockdata.get(BlockBed.PART) != BlockBed.EnumBedPart.HEAD;
         }
      }
   }
}
