package net.minecraft.server.v1_8_R3;

import java.util.EnumSet;
import java.util.List;

public class CommandTp extends CommandAbstract {
   @Override
   public String getCommand() {
      return "tp";
   }

   @Override
   public int a() {
      return 2;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.tp.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (var2.length < 1) {
         throw new ExceptionUsage("commands.tp.usage");
      } else {
         byte var3 = 0;
         Object var4;
         if (var2.length != 2 && var2.length != 4 && var2.length != 6) {
            var4 = b(var1);
         } else {
            var4 = b(var1, var2[0]);
            var3 = 1;
         }

         if (var2.length == 1 || var2.length == 2) {
            Entity var16 = b(var1, var2[var2.length - 1]);
            if (var16.world != ((Entity)var4).world) {
               throw new CommandException("commands.tp.notSameDimension");
            } else {
               ((Entity)var4).mount(null);
               if (var4 instanceof EntityPlayer) {
                  ((EntityPlayer)var4).playerConnection.a(var16.locX, var16.locY, var16.locZ, var16.yaw, var16.pitch);
               } else {
                  ((Entity)var4).setPositionRotation(var16.locX, var16.locY, var16.locZ, var16.yaw, var16.pitch);
               }

               a(var1, this, "commands.tp.success", new Object[]{((Entity)var4).getName(), var16.getName()});
            }
         } else if (var2.length < var3 + 3) {
            throw new ExceptionUsage("commands.tp.usage");
         } else if (((Entity)var4).world != null) {
            int var5 = var3 + 1;
            CommandAbstract.CommandNumber var6 = a(((Entity)var4).locX, var2[var3], true);
            CommandAbstract.CommandNumber var7 = a(((Entity)var4).locY, var2[var5++], 0, 0, false);
            CommandAbstract.CommandNumber var8 = a(((Entity)var4).locZ, var2[var5++], true);
            CommandAbstract.CommandNumber var9 = a(((Entity)var4).yaw, var2.length > var5 ? var2[var5++] : "~", false);
            CommandAbstract.CommandNumber var10 = a(((Entity)var4).pitch, var2.length > var5 ? var2[var5] : "~", false);
            if (var4 instanceof EntityPlayer) {
               EnumSet var11 = EnumSet.noneOf(PacketPlayOutPosition.EnumPlayerTeleportFlags.class);
               if (var6.c()) {
                  var11.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.X);
               }

               if (var7.c()) {
                  var11.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y);
               }

               if (var8.c()) {
                  var11.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.Z);
               }

               if (var10.c()) {
                  var11.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.X_ROT);
               }

               if (var9.c()) {
                  var11.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y_ROT);
               }

               float var12 = (float)var9.b();
               if (!var9.c()) {
                  var12 = MathHelper.g(var12);
               }

               float var13 = (float)var10.b();
               if (!var10.c()) {
                  var13 = MathHelper.g(var13);
               }

               if (var13 > 90.0F || var13 < -90.0F) {
                  var13 = MathHelper.g(180.0F - var13);
                  var12 = MathHelper.g(var12 + 180.0F);
               }

               ((Entity)var4).mount(null);
               ((EntityPlayer)var4).playerConnection.a(var6.b(), var7.b(), var8.b(), var12, var13, var11);
               ((Entity)var4).f(var12);
            } else {
               float var17 = (float)MathHelper.g(var9.a());
               float var18 = (float)MathHelper.g(var10.a());
               if (var18 > 90.0F || var18 < -90.0F) {
                  var18 = MathHelper.g(180.0F - var18);
                  var17 = MathHelper.g(var17 + 180.0F);
               }

               ((Entity)var4).setPositionRotation(var6.a(), var7.a(), var8.a(), var17, var18);
               ((Entity)var4).f(var17);
            }

            a(var1, this, "commands.tp.success.coordinates", new Object[]{((Entity)var4).getName(), var6.a(), var7.a(), var8.a()});
         }
      }
   }

   @Override
   public List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3) {
      return var2.length != 1 && var2.length != 2 ? null : a(var2, MinecraftServer.getServer().getPlayers());
   }

   @Override
   public boolean isListStart(String[] var1, int var2) {
      return var2 == 0;
   }
}
