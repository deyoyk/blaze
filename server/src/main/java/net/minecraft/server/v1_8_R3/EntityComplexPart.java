package net.minecraft.server.v1_8_R3;

public class EntityComplexPart extends Entity {
   public final IComplex owner;
   public final String b;

   public EntityComplexPart(IComplex icomplex, String s, float f, float f1) {
      super(icomplex.a());
      this.setSize(f, f1);
      this.owner = icomplex;
      this.b = s;
   }

   @Override
   protected void h() {
   }

   @Override
   protected void a(NBTTagCompound nbttagcompound) {
   }

   @Override
   protected void b(NBTTagCompound nbttagcompound) {
   }

   @Override
   public boolean ad() {
      return true;
   }

   @Override
   public boolean damageEntity(DamageSource damagesource, float f) {
      return !this.isInvulnerable(damagesource) && this.owner.a(this, damagesource, f);
   }

   @Override
   public boolean k(Entity entity) {
      return this == entity || this.owner == entity;
   }
}
