package org.bukkit.plugin;

public abstract class PluginBase implements Plugin {
   @Override
   public final int hashCode() {
      return this.getName().hashCode();
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else {
         return !(obj instanceof Plugin) ? false : this.getName().equals(((Plugin)obj).getName());
      }
   }

   @Override
   public final String getName() {
      return this.getDescription().getName();
   }
}
