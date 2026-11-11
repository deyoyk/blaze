package net.minecraft.server.v1_8_R3;

public class ExceptionUnknownCommand extends CommandException {
   public ExceptionUnknownCommand() {
      this("commands.generic.notFound");
   }

   public ExceptionUnknownCommand(String var1, Object... var2) {
      super(var1, var2);
   }
}
