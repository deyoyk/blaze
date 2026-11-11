package net.minecraft.server.v1_8_R3;

import java.util.Collection;
import net.techcable.tacospigot.Indexer;

public interface IBlockState<T extends Comparable<T>> {
   Indexer<IBlockState> INDEXER = IBlockState::getId;

   String a();

   Collection<T> c();

   Class<T> b();

   String a(T var1);

   default void tryInitId() {
   }

   int getId();

   int getValueId(T var1);

   T getByValueId(int var1);
}
