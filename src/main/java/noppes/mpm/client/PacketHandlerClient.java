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
import noppes.mpm.util.EntityScaleManagerClient;
import noppes.mpm.util.EntityScaleManagerServer;
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
		LogWriter.warn("ClientPacket: " + type);
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
				UUID uuid = new UUID(buffer.readLong(), buffer.readLong());
				pl = player.worldObj.getPlayerEntityByUUID(uuid);
				if (pl == null) {
					return;
				}

				ModelData data = ModelData.get(pl);
				data.eyes.blinkStart = System.currentTimeMillis();
			} else if (type == EnumPackets.SEND_PLAYER_DATA) {
				UUID uuid = new UUID(buffer.readLong(), buffer.readLong());
				pl = player.worldObj.getPlayerEntityByUUID(uuid);
				if (pl == null) {
					return;
				}

				ModelData data = ModelData.get(pl);
				NBTTagCompound compound = Server.readNBT(buffer);
				data.readFromNBT(compound);
				data.save();
			} else if (type == EnumPackets.CHAT_EVENT) {
				UUID uuid = new UUID(buffer.readLong(), buffer.readLong());
				pl = player.worldObj.getPlayerEntityByUUID(uuid);
				if (pl == null) {
					return;
				}

				String message = Server.readString(buffer);
				ChatMessages.getChatMessages(pl.getName()).addMessage(message);
			} else if (type == EnumPackets.BACK_ITEM_REMOVE) {
				UUID uuid = new UUID(buffer.readLong(), buffer.readLong());
				pl = player.worldObj.getPlayerEntityByUUID(uuid);
				if (pl == null) {
					return;
				}

				ModelData data = ModelData.get(pl);
				data.backItem = null;
			} else if (type == EnumPackets.BACK_ITEM_UPDATE) {
				UUID uuid = new UUID(buffer.readLong(), buffer.readLong());
				pl = player.worldObj.getPlayerEntityByUUID(uuid);
				if (pl == null) {
					return;
				}

				NBTTagCompound compound = Server.readNBT(buffer);
				ItemStack item = new ItemStack(compound);
				ModelData data = ModelData.get(pl);
				data.backItem = item;
			} else if (type == EnumPackets.PROP_ADD) {
				UUID uuid = new UUID(buffer.readLong(), buffer.readLong());
				pl = player.worldObj.getPlayerEntityByUUID(uuid);
				if (pl == null) {
					return;
				}

				ModelData data = ModelData.get(pl);

				Prop prop = new Prop();
				NBTTagCompound compound = Server.readNBT(buffer);
				prop.readFromNBT(compound);
				data.propBase.props.add(prop);
			} else if (type == EnumPackets.PROP_SYNC) {
				UUID uuid = new UUID(buffer.readLong(), buffer.readLong());
				pl = player.worldObj.getPlayerEntityByUUID(uuid);
				if (pl == null) {
					return;
				}

				ModelData data = ModelData.get(pl);
				NBTTagCompound compound = Server.readNBT(buffer);
				data.propsFromNBT(compound);
			} else if (type == EnumPackets.PROP_CLEAR) {
				UUID uuid = new UUID(buffer.readLong(), buffer.readLong());
				pl = player.worldObj.getPlayerEntityByUUID(uuid);
				if (pl == null) {
					return;
				}

				ModelData data = ModelData.get(pl);
				data.propBase.props.clear();
				data.propGroups = new ArrayList<PropGroup>();
			} else if (type == EnumPackets.PROP_REMOVE) {
				UUID uuid = new UUID(buffer.readLong(), buffer.readLong());
				pl = player.worldObj.getPlayerEntityByUUID(uuid);
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
				UUID uuid = new UUID(buffer.readLong(), buffer.readLong());
				pl = player.worldObj.getPlayerEntityByUUID(uuid);
				if (pl == null) {
					return;
				}

				ModelData data = ModelData.get(pl);
				data.propBase.props.get(buffer.readInt()).hide = true;
			} else if (type == EnumPackets.PROP_SHOW) {
				UUID uuid = new UUID(buffer.readLong(), buffer.readLong());
				pl = player.worldObj.getPlayerEntityByUUID(uuid);
				if (pl == null) {
					return;
				}

				ModelData data = ModelData.get(pl);
				data.propBase.props.get(buffer.readInt()).hide = false;
			} else if (type == EnumPackets.PROP_NAME) {
				UUID uuid = new UUID(buffer.readLong(), buffer.readLong());
				pl = player.worldObj.getPlayerEntityByUUID(uuid);
				if (pl == null) {
					return;
				}

				ModelData data = ModelData.get(pl);
				data.propBase.props.get(data.propBase.props.size() - 1).name = Server.readString(buffer);
			} else if (type == EnumPackets.PROPGROUP_HIDE) {
				UUID uuid = new UUID(buffer.readLong(), buffer.readLong());
				pl = player.worldObj.getPlayerEntityByUUID(uuid);
				if (pl == null) {
					return;
				}

				ModelData data = ModelData.get(pl);
				data.propGroups.get(buffer.readInt()).hide = true;
			} else if (type == EnumPackets.PROPGROUP_SHOW) {
				UUID uuid = new UUID(buffer.readLong(), buffer.readLong());
				pl = player.worldObj.getPlayerEntityByUUID(uuid);
				if (pl == null) {
					return;
				}

				ModelData data = ModelData.get(pl);
				data.propGroups.get(buffer.readInt()).hide = false;
			} else if (type == EnumPackets.PROPGROUP_ADD) {
				UUID uuid = new UUID(buffer.readLong(), buffer.readLong());
				pl = player.worldObj.getPlayerEntityByUUID(uuid);
				if (pl == null) {
					return;
				}

				ModelData data = ModelData.get(pl);

				PropGroup propGroup = new PropGroup(pl);
				NBTTagCompound compound = Server.readNBT(buffer);
				propGroup.readFromNBT(compound);
				data.propGroups.add(propGroup);
			} else if (type == EnumPackets.PROPGROUP_REMOVE) {
				UUID uuid = new UUID(buffer.readLong(), buffer.readLong());
				pl = player.worldObj.getPlayerEntityByUUID(uuid);
				if (pl == null) {
					return;
				}

				ModelData data = ModelData.get(pl);
				data.propGroups.remove(buffer.readInt());
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
				int totalVaulted = buffer.readInt();
				ArrayList<String> names = Server.readArray(buffer);
				if(names == null || totalVaulted > names.size()) return;
				for(int i = 0; i < totalVaulted; i += 1) {
					names.set(i, names.get(i) + " (vault)");
				}
				Collections.sort(names);
				if(!GuiCreationEmoteLoad.cachedEmoteFileNames.equals(names)) {
					GuiCreationEmoteLoad.cachedEmoteFileNames = names;
					GuiCreationEmoteLoad.hasCachedEmoteFileNamesChanged = true;
				}
			} else if (type == EnumPackets.EMOTE_LOAD) {
				Emote emote = Emote.readEmote(buffer);
				if(emote == null) return;

				GuiCreationEmotes.loadNewEmote(emote);
				ModelData data = ModelData.get(player);

				data.startPreview(emote.clone());
			} else if (type == EnumPackets.EMOTE_DO) {
				UUID uuid = new UUID(buffer.readLong(), buffer.readLong());
				pl = player.worldObj.getPlayerEntityByUUID(uuid);
				if(pl == null) {
					LogWriter.error("Packet error: Invalid player for EMOTE_DO, " + uuid.toString());
					return;
				}
				ModelData data = ModelData.get(pl);

				Emote emote = Emote.readEmote(buffer);
				if(emote == null) {
					LogWriter.error("Packet error: Invalid emote data for EMOTE_DO");
					return;
				}
				LogWriter.error("EMOTE_DO " + uuid + " ; " + emote.toString());

				Float speed = buffer.readFloat();
				boolean cancel_if_conflicting = buffer.readBoolean();
				boolean outro_all_playing_first = buffer.readBoolean();
				boolean override_instead_of_outro = buffer.readBoolean();

				data.startEmote(emote, speed, cancel_if_conflicting, outro_all_playing_first, override_instead_of_outro);
			} else if (type == EnumPackets.EMOTE_DATA) {
				UUID uuid = new UUID(buffer.readLong(), buffer.readLong());
				pl = player.worldObj.getPlayerEntityByUUID(uuid);
				if(pl == null) {
					LogWriter.error("Packet error: Invalid player for EMOTE_DATA, " + uuid);
					return;
				}
				ModelData data = ModelData.get(pl);

				Emote emote = Emote.readEmote(buffer);
				if(emote == null) {
					LogWriter.error("Packet error: Invalid emote data for EMOTE_DATA");
					return;
				}
				LogWriter.error("EMOTE_DATA " + uuid + " ; " + emote.toString());


				data.emoteCommands = emote.commands;
				data.emotePartUsages = emote.partUsages;
				//totally overwrite previous emote data with new data
				//NOTE: if an exception is thrown while reading the packet it might leave garbage values in the emote data tables, causing minor animation glitches, however this should not be able to happen

				for(int i = 0; i < 2*Emote.PART_COUNT; i += 1) data.emoteSpeeds[i] = buffer.readFloat();
				for(int i = 0; i < 2*Emote.PART_COUNT; i += 1) {
					int a = buffer.readInt();
					data.emoteCommandSections[i] = (byte)(a&3);
					data.emoteCommandIndices[i] = a >> 2;
				}
				for(int i = 0; i < 2*Emote.PART_COUNT; i += 1) data.emoteCommandTimes[i] = buffer.readFloat();

				for(int i = 0; i < Emote.STATE_COUNT; i += 1) {
					data.emoteMovements[i] = 0.0f;
					data.emoteStates[i] = 0.0f;
				}

				data.emoteIsPlaying = true;
			} else if (type == EnumPackets.EMOTE_END) {
				UUID uuid = new UUID(buffer.readLong(), buffer.readLong());
				pl = player.worldObj.getPlayerEntityByUUID(uuid);

				// pl = player.worldObj.getPlayerEntityByUUID(UUID.fromString(Server.readString(buffer)));

				if(pl == null) {
					LogWriter.error("Packet error: Invalid player for EMOTE_END, " + uuid);
					return;
				}
				ModelData data = ModelData.get(pl);


				LogWriter.error("EMOTE_END " + uuid);

				boolean override_instead_of_outro = buffer.readBoolean();

				data.endEmotes(override_instead_of_outro);
			} else if (type == EnumPackets.NAMES_TOGGLE) {
				MorePlayerModels.HidePlayerNames = !MorePlayerModels.HidePlayerNames;
			} else if (type == EnumPackets.TOGGLE_HAT) {
				ModelData data = ModelData.get(player);
				data.hideHat = !data.hideHat;
			} else if (type == EnumPackets.TOGGLE_SHIRT) {
				ModelData data = ModelData.get(player);
				data.hideShirt = !data.hideShirt;
			} else if (type == EnumPackets.TOGGLE_PANTS) {
				ModelData data = ModelData.get(player);
				data.hidePants = !data.hidePants;
			} else if (type == EnumPackets.ENTITY_SCALE_MULT) {
				String name = Server.readString(buffer);
				Float mult = buffer.readFloat();

				EntityScaleManagerClient.setScaleMult(name, mult);
			}
		}
	}
}
