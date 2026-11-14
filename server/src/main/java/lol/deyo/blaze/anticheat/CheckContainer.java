package lol.deyo.blaze.anticheat;

import java.util.ArrayList;
import java.util.List;

public final class CheckContainer {
   private final List<Check> checks = new ArrayList<>();

   public void add(Check check) {
      this.checks.add(check);
   }

   public List<Check> all() {
      return this.checks;
   }

   public void onFlying(ACPlayer data, long nowNanos) {
      for (int i = 0; i < this.checks.size(); i++) {
         Check check = this.checks.get(i);
         if (check.enabled()) {
            check.onFlying(data, nowNanos);
         }
      }
   }

   public void onAttack(ACPlayer data, long nowNanos) {
      for (int i = 0; i < this.checks.size(); i++) {
         Check check = this.checks.get(i);
         if (check.enabled()) {
            check.onAttack(data, nowNanos);
         }
      }
   }

   public void onSwing(ACPlayer data, long nowNanos) {
      for (int i = 0; i < this.checks.size(); i++) {
         Check check = this.checks.get(i);
         if (check.enabled()) {
            check.onSwing(data, nowNanos);
         }
      }
   }

   public void onDig(ACPlayer data, long nowNanos) {
      for (int i = 0; i < this.checks.size(); i++) {
         Check check = this.checks.get(i);
         if (check.enabled()) {
            check.onDig(data, nowNanos);
         }
      }
   }

   public void onPlace(ACPlayer data, long nowNanos) {
      for (int i = 0; i < this.checks.size(); i++) {
         Check check = this.checks.get(i);
         if (check.enabled()) {
            check.onPlace(data, nowNanos);
         }
      }
   }

   public void tick(ACPlayer data) {
      for (int i = 0; i < this.checks.size(); i++) {
         Check check = this.checks.get(i);
         if (check.enabled()) {
            check.tick(data);
         }
      }
   }
}
