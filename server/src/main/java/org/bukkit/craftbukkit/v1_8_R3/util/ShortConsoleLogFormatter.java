package org.bukkit.craftbukkit.v1_8_R3.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import joptsimple.OptionException;
import joptsimple.OptionSet;
import net.minecraft.server.v1_8_R3.MinecraftServer;

public class ShortConsoleLogFormatter extends Formatter {
   private final SimpleDateFormat date;

   public ShortConsoleLogFormatter(MinecraftServer server) {
      OptionSet options = server.options;
      SimpleDateFormat date = null;
      if (options.has("date-format")) {
         try {
            Object object = options.valueOf("date-format");
            if (object instanceof SimpleDateFormat) {
               date = (SimpleDateFormat)object;
            }
         } catch (OptionException var5) {
            System.err.println("Given date format is not valid. Falling back to default.");
         }
      } else if (options.has("nojline")) {
         date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      }

      if (date == null) {
         date = new SimpleDateFormat("HH:mm:ss");
      }

      this.date = date;
   }

   @Override
   public String format(LogRecord record) {
      StringBuilder builder = new StringBuilder();
      Throwable ex = record.getThrown();
      builder.append(this.date.format(record.getMillis()));
      builder.append(" [");
      builder.append(record.getLevel().getLocalizedName().toUpperCase());
      builder.append("] ");
      builder.append(this.formatMessage(record));
      builder.append('\n');
      if (ex != null) {
         StringWriter writer = new StringWriter();
         ex.printStackTrace(new PrintWriter(writer));
         builder.append(writer);
      }

      return builder.toString();
   }
}
