package net.minecraft.server.v1_8_R3;

import java.util.Random;
import net.jafama.FastMath;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;

public class BlockDaylightDetector extends BlockContainer {
   public static final BlockStateInteger POWER = BlockStateInteger.of("power", 0, 15);
   private final boolean b;

   public BlockDaylightDetector(boolean flag) {
      super(Material.WOOD);
      this.b = flag;
      this.j(this.blockStateList.getBlockData().set(POWER, 0));
      this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F);
      this.a(CreativeModeTab.d);
      this.c(0.2F);
      this.a(f);
      this.c("daylightDetector");
   }

   @Override
   public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
      this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F);
   }

   @Override
   public int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
      return iblockdata.get(POWER);
   }

   public void f(World world, BlockPosition blockposition) {
      if (!world.worldProvider.o()) {
         IBlockData iblockdata = world.getType(blockposition);
         int i = world.b(EnumSkyBlock.SKY, blockposition) - world.ab();
         float f = world.d(1.0F);
         float f1 = f < (float) Math.PI ? 0.0F : (float) (Math.PI * 2);
         f += (f1 - f) * 0.2F;
         i = FastMath.round(i * MathHelper.cos(f));
         i = MathHelper.clamp(i, 0, 15);
         if (this.b) {
            i = 15 - i;
         }

         if (iblockdata.get(POWER) != i) {
            i = CraftEventFactory.callRedstoneChange(world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), iblockdata.get(POWER), i)
               .getNewCurrent();
            world.setTypeAndData(blockposition, iblockdata.set(POWER, i), 3);
         }
      }
   }

   @Override
   public boolean interact(
      World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2
   ) {
      if (entityhuman.cn()) {
         if (this.b) {
            world.setTypeAndData(blockposition, Blocks.DAYLIGHT_DETECTOR.getBlockData().set(POWER, iblockdata.get(POWER)), 4);
            Blocks.DAYLIGHT_DETECTOR.f(world, blockposition);
         } else {
            world.setTypeAndData(blockposition, Blocks.DAYLIGHT_DETECTOR_INVERTED.getBlockData().set(POWER, iblockdata.get(POWER)), 4);
            Blocks.DAYLIGHT_DETECTOR_INVERTED.f(world, blockposition);
         }

         return true;
      } else {
         return super.interact(world, blockposition, iblockdata, entityhuman, enumdirection, f, f1, f2);
      }
   }

   @Override
   public Item getDropType(IBlockData iblockdata, Random random, int i) {
      return Item.getItemOf(Blocks.DAYLIGHT_DETECTOR);
   }

   @Override
   public boolean d() {
      return false;
   }

   @Override
   public boolean c() {
      return false;
   }

   @Override
   public int b() {
      return 3;
   }

   @Override
   public boolean isPowerSource() {
      return true;
   }

   @Override
   public TileEntity a(World world, int i) {
      return new TileEntityLightDetector();
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(POWER, i);
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      return iblockdata.get(POWER);
   }

   @Override
   public BlockStateList getStateList() {
      return new BlockStateList(this, POWER);
   }
}
