package net.minecraft.server.v1_8_R3;

import java.io.IOException;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class PacketPlayOutTitle implements Packet<PacketListenerPlayOut> {
   private PacketPlayOutTitle.EnumTitleAction a;
   private IChatBaseComponent b;
   private int c;
   private int d;
   private int e;
   public BaseComponent[] components;

   public PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction action, BaseComponent[] components, int fadeIn, int stay, int fadeOut) {
      this.a = action;
      this.components = components;
      this.c = fadeIn;
      this.d = stay;
      this.e = fadeOut;
   }

   public PacketPlayOutTitle() {
   }

   public PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction packetplayouttitle_enumtitleaction, IChatBaseComponent ichatbasecomponent) {
      this(packetplayouttitle_enumtitleaction, ichatbasecomponent, -1, -1, -1);
   }

   public PacketPlayOutTitle(int i, int j, int k) {
      this(PacketPlayOutTitle.EnumTitleAction.TIMES, (IChatBaseComponent)null, i, j, k);
   }

   public PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction packetplayouttitle_enumtitleaction, IChatBaseComponent ichatbasecomponent, int i, int j, int k) {
      this.a = packetplayouttitle_enumtitleaction;
      this.b = ichatbasecomponent;
      this.c = i;
      this.d = j;
      this.e = k;
   }

   @Override
   public void a(PacketDataSerializer packetdataserializer) throws IOException {
      this.a = packetdataserializer.a(PacketPlayOutTitle.EnumTitleAction.class);
      if (this.a == PacketPlayOutTitle.EnumTitleAction.TITLE || this.a == PacketPlayOutTitle.EnumTitleAction.SUBTITLE) {
         this.b = packetdataserializer.d();
      }

      if (this.a == PacketPlayOutTitle.EnumTitleAction.TIMES) {
         this.c = packetdataserializer.readInt();
         this.d = packetdataserializer.readInt();
         this.e = packetdataserializer.readInt();
      }
   }

   @Override
   public void b(PacketDataSerializer packetdataserializer) throws IOException {
      packetdataserializer.a(this.a);
      if (this.a == PacketPlayOutTitle.EnumTitleAction.TITLE || this.a == PacketPlayOutTitle.EnumTitleAction.SUBTITLE) {
         if (this.components != null) {
            packetdataserializer.a(ComponentSerializer.toString(this.components));
         } else {
            packetdataserializer.a(this.b);
         }
      }

      if (this.a == PacketPlayOutTitle.EnumTitleAction.TIMES) {
         packetdataserializer.writeInt(this.c);
         packetdataserializer.writeInt(this.d);
         packetdataserializer.writeInt(this.e);
      }
   }

   public void a(PacketListenerPlayOut packetlistenerplayout) {
      packetlistenerplayout.a(this);
   }

   public static enum EnumTitleAction {
      TITLE,
      SUBTITLE,
      TIMES,
      CLEAR,
      RESET;

      public static PacketPlayOutTitle.EnumTitleAction a(String s) {
         PacketPlayOutTitle.EnumTitleAction[] var4;
         for (PacketPlayOutTitle.EnumTitleAction packetplayouttitle_enumtitleaction : var4 = values()) {
            if (packetplayouttitle_enumtitleaction.name().equalsIgnoreCase(s)) {
               return packetplayouttitle_enumtitleaction;
            }
         }

         return TITLE;
      }

      public static String[] a() {
         String[] astring = new String[values().length];
         int i = 0;

         PacketPlayOutTitle.EnumTitleAction[] var5;
         for (PacketPlayOutTitle.EnumTitleAction packetplayouttitle_enumtitleaction : var5 = values()) {
            astring[i++] = packetplayouttitle_enumtitleaction.name().toLowerCase();
         }

         return astring;
      }
   }
}
