package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import lol.deyo.blaze.configs.BlazeConfig;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public abstract class MobSpawnerAbstract {
   public int spawnDelay = 20;
   private String mobName = "Pig";
   private final List<MobSpawnerAbstract.a> mobs = Lists.newArrayList();
   private MobSpawnerAbstract.a spawnData;
   private int tickDelay = 0;

   public String getMobName() {
      if (this.i() == null) {
         if (this.mobName == null) {
            this.mobName = "Pig";
         } else if (this.mobName.equals("Minecart")) {
            this.mobName = "MinecartRideable";
         }

         return this.mobName;
      } else {
         return this.i().d;
      }
   }

   public void setMobName(String s) {
      this.mobName = s;
   }

   private boolean g() {
      BlockPosition blockposition = this.b();
      return this.a()
         .isPlayerNearbyWhoAffectsSpawning(
            blockposition.getX() + 0.5, blockposition.getY() + 0.5, blockposition.getZ() + 0.5, (Double)BlazeConfig.playerSpawnRange.getValue()
         );
   }

   public void c() {
      if (this.spawnDelay <= 0 || --this.tickDelay <= 0) {
         World world = this.a();
         this.tickDelay = world.paperSpigotConfig.mobSpawnerTickRate;
         BlockPosition blockposition = this.b();
         if ((Double)BlazeConfig.playerSpawnRange.getValue() <= 0.0 || this.g()) {
            if (this.spawnDelay < -this.tickDelay) {
               this.h();
            }

            if (this.spawnDelay > 0) {
               this.spawnDelay = this.spawnDelay - this.tickDelay;
               return;
            }

            boolean spawned = false;

            for (int i = 0; i < BlazeConfig.spawnCount.getValue(); i++) {
               int chunkMobLimit = (Integer)BlazeConfig.chunkMobLimit.getValue();
               if (chunkMobLimit > 0) {
                  org.bukkit.Chunk chunk = new Location(world.getWorld(), blockposition.getX(), blockposition.getY(), blockposition.getZ()).getChunk();
                  int amount = 0;

                  org.bukkit.entity.Entity[] var11;
                  for (org.bukkit.entity.Entity e : var11 = chunk.getEntities()) {
                     if (e instanceof LivingEntity && !e.isDead() && amount++ > chunkMobLimit) {
                        return;
                     }
                  }
               }

               Entity entity = EntityTypes.createEntityByName(this.getMobName(), world);
               if (entity == null) {
                  return;
               }

               double range = (Double)BlazeConfig.spawnRange.getValue();
               int nearbyEntitiesLimit = (Integer)BlazeConfig.maximumNearbyEntities.getValue();
               if (nearbyEntitiesLimit > 0) {
                  int entityCount = world.a(
                        entity.getClass(),
                        new AxisAlignedBB(
                              blockposition.getX(),
                              blockposition.getY(),
                              blockposition.getZ(),
                              blockposition.getX() + 1,
                              blockposition.getY() + 1,
                              blockposition.getZ() + 1
                           )
                           .grow(range, range, range)
                     )
                     .size();
                  if (entityCount >= nearbyEntitiesLimit) {
                     this.h();
                     return;
                  }
               }

               double locX = blockposition.getX() + (world.random.nextDouble() - world.random.nextDouble()) * range + 0.5;
               double locY = blockposition.getY() + world.random.nextInt(3) - 1;
               double locZ = blockposition.getZ() + (world.random.nextDouble() - world.random.nextDouble()) * range + 0.5;
               EntityInsentient entityinsentient = entity instanceof EntityInsentient ? (EntityInsentient)entity : null;
               entity.fromMobSpawner = true;
               entity.setPosition(locX, locY, locZ);
               if (entityinsentient == null || entityinsentient.canSpawn() && (!(Boolean)BlazeConfig.lightLevelCheck.getValue() || entityinsentient.bR())) {
                  this.a(entity, false);
                  if ((Boolean)BlazeConfig.spawnerParticles.getValue()) {
                     world.triggerEffect(2004, blockposition, 0);
                     if (entityinsentient != null) {
                        entityinsentient.y();
                     }
                  }

                  spawned = true;
               }
            }

            if (spawned) {
               this.h();
            }
         }
      }
   }

   private Entity a(Entity entity, boolean flag) {
      MobSpawnerAbstract.a spawnData = this.i();
      if (spawnData != null) {
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         entity.d(nbttagcompound);

         for (String s : spawnData.c.c()) {
            nbttagcompound.set(s, spawnData.c.get(s).clone());
         }

         entity.f(nbttagcompound);
         if (entity.world != null) {
            SpawnerSpawnEvent event = CraftEventFactory.callSpawnerSpawnEvent(entity, this.b().getX(), this.b().getY(), this.b().getZ());
            if (!event.isCancelled()) {
               entity.fromMobSpawner = true;
               entity.world.addEntity(entity, SpawnReason.SPAWNER);
            }
         }

         Entity entity1 = entity;

         while (nbttagcompound.hasKeyOfType("Riding", 10)) {
            NBTTagCompound nbttagcompound1;
            label54: {
               nbttagcompound1 = nbttagcompound.getCompound("Riding");
               Entity entity2 = EntityTypes.createEntityByName(nbttagcompound1.getString("id"), entity.world);
               if (entity2 != null) {
                  NBTTagCompound nbttagcompound2 = new NBTTagCompound();
                  entity2.d(nbttagcompound2);

                  for (String s1 : nbttagcompound1.c()) {
                     NBTBase nbtbase1 = nbttagcompound1.get(s1);
                     nbttagcompound2.set(s1, nbtbase1.clone());
                  }

                  entity2.f(nbttagcompound2);
                  entity2.setPositionRotation(entity1.locX, entity1.locY, entity1.locZ, entity1.yaw, entity1.pitch);
                  SpawnerSpawnEvent event = CraftEventFactory.callSpawnerSpawnEvent(entity2, this.b().getX(), this.b().getY(), this.b().getZ());
                  if (event.isCancelled()) {
                     break label54;
                  }

                  if (entity.world != null) {
                     entity.world.addEntity(entity2, SpawnReason.SPAWNER);
                  }

                  entity1.mount(entity2);
               }

               entity1 = entity2;
            }

            nbttagcompound = nbttagcompound1;
         }
      } else if (entity instanceof EntityLiving && entity.world != null) {
         if (entity instanceof EntityInsentient) {
            ((EntityInsentient)entity)
               .prepare(entity.world.E(MathHelper.floor(entity.locX), MathHelper.floor(entity.locY), MathHelper.floor(entity.locZ)), null);
         }

         SpawnerSpawnEvent eventx = CraftEventFactory.callSpawnerSpawnEvent(entity, this.b().getX(), this.b().getY(), this.b().getZ());
         if (!eventx.isCancelled()) {
            entity.fromMobSpawner = true;
            entity.world.addEntity(entity, SpawnReason.SPAWNER);
         }
      }

      return entity;
   }

   private void h() {
      int maxSpawnDelay = (Integer)BlazeConfig.maximumSpawnDelay.getValue();
      int minSpawnDelay = (Integer)BlazeConfig.minimumSpawnDelay.getValue();
      if (maxSpawnDelay <= minSpawnDelay) {
         this.spawnDelay = minSpawnDelay;
      } else {
         this.spawnDelay = minSpawnDelay + this.a().random.nextInt(maxSpawnDelay - minSpawnDelay);
      }

      if (!this.mobs.isEmpty()) {
         this.a(WeightedRandom.a(this.a().random, this.mobs));
      }

      this.a(1);
   }

   public void a(NBTTagCompound nbttagcompound) {
      this.mobName = nbttagcompound.getString("EntityId");
      this.spawnDelay = nbttagcompound.getShort("Delay");
      this.mobs.clear();
      if (nbttagcompound.hasKeyOfType("SpawnPotentials", 9)) {
         NBTTagList nbttaglist = nbttagcompound.getList("SpawnPotentials", 10);

         for (int i = 0; i < nbttaglist.size(); i++) {
            this.mobs.add(new MobSpawnerAbstract.a(nbttaglist.get(i)));
         }
      }

      if (nbttagcompound.hasKeyOfType("SpawnData", 10)) {
         this.a(new MobSpawnerAbstract.a(nbttagcompound.getCompound("SpawnData"), this.mobName));
      } else {
         this.a((MobSpawnerAbstract.a)null);
      }
   }

   public void b(NBTTagCompound nbttagcompound) {
      String s = this.getMobName();
      if (!StringUtils.isEmpty(s)) {
         nbttagcompound.setString("EntityId", s);
         nbttagcompound.setShort("Delay", (short)this.spawnDelay);
         nbttagcompound.setShort("MinSpawnDelay", ((Integer)BlazeConfig.minimumSpawnDelay.getValue()).shortValue());
         nbttagcompound.setShort("MaxSpawnDelay", ((Integer)BlazeConfig.maximumSpawnDelay.getValue()).shortValue());
         nbttagcompound.setShort("SpawnCount", ((Integer)BlazeConfig.spawnCount.getValue()).shortValue());
         nbttagcompound.setShort("MaxNearbyEntities", ((Integer)BlazeConfig.maximumNearbyEntities.getValue()).shortValue());
         nbttagcompound.setShort("RequiredPlayerRange", ((Double)BlazeConfig.playerSpawnRange.getValue()).shortValue());
         nbttagcompound.setShort("SpawnRange", ((Double)BlazeConfig.spawnRange.getValue()).shortValue());
         if (this.i() != null) {
            nbttagcompound.set("SpawnData", this.i().c.clone());
         }

         if (this.i() != null || !this.mobs.isEmpty()) {
            NBTTagList nbttaglist = new NBTTagList();
            if (!this.mobs.isEmpty()) {
               for (MobSpawnerAbstract.a mob : this.mobs) {
                  nbttaglist.add(mob.a());
               }
            } else {
               nbttaglist.add(this.i().a());
            }

            nbttagcompound.set("SpawnPotentials", nbttaglist);
         }
      }
   }

   public boolean b(int i) {
      return false;
   }

   private MobSpawnerAbstract.a i() {
      return this.spawnData;
   }

   public void a(MobSpawnerAbstract.a spawnData) {
      this.spawnData = spawnData;
   }

   public abstract void a(int var1);

   public abstract World a();

   public abstract BlockPosition b();

   public class a extends WeightedRandom.WeightedRandomChoice {
      private final NBTTagCompound c;
      private final String d;

      public a(NBTTagCompound nbttagcompound) {
         this(nbttagcompound.getCompound("Properties"), nbttagcompound.getString("Type"), nbttagcompound.getInt("Weight"));
      }

      public a(NBTTagCompound nbttagcompound, String s) {
         this(nbttagcompound, s, 1);
      }

      private a(NBTTagCompound nbttagcompound, String s, int i) {
         super(i);
         if (s.equals("Minecart")) {
            if (nbttagcompound != null) {
               s = EntityMinecartAbstract.EnumMinecartType.a(nbttagcompound.getInt("Type")).b();
            } else {
               s = "MinecartRideable";
            }
         }

         this.c = nbttagcompound;
         this.d = s;
      }

      public NBTTagCompound a() {
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         nbttagcompound.set("Properties", this.c);
         nbttagcompound.setString("Type", this.d);
         nbttagcompound.setInt("Weight", this.a);
         return nbttagcompound;
      }
   }
}
