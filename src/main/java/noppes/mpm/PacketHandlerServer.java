package noppes.mpm;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.constants.EnumPackets;

public class PacketHandlerServer {
     @SubscribeEvent
     public void onPacketData(ServerCustomPacketEvent event) {
          EntityPlayerMP player = ((NetHandlerPlayServer)event.getHandler()).playerEntity;
          ByteBuf buf = event.getPacket().payload();
          player.getServer().addScheduledTask(() -> {
               EnumPackets type = null;

               try {
                    type = EnumPackets.values()[buf.readInt()];
                    this.handlePacket(buf, player, type);
               } catch (Exception var5) {
                    LogWriter.error("Error with EnumPackets." + type, var5);
               }

          });
     }

     private void handlePacket(ByteBuf buffer, EntityPlayerMP player, EnumPackets type) throws Exception {
          if (type == EnumPackets.PING) {
               int version = buffer.readInt();
               if (version == MorePlayerModels.Version) {
            	   ModelData data = ModelData.get(player);
                    data.readFromNBT(Server.readNBT(buffer));
                    if (!player.worldObj.getGameRules().getBoolean("mpmAllowEntityModels")) {
                         data.entityClass = null;
                    }

                    data.save();
                    Server.sendAssociatedData(player, EnumPackets.SEND_PLAYER_DATA, player.getUniqueID(), data.writeToNBT());
               }

               ItemStack back = (ItemStack)player.inventory.mainInventory.get(0);
               if (back != null) {
                    Server.sendAssociatedData(player, EnumPackets.BACK_ITEM_UPDATE, player.getUniqueID(), back.writeToNBT(new NBTTagCompound()));
               }

               Server.sendAssociatedData(player, EnumPackets.PROP_ITEM_CLEAR, player.getUniqueID());
               for (int i = 0; i < ((ModelData.get(player)).propItemStack.size()); i++) {
            	   ItemStack propItemStack = ((ModelData.get(player)).propItemStack.get(i));
            	   Server.sendAssociatedData(player, EnumPackets.PROP_ITEM_UPDATE, player.getUniqueID(), propItemStack.writeToNBT(new NBTTagCompound()));
               }

               Server.sendData(player, EnumPackets.PING, MorePlayerModels.Version);
          } else if (type == EnumPackets.UPDATE_PLAYER_DATA) {
               ModelData data = ModelData.get(player);
               data.readFromNBT(Server.readNBT(buffer));
               if (!player.worldObj.getGameRules().getBoolean("mpmAllowEntityModels")) {
                    data.entityClass = null;
               }

               data.save();
               Server.sendAssociatedData(player, EnumPackets.SEND_PLAYER_DATA, player.getUniqueID(), data.writeToNBT());
          } else if (type == EnumPackets.ANIMATION) {
               EnumAnimation animation = EnumAnimation.values()[buffer.readInt()];
               if (animation == EnumAnimation.SLEEPING_SOUTH) {
                    float rotation;
                    for(rotation = player.rotationYaw; rotation < 0.0F; rotation += 360.0F) {
                    }

                    while(rotation > 360.0F) {
                         rotation -= 360.0F;
                    }

                    int rotate = (int)((rotation + 45.0F) / 90.0F);
                    if (rotate == 1) {
                         animation = EnumAnimation.SLEEPING_WEST;
                    }

                    if (rotate == 2) {
                         animation = EnumAnimation.SLEEPING_NORTH;
                    }

                    if (rotate == 3) {
                         animation = EnumAnimation.SLEEPING_EAST;
                    }
               }

               ModelData data = ModelData.get(player);
               if (data.animationEquals(animation)) {
                    animation = EnumAnimation.NONE;
               }

               Server.sendAssociatedData(player, EnumPackets.ANIMATION, player.getUniqueID(), animation);
               data.setAnimation(animation);
          }

     }
}
