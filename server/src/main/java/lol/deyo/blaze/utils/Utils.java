package lol.deyo.blaze.utils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.MobEffect;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Utils {
   private static final Field KNOWN_COMMANDS;

   static {
      try {
         KNOWN_COMMANDS = SimpleCommandMap.class.getDeclaredField("knownCommands");
         KNOWN_COMMANDS.setAccessible(true);
      } catch (NoSuchFieldException var1) {
         throw new RuntimeException(var1);
      }
   }

   public static void unregister(BukkitCommand command) {
      try {
         CommandMap commandMap = Bukkit.getCommandMap();
         command.unregister(commandMap);
         Map<String, Command> map = (Map<String, Command>)KNOWN_COMMANDS.get(commandMap);
         map.entrySet().removeIf(entry -> entry.getValue().getLabel().equals(command.getLabel()));
      } catch (IllegalAccessException var3) {
         throw new RuntimeException(var3);
      }
   }

   public static void register(String prefix, BukkitCommand command, boolean reload) {
      CommandMap commandMap = Bukkit.getCommandMap();
      Command alreadyRegistered = commandMap.getCommand(command.getName());
      if (!reload || alreadyRegistered == null || alreadyRegistered.getLabel().equals(command.getLabel())) {
         commandMap.register(prefix, command);
      }
   }

   public static void takeItemInHand(Player player, int amount) {
      ItemStack hand = player.getItemInHand();
      if (hand != null) {
         if (hand.getAmount() <= 1) {
            player.setItemInHand(new ItemStack(Material.AIR));
         } else {
            hand.setAmount(hand.getAmount() - amount);
         }

         player.updateInventory();
      }
   }

   public static boolean isInvalidChunk(Chunk chunk, int chunkX, int chunkZ) {
      return chunk == null || !chunk.o() || chunk.locX != chunkX || chunk.locZ != chunkZ;
   }

   public static PotionEffect toBukkitPotion(MobEffect effect) {
      return new PotionEffect(PotionEffectType.getById(effect.getEffectId()), effect.getDuration(), effect.getAmplifier(), effect.isAmbient());
   }

   public static int tryParseIntegerSafe(String string) {
      try {
         return Integer.parseInt(string);
      } catch (IllegalArgumentException var2) {
         return 0;
      }
   }

   public static float tryParseFloatSafe(String string) {
      try {
         return Float.parseFloat(string);
      } catch (IllegalArgumentException var2) {
         return 0.0F;
      }
   }
}
