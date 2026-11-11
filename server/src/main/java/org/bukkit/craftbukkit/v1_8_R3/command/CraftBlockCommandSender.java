package org.bukkit.craftbukkit.v1_8_R3.command;

import java.text.MessageFormat;
import net.minecraft.server.v1_8_R3.CommandBlockListenerAbstract;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.ICommandListener;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;

public class CraftBlockCommandSender extends ServerCommandSender implements BlockCommandSender {
   private final CommandBlockListenerAbstract commandBlock;

   public CraftBlockCommandSender(CommandBlockListenerAbstract commandBlockListenerAbstract) {
      this.commandBlock = commandBlockListenerAbstract;
   }

   @Override
   public Block getBlock() {
      return this.commandBlock
         .getWorld()
         .getWorld()
         .getBlockAt(
            this.commandBlock.getChunkCoordinates().getX(), this.commandBlock.getChunkCoordinates().getY(), this.commandBlock.getChunkCoordinates().getZ()
         );
   }

   @Override
   public void sendMessage(String message) {
      IChatBaseComponent[] var5;
      for (IChatBaseComponent component : var5 = CraftChatMessage.fromString(message)) {
         this.commandBlock.sendMessage(component);
      }
   }

   @Override
   public void sendMessage(String[] messages) {
      for (String message : messages) {
         this.sendMessage(message);
      }
   }

   public void sendFormattedMessage(String message, Object... args) {
      this.sendMessage(MessageFormat.format(message, args));
   }

   @Override
   public String getName() {
      return this.commandBlock.getName();
   }

   @Override
   public boolean isOp() {
      return true;
   }

   @Override
   public void setOp(boolean value) {
      throw new UnsupportedOperationException("Cannot change operator status of a block");
   }

   public ICommandListener getTileEntity() {
      return this.commandBlock;
   }
}
