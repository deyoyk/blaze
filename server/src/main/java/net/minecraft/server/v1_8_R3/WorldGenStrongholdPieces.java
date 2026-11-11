package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;

public class WorldGenStrongholdPieces {
   private static final WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight[] b = new WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight[]{
      new WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight(WorldGenStrongholdPieces.WorldGenStrongholdStairs.class, 40, 0),
      new WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight(WorldGenStrongholdPieces.WorldGenStrongholdPrison.class, 5, 5),
      new WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight(WorldGenStrongholdPieces.WorldGenStrongholdLeftTurn.class, 20, 0),
      new WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight(WorldGenStrongholdPieces.WorldGenStrongholdRightTurn.class, 20, 0),
      new WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight(WorldGenStrongholdPieces.WorldGenStrongholdRoomCrossing.class, 10, 6),
      new WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight(WorldGenStrongholdPieces.WorldGenStrongholdStairsStraight.class, 5, 5),
      new WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight(WorldGenStrongholdPieces.WorldGenStrongholdStairs2.class, 5, 5),
      new WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight(WorldGenStrongholdPieces.WorldGenStrongholdCrossing.class, 5, 4),
      new WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight(WorldGenStrongholdPieces.WorldGenStrongholdChestCorridor.class, 5, 4),
      new WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight(WorldGenStrongholdPieces.WorldGenStrongholdLibrary.class, 10, 2) {
         @Override
         public boolean a(int i) {
            return super.a(i) && i > 4;
         }
      },
      new WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight(WorldGenStrongholdPieces.WorldGenStrongholdPortalRoom.class, 20, 1) {
         @Override
         public boolean a(int i) {
            return super.a(i) && i > 5;
         }
      }
   };
   private static List<WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight> c;
   private static Class<? extends WorldGenStrongholdPieces.WorldGenStrongholdPiece> d;
   static int a;
   private static final WorldGenStrongholdPieces.WorldGenStrongholdStones e = new WorldGenStrongholdPieces.WorldGenStrongholdStones(null);

   public static void a() {
      WorldGenFactory.a(WorldGenStrongholdPieces.WorldGenStrongholdChestCorridor.class, "SHCC");
      WorldGenFactory.a(WorldGenStrongholdPieces.WorldGenStrongholdCorridor.class, "SHFC");
      WorldGenFactory.a(WorldGenStrongholdPieces.WorldGenStrongholdCrossing.class, "SH5C");
      WorldGenFactory.a(WorldGenStrongholdPieces.WorldGenStrongholdLeftTurn.class, "SHLT");
      WorldGenFactory.a(WorldGenStrongholdPieces.WorldGenStrongholdLibrary.class, "SHLi");
      WorldGenFactory.a(WorldGenStrongholdPieces.WorldGenStrongholdPortalRoom.class, "SHPR");
      WorldGenFactory.a(WorldGenStrongholdPieces.WorldGenStrongholdPrison.class, "SHPH");
      WorldGenFactory.a(WorldGenStrongholdPieces.WorldGenStrongholdRightTurn.class, "SHRT");
      WorldGenFactory.a(WorldGenStrongholdPieces.WorldGenStrongholdRoomCrossing.class, "SHRC");
      WorldGenFactory.a(WorldGenStrongholdPieces.WorldGenStrongholdStairs2.class, "SHSD");
      WorldGenFactory.a(WorldGenStrongholdPieces.WorldGenStrongholdStart.class, "SHStart");
      WorldGenFactory.a(WorldGenStrongholdPieces.WorldGenStrongholdStairs.class, "SHS");
      WorldGenFactory.a(WorldGenStrongholdPieces.WorldGenStrongholdStairsStraight.class, "SHSSD");
   }

   public static void b() {
      c = Lists.newArrayList();

      for (WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight worldgenstrongholdpieces_worldgenstrongholdpieceweight : b) {
         worldgenstrongholdpieces_worldgenstrongholdpieceweight.c = 0;
         c.add(worldgenstrongholdpieces_worldgenstrongholdpieceweight);
      }

      d = null;
   }

   private static boolean d() {
      boolean flag = false;
      a = 0;

      for (WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight worldgenstrongholdpieces_worldgenstrongholdpieceweight : c) {
         if (worldgenstrongholdpieces_worldgenstrongholdpieceweight.d > 0
            && worldgenstrongholdpieces_worldgenstrongholdpieceweight.c < worldgenstrongholdpieces_worldgenstrongholdpieceweight.d) {
            flag = true;
         }

         a = a + worldgenstrongholdpieces_worldgenstrongholdpieceweight.b;
      }

      return flag;
   }

   private static WorldGenStrongholdPieces.WorldGenStrongholdPiece a(
      Class<? extends WorldGenStrongholdPieces.WorldGenStrongholdPiece> oclass,
      List<StructurePiece> list,
      Random random,
      int i,
      int j,
      int k,
      EnumDirection enumdirection,
      int l
   ) {
      Object object = null;
      if (oclass == WorldGenStrongholdPieces.WorldGenStrongholdStairs.class) {
         object = WorldGenStrongholdPieces.WorldGenStrongholdStairs.a(list, random, i, j, k, enumdirection, l);
      } else if (oclass == WorldGenStrongholdPieces.WorldGenStrongholdPrison.class) {
         object = WorldGenStrongholdPieces.WorldGenStrongholdPrison.a(list, random, i, j, k, enumdirection, l);
      } else if (oclass == WorldGenStrongholdPieces.WorldGenStrongholdLeftTurn.class) {
         object = WorldGenStrongholdPieces.WorldGenStrongholdLeftTurn.a(list, random, i, j, k, enumdirection, l);
      } else if (oclass == WorldGenStrongholdPieces.WorldGenStrongholdRightTurn.class) {
         object = WorldGenStrongholdPieces.WorldGenStrongholdRightTurn.a(list, random, i, j, k, enumdirection, l);
      } else if (oclass == WorldGenStrongholdPieces.WorldGenStrongholdRoomCrossing.class) {
         object = WorldGenStrongholdPieces.WorldGenStrongholdRoomCrossing.a(list, random, i, j, k, enumdirection, l);
      } else if (oclass == WorldGenStrongholdPieces.WorldGenStrongholdStairsStraight.class) {
         object = WorldGenStrongholdPieces.WorldGenStrongholdStairsStraight.a(list, random, i, j, k, enumdirection, l);
      } else if (oclass == WorldGenStrongholdPieces.WorldGenStrongholdStairs2.class) {
         object = WorldGenStrongholdPieces.WorldGenStrongholdStairs2.a(list, random, i, j, k, enumdirection, l);
      } else if (oclass == WorldGenStrongholdPieces.WorldGenStrongholdCrossing.class) {
         object = WorldGenStrongholdPieces.WorldGenStrongholdCrossing.a(list, random, i, j, k, enumdirection, l);
      } else if (oclass == WorldGenStrongholdPieces.WorldGenStrongholdChestCorridor.class) {
         object = WorldGenStrongholdPieces.WorldGenStrongholdChestCorridor.a(list, random, i, j, k, enumdirection, l);
      } else if (oclass == WorldGenStrongholdPieces.WorldGenStrongholdLibrary.class) {
         object = WorldGenStrongholdPieces.WorldGenStrongholdLibrary.a(list, random, i, j, k, enumdirection, l);
      } else if (oclass == WorldGenStrongholdPieces.WorldGenStrongholdPortalRoom.class) {
         object = WorldGenStrongholdPieces.WorldGenStrongholdPortalRoom.a(list, random, i, j, k, enumdirection, l);
      }

      return (WorldGenStrongholdPieces.WorldGenStrongholdPiece)object;
   }

   private static WorldGenStrongholdPieces.WorldGenStrongholdPiece b(
      WorldGenStrongholdPieces.WorldGenStrongholdStart worldgenstrongholdpieces_worldgenstrongholdstart,
      List<StructurePiece> list,
      Random random,
      int i,
      int j,
      int k,
      EnumDirection enumdirection,
      int l
   ) {
      if (!d()) {
         return null;
      } else {
         if (d != null) {
            WorldGenStrongholdPieces.WorldGenStrongholdPiece worldgenstrongholdpieces_worldgenstrongholdpiece = a(d, list, random, i, j, k, enumdirection, l);
            d = null;
            if (worldgenstrongholdpieces_worldgenstrongholdpiece != null) {
               return worldgenstrongholdpieces_worldgenstrongholdpiece;
            }
         }

         int i1 = 0;

         while (i1 < 5) {
            i1++;
            int j1 = random.nextInt(a);

            for (WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight worldgenstrongholdpieces_worldgenstrongholdpieceweight : c) {
               j1 -= worldgenstrongholdpieces_worldgenstrongholdpieceweight.b;
               if (j1 < 0) {
                  if (!worldgenstrongholdpieces_worldgenstrongholdpieceweight.a(l)
                     || worldgenstrongholdpieces_worldgenstrongholdpieceweight == worldgenstrongholdpieces_worldgenstrongholdstart.a) {
                     break;
                  }

                  WorldGenStrongholdPieces.WorldGenStrongholdPiece worldgenstrongholdpieces_worldgenstrongholdpiece1 = a(
                     worldgenstrongholdpieces_worldgenstrongholdpieceweight.a, list, random, i, j, k, enumdirection, l
                  );
                  if (worldgenstrongholdpieces_worldgenstrongholdpiece1 != null) {
                     worldgenstrongholdpieces_worldgenstrongholdpieceweight.c++;
                     worldgenstrongholdpieces_worldgenstrongholdstart.a = worldgenstrongholdpieces_worldgenstrongholdpieceweight;
                     if (!worldgenstrongholdpieces_worldgenstrongholdpieceweight.a()) {
                        c.remove(worldgenstrongholdpieces_worldgenstrongholdpieceweight);
                     }

                     return worldgenstrongholdpieces_worldgenstrongholdpiece1;
                  }
               }
            }
         }

         StructureBoundingBox structureboundingbox = WorldGenStrongholdPieces.WorldGenStrongholdCorridor.a(list, random, i, j, k, enumdirection);
         return structureboundingbox != null && structureboundingbox.b > 1
            ? new WorldGenStrongholdPieces.WorldGenStrongholdCorridor(l, random, structureboundingbox, enumdirection)
            : null;
      }
   }

