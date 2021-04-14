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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
               if (!fillBuffer(buffer, enu, obs)) {
                    return false;
               }

               MorePlayerModels.Channel.sendTo(new FMLProxyPacket(buffer, "MorePlayerModels"), player);
          } catch (IOException var5) {
               LogWriter.except(var5);
          }

          return true;
     }

     public static void sendDelayedData(EntityPlayerMP player, EnumPackets enu, int delay, Object... obs) {
          PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());

          try {
               if (!fillBuffer(buffer, enu, obs)) {
                    return;
               }

               MPMScheduler.runTack(() -> {
                    MorePlayerModels.Channel.sendTo(new FMLProxyPacket(buffer, "MorePlayerModels"), player);
               }, delay);
          } catch (IOException var6) {
               LogWriter.except(var6);
          }

     }

     public static void sendAssociatedData(Entity entity, EnumPackets enu, Object... obs) {
          List list = entity.world.getEntitiesWithinAABB(EntityPlayerMP.class, entity.getEntityBoundingBox().grow(160.0D, 160.0D, 160.0D));
          if (!list.isEmpty()) {
               MPMScheduler.runTack(() -> {
                    try {
                         ByteBuf buffer = Unpooled.buffer();
                         if (!fillBuffer(buffer, enu, obs)) {
                              return;
                         }

                         Iterator var4 = list.iterator();

                         while(var4.hasNext()) {
                              EntityPlayerMP player = (EntityPlayerMP)var4.next();
                              MorePlayerModels.Channel.sendTo(new FMLProxyPacket(new PacketBuffer(buffer.copy()), "MorePlayerModels"), player);
                         }
                    } catch (IOException var6) {
                         LogWriter.except(var6);
                    }

               });
          }
     }

     public static void sendToAll(MinecraftServer server, EnumPackets enu, Object... obs) {
          List list = new ArrayList(server.getPlayerList().getPlayers());
          if (!list.isEmpty()) {
               MPMScheduler.runTack(() -> {
                    ByteBuf buffer = Unpooled.buffer();

                    try {
                         if (!fillBuffer(buffer, enu, obs)) {
                              return;
                         }

                         Iterator var4 = list.iterator();

                         while(var4.hasNext()) {
                              EntityPlayerMP player = (EntityPlayerMP)var4.next();
                              MorePlayerModels.Channel.sendTo(new FMLProxyPacket(new PacketBuffer(buffer.copy()), "MorePlayerModels"), player);
                         }
                    } catch (IOException var6) {
                         LogWriter.except(var6);
                    }

               });
          }
     }

     public static boolean fillBuffer(ByteBuf buffer, Enum enu, Object... obs) throws IOException {
          buffer.writeInt(enu.ordinal());
          Object[] var3 = obs;
          int var4 = obs.length;

          for(int var5 = 0; var5 < var4; ++var5) {
               Object ob = var3[var5];
               if (ob != null) {
                    Iterator var8;
                    String s;
                    if (ob instanceof Map) {
                         Map map = (Map)ob;
                         buffer.writeInt(map.size());
                         var8 = map.keySet().iterator();

                         while(var8.hasNext()) {
                              s = (String)var8.next();
                              int value = (Integer)map.get(s);
                              buffer.writeInt(value);
                              writeString(buffer, s);
                         }
                    } else if (ob instanceof MerchantRecipeList) {
                         ((MerchantRecipeList)ob).writeToBuf(new PacketBuffer(buffer));
                    } else if (ob instanceof List) {
                         List list = (List)ob;
                         buffer.writeInt(list.size());
                         var8 = list.iterator();

                         while(var8.hasNext()) {
                              s = (String)var8.next();
                              writeString(buffer, s);
                         }
                    } else if (ob instanceof UUID) {
                         writeString(buffer, ob.toString());
                    } else if (ob instanceof Enum) {
                         buffer.writeInt(((Enum)ob).ordinal());
                    } else if (ob instanceof Integer) {
                         buffer.writeInt((Integer)ob);
                    } else if (ob instanceof Boolean) {
                         buffer.writeBoolean((Boolean)ob);
                    } else if (ob instanceof String) {
                         writeString(buffer, (String)ob);
                    } else if (ob instanceof Float) {
                         buffer.writeFloat((Float)ob);
                    } else if (ob instanceof Long) {
                         buffer.writeLong((Long)ob);
                    } else if (ob instanceof Double) {
                         buffer.writeDouble((Double)ob);
                    } else if (ob instanceof NBTTagCompound) {
                         writeNBT(buffer, (NBTTagCompound)ob);
                    }
               }
          }

          return true;
     }

     public static void writeNBT(ByteBuf buffer, NBTTagCompound compound) throws IOException {
          ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
          DataOutputStream dataoutputstream = new DataOutputStream(new GZIPOutputStream(bytearrayoutputstream));

          try {
               CompressedStreamTools.write(compound, dataoutputstream);
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

          NBTTagCompound var3;
          try {
               var3 = CompressedStreamTools.read(datainputstream, new NBTSizeTracker(2097152L));
          } finally {
               datainputstream.close();
          }

          return var3;
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
          } catch (IndexOutOfBoundsException var2) {
               return null;
          }
     }
}
