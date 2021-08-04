package noppes.mpm.client;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Collections;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
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
import noppes.mpm.Emote;
import noppes.mpm.Prop;
import noppes.mpm.PropGroup;
import noppes.mpm.Server;
import noppes.mpm.client.gui.GuiCreationPropLoad;
import noppes.mpm.client.gui.GuiCreationProps;
import noppes.mpm.client.gui.GuiCreationScreenInterface;
import noppes.mpm.client.gui.GuiCreationEmotes;
import noppes.mpm.client.gui.GuiCreationEmoteLoad;
import noppes.mpm.client.gui.GuiCreationSkinLoad;
import noppes.mpm.client.gui.GuiMPM;
import noppes.mpm.client.gui.util.GuiNPCInterface;
import noppes.mpm.constants.EnumPackets;
import net.minecraft.util.text.TextComponentTranslation;

public class PacketHandlerClient extends PacketHandlerServer {
	static EnumPackets[] cachedEnums = EnumPackets.values();
	@SubscribeEvent
	public void onPacketData(ClientCustomPacketEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		ByteBuf buf = event.getPacket().payload();
		Minecraft.getMinecraft().addScheduledTask(() -> {
			EnumPackets en = null;

			try {
				en = cachedEnums[buf.readInt()];
				this.handlePacket(buf, player, en);
			} catch (Exception var5) {
				LogWriter.error("Packet error: " + en, var5);
			}

		});
	}

