package net.minecraft.server.v1_8_R3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import lol.deyo.blaze.Blaze;
import org.junit.BeforeClass;
import org.junit.Test;

public class ChunkRegionLoaderTest {
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
   public void countsLoadedBlocksWithoutASecondPass() {
      WorldServer world = mock(WorldServer.class);
      when(world.getSeed()).thenReturn(0L);
      WorldProvider provider = mock(WorldProvider.class);
      when(provider.o()).thenReturn(false);
      world.worldProvider = provider;
      ChunkRegionLoader loader = new ChunkRegionLoader(new java.io.File("."));
      NBTTagCompound level = new NBTTagCompound();
      level.setInt("xPos", 0);
      level.setInt("zPos", 0);
      level.setIntArray("HeightMap", new int[256]);
      level.setBoolean("TerrainPopulated", true);
      level.setBoolean("LightPopulated", true);
      level.setLong("InhabitedTime", 0L);

      NBTTagCompound section = new NBTTagCompound();
      section.setByte("Y", (byte)0);
      byte[] blocks = new byte[4096];
      blocks[0] = (byte)Block.getId(Blocks.STONE);
      section.setByteArray("Blocks", blocks);
      section.setByteArray("Data", new byte[2048]);
      section.setByteArray("BlockLight", new byte[2048]);
      section.setByteArray("SkyLight", new byte[2048]);
      NBTTagList sections = new NBTTagList();
      sections.add(section);
      level.set("Sections", sections);
      level.setByteArray("Biomes", new byte[256]);
      level.set("Entities", new NBTTagList());
      level.set("TileEntities", new NBTTagList());

      NBTTagCompound root = new NBTTagCompound();
      root.set("Level", level);
      Object[] loaded = loader.a(world, 0, 0, root);
      assertNotNull(loaded);
      Chunk chunk = (Chunk)loaded[0];
      ChunkSection loadedSection = chunk.getSections()[0];
      assertNotNull(loadedSection);
      assertFalse(loadedSection.a());
      assertEquals(Block.getId(Blocks.STONE) << 4, loadedSection.getBlockId(0));
      assertEquals(0, loadedSection.getTickingBlockCount());
   }
}
