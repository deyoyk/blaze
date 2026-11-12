package net.minecraft.server.v1_8_R3;

import static org.junit.Assert.assertEquals;

import io.netty.buffer.Unpooled;
import org.junit.Test;

public class PacketDataSerializerTest {
   @Test
   public void writesRawNbtInPackets() throws Exception {
      NBTTagCompound nbt = new NBTTagCompound();
      nbt.setString("name", "blaze");
      nbt.setInt("value", 42);

      PacketDataSerializer serializer = new PacketDataSerializer(Unpooled.buffer());
      serializer.a(nbt);
      assertEquals(10, serializer.getByte(0));

      NBTTagCompound decoded = serializer.h();
      assertEquals("blaze", decoded.getString("name"));
      assertEquals(42, decoded.getInt("value"));
   }
}
