package lol.deyo.blaze.menu.extra;

import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.api.knockback.IKnockback;
import lol.deyo.blaze.framework.menu.Menu;
import lol.deyo.blaze.generator.Generator;
import lol.deyo.blaze.knockback.Knockback;
import lol.deyo.blaze.menu.ConfigEditMenu;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;

public enum ProfileType {
   KNOCKBACK("&dKnockback Profiles", "&4"),
   GENERATOR("&dGenerator Profiles", "&a");

   private final String title;
   private final String color;

   private ProfileType(String title, String color) {
      this.title = title;
      this.color = color;
   }

   public String getTitle() {
      return this.title;
   }

   public String getColor() {
      return this.color;
   }

   public void openEdit(Player player, String string, Menu previous) {
      switch (this) {
         case KNOCKBACK:
            IKnockback knockback = Blaze.getInstance().getKnockbackManager().getByName(string);
            new ConfigEditMenu(player, (Knockback)knockback, previous, ((Knockback)knockback).getActiveSettings()).open();
            break;
         case GENERATOR:
            Generator generator = Blaze.getInstance().getGeneratorManager().getOrCreate(string);
            new ConfigEditMenu(player, generator, previous, generator.getSettings()).open();
      }
   }

   public void addProfiles(List<String> list) {
      switch (this) {
         case KNOCKBACK:
            list.addAll(Blaze.getInstance().getKnockbackManager().getProfiles().stream().map(IKnockback::getName).collect(Collectors.toList()));
            break;
         case GENERATOR:
            list.addAll(Blaze.getInstance().getGeneratorManager().getGenerators().values().stream().map(Generator::getName).collect(Collectors.toList()));
      }
   }
}