	private void handlePacket(ByteBuf buffer, EntityPlayer player, EnumPackets type) throws Exception {
		// LogWriter.warn("ClientPacket: " + type);
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
			EntityPlayer pl;
			if (type == EnumPackets.EYE_BLINK) {
				pl = player.worldObj.getPlayerEntityByUUID(UUID.fromString(Server.readString(buffer)));
				if (pl == null) {
					return;
				}

				ModelData data = ModelData.get(pl);
				data.eyes.blinkStart = System.currentTimeMillis();
			} else if (type == EnumPackets.SEND_PLAYER_DATA) {
				pl = player.worldObj.getPlayerEntityByUUID(UUID.fromString(Server.readString(buffer)));
				if (pl == null) {
					return;
				}

				ModelData data = ModelData.get(pl);
				NBTTagCompound compound = Server.readNBT(buffer);
				data.readFromNBT(compound);
				data.save();
				if (pl == Minecraft.getMinecraft().thePlayer) {
					data.lastEdited = System.currentTimeMillis();
				}
			} else if (type == EnumPackets.CHAT_EVENT) {
				pl = player.worldObj.getPlayerEntityByUUID(UUID.fromString(Server.readString(buffer)));
				if (pl == null) {
					return;
				}

				String message = Server.readString(buffer);
				ChatMessages.getChatMessages(pl.getName()).addMessage(message);
			} else if (type == EnumPackets.BACK_ITEM_REMOVE) {
				pl = player.worldObj.getPlayerEntityByUUID(UUID.fromString(Server.readString(buffer)));
				if (pl == null) {
					return;
				}

				ModelData data = ModelData.get(pl);
				data.backItem = null;
			} else if (type == EnumPackets.BACK_ITEM_UPDATE) {
				pl = player.worldObj.getPlayerEntityByUUID(UUID.fromString(Server.readString(buffer)));
				if (pl == null) {
					return;
				}

				NBTTagCompound compound = Server.readNBT(buffer);
				ItemStack item = new ItemStack(compound);
				ModelData data = ModelData.get(pl);
				data.backItem = item;
			} else if (type == EnumPackets.PROP_ADD) {
				pl = player.worldObj.getPlayerEntityByUUID(UUID.fromString(Server.readString(buffer)));
				if (pl == null) {
					return;
				}

				ModelData data = ModelData.get(pl);

				Prop prop = new Prop();
				NBTTagCompound compound = Server.readNBT(buffer);
				prop.readFromNBT(compound);
				data.propBase.props.add(prop);
			} else if (type == EnumPackets.PROP_SYNC) {
				pl = player.worldObj.getPlayerEntityByUUID(UUID.fromString(Server.readString(buffer)));
				if (pl == null) {
					return;
				}

				ModelData data = ModelData.get(pl);
				NBTTagCompound compound = Server.readNBT(buffer);
				data.propsFromNBT(compound);
			} else if (type == EnumPackets.PROP_CLEAR) {
				pl = player.worldObj.getPlayerEntityByUUID(UUID.fromString(Server.readString(buffer)));
				if (pl == null) {
					return;
				}

				ModelData data = ModelData.get(pl);
				data.propBase.props.clear();
				data.propGroups = new ArrayList<PropGroup>();
			} else if (type == EnumPackets.PROP_REMOVE) {
				pl = player.worldObj.getPlayerEntityByUUID(UUID.fromString(Server.readString(buffer)));
				if (pl == null) {
					return;
				}

				ModelData data = ModelData.get(pl);
				data.propBase.props.remove(buffer.readInt());
			} else if (type == EnumPackets.PROP_GUI_OPEN) {
				GuiMPM guiMPM = new GuiMPM();
				Minecraft.getMinecraft().displayGuiScreen(guiMPM);
				try {
					guiMPM.setSubGui((GuiNPCInterface)GuiCreationProps.GuiProps.getClass().newInstance());
				} catch (IllegalAccessException | InstantiationException e) {

				}
			} else if (type == EnumPackets.PROP_HIDE) {
				pl = player.worldObj.getPlayerEntityByUUID(UUID.fromString(Server.readString(buffer)));
				if (pl == null) {
					return;
				}

				ModelData data = ModelData.get(pl);
				data.propBase.props.get(buffer.readInt()).hide = true;
			} else if (type == EnumPackets.PROP_SHOW) {
				pl = player.worldObj.getPlayerEntityByUUID(UUID.fromString(Server.readString(buffer)));
				if (pl == null) {
					return;
				}

				ModelData data = ModelData.get(pl);
				data.propBase.props.get(buffer.readInt()).hide = false;
			} else if (type == EnumPackets.PROP_NAME) {
				pl = player.worldObj.getPlayerEntityByUUID(UUID.fromString(Server.readString(buffer)));
				if (pl == null) {
					return;
				}

				ModelData data = ModelData.get(pl);
				data.propBase.props.get(data.propBase.props.size() - 1).name = Server.readString(buffer);
			} else if (type == EnumPackets.PROPGROUP_HIDE) {
				pl = player.worldObj.getPlayerEntityByUUID(UUID.fromString(Server.readString(buffer)));
				if (pl == null) {
					return;
				}

				ModelData data = ModelData.get(pl);
				data.propGroups.get(buffer.readInt()).hide = true;
			} else if (type == EnumPackets.PROPGROUP_SHOW) {
				pl = player.worldObj.getPlayerEntityByUUID(UUID.fromString(Server.readString(buffer)));
				if (pl == null) {
					return;
				}

				ModelData data = ModelData.get(pl);
				data.propGroups.get(buffer.readInt()).hide = false;
			} else if (type == EnumPackets.PROPGROUP_ADD) {
				pl = player.worldObj.getPlayerEntityByUUID(UUID.fromString(Server.readString(buffer)));
				if (pl == null) {
					return;
				}

				ModelData data = ModelData.get(pl);

				PropGroup propGroup = new PropGroup(pl);
				NBTTagCompound compound = Server.readNBT(buffer);
				propGroup.readFromNBT(compound);
				data.propGroups.add(propGroup);
			} else if (type == EnumPackets.PROPGROUP_REMOVE) {
				pl = player.worldObj.getPlayerEntityByUUID(UUID.fromString(Server.readString(buffer)));
				if (pl == null) {
					return;
				}

				ModelData data = ModelData.get(pl);
				data.propGroups.remove(buffer.readInt());
			} else if (type == EnumPackets.PARTICLE) {
				animation = buffer.readInt();
				if (animation == 0) {
					pl = player.worldObj.getPlayerEntityByUUID(UUID.fromString(Server.readString(buffer)));
					if (pl == null) {
						return;
					}

					ModelData data = ModelData.get(pl);
					data.inLove = 40;
				} else if (animation == 1) {
					player.worldObj.spawnParticle(EnumParticleTypes.NOTE, buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), 0.0D, 0.0D, new int[0]);
				} else if (animation == 2) {
					pl = player.worldObj.getPlayerEntityByUUID(UUID.fromString(Server.readString(buffer)));
					if (pl == null) {
						return;
					}

					ModelData data = ModelData.get(pl);

					for(int i = 0; i < 5; ++i) {
						double d0 = player.getRNG().nextGaussian() * 0.02D;
						double d1 = player.getRNG().nextGaussian() * 0.02D;
						double d2 = player.getRNG().nextGaussian() * 0.02D;
						double x = player.posX + (double)((player.getRNG().nextFloat() - 0.5F) * player.width * 2.0F);
						double z = player.posZ + (double)((player.getRNG().nextFloat() - 0.5F) * player.width * 2.0F);
						player.worldObj.spawnParticle(EnumParticleTypes.VILLAGER_ANGRY, x, player.posY + 0.800000011920929D + (double)(player.getRNG().nextFloat() * player.height / 2.0F) - player.getYOffset() - (double)data.getBodyY(), z, d0, d1, d2, new int[0]);
					}
				}
			} else if (type == EnumPackets.ANIMATION) {
				pl = player.worldObj.getPlayerEntityByUUID(UUID.fromString(Server.readString(buffer)));
				if (pl == null) {
					return;
				}

				ModelData data = ModelData.get(pl);
				data.setAnimation(buffer.readInt());
				data.animationStart = pl.ticksExisted;
			} else if (type == EnumPackets.ENTITIES_ENABLE) {
				MorePlayerModels.hasEntityPermission = true;
			} else if (type == EnumPackets.ENTITIES_DISABLE) {
				MorePlayerModels.hasEntityPermission = false;
			} else if (type == EnumPackets.SKIN_FILENAME_UPDATE) {
				MorePlayerModels.fileNamesSkins = new ArrayList<String>();

				NBTTagCompound compound = Server.readNBT(buffer);

				for (int i = 0; i < Integer.MAX_VALUE; i++) {
					String string = compound.getString(("skinName" + String.valueOf(i)));

					if (!string.equals("")) {
						MorePlayerModels.fileNamesSkins.add(string);
					} else {
						break;
					}
				}
			} else if (type == EnumPackets.UPDATE_PLAYER_DATA_CLIENT) {
				ModelData data = ModelData.get(player);
				NBTTagCompound compound = Server.readNBT(buffer);

				data.readFromNBT(compound);
				data.save();
				data.lastEdited = System.currentTimeMillis();
			} else if (type == EnumPackets.SKIN_LOAD_GUI) {
				GuiMPM guiMPM = new GuiMPM();
				Minecraft.getMinecraft().displayGuiScreen(guiMPM);
				try {
					guiMPM.setSubGui((GuiNPCInterface)GuiCreationSkinLoad.GuiSkinLoad.getClass().newInstance());
				} catch (IllegalAccessException | InstantiationException e) {

				}
			} else if (type == EnumPackets.PROPGROUPS_LOAD_GUI) {
				GuiMPM guiMPM = new GuiMPM();
				Minecraft.getMinecraft().displayGuiScreen(guiMPM);
				try {
					guiMPM.setSubGui((GuiNPCInterface)GuiCreationPropLoad.GuiPropLoad.getClass().newInstance());
				} catch (IllegalAccessException | InstantiationException e) {

				}
			} else if (type == EnumPackets.PROPGROUPS_FILENAME_UPDATE) {
				MorePlayerModels.fileNamesPropGroups = new ArrayList<String>();

				NBTTagCompound compound = Server.readNBT(buffer);

				for (int i = 0; i < Integer.MAX_VALUE; i++) {
					String string = compound.getString(("propGroupName" + String.valueOf(i)));

					if (!string.equals("")) {
						MorePlayerModels.fileNamesPropGroups.add(string);
					} else {
						break;
					}
				}
			} else if (type == EnumPackets.PROPGROUP_LOAD_CLIENT) {
				ModelData data = ModelData.get(player);
				NBTTagCompound compound = Server.readNBT(buffer);

				PropGroup propGroup = new PropGroup(player);
				propGroup.readFromNBT(compound);
				data.propGroups.add(propGroup);
			} else if (type == EnumPackets.EMOTE_FILENAME_UPDATE) {
				ArrayList<String> names = Server.readArray(buffer);
				if(names == null) return;
				Collections.sort(names);
				if(!GuiCreationEmoteLoad.cachedEmoteFileNames.equals(names)) {
					GuiCreationEmoteLoad.cachedEmoteFileNames = names;
					GuiCreationEmoteLoad.hasCachedEmoteFileNamesChanged = true;
				}
			} else if (type == EnumPackets.EMOTE_LOAD) {
				// String emoteName = Server.readString(buffer);
				// if(emoteName == null) return;
				Emote emote = Emote.readEmote(buffer);
				if(emote == null) return;

				GuiCreationEmotes.resetAndReplaceEmote(emote);
				// GuiCreationEmotes.curEmoteName = emoteName;
				GuiCreationEmotes.ischangedfromserver = false;
			} else if (type == EnumPackets.EMOTE_DO) {
				Float speed = buffer.readFloat();
				String playerName = Server.readString(buffer);
				if(playerName == null) return;
				Emote emote = Emote.readEmote(buffer);
				if(emote == null) return;

				World world = Minecraft.getMinecraft().theWorld;
				EntityPlayer target = world.getPlayerEntityByName(playerName);
				if(target != null) {
					ModelData data = ModelData.get(target);
					data.startEmote(emote, speed, target);
				}
			} else if (type == EnumPackets.EMOTE_END) {
				String playerName = Server.readString(buffer);
				if(playerName == null) return;

				World world = Minecraft.getMinecraft().theWorld;
				EntityPlayer target = world.getPlayerEntityByName(playerName);
				if(target != null) {
					ModelData data = ModelData.get(target);
					data.endCurEmote();
				}
			}
		}
	}
}
