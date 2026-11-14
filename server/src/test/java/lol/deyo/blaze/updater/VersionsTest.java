package lol.deyo.blaze.updater;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VersionsTest {
   @Test
   public void parsesSequentialTags() {
      assertEquals(1, Versions.parse("v1"));
      assertEquals(2, Versions.parse("v2"));
      assertEquals(42, Versions.parse(" v42 "));
      assertEquals(10, Versions.parse("V10"));
   }

   @Test
   public void rejectsNonReleaseVersions() {
      assertEquals(-1, Versions.parse(null));
      assertEquals(-1, Versions.parse(""));
      assertEquals(-1, Versions.parse("1"));
      assertEquals(-1, Versions.parse("v1.2"));
      assertEquals(-1, Versions.parse("v1-beta"));
      assertEquals(-1, Versions.parse("1.8.8-R0.1-SNAPSHOT"));
   }
}
