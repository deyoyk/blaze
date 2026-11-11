package net.minecraft.server.v1_8_R3;

public class ChunkCache implements IBlockAccess {
   protected int a;
   protected int b;
   protected Chunk[][] c;
   protected boolean d;
   protected World e;

   public ChunkCache(World world, BlockPosition blockposition, BlockPosition blockposition1, int i) {
      this.e = world;
      this.a = blockposition.getX() - i >> 4;
      this.b = blockposition.getZ() - i >> 4;
      int j = blockposition1.getX() + i >> 4;
      int k = blockposition1.getZ() + i >> 4;
      this.c = new Chunk[j - this.a + 1][k - this.b + 1];
      this.d = true;

      for (int l = this.a; l <= j; l++) {
         for (int i1 = this.b; i1 <= k; i1++) {
            this.c[l - this.a][i1 - this.b] = world.getChunkAt(l, i1);
         }
      }

      for (int var10 = blockposition.getX() >> 4; var10 <= blockposition1.getX() >> 4; var10++) {
         for (int i1 = blockposition.getZ() >> 4; i1 <= blockposition1.getZ() >> 4; i1++) {
            Chunk chunk = this.c[var10 - this.a][i1 - this.b];
            if (chunk != null && !chunk.c(blockposition.getY(), blockposition1.getY())) {
               this.d = false;
            }
         }
      }
   }

   @Override
   public TileEntity getTileEntity(BlockPosition blockposition) {
      return this.c[(blockposition.getX() >> 4) - this.a][(blockposition.getZ() >> 4) - this.b].a(blockposition, Chunk.EnumTileEntityState.IMMEDIATE);
   }

   @Override
   public IBlockData getType(BlockPosition blockposition) {
      return this.getType(blockposition.getX(), blockposition.getY(), blockposition.getZ());
   }

   @Override
   public IBlockData getType(int x, int y, int z) {
      if (y >= 0 && y < 256) {
         int i = (x >> 4) - this.a;
         int j = (z >> 4) - this.b;
         if (i >= 0 && i < this.c.length && j >= 0) {
            Chunk[] chunks = this.c[i];
            if (j < chunks.length) {
               Chunk chunk = chunks[j];
               if (chunk != null) {
                  ChunkSection section = chunk.getSections()[y >> 4];
                  if (section != null) {
                     return section.getType(x & 15, y & 15, z & 15);
                  }
               }
            }
         }
      }

      return Blocks.AIR.getBlockData();
   }

   @Override
   public boolean isEmpty(BlockPosition blockposition) {
      return this.getType(blockposition).getBlock().getMaterial() == Material.AIR;
   }

   @Override
   public int getBlockPower(BlockPosition blockposition, EnumDirection enumdirection) {
      IBlockData iblockdata = this.getType(blockposition);
      return iblockdata.getBlock().b(this, blockposition, iblockdata, enumdirection);
   }
}
