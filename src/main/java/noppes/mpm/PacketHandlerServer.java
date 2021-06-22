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
         } else if (type == EnumPackets.PROP_CLEAR) {
             ModelData data = ModelData.get(player);

             data.clearPropsLocal();

              Server.sendAssociatedData(player, EnumPackets.PROP_CLEAR, player.getUniqueID());
         } else if (type == EnumPackets.PROP_ITEM_UPDATE) {
             ModelData data = ModelData.get(player);

             ItemStack propItemStack = new ItemStack(Server.readNBT(buffer));
             data.propItemStack.add(propItemStack);

      	     Server.sendAssociatedData(player, EnumPackets.PROP_ITEM_UPDATE, player.getUniqueID(), propItemStack.writeToNBT(new NBTTagCompound()));
	     } else if (type == EnumPackets.PROP_PART_UPDATE) {
             ModelData data = ModelData.get(player);

             String propBodyPartName = Server.readString(buffer);
             data.propBodyPartName.add(propBodyPartName);

	 	     Server.sendAssociatedData(player, EnumPackets.PROP_PART_UPDATE, player.getUniqueID(), propBodyPartName);
	     } else if (type == EnumPackets.PROP_SCALEX_UPDATE) {
             ModelData data = ModelData.get(player);

             Float partFloat = buffer.readFloat();
             data.propScaleX.add(partFloat);

	 	     Server.sendAssociatedData(player, EnumPackets.PROP_SCALEX_UPDATE, player.getUniqueID(), partFloat);
	     } else if (type == EnumPackets.PROP_SCALEY_UPDATE) {
             ModelData data = ModelData.get(player);

             Float partFloat = buffer.readFloat();
             data.propScaleY.add(partFloat);

	 	     Server.sendAssociatedData(player, EnumPackets.PROP_SCALEY_UPDATE, player.getUniqueID(), partFloat);
	     } else if (type == EnumPackets.PROP_SCALEZ_UPDATE) {
             ModelData data = ModelData.get(player);

             Float partFloat = buffer.readFloat();
             data.propScaleZ.add(partFloat);

	 	     Server.sendAssociatedData(player, EnumPackets.PROP_SCALEZ_UPDATE, player.getUniqueID(), partFloat);
	     } else if (type == EnumPackets.PROP_OFFSETX_UPDATE) {
             ModelData data = ModelData.get(player);

             Float partFloat = buffer.readFloat();
             data.propOffsetX.add(partFloat);

	 	     Server.sendAssociatedData(player, EnumPackets.PROP_OFFSETX_UPDATE, player.getUniqueID(), partFloat);
	     } else if (type == EnumPackets.PROP_OFFSETY_UPDATE) {
             ModelData data = ModelData.get(player);

             Float partFloat = buffer.readFloat();
             data.propOffsetY.add(partFloat);

	 	     Server.sendAssociatedData(player, EnumPackets.PROP_OFFSETY_UPDATE, player.getUniqueID(), partFloat);
	     } else if (type == EnumPackets.PROP_OFFSETZ_UPDATE) {
             ModelData data = ModelData.get(player);

             Float partFloat = buffer.readFloat();
             data.propOffsetZ.add(partFloat);

	 	     Server.sendAssociatedData(player, EnumPackets.PROP_OFFSETZ_UPDATE, player.getUniqueID(), partFloat);
	     } else if (type == EnumPackets.PROP_ROTATEX_UPDATE) {
             ModelData data = ModelData.get(player);

             Float partFloat = buffer.readFloat();
             data.propRotateX.add(partFloat);

	 	     Server.sendAssociatedData(player, EnumPackets.PROP_ROTATEX_UPDATE, player.getUniqueID(), partFloat);
	     } else if (type == EnumPackets.PROP_ROTATEY_UPDATE) {
             ModelData data = ModelData.get(player);

             Float partFloat = buffer.readFloat();
             data.propRotateY.add(partFloat);

	 	     Server.sendAssociatedData(player, EnumPackets.PROP_ROTATEY_UPDATE, player.getUniqueID(), partFloat);
	     } else if (type == EnumPackets.PROP_ROTATEZ_UPDATE) {
             ModelData data = ModelData.get(player);

             Float partFloat = buffer.readFloat();
             data.propRotateZ.add(partFloat);

	 	     Server.sendAssociatedData(player, EnumPackets.PROP_ROTATEZ_UPDATE, player.getUniqueID(), partFloat);
	     } else if (type == EnumPackets.PROP_AUTOSCALE_UPDATE) {
             ModelData data = ModelData.get(player);

             Boolean partBoolean = buffer.readBoolean();
             data.propMatchScaling.add(partBoolean);

	 	     Server.sendAssociatedData(player, EnumPackets.PROP_AUTOSCALE_UPDATE, player.getUniqueID(), partBoolean);
	     } else if (type == EnumPackets.PROP_REMOVE) {
             ModelData data = ModelData.get(player);

             Integer index = buffer.readInt();
             data.removePropLocal(index);

	 	     Server.sendAssociatedData(player, EnumPackets.PROP_REMOVE, player.getUniqueID(), index);
	     }

     }
}