   private static StructurePiece c(
      WorldGenStrongholdPieces.WorldGenStrongholdStart worldgenstrongholdpieces_worldgenstrongholdstart,
      List<StructurePiece> list,
      Random random,
      int i,
      int j,
      int k,
      EnumDirection enumdirection,
      int l
   ) {
      if (l > 50) {
         return null;
      } else if (Math.abs(i - worldgenstrongholdpieces_worldgenstrongholdstart.c().a) <= 112
         && Math.abs(k - worldgenstrongholdpieces_worldgenstrongholdstart.c().c) <= 112) {
         WorldGenStrongholdPieces.WorldGenStrongholdPiece worldgenstrongholdpieces_worldgenstrongholdpiece = b(
            worldgenstrongholdpieces_worldgenstrongholdstart, list, random, i, j, k, enumdirection, l + 1
         );
         if (worldgenstrongholdpieces_worldgenstrongholdpiece != null) {
            list.add(worldgenstrongholdpieces_worldgenstrongholdpiece);
            worldgenstrongholdpieces_worldgenstrongholdstart.c.add(worldgenstrongholdpieces_worldgenstrongholdpiece);
         }

         return worldgenstrongholdpieces_worldgenstrongholdpiece;
      } else {
         return null;
      }
   }

   static class SyntheticClass_1 {
      static final int[] a;
      static final int[] b = new int[EnumDirection.values().length];

      static {
         try {
            b[EnumDirection.NORTH.ordinal()] = 1;
         } catch (NoSuchFieldError var8) {
         }

         try {
            b[EnumDirection.SOUTH.ordinal()] = 2;
         } catch (NoSuchFieldError var7) {
         }

         try {
            b[EnumDirection.WEST.ordinal()] = 3;
         } catch (NoSuchFieldError var6) {
         }

         try {
            b[EnumDirection.EAST.ordinal()] = 4;
         } catch (NoSuchFieldError var5) {
         }

         a = new int[WorldGenStrongholdPieces$WorldGenStrongholdPiece$WorldGenStrongholdPiece$WorldGenStrongholdDoorType.values().length];

         try {
            a[WorldGenStrongholdPieces$WorldGenStrongholdPiece$WorldGenStrongholdPiece$WorldGenStrongholdDoorType.OPENING.ordinal()] = 1;
         } catch (NoSuchFieldError var4) {
         }

         try {
            a[WorldGenStrongholdPieces$WorldGenStrongholdPiece$WorldGenStrongholdPiece$WorldGenStrongholdDoorType.WOOD_DOOR.ordinal()] = 2;
         } catch (NoSuchFieldError var3) {
         }

         try {
            a[WorldGenStrongholdPieces$WorldGenStrongholdPiece$WorldGenStrongholdPiece$WorldGenStrongholdDoorType.GRATES.ordinal()] = 3;
         } catch (NoSuchFieldError var2) {
         }

         try {
            a[WorldGenStrongholdPieces$WorldGenStrongholdPiece$WorldGenStrongholdPiece$WorldGenStrongholdDoorType.IRON_DOOR.ordinal()] = 4;
         } catch (NoSuchFieldError var1) {
         }
      }
   }

   public static class WorldGenStrongholdChestCorridor extends WorldGenStrongholdPieces.WorldGenStrongholdPiece {
      private static final List<StructurePieceTreasure> a = Lists.newArrayList(
         new StructurePieceTreasure[]{
            new StructurePieceTreasure(Items.ENDER_PEARL, 0, 1, 1, 10),
            new StructurePieceTreasure(Items.DIAMOND, 0, 1, 3, 3),
            new StructurePieceTreasure(Items.IRON_INGOT, 0, 1, 5, 10),
            new StructurePieceTreasure(Items.GOLD_INGOT, 0, 1, 3, 5),
            new StructurePieceTreasure(Items.REDSTONE, 0, 4, 9, 5),
            new StructurePieceTreasure(Items.BREAD, 0, 1, 3, 15),
            new StructurePieceTreasure(Items.APPLE, 0, 1, 3, 15),
            new StructurePieceTreasure(Items.IRON_PICKAXE, 0, 1, 1, 5),
            new StructurePieceTreasure(Items.IRON_SWORD, 0, 1, 1, 5),
            new StructurePieceTreasure(Items.IRON_CHESTPLATE, 0, 1, 1, 5),
            new StructurePieceTreasure(Items.IRON_HELMET, 0, 1, 1, 5),
            new StructurePieceTreasure(Items.IRON_LEGGINGS, 0, 1, 1, 5),
            new StructurePieceTreasure(Items.IRON_BOOTS, 0, 1, 1, 5),
            new StructurePieceTreasure(Items.GOLDEN_APPLE, 0, 1, 1, 1),
            new StructurePieceTreasure(Items.SADDLE, 0, 1, 1, 1),
            new StructurePieceTreasure(Items.IRON_HORSE_ARMOR, 0, 1, 1, 1),
            new StructurePieceTreasure(Items.GOLDEN_HORSE_ARMOR, 0, 1, 1, 1),
            new StructurePieceTreasure(Items.DIAMOND_HORSE_ARMOR, 0, 1, 1, 1)
         }
      );
      private boolean b;

      public WorldGenStrongholdChestCorridor() {
      }

      public WorldGenStrongholdChestCorridor(int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
         super(i);
         this.m = enumdirection;
         this.d = this.a(random);
         this.l = structureboundingbox;
      }

      @Override
      protected void a(NBTTagCompound nbttagcompound) {
         super.a(nbttagcompound);
         nbttagcompound.setBoolean("Chest", this.b);
      }

      @Override
      protected void b(NBTTagCompound nbttagcompound) {
         super.b(nbttagcompound);
         this.b = nbttagcompound.getBoolean("Chest");
      }

      @Override
      public void a(StructurePiece structurepiece, List<StructurePiece> list, Random random) {
         this.a((WorldGenStrongholdPieces.WorldGenStrongholdStart)structurepiece, list, random, 1, 1);
      }

      public static WorldGenStrongholdPieces.WorldGenStrongholdChestCorridor a(
         List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection, int l
      ) {
         StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, -1, -1, 0, 5, 5, 7, enumdirection);
         return a(structureboundingbox) && StructurePiece.a(list, structureboundingbox) == null
            ? new WorldGenStrongholdPieces.WorldGenStrongholdChestCorridor(l, random, structureboundingbox, enumdirection)
            : null;
      }

