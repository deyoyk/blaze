package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.entity.SheepRegrowWoolEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.InventoryView;

public class EntitySheep extends EntityAnimal {
   private final InventoryCrafting bm = new InventoryCrafting(new Container() {
      @Override
      public boolean a(EntityHuman entityhuman) {
         return false;
      }

      @Override
      public InventoryView getBukkitView() {
         return null;
      }
   }, 2, 1);
   private static final Map<EnumColor, float[]> bo = Maps.newEnumMap(EnumColor.class);

   static {
      bo.put(EnumColor.WHITE, new float[]{1.0F, 1.0F, 1.0F});
      bo.put(EnumColor.ORANGE, new float[]{0.85F, 0.5F, 0.2F});
      bo.put(EnumColor.MAGENTA, new float[]{0.7F, 0.3F, 0.85F});
      bo.put(EnumColor.LIGHT_BLUE, new float[]{0.4F, 0.6F, 0.85F});
      bo.put(EnumColor.YELLOW, new float[]{0.9F, 0.9F, 0.2F});
      bo.put(EnumColor.LIME, new float[]{0.5F, 0.8F, 0.1F});
      bo.put(EnumColor.PINK, new float[]{0.95F, 0.5F, 0.65F});
      bo.put(EnumColor.GRAY, new float[]{0.3F, 0.3F, 0.3F});
      bo.put(EnumColor.SILVER, new float[]{0.6F, 0.6F, 0.6F});
      bo.put(EnumColor.CYAN, new float[]{0.3F, 0.5F, 0.6F});
      bo.put(EnumColor.PURPLE, new float[]{0.5F, 0.25F, 0.7F});
      bo.put(EnumColor.BLUE, new float[]{0.2F, 0.3F, 0.7F});
      bo.put(EnumColor.BROWN, new float[]{0.4F, 0.3F, 0.2F});
      bo.put(EnumColor.GREEN, new float[]{0.4F, 0.5F, 0.2F});
      bo.put(EnumColor.RED, new float[]{0.6F, 0.2F, 0.2F});
      bo.put(EnumColor.BLACK, new float[]{0.1F, 0.1F, 0.1F});
   }

   public static float[] a(EnumColor enumcolor) {
      return bo.get(enumcolor);
   }

   public EntitySheep(World world) {
      super(world);
      this.setSize(0.9F, 1.3F);
      ((Navigation)this.getNavigation()).a(true);
      this.goalSelector.a(0, new PathfinderGoalFloat(this));
      this.goalSelector.a(1, new PathfinderGoalPanic(this, 1.25));
      this.goalSelector.a(2, new PathfinderGoalBreed(this, 1.0));
      this.goalSelector.a(3, new PathfinderGoalTempt(this, 1.1, Items.WHEAT, false));
      this.goalSelector.a(4, new PathfinderGoalFollowParent(this, 1.1));
      this.goalSelector.a(5, new PathfinderGoalEatTile(this));
      this.goalSelector.a(6, new PathfinderGoalRandomStroll(this, 1.0));
      this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
      this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
      this.bm.setItem(0, new ItemStack(Items.DYE, 1, 0));
      this.bm.setItem(1, new ItemStack(Items.DYE, 1, 0));
      this.bm.resultInventory = new InventoryCraftResult();
   }

