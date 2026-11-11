package net.minecraft.server.v1_8_R3;

public class ExceptionInvalidSyntax extends CommandException {
   public ExceptionInvalidSyntax() {
      this("commands.generic.snytax");
   }

   public ExceptionInvalidSyntax(String var1, Object... var2) {
      super(var1, var2);
   }
}
