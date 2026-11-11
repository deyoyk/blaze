package net.minecraft.server.v1_8_R3;

public enum EnumItemRarity {
   COMMON(EnumChatFormat.WHITE, "Common"),
   UNCOMMON(EnumChatFormat.YELLOW, "Uncommon"),
   RARE(EnumChatFormat.AQUA, "Rare"),
   EPIC(EnumChatFormat.LIGHT_PURPLE, "Epic");

   public final EnumChatFormat e;
   public final String f;

   private EnumItemRarity(EnumChatFormat var3, String var4) {
      this.e = var3;
      this.f = var4;
   }
}