   @Override
   protected void E() {
      super.E();
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.getAttributeInstance(GenericAttributes.maxHealth).setValue(8.0);
      this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.23F);
   }

   @Override
   protected void h() {
      super.h();
      this.datawatcher.a(16, (byte)0);
   }

   @Override
   protected void dropDeathLoot(boolean flag, int i) {
      if (!this.isSheared()) {
         this.a(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, this.getColor().getColorIndex()), 0.0F);
      }

      int j = this.random.nextInt(2) + 1 + this.random.nextInt(1 + i);

      for (int k = 0; k < j; k++) {
         if (this.isBurning()) {
            this.a(Items.COOKED_MUTTON, 1);
         } else {
            this.a(Items.MUTTON, 1);
         }
      }
   }

   @Override
   protected Item getLoot() {
      return Item.getItemOf(Blocks.WOOL);
   }

   @Override
   public boolean a(EntityHuman entityhuman) {
      ItemStack itemstack = entityhuman.inventory.getItemInHand();
      if (itemstack != null && itemstack.getItem() == Items.SHEARS && !this.isSheared() && !this.isBaby()) {
         PlayerShearEntityEvent event = new PlayerShearEntityEvent((Player)entityhuman.getBukkitEntity(), this.getBukkitEntity());
         this.world.getServer().getPluginManager().callEvent(event);
         if (event.isCancelled()) {
            return false;
         }

         this.setSheared(true);
         int i = 1 + this.random.nextInt(3);

         for (int j = 0; j < i; j++) {
            EntityItem entityitem = this.a(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, this.getColor().getColorIndex()), 1.0F);
            entityitem.motY = entityitem.motY + this.random.nextFloat() * 0.05F;
            entityitem.motX = entityitem.motX + (this.random.nextFloat() - this.random.nextFloat()) * 0.1F;
            entityitem.motZ = entityitem.motZ + (this.random.nextFloat() - this.random.nextFloat()) * 0.1F;
         }

         itemstack.damage(1, entityhuman);
         this.makeSound("mob.sheep.shear", 1.0F, 1.0F);
      }

      return super.a(entityhuman);
   }

   @Override
   public void b(NBTTagCompound nbttagcompound) {
      super.b(nbttagcompound);
      nbttagcompound.setBoolean("Sheared", this.isSheared());
      nbttagcompound.setByte("Color", (byte)this.getColor().getColorIndex());
   }

   @Override
   public void a(NBTTagCompound nbttagcompound) {
      super.a(nbttagcompound);
      this.setSheared(nbttagcompound.getBoolean("Sheared"));
      this.setColor(EnumColor.fromColorIndex(nbttagcompound.getByte("Color")));
   }

   @Override
   protected String z() {
      return "mob.sheep.say";
   }

   @Override
   protected String bo() {
      return "mob.sheep.say";
   }

   @Override
   protected String bp() {
      return "mob.sheep.say";
   }

   @Override
   protected void a(BlockPosition blockposition, Block block) {
      this.makeSound("mob.sheep.step", 0.15F, 1.0F);
   }

   public EnumColor getColor() {
      return EnumColor.fromColorIndex(this.datawatcher.getByte(16) & 15);
   }

   public void setColor(EnumColor enumcolor) {
      byte b0 = this.datawatcher.getByte(16);
      this.datawatcher.watch(16, (byte)(b0 & 240 | enumcolor.getColorIndex() & 15));
   }

   public boolean isSheared() {
      return (this.datawatcher.getByte(16) & 16) != 0;
   }

   public void setSheared(boolean flag) {
      byte b0 = this.datawatcher.getByte(16);
      if (flag) {
         this.datawatcher.watch(16, (byte)(b0 | 16));
      } else {
         this.datawatcher.watch(16, (byte)(b0 & -17));
      }
   }

   public static EnumColor a(Random random) {
      int i = random.nextInt(100);
      return i < 5
         ? EnumColor.BLACK
         : (i < 10 ? EnumColor.GRAY : (i < 15 ? EnumColor.SILVER : (i < 18 ? EnumColor.BROWN : (random.nextInt(500) == 0 ? EnumColor.PINK : EnumColor.WHITE))));
   }

   public EntitySheep b(EntityAgeable entityageable) {
      EntitySheep entitysheep1 = new EntitySheep(this.world);
      entitysheep1.setColor(this.a(this, (EntitySheep)entityageable));
      return entitysheep1;
   }

   @Override
   public void v() {
      SheepRegrowWoolEvent event = new SheepRegrowWoolEvent((Sheep)this.getBukkitEntity());
      this.world.getServer().getPluginManager().callEvent(event);
      if (!event.isCancelled()) {
         this.setSheared(false);
      }

      if (this.isBaby()) {
         this.setAge(60);
      }
   }

   @Override
   public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, GroupDataEntity groupdataentity) {
      groupdataentity = super.prepare(difficultydamagescaler, groupdataentity);
      this.setColor(a(this.world.random));
      return groupdataentity;
   }

   private EnumColor a(EntityAnimal entityanimal, EntityAnimal entityanimal1) {
      int i = ((EntitySheep)entityanimal).getColor().getInvColorIndex();
      int j = ((EntitySheep)entityanimal1).getColor().getInvColorIndex();
      this.bm.getItem(0).setData(i);
      this.bm.getItem(1).setData(j);
      ItemStack itemstack = CraftingManager.getInstance().craft(this.bm, entityanimal.world);
      int k;
      if (itemstack != null && itemstack.getItem() == Items.DYE) {
         k = itemstack.getData();
      } else {
         k = this.world.random.nextBoolean() ? i : j;
      }

      return EnumColor.fromInvColorIndex(k);
   }

   @Override
   public float getHeadHeight() {
      return 0.95F * this.length;
   }

   @Override
   public EntityAgeable createChild(EntityAgeable entityageable) {
      return this.b(entityageable);
   }
}
