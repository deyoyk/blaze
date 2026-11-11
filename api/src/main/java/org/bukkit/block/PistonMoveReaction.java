package org.bukkit.block;

import java.util.HashMap;
import java.util.Map;

public enum PistonMoveReaction {
   MOVE(0),
   BREAK(1),
   BLOCK(2);

   private final int id;
   private static final Map<Integer, PistonMoveReaction> byId = new HashMap<>();

   private PistonMoveReaction(int id) {
      this.id = id;
   }

   @Deprecated
   public int getId() {
      return this.id;
   }

   @Deprecated
   public static PistonMoveReaction getById(int id) {
      return byId.get(id);
   }

   static {
      for (PistonMoveReaction reaction : values()) {
         byId.put(reaction.id, reaction);
      }
   }
}
