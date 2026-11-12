package org.bukkit.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.bukkit.ChatColor;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class PermissionMessageTest {
   @Test
   public void sendsCreditsBeforeThePermissionMessage() {
      Command command = new Command("test") {
         @Override
         public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            return true;
         }

         @Override
         public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
            return null;
         }
      };
      command.setPermission("test.permission");

      CommandSender sender = mock(CommandSender.class);
      when(sender.hasPermission("test.permission")).thenReturn(false);

      assertFalse(command.testPermission(sender));

      ArgumentCaptor<String> messages = ArgumentCaptor.forClass(String.class);
      verify(sender, times(2)).sendMessage(messages.capture());
      assertEquals(ChatColor.GOLD + "Blaze Spigot " + ChatColor.YELLOW + "by deyo", messages.getAllValues().get(0));
      assertTrue(messages.getAllValues().get(1).contains("do not have permission"));
   }
}
