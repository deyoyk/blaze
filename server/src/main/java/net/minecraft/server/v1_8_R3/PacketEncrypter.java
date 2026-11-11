package net.minecraft.server.v1_8_R3;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import javax.crypto.Cipher;

public class PacketEncrypter extends MessageToByteEncoder<ByteBuf> {
   private final PacketEncryptionHandler a;

   public PacketEncrypter(Cipher var1) {
      this.a = new PacketEncryptionHandler(var1);
   }

   @Override
   protected void encode(ChannelHandlerContext var1, ByteBuf var2, ByteBuf var3) throws Exception {
      this.a.a(var2, var3);
   }
}
