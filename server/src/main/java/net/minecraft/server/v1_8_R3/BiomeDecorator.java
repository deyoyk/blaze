package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.generator.Generator;
import java.util.Random;

public class BiomeDecorator {
   protected World a;
   protected Random b;
   protected BlockPosition c;
   protected CustomWorldSettingsFinal d;
   protected WorldGenerator e = new WorldGenClay(4);
   protected WorldGenerator f = new WorldGenSand(Blocks.SAND, 7);
   protected WorldGenerator g = new WorldGenSand(Blocks.GRAVEL, 6);
   protected WorldGenerator h;
   protected WorldGenerator i;
   protected WorldGenerator j;
   protected WorldGenerator k;
   protected WorldGenerator l;
   protected WorldGenerator m;
   protected WorldGenerator n;
   protected WorldGenerator o;
   protected WorldGenerator p;
   protected WorldGenerator q;
   protected WorldGenerator r;
   protected WorldGenFlowers s = new WorldGenFlowers(Blocks.YELLOW_FLOWER, BlockFlowers.EnumFlowerVarient.DANDELION);
   protected WorldGenerator t = new WorldGenMushrooms(Blocks.BROWN_MUSHROOM);
   protected WorldGenerator u = new WorldGenMushrooms(Blocks.RED_MUSHROOM);
   protected WorldGenerator v = new WorldGenHugeMushroom();
   protected WorldGenerator w = new WorldGenReed();
   protected WorldGenerator x = new WorldGenCactus();
   protected WorldGenerator y = new WorldGenWaterLily();
   protected int z;
   protected int A;
   protected int B = 2;
   protected int C = 1;
   protected int D;
   protected int E;
   protected int F;
   protected int G;
   protected int H = 1;
   protected int I = 3;
   protected int J = 1;
   protected int K;
   public boolean L = true;

   public void a(World world, Random random, BiomeBase biomebase, BlockPosition blockposition) {
      this.a = world;
      String s = world.getWorldData().getGeneratorOptions();
      Generator generator = world.generatorConfig;
      if (s != null) {
         this.d = CustomWorldSettingsFinal.CustomWorldSettings.a(s).b();
      } else {
         this.d = CustomWorldSettingsFinal.CustomWorldSettings.a("").b();
      }

      this.b = random;
      this.c = blockposition;
      this.h = new WorldGenMinable(Blocks.DIRT.getBlockData(), this.d.I);
      this.i = new WorldGenMinable(Blocks.GRAVEL.getBlockData(), this.d.M);
      this.j = new WorldGenMinable(Blocks.STONE.getBlockData().set(BlockStone.VARIANT, BlockStone.EnumStoneVariant.GRANITE), this.d.Q);
      this.k = new WorldGenMinable(Blocks.STONE.getBlockData().set(BlockStone.VARIANT, BlockStone.EnumStoneVariant.DIORITE), this.d.U);
      this.l = new WorldGenMinable(Blocks.STONE.getBlockData().set(BlockStone.VARIANT, BlockStone.EnumStoneVariant.ANDESITE), this.d.Y);
      this.m = new WorldGenMinable(Blocks.COAL_ORE.getBlockData(), (Integer)generator.coalSize.getValue(), (Boolean)generator.coalMustTouchAir.getValue());
      this.n = new WorldGenMinable(Blocks.IRON_ORE.getBlockData(), (Integer)generator.ironSize.getValue(), (Boolean)generator.ironMustTouchAir.getValue());
      this.o = new WorldGenMinable(Blocks.GOLD_ORE.getBlockData(), (Integer)generator.goldSize.getValue(), (Boolean)generator.goldMustTouchAir.getValue());
      this.p = new WorldGenMinable(
         Blocks.REDSTONE_ORE.getBlockData(), (Integer)generator.redstoneSize.getValue(), (Boolean)generator.redstoneMustTouchAir.getValue()
      );
      this.q = new WorldGenMinable(
         Blocks.DIAMOND_ORE.getBlockData(), (Integer)generator.diamondSize.getValue(), (Boolean)generator.diamondMustTouchAir.getValue()
      );
      this.r = new WorldGenMinable(Blocks.LAPIS_ORE.getBlockData(), (Integer)generator.lapisSize.getValue(), (Boolean)generator.lapisMustTouchAir.getValue());
      this.a(biomebase);
      this.a = null;
      this.b = null;
   }

