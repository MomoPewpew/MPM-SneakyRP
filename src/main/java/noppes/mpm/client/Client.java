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
     public static void sendData(ByteBuf buf) {
          PacketBuffer buffer = new PacketBuffer(buf);

          try {
               MorePlayerModels.Channel.sendToServer(new FMLProxyPacket(new PacketBuffer(buffer), "MorePlayerModels"));
          } catch (Exception var4) {
               LogWriter.except(var4);
          }
     }
     public static void sendData(EnumPackets enu, Object... obs) {
          ByteBuf buffer = Unpooled.buffer();
          if (!Server.fillBuffer(buffer, enu, obs)) return;
          sendData(buffer);
     }
}
