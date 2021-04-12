package noppes.mpm;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.constants.EnumPackets;

public class PacketHandlerServer {
  @SubscribeEvent
  public void onPacketData(FMLNetworkEvent.ServerCustomPacketEvent event) {
    EntityPlayerMP player = ((NetHandlerPlayServer)event.getHandler()).field_147369_b;
    ByteBuf buf = event.getPacket().payload();
    player.func_184102_h().func_152344_a(() -> {
          EnumPackets type = null;
          try {
            type = EnumPackets.values()[buf.readInt()];
            handlePacket(buf, player, type);
          } catch (Exception e) {
            LogWriter.error("Error with EnumPackets." + type, e);
          }
        });
  }

  private void handlePacket(ByteBuf buffer, EntityPlayerMP player, EnumPackets type) throws Exception {
    if (type == EnumPackets.PING) {
      int version = buffer.readInt();
      if (version == MorePlayerModels.Version) {
        ModelData data = ModelData.get((EntityPlayer)player);
        data.readFromNBT(Server.readNBT(buffer));
        if (!player.field_70170_p.func_82736_K().func_82766_b("mpmAllowEntityModels"))
          data.entityClass = null;
        data.save();
        Server.sendAssociatedData((Entity)player, EnumPackets.SEND_PLAYER_DATA, new Object[] { player.func_110124_au(), data.writeToNBT() });
      }
      ItemStack back = (ItemStack)player.field_71071_by.field_70462_a.get(0);
      if (!back.func_190926_b())
        Server.sendAssociatedData((Entity)player, EnumPackets.BACK_ITEM_UPDATE, new Object[] { player.func_110124_au(), back.func_77955_b(new NBTTagCompound()) });
      Server.sendData(player, EnumPackets.PING, new Object[] { Integer.valueOf(MorePlayerModels.Version) });
    } else if (type == EnumPackets.UPDATE_PLAYER_DATA) {
      ModelData data = ModelData.get((EntityPlayer)player);
      data.readFromNBT(Server.readNBT(buffer));
      if (!player.field_70170_p.func_82736_K().func_82766_b("mpmAllowEntityModels"))
        data.entityClass = null;
      data.save();
      Server.sendAssociatedData((Entity)player, EnumPackets.SEND_PLAYER_DATA, new Object[] { player.func_110124_au(), data.writeToNBT() });
    } else if (type == EnumPackets.ANIMATION) {
      EnumAnimation animation = EnumAnimation.values()[buffer.readInt()];
      if (animation == EnumAnimation.SLEEPING_SOUTH) {
        float rotation = player.field_70177_z;
        while (rotation < 0.0F)
          rotation += 360.0F;
        while (rotation > 360.0F)
          rotation -= 360.0F;
        int rotate = (int)((rotation + 45.0F) / 90.0F);
        if (rotate == 1)
          animation = EnumAnimation.SLEEPING_WEST;
        if (rotate == 2)
          animation = EnumAnimation.SLEEPING_NORTH;
        if (rotate == 3)
          animation = EnumAnimation.SLEEPING_EAST;
      }
      ModelData data = ModelData.get((EntityPlayer)player);
      if (data.animationEquals(animation))
        animation = EnumAnimation.NONE;
      Server.sendAssociatedData((Entity)player, EnumPackets.ANIMATION, new Object[] { player.func_110124_au(), animation });
      data.setAnimation(animation);
    }
  }
}