   protected void a(BiomeBase var1) {
      this.a();

      for (int var2 = 0; var2 < this.I; var2++) {
         int var3 = this.b.nextInt(16) + 8;
         int var4 = this.b.nextInt(16) + 8;
         this.f.generate(this.a, this.b, this.a.r(this.c.a(var3, 0, var4)));
      }

      for (int var121 = 0; var121 < this.J; var121++) {
         int var3 = this.b.nextInt(16) + 8;
         int var4 = this.b.nextInt(16) + 8;
         this.e.generate(this.a, this.b, this.a.r(this.c.a(var3, 0, var4)));
      }

      for (int var131 = 0; var131 < this.H; var131++) {
         int var3 = this.b.nextInt(16) + 8;
         int var4 = this.b.nextInt(16) + 8;
         this.g.generate(this.a, this.b, this.a.r(this.c.a(var3, 0, var4)));
      }

      int var14 = this.A;
      if (this.b.nextInt(10) == 0) {
         var14++;
      }

      for (int var3 = 0; var3 < var14; var3++) {
         int var4 = this.b.nextInt(16) + 8;
         int var5 = this.b.nextInt(16) + 8;
         WorldGenTreeAbstract var6 = var1.a(this.b);
         var6.e();
         BlockPosition var7 = this.a.getHighestBlockYAt(this.c.a(var4, 0, var5));
         if (var6.generate(this.a, this.b, var7)) {
            var6.a(this.a, this.b, var7);
         }
      }

      for (int var18 = 0; var18 < this.K; var18++) {
         int var4 = this.b.nextInt(16) + 8;
         int var5 = this.b.nextInt(16) + 8;
         this.v.generate(this.a, this.b, this.a.getHighestBlockYAt(this.c.a(var4, 0, var5)));
      }

      for (int var19 = 0; var19 < this.B; var19++) {
         int var4 = this.b.nextInt(16) + 8;
         int var5 = this.b.nextInt(16) + 8;
         int var11 = this.a.getHighestBlockYAt(this.c.a(var4, 0, var5)).getY() + 32;
         if (var11 > 0) {
            int var12 = this.b.nextInt(var11);
            BlockPosition var8 = this.c.a(var4, var12, var5);
            BlockFlowers.EnumFlowerVarient var9 = var1.a(this.b, var8);
            BlockFlowers var10 = var9.a().a();
            if (var10.getMaterial() != Material.AIR) {
               this.s.a(var10, var9);
               this.s.generate(this.a, this.b, var8);
            }
         }
      }

      for (int var20 = 0; var20 < this.C; var20++) {
         int var4 = this.b.nextInt(16) + 8;
         int var5 = this.b.nextInt(16) + 8;
         int var11 = this.a.getHighestBlockYAt(this.c.a(var4, 0, var5)).getY() * 2;
         if (var11 > 0) {
            int var12 = this.b.nextInt(var11);
            var1.b(this.b).generate(this.a, this.b, this.c.a(var4, var12, var5));
         }
      }

      for (int var21 = 0; var21 < this.D; var21++) {
         int var4 = this.b.nextInt(16) + 8;
         int var5 = this.b.nextInt(16) + 8;
         int var11 = this.a.getHighestBlockYAt(this.c.a(var4, 0, var5)).getY() * 2;
         if (var11 > 0) {
            int var12 = this.b.nextInt(var11);
            new WorldGenDeadBush().generate(this.a, this.b, this.c.a(var4, var12, var5));
         }
      }

      for (int var22 = 0; var22 < this.z; var22++) {
         int var4 = this.b.nextInt(16) + 8;
         int var5 = this.b.nextInt(16) + 8;
         int var11 = this.a.getHighestBlockYAt(this.c.a(var4, 0, var5)).getY() * 2;
         if (var11 > 0) {
            int var12 = this.b.nextInt(var11);
            BlockPosition var8 = this.c.a(var4, var12, var5);

            while (var8.getY() > 0) {
               BlockPosition var14x = var8.down();
               if (!this.a.isEmpty(var14x)) {
                  break;
               }

               var8 = var14x;
            }

            this.y.generate(this.a, this.b, var8);
         }
      }

      for (int var23 = 0; var23 < this.E; var23++) {
         if (this.b.nextInt(4) == 0) {
            int var4 = this.b.nextInt(16) + 8;
            int var5 = this.b.nextInt(16) + 8;
            BlockPosition var13 = this.a.getHighestBlockYAt(this.c.a(var4, 0, var5));
            this.t.generate(this.a, this.b, var13);
         }

         if (this.b.nextInt(8) == 0) {
            int var4 = this.b.nextInt(16) + 8;
            int var5 = this.b.nextInt(16) + 8;
            int var11 = this.a.getHighestBlockYAt(this.c.a(var4, 0, var5)).getY() * 2;
            if (var11 > 0) {
               int var12 = this.b.nextInt(var11);
               BlockPosition var8 = this.c.a(var4, var12, var5);
               this.u.generate(this.a, this.b, var8);
            }
         }
      }

      if (this.b.nextInt(4) == 0) {
         int var24 = this.b.nextInt(16) + 8;
         int var4 = this.b.nextInt(16) + 8;
         int var5 = this.a.getHighestBlockYAt(this.c.a(var24, 0, var4)).getY() * 2;
         if (var5 > 0) {
            int var11 = this.b.nextInt(var5);
            this.t.generate(this.a, this.b, this.c.a(var24, var11, var4));
         }
      }

      if (this.b.nextInt(8) == 0) {
         int var25 = this.b.nextInt(16) + 8;
         int var4 = this.b.nextInt(16) + 8;
         int var5 = this.a.getHighestBlockYAt(this.c.a(var25, 0, var4)).getY() * 2;
         if (var5 > 0) {
            int var11 = this.b.nextInt(var5);
            this.u.generate(this.a, this.b, this.c.a(var25, var11, var4));
         }
      }

      for (int var26 = 0; var26 < this.F * this.a.generatorConfig.sugarCaneMultiplier.getValue(); var26++) {
         int var4 = this.b.nextInt(16) + 8;
         int var5 = this.b.nextInt(16) + 8;
         int var11 = this.a.getHighestBlockYAt(this.c.a(var4, 0, var5)).getY() * 2;
         if (var11 > 0) {
            int var12 = this.b.nextInt(var11);
            this.w.generate(this.a, this.b, this.c.a(var4, var12, var5));
         }
      }

      for (int var27 = 0; var27 < 10.0 * this.a.generatorConfig.sugarCaneMultiplier.getValue(); var27++) {
         int var4 = this.b.nextInt(16) + 8;
         int var5 = this.b.nextInt(16) + 8;
         int var11 = this.a.getHighestBlockYAt(this.c.a(var4, 0, var5)).getY() * 2;
         if (var11 > 0) {
            int var12 = this.b.nextInt(var11);
            this.w.generate(this.a, this.b, this.c.a(var4, var12, var5));
         }
      }

      if (this.b.nextInt(32) == 0) {
         int var28 = this.b.nextInt(16) + 8;
         int var4 = this.b.nextInt(16) + 8;
         int var5 = this.a.getHighestBlockYAt(this.c.a(var28, 0, var4)).getY() * 2;
         if (var5 > 0) {
            int var11 = this.b.nextInt(var5);
            new WorldGenPumpkin().generate(this.a, this.b, this.c.a(var28, var11, var4));
         }
      }

      for (int var29 = 0; var29 < this.G; var29++) {
         int var4 = this.b.nextInt(16) + 8;
         int var5 = this.b.nextInt(16) + 8;
         int var11 = this.a.getHighestBlockYAt(this.c.a(var4, 0, var5)).getY() * 2;
         if (var11 > 0) {
            int var12 = this.b.nextInt(var11);
            this.x.generate(this.a, this.b, this.c.a(var4, var12, var5));
         }
      }

      if (this.L) {
         for (int var30 = 0; var30 < 50; var30++) {
            int var4 = this.b.nextInt(16) + 8;
            int var5 = this.b.nextInt(16) + 8;
            int var11 = this.b.nextInt(248) + 8;
            int var12 = this.b.nextInt(var11);
            BlockPosition var8 = this.c.a(var4, var12, var5);
            new WorldGenLiquids(Blocks.FLOWING_WATER).generate(this.a, this.b, var8);
         }

         for (int var31 = 0; var31 < 20; var31++) {
            int var4 = this.b.nextInt(16) + 8;
            int var5 = this.b.nextInt(16) + 8;
            int var11 = this.b.nextInt(this.b.nextInt(this.b.nextInt(240) + 8) + 8);
            BlockPosition var7 = this.c.a(var4, var11, var5);
            new WorldGenLiquids(Blocks.FLOWING_LAVA).generate(this.a, this.b, var7);
         }
      }
   }

