package net.minecraft.server.v1_8_R3;

public class Achievement extends Statistic {
   public final int a;
   public final int b;
   public final Achievement c;
   private final String k;
   public final ItemStack d;
   private boolean m;

   public Achievement(String s, String s1, int i, int j, Item item, Achievement achievement) {
      this(s, s1, i, j, new ItemStack(item), achievement);
   }

   public Achievement(String s, String s1, int i, int j, Block block, Achievement achievement) {
      this(s, s1, i, j, new ItemStack(block), achievement);
   }

   public Achievement(String s, String s1, int i, int j, ItemStack itemstack, Achievement achievement) {
      super(s, new ChatMessage("achievement." + s1));
      this.d = itemstack;
      this.k = "achievement." + s1 + ".desc";
      this.a = i;
      this.b = j;
      if (i > AchievementList.c) {
         AchievementList.c = i;
      }

      if (j > AchievementList.d) {
         AchievementList.d = j;
      }

      this.c = achievement;
   }

   public Achievement a() {
      this.f = true;
      return this;
   }

   public Achievement b() {
      this.m = true;
      return this;
   }

   public Achievement c() {
      super.h();
      AchievementList.e.add(this);
      return this;
   }

   @Override
   public boolean d() {
      return true;
   }

   @Override
   public IChatBaseComponent e() {
      IChatBaseComponent ichatbasecomponent = super.e();
      ichatbasecomponent.getChatModifier().setColor(this.g() ? EnumChatFormat.DARK_PURPLE : EnumChatFormat.GREEN);
      return ichatbasecomponent;
   }

   public Achievement a(Class<? extends IJsonStatistic> oclass) {
      return (Achievement)super.b(oclass);
   }

   public boolean g() {
      return this.m;
   }

   @Override
   public Statistic b(Class<? extends IJsonStatistic> oclass) {
      return this.a(oclass);
   }

   @Override
   public Statistic h() {
      return this.c();
   }

   @Override
   public Statistic i() {
      return this.a();
   }
}
