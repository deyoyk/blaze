package net.minecraft.server.v1_8_R3;

public interface Convertable {
   IDataManager a(String var1, boolean var2);

   void d();

   boolean e(String var1);

   boolean isConvertable(String var1);

   boolean convert(String var1, IProgressUpdate var2);
}
