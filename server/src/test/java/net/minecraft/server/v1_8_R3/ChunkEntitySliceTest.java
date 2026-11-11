package net.minecraft.server.v1_8_R3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import lol.deyo.blaze.Blaze;
import org.junit.BeforeClass;
import org.junit.Test;

public class ChunkEntitySliceTest {
   @BeforeClass
   public static void bootstrap() throws Exception {
      Blaze blaze = mock(Blaze.class);
      when(blaze.getRedstoneOptimization()).thenReturn(new BlockRedstoneWire());
      Field instance = Blaze.class.getDeclaredField("instance");
      instance.setAccessible(true);
      instance.set(null, blaze);
      DispenserRegistry.c();
   }

   @Test
   public void entitySlicesAreAllocatedOnDemandAndReleased() {
      WorldServer world = mock(WorldServer.class);
      when(world.getSeed()).thenReturn(0L);
      Chunk chunk = new Chunk(world, 0, 0);

      for (int index = 0; index < chunk.entitySlices.length; index++) {
         assertSame(Collections.emptyList(), chunk.entitySlices[index]);
      }

      TestEntity entity = new TestEntity();
      chunk.a(entity);
      List<Entity> slice = chunk.entitySlices[0];
      assertNotSame(Collections.emptyList(), slice);
      assertTrue(slice.contains(entity));
      assertEquals(1, slice.size());

      chunk.a(entity, 0);
      assertSame(Collections.emptyList(), chunk.entitySlices[0]);

      chunk.addCannonEntity(entity);
      assertTrue(chunk.entitySlices[0].contains(entity));
      chunk.removeCannonEntity(entity, 0);
      assertSame(Collections.emptyList(), chunk.entitySlices[0]);
   }

   private static final class TestEntity extends Entity {
      private TestEntity() {
         super(null);
      }

      @Override
      protected void h() {
      }

      @Override
      protected void a(NBTTagCompound nbtTagCompound) {
      }

      @Override
      protected void b(NBTTagCompound nbtTagCompound) {
      }
   }
}
