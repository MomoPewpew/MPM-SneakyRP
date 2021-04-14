package noppes.mpm.client;

import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import noppes.mpm.LogWriter;
import noppes.mpm.ModelData;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.PacketHandlerServer;
import noppes.mpm.Server;
import noppes.mpm.client.gui.GuiCreationScreenInterface;
import noppes.mpm.constants.EnumPackets;

public class PacketHandlerClient extends PacketHandlerServer {
     @SubscribeEvent
     public void onPacketData(ClientCustomPacketEvent event) {
          EntityPlayer player = Minecraft.getMinecraft().player;
          ByteBuf buf = event.getPacket().payload();
          Minecraft.getMinecraft().addScheduledTask(() -> {
               EnumPackets en = null;

               try {
                    en = EnumPackets.values()[buf.readInt()];
                    this.handlePacket(buf, player, en);
               } catch (Exception var5) {
                    LogWriter.error("Packet error: " + en, var5);
               }

          });
     }

     private void handlePacket(ByteBuf buffer, EntityPlayer player, EnumPackets type) throws Exception {
          int animation;
          if (type == EnumPackets.PING) {
               animation = buffer.readInt();
               if (animation == MorePlayerModels.Version) {
                    MorePlayerModels.HasServerSide = true;
                    GuiCreationScreenInterface.Message = "";
               } else if (animation < MorePlayerModels.Version) {
                    MorePlayerModels.HasServerSide = false;
                    GuiCreationScreenInterface.Message = "message.lowerversion";
               } else if (animation > MorePlayerModels.Version) {
                    MorePlayerModels.HasServerSide = false;
                    GuiCreationScreenInterface.Message = "message.higherversion";
               }
          } else {
               ModelData data;
               EntityPlayer pl;
               if (type == EnumPackets.EYE_BLINK) {
                    pl = player.world.getPlayerEntityByUUID(UUID.fromString(Server.readString(buffer)));
                    if (pl == null) {
                         return;
                    }

                    data = ModelData.get(pl);
                    data.eyes.blinkStart = System.currentTimeMillis();
               } else if (type == EnumPackets.SEND_PLAYER_DATA) {
                    pl = player.world.getPlayerEntityByUUID(UUID.fromString(Server.readString(buffer)));
                    if (pl == null) {
                         return;
                    }

                    data = ModelData.get(pl);
                    NBTTagCompound compound = Server.readNBT(buffer);
                    data.readFromNBT(compound);
                    data.save();
                    if (pl == Minecraft.getMinecraft().player) {
                         data.lastEdited = System.currentTimeMillis();
                    }
               } else if (type == EnumPackets.CHAT_EVENT) {
                    pl = player.world.getPlayerEntityByUUID(UUID.fromString(Server.readString(buffer)));
                    if (pl == null) {
                         return;
                    }

                    String message = Server.readString(buffer);
                    ChatMessages.getChatMessages(pl.getName()).addMessage(message);
               } else if (type == EnumPackets.BACK_ITEM_REMOVE) {
                    pl = player.world.getPlayerEntityByUUID(UUID.fromString(Server.readString(buffer)));
                    if (pl == null) {
                         return;
                    }

                    data = ModelData.get(pl);
                    data.backItem = ItemStack.EMPTY;
               } else if (type == EnumPackets.BACK_ITEM_UPDATE) {
                    pl = player.world.getPlayerEntityByUUID(UUID.fromString(Server.readString(buffer)));
                    if (pl == null) {
                         return;
                    }

                    NBTTagCompound compound = Server.readNBT(buffer);
                    ItemStack item = new ItemStack(compound);
                    ModelData data = ModelData.get(pl);
                    data.backItem = item;
               } else if (type == EnumPackets.PARTICLE) {
                    animation = buffer.readInt();
                    EntityPlayer pl;
                    ModelData data;
                    if (animation == 0) {
                         pl = player.world.getPlayerEntityByUUID(UUID.fromString(Server.readString(buffer)));
                         if (pl == null) {
                              return;
                         }

                         data = ModelData.get(pl);
                         data.inLove = 40;
                    } else if (animation == 1) {
                         player.world.spawnParticle(EnumParticleTypes.NOTE, buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), 0.0D, 0.0D, new int[0]);
                    } else if (animation == 2) {
                         pl = player.world.getPlayerEntityByUUID(UUID.fromString(Server.readString(buffer)));
                         if (pl == null) {
                              return;
                         }

                         data = ModelData.get(pl);

                         for(int i = 0; i < 5; ++i) {
                              double d0 = player.getRNG().nextGaussian() * 0.02D;
                              double d1 = player.getRNG().nextGaussian() * 0.02D;
                              double d2 = player.getRNG().nextGaussian() * 0.02D;
                              double x = player.posX + (double)((player.getRNG().nextFloat() - 0.5F) * player.width * 2.0F);
                              double z = player.posZ + (double)((player.getRNG().nextFloat() - 0.5F) * player.width * 2.0F);
                              player.world.spawnParticle(EnumParticleTypes.VILLAGER_ANGRY, x, player.posY + 0.800000011920929D + (double)(player.getRNG().nextFloat() * player.height / 2.0F) - player.getYOffset() - (double)data.getBodyY(), z, d0, d1, d2, new int[0]);
                         }
                    }
               } else if (type == EnumPackets.ANIMATION) {
                    pl = player.world.getPlayerEntityByUUID(UUID.fromString(Server.readString(buffer)));
                    if (pl == null) {
                         return;
                    }

                    data = ModelData.get(pl);
                    data.setAnimation(buffer.readInt());
                    data.animationStart = pl.ticksExisted;
               }
          }

     }
}
