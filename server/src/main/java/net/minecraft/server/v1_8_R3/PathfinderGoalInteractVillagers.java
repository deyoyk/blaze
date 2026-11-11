package net.minecraft.server.v1_8_R3;

public class PathfinderGoalInteractVillagers extends PathfinderGoalInteract {
   private int e;
   private final EntityVillager f;

   public PathfinderGoalInteractVillagers(EntityVillager entityvillager) {
      super(entityvillager, EntityVillager.class, 3.0F, 0.02F);
      this.f = entityvillager;
   }

   @Override
   public void c() {
      super.c();
      if (this.f.cs() && this.b instanceof EntityVillager && ((EntityVillager)this.b).ct()) {
         this.e = 10;
      } else {
         this.e = 0;
      }
   }

   @Override
   public void e() {
      super.e();
      if (this.e > 0) {
         this.e--;
         if (this.e == 0) {
            InventorySubcontainer inventorysubcontainer = this.f.cq();

            for (int i = 0; i < inventorysubcontainer.getSize(); i++) {
               ItemStack itemstack = inventorysubcontainer.getItem(i);
               ItemStack itemstack1 = null;
               if (itemstack != null) {
                  Item item = itemstack.getItem();
                  if ((item == Items.BREAD || item == Items.POTATO || item == Items.CARROT) && itemstack.count > 3) {
                     int j = itemstack.count / 2;
                     itemstack.count -= j;
                     itemstack1 = new ItemStack(item, j, itemstack.getData());
                  } else if (item == Items.WHEAT && itemstack.count > 5) {
                     int j = itemstack.count / 2 / 3 * 3;
                     int k = j / 3;
                     itemstack.count -= j;
                     itemstack1 = new ItemStack(Items.BREAD, k, 0);
                  }

                  if (itemstack.count <= 0) {
                     inventorysubcontainer.setItem(i, null);
                  }
               }

               if (itemstack1 != null) {
                  double d0 = this.f.locY - 0.3F + this.f.getHeadHeight();
                  EntityItem entityitem = new EntityItem(this.f.world, this.f.locX, d0, this.f.locZ, itemstack1);
                  float f = 0.3F;
                  float f1 = this.f.aK;
                  float f2 = this.f.pitch;
                  entityitem.motX = -MathHelper.sin(f1 / 180.0F * (float) Math.PI) * MathHelper.cos(f2 / 180.0F * (float) Math.PI) * f;
                  entityitem.motZ = MathHelper.cos(f1 / 180.0F * (float) Math.PI) * MathHelper.cos(f2 / 180.0F * (float) Math.PI) * f;
                  entityitem.motY = -MathHelper.sin(f2 / 180.0F * (float) Math.PI) * f + 0.1F;
                  entityitem.p();
                  this.f.world.addEntity(entityitem);
                  break;
               }
            }
         }
      }
   }
}
