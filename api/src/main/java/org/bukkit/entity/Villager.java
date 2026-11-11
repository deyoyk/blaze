package org.bukkit.entity;

public interface Villager extends Ageable, NPC {
   Villager.Profession getProfession();

   void setProfession(Villager.Profession var1);

   public static enum Profession {
      FARMER(0),
      LIBRARIAN(1),
      PRIEST(2),
      BLACKSMITH(3),
      BUTCHER(4);

      private static final Villager.Profession[] professions = new Villager.Profession[values().length];
      private final int id;

      private Profession(int id) {
         this.id = id;
      }

      @Deprecated
      public int getId() {
         return this.id;
      }

      @Deprecated
      public static Villager.Profession getProfession(int id) {
         return id >= professions.length ? null : professions[id];
      }

      static {
         for (Villager.Profession type : values()) {
            professions[type.getId()] = type;
         }
      }
   }
}
