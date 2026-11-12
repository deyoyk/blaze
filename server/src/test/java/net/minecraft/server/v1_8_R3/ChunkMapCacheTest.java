package net.minecraft.server.v1_8_R3;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import lol.deyo.blaze.Blaze;
import org.spigotmc.SpigotWorldConfig;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ChunkMapCacheTest {
   private WorldServer world;

   @BeforeClass
   public static void bootstrap() throws Exception {
      Blaze blaze = mock(Blaze.class);
      when(blaze.getRedstoneOptimization()).thenReturn(new BlockRedstoneWire());
      Field instance = Blaze.class.getDeclaredField("instance");
      instance.setAccessible(true);
      instance.set(null, blaze);
      DispenserRegistry.c();
   }

   @Before
   public void setUp() {
      this.world = mock(WorldServer.class);
      WorldProvider provider = mock(WorldProvider.class);
      when(provider.o()).thenReturn(true);
      this.world.worldProvider = provider;
      this.world.spigotConfig = mock(SpigotWorldConfig.class);
      this.world.spigotConfig.antiXray = false;
   }

   @Test
   public void rebuildsCachedMapWhenAnEmptySectionBecomesPopulated() {
      Chunk chunk = new Chunk(this.world, 0, 0);
      chunk.getChunkMap(true, 65535);

      chunk.getSections()[5] = nonEmptySection(5);

      PacketPlayOutMapChunk.ChunkMap map = chunk.getChunkMap(true, 65535);
      assertTrue("section 5 should be serialized", (map.b & 1 << 5) != 0);
   }

   @Test
   public void rebuildsCachedMapWhenSectionBlocksChange() {
      Chunk chunk = new Chunk(this.world, 0, 0);
      ChunkSection section = new ChunkSection(5 << 4, false);
      chunk.getSections()[5] = section;
      chunk.getChunkMap(true, 65535);

      section.setBlockCounts(1, 0);
      section.setBlockId(0, (char)1);

      PacketPlayOutMapChunk.ChunkMap map = chunk.getChunkMap(true, 65535);
      assertTrue("dirty section 5 should be serialized", (map.b & 1 << 5) != 0);
   }

   @Test
   public void rebuildsCachedMapWhenSectionsAreReplaced() {
      Chunk chunk = new Chunk(this.world, 0, 0);
      chunk.getChunkMap(true, 65535);

      ChunkSection[] sections = new ChunkSection[16];
      sections[7] = nonEmptySection(7);
      chunk.a(sections);

      PacketPlayOutMapChunk.ChunkMap map = chunk.getChunkMap(true, 65535);
      assertTrue("section 7 should be serialized after replacement", (map.b & 1 << 7) != 0);
   }

   private static ChunkSection nonEmptySection(int sectionIndex) {
      ChunkSection section = new ChunkSection(sectionIndex << 4, false);
      char[] blocks = new char[ChunkSectionStorage.SIZE];
      blocks[0] = 1;
      section.a(blocks);
      section.setBlockCounts(1, 0);
      section.isDirty = false;
      return section;
   }
}
