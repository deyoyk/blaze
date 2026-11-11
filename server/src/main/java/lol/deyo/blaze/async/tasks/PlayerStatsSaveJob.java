package lol.deyo.blaze.async.tasks;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import net.minecraft.server.v1_8_R3.ServerStatisticManager;
import net.minecraft.server.v1_8_R3.Statistic;
import net.minecraft.server.v1_8_R3.StatisticWrapper;
import org.apache.commons.io.FileUtils;

public class PlayerStatsSaveJob implements Runnable {
   private final File target;
   private final Map<Statistic, StatisticWrapper> stats;

   public PlayerStatsSaveJob(File target, Map<Statistic, StatisticWrapper> stats) {
      this.target = target;
      this.stats = stats;
   }

   @Override
   public void run() {
      try {
         FileUtils.writeStringToFile(this.target, ServerStatisticManager.a(this.stats));
      } catch (IOException var2) {
         var2.printStackTrace();
      }
   }
}
