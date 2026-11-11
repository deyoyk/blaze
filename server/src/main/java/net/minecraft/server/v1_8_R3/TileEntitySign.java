package net.minecraft.server.v1_8_R3;

import com.google.gson.JsonParseException;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;

public class TileEntitySign extends TileEntity {
   public final IChatBaseComponent[] lines = new IChatBaseComponent[]{
      new ChatComponentText(""), new ChatComponentText(""), new ChatComponentText(""), new ChatComponentText("")
   };
   public int f = -1;
   public boolean isEditable = true;
   private EntityHuman h;
   private final CommandObjectiveExecutor i = new CommandObjectiveExecutor();

   @Override
   public void b(NBTTagCompound nbttagcompound) {
      super.b(nbttagcompound);

      for (int i = 0; i < 4; i++) {
         String s = IChatBaseComponent.ChatSerializer.a(this.lines[i]);
         nbttagcompound.setString("Text" + (i + 1), s);
      }

      if (Boolean.getBoolean("convertLegacySigns")) {
         nbttagcompound.setBoolean("Bukkit.isConverted", true);
      }

      this.i.b(nbttagcompound);
   }

   @Override
   public void a(NBTTagCompound nbttagcompound) {
      this.isEditable = false;
      super.a(nbttagcompound);
      ICommandListener icommandlistener = new ICommandListener() {
         @Override
         public String getName() {
            return "Sign";
         }

         @Override
         public IChatBaseComponent getScoreboardDisplayName() {
            return new ChatComponentText(this.getName());
         }

         @Override
         public void sendMessage(IChatBaseComponent ichatbasecomponent) {
         }

         @Override
         public boolean a(int i, String s) {
            return true;
         }

         @Override
         public BlockPosition getChunkCoordinates() {
            return TileEntitySign.this.position;
         }

         @Override
         public Vec3D d() {
            return new Vec3D(TileEntitySign.this.position.getX() + 0.5, TileEntitySign.this.position.getY() + 0.5, TileEntitySign.this.position.getZ() + 0.5);
         }

         @Override
         public World getWorld() {
            return TileEntitySign.this.world;
         }

         @Override
         public Entity f() {
            return null;
         }

         @Override
         public boolean getSendCommandFeedback() {
            return false;
         }

         @Override
         public void a(CommandObjectiveExecutor.EnumCommandResult commandobjectiveexecutor_enumcommandresult, int i) {
         }
      };
      boolean oldSign = Boolean.getBoolean("convertLegacySigns") && !nbttagcompound.getBoolean("Bukkit.isConverted");

      for (int i = 0; i < 4; i++) {
         String s = nbttagcompound.getString("Text" + (i + 1));
         if (s != null && s.length() > 2048) {
            s = "\"\"";
         }

         try {
            IChatBaseComponent ichatbasecomponent = IChatBaseComponent.ChatSerializer.a(s);
            if (oldSign) {
               this.lines[i] = CraftChatMessage.fromString(s)[0];
            } else {
               try {
                  this.lines[i] = ChatComponentUtils.filterForDisplay(icommandlistener, ichatbasecomponent, null);
               } catch (CommandException var8) {
                  this.lines[i] = ichatbasecomponent;
               }
            }
         } catch (JsonParseException var9) {
            this.lines[i] = new ChatComponentText(s);
         }
      }

      this.i.a(nbttagcompound);
   }

   @Override
   public Packet<?> getUpdatePacket() {
      IChatBaseComponent[] aichatbasecomponent = new IChatBaseComponent[4];
      System.arraycopy(this.lines, 0, aichatbasecomponent, 0, 4);
      return new PacketPlayOutUpdateSign(this.world, this.position, aichatbasecomponent);
   }

   @Override
   public boolean F() {
      return true;
   }

   public boolean b() {
      return this.isEditable;
   }

   public void a(EntityHuman entityhuman) {
      this.h = entityhuman;
   }

   public EntityHuman c() {
      return this.h;
   }

   public boolean b(EntityHuman entityhuman) {
      for (IChatBaseComponent line : this.lines) {
         ChatModifier chatmodifier = line == null ? null : line.getChatModifier();
         if (chatmodifier != null && chatmodifier.h() != null) {
            ChatClickable chatclickable = chatmodifier.h();
            if (chatclickable.a() == ChatClickable.EnumClickAction.RUN_COMMAND) {
               CommandBlockListenerAbstract.executeCommand(entityhuman, entityhuman.getBukkitEntity(), chatclickable.b());
            }
         }
      }

      return true;
   }

   public CommandObjectiveExecutor d() {
      return this.i;
   }
}
