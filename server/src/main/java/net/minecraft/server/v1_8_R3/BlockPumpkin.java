package net.minecraft.server.v1_8_R3;

import com.google.common.base.Predicate;
import org.bukkit.craftbukkit.v1_8_R3.util.BlockStateListPopulator;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class BlockPumpkin extends BlockDirectional {
   private ShapeDetector snowGolemPart;
   private ShapeDetector snowGolem;
   private ShapeDetector ironGolemPart;
   private ShapeDetector ironGolem;
   private static final Predicate<IBlockData> Q = new Predicate() {
      public boolean a(IBlockData iblockdata) {
         return iblockdata != null && (iblockdata.getBlock() == Blocks.PUMPKIN || iblockdata.getBlock() == Blocks.LIT_PUMPKIN);
      }

      public boolean apply(Object object) {
         return this.a((IBlockData)object);
      }
   };

   protected BlockPumpkin() {
      super(Material.PUMPKIN, MaterialMapColor.q);
      this.j(this.blockStateList.getBlockData().set(FACING, EnumDirection.NORTH));
      this.a(true);
      this.a(CreativeModeTab.b);
   }

   @Override
   public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
      super.onPlace(world, blockposition, iblockdata);
      this.f(world, blockposition);
   }

   public boolean e(World world, BlockPosition blockposition) {
      return this.getDetectorSnowGolemPart().a(world, blockposition) != null || this.getDetectorIronGolemPart().a(world, blockposition) != null;
   }

   private void f(World world, BlockPosition blockposition) {
      ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection;
      if ((shapedetector_shapedetectorcollection = this.getDetectorSnowGolem().a(world, blockposition)) != null) {
         BlockStateListPopulator blockList = new BlockStateListPopulator(world.getWorld());

         for (int i = 0; i < this.getDetectorSnowGolem().b(); i++) {
            ShapeDetectorBlock shapedetectorblock = shapedetector_shapedetectorcollection.a(0, i, 0);
            BlockPosition pos = shapedetectorblock.getPosition();
            blockList.setTypeId(pos.getX(), pos.getY(), pos.getZ(), 0);
         }

         EntitySnowman entitysnowman = new EntitySnowman(world);
         BlockPosition blockposition1 = shapedetector_shapedetectorcollection.a(0, 2, 0).getPosition();
         entitysnowman.setPositionRotation(blockposition1.getX() + 0.5, blockposition1.getY() + 0.05, blockposition1.getZ() + 0.5, 0.0F, 0.0F);
         if (world.addEntity(entitysnowman, SpawnReason.BUILD_SNOWMAN)) {
            blockList.updateList();

            for (int j = 0; j < 120; j++) {
               world.addParticle(
                  EnumParticle.SNOW_SHOVEL,
                  blockposition1.getX() + world.random.nextDouble(),
                  blockposition1.getY() + world.random.nextDouble() * 2.5,
                  blockposition1.getZ() + world.random.nextDouble(),
                  0.0,
                  0.0,
                  0.0,
                  EnumParticle.EMPTY_ARRAY
               );
            }

            for (int var13 = 0; var13 < this.getDetectorSnowGolem().b(); var13++) {
               ShapeDetectorBlock shapedetectorblock1 = shapedetector_shapedetectorcollection.a(0, var13, 0);
               world.update(shapedetectorblock1.getPosition(), Blocks.AIR);
            }
         }
      } else if ((shapedetector_shapedetectorcollection = this.getDetectorIronGolem().a(world, blockposition)) != null) {
         BlockStateListPopulator blockList = new BlockStateListPopulator(world.getWorld());

         for (int i = 0; i < this.getDetectorIronGolem().c(); i++) {
            for (int k = 0; k < this.getDetectorIronGolem().b(); k++) {
               BlockPosition pos = shapedetector_shapedetectorcollection.a(i, k, 0).getPosition();
               blockList.setTypeId(pos.getX(), pos.getY(), pos.getZ(), 0);
            }
         }

         BlockPosition blockposition2 = shapedetector_shapedetectorcollection.a(1, 2, 0).getPosition();
         EntityIronGolem entityirongolem = new EntityIronGolem(world);
         entityirongolem.setPlayerCreated(true);
         entityirongolem.setPositionRotation(blockposition2.getX() + 0.5, blockposition2.getY() + 0.05, blockposition2.getZ() + 0.5, 0.0F, 0.0F);
         if (world.addEntity(entityirongolem, SpawnReason.BUILD_IRONGOLEM)) {
            blockList.updateList();

            for (int j = 0; j < 120; j++) {
               world.addParticle(
                  EnumParticle.SNOWBALL,
                  blockposition2.getX() + world.random.nextDouble(),
                  blockposition2.getY() + world.random.nextDouble() * 3.9,
                  blockposition2.getZ() + world.random.nextDouble(),
                  0.0,
                  0.0,
                  0.0,
                  EnumParticle.EMPTY_ARRAY
               );
            }

            for (int var15 = 0; var15 < this.getDetectorIronGolem().c(); var15++) {
               for (int l = 0; l < this.getDetectorIronGolem().b(); l++) {
                  ShapeDetectorBlock shapedetectorblock2 = shapedetector_shapedetectorcollection.a(var15, l, 0);
                  world.update(shapedetectorblock2.getPosition(), Blocks.AIR);
               }
            }
         }
      }
   }

   @Override
   public boolean canPlace(World world, BlockPosition blockposition) {
      return world.getType(blockposition).getBlock().material.isReplaceable()
         && World.a(world, blockposition.getX(), blockposition.getY() - 1, blockposition.getZ());
   }

   @Override
   public IBlockData getPlacedState(
      World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving
   ) {
      return this.getBlockData().set(FACING, entityliving.getDirection().opposite());
   }

   @Override
   public IBlockData fromLegacyData(int i) {
      return this.getBlockData().set(FACING, EnumDirection.fromType2(i));
   }

   @Override
   public int toLegacyData(IBlockData iblockdata) {
      return iblockdata.get(FACING).b();
   }

   @Override
   public BlockStateList getStateList() {
      return new BlockStateList(this, FACING);
   }

   protected ShapeDetector getDetectorSnowGolemPart() {
      if (this.snowGolemPart == null) {
         this.snowGolemPart = ShapeDetectorBuilder.a().a(" ", "#", "#").a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.SNOW))).b();
      }

      return this.snowGolemPart;
   }

   protected ShapeDetector getDetectorSnowGolem() {
      if (this.snowGolem == null) {
         this.snowGolem = ShapeDetectorBuilder.a()
            .a("^", "#", "#")
            .a('^', ShapeDetectorBlock.a(Q))
            .a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.SNOW)))
            .b();
      }

      return this.snowGolem;
   }

   protected ShapeDetector getDetectorIronGolemPart() {
      if (this.ironGolemPart == null) {
         this.ironGolemPart = ShapeDetectorBuilder.a()
            .a("~ ~", "###", "~#~")
            .a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.IRON_BLOCK)))
            .a('~', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.AIR)))
            .b();
      }

      return this.ironGolemPart;
   }

   protected ShapeDetector getDetectorIronGolem() {
      if (this.ironGolem == null) {
         this.ironGolem = ShapeDetectorBuilder.a()
            .a("~^~", "###", "~#~")
            .a('^', ShapeDetectorBlock.a(Q))
            .a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.IRON_BLOCK)))
            .a('~', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.AIR)))
            .b();
      }

      return this.ironGolem;
   }
}
