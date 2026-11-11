package net.minecraft.server.v1_8_R3;

import lol.deyo.blaze.configs.BlazeConfig;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList.Itr;

public class PathfinderGoalSelector {
   private static final Logger a = LogManager.getLogger();
   private final List<PathfinderGoalSelector.PathfinderGoalSelectorItem> b = new UnsafeList();
   private final List<PathfinderGoalSelector.PathfinderGoalSelectorItem> c = new UnsafeList();
   private int e;

   public PathfinderGoalSelector(MethodProfiler methodprofiler) {
   }

   public PathfinderGoalSelector() {
   }

   public void a(int i, PathfinderGoal pathfindergoal) {
      if ((Boolean)BlazeConfig.mobAI.getValue() || pathfindergoal instanceof PathfinderGoalBreed) {
         this.b.add(new PathfinderGoalSelector.PathfinderGoalSelectorItem(i, pathfindergoal));
      }
   }

   public void a(PathfinderGoal pathfindergoal) {
      Iterator<PathfinderGoalSelector.PathfinderGoalSelectorItem> iterator = this.b.iterator();

      while (iterator.hasNext()) {
         PathfinderGoalSelector.PathfinderGoalSelectorItem pathfindergoalselector_pathfindergoalselectoritem = iterator.next();
         PathfinderGoal pathfindergoal1 = pathfindergoalselector_pathfindergoalselectoritem.a;
         if (pathfindergoal1 == pathfindergoal) {
            if (this.c.contains(pathfindergoalselector_pathfindergoalselectoritem)) {
               pathfindergoal1.d();
               this.c.remove(pathfindergoalselector_pathfindergoalselectoritem);
            }

            iterator.remove();
         }
      }
   }

   public void a() {
      if (this.e++ % 3 == 0) {
         for (PathfinderGoalSelector.PathfinderGoalSelectorItem pathfindergoalselector_pathfindergoalselectoritem : this.b) {
            boolean flag = this.c.contains(pathfindergoalselector_pathfindergoalselectoritem);
            if (flag) {
               if (this.b(pathfindergoalselector_pathfindergoalselectoritem) && this.a(pathfindergoalselector_pathfindergoalselectoritem)) {
                  continue;
               }

               pathfindergoalselector_pathfindergoalselectoritem.a.d();
               this.c.remove(pathfindergoalselector_pathfindergoalselectoritem);
            }

            if (this.b(pathfindergoalselector_pathfindergoalselectoritem) && pathfindergoalselector_pathfindergoalselectoritem.a.a()) {
               pathfindergoalselector_pathfindergoalselectoritem.a.c();
               this.c.add(pathfindergoalselector_pathfindergoalselectoritem);
            }
         }
      } else {
         Iterator iterator = this.c.iterator();

         while (iterator.hasNext()) {
            PathfinderGoalSelector.PathfinderGoalSelectorItem pathfindergoalselector_pathfindergoalselectoritem = (PathfinderGoalSelector.PathfinderGoalSelectorItem)iterator.next();
            if (!this.a(pathfindergoalselector_pathfindergoalselectoritem)) {
               pathfindergoalselector_pathfindergoalselectoritem.a.d();
               iterator.remove();
            }
         }
      }

      for (PathfinderGoalSelector.PathfinderGoalSelectorItem pathfindergoalselector_pathfindergoalselectoritem : this.c) {
         pathfindergoalselector_pathfindergoalselectoritem.a.e();
      }
   }

   private boolean a(PathfinderGoalSelector.PathfinderGoalSelectorItem pathfindergoalselector_pathfindergoalselectoritem) {
      return pathfindergoalselector_pathfindergoalselectoritem.a.b();
   }

   private boolean b(PathfinderGoalSelector.PathfinderGoalSelectorItem pathfindergoalselector_pathfindergoalselectoritem) {
      Iterator<PathfinderGoalSelector.PathfinderGoalSelectorItem> iterator = this.b.iterator();

      while (iterator.hasNext()) {
         PathfinderGoalSelector.PathfinderGoalSelectorItem pathfindergoalselector_pathfindergoalselectoritem1 = iterator.next();
         if (pathfindergoalselector_pathfindergoalselectoritem1 != pathfindergoalselector_pathfindergoalselectoritem) {
            if (pathfindergoalselector_pathfindergoalselectoritem.b >= pathfindergoalselector_pathfindergoalselectoritem1.b) {
               if (!this.a(pathfindergoalselector_pathfindergoalselectoritem, pathfindergoalselector_pathfindergoalselectoritem1)
                  && this.c.contains(pathfindergoalselector_pathfindergoalselectoritem1)) {
                  ((Itr)iterator).valid = false;
                  return false;
               }
            } else if (!pathfindergoalselector_pathfindergoalselectoritem1.a.i() && this.c.contains(pathfindergoalselector_pathfindergoalselectoritem1)) {
               ((Itr)iterator).valid = false;
               return false;
            }
         }
      }

      return true;
   }

   private boolean a(
      PathfinderGoalSelector.PathfinderGoalSelectorItem pathfindergoalselector_pathfindergoalselectoritem,
      PathfinderGoalSelector.PathfinderGoalSelectorItem pathfindergoalselector_pathfindergoalselectoritem1
   ) {
      return (pathfindergoalselector_pathfindergoalselectoritem.a.j() & pathfindergoalselector_pathfindergoalselectoritem1.a.j()) == 0;
   }

   static class PathfinderGoalSelectorItem {
      public PathfinderGoal a;
      public int b;

      public PathfinderGoalSelectorItem(int i, PathfinderGoal pathfindergoal) {
         this.b = i;
         this.a = pathfindergoal;
      }
   }
}
