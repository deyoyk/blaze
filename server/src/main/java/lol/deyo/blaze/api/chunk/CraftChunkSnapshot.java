package lol.deyo.blaze.api.chunk;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.v1_8_R3.NBTTagCompound;

public class CraftChunkSnapshot implements ChunkSnapshot {
   private final ChunkSectionSnapshot[] sections = new ChunkSectionSnapshot[16];
   private final List<NBTTagCompound> tileEntities = new ArrayList<>();

   public ChunkSectionSnapshot[] getSections() {
      return this.sections;
   }

   public List<NBTTagCompound> getTileEntities() {
      return this.tileEntities;
   }
}
