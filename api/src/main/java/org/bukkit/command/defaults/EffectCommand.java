package org.bukkit.command.defaults;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.StringUtil;

@Deprecated
public class EffectCommand extends VanillaCommand {
   private static final List<String> effects;

   public EffectCommand() {
      super("effect");
      this.description = "Adds/Removes effects on players";
      this.usageMessage = "/effect <player> <effect|clear> [seconds] [amplifier]";
      this.setPermission("bukkit.command.effect");
   }

   @Override
   public boolean execute(CommandSender sender, String commandLabel, String[] args) {
      if (!this.testPermission(sender)) {
         return true;
      } else if (args.length < 2) {
         sender.sendMessage(this.getUsage());
         return true;
      } else {
         Player player = sender.getServer().getPlayer(args[0]);
         if (player == null) {
            sender.sendMessage(org.bukkit.ChatColor.RED + String.format("Player, %s, not found", args[0]));
            return true;
         } else if (!"clear".equalsIgnoreCase(args[1])) {
            PotionEffectType effect = PotionEffectType.getByName(args[1]);
            if (effect == null) {
               effect = PotionEffectType.getById(this.getInteger(sender, args[1], 0));
            }

            if (effect == null) {
               sender.sendMessage(org.bukkit.ChatColor.RED + String.format("Effect, %s, not found", args[1]));
               return true;
            } else {
               int duration = 600;
               int duration_temp = 30;
               int amplification = 0;
               if (args.length >= 3) {
                  duration_temp = this.getInteger(sender, args[2], 0, 1000000);
                  if (effect.isInstant()) {
                     duration = duration_temp;
                  } else {
                     duration = duration_temp * 20;
                  }
               } else if (effect.isInstant()) {
                  duration = 1;
               }

               if (args.length >= 4) {
                  amplification = this.getInteger(sender, args[3], 0, 255);
               }

               if (duration_temp == 0) {
                  if (!player.hasPotionEffect(effect)) {
                     sender.sendMessage(String.format("Couldn't take %s from %s as they do not have the effect", effect.getName(), args[0]));
                     return true;
                  }

                  player.removePotionEffect(effect);
                  broadcastCommandMessage(sender, String.format("Took %s from %s", effect.getName(), args[0]));
               } else {
                  PotionEffect applyEffect = new PotionEffect(effect, duration, amplification);
                  player.addPotionEffect(applyEffect, true);
                  broadcastCommandMessage(
                     sender,
                     String.format("Given %s (ID %d) * %d to %s for %d seconds", effect.getName(), effect.getId(), amplification, args[0], duration_temp)
                  );
               }

               return true;
            }
         } else {
            for (PotionEffect effectx : player.getActivePotionEffects()) {
               player.removePotionEffect(effectx.getType());
            }

            sender.sendMessage(String.format("Took all effects from %s", args[0]));
            return true;
         }
      }
   }

   @Override
   public List<String> tabComplete(CommandSender sender, String commandLabel, String[] args) {
      if (args.length == 1) {
         return super.tabComplete(sender, commandLabel, args);
      } else {
         return (List<String>)(args.length == 2 ? StringUtil.copyPartialMatches(args[1], effects, new ArrayList<>(effects.size())) : ImmutableList.of());
      }
   }

   static {
      Builder<String> builder = ImmutableList.builder();

      for (PotionEffectType type : PotionEffectType.values()) {
         if (type != null) {
            builder.add(type.getName());
         }
      }

      effects = builder.build();
   }
}