      @Override
      public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
         if (this.a(world, structureboundingbox)) {
            return false;
         } else {
            this.a(world, structureboundingbox, 0, 0, 0, 4, 4, 6, true, random, WorldGenStrongholdPieces.e);
            this.a(world, random, structureboundingbox, this.d, 1, 1, 0);
            this.a(
               world,
               random,
               structureboundingbox,
               WorldGenStrongholdPieces$WorldGenStrongholdPiece$WorldGenStrongholdPiece$WorldGenStrongholdDoorType.OPENING,
               1,
               1,
               6
            );
            this.a(world, structureboundingbox, 3, 1, 2, 3, 1, 4, Blocks.STONEBRICK.getBlockData(), Blocks.STONEBRICK.getBlockData(), false);
            this.a(world, Blocks.STONE_SLAB.fromLegacyData(BlockDoubleStepAbstract.EnumStoneSlabVariant.SMOOTHBRICK.a()), 3, 1, 1, structureboundingbox);
            this.a(world, Blocks.STONE_SLAB.fromLegacyData(BlockDoubleStepAbstract.EnumStoneSlabVariant.SMOOTHBRICK.a()), 3, 1, 5, structureboundingbox);
            this.a(world, Blocks.STONE_SLAB.fromLegacyData(BlockDoubleStepAbstract.EnumStoneSlabVariant.SMOOTHBRICK.a()), 3, 2, 2, structureboundingbox);
            this.a(world, Blocks.STONE_SLAB.fromLegacyData(BlockDoubleStepAbstract.EnumStoneSlabVariant.SMOOTHBRICK.a()), 3, 2, 4, structureboundingbox);

            for (int i = 2; i <= 4; i++) {
               this.a(world, Blocks.STONE_SLAB.fromLegacyData(BlockDoubleStepAbstract.EnumStoneSlabVariant.SMOOTHBRICK.a()), 2, 1, i, structureboundingbox);
            }

            if (!this.b && structureboundingbox.b(this.a(3, 3), this.d(2), this.b(3, 3))) {
               this.b = true;
               this.a(world, structureboundingbox, random, 3, 2, 3, StructurePieceTreasure.a(a, Items.ENCHANTED_BOOK.b(random)), 2 + random.nextInt(2));
            }

            return true;
         }
      }
   }

   public static class WorldGenStrongholdCorridor extends WorldGenStrongholdPieces.WorldGenStrongholdPiece {
      private int a;

      public WorldGenStrongholdCorridor() {
      }

      public WorldGenStrongholdCorridor(int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
         super(i);
         this.m = enumdirection;
         this.l = structureboundingbox;
         this.a = enumdirection != EnumDirection.NORTH && enumdirection != EnumDirection.SOUTH ? structureboundingbox.c() : structureboundingbox.e();
      }

      @Override
      protected void a(NBTTagCompound nbttagcompound) {
         super.a(nbttagcompound);
         nbttagcompound.setInt("Steps", this.a);
      }

      @Override
      protected void b(NBTTagCompound nbttagcompound) {
         super.b(nbttagcompound);
         this.a = nbttagcompound.getInt("Steps");
      }

      public static StructureBoundingBox a(List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection) {
         boolean flag = true;
         StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, -1, -1, 0, 5, 5, 4, enumdirection);
         StructurePiece structurepiece = StructurePiece.a(list, structureboundingbox);
         if (structurepiece == null) {
            return null;
         } else {
            if (structurepiece.c().b == structureboundingbox.b) {
               for (int l = 3; l >= 1; l--) {
                  structureboundingbox = StructureBoundingBox.a(i, j, k, -1, -1, 0, 5, 5, l - 1, enumdirection);
                  if (!structurepiece.c().a(structureboundingbox)) {
                     return StructureBoundingBox.a(i, j, k, -1, -1, 0, 5, 5, l, enumdirection);
                  }
               }
            }

            return null;
         }
      }

      @Override
      public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
         if (this.a(world, structureboundingbox)) {
            return false;
         } else {
            for (int i = 0; i < this.a; i++) {
               this.a(world, Blocks.STONEBRICK.getBlockData(), 0, 0, i, structureboundingbox);
               this.a(world, Blocks.STONEBRICK.getBlockData(), 1, 0, i, structureboundingbox);
               this.a(world, Blocks.STONEBRICK.getBlockData(), 2, 0, i, structureboundingbox);
               this.a(world, Blocks.STONEBRICK.getBlockData(), 3, 0, i, structureboundingbox);
               this.a(world, Blocks.STONEBRICK.getBlockData(), 4, 0, i, structureboundingbox);

               for (int j = 1; j <= 3; j++) {
                  this.a(world, Blocks.STONEBRICK.getBlockData(), 0, j, i, structureboundingbox);
                  this.a(world, Blocks.AIR.getBlockData(), 1, j, i, structureboundingbox);
                  this.a(world, Blocks.AIR.getBlockData(), 2, j, i, structureboundingbox);
                  this.a(world, Blocks.AIR.getBlockData(), 3, j, i, structureboundingbox);
                  this.a(world, Blocks.STONEBRICK.getBlockData(), 4, j, i, structureboundingbox);
               }

               this.a(world, Blocks.STONEBRICK.getBlockData(), 0, 4, i, structureboundingbox);
               this.a(world, Blocks.STONEBRICK.getBlockData(), 1, 4, i, structureboundingbox);
               this.a(world, Blocks.STONEBRICK.getBlockData(), 2, 4, i, structureboundingbox);
               this.a(world, Blocks.STONEBRICK.getBlockData(), 3, 4, i, structureboundingbox);
               this.a(world, Blocks.STONEBRICK.getBlockData(), 4, 4, i, structureboundingbox);
            }

            return true;
         }
      }
   }

   public static class WorldGenStrongholdCrossing extends WorldGenStrongholdPieces.WorldGenStrongholdPiece {
      private boolean a;
      private boolean b;
      private boolean c;
      private boolean e;

      public WorldGenStrongholdCrossing() {
      }

      public WorldGenStrongholdCrossing(int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
         super(i);
         this.m = enumdirection;
         this.d = this.a(random);
         this.l = structureboundingbox;
         this.a = random.nextBoolean();
         this.b = random.nextBoolean();
         this.c = random.nextBoolean();
         this.e = random.nextInt(3) > 0;
      }

      @Override
      protected void a(NBTTagCompound nbttagcompound) {
         super.a(nbttagcompound);
         nbttagcompound.setBoolean("leftLow", this.a);
         nbttagcompound.setBoolean("leftHigh", this.b);
         nbttagcompound.setBoolean("rightLow", this.c);
         nbttagcompound.setBoolean("rightHigh", this.e);
      }

      @Override
      protected void b(NBTTagCompound nbttagcompound) {
         super.b(nbttagcompound);
         this.a = nbttagcompound.getBoolean("leftLow");
         this.b = nbttagcompound.getBoolean("leftHigh");
         this.c = nbttagcompound.getBoolean("rightLow");
         this.e = nbttagcompound.getBoolean("rightHigh");
      }

      @Override
      public void a(StructurePiece structurepiece, List<StructurePiece> list, Random random) {
         int i = 3;
         int j = 5;
         if (this.m == EnumDirection.WEST || this.m == EnumDirection.NORTH) {
            i = 8 - i;
            j = 8 - j;
         }

         this.a((WorldGenStrongholdPieces.WorldGenStrongholdStart)structurepiece, list, random, 5, 1);
         if (this.a) {
            this.b((WorldGenStrongholdPieces.WorldGenStrongholdStart)structurepiece, list, random, i, 1);
         }

         if (this.b) {
            this.b((WorldGenStrongholdPieces.WorldGenStrongholdStart)structurepiece, list, random, j, 7);
         }

         if (this.c) {
            this.c((WorldGenStrongholdPieces.WorldGenStrongholdStart)structurepiece, list, random, i, 1);
         }

         if (this.e) {
            this.c((WorldGenStrongholdPieces.WorldGenStrongholdStart)structurepiece, list, random, j, 7);
         }
      }

      public static WorldGenStrongholdPieces.WorldGenStrongholdCrossing a(
         List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection, int l
      ) {
         StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, -4, -3, 0, 10, 9, 11, enumdirection);
         return a(structureboundingbox) && StructurePiece.a(list, structureboundingbox) == null
            ? new WorldGenStrongholdPieces.WorldGenStrongholdCrossing(l, random, structureboundingbox, enumdirection)
            : null;
      }

      @Override
      public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
         if (this.a(world, structureboundingbox)) {
            return false;
         } else {
            this.a(world, structureboundingbox, 0, 0, 0, 9, 8, 10, true, random, WorldGenStrongholdPieces.e);
            this.a(world, random, structureboundingbox, this.d, 4, 3, 0);
            if (this.a) {
               this.a(world, structureboundingbox, 0, 3, 1, 0, 5, 3, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            }

            if (this.c) {
               this.a(world, structureboundingbox, 9, 3, 1, 9, 5, 3, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            }

            if (this.b) {
               this.a(world, structureboundingbox, 0, 5, 7, 0, 7, 9, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            }

            if (this.e) {
               this.a(world, structureboundingbox, 9, 5, 7, 9, 7, 9, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            }

            this.a(world, structureboundingbox, 5, 1, 10, 7, 3, 10, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 1, 2, 1, 8, 2, 6, false, random, WorldGenStrongholdPieces.e);
            this.a(world, structureboundingbox, 4, 1, 5, 4, 4, 9, false, random, WorldGenStrongholdPieces.e);
            this.a(world, structureboundingbox, 8, 1, 5, 8, 4, 9, false, random, WorldGenStrongholdPieces.e);
            this.a(world, structureboundingbox, 1, 4, 7, 3, 4, 9, false, random, WorldGenStrongholdPieces.e);
            this.a(world, structureboundingbox, 1, 3, 5, 3, 3, 6, false, random, WorldGenStrongholdPieces.e);
            this.a(world, structureboundingbox, 1, 3, 4, 3, 3, 4, Blocks.STONE_SLAB.getBlockData(), Blocks.STONE_SLAB.getBlockData(), false);
            this.a(world, structureboundingbox, 1, 4, 6, 3, 4, 6, Blocks.STONE_SLAB.getBlockData(), Blocks.STONE_SLAB.getBlockData(), false);
            this.a(world, structureboundingbox, 5, 1, 7, 7, 1, 8, false, random, WorldGenStrongholdPieces.e);
            this.a(world, structureboundingbox, 5, 1, 9, 7, 1, 9, Blocks.STONE_SLAB.getBlockData(), Blocks.STONE_SLAB.getBlockData(), false);
            this.a(world, structureboundingbox, 5, 2, 7, 7, 2, 7, Blocks.STONE_SLAB.getBlockData(), Blocks.STONE_SLAB.getBlockData(), false);
            this.a(world, structureboundingbox, 4, 5, 7, 4, 5, 9, Blocks.STONE_SLAB.getBlockData(), Blocks.STONE_SLAB.getBlockData(), false);
            this.a(world, structureboundingbox, 8, 5, 7, 8, 5, 9, Blocks.STONE_SLAB.getBlockData(), Blocks.STONE_SLAB.getBlockData(), false);
            this.a(world, structureboundingbox, 5, 5, 7, 7, 5, 9, Blocks.DOUBLE_STONE_SLAB.getBlockData(), Blocks.DOUBLE_STONE_SLAB.getBlockData(), false);
            this.a(world, Blocks.TORCH.getBlockData(), 6, 5, 6, structureboundingbox);
            return true;
         }
      }
   }

   public static class WorldGenStrongholdLeftTurn extends WorldGenStrongholdPieces.WorldGenStrongholdPiece {
      public WorldGenStrongholdLeftTurn() {
      }

      public WorldGenStrongholdLeftTurn(int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
         super(i);
         this.m = enumdirection;
         this.d = this.a(random);
         this.l = structureboundingbox;
      }

      @Override
      public void a(StructurePiece structurepiece, List<StructurePiece> list, Random random) {
         if (this.m != EnumDirection.NORTH && this.m != EnumDirection.EAST) {
            this.c((WorldGenStrongholdPieces.WorldGenStrongholdStart)structurepiece, list, random, 1, 1);
         } else {
            this.b((WorldGenStrongholdPieces.WorldGenStrongholdStart)structurepiece, list, random, 1, 1);
         }
      }

      public static WorldGenStrongholdPieces.WorldGenStrongholdLeftTurn a(
         List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection, int l
      ) {
         StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, -1, -1, 0, 5, 5, 5, enumdirection);
         return a(structureboundingbox) && StructurePiece.a(list, structureboundingbox) == null
            ? new WorldGenStrongholdPieces.WorldGenStrongholdLeftTurn(l, random, structureboundingbox, enumdirection)
            : null;
      }

      @Override
      public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
         if (this.a(world, structureboundingbox)) {
            return false;
         } else {
            this.a(world, structureboundingbox, 0, 0, 0, 4, 4, 4, true, random, WorldGenStrongholdPieces.e);
            this.a(world, random, structureboundingbox, this.d, 1, 1, 0);
            if (this.m != EnumDirection.NORTH && this.m != EnumDirection.EAST) {
               this.a(world, structureboundingbox, 4, 1, 1, 4, 3, 3, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            } else {
               this.a(world, structureboundingbox, 0, 1, 1, 0, 3, 3, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            }

            return true;
         }
      }
   }

   public static class WorldGenStrongholdLibrary extends WorldGenStrongholdPieces.WorldGenStrongholdPiece {
      private static final List<StructurePieceTreasure> a = Lists.newArrayList(
         new StructurePieceTreasure[]{
            new StructurePieceTreasure(Items.BOOK, 0, 1, 3, 20),
            new StructurePieceTreasure(Items.PAPER, 0, 2, 7, 20),
            new StructurePieceTreasure(Items.MAP, 0, 1, 1, 1),
            new StructurePieceTreasure(Items.COMPASS, 0, 1, 1, 1)
         }
      );
      private boolean b;

      public WorldGenStrongholdLibrary() {
      }

      public WorldGenStrongholdLibrary(int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
         super(i);
         this.m = enumdirection;
         this.d = this.a(random);
         this.l = structureboundingbox;
         this.b = structureboundingbox.d() > 6;
      }

      @Override
      protected void a(NBTTagCompound nbttagcompound) {
         super.a(nbttagcompound);
         nbttagcompound.setBoolean("Tall", this.b);
      }

      @Override
      protected void b(NBTTagCompound nbttagcompound) {
         super.b(nbttagcompound);
         this.b = nbttagcompound.getBoolean("Tall");
      }

      public static WorldGenStrongholdPieces.WorldGenStrongholdLibrary a(
         List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection, int l
      ) {
         StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, -4, -1, 0, 14, 11, 15, enumdirection);
         if (!a(structureboundingbox) || StructurePiece.a(list, structureboundingbox) != null) {
            structureboundingbox = StructureBoundingBox.a(i, j, k, -4, -1, 0, 14, 6, 15, enumdirection);
            if (!a(structureboundingbox) || StructurePiece.a(list, structureboundingbox) != null) {
               return null;
            }
         }

         return new WorldGenStrongholdPieces.WorldGenStrongholdLibrary(l, random, structureboundingbox, enumdirection);
      }

      @Override
      public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
         if (this.a(world, structureboundingbox)) {
            return false;
         } else {
            byte b0 = 11;
            if (!this.b) {
               b0 = 6;
            }

            this.a(world, structureboundingbox, 0, 0, 0, 13, b0 - 1, 14, true, random, WorldGenStrongholdPieces.e);
            this.a(world, random, structureboundingbox, this.d, 4, 1, 0);
            this.a(world, structureboundingbox, random, 0.07F, 2, 1, 1, 11, 4, 13, Blocks.WEB.getBlockData(), Blocks.WEB.getBlockData(), false);

            for (int i = 1; i <= 13; i++) {
               if ((i - 1) % 4 == 0) {
                  this.a(world, structureboundingbox, 1, 1, i, 1, 4, i, Blocks.PLANKS.getBlockData(), Blocks.PLANKS.getBlockData(), false);
                  this.a(world, structureboundingbox, 12, 1, i, 12, 4, i, Blocks.PLANKS.getBlockData(), Blocks.PLANKS.getBlockData(), false);
                  this.a(world, Blocks.TORCH.getBlockData(), 2, 3, i, structureboundingbox);
                  this.a(world, Blocks.TORCH.getBlockData(), 11, 3, i, structureboundingbox);
                  if (this.b) {
                     this.a(world, structureboundingbox, 1, 6, i, 1, 9, i, Blocks.PLANKS.getBlockData(), Blocks.PLANKS.getBlockData(), false);
                     this.a(world, structureboundingbox, 12, 6, i, 12, 9, i, Blocks.PLANKS.getBlockData(), Blocks.PLANKS.getBlockData(), false);
                  }
               } else {
                  this.a(world, structureboundingbox, 1, 1, i, 1, 4, i, Blocks.BOOKSHELF.getBlockData(), Blocks.BOOKSHELF.getBlockData(), false);
                  this.a(world, structureboundingbox, 12, 1, i, 12, 4, i, Blocks.BOOKSHELF.getBlockData(), Blocks.BOOKSHELF.getBlockData(), false);
                  if (this.b) {
                     this.a(world, structureboundingbox, 1, 6, i, 1, 9, i, Blocks.BOOKSHELF.getBlockData(), Blocks.BOOKSHELF.getBlockData(), false);
                     this.a(world, structureboundingbox, 12, 6, i, 12, 9, i, Blocks.BOOKSHELF.getBlockData(), Blocks.BOOKSHELF.getBlockData(), false);
                  }
               }
            }

            for (int var8 = 3; var8 < 12; var8 += 2) {
               this.a(world, structureboundingbox, 3, 1, var8, 4, 3, var8, Blocks.BOOKSHELF.getBlockData(), Blocks.BOOKSHELF.getBlockData(), false);
               this.a(world, structureboundingbox, 6, 1, var8, 7, 3, var8, Blocks.BOOKSHELF.getBlockData(), Blocks.BOOKSHELF.getBlockData(), false);
               this.a(world, structureboundingbox, 9, 1, var8, 10, 3, var8, Blocks.BOOKSHELF.getBlockData(), Blocks.BOOKSHELF.getBlockData(), false);
            }

            if (this.b) {
               this.a(world, structureboundingbox, 1, 5, 1, 3, 5, 13, Blocks.PLANKS.getBlockData(), Blocks.PLANKS.getBlockData(), false);
               this.a(world, structureboundingbox, 10, 5, 1, 12, 5, 13, Blocks.PLANKS.getBlockData(), Blocks.PLANKS.getBlockData(), false);
               this.a(world, structureboundingbox, 4, 5, 1, 9, 5, 2, Blocks.PLANKS.getBlockData(), Blocks.PLANKS.getBlockData(), false);
               this.a(world, structureboundingbox, 4, 5, 12, 9, 5, 13, Blocks.PLANKS.getBlockData(), Blocks.PLANKS.getBlockData(), false);
               this.a(world, Blocks.PLANKS.getBlockData(), 9, 5, 11, structureboundingbox);
               this.a(world, Blocks.PLANKS.getBlockData(), 8, 5, 11, structureboundingbox);
               this.a(world, Blocks.PLANKS.getBlockData(), 9, 5, 10, structureboundingbox);
               this.a(world, structureboundingbox, 3, 6, 2, 3, 6, 12, Blocks.FENCE.getBlockData(), Blocks.FENCE.getBlockData(), false);
               this.a(world, structureboundingbox, 10, 6, 2, 10, 6, 10, Blocks.FENCE.getBlockData(), Blocks.FENCE.getBlockData(), false);
               this.a(world, structureboundingbox, 4, 6, 2, 9, 6, 2, Blocks.FENCE.getBlockData(), Blocks.FENCE.getBlockData(), false);
               this.a(world, structureboundingbox, 4, 6, 12, 8, 6, 12, Blocks.FENCE.getBlockData(), Blocks.FENCE.getBlockData(), false);
               this.a(world, Blocks.FENCE.getBlockData(), 9, 6, 11, structureboundingbox);
               this.a(world, Blocks.FENCE.getBlockData(), 8, 6, 11, structureboundingbox);
               this.a(world, Blocks.FENCE.getBlockData(), 9, 6, 10, structureboundingbox);
               int var9 = this.a(Blocks.LADDER, 3);
               this.a(world, Blocks.LADDER.fromLegacyData(var9), 10, 1, 13, structureboundingbox);
               this.a(world, Blocks.LADDER.fromLegacyData(var9), 10, 2, 13, structureboundingbox);
               this.a(world, Blocks.LADDER.fromLegacyData(var9), 10, 3, 13, structureboundingbox);
               this.a(world, Blocks.LADDER.fromLegacyData(var9), 10, 4, 13, structureboundingbox);
               this.a(world, Blocks.LADDER.fromLegacyData(var9), 10, 5, 13, structureboundingbox);
               this.a(world, Blocks.LADDER.fromLegacyData(var9), 10, 6, 13, structureboundingbox);
               this.a(world, Blocks.LADDER.fromLegacyData(var9), 10, 7, 13, structureboundingbox);
               byte b1 = 7;
               byte b2 = 7;
               this.a(world, Blocks.FENCE.getBlockData(), b1 - 1, 9, b2, structureboundingbox);
               this.a(world, Blocks.FENCE.getBlockData(), b1, 9, b2, structureboundingbox);
               this.a(world, Blocks.FENCE.getBlockData(), b1 - 1, 8, b2, structureboundingbox);
               this.a(world, Blocks.FENCE.getBlockData(), b1, 8, b2, structureboundingbox);
               this.a(world, Blocks.FENCE.getBlockData(), b1 - 1, 7, b2, structureboundingbox);
               this.a(world, Blocks.FENCE.getBlockData(), b1, 7, b2, structureboundingbox);
               this.a(world, Blocks.FENCE.getBlockData(), b1 - 2, 7, b2, structureboundingbox);
               this.a(world, Blocks.FENCE.getBlockData(), b1 + 1, 7, b2, structureboundingbox);
               this.a(world, Blocks.FENCE.getBlockData(), b1 - 1, 7, b2 - 1, structureboundingbox);
               this.a(world, Blocks.FENCE.getBlockData(), b1 - 1, 7, b2 + 1, structureboundingbox);
               this.a(world, Blocks.FENCE.getBlockData(), b1, 7, b2 - 1, structureboundingbox);
               this.a(world, Blocks.FENCE.getBlockData(), b1, 7, b2 + 1, structureboundingbox);
               this.a(world, Blocks.TORCH.getBlockData(), b1 - 2, 8, b2, structureboundingbox);
               this.a(world, Blocks.TORCH.getBlockData(), b1 + 1, 8, b2, structureboundingbox);
               this.a(world, Blocks.TORCH.getBlockData(), b1 - 1, 8, b2 - 1, structureboundingbox);
               this.a(world, Blocks.TORCH.getBlockData(), b1 - 1, 8, b2 + 1, structureboundingbox);
               this.a(world, Blocks.TORCH.getBlockData(), b1, 8, b2 - 1, structureboundingbox);
               this.a(world, Blocks.TORCH.getBlockData(), b1, 8, b2 + 1, structureboundingbox);
            }

            this.a(world, structureboundingbox, random, 3, 3, 5, StructurePieceTreasure.a(a, Items.ENCHANTED_BOOK.a(random, 1, 5, 2)), 1 + random.nextInt(4));
            if (this.b) {
               this.a(world, Blocks.AIR.getBlockData(), 12, 9, 1, structureboundingbox);
               this.a(
                  world, structureboundingbox, random, 12, 8, 1, StructurePieceTreasure.a(a, Items.ENCHANTED_BOOK.a(random, 1, 5, 2)), 1 + random.nextInt(4)
               );
            }

            return true;
         }
      }
   }

   abstract static class WorldGenStrongholdPiece extends StructurePiece {
      protected WorldGenStrongholdPieces$WorldGenStrongholdPiece$WorldGenStrongholdPiece$WorldGenStrongholdDoorType d = WorldGenStrongholdPieces$WorldGenStrongholdPiece$WorldGenStrongholdPiece$WorldGenStrongholdDoorType.OPENING;

      public WorldGenStrongholdPiece() {
      }

      protected WorldGenStrongholdPiece(int i) {
         super(i);
      }

      @Override
      protected void a(NBTTagCompound nbttagcompound) {
         nbttagcompound.setString("EntryDoor", this.d.name());
      }

      @Override
      protected void b(NBTTagCompound nbttagcompound) {
         this.d = WorldGenStrongholdPieces$WorldGenStrongholdPiece$WorldGenStrongholdPiece$WorldGenStrongholdDoorType.valueOf(
            nbttagcompound.getString("EntryDoor")
         );
      }

      protected void a(
         World world,
         Random random,
         StructureBoundingBox structureboundingbox,
         WorldGenStrongholdPieces$WorldGenStrongholdPiece$WorldGenStrongholdPiece$WorldGenStrongholdDoorType worldgenstrongholdpieces_worldgenstrongholdpiece_worldgenstrongholdpiece$worldgenstrongholddoortype,
         int i,
         int j,
         int k
      ) {
         switch (StructurePiece.SyntheticClass_1.a[worldgenstrongholdpieces_worldgenstrongholdpiece_worldgenstrongholdpiece$worldgenstrongholddoortype.ordinal()]) {
            case 1:
            default:
               this.a(world, structureboundingbox, i, j, k, i + 3 - 1, j + 3 - 1, k, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
               break;
            case 2:
               this.a(world, Blocks.STONEBRICK.getBlockData(), i, j, k, structureboundingbox);
               this.a(world, Blocks.STONEBRICK.getBlockData(), i, j + 1, k, structureboundingbox);
               this.a(world, Blocks.STONEBRICK.getBlockData(), i, j + 2, k, structureboundingbox);
               this.a(world, Blocks.STONEBRICK.getBlockData(), i + 1, j + 2, k, structureboundingbox);
               this.a(world, Blocks.STONEBRICK.getBlockData(), i + 2, j + 2, k, structureboundingbox);
               this.a(world, Blocks.STONEBRICK.getBlockData(), i + 2, j + 1, k, structureboundingbox);
               this.a(world, Blocks.STONEBRICK.getBlockData(), i + 2, j, k, structureboundingbox);
               this.a(world, Blocks.WOODEN_DOOR.getBlockData(), i + 1, j, k, structureboundingbox);
               this.a(world, Blocks.WOODEN_DOOR.fromLegacyData(8), i + 1, j + 1, k, structureboundingbox);
               break;
            case 3:
               this.a(world, Blocks.AIR.getBlockData(), i + 1, j, k, structureboundingbox);
               this.a(world, Blocks.AIR.getBlockData(), i + 1, j + 1, k, structureboundingbox);
               this.a(world, Blocks.IRON_BARS.getBlockData(), i, j, k, structureboundingbox);
               this.a(world, Blocks.IRON_BARS.getBlockData(), i, j + 1, k, structureboundingbox);
               this.a(world, Blocks.IRON_BARS.getBlockData(), i, j + 2, k, structureboundingbox);
               this.a(world, Blocks.IRON_BARS.getBlockData(), i + 1, j + 2, k, structureboundingbox);
               this.a(world, Blocks.IRON_BARS.getBlockData(), i + 2, j + 2, k, structureboundingbox);
               this.a(world, Blocks.IRON_BARS.getBlockData(), i + 2, j + 1, k, structureboundingbox);
               this.a(world, Blocks.IRON_BARS.getBlockData(), i + 2, j, k, structureboundingbox);
               break;
            case 4:
               this.a(world, Blocks.STONEBRICK.getBlockData(), i, j, k, structureboundingbox);
               this.a(world, Blocks.STONEBRICK.getBlockData(), i, j + 1, k, structureboundingbox);
               this.a(world, Blocks.STONEBRICK.getBlockData(), i, j + 2, k, structureboundingbox);
               this.a(world, Blocks.STONEBRICK.getBlockData(), i + 1, j + 2, k, structureboundingbox);
               this.a(world, Blocks.STONEBRICK.getBlockData(), i + 2, j + 2, k, structureboundingbox);
               this.a(world, Blocks.STONEBRICK.getBlockData(), i + 2, j + 1, k, structureboundingbox);
               this.a(world, Blocks.STONEBRICK.getBlockData(), i + 2, j, k, structureboundingbox);
               this.a(world, Blocks.IRON_DOOR.getBlockData(), i + 1, j, k, structureboundingbox);
               this.a(world, Blocks.IRON_DOOR.fromLegacyData(8), i + 1, j + 1, k, structureboundingbox);
               this.a(world, Blocks.STONE_BUTTON.fromLegacyData(this.a(Blocks.STONE_BUTTON, 4)), i + 2, j + 1, k + 1, structureboundingbox);
               this.a(world, Blocks.STONE_BUTTON.fromLegacyData(this.a(Blocks.STONE_BUTTON, 3)), i + 2, j + 1, k - 1, structureboundingbox);
         }
      }

      protected WorldGenStrongholdPieces$WorldGenStrongholdPiece$WorldGenStrongholdPiece$WorldGenStrongholdDoorType a(Random random) {
         int i = random.nextInt(5);
         switch (i) {
            case 0:
            case 1:
            default:
               return WorldGenStrongholdPieces$WorldGenStrongholdPiece$WorldGenStrongholdPiece$WorldGenStrongholdDoorType.OPENING;
            case 2:
               return WorldGenStrongholdPieces$WorldGenStrongholdPiece$WorldGenStrongholdPiece$WorldGenStrongholdDoorType.WOOD_DOOR;
            case 3:
               return WorldGenStrongholdPieces$WorldGenStrongholdPiece$WorldGenStrongholdPiece$WorldGenStrongholdDoorType.GRATES;
            case 4:
               return WorldGenStrongholdPieces$WorldGenStrongholdPiece$WorldGenStrongholdPiece$WorldGenStrongholdDoorType.IRON_DOOR;
         }
      }

      protected StructurePiece a(
         WorldGenStrongholdPieces.WorldGenStrongholdStart worldgenstrongholdpieces_worldgenstrongholdstart,
         List<StructurePiece> list,
         Random random,
         int i,
         int j
      ) {
         if (this.m != null) {
            switch (WorldGenStrongholdPieces.SyntheticClass_1.b[this.m.ordinal()]) {
               case 1:
                  return WorldGenStrongholdPieces.c(
                     worldgenstrongholdpieces_worldgenstrongholdstart, list, random, this.l.a + i, this.l.b + j, this.l.c - 1, this.m, this.d()
                  );
               case 2:
                  return WorldGenStrongholdPieces.c(
                     worldgenstrongholdpieces_worldgenstrongholdstart, list, random, this.l.a + i, this.l.b + j, this.l.f + 1, this.m, this.d()
                  );
               case 3:
                  return WorldGenStrongholdPieces.c(
                     worldgenstrongholdpieces_worldgenstrongholdstart, list, random, this.l.a - 1, this.l.b + j, this.l.c + i, this.m, this.d()
                  );
               case 4:
                  return WorldGenStrongholdPieces.c(
                     worldgenstrongholdpieces_worldgenstrongholdstart, list, random, this.l.d + 1, this.l.b + j, this.l.c + i, this.m, this.d()
                  );
            }
         }

         return null;
      }

      protected StructurePiece b(
         WorldGenStrongholdPieces.WorldGenStrongholdStart worldgenstrongholdpieces_worldgenstrongholdstart,
         List<StructurePiece> list,
         Random random,
         int i,
         int j
      ) {
         if (this.m != null) {
            switch (WorldGenStrongholdPieces.SyntheticClass_1.b[this.m.ordinal()]) {
               case 1:
               case 2:
                  return WorldGenStrongholdPieces.c(
                     worldgenstrongholdpieces_worldgenstrongholdstart, list, random, this.l.a - 1, this.l.b + i, this.l.c + j, EnumDirection.WEST, this.d()
                  );
               case 3:
               case 4:
                  return WorldGenStrongholdPieces.c(
                     worldgenstrongholdpieces_worldgenstrongholdstart, list, random, this.l.a + j, this.l.b + i, this.l.c - 1, EnumDirection.NORTH, this.d()
                  );
            }
         }

         return null;
      }

      protected StructurePiece c(
         WorldGenStrongholdPieces.WorldGenStrongholdStart worldgenstrongholdpieces_worldgenstrongholdstart,
         List<StructurePiece> list,
         Random random,
         int i,
         int j
      ) {
         if (this.m != null) {
            switch (WorldGenStrongholdPieces.SyntheticClass_1.b[this.m.ordinal()]) {
               case 1:
               case 2:
                  return WorldGenStrongholdPieces.c(
                     worldgenstrongholdpieces_worldgenstrongholdstart, list, random, this.l.d + 1, this.l.b + i, this.l.c + j, EnumDirection.EAST, this.d()
                  );
               case 3:
               case 4:
                  return WorldGenStrongholdPieces.c(
                     worldgenstrongholdpieces_worldgenstrongholdstart, list, random, this.l.a + j, this.l.b + i, this.l.f + 1, EnumDirection.SOUTH, this.d()
                  );
            }
         }

         return null;
      }

      protected static boolean a(StructureBoundingBox structureboundingbox) {
         return structureboundingbox != null && structureboundingbox.b > 10;
      }
   }

   static class WorldGenStrongholdPieceWeight {
      public Class<? extends WorldGenStrongholdPieces.WorldGenStrongholdPiece> a;
      public final int b;
      public int c;
      public int d;

      public WorldGenStrongholdPieceWeight(Class<? extends WorldGenStrongholdPieces.WorldGenStrongholdPiece> oclass, int i, int j) {
         this.a = oclass;
         this.b = i;
         this.d = j;
      }

      public boolean a(int i) {
         return this.d == 0 || this.c < this.d;
      }

      public boolean a() {
         return this.d == 0 || this.c < this.d;
      }
   }

   public static class WorldGenStrongholdPortalRoom extends WorldGenStrongholdPieces.WorldGenStrongholdPiece {
      private boolean a;

      public WorldGenStrongholdPortalRoom() {
      }

      public WorldGenStrongholdPortalRoom(int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
         super(i);
         this.m = enumdirection;
         this.l = structureboundingbox;
      }

      @Override
      protected void a(NBTTagCompound nbttagcompound) {
         super.a(nbttagcompound);
         nbttagcompound.setBoolean("Mob", this.a);
      }

      @Override
      protected void b(NBTTagCompound nbttagcompound) {
         super.b(nbttagcompound);
         this.a = nbttagcompound.getBoolean("Mob");
      }

      @Override
      public void a(StructurePiece structurepiece, List<StructurePiece> list, Random random) {
         if (structurepiece != null) {
            ((WorldGenStrongholdPieces.WorldGenStrongholdStart)structurepiece).b = this;
         }
      }

      public static WorldGenStrongholdPieces.WorldGenStrongholdPortalRoom a(
         List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection, int l
      ) {
         StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, -4, -1, 0, 11, 8, 16, enumdirection);
         return a(structureboundingbox) && StructurePiece.a(list, structureboundingbox) == null
            ? new WorldGenStrongholdPieces.WorldGenStrongholdPortalRoom(l, random, structureboundingbox, enumdirection)
            : null;
      }

      @Override
      public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
         this.a(world, structureboundingbox, 0, 0, 0, 10, 7, 15, false, random, WorldGenStrongholdPieces.e);
         this.a(
            world,
            random,
            structureboundingbox,
            WorldGenStrongholdPieces$WorldGenStrongholdPiece$WorldGenStrongholdPiece$WorldGenStrongholdDoorType.GRATES,
            4,
            1,
            0
         );
         byte b0 = 6;
         this.a(world, structureboundingbox, 1, b0, 1, 1, b0, 14, false, random, WorldGenStrongholdPieces.e);
         this.a(world, structureboundingbox, 9, b0, 1, 9, b0, 14, false, random, WorldGenStrongholdPieces.e);
         this.a(world, structureboundingbox, 2, b0, 1, 8, b0, 2, false, random, WorldGenStrongholdPieces.e);
         this.a(world, structureboundingbox, 2, b0, 14, 8, b0, 14, false, random, WorldGenStrongholdPieces.e);
         this.a(world, structureboundingbox, 1, 1, 1, 2, 1, 4, false, random, WorldGenStrongholdPieces.e);
         this.a(world, structureboundingbox, 8, 1, 1, 9, 1, 4, false, random, WorldGenStrongholdPieces.e);
         this.a(world, structureboundingbox, 1, 1, 1, 1, 1, 3, Blocks.FLOWING_LAVA.getBlockData(), Blocks.FLOWING_LAVA.getBlockData(), false);
         this.a(world, structureboundingbox, 9, 1, 1, 9, 1, 3, Blocks.FLOWING_LAVA.getBlockData(), Blocks.FLOWING_LAVA.getBlockData(), false);
         this.a(world, structureboundingbox, 3, 1, 8, 7, 1, 12, false, random, WorldGenStrongholdPieces.e);
         this.a(world, structureboundingbox, 4, 1, 9, 6, 1, 11, Blocks.FLOWING_LAVA.getBlockData(), Blocks.FLOWING_LAVA.getBlockData(), false);

         for (int i = 3; i < 14; i += 2) {
            this.a(world, structureboundingbox, 0, 3, i, 0, 4, i, Blocks.IRON_BARS.getBlockData(), Blocks.IRON_BARS.getBlockData(), false);
            this.a(world, structureboundingbox, 10, 3, i, 10, 4, i, Blocks.IRON_BARS.getBlockData(), Blocks.IRON_BARS.getBlockData(), false);
         }

         for (int var15 = 2; var15 < 9; var15 += 2) {
            this.a(world, structureboundingbox, var15, 3, 15, var15, 4, 15, Blocks.IRON_BARS.getBlockData(), Blocks.IRON_BARS.getBlockData(), false);
         }

         int var16 = this.a(Blocks.STONE_BRICK_STAIRS, 3);
         this.a(world, structureboundingbox, 4, 1, 5, 6, 1, 7, false, random, WorldGenStrongholdPieces.e);
         this.a(world, structureboundingbox, 4, 2, 6, 6, 2, 7, false, random, WorldGenStrongholdPieces.e);
         this.a(world, structureboundingbox, 4, 3, 7, 6, 3, 7, false, random, WorldGenStrongholdPieces.e);

         for (int j = 4; j <= 6; j++) {
            this.a(world, Blocks.STONE_BRICK_STAIRS.fromLegacyData(var16), j, 1, 4, structureboundingbox);
            this.a(world, Blocks.STONE_BRICK_STAIRS.fromLegacyData(var16), j, 2, 5, structureboundingbox);
            this.a(world, Blocks.STONE_BRICK_STAIRS.fromLegacyData(var16), j, 3, 6, structureboundingbox);
         }

         int var17 = EnumDirection.NORTH.b();
         int k = EnumDirection.SOUTH.b();
         int l = EnumDirection.EAST.b();
         int i1 = EnumDirection.WEST.b();
         if (this.m != null) {
            switch (WorldGenStrongholdPieces.SyntheticClass_1.b[this.m.ordinal()]) {
               case 2:
                  var17 = EnumDirection.SOUTH.b();
                  k = EnumDirection.NORTH.b();
                  break;
               case 3:
                  var17 = EnumDirection.WEST.b();
                  k = EnumDirection.EAST.b();
                  l = EnumDirection.SOUTH.b();
                  i1 = EnumDirection.NORTH.b();
                  break;
               case 4:
                  var17 = EnumDirection.EAST.b();
                  k = EnumDirection.WEST.b();
                  l = EnumDirection.SOUTH.b();
                  i1 = EnumDirection.NORTH.b();
            }
         }

         this.a(world, Blocks.END_PORTAL_FRAME.fromLegacyData(var17).set(BlockEnderPortalFrame.EYE, random.nextFloat() > 0.9F), 4, 3, 8, structureboundingbox);
         this.a(world, Blocks.END_PORTAL_FRAME.fromLegacyData(var17).set(BlockEnderPortalFrame.EYE, random.nextFloat() > 0.9F), 5, 3, 8, structureboundingbox);
         this.a(world, Blocks.END_PORTAL_FRAME.fromLegacyData(var17).set(BlockEnderPortalFrame.EYE, random.nextFloat() > 0.9F), 6, 3, 8, structureboundingbox);
         this.a(world, Blocks.END_PORTAL_FRAME.fromLegacyData(k).set(BlockEnderPortalFrame.EYE, random.nextFloat() > 0.9F), 4, 3, 12, structureboundingbox);
         this.a(world, Blocks.END_PORTAL_FRAME.fromLegacyData(k).set(BlockEnderPortalFrame.EYE, random.nextFloat() > 0.9F), 5, 3, 12, structureboundingbox);
         this.a(world, Blocks.END_PORTAL_FRAME.fromLegacyData(k).set(BlockEnderPortalFrame.EYE, random.nextFloat() > 0.9F), 6, 3, 12, structureboundingbox);
         this.a(world, Blocks.END_PORTAL_FRAME.fromLegacyData(l).set(BlockEnderPortalFrame.EYE, random.nextFloat() > 0.9F), 3, 3, 9, structureboundingbox);
         this.a(world, Blocks.END_PORTAL_FRAME.fromLegacyData(l).set(BlockEnderPortalFrame.EYE, random.nextFloat() > 0.9F), 3, 3, 10, structureboundingbox);
         this.a(world, Blocks.END_PORTAL_FRAME.fromLegacyData(l).set(BlockEnderPortalFrame.EYE, random.nextFloat() > 0.9F), 3, 3, 11, structureboundingbox);
         this.a(world, Blocks.END_PORTAL_FRAME.fromLegacyData(i1).set(BlockEnderPortalFrame.EYE, random.nextFloat() > 0.9F), 7, 3, 9, structureboundingbox);
         this.a(world, Blocks.END_PORTAL_FRAME.fromLegacyData(i1).set(BlockEnderPortalFrame.EYE, random.nextFloat() > 0.9F), 7, 3, 10, structureboundingbox);
         this.a(world, Blocks.END_PORTAL_FRAME.fromLegacyData(i1).set(BlockEnderPortalFrame.EYE, random.nextFloat() > 0.9F), 7, 3, 11, structureboundingbox);
         if (!this.a) {
            int j1 = this.d(3);
            int x = this.a(5, 6);
            int z = this.b(5, 6);
            if (structureboundingbox.b(x, j1, z)) {
               BlockPosition blockposition = new BlockPosition(x, j1, z);
               this.a = true;
               world.setTypeAndData(blockposition, Blocks.MOB_SPAWNER.getBlockData(), 2);
               TileEntity tileentity = world.getTileEntity(blockposition);
               if (tileentity instanceof TileEntityMobSpawner) {
                  ((TileEntityMobSpawner)tileentity).getSpawner().setMobName("Silverfish");
               }
            }
         }

         return true;
      }
   }

   public static class WorldGenStrongholdPrison extends WorldGenStrongholdPieces.WorldGenStrongholdPiece {
      public WorldGenStrongholdPrison() {
      }

      public WorldGenStrongholdPrison(int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
         super(i);
         this.m = enumdirection;
         this.d = this.a(random);
         this.l = structureboundingbox;
      }

      @Override
      public void a(StructurePiece structurepiece, List<StructurePiece> list, Random random) {
         this.a((WorldGenStrongholdPieces.WorldGenStrongholdStart)structurepiece, list, random, 1, 1);
      }

      public static WorldGenStrongholdPieces.WorldGenStrongholdPrison a(
         List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection, int l
      ) {
         StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, -1, -1, 0, 9, 5, 11, enumdirection);
         return a(structureboundingbox) && StructurePiece.a(list, structureboundingbox) == null
            ? new WorldGenStrongholdPieces.WorldGenStrongholdPrison(l, random, structureboundingbox, enumdirection)
            : null;
      }

      @Override
      public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
         if (this.a(world, structureboundingbox)) {
            return false;
         } else {
            this.a(world, structureboundingbox, 0, 0, 0, 8, 4, 10, true, random, WorldGenStrongholdPieces.e);
            this.a(world, random, structureboundingbox, this.d, 1, 1, 0);
            this.a(world, structureboundingbox, 1, 1, 10, 3, 3, 10, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 4, 1, 1, 4, 3, 1, false, random, WorldGenStrongholdPieces.e);
            this.a(world, structureboundingbox, 4, 1, 3, 4, 3, 3, false, random, WorldGenStrongholdPieces.e);
            this.a(world, structureboundingbox, 4, 1, 7, 4, 3, 7, false, random, WorldGenStrongholdPieces.e);
            this.a(world, structureboundingbox, 4, 1, 9, 4, 3, 9, false, random, WorldGenStrongholdPieces.e);
            this.a(world, structureboundingbox, 4, 1, 4, 4, 3, 6, Blocks.IRON_BARS.getBlockData(), Blocks.IRON_BARS.getBlockData(), false);
            this.a(world, structureboundingbox, 5, 1, 5, 7, 3, 5, Blocks.IRON_BARS.getBlockData(), Blocks.IRON_BARS.getBlockData(), false);
            this.a(world, Blocks.IRON_BARS.getBlockData(), 4, 3, 2, structureboundingbox);
            this.a(world, Blocks.IRON_BARS.getBlockData(), 4, 3, 8, structureboundingbox);
            this.a(world, Blocks.IRON_DOOR.fromLegacyData(this.a(Blocks.IRON_DOOR, 3)), 4, 1, 2, structureboundingbox);
            this.a(world, Blocks.IRON_DOOR.fromLegacyData(this.a(Blocks.IRON_DOOR, 3) + 8), 4, 2, 2, structureboundingbox);
            this.a(world, Blocks.IRON_DOOR.fromLegacyData(this.a(Blocks.IRON_DOOR, 3)), 4, 1, 8, structureboundingbox);
            this.a(world, Blocks.IRON_DOOR.fromLegacyData(this.a(Blocks.IRON_DOOR, 3) + 8), 4, 2, 8, structureboundingbox);
            return true;
         }
      }
   }

   public static class WorldGenStrongholdRightTurn extends WorldGenStrongholdPieces.WorldGenStrongholdLeftTurn {
      @Override
      public void a(StructurePiece structurepiece, List<StructurePiece> list, Random random) {
         if (this.m != EnumDirection.NORTH && this.m != EnumDirection.EAST) {
            this.b((WorldGenStrongholdPieces.WorldGenStrongholdStart)structurepiece, list, random, 1, 1);
         } else {
            this.c((WorldGenStrongholdPieces.WorldGenStrongholdStart)structurepiece, list, random, 1, 1);
         }
      }

      @Override
      public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
         if (this.a(world, structureboundingbox)) {
            return false;
         } else {
            this.a(world, structureboundingbox, 0, 0, 0, 4, 4, 4, true, random, WorldGenStrongholdPieces.e);
            this.a(world, random, structureboundingbox, this.d, 1, 1, 0);
            if (this.m != EnumDirection.NORTH && this.m != EnumDirection.EAST) {
               this.a(world, structureboundingbox, 0, 1, 1, 0, 3, 3, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            } else {
               this.a(world, structureboundingbox, 4, 1, 1, 4, 3, 3, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            }

            return true;
         }
      }
   }

   public static class WorldGenStrongholdRoomCrossing extends WorldGenStrongholdPieces.WorldGenStrongholdPiece {
      private static final List<StructurePieceTreasure> b = Lists.newArrayList(
         new StructurePieceTreasure[]{
            new StructurePieceTreasure(Items.IRON_INGOT, 0, 1, 5, 10),
            new StructurePieceTreasure(Items.GOLD_INGOT, 0, 1, 3, 5),
            new StructurePieceTreasure(Items.REDSTONE, 0, 4, 9, 5),
            new StructurePieceTreasure(Items.COAL, 0, 3, 8, 10),
            new StructurePieceTreasure(Items.BREAD, 0, 1, 3, 15),
            new StructurePieceTreasure(Items.APPLE, 0, 1, 3, 15),
            new StructurePieceTreasure(Items.IRON_PICKAXE, 0, 1, 1, 1)
         }
      );
      protected int a;

      public WorldGenStrongholdRoomCrossing() {
      }

      public WorldGenStrongholdRoomCrossing(int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
         super(i);
         this.m = enumdirection;
         this.d = this.a(random);
         this.l = structureboundingbox;
         this.a = random.nextInt(5);
      }

      @Override
      protected void a(NBTTagCompound nbttagcompound) {
         super.a(nbttagcompound);
         nbttagcompound.setInt("Type", this.a);
      }

      @Override
      protected void b(NBTTagCompound nbttagcompound) {
         super.b(nbttagcompound);
         this.a = nbttagcompound.getInt("Type");
      }

      @Override
      public void a(StructurePiece structurepiece, List<StructurePiece> list, Random random) {
         this.a((WorldGenStrongholdPieces.WorldGenStrongholdStart)structurepiece, list, random, 4, 1);
         this.b((WorldGenStrongholdPieces.WorldGenStrongholdStart)structurepiece, list, random, 1, 4);
         this.c((WorldGenStrongholdPieces.WorldGenStrongholdStart)structurepiece, list, random, 1, 4);
      }

      public static WorldGenStrongholdPieces.WorldGenStrongholdRoomCrossing a(
         List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection, int l
      ) {
         StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, -4, -1, 0, 11, 7, 11, enumdirection);
         return a(structureboundingbox) && StructurePiece.a(list, structureboundingbox) == null
            ? new WorldGenStrongholdPieces.WorldGenStrongholdRoomCrossing(l, random, structureboundingbox, enumdirection)
            : null;
      }

      @Override
      public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
         if (this.a(world, structureboundingbox)) {
            return false;
         } else {
            this.a(world, structureboundingbox, 0, 0, 0, 10, 6, 10, true, random, WorldGenStrongholdPieces.e);
            this.a(world, random, structureboundingbox, this.d, 4, 1, 0);
            this.a(world, structureboundingbox, 4, 1, 10, 6, 3, 10, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 0, 1, 4, 0, 3, 6, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 10, 1, 4, 10, 3, 6, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            switch (this.a) {
               case 0:
                  this.a(world, Blocks.STONEBRICK.getBlockData(), 5, 1, 5, structureboundingbox);
                  this.a(world, Blocks.STONEBRICK.getBlockData(), 5, 2, 5, structureboundingbox);
                  this.a(world, Blocks.STONEBRICK.getBlockData(), 5, 3, 5, structureboundingbox);
                  this.a(world, Blocks.TORCH.getBlockData(), 4, 3, 5, structureboundingbox);
                  this.a(world, Blocks.TORCH.getBlockData(), 6, 3, 5, structureboundingbox);
                  this.a(world, Blocks.TORCH.getBlockData(), 5, 3, 4, structureboundingbox);
                  this.a(world, Blocks.TORCH.getBlockData(), 5, 3, 6, structureboundingbox);
                  this.a(world, Blocks.STONE_SLAB.getBlockData(), 4, 1, 4, structureboundingbox);
                  this.a(world, Blocks.STONE_SLAB.getBlockData(), 4, 1, 5, structureboundingbox);
                  this.a(world, Blocks.STONE_SLAB.getBlockData(), 4, 1, 6, structureboundingbox);
                  this.a(world, Blocks.STONE_SLAB.getBlockData(), 6, 1, 4, structureboundingbox);
                  this.a(world, Blocks.STONE_SLAB.getBlockData(), 6, 1, 5, structureboundingbox);
                  this.a(world, Blocks.STONE_SLAB.getBlockData(), 6, 1, 6, structureboundingbox);
                  this.a(world, Blocks.STONE_SLAB.getBlockData(), 5, 1, 4, structureboundingbox);
                  this.a(world, Blocks.STONE_SLAB.getBlockData(), 5, 1, 6, structureboundingbox);
                  break;
               case 1:
                  for (int i = 0; i < 5; i++) {
                     this.a(world, Blocks.STONEBRICK.getBlockData(), 3, 1, 3 + i, structureboundingbox);
                     this.a(world, Blocks.STONEBRICK.getBlockData(), 7, 1, 3 + i, structureboundingbox);
                     this.a(world, Blocks.STONEBRICK.getBlockData(), 3 + i, 1, 3, structureboundingbox);
                     this.a(world, Blocks.STONEBRICK.getBlockData(), 3 + i, 1, 7, structureboundingbox);
                  }

                  this.a(world, Blocks.STONEBRICK.getBlockData(), 5, 1, 5, structureboundingbox);
                  this.a(world, Blocks.STONEBRICK.getBlockData(), 5, 2, 5, structureboundingbox);
                  this.a(world, Blocks.STONEBRICK.getBlockData(), 5, 3, 5, structureboundingbox);
                  this.a(world, Blocks.FLOWING_WATER.getBlockData(), 5, 4, 5, structureboundingbox);
                  break;
               case 2:
                  for (int i = 1; i <= 9; i++) {
                     this.a(world, Blocks.COBBLESTONE.getBlockData(), 1, 3, i, structureboundingbox);
                     this.a(world, Blocks.COBBLESTONE.getBlockData(), 9, 3, i, structureboundingbox);
                  }

                  for (int var5 = 1; var5 <= 9; var5++) {
                     this.a(world, Blocks.COBBLESTONE.getBlockData(), var5, 3, 1, structureboundingbox);
                     this.a(world, Blocks.COBBLESTONE.getBlockData(), var5, 3, 9, structureboundingbox);
                  }

                  this.a(world, Blocks.COBBLESTONE.getBlockData(), 5, 1, 4, structureboundingbox);
                  this.a(world, Blocks.COBBLESTONE.getBlockData(), 5, 1, 6, structureboundingbox);
                  this.a(world, Blocks.COBBLESTONE.getBlockData(), 5, 3, 4, structureboundingbox);
                  this.a(world, Blocks.COBBLESTONE.getBlockData(), 5, 3, 6, structureboundingbox);
                  this.a(world, Blocks.COBBLESTONE.getBlockData(), 4, 1, 5, structureboundingbox);
                  this.a(world, Blocks.COBBLESTONE.getBlockData(), 6, 1, 5, structureboundingbox);
                  this.a(world, Blocks.COBBLESTONE.getBlockData(), 4, 3, 5, structureboundingbox);
                  this.a(world, Blocks.COBBLESTONE.getBlockData(), 6, 3, 5, structureboundingbox);

                  for (int var6 = 1; var6 <= 3; var6++) {
                     this.a(world, Blocks.COBBLESTONE.getBlockData(), 4, var6, 4, structureboundingbox);
                     this.a(world, Blocks.COBBLESTONE.getBlockData(), 6, var6, 4, structureboundingbox);
                     this.a(world, Blocks.COBBLESTONE.getBlockData(), 4, var6, 6, structureboundingbox);
                     this.a(world, Blocks.COBBLESTONE.getBlockData(), 6, var6, 6, structureboundingbox);
                  }

                  this.a(world, Blocks.TORCH.getBlockData(), 5, 3, 5, structureboundingbox);

                  for (int var7 = 2; var7 <= 8; var7++) {
                     this.a(world, Blocks.PLANKS.getBlockData(), 2, 3, var7, structureboundingbox);
                     this.a(world, Blocks.PLANKS.getBlockData(), 3, 3, var7, structureboundingbox);
                     if (var7 <= 3 || var7 >= 7) {
                        this.a(world, Blocks.PLANKS.getBlockData(), 4, 3, var7, structureboundingbox);
                        this.a(world, Blocks.PLANKS.getBlockData(), 5, 3, var7, structureboundingbox);
                        this.a(world, Blocks.PLANKS.getBlockData(), 6, 3, var7, structureboundingbox);
                     }

                     this.a(world, Blocks.PLANKS.getBlockData(), 7, 3, var7, structureboundingbox);
                     this.a(world, Blocks.PLANKS.getBlockData(), 8, 3, var7, structureboundingbox);
                  }

                  this.a(world, Blocks.LADDER.fromLegacyData(this.a(Blocks.LADDER, EnumDirection.WEST.a())), 9, 1, 3, structureboundingbox);
                  this.a(world, Blocks.LADDER.fromLegacyData(this.a(Blocks.LADDER, EnumDirection.WEST.a())), 9, 2, 3, structureboundingbox);
                  this.a(world, Blocks.LADDER.fromLegacyData(this.a(Blocks.LADDER, EnumDirection.WEST.a())), 9, 3, 3, structureboundingbox);
                  this.a(world, structureboundingbox, random, 3, 4, 8, StructurePieceTreasure.a(b, Items.ENCHANTED_BOOK.b(random)), 1 + random.nextInt(4));
            }

            return true;
         }
      }
   }

   public static class WorldGenStrongholdStairs extends WorldGenStrongholdPieces.WorldGenStrongholdPiece {
      private boolean a;
      private boolean b;

      public WorldGenStrongholdStairs() {
      }

      public WorldGenStrongholdStairs(int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
         super(i);
         this.m = enumdirection;
         this.d = this.a(random);
         this.l = structureboundingbox;
         this.a = random.nextInt(2) == 0;
         this.b = random.nextInt(2) == 0;
      }

      @Override
      protected void a(NBTTagCompound nbttagcompound) {
         super.a(nbttagcompound);
         nbttagcompound.setBoolean("Left", this.a);
         nbttagcompound.setBoolean("Right", this.b);
      }

      @Override
      protected void b(NBTTagCompound nbttagcompound) {
         super.b(nbttagcompound);
         this.a = nbttagcompound.getBoolean("Left");
         this.b = nbttagcompound.getBoolean("Right");
      }

      @Override
      public void a(StructurePiece structurepiece, List<StructurePiece> list, Random random) {
         this.a((WorldGenStrongholdPieces.WorldGenStrongholdStart)structurepiece, list, random, 1, 1);
         if (this.a) {
            this.b((WorldGenStrongholdPieces.WorldGenStrongholdStart)structurepiece, list, random, 1, 2);
         }

         if (this.b) {
            this.c((WorldGenStrongholdPieces.WorldGenStrongholdStart)structurepiece, list, random, 1, 2);
         }
      }

      public static WorldGenStrongholdPieces.WorldGenStrongholdStairs a(
         List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection, int l
      ) {
         StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, -1, -1, 0, 5, 5, 7, enumdirection);
         return a(structureboundingbox) && StructurePiece.a(list, structureboundingbox) == null
            ? new WorldGenStrongholdPieces.WorldGenStrongholdStairs(l, random, structureboundingbox, enumdirection)
            : null;
      }

      @Override
      public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
         if (this.a(world, structureboundingbox)) {
            return false;
         } else {
            this.a(world, structureboundingbox, 0, 0, 0, 4, 4, 6, true, random, WorldGenStrongholdPieces.e);
            this.a(world, random, structureboundingbox, this.d, 1, 1, 0);
            this.a(
               world,
               random,
               structureboundingbox,
               WorldGenStrongholdPieces$WorldGenStrongholdPiece$WorldGenStrongholdPiece$WorldGenStrongholdDoorType.OPENING,
               1,
               1,
               6
            );
            this.a(world, structureboundingbox, random, 0.1F, 1, 2, 1, Blocks.TORCH.getBlockData());
            this.a(world, structureboundingbox, random, 0.1F, 3, 2, 1, Blocks.TORCH.getBlockData());
            this.a(world, structureboundingbox, random, 0.1F, 1, 2, 5, Blocks.TORCH.getBlockData());
            this.a(world, structureboundingbox, random, 0.1F, 3, 2, 5, Blocks.TORCH.getBlockData());
            if (this.a) {
               this.a(world, structureboundingbox, 0, 1, 2, 0, 3, 4, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            }

            if (this.b) {
               this.a(world, structureboundingbox, 4, 1, 2, 4, 3, 4, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            }

            return true;
         }
      }
   }

   public static class WorldGenStrongholdStairs2 extends WorldGenStrongholdPieces.WorldGenStrongholdPiece {
      private boolean a;

      public WorldGenStrongholdStairs2() {
      }

      public WorldGenStrongholdStairs2(int i, Random random, int j, int k) {
         super(i);
         this.a = true;
         this.m = EnumDirection.EnumDirectionLimit.HORIZONTAL.a(random);
         this.d = WorldGenStrongholdPieces$WorldGenStrongholdPiece$WorldGenStrongholdPiece$WorldGenStrongholdDoorType.OPENING;
         switch (WorldGenStrongholdPieces.SyntheticClass_1.b[this.m.ordinal()]) {
            case 1:
            case 2:
            default:
               this.l = new StructureBoundingBox(j, 64, k, j + 5 - 1, 74, k + 5 - 1);
         }
      }

      public WorldGenStrongholdStairs2(int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
         super(i);
         this.a = false;
         this.m = enumdirection;
         this.d = this.a(random);
         this.l = structureboundingbox;
      }

      @Override
      protected void a(NBTTagCompound nbttagcompound) {
         super.a(nbttagcompound);
         nbttagcompound.setBoolean("Source", this.a);
      }

      @Override
      protected void b(NBTTagCompound nbttagcompound) {
         super.b(nbttagcompound);
         this.a = nbttagcompound.getBoolean("Source");
      }

      @Override
      public void a(StructurePiece structurepiece, List<StructurePiece> list, Random random) {
         if (this.a) {
            WorldGenStrongholdPieces.d = WorldGenStrongholdPieces.WorldGenStrongholdCrossing.class;
         }

         this.a((WorldGenStrongholdPieces.WorldGenStrongholdStart)structurepiece, list, random, 1, 1);
      }

      public static WorldGenStrongholdPieces.WorldGenStrongholdStairs2 a(
         List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection, int l
      ) {
         StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, -1, -7, 0, 5, 11, 5, enumdirection);
         return a(structureboundingbox) && StructurePiece.a(list, structureboundingbox) == null
            ? new WorldGenStrongholdPieces.WorldGenStrongholdStairs2(l, random, structureboundingbox, enumdirection)
            : null;
      }

      @Override
      public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
         if (this.a(world, structureboundingbox)) {
            return false;
         } else {
            this.a(world, structureboundingbox, 0, 0, 0, 4, 10, 4, true, random, WorldGenStrongholdPieces.e);
            this.a(world, random, structureboundingbox, this.d, 1, 7, 0);
            this.a(
               world,
               random,
               structureboundingbox,
               WorldGenStrongholdPieces$WorldGenStrongholdPiece$WorldGenStrongholdPiece$WorldGenStrongholdDoorType.OPENING,
               1,
               1,
               4
            );
            this.a(world, Blocks.STONEBRICK.getBlockData(), 2, 6, 1, structureboundingbox);
            this.a(world, Blocks.STONEBRICK.getBlockData(), 1, 5, 1, structureboundingbox);
            this.a(world, Blocks.STONE_SLAB.fromLegacyData(BlockDoubleStepAbstract.EnumStoneSlabVariant.STONE.a()), 1, 6, 1, structureboundingbox);
            this.a(world, Blocks.STONEBRICK.getBlockData(), 1, 5, 2, structureboundingbox);
            this.a(world, Blocks.STONEBRICK.getBlockData(), 1, 4, 3, structureboundingbox);
            this.a(world, Blocks.STONE_SLAB.fromLegacyData(BlockDoubleStepAbstract.EnumStoneSlabVariant.STONE.a()), 1, 5, 3, structureboundingbox);
            this.a(world, Blocks.STONEBRICK.getBlockData(), 2, 4, 3, structureboundingbox);
            this.a(world, Blocks.STONEBRICK.getBlockData(), 3, 3, 3, structureboundingbox);
            this.a(world, Blocks.STONE_SLAB.fromLegacyData(BlockDoubleStepAbstract.EnumStoneSlabVariant.STONE.a()), 3, 4, 3, structureboundingbox);
            this.a(world, Blocks.STONEBRICK.getBlockData(), 3, 3, 2, structureboundingbox);
            this.a(world, Blocks.STONEBRICK.getBlockData(), 3, 2, 1, structureboundingbox);
            this.a(world, Blocks.STONE_SLAB.fromLegacyData(BlockDoubleStepAbstract.EnumStoneSlabVariant.STONE.a()), 3, 3, 1, structureboundingbox);
            this.a(world, Blocks.STONEBRICK.getBlockData(), 2, 2, 1, structureboundingbox);
            this.a(world, Blocks.STONEBRICK.getBlockData(), 1, 1, 1, structureboundingbox);
            this.a(world, Blocks.STONE_SLAB.fromLegacyData(BlockDoubleStepAbstract.EnumStoneSlabVariant.STONE.a()), 1, 2, 1, structureboundingbox);
            this.a(world, Blocks.STONEBRICK.getBlockData(), 1, 1, 2, structureboundingbox);
            this.a(world, Blocks.STONE_SLAB.fromLegacyData(BlockDoubleStepAbstract.EnumStoneSlabVariant.STONE.a()), 1, 1, 3, structureboundingbox);
            return true;
         }
      }
   }

   public static class WorldGenStrongholdStairsStraight extends WorldGenStrongholdPieces.WorldGenStrongholdPiece {
      public WorldGenStrongholdStairsStraight() {
      }

      public WorldGenStrongholdStairsStraight(int i, Random random, StructureBoundingBox structureboundingbox, EnumDirection enumdirection) {
         super(i);
         this.m = enumdirection;
         this.d = this.a(random);
         this.l = structureboundingbox;
      }

      @Override
      public void a(StructurePiece structurepiece, List<StructurePiece> list, Random random) {
         this.a((WorldGenStrongholdPieces.WorldGenStrongholdStart)structurepiece, list, random, 1, 1);
      }

      public static WorldGenStrongholdPieces.WorldGenStrongholdStairsStraight a(
         List<StructurePiece> list, Random random, int i, int j, int k, EnumDirection enumdirection, int l
      ) {
         StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, j, k, -1, -7, 0, 5, 11, 8, enumdirection);
         return a(structureboundingbox) && StructurePiece.a(list, structureboundingbox) == null
            ? new WorldGenStrongholdPieces.WorldGenStrongholdStairsStraight(l, random, structureboundingbox, enumdirection)
            : null;
      }

      @Override
      public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
         if (this.a(world, structureboundingbox)) {
            return false;
         } else {
            this.a(world, structureboundingbox, 0, 0, 0, 4, 10, 7, true, random, WorldGenStrongholdPieces.e);
            this.a(world, random, structureboundingbox, this.d, 1, 7, 0);
            this.a(
               world,
               random,
               structureboundingbox,
               WorldGenStrongholdPieces$WorldGenStrongholdPiece$WorldGenStrongholdPiece$WorldGenStrongholdDoorType.OPENING,
               1,
               1,
               7
            );
            int i = this.a(Blocks.STONE_STAIRS, 2);

            for (int j = 0; j < 6; j++) {
               this.a(world, Blocks.STONE_STAIRS.fromLegacyData(i), 1, 6 - j, 1 + j, structureboundingbox);
               this.a(world, Blocks.STONE_STAIRS.fromLegacyData(i), 2, 6 - j, 1 + j, structureboundingbox);
               this.a(world, Blocks.STONE_STAIRS.fromLegacyData(i), 3, 6 - j, 1 + j, structureboundingbox);
               if (j < 5) {
                  this.a(world, Blocks.STONEBRICK.getBlockData(), 1, 5 - j, 1 + j, structureboundingbox);
                  this.a(world, Blocks.STONEBRICK.getBlockData(), 2, 5 - j, 1 + j, structureboundingbox);
                  this.a(world, Blocks.STONEBRICK.getBlockData(), 3, 5 - j, 1 + j, structureboundingbox);
               }
            }

            return true;
         }
      }
   }

   public static class WorldGenStrongholdStart extends WorldGenStrongholdPieces.WorldGenStrongholdStairs2 {
      public WorldGenStrongholdPieces.WorldGenStrongholdPieceWeight a;
      public WorldGenStrongholdPieces.WorldGenStrongholdPortalRoom b;
      public List<StructurePiece> c = Lists.newArrayList();

      public WorldGenStrongholdStart() {
      }

      public WorldGenStrongholdStart(int i, Random random, int j, int k) {
         super(0, random, j, k);
      }

      @Override
      public BlockPosition a() {
         return this.b != null ? this.b.a() : super.a();
      }
   }

   static class WorldGenStrongholdStones extends StructurePiece.StructurePieceBlockSelector {
      private WorldGenStrongholdStones() {
      }

      @Override
      public void a(Random random, int i, int j, int k, boolean flag) {
         if (flag) {
            float f = random.nextFloat();
            if (f < 0.2F) {
               this.a = Blocks.STONEBRICK.fromLegacyData(BlockSmoothBrick.O);
            } else if (f < 0.5F) {
               this.a = Blocks.STONEBRICK.fromLegacyData(BlockSmoothBrick.N);
            } else if (f < 0.55F) {
               this.a = Blocks.MONSTER_EGG.fromLegacyData(BlockMonsterEggs.EnumMonsterEggVarient.STONEBRICK.a());
            } else {
               this.a = Blocks.STONEBRICK.getBlockData();
            }
         } else {
            this.a = Blocks.AIR.getBlockData();
         }
      }

      WorldGenStrongholdStones(Object object) {
         this();
      }
   }
}