   protected void a(int var1, WorldGenerator var2, int var3, int var4) {
      if (var4 < var3) {
         int var5 = var3;
         var3 = var4;
         var4 = var5;
      } else if (var4 == var3) {
         if (var3 < 255) {
            var4++;
         } else {
            var3--;
         }
      }

      for (int var5 = 0; var5 < var1; var5++) {
         BlockPosition var6 = this.c.a(this.b.nextInt(16), this.b.nextInt(var4 - var3) + var3, this.b.nextInt(16));
         var2.generate(this.a, this.b, var6);
      }
   }

   protected void b(int var1, WorldGenerator var2, int var3, int var4) {
      for (int var5 = 0; var5 < var1; var5++) {
         BlockPosition var6 = this.c.a(this.b.nextInt(16), this.b.nextInt(var4) + this.b.nextInt(var4) + var3 - var4, this.b.nextInt(16));
         var2.generate(this.a, this.b, var6);
      }
   }

   protected void a() {
      this.a(this.d.J, this.h, this.d.K, this.d.L);
      this.a(this.d.N, this.i, this.d.O, this.d.P);
      Generator generator = this.a.generatorConfig;
      double coal = (Double)generator.coalMultiplier.getValue();
      double iron = (Double)generator.ironMultiplier.getValue();
      double gold = (Double)generator.goldMultiplier.getValue();
      double redstone = (Double)generator.redstoneMultiplier.getValue();
      double diamond = (Double)generator.diamondMultiplier.getValue();
      double lapis = (Double)generator.lapisMultiplier.getValue();
      if (coal > 0.0) {
         this.a((int)(this.d.ad * coal + this.b.nextFloat()), this.m, this.d.ae, this.d.af);
      }

      if (iron > 0.0) {
         this.a((int)(this.d.ah * iron + this.b.nextFloat()), this.n, this.d.ai, this.d.aj);
      }

      if (gold > 0.0) {
         this.a((int)(this.d.al * gold + this.b.nextFloat()), this.o, this.d.am, this.d.an);
      }

      if (redstone > 0.0) {
         this.a((int)(this.d.ap * redstone + this.b.nextFloat()), this.p, this.d.aq, this.d.ar);
      }

      if (diamond > 0.0) {
         this.a((int)(this.d.at * diamond + this.b.nextFloat()), this.q, this.d.au, this.d.av);
      }

      if (lapis > 0.0) {
         this.b((int)(this.d.ax * lapis + this.b.nextFloat()), this.r, this.d.ay, this.d.az);
      }
   }
}
