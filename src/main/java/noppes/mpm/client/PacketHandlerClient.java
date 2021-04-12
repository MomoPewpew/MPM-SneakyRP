package noppes.mpm.client;

import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import noppes.mpm.LogWriter;
import noppes.mpm.ModelData;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.PacketHandlerServer;
import noppes.mpm.Server;
import noppes.mpm.client.gui.GuiCreationScreenInterface;
import noppes.mpm.constants.EnumPackets;

public class PacketHandlerClient extends PacketHandlerServer {
  @SubscribeEvent
  public void onPacketData(FMLNetworkEvent.ClientCustomPacketEvent event) {
    EntityPlayerSP entityPlayerSP = (Minecraft.getMinecraft()).thePlayer;
    ByteBuf buf = event.getPacket().payload();
    Minecraft.getMinecraft().addScheduledTask(() -> {
          EnumPackets en = null;
          try {
            en = EnumPackets.values()[buf.readInt()];
            handlePacket(buf, player, en);
          } catch (Exception e) {
            LogWriter.error("Packet error: " + en, e);
          }
        });
  }

  private void handlePacket(ByteBuf buffer, EntityPlayer player, EnumPackets type) throws Exception {
    if (type == EnumPackets.PING) {
      int version = buffer.readInt();
      if (version == MorePlayerModels.Version) {
        MorePlayerModels.HasServerSide = true;
        GuiCreationScreenInterface.Message = "";
      } else if (version < MorePlayerModels.Version) {
        MorePlayerModels.HasServerSide = false;
        GuiCreationScreenInterface.Message = "message.lowerversion";
      } else if (version > MorePlayerModels.Version) {
        MorePlayerModels.HasServerSide = false;
        GuiCreationScreenInterface.Message = "message.higherversion";
      }
    } else if (type == EnumPackets.EYE_BLINK) {
      EntityPlayer pl = player.worldObj.func_152378_a(UUID.fromString(Server.readString(buffer)));
      if (pl == null)
        return;
      ModelData data = ModelData.get(pl);
      data.eyes.blinkStart = System.currentTimeMillis();
    } else if (type == EnumPackets.SEND_PLAYER_DATA) {
      EntityPlayer pl = player.worldObj.func_152378_a(UUID.fromString(Server.readString(buffer)));
      if (pl == null)
        return;
      ModelData data = ModelData.get(pl);
      NBTTagCompound compound = Server.readNBT(buffer);
      data.readFromNBT(compound);
      data.save();
      if (pl == (Minecraft.getMinecraft()).thePlayer)
        data.lastEdited = System.currentTimeMillis();
    } else if (type == EnumPackets.CHAT_EVENT) {
      EntityPlayer pl = player.worldObj.func_152378_a(UUID.fromString(Server.readString(buffer)));
      if (pl == null)
        return;
      String message = Server.readString(buffer);
      ChatMessages.getChatMessages(pl.func_70005_c_()).addMessage(message);
    } else if (type == EnumPackets.BACK_ITEM_REMOVE) {
      EntityPlayer pl = player.worldObj.func_152378_a(UUID.fromString(Server.readString(buffer)));
      if (pl == null)
        return;
      ModelData data = ModelData.get(pl);
      data.backItem = ItemStack.field_190927_a;
    } else if (type == EnumPackets.BACK_ITEM_UPDATE) {
      EntityPlayer pl = player.worldObj.func_152378_a(UUID.fromString(Server.readString(buffer)));
      if (pl == null)
        return;
      NBTTagCompound compound = Server.readNBT(buffer);
      ItemStack item = new ItemStack(compound);
      ModelData data = ModelData.get(pl);
      data.backItem = item;
    } else if (type == EnumPackets.PARTICLE) {
      int animation = buffer.readInt();
      if (animation == 0) {
        EntityPlayer pl = player.worldObj.func_152378_a(UUID.fromString(Server.readString(buffer)));
        if (pl == null)
          return;
        ModelData data = ModelData.get(pl);
        data.inLove = 40;
      } else if (animation == 1) {
        player.worldObj.func_175688_a(EnumParticleTypes.NOTE, buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), 0.0D, 0.0D, new int[0]);
      } else if (animation == 2) {
        EntityPlayer pl = player.worldObj.func_152378_a(UUID.fromString(Server.readString(buffer)));
        if (pl == null)
          return;
        ModelData data = ModelData.get(pl);
        for (int i = 0; i < 5; i++) {
          double d0 = player.getRNG().nextGaussian() * 0.02D;
          double d1 = player.getRNG().nextGaussian() * 0.02D;
          double d2 = player.getRNG().nextGaussian() * 0.02D;
          double x = player.posX + ((player.getRNG().nextFloat() - 0.5F) * player.field_70130_N * 2.0F);
          double z = player.posZ + ((player.getRNG().nextFloat() - 0.5F) * player.field_70130_N * 2.0F);
          player.worldObj.func_175688_a(EnumParticleTypes.VILLAGER_ANGRY, x, player.posY + 0.800000011920929D + (player.getRNG().nextFloat() * player.height / 2.0F) - player.func_70033_W() - data.getBodyY(), z, d0, d1, d2, new int[0]);
        }
      }
    } else if (type == EnumPackets.ANIMATION) {
      EntityPlayer pl = player.worldObj.func_152378_a(UUID.fromString(Server.readString(buffer)));
      if (pl == null)
        return;
      ModelData data = ModelData.get(pl);
      data.setAnimation(buffer.readInt());
      data.animationStart = pl.ticksExisted;
    }
  }
}
