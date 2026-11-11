package org.bukkit.block;

import java.util.List;
import org.bukkit.block.banner.Pattern;

public interface Banner extends BlockState {
   org.bukkit.DyeColor getBaseColor();

   void setBaseColor(org.bukkit.DyeColor var1);

   List<Pattern> getPatterns();

   void setPatterns(List<Pattern> var1);

   void addPattern(Pattern var1);

   Pattern getPattern(int var1);

   Pattern removePattern(int var1);

   void setPattern(int var1, Pattern var2);

   int numberOfPatterns();
}
