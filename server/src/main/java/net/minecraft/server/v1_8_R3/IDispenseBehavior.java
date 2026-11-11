package net.minecraft.server.v1_8_R3;

public interface IDispenseBehavior {
   IDispenseBehavior NONE = new IDispenseBehavior() {
      @Override
      public ItemStack a(ISourceBlock var1, ItemStack var2) {
         return var2;
      }
   };

   ItemStack a(ISourceBlock var1, ItemStack var2);
}
