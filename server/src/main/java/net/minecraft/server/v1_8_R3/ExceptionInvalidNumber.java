package net.minecraft.server.v1_8_R3;

public class ExceptionInvalidNumber extends CommandException {
   public ExceptionInvalidNumber() {
      this("commands.generic.num.invalid");
   }

   public ExceptionInvalidNumber(String var1, Object... var2) {
      super(var1, var2);
   }
}
