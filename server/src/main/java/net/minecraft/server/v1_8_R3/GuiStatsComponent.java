package net.minecraft.server.v1_8_R3;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import javax.swing.JComponent;
import javax.swing.Timer;

public class GuiStatsComponent extends JComponent {
   private static final DecimalFormat a = new DecimalFormat("########0.000");
   private int[] b = new int[256];
   private int c;
   private String[] d = new String[11];
   private final MinecraftServer e;

   public GuiStatsComponent(MinecraftServer var1) {
      this.e = var1;
      this.setPreferredSize(new Dimension(456, 246));
      this.setMinimumSize(new Dimension(456, 246));
      this.setMaximumSize(new Dimension(456, 246));
      new Timer(500, new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent var1) {
            GuiStatsComponent.this.a();
         }
      }).start();
      this.setBackground(Color.BLACK);
   }

   private void a() {
      long var1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
      System.gc();
      this.d[0] = "Memory use: " + var1 / 1024L / 1024L + " mb (" + Runtime.getRuntime().freeMemory() * 100L / Runtime.getRuntime().maxMemory() + "% free)";
      this.d[1] = "Avg tick: " + a.format(this.a(this.e.i[0]) * 1.0E-6) + " ms";
      this.repaint();
   }

   private double a(long[] var1) {
      long var2 = 0L;

      for (int var4 = 0; var4 < var1.length; var4++) {
         var2 += var1[var4];
      }

      return (double)var2 / var1.length;
   }

   @Override
   public void paint(Graphics var1) {
      var1.setColor(new Color(16777215));
      var1.fillRect(0, 0, 456, 246);

      for (int var2 = 0; var2 < 256; var2++) {
         int var3 = this.b[var2 + this.c & 0xFF];
         var1.setColor(new Color(var3 + 28 << 16));
         var1.fillRect(var2, 100 - var3, 1, var3);
      }

      var1.setColor(Color.BLACK);

      for (int var4 = 0; var4 < this.d.length; var4++) {
         String var5 = this.d[var4];
         if (var5 != null) {
            var1.drawString(var5, 32, 116 + var4 * 16);
         }
      }
   }
}
