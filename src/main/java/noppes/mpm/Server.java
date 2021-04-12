package noppes.mpm;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import noppes.mpm.constants.EnumPackets;
import noppes.mpm.util.MPMScheduler;

public class Server {
  public static boolean sendData(EntityPlayerMP player, EnumPackets enu, Object... obs) {
    PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
    try {
      if (!fillBuffer((ByteBuf)buffer, (Enum)enu, obs))
        return false;
      MorePlayerModels.Channel.sendTo(new FMLProxyPacket(buffer, "MorePlayerModels"), player);
    } catch (IOException e) {
      LogWriter.except(e);
    }
    return true;
  }

  public static void sendDelayedData(EntityPlayerMP player, EnumPackets enu, int delay, Object... obs) {
    PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
    try {
      if (!fillBuffer((ByteBuf)buffer, (Enum)enu, obs))
        return;
      MPMScheduler.runTack(() -> MorePlayerModels.Channel.sendTo(new FMLProxyPacket(buffer, "MorePlayerModels"), player), delay);
    } catch (IOException e) {
      LogWriter.except(e);
    }
  }

  public static void sendAssociatedData(Entity entity, EnumPackets enu, Object... obs) {
    List<EntityPlayerMP> list = entity.field_70170_p.func_72872_a(EntityPlayerMP.class, entity.func_174813_aQ().func_72314_b(160.0D, 160.0D, 160.0D));
    if (list.isEmpty())
      return;
    MPMScheduler.runTack(() -> {
          try {
            ByteBuf buffer = Unpooled.buffer();
            if (!fillBuffer(buffer, (Enum)enu, obs))
              return;
            for (EntityPlayerMP player : list)
              MorePlayerModels.Channel.sendTo(new FMLProxyPacket(new PacketBuffer(buffer.copy()), "MorePlayerModels"), player);
          } catch (IOException e) {
            LogWriter.except(e);
          }
        });
  }

  public static void sendToAll(MinecraftServer server, EnumPackets enu, Object... obs) {
    List<EntityPlayerMP> list = new ArrayList<>(server.func_184103_al().func_181057_v());
    if (list.isEmpty())
      return;
    MPMScheduler.runTack(() -> {
          ByteBuf buffer = Unpooled.buffer();
          try {
            if (!fillBuffer(buffer, (Enum)enu, obs))
              return;
            for (EntityPlayerMP player : list)
              MorePlayerModels.Channel.sendTo(new FMLProxyPacket(new PacketBuffer(buffer.copy()), "MorePlayerModels"), player);
          } catch (IOException e) {
            LogWriter.except(e);
          }
        });
  }

  public static boolean fillBuffer(ByteBuf buffer, Enum enu, Object... obs) throws IOException {
    buffer.writeInt(enu.ordinal());
    for (Object ob : obs) {
      if (ob != null)
        if (ob instanceof Map) {
          Map<String, Integer> map = (Map<String, Integer>)ob;
          buffer.writeInt(map.size());
          for (String key : map.keySet()) {
            int value = ((Integer)map.get(key)).intValue();
            buffer.writeInt(value);
            writeString(buffer, key);
          }
        } else if (ob instanceof MerchantRecipeList) {
          ((MerchantRecipeList)ob).func_151391_a(new PacketBuffer(buffer));
        } else if (ob instanceof List) {
          List<String> list = (List<String>)ob;
          buffer.writeInt(list.size());
          for (String s : list)
            writeString(buffer, s);
        } else if (ob instanceof java.util.UUID) {
          writeString(buffer, ob.toString());
        } else if (ob instanceof Enum) {
          buffer.writeInt(((Enum)ob).ordinal());
        } else if (ob instanceof Integer) {
          buffer.writeInt(((Integer)ob).intValue());
        } else if (ob instanceof Boolean) {
          buffer.writeBoolean(((Boolean)ob).booleanValue());
        } else if (ob instanceof String) {
          writeString(buffer, (String)ob);
        } else if (ob instanceof Float) {
          buffer.writeFloat(((Float)ob).floatValue());
        } else if (ob instanceof Long) {
          buffer.writeLong(((Long)ob).longValue());
        } else if (ob instanceof Double) {
          buffer.writeDouble(((Double)ob).doubleValue());
        } else if (ob instanceof NBTTagCompound) {
          writeNBT(buffer, (NBTTagCompound)ob);
        }
    }
    return true;
  }

  public static void writeNBT(ByteBuf buffer, NBTTagCompound compound) throws IOException {
    ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
    DataOutputStream dataoutputstream = new DataOutputStream(new GZIPOutputStream(bytearrayoutputstream));
    try {
      CompressedStreamTools.func_74800_a(compound, dataoutputstream);
    } finally {
      dataoutputstream.close();
    }
    byte[] bytes = bytearrayoutputstream.toByteArray();
    buffer.writeShort((short)bytes.length);
    buffer.writeBytes(bytes);
  }

  public static NBTTagCompound readNBT(ByteBuf buffer) throws IOException {
    byte[] bytes = new byte[buffer.readShort()];
    buffer.readBytes(bytes);
    DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(bytes))));
    try {
      return CompressedStreamTools.func_152456_a(datainputstream, new NBTSizeTracker(2097152L));
    } finally {
      datainputstream.close();
    }
  }

  public static void writeString(ByteBuf buffer, String s) {
    byte[] bytes = s.getBytes(Charsets.UTF_8);
    buffer.writeShort((short)bytes.length);
    buffer.writeBytes(bytes);
  }

  public static String readString(ByteBuf buffer) {
    try {
      byte[] bytes = new byte[buffer.readShort()];
      buffer.readBytes(bytes);
      return new String(bytes, Charsets.UTF_8);
    } catch (IndexOutOfBoundsException ex) {
      return null;
    }
  }
}
