package lol.deyo.blaze.api.knockback;

import java.util.List;

public interface IKnockbackManager {
   List<IKnockback> getProfiles();

   IKnockback createProfile(String var1);

   IKnockback deleteProfile(String var1);

   IKnockback getByName(String var1);

   IKnockback getActiveProfile();

   void setActiveProfile(IKnockback var1);

   void save(boolean var1);
}
