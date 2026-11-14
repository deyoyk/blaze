package lol.deyo.blaze.updater;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Versions {
   private static final Pattern RELEASE = Pattern.compile("^v(\\d+)$", Pattern.CASE_INSENSITIVE);

   private Versions() {
   }

   public static int parse(String version) {
      if (version == null) {
         return -1;
      }

      Matcher matcher = RELEASE.matcher(version.trim());
      if (!matcher.matches()) {
         return -1;
      }

      try {
         return Integer.parseInt(matcher.group(1));
      } catch (NumberFormatException exception) {
         return -1;
      }
   }
}
