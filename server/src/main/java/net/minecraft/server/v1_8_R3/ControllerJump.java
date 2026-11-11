package net.minecraft.server.v1_8_R3;

public class ControllerJump {
   private final EntityInsentient b;
   protected boolean a;

   public ControllerJump(EntityInsentient var1) {
      this.b = var1;
   }

   public void a() {
      this.a = true;
   }

   public void b() {
      this.b.i(this.a);
      this.a = false;
   }
}
