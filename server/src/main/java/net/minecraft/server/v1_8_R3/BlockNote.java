package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import java.util.List;
import net.jafama.FastMath;

public class BlockNote extends BlockContainer {
   private static final List<String> a = Lists.newArrayList(new String[]{"harp", "bd", "snare", "hat", "bassattack"});

   public BlockNote() {
      super(Material.WOOD);
      this.a(CreativeModeTab.d);
   }

   @Override
   public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
      boolean flag = world.isBlockIndirectlyPowered(blockposition);
      TileEntity tileentity = world.getTileEntity(blockposition);
      if (tileentity instanceof TileEntityNote) {
         TileEntityNote tileentitynote = (TileEntityNote)tileentity;
         if (tileentitynote.f != flag) {
            if (flag) {
               tileentitynote.play(world, blockposition);
            }

            tileentitynote.f = flag;
         }
      }
   }

   @Override
   public boolean interact(
      World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2
   ) {
      TileEntity tileentity = world.getTileEntity(blockposition);
      if (tileentity instanceof TileEntityNote) {
         TileEntityNote tileentitynote = (TileEntityNote)tileentity;
         tileentitynote.b();
         tileentitynote.play(world, blockposition);
      }

      return true;
   }

   @Override
   public void attack(World world, BlockPosition blockposition, EntityHuman entityhuman) {
      TileEntity tileentity = world.getTileEntity(blockposition);
      if (tileentity instanceof TileEntityNote) {
         ((TileEntityNote)tileentity).play(world, blockposition);
      }
   }

   @Override
   public TileEntity a(World world, int i) {
      return new TileEntityNote();
   }

   private String b(int i) {
      if (i < 0 || i >= a.size()) {
         i = 0;
      }

      return a.get(i);
   }

   @Override
   public boolean a(World world, BlockPosition blockposition, IBlockData iblockdata, int i, int j) {
      float f = (float)FastMath.pow(2.0, (j - 12) / 12.0);
      world.makeSound(blockposition.getX() + 0.5, blockposition.getY() + 0.5, blockposition.getZ() + 0.5, "note." + this.b(i), 3.0F, f);
      world.addParticle(
         EnumParticle.NOTE, blockposition.getX() + 0.5, blockposition.getY() + 1.2, blockposition.getZ() + 0.5, j / 24.0, 0.0, 0.0, EnumParticle.EMPTY_ARRAY
      );
      return true;
   }

   @Override
   public int b() {
      return 3;
   }
}
