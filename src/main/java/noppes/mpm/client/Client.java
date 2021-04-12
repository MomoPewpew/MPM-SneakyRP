package noppes.mpm.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import noppes.mpm.LogWriter;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;

public class Client {
  public static void sendData(EnumPackets enu, Object... obs) {
    PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
    try {
      if (!Server.fillBuffer((ByteBuf)buffer, (Enum)enu, obs))
        return;
      MorePlayerModels.Channel.sendToServer(new FMLProxyPacket(new PacketBuffer(buffer.copy()), "MorePlayerModels"));
    } catch (IOException e) {
      LogWriter.except(e);
    }
  }
}
