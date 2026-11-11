package net.minecraft.server.v1_8_R3;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import javax.crypto.Cipher;

public class PacketDecrypter extends MessageToMessageDecoder<ByteBuf> {
   private final PacketEncryptionHandler a;

   public PacketDecrypter(Cipher var1) {
      this.a = new PacketEncryptionHandler(var1);
   }

   @Override
   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      var3.add(this.a.a(var1, var2));
   }
}
