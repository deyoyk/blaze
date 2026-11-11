package org.bukkit.command;

import java.util.ArrayList;

public class FormattedCommandAlias extends Command {
   private final String[] formatStrings;

   public FormattedCommandAlias(String alias, String[] formatStrings) {
      super(alias);
      this.formatStrings = formatStrings;
   }

   @Override
   public boolean execute(CommandSender sender, String commandLabel, String[] args) {
      boolean result = false;
      ArrayList<String> commands = new ArrayList<>();

      for (String formatString : this.formatStrings) {
         try {
            commands.add(this.buildCommand(formatString, args));
         } catch (Throwable var11) {
            if (var11 instanceof IllegalArgumentException) {
               sender.sendMessage(var11.getMessage());
            } else {
               sender.sendMessage(org.bukkit.ChatColor.RED + "An internal error occurred while attempting to perform this command");
            }

            return false;
         }
      }

      for (String command : commands) {
         result |= org.bukkit.Bukkit.dispatchCommand(sender, command);
      }

      return result;
   }

   private String buildCommand(String formatString, String[] args) {
      int index = formatString.indexOf("$");

      while (index != -1) {
         int start = index;
         if (index <= 0 || formatString.charAt(index - 1) != '\\') {
            boolean required = false;
            if (formatString.charAt(index + 1) == '$') {
               required = true;
               index++;
            }

            int argStart = ++index;

            while (index < formatString.length() && inRange(formatString.charAt(index) - '0', 0, 9)) {
               index++;
            }

            if (argStart == index) {
               throw new IllegalArgumentException("Invalid replacement token");
            }

            int position = Integer.parseInt(formatString.substring(argStart, index));
            if (position == 0) {
               throw new IllegalArgumentException("Invalid replacement token");
            }

            position--;
            boolean rest = false;
            if (index < formatString.length() && formatString.charAt(index) == '-') {
               rest = true;
               index++;
            }

            if (required && position >= args.length) {
               throw new IllegalArgumentException("Missing required argument " + (position + 1));
            }

            StringBuilder replacement = new StringBuilder();
            if (rest && position < args.length) {
               for (int i = position; i < args.length; i++) {
                  if (i != position) {
                     replacement.append(' ');
                  }

                  replacement.append(args[i]);
               }
            } else if (position < args.length) {
               replacement.append(args[position]);
            }

            formatString = formatString.substring(0, start) + replacement.toString() + formatString.substring(index);
            index = start + replacement.length();
            index = formatString.indexOf("$", index);
         } else {
            formatString = formatString.substring(0, index - 1) + formatString.substring(index);
            index = formatString.indexOf("$", index);
         }
      }

      return formatString;
   }

   @Override
   public String getTimingName() {
      return "Command Forwarder - " + super.getTimingName();
   }

   private static boolean inRange(int i, int j, int k) {
      return i >= j && i <= k;
   }
}
