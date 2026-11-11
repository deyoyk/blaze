package lol.deyo.blaze.knockback;

import lol.deyo.blaze.Blaze;
import lol.deyo.blaze.api.knockback.IKnockback;
import lol.deyo.blaze.api.knockback.IKnockbackManager;
import lol.deyo.blaze.configs.BlazeConfig;
import lol.deyo.blaze.configs.extra.ConfigFolder;
import lol.deyo.blaze.knockback.extra.KnockbackEdit;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class KnockbackManager implements IKnockbackManager {
   private final Map<String, Knockback> profiles = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
   private final Map<UUID, KnockbackEdit> editing = new HashMap<>();
   private final ConfigFolder parent = new ConfigFolder("knockback");
   private Knockback activeProfile = null;

   public KnockbackManager() {
      this.load();
   }

   private void load() {
      File[] files = this.parent.listFiles();
      if (files != null) {
         for (File file : files) {
            Knockback knockback = (Knockback)this.createProfile(file.getName());
            if (BlazeConfig.globalActiveKnockback.getValue().equals(knockback.getName())) {
               this.activeProfile = knockback;
            }
         }
      }

      if (this.activeProfile == null) {
         BlazeConfig.globalActiveKnockback.set("Default", true);
         IKnockback defaultKnockback = this.getByName("Default");
         this.activeProfile = defaultKnockback != null ? (Knockback)defaultKnockback : (Knockback)this.createProfile("Default");
      }
   }

   public Map<UUID, KnockbackEdit> getEditing() {
      return this.editing;
   }

   @Override
   public List<IKnockback> getProfiles() {
      return new ArrayList<>(this.profiles.values());
   }

   @Override
   public IKnockback createProfile(String name) {
      Knockback knockback = new Knockback(Blaze.getInstance(), name, this.parent);
      knockback.load();
      knockback.save(true);
      this.profiles.put(knockback.getName(), knockback);
      return knockback;
   }

   @Override
   public IKnockback deleteProfile(String name) {
      Knockback knockback = this.profiles.get(name);
      if (knockback != null && !knockback.getName().equals(this.activeProfile.getName())) {
         knockback.delete();
         this.profiles.remove(name);
      }

      return knockback;
   }

   @Override
   public IKnockback getByName(String name) {
      return this.profiles.get(name);
   }

   @Override
   public IKnockback getActiveProfile() {
      return this.activeProfile;
   }

   @Override
   public void setActiveProfile(IKnockback knockback) {
      this.activeProfile = (Knockback)knockback;
      BlazeConfig.globalActiveKnockback.set(this.activeProfile.getName(), true);
   }

   @Override
   public void save(boolean async) {
      for (Knockback profile : this.profiles.values()) {
         profile.save(async);
      }
   }
}
