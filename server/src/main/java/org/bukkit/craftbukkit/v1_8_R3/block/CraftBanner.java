package org.bukkit.craftbukkit.v1_8_R3.block;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.TileEntityBanner;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class CraftBanner extends CraftBlockState implements Banner {
   private final TileEntityBanner banner;
   private org.bukkit.DyeColor base;
   private List<Pattern> patterns = new ArrayList<>();

   public CraftBanner(Block block) {
      super(block);
      CraftWorld world = (CraftWorld)block.getWorld();
      this.banner = (TileEntityBanner)world.getTileEntityAt(this.getX(), this.getY(), this.getZ());
      this.base = org.bukkit.DyeColor.getByDyeData((byte)this.banner.color);
      if (this.banner.patterns != null) {
         for (int i = 0; i < this.banner.patterns.size(); i++) {
            NBTTagCompound p = this.banner.patterns.get(i);
            this.patterns.add(new Pattern(org.bukkit.DyeColor.getByDyeData((byte)p.getInt("Color")), PatternType.getByIdentifier(p.getString("Pattern"))));
         }
      }
   }

   public CraftBanner(org.bukkit.Material material, TileEntityBanner te) {
      super(material);
      this.banner = te;
      this.base = org.bukkit.DyeColor.getByDyeData((byte)this.banner.color);
      if (this.banner.patterns != null) {
         for (int i = 0; i < this.banner.patterns.size(); i++) {
            NBTTagCompound p = this.banner.patterns.get(i);
            this.patterns.add(new Pattern(org.bukkit.DyeColor.getByDyeData((byte)p.getInt("Color")), PatternType.getByIdentifier(p.getString("Pattern"))));
         }
      }
   }

   @Override
   public org.bukkit.DyeColor getBaseColor() {
      return this.base;
   }

   @Override
   public void setBaseColor(org.bukkit.DyeColor color) {
      this.base = color;
   }

   @Override
   public List<Pattern> getPatterns() {
      return new ArrayList<>(this.patterns);
   }

   @Override
   public void setPatterns(List<Pattern> patterns) {
      this.patterns = new ArrayList<>(patterns);
   }

   @Override
   public void addPattern(Pattern pattern) {
      this.patterns.add(pattern);
   }

   @Override
   public Pattern getPattern(int i) {
      return this.patterns.get(i);
   }

   @Override
   public Pattern removePattern(int i) {
      return this.patterns.remove(i);
   }

   @Override
   public void setPattern(int i, Pattern pattern) {
      this.patterns.set(i, pattern);
   }

   @Override
   public int numberOfPatterns() {
      return this.patterns.size();
   }

   @Override
   public boolean update(boolean force, boolean applyPhysics) {
      boolean result = super.update(force, applyPhysics);
      if (result) {
         this.banner.color = this.base.getDyeData();
         NBTTagList newPatterns = new NBTTagList();

         for (Pattern p : this.patterns) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInt("Color", p.getColor().getDyeData());
            compound.setString("Pattern", p.getPattern().getIdentifier());
            newPatterns.add(compound);
         }

         this.banner.patterns = newPatterns;
         this.banner.update();
      }

      return result;
   }
}
