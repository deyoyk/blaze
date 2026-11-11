package net.minecraft.server.v1_8_R3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EULA {
   private static final Logger a = LogManager.getLogger();
   private final File b;
   private final boolean c;

   public EULA(File var1) {
      this.b = var1;
      this.c = this.a(var1);
   }

   private boolean a(File var1) {
      FileInputStream var2 = null;
      boolean var3 = false;

      try {
         Properties var4 = new Properties();
         var2 = new FileInputStream(var1);
         var4.load(var2);
         var3 = Boolean.parseBoolean(var4.getProperty("eula", "false"));
      } catch (Exception var8) {
         a.warn("Failed to load " + var1);
         this.b();
      } finally {
         IOUtils.closeQuietly(var2);
      }

      return var3;
   }

   public boolean a() {
      return this.c;
   }

   public void b() {
      FileOutputStream var1 = null;

      try {
         Properties var2 = new Properties();
         var1 = new FileOutputStream(this.b);
         var2.setProperty("eula", "false");
         var2.store(
            var1, "By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula)."
         );
      } catch (Exception var6) {
         a.warn("Failed to save " + this.b, var6);
      } finally {
         IOUtils.closeQuietly(var1);
      }
   }
}
