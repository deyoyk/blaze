package net.minecraft.server.v1_8_R3;

public class ExceptionEntityNotFound extends CommandException {
   public ExceptionEntityNotFound() {
      this("commands.generic.entity.notFound");
   }

   public ExceptionEntityNotFound(String var1, Object... var2) {
      super(var1, var2);
   }
}
