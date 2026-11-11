package org.bukkit.entity;

public interface Skeleton extends Monster {
   Skeleton.SkeletonType getSkeletonType();

   void setSkeletonType(Skeleton.SkeletonType var1);

   public static enum SkeletonType {
      NORMAL(0),
      WITHER(1);

      private static final Skeleton.SkeletonType[] types = new Skeleton.SkeletonType[values().length];
      private final int id;

      private SkeletonType(int id) {
         this.id = id;
      }

      @Deprecated
      public int getId() {
         return this.id;
      }

      @Deprecated
      public static Skeleton.SkeletonType getType(int id) {
         return id >= types.length ? null : types[id];
      }

      static {
         for (Skeleton.SkeletonType type : values()) {
            types[type.getId()] = type;
         }
      }
   }
}
