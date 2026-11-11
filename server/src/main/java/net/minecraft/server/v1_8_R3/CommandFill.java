package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;

public class CommandFill extends CommandAbstract {
   @Override
   public String getCommand() {
      return "fill";
   }

   @Override
   public int a() {
      return 2;
   }

   @Override
   public String getUsage(ICommandListener var1) {
      return "commands.fill.usage";
   }

   @Override
   public void execute(ICommandListener var1, String[] var2) throws CommandException {
      if (var2.length < 7) {
         throw new ExceptionUsage("commands.fill.usage");
      } else {
         var1.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_BLOCKS, 0);
         BlockPosition var3 = a(var1, var2, 0, false);
         BlockPosition var4 = a(var1, var2, 3, false);
         Block var5 = CommandAbstract.g(var1, var2[6]);
         int var6 = 0;
         if (var2.length >= 8) {
            var6 = a(var2[7], 0, 15);
         }

         BlockPosition var7 = new BlockPosition(Math.min(var3.getX(), var4.getX()), Math.min(var3.getY(), var4.getY()), Math.min(var3.getZ(), var4.getZ()));
         BlockPosition var8 = new BlockPosition(Math.max(var3.getX(), var4.getX()), Math.max(var3.getY(), var4.getY()), Math.max(var3.getZ(), var4.getZ()));
         int var9 = (var8.getX() - var7.getX() + 1) * (var8.getY() - var7.getY() + 1) * (var8.getZ() - var7.getZ() + 1);
         if (var9 > 32768) {
            throw new CommandException("commands.fill.tooManyBlocks", var9, 32768);
         } else if (var7.getY() >= 0 && var8.getY() < 256) {
            World var10 = var1.getWorld();

            for (int var11 = var7.getZ(); var11 < var8.getZ() + 16; var11 += 16) {
               for (int var12 = var7.getX(); var12 < var8.getX() + 16; var12 += 16) {
                  if (!var10.isLoaded(new BlockPosition(var12, var8.getY() - var7.getY(), var11))) {
                     throw new CommandException("commands.fill.outOfWorld");
                  }
               }
            }

            NBTTagCompound var23 = new NBTTagCompound();
            boolean var24 = false;
            if (var2.length >= 10 && var5.isTileEntity()) {
               String var13 = a(var1, var2, 9).c();

               try {
                  var23 = MojangsonParser.parse(var13);
                  var24 = true;
               } catch (MojangsonParseException var21) {
                  throw new CommandException("commands.fill.tagError", var21.getMessage());
               }
            }

            ArrayList var25 = Lists.newArrayList();
            var9 = 0;

            for (int var14 = var7.getZ(); var14 <= var8.getZ(); var14++) {
               for (int var15 = var7.getY(); var15 <= var8.getY(); var15++) {
                  for (int var16 = var7.getX(); var16 <= var8.getX(); var16++) {
                     BlockPosition var17 = new BlockPosition(var16, var15, var14);
                     if (var2.length >= 9) {
                        if (!var2[8].equals("outline") && !var2[8].equals("hollow")) {
                           if (var2[8].equals("destroy")) {
                              var10.setAir(var17, true);
                           } else if (var2[8].equals("keep")) {
                              if (!var10.isEmpty(var17)) {
                                 continue;
                              }
                           } else if (var2[8].equals("replace") && !var5.isTileEntity()) {
                              if (var2.length > 9) {
                                 Block var18 = CommandAbstract.g(var1, var2[9]);
                                 if (var10.getType(var17).getBlock() != var18) {
                                    continue;
                                 }
                              }

                              if (var2.length > 10) {
                                 int var29 = CommandAbstract.a(var2[10]);
                                 IBlockData var19 = var10.getType(var17);
                                 if (var19.getBlock().toLegacyData(var19) != var29) {
                                    continue;
                                 }
                              }
                           }
                        } else if (var16 != var7.getX()
                           && var16 != var8.getX()
                           && var15 != var7.getY()
                           && var15 != var8.getY()
                           && var14 != var7.getZ()
                           && var14 != var8.getZ()) {
                           if (var2[8].equals("hollow")) {
                              var10.setTypeAndData(var17, Blocks.AIR.getBlockData(), 2);
                              var25.add(var17);
                           }
                           continue;
                        }
                     }

                     TileEntity var30 = var10.getTileEntity(var17);
                     if (var30 != null) {
                        if (var30 instanceof IInventory) {
                           ((IInventory)var30).l();
                        }

                        var10.setTypeAndData(var17, Blocks.BARRIER.getBlockData(), var5 == Blocks.BARRIER ? 2 : 4);
                     }

                     IBlockData var31 = var5.fromLegacyData(var6);
                     if (var10.setTypeAndData(var17, var31, 2)) {
                        var25.add(var17);
                        var9++;
                        if (var24) {
                           TileEntity var20 = var10.getTileEntity(var17);
                           if (var20 != null) {
                              var23.setInt("x", var17.getX());
                              var23.setInt("y", var17.getY());
                              var23.setInt("z", var17.getZ());
                              var20.a(var23);
                           }
                        }
                     }
                  }
               }
            }

            for (BlockPosition var27 : (List<BlockPosition>)var25) {
               Block var28 = var10.getType(var27).getBlock();
               var10.update(var27, var28);
            }

            if (var9 <= 0) {
               throw new CommandException("commands.fill.failed");
            } else {
               var1.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_BLOCKS, var9);
               a(var1, this, "commands.fill.success", new Object[]{var9});
            }
         } else {
            throw new CommandException("commands.fill.outOfWorld");
         }
      }
   }

   @Override
   public List<String> tabComplete(ICommandListener var1, String[] var2, BlockPosition var3) {
      if (var2.length > 0 && var2.length <= 3) {
         return a(var2, 0, var3);
      } else if (var2.length > 3 && var2.length <= 6) {
         return a(var2, 3, var3);
      } else if (var2.length == 7) {
         return a(var2, Block.REGISTRY.keySet());
      } else if (var2.length == 9) {
         return a(var2, new String[]{"replace", "destroy", "keep", "hollow", "outline"});
      } else {
         return var2.length == 10 && "replace".equals(var2[8]) ? a(var2, Block.REGISTRY.keySet()) : null;
      }
   }
}
