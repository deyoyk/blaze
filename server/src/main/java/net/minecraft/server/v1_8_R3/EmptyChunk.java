package net.minecraft.server.v1_8_R3;

import com.google.common.base.Predicate;
import java.util.List;
import java.util.Random;

public class EmptyChunk extends Chunk {
   public EmptyChunk(World var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   @Override
   public boolean a(int var1, int var2) {
      return var1 == this.locX && var2 == this.locZ;
   }

   @Override
   public int b(int var1, int var2) {
      return 0;
   }

   @Override
   public void initLighting() {
   }

   @Override
   public Block getType(BlockPosition var1) {
      return Blocks.AIR;
   }

   @Override
   public int b(BlockPosition var1) {
      return 255;
   }

   @Override
   public int c(BlockPosition var1) {
      return 0;
   }

   @Override
   public int getBrightness(EnumSkyBlock var1, BlockPosition var2) {
      return var1.c;
   }

   @Override
   public void a(EnumSkyBlock var1, BlockPosition var2, int var3) {
   }

   @Override
   public int a(BlockPosition var1, int var2) {
      return 0;
   }

   @Override
   public void a(Entity var1) {
   }

   @Override
   public void b(Entity var1) {
   }

   @Override
   public void a(Entity var1, int var2) {
   }

   @Override
   public boolean d(BlockPosition var1) {
      return false;
   }

   @Override
   public TileEntity a(BlockPosition var1, Chunk.EnumTileEntityState var2) {
      return null;
   }

   @Override
   public void a(TileEntity var1) {
   }

   @Override
   public void a(BlockPosition var1, TileEntity var2) {
   }

   @Override
   public void e(BlockPosition var1) {
   }

   @Override
   public void addEntities() {
   }

   @Override
   public void removeEntities() {
   }

   @Override
   public void e() {
   }

   @Override
   public void a(Entity var1, AxisAlignedBB var2, List<Entity> var3, Predicate<? super Entity> var4) {
   }

   @Override
   public <T extends Entity> void a(Class<? extends T> var1, AxisAlignedBB var2, List<T> var3, Predicate<? super T> var4) {
   }

   @Override
   public boolean a(boolean var1) {
      return false;
   }

   @Override
   public Random a(long var1) {
      return new Random(
         this.getWorld().getSeed() + this.locX * this.locX * 4987142 + this.locX * 5947611 + this.locZ * this.locZ * 4392871L + this.locZ * 389711 ^ var1
      );
   }

   @Override
   public boolean isEmpty() {
      return true;
   }

   @Override
   public boolean c(int var1, int var2) {
      return true;
   }
}
