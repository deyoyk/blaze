package net.minecraft.server.v1_8_R3;

public class EntityDamageSource extends DamageSource {
   protected Entity q;
   private boolean r = false;

   public EntityDamageSource(String var1, Entity var2) {
      super(var1);
      this.q = var2;
   }

   public EntityDamageSource v() {
      this.r = true;
      return this;
   }

   public boolean w() {
      return this.r;
   }

   @Override
   public Entity getEntity() {
      return this.q;
   }

   @Override
   public IChatBaseComponent getLocalizedDeathMessage(EntityLiving var1) {
      ItemStack var2 = this.q instanceof EntityLiving ? ((EntityLiving)this.q).bA() : null;
      String var3 = "death.attack." + this.translationIndex;
      String var4 = var3 + ".item";
      return var2 != null && var2.hasName() && LocaleI18n.c(var4)
         ? new ChatMessage(var4, var1.getScoreboardDisplayName(), this.q.getScoreboardDisplayName(), var2.C())
         : new ChatMessage(var3, var1.getScoreboardDisplayName(), this.q.getScoreboardDisplayName());
   }

   @Override
   public boolean r() {
      return this.q != null && this.q instanceof EntityLiving && !(this.q instanceof EntityHuman);
   }
}
