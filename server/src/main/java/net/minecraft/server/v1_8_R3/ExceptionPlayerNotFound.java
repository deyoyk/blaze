package net.minecraft.server.v1_8_R3;

public class ExceptionPlayerNotFound extends CommandException {
   public ExceptionPlayerNotFound() {
      this("commands.generic.player.notFound");
   }

   public ExceptionPlayerNotFound(String var1, Object... var2) {
      super(var1, var2);
   }
}
