package net.minecraft.server.v1_8_R3;

import java.util.List;

public class CommandDifficulty extends CommandAbstract {
   @Override
   public String getCommand() {
      return "difficulty";
   }

   @Override
   public int a() {
      return 2;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.difficulty.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (var2.length <= 0) {
         throw new ExceptionUsage("commands.difficulty.usage");
      } else {
         EnumDifficulty var3 = this.e(var2[0]);
         MinecraftServer.getServer().a(var3);
         a(var1, this, "commands.difficulty.success", new Object[]{new ChatMessage(var3.b())});
      }
   }

   protected EnumDifficulty e(String var1) throws ExceptionInvalidNumber {
      if (var1.equalsIgnoreCase("peaceful") || var1.equalsIgnoreCase("p")) {
         return EnumDifficulty.PEACEFUL;
      } else if (var1.equalsIgnoreCase("easy") || var1.equalsIgnoreCase("e")) {
         return EnumDifficulty.EASY;
      } else if (var1.equalsIgnoreCase("normal") || var1.equalsIgnoreCase("n")) {
         return EnumDifficulty.NORMAL;
      } else {
         return !var1.equalsIgnoreCase("hard") && !var1.equalsIgnoreCase("h") ? EnumDifficulty.getById(a(var1, 0, 3)) : EnumDifficulty.HARD;
      }
   }

   @Override
   public List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3) {
      return var2.length == 1 ? a(var2, new String[]{"peaceful", "easy", "normal", "hard"}) : null;
   }
}
