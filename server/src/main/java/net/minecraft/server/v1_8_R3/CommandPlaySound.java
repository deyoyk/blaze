package net.minecraft.server.v1_8_R3;

import java.util.List;

public class CommandPlaySound extends CommandAbstract {
   @Override
   public String getCommand() {
      return "playsound";
   }

   @Override
   public int a() {
      return 2;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.playsound.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (var2.length < 2) {
         throw new ExceptionUsage(this.getUsage(var1));
      } else {
         int var3 = 0;
         String var4 = var2[var3++];
         EntityPlayer var5 = a(var1, var2[var3++]);
         Vec3D var6 = var1.d();
         double var7 = var6.a;
         if (var2.length > var3) {
            var7 = b(var7, var2[var3++], true);
         }

         double var9 = var6.b;
         if (var2.length > var3) {
            var9 = b(var9, var2[var3++], 0, 0, false);
         }

         double var11 = var6.c;
         if (var2.length > var3) {
            var11 = b(var11, var2[var3++], true);
         }

         double var13 = 1.0;
         if (var2.length > var3) {
            var13 = a(var2[var3++], 0.0, Float.MAX_VALUE);
         }

         double var15 = 1.0;
         if (var2.length > var3) {
            var15 = a(var2[var3++], 0.0, 2.0);
         }

         double var17 = 0.0;
         if (var2.length > var3) {
            var17 = a(var2[var3], 0.0, 1.0);
         }

         double var19 = var13 > 1.0 ? var13 * 16.0 : 16.0;
         double var21 = var5.f(var7, var9, var11);
         if (var21 > var19) {
            if (var17 <= 0.0) {
               throw new CommandException("commands.playsound.playerTooFar", var5.getName());
            }

            double var23 = var7 - var5.locX;
            double var25 = var9 - var5.locY;
            double var27 = var11 - var5.locZ;
            double var29 = Math.sqrt(var23 * var23 + var25 * var25 + var27 * var27);
            if (var29 > 0.0) {
               var7 = var5.locX + var23 / var29 * 2.0;
               var9 = var5.locY + var25 / var29 * 2.0;
               var11 = var5.locZ + var27 / var29 * 2.0;
            }

            var13 = var17;
         }

         var5.playerConnection.sendPacket(new PacketPlayOutNamedSoundEffect(var4, var7, var9, var11, (float)var13, (float)var15));
         a(var1, this, "commands.playsound.success", new Object[]{var4, var5.getName()});
      }
   }

   @Override
   public List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3) {
      if (var2.length == 2) {
         return a(var2, MinecraftServer.getServer().getPlayers());
      } else {
         return var2.length > 2 && var2.length <= 5 ? a(var2, 2, var3) : null;
      }
   }

   @Override
   public boolean isListStart(String[] var1, int var2) {
      return var2 == 1;
   }
}
