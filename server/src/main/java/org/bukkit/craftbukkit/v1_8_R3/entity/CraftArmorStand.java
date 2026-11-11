package org.bukkit.craftbukkit.v1_8_R3.entity;

import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.Vector3f;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public class CraftArmorStand extends CraftLivingEntity implements ArmorStand {
   public CraftArmorStand(CraftServer server, EntityArmorStand entity) {
      super(server, entity);
   }

   @Override
   public String toString() {
      return "CraftArmorStand";
   }

   @Override
   public EntityType getType() {
      return EntityType.ARMOR_STAND;
   }

   public EntityArmorStand getHandle() {
      return (EntityArmorStand)super.getHandle();
   }

   @Override
   public ItemStack getItemInHand() {
      return this.getEquipment().getItemInHand();
   }

   @Override
   public void setItemInHand(ItemStack item) {
      this.getEquipment().setItemInHand(item);
   }

   @Override
   public ItemStack getBoots() {
      return this.getEquipment().getBoots();
   }

   @Override
   public void setBoots(ItemStack item) {
      this.getEquipment().setBoots(item);
   }

   @Override
   public ItemStack getLeggings() {
      return this.getEquipment().getLeggings();
   }

   @Override
   public void setLeggings(ItemStack item) {
      this.getEquipment().setLeggings(item);
   }

   @Override
   public ItemStack getChestplate() {
      return this.getEquipment().getChestplate();
   }

   @Override
   public void setChestplate(ItemStack item) {
      this.getEquipment().setChestplate(item);
   }

   @Override
   public ItemStack getHelmet() {
      return this.getEquipment().getHelmet();
   }

   @Override
   public void setHelmet(ItemStack item) {
      this.getEquipment().setHelmet(item);
   }

   @Override
   public EulerAngle getBodyPose() {
      return fromNMS(this.getHandle().bodyPose);
   }

   @Override
   public void setBodyPose(EulerAngle pose) {
      this.getHandle().setBodyPose(toNMS(pose));
   }

   @Override
   public EulerAngle getLeftArmPose() {
      return fromNMS(this.getHandle().leftArmPose);
   }

   @Override
   public void setLeftArmPose(EulerAngle pose) {
      this.getHandle().setLeftArmPose(toNMS(pose));
   }

   @Override
   public EulerAngle getRightArmPose() {
      return fromNMS(this.getHandle().rightArmPose);
   }

   @Override
   public void setRightArmPose(EulerAngle pose) {
      this.getHandle().setRightArmPose(toNMS(pose));
   }

   @Override
   public EulerAngle getLeftLegPose() {
      return fromNMS(this.getHandle().leftLegPose);
   }

   @Override
   public void setLeftLegPose(EulerAngle pose) {
      this.getHandle().setLeftLegPose(toNMS(pose));
   }

   @Override
   public EulerAngle getRightLegPose() {
      return fromNMS(this.getHandle().rightLegPose);
   }

   @Override
   public void setRightLegPose(EulerAngle pose) {
      this.getHandle().setRightLegPose(toNMS(pose));
   }

   @Override
   public EulerAngle getHeadPose() {
      return fromNMS(this.getHandle().headPose);
   }

   @Override
   public void setHeadPose(EulerAngle pose) {
      this.getHandle().setHeadPose(toNMS(pose));
   }

   @Override
   public boolean hasBasePlate() {
      return !this.getHandle().hasBasePlate();
   }

   @Override
   public void setBasePlate(boolean basePlate) {
      this.getHandle().setBasePlate(!basePlate);
   }

   @Override
   public boolean hasGravity() {
      return !this.getHandle().hasGravity();
   }

   @Override
   public void setGravity(boolean gravity) {
      this.getHandle().setGravity(!gravity);
   }

   @Override
   public boolean isVisible() {
      return !this.getHandle().isInvisible();
   }

   @Override
   public void setVisible(boolean visible) {
      this.getHandle().setInvisible(!visible);
   }

   @Override
   public boolean hasArms() {
      return this.getHandle().hasArms();
   }

   @Override
   public void setArms(boolean arms) {
      this.getHandle().setArms(arms);
   }

   @Override
   public boolean isSmall() {
      return this.getHandle().isSmall();
   }

   @Override
   public void setSmall(boolean small) {
      this.getHandle().setSmall(small);
   }

   private static EulerAngle fromNMS(Vector3f old) {
      return new EulerAngle(Math.toRadians(old.getX()), Math.toRadians(old.getY()), Math.toRadians(old.getZ()));
   }

   private static Vector3f toNMS(EulerAngle old) {
      return new Vector3f((float)Math.toDegrees(old.getX()), (float)Math.toDegrees(old.getY()), (float)Math.toDegrees(old.getZ()));
   }

   @Override
   public boolean isMarker() {
      return this.getHandle().s();
   }

   @Override
   public void setMarker(boolean marker) {
      this.getHandle().n(marker);
   }
}
