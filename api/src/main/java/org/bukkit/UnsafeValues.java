package org.bukkit;

import java.util.List;
import org.bukkit.inventory.ItemStack;

@Deprecated
public interface UnsafeValues {
   org.bukkit.Material getMaterialFromInternalName(String var1);

   List<String> tabCompleteInternalMaterialName(String var1, List<String> var2);

   ItemStack modifyItemStack(ItemStack var1, String var2);

   org.bukkit.Statistic getStatisticFromInternalName(String var1);

   org.bukkit.Achievement getAchievementFromInternalName(String var1);

   List<String> tabCompleteInternalStatisticOrAchievementName(String var1, List<String> var2);
}
