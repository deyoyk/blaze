package net.minecraft.server.v1_8_R3;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.techcable.tacospigot.ImmutableArrayMap;
import net.techcable.tacospigot.ImmutableArrayTable;

public class BlockStateList {
   private static final Function<IBlockState, String> b = iblockstate -> iblockstate == null ? "<NULL>" : iblockstate.a();
   private final Block c;
   private final ImmutableList<IBlockState<?>> d;
   private final ImmutableList<IBlockData> e;

   public BlockStateList(Block block, IBlockState<?>... aiblockstate) {
      this.c = block;
      Arrays.sort(aiblockstate, new Comparator() {
         public int a(IBlockState<?> iblockstate, IBlockState<?> iblockstate1) {
            return iblockstate.a().compareTo(iblockstate1.a());
         }

         @Override
         public int compare(Object object, Object object1) {
            return this.a((IBlockState<?>)object, (IBlockState<?>)object1);
         }
      });

      for (IBlockState<?> state : aiblockstate) {
         state.tryInitId();
      }

      this.d = ImmutableList.copyOf(aiblockstate);
      LinkedHashMap linkedhashmap = Maps.newLinkedHashMap();
      List<BlockStateList.BlockData> arraylist = Lists.newArrayList();

      for (List<Comparable> list : IteratorUtils.a(this.e())) {
         Map map = MapGeneratorUtils.b(this.d, list);
         BlockStateList.BlockData blockstatelist_blockdata = new BlockStateList.BlockData(block, ImmutableMap.copyOf(map), null);
         linkedhashmap.put(map, blockstatelist_blockdata);
         arraylist.add(blockstatelist_blockdata);
      }

      for (BlockStateList.BlockData iblockdata : arraylist) {
         iblockdata.a(linkedhashmap);
      }

      this.e = ImmutableList.copyOf(arraylist);
   }

   public ImmutableList<IBlockData> a() {
      return this.e;
   }

   private List<Iterable<Comparable>> e() {
      ArrayList<Iterable<Comparable>> arraylist = Lists.newArrayList();

      for (int i = 0; i < this.d.size(); i++) {
         arraylist.add(((IBlockState)this.d.get(i)).c());
      }

      return arraylist;
   }

   public IBlockData getBlockData() {
      return (IBlockData)this.e.get(0);
   }

   public Block getBlock() {
      return this.c;
   }

   public Collection<IBlockState<?>> d() {
      return this.d;
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("block", Block.REGISTRY.c(this.c)).add("properties", Iterables.transform(this.d, b)).toString();
   }

   static class BlockData extends BlockDataAbstract {
      private final Block a;
      private final ImmutableMap<IBlockState, Comparable> b;
      private final Map<IBlockState, Comparable> bAsArrayMap;
      private Table<IBlockState, Comparable, IBlockData> c;

      private BlockData(Block block, ImmutableMap<IBlockState, Comparable> immutablemap) {
         this.a = block;
         this.b = immutablemap;
         this.bAsArrayMap = new ImmutableArrayMap(IBlockState.INDEXER, immutablemap);
      }

      @Override
      public Collection<IBlockState> a() {
         return Collections.unmodifiableCollection(this.bAsArrayMap.keySet());
      }

      @Override
      public <T extends Comparable<T>> T get(IBlockState<T> iblockstate) {
         assert this.bAsArrayMap.containsKey(iblockstate) : "Cannot get property " + iblockstate + " as it does not exist in " + this.a.P();

         Object value = this.bAsArrayMap.get(iblockstate);

         assert value == this.b.get(iblockstate) : "Array map gave data " + value + " and regular map gave data " + this.b.get(iblockstate);

         assert value != null : "Null value for state " + iblockstate + " and data " + this;

         assert iblockstate.b().isInstance(value) : "Value "
            + value
            + " for state "
            + iblockstate
            + " and data "
            + this
            + " not instanceof "
            + iblockstate.b().getTypeName();

         return (T)value;
      }

      @Override
      public <T extends Comparable<T>, V extends T> IBlockData set(IBlockState<T> iblockstate, V v0) {
         assert iblockstate != null : "Null block state";

         assert v0 != null : "Null value for block state " + iblockstate;

         assert this.bAsArrayMap.containsKey(iblockstate) : "Cannot set property " + iblockstate + " as it does not exist in " + this.a.P();

         assert iblockstate.c().contains(v0) : "Cannot set property "
            + iblockstate
            + " to "
            + v0
            + " on block "
            + Block.REGISTRY.c(this.a)
            + ", it is not an allowed value";

         IBlockData data = (IBlockData)(this.bAsArrayMap.get(iblockstate) == v0 ? this : (IBlockData)this.c.get(iblockstate, v0));

         assert data != null : "No block data with property " + iblockstate + " and value " + v0 + " for block data " + this;

         return data;
      }

      @Override
      public ImmutableMap<IBlockState, Comparable> b() {
         return this.b;
      }

      public Map<IBlockState, Comparable> asArrayMap() {
         return this.bAsArrayMap;
      }

      @Override
      public Block getBlock() {
         return this.a;
      }

      @Override
      public boolean equals(Object object) {
         return this == object;
      }

      @Override
      public int hashCode() {
         return this.bAsArrayMap.hashCode();
      }

      public void a(Map<Map<IBlockState, Comparable>, BlockStateList.BlockData> map) {
         if (this.c != null) {
            throw new IllegalStateException();
         } else {
            HashBasedTable<IBlockState, Comparable, IBlockData> hashbasedtable = HashBasedTable.create();

            for (IBlockState iblockstate : this.bAsArrayMap.keySet()) {
               for (Object o : iblockstate.c()) {
                  Comparable comparable = (Comparable)o;
                  if (comparable != this.get(iblockstate)) {
                     assert map.get(this.b(iblockstate, comparable)) != null : "Map doesn't contain block data with state "
                        + iblockstate
                        + " and comparable "
                        + comparable
                        + this.b(iblockstate, comparable);

                     hashbasedtable.put(iblockstate, comparable, map.get(this.b(iblockstate, comparable)));
                  }
               }
            }

            this.c = new ImmutableArrayTable<IBlockState, Comparable, IBlockData>(IBlockState.INDEXER, (state, value) -> state.getValueId(value), hashbasedtable);
         }
      }

      private Map<IBlockState, Comparable> b(IBlockState iblockstate, Comparable comparable) {
         HashMap hashmap = Maps.newHashMap(this.bAsArrayMap);
         hashmap.put(iblockstate, comparable);
         return hashmap;
      }

      BlockData(Block block, ImmutableMap immutablemap, Object object) {
         this(block, immutablemap);
      }
   }
}
