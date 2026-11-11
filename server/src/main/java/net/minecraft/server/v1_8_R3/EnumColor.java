package net.minecraft.server.v1_8_R3;

public enum EnumColor implements INamable {
   WHITE(0, 15, "white", "white", MaterialMapColor.j, EnumChatFormat.WHITE),
   ORANGE(1, 14, "orange", "orange", MaterialMapColor.q, EnumChatFormat.GOLD),
   MAGENTA(2, 13, "magenta", "magenta", MaterialMapColor.r, EnumChatFormat.AQUA),
   LIGHT_BLUE(3, 12, "light_blue", "lightBlue", MaterialMapColor.s, EnumChatFormat.BLUE),
   YELLOW(4, 11, "yellow", "yellow", MaterialMapColor.t, EnumChatFormat.YELLOW),
   LIME(5, 10, "lime", "lime", MaterialMapColor.u, EnumChatFormat.GREEN),
   PINK(6, 9, "pink", "pink", MaterialMapColor.v, EnumChatFormat.LIGHT_PURPLE),
   GRAY(7, 8, "gray", "gray", MaterialMapColor.w, EnumChatFormat.DARK_GRAY),
   SILVER(8, 7, "silver", "silver", MaterialMapColor.x, EnumChatFormat.GRAY),
   CYAN(9, 6, "cyan", "cyan", MaterialMapColor.y, EnumChatFormat.DARK_AQUA),
   PURPLE(10, 5, "purple", "purple", MaterialMapColor.z, EnumChatFormat.DARK_PURPLE),
   BLUE(11, 4, "blue", "blue", MaterialMapColor.A, EnumChatFormat.DARK_BLUE),
   BROWN(12, 3, "brown", "brown", MaterialMapColor.B, EnumChatFormat.GOLD),
   GREEN(13, 2, "green", "green", MaterialMapColor.C, EnumChatFormat.DARK_GREEN),
   RED(14, 1, "red", "red", MaterialMapColor.D, EnumChatFormat.DARK_RED),
   BLACK(15, 0, "black", "black", MaterialMapColor.E, EnumChatFormat.BLACK);

   private static final EnumColor[] q = new EnumColor[values().length];
   private static final EnumColor[] r = new EnumColor[values().length];
   private final int s;
   private final int t;
   private final String u;
   private final String v;
   private final MaterialMapColor w;
   private final EnumChatFormat x;

   private EnumColor(int var3, int var4, String var5, String var6, MaterialMapColor var7, EnumChatFormat var8) {
      this.s = var3;
      this.t = var4;
      this.u = var5;
      this.v = var6;
      this.w = var7;
      this.x = var8;
   }

   public int getColorIndex() {
      return this.s;
   }

   public int getInvColorIndex() {
      return this.t;
   }

   public String d() {
      return this.v;
   }

   public MaterialMapColor e() {
      return this.w;
   }

   public static EnumColor fromInvColorIndex(int var0) {
      if (var0 < 0 || var0 >= r.length) {
         var0 = 0;
      }

      return r[var0];
   }

   public static EnumColor fromColorIndex(int var0) {
      if (var0 < 0 || var0 >= q.length) {
         var0 = 0;
      }

      return q[var0];
   }

   @Override
   public String toString() {
      return this.v;
   }

   @Override
   public String getName() {
      return this.u;
   }

   static {
      for (EnumColor var3 : values()) {
         q[var3.getColorIndex()] = var3;
         r[var3.getInvColorIndex()] = var3;
      }
   }
}
