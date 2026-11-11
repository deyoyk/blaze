package net.minecraft.server.v1_8_R3;

import java.io.IOException;

public interface IChunkLoader {
   Chunk a(World var1, int var2, int var3) throws IOException;

   void a(World var1, Chunk var2) throws IOException, ExceptionWorldConflict;

   void b(World var1, Chunk var2) throws IOException;

   void a();

   void b();
}
