package org.bukkit.entity;

public interface Ocelot extends Animals, Tameable {
   Ocelot.Type getCatType();

   void setCatType(Ocelot.Type var1);

   boolean isSitting();

   void setSitting(boolean var1);

   public static enum Type {
      WILD_OCELOT(0),
      BLACK_CAT(1),
      RED_CAT(2),
      SIAMESE_CAT(3);

      private static final Ocelot.Type[] types = new Ocelot.Type[values().length];
      private final int id;

      private Type(int id) {
         this.id = id;
      }

      @Deprecated
      public int getId() {
         return this.id;
      }

      @Deprecated
      public static Ocelot.Type getType(int id) {
         return id >= types.length ? null : types[id];
      }

      static {
         for (Ocelot.Type type : values()) {
            types[type.getId()] = type;
         }
      }
   }
}
