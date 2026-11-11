package net.minecraft.server.v1_8_R3;

import java.util.List;
import java.util.Random;

public abstract class StructurePiece {
   protected StructureBoundingBox l;
   protected EnumDirection m;
   protected int n;

   public StructurePiece() {
   }

   protected StructurePiece(int i) {
      this.n = i;
   }

   public NBTTagCompound b() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.setString("id", WorldGenFactory.a(this));
      nbttagcompound.set("BB", this.l.g());
      nbttagcompound.setInt("O", this.m == null ? -1 : this.m.b());
      nbttagcompound.setInt("GD", this.n);
      this.a(nbttagcompound);
      return nbttagcompound;
   }

   protected abstract void a(NBTTagCompound var1);

   public void a(World world, NBTTagCompound nbttagcompound) {
      if (nbttagcompound.hasKey("BB")) {
         this.l = new StructureBoundingBox(nbttagcompound.getIntArray("BB"));
      }

      int i = nbttagcompound.getInt("O");
      this.m = i == -1 ? null : EnumDirection.fromType2(i);
      this.n = nbttagcompound.getInt("GD");
      this.b(nbttagcompound);
   }

   protected abstract void b(NBTTagCompound var1);

   public void a(StructurePiece structurepiece, List<StructurePiece> list, Random random) {
   }

   public abstract boolean a(World var1, Random var2, StructureBoundingBox var3);

   public StructureBoundingBox c() {
      return this.l;
   }

   public int d() {
      return this.n;
   }

   public static StructurePiece a(List<StructurePiece> list, StructureBoundingBox structureboundingbox) {
      for (StructurePiece structurepiece : list) {
         if (structurepiece.c() != null && structurepiece.c().a(structureboundingbox)) {
            return structurepiece;
         }
      }

      return null;
   }

   public BlockPosition a() {
      return new BlockPosition(this.l.f());
   }

   protected boolean a(World world, StructureBoundingBox structureboundingbox) {
      int i = Math.max(this.l.a - 1, structureboundingbox.a);
      int j = Math.max(this.l.b - 1, structureboundingbox.b);
      int k = Math.max(this.l.c - 1, structureboundingbox.c);
      int l = Math.min(this.l.d + 1, structureboundingbox.d);
      int i1 = Math.min(this.l.e + 1, structureboundingbox.e);
      int j1 = Math.min(this.l.f + 1, structureboundingbox.f);

      for (int k1 = i; k1 <= l; k1++) {
         for (int l1 = k; l1 <= j1; l1++) {
            if (world.getType(k1, j, l1).getBlock().getMaterial().isLiquid() || world.getType(k1, i1, l1).getBlock().getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      for (int var11 = i; var11 <= l; var11++) {
         for (int l1x = j; l1x <= i1; l1x++) {
            if (world.getType(var11, l1x, k).getBlock().getMaterial().isLiquid() || world.getType(var11, l1x, j1).getBlock().getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      for (int var12 = k; var12 <= j1; var12++) {
         for (int l1xx = j; l1xx <= i1; l1xx++) {
            if (world.getType(i, l1xx, var12).getBlock().getMaterial().isLiquid() || world.getType(l, l1xx, var12).getBlock().getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      return false;
   }

   protected int a(int i, int j) {
      if (this.m == null) {
         return i;
      } else {
         switch (StructurePiece.SyntheticClass_1.a[this.m.ordinal()]) {
            case 1:
            case 2:
               return this.l.a + i;
            case 3:
               return this.l.d - j;
            case 4:
               return this.l.a + j;
            default:
               return i;
         }
      }
   }

   protected int d(int i) {
      return this.m == null ? i : i + this.l.b;
   }

   protected int b(int i, int j) {
      if (this.m == null) {
         return j;
      } else {
         switch (StructurePiece.SyntheticClass_1.a[this.m.ordinal()]) {
            case 1:
               return this.l.f - j;
            case 2:
               return this.l.c + j;
            case 3:
            case 4:
               return this.l.c + i;
            default:
               return j;
         }
      }
   }

   protected int a(Block block, int i) {
      if (block == Blocks.RAIL) {
         if (this.m == EnumDirection.WEST || this.m == EnumDirection.EAST) {
            if (i == 1) {
               return 0;
            }

            return 1;
         }
      } else if (block instanceof BlockDoor) {
         if (this.m == EnumDirection.SOUTH) {
            if (i == 0) {
               return 2;
            }

            if (i == 2) {
               return 0;
            }
         } else {
            if (this.m == EnumDirection.WEST) {
               return i + 1 & 3;
            }

            if (this.m == EnumDirection.EAST) {
               return i + 3 & 3;
            }
         }
      } else if (block != Blocks.STONE_STAIRS
         && block != Blocks.OAK_STAIRS
         && block != Blocks.NETHER_BRICK_STAIRS
         && block != Blocks.STONE_BRICK_STAIRS
         && block != Blocks.SANDSTONE_STAIRS) {
         if (block == Blocks.LADDER) {
            if (this.m == EnumDirection.SOUTH) {
               if (i == EnumDirection.NORTH.a()) {
                  return EnumDirection.SOUTH.a();
               }

               if (i == EnumDirection.SOUTH.a()) {
                  return EnumDirection.NORTH.a();
               }
            } else if (this.m == EnumDirection.WEST) {
               if (i == EnumDirection.NORTH.a()) {
                  return EnumDirection.WEST.a();
               }

               if (i == EnumDirection.SOUTH.a()) {
                  return EnumDirection.EAST.a();
               }

               if (i == EnumDirection.WEST.a()) {
                  return EnumDirection.NORTH.a();
               }

               if (i == EnumDirection.EAST.a()) {
                  return EnumDirection.SOUTH.a();
               }
            } else if (this.m == EnumDirection.EAST) {
               if (i == EnumDirection.NORTH.a()) {
                  return EnumDirection.EAST.a();
               }

               if (i == EnumDirection.SOUTH.a()) {
                  return EnumDirection.WEST.a();
               }

               if (i == EnumDirection.WEST.a()) {
                  return EnumDirection.NORTH.a();
               }

               if (i == EnumDirection.EAST.a()) {
                  return EnumDirection.SOUTH.a();
               }
            }
         } else if (block == Blocks.STONE_BUTTON) {
            if (this.m == EnumDirection.SOUTH) {
               if (i == 3) {
                  return 4;
               }

               if (i == 4) {
                  return 3;
               }
            } else if (this.m == EnumDirection.WEST) {
               if (i == 3) {
                  return 1;
               }

               if (i == 4) {
                  return 2;
               }

               if (i == 2) {
                  return 3;
               }

               if (i == 1) {
                  return 4;
               }
            } else if (this.m == EnumDirection.EAST) {
               if (i == 3) {
                  return 2;
               }

               if (i == 4) {
                  return 1;
               }

               if (i == 2) {
                  return 3;
               }

               if (i == 1) {
                  return 4;
               }
            }
         } else if (block == Blocks.TRIPWIRE_HOOK || block instanceof BlockDirectional) {
            EnumDirection enumdirection = EnumDirection.fromType2(i);
            if (this.m == EnumDirection.SOUTH) {
               if (enumdirection == EnumDirection.SOUTH || enumdirection == EnumDirection.NORTH) {
                  return enumdirection.opposite().b();
               }
            } else if (this.m == EnumDirection.WEST) {
               if (enumdirection == EnumDirection.NORTH) {
                  return EnumDirection.WEST.b();
               }

               if (enumdirection == EnumDirection.SOUTH) {
                  return EnumDirection.EAST.b();
               }

               if (enumdirection == EnumDirection.WEST) {
                  return EnumDirection.NORTH.b();
               }

               if (enumdirection == EnumDirection.EAST) {
                  return EnumDirection.SOUTH.b();
               }
            } else if (this.m == EnumDirection.EAST) {
               if (enumdirection == EnumDirection.NORTH) {
                  return EnumDirection.EAST.b();
               }

               if (enumdirection == EnumDirection.SOUTH) {
                  return EnumDirection.WEST.b();
               }

               if (enumdirection == EnumDirection.WEST) {
                  return EnumDirection.NORTH.b();
               }

               if (enumdirection == EnumDirection.EAST) {
                  return EnumDirection.SOUTH.b();
               }
            }
         } else if (block == Blocks.PISTON || block == Blocks.STICKY_PISTON || block == Blocks.LEVER || block == Blocks.DISPENSER) {
            if (this.m == EnumDirection.SOUTH) {
               if (i == EnumDirection.NORTH.a() || i == EnumDirection.SOUTH.a()) {
                  return EnumDirection.fromType1(i).opposite().a();
               }
            } else if (this.m == EnumDirection.WEST) {
               if (i == EnumDirection.NORTH.a()) {
                  return EnumDirection.WEST.a();
               }

               if (i == EnumDirection.SOUTH.a()) {
                  return EnumDirection.EAST.a();
               }

               if (i == EnumDirection.WEST.a()) {
                  return EnumDirection.NORTH.a();
               }

               if (i == EnumDirection.EAST.a()) {
                  return EnumDirection.SOUTH.a();
               }
            } else if (this.m == EnumDirection.EAST) {
               if (i == EnumDirection.NORTH.a()) {
                  return EnumDirection.EAST.a();
               }

               if (i == EnumDirection.SOUTH.a()) {
                  return EnumDirection.WEST.a();
               }

               if (i == EnumDirection.WEST.a()) {
                  return EnumDirection.NORTH.a();
               }

               if (i == EnumDirection.EAST.a()) {
                  return EnumDirection.SOUTH.a();
               }
            }
         }
      } else if (this.m == EnumDirection.SOUTH) {
         if (i == 2) {
            return 3;
         }

         if (i == 3) {
            return 2;
         }
      } else if (this.m == EnumDirection.WEST) {
         if (i == 0) {
            return 2;
         }

         if (i == 1) {
            return 3;
         }

         if (i == 2) {
            return 0;
         }

         if (i == 3) {
            return 1;
         }
      } else if (this.m == EnumDirection.EAST) {
         if (i == 0) {
            return 2;
         }

         if (i == 1) {
            return 3;
         }

         if (i == 2) {
            return 1;
         }

         if (i == 3) {
            return 0;
         }
      }

      return i;
   }

   protected void a(World world, IBlockData iblockdata, int i, int j, int k, StructureBoundingBox structureboundingbox) {
      int x = this.a(i, k);
      int y = this.d(j);
      int z = this.b(i, k);
      if (structureboundingbox.b(x, y, z)) {
         world.setTypeAndData(new BlockPosition(x, y, z), iblockdata, 2);
      }
   }

   protected IBlockData a(World world, int i, int j, int k, StructureBoundingBox structureboundingbox) {
      int l = this.a(i, k);
      int i1 = this.d(j);
      int j1 = this.b(i, k);
      return !structureboundingbox.b(l, i1, j1) ? Blocks.AIR.getBlockData() : world.getType(l, i1, j1);
   }

   protected void a(World world, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, int j1) {
      for (int k1 = j; k1 <= i1; k1++) {
         for (int l1 = i; l1 <= l; l1++) {
            for (int i2 = k; i2 <= j1; i2++) {
               this.a(world, Blocks.AIR.getBlockData(), l1, k1, i2, structureboundingbox);
            }
         }
      }
   }

   protected void a(
      World world,
      StructureBoundingBox structureboundingbox,
      int i,
      int j,
      int k,
      int l,
      int i1,
      int j1,
      IBlockData iblockdata,
      IBlockData iblockdata1,
      boolean flag
   ) {
      for (int k1 = j; k1 <= i1; k1++) {
         for (int l1 = i; l1 <= l; l1++) {
            for (int i2 = k; i2 <= j1; i2++) {
               if (!flag || this.a(world, l1, k1, i2, structureboundingbox).getBlock().getMaterial() != Material.AIR) {
                  if (k1 != j && k1 != i1 && l1 != i && l1 != l && i2 != k && i2 != j1) {
                     this.a(world, iblockdata1, l1, k1, i2, structureboundingbox);
                  } else {
                     this.a(world, iblockdata, l1, k1, i2, structureboundingbox);
                  }
               }
            }
         }
      }
   }

   protected void a(
      World world,
      StructureBoundingBox structureboundingbox,
      int i,
      int j,
      int k,
      int l,
      int i1,
      int j1,
      boolean flag,
      Random random,
      StructurePiece.StructurePieceBlockSelector structurepiece_structurepieceblockselector
   ) {
      for (int k1 = j; k1 <= i1; k1++) {
         for (int l1 = i; l1 <= l; l1++) {
            for (int i2 = k; i2 <= j1; i2++) {
               if (!flag || this.a(world, l1, k1, i2, structureboundingbox).getBlock().getMaterial() != Material.AIR) {
                  structurepiece_structurepieceblockselector.a(random, l1, k1, i2, k1 == j || k1 == i1 || l1 == i || l1 == l || i2 == k || i2 == j1);
                  this.a(world, structurepiece_structurepieceblockselector.a(), l1, k1, i2, structureboundingbox);
               }
            }
         }
      }
   }

   protected void a(
      World world,
      StructureBoundingBox structureboundingbox,
      Random random,
      float f,
      int i,
      int j,
      int k,
      int l,
      int i1,
      int j1,
      IBlockData iblockdata,
      IBlockData iblockdata1,
      boolean flag
   ) {
      for (int k1 = j; k1 <= i1; k1++) {
         for (int l1 = i; l1 <= l; l1++) {
            for (int i2 = k; i2 <= j1; i2++) {
               if (random.nextFloat() <= f && (!flag || this.a(world, l1, k1, i2, structureboundingbox).getBlock().getMaterial() != Material.AIR)) {
                  if (k1 != j && k1 != i1 && l1 != i && l1 != l && i2 != k && i2 != j1) {
                     this.a(world, iblockdata1, l1, k1, i2, structureboundingbox);
                  } else {
                     this.a(world, iblockdata, l1, k1, i2, structureboundingbox);
                  }
               }
            }
         }
      }
   }

   protected void a(World world, StructureBoundingBox structureboundingbox, Random random, float f, int i, int j, int k, IBlockData iblockdata) {
      if (random.nextFloat() < f) {
         this.a(world, iblockdata, i, j, k, structureboundingbox);
      }
   }

   protected void a(World world, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, int j1, IBlockData iblockdata, boolean flag) {
      float f = l - i + 1;
      float f1 = i1 - j + 1;
      float f2 = j1 - k + 1;
      float f3 = i + f / 2.0F;
      float f4 = k + f2 / 2.0F;

      for (int k1 = j; k1 <= i1; k1++) {
         float f5 = (k1 - j) / f1;

         for (int l1 = i; l1 <= l; l1++) {
            float f6 = (l1 - f3) / (f * 0.5F);

            for (int i2 = k; i2 <= j1; i2++) {
               float f7 = (i2 - f4) / (f2 * 0.5F);
               if (!flag || this.a(world, l1, k1, i2, structureboundingbox).getBlock().getMaterial() != Material.AIR) {
                  float f8 = f6 * f6 + f5 * f5 + f7 * f7;
                  if (f8 <= 1.05F) {
                     this.a(world, iblockdata, l1, k1, i2, structureboundingbox);
                  }
               }
            }
         }
      }
   }

   protected void b(World world, int i, int j, int k, StructureBoundingBox structureboundingbox) {
      int l = this.a(i, k);
      int i1 = this.d(j);
      int j1 = this.b(i, k);
      if (structureboundingbox.b(l, i1, j1)) {
         for (BlockPosition.MutableBlockPosition pos = new BlockPosition.MutableBlockPosition(); !world.isEmpty(l, i1, j1) && i1 < 255; i1++) {
            world.setTypeAndData(pos.c(l, i1, j1), Blocks.AIR.getBlockData(), 2);
         }
      }
   }

   protected void b(World world, IBlockData iblockdata, int i, int j, int k, StructureBoundingBox structureboundingbox) {
      int l = this.a(i, k);
      int i1 = this.d(j);
      int j1 = this.b(i, k);
      if (structureboundingbox.b(l, i1, j1)) {
         for (BlockPosition.MutableBlockPosition pos = new BlockPosition.MutableBlockPosition();
            (world.isEmpty(l, i1, j1) || world.getType(l, i1, j1).getBlock().getMaterial().isLiquid()) && i1 > 1;
            i1--
         ) {
            world.setTypeAndData(pos.c(l, i1, j1), iblockdata, 2);
         }
      }
   }

   protected boolean a(World world, StructureBoundingBox structureboundingbox, Random random, int i, int j, int k, List<StructurePieceTreasure> list, int l) {
      BlockPosition blockposition = new BlockPosition(this.a(i, k), this.d(j), this.b(i, k));
      if (structureboundingbox.b(blockposition) && world.getType(blockposition).getBlock() != Blocks.CHEST) {
         IBlockData iblockdata = Blocks.CHEST.getBlockData();
         world.setTypeAndData(blockposition, Blocks.CHEST.f(world, blockposition, iblockdata), 2);
         TileEntity tileentity = world.getTileEntity(blockposition);
         if (tileentity instanceof TileEntityChest) {
            StructurePieceTreasure.a(random, list, (TileEntityChest)tileentity, l);
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean a(
      World world, StructureBoundingBox structureboundingbox, Random random, int i, int j, int k, int l, List<StructurePieceTreasure> list, int i1
   ) {
      BlockPosition blockposition = new BlockPosition(this.a(i, k), this.d(j), this.b(i, k));
      if (structureboundingbox.b(blockposition) && world.getType(blockposition).getBlock() != Blocks.DISPENSER) {
         world.setTypeAndData(blockposition, Blocks.DISPENSER.fromLegacyData(this.a(Blocks.DISPENSER, l)), 2);
         TileEntity tileentity = world.getTileEntity(blockposition);
         if (tileentity instanceof TileEntityDispenser) {
            StructurePieceTreasure.a(random, list, (TileEntityDispenser)tileentity, i1);
         }

         return true;
      } else {
         return false;
      }
   }

   protected void a(World world, StructureBoundingBox structureboundingbox, Random random, int i, int j, int k, EnumDirection enumdirection) {
      int x = this.a(i, k);
      int y = this.d(j);
      int z = this.b(i, k);
      if (structureboundingbox.b(x, y, z)) {
         ItemDoor.a(world, new BlockPosition(x, y, z), enumdirection.f(), Blocks.WOODEN_DOOR);
      }
   }

   public void a(int i, int j, int k) {
      this.l.a(i, j, k);
   }

   public abstract static class StructurePieceBlockSelector {
      protected IBlockData a = Blocks.AIR.getBlockData();

      protected StructurePieceBlockSelector() {
      }

      public abstract void a(Random var1, int var2, int var3, int var4, boolean var5);

      public IBlockData a() {
         return this.a;
      }
   }

   static class SyntheticClass_1 {
      static final int[] a = new int[EnumDirection.values().length];

      static {
         try {
            a[EnumDirection.NORTH.ordinal()] = 1;
         } catch (NoSuchFieldError var4) {
         }

         try {
            a[EnumDirection.SOUTH.ordinal()] = 2;
         } catch (NoSuchFieldError var3) {
         }

         try {
            a[EnumDirection.WEST.ordinal()] = 3;
         } catch (NoSuchFieldError var2) {
         }

         try {
            a[EnumDirection.EAST.ordinal()] = 4;
         } catch (NoSuchFieldError var1) {
         }
      }
   }
}
