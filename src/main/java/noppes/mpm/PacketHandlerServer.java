package noppes.mpm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import noppes.mpm.commands.CommandProp;
import noppes.mpm.constants.EnumPackets;
import noppes.mpm.util.EntityScaleManagerServer;
import noppes.mpm.MorePlayerModels;

public class PacketHandlerServer {
	static final EnumPackets[] cachedEnums = EnumPackets.values();
	@SubscribeEvent
	public void onPacketData(ServerCustomPacketEvent event) {
		EntityPlayerMP player = ((NetHandlerPlayServer)event.getHandler()).playerEntity;
		ByteBuf buf = event.getPacket().payload();
		player.getServer().addScheduledTask(() -> {
			EnumPackets type = null;

			try {
				int t = buf.readInt();
				type = cachedEnums[t];
				this.handlePacket(buf, player, type);
			} catch (Exception var5) {
				LogWriter.error("Error with EnumPackets." + type, var5);
			}

		});
	}

	private void handlePacket(ByteBuf buffer, EntityPlayerMP player, EnumPackets type) throws Exception {
		// LogWriter.warn("ServerPacket: " + type);
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
		} else if (type == EnumPackets.PROP_CLEAR) {
			ModelData data = ModelData.get(player);

			data.clearPropsServer();
		} else if (type == EnumPackets.PROP_ADD) {
			ModelData data = ModelData.get(player);

			Prop prop = new Prop();
			NBTTagCompound compound = Server.readNBT(buffer);
			prop.readFromNBT(compound);
			data.propBase.props.add(prop);

			Server.sendAssociatedData(player, EnumPackets.PROP_ADD, player.getUniqueID(), compound);
		} else if (type == EnumPackets.PROP_SYNC) {
			ModelData data = ModelData.get(player);

			NBTTagCompound compound = Server.readNBT(buffer);
			data.propsFromNBT(compound);

			Server.sendAssociatedData(player, EnumPackets.PROP_SYNC, player.getUniqueID(), compound);
		} else if (type == EnumPackets.PROP_REMOVE) {
			ModelData data = ModelData.get(player);

			Integer index = buffer.readInt();
			data.propBase.removePropServer(index);
		} else if (type == EnumPackets.PROP_GIVE) {
			Integer index = buffer.readInt();

			CommandProp.giveProp(null, index, player);
		} else if (type == EnumPackets.PROPGROUP_GIVE) {
			Integer index = buffer.readInt();

			CommandProp.givePropGroup(null, index, player);
		} else if (type == EnumPackets.PROPGROUP_SAVE) {
			NBTTagCompound compound = Server.readNBT(buffer);
			String uuid = compound.getString("uuid");
			NBTTagCompound propCompound = compound.getCompoundTag("propGroup");

			File dir = null;
			dir = new File(dir, ".." + File.separator + "moreplayermodels" + File.separator + "propGroups");
			if (!dir.exists()) {
				dir.mkdirs();
			}

			String filename = uuid + ".dat";

			try {
				File file = new File(dir, filename);
				CompressedStreamTools.writeCompressed(propCompound, new FileOutputStream(file));
			} catch (Exception var6) {
				LogWriter.except(var6);
				var6.printStackTrace();
			}
		} else if (type == EnumPackets.SKIN_FILENAME_UPDATE) {
			MorePlayerModels.syncSkinFileNames(player);
		} else if (type == EnumPackets.PROPGROUPS_FILENAME_UPDATE) {
			MorePlayerModels.syncPropGroupFileNames(player);
		} else if (type == EnumPackets.UPDATE_PLAYER_DATA_CLIENT) {
			NBTTagCompound compound = Server.readNBT(buffer);

			String filename = compound.getString("skinName") + ".dat";
			File file;

			File dir = null;

			try {
				dir = new File(dir, ".." + File.separator + "moreplayermodels" + File.separator + "skins" + File.separator + "unrestricted");

				if (!dir.exists()) {
					dir.mkdirs();
				}

				file = new File(dir, filename);

				if (!file.exists()) {
					dir = null;
					dir = new File(dir, ".." + File.separator + "moreplayermodels" + File.separator + "skins");

					if (!dir.exists()) {
						dir.mkdirs();
					}

					file = new File(dir, filename);
				}

				if (!file.exists()) {
					dir = null;
					dir = new File(dir, ".." + File.separator + "moreplayermodels" + File.separator + "skins" + File.separator + "restricted");

					if (!dir.exists()) {
						dir.mkdirs();
					}

					file = new File(dir, filename);
				}

				if (!file.exists()) {
					return;
				}

				NBTTagCompound skinCompound = new NBTTagCompound();

				skinCompound = CompressedStreamTools.readCompressed(new FileInputStream(file));

				Server.sendData(player, EnumPackets.UPDATE_PLAYER_DATA_CLIENT, skinCompound);
			} catch (Exception var4) {
				LogWriter.except(var4);
			}
		} else if (type == EnumPackets.PROPGROUP_LOAD_CLIENT) {
			NBTTagCompound compound = Server.readNBT(buffer);

			String filename = compound.getString("propName") + ".dat";
			File file;

			File dir = null;

			try {
				dir = new File(dir, ".." + File.separator + "moreplayermodels" + File.separator + "propGroupsNamed");

				if (!dir.exists()) {
					dir.mkdirs();
				}

				file = new File(dir, filename);

				if (!file.exists()) {
					dir = null;
					dir = new File(dir, ".." + File.separator + "moreplayermodels" + File.separator + "propGroupsNamed" + File.separator + "restricted");

					if (!dir.exists()) {
						dir.mkdirs();
					}

					file = new File(dir, filename);
				}

				if (!file.exists()) {
					return;
				}

				NBTTagCompound propCompound = new NBTTagCompound();

				propCompound = CompressedStreamTools.readCompressed(new FileInputStream(file));

				Server.sendData(player, EnumPackets.PROPGROUP_LOAD_CLIENT, propCompound);
			} catch (Exception var4) {
				LogWriter.except(var4);
			}
		} else if (type == EnumPackets.EMOTE_FILENAME_UPDATE) {
			ArrayList<String> list = new ArrayList<String>();
			int totalVaulted = 0;

			if (MorePlayerModels.emoteVaultFolder.exists()) {
				for (final File fileEntry : MorePlayerModels.emoteVaultFolder.listFiles()) {
					if (fileEntry.isDirectory()) {
						continue;
					} else {
						String emoteName = fileEntry.getName().substring(0, fileEntry.getName().length() - 4);
						list.add(emoteName);
						totalVaulted += 1;
					}
				}
			}

			if (MorePlayerModels.emoteFolder.exists()) {
				for (final File fileEntry : MorePlayerModels.emoteFolder.listFiles()) {
					if (fileEntry.isDirectory()) {
						continue;
					} else {
						String emoteName = fileEntry.getName().substring(0, fileEntry.getName().length() - 4);
						list.add(emoteName);
					}
				}
			}

			//NOTE: we do not deduplicate emotes with the same name, it is assumed that this should not happen
			Server.sendData(player, EnumPackets.EMOTE_FILENAME_UPDATE, totalVaulted, list);
		} else if (type == EnumPackets.EMOTE_LOAD) {
			String emoteName = MorePlayerModels.validateFileName(Server.readString(buffer));
			if(emoteName == null) return;
			String filename = emoteName + ".dat";

			File file = new File(MorePlayerModels.emoteVaultFolder, filename);
			if (!file.exists()) {
				file = new File(MorePlayerModels.emoteFolder, filename);
				if (!file.exists()) return;
			}

			ByteBuf sendBuffer = Unpooled.buffer();
			try {
				sendBuffer.writeInt(EnumPackets.EMOTE_LOAD.ordinal());
				sendBuffer.writeBytes(new FileInputStream(file), (int)file.length());
				Server.sendData(player, sendBuffer);
			} catch(Exception e) {
				sendBuffer.release();
			}
		} else if (type == EnumPackets.EMOTE_SAVE) {
			String emoteName = MorePlayerModels.validateFileName(Server.readString(buffer));
			if(emoteName == null) return;
			Emote emote = Emote.readEmote(buffer);
			if(emote == null) return;

			String filename = emoteName + ".dat";

			File vaultfile = new File(MorePlayerModels.emoteVaultFolder, filename);
			if(vaultfile.exists()) return;

			if (!MorePlayerModels.emoteFolder.exists()) MorePlayerModels.emoteFolder.mkdirs();

			File file = new File(MorePlayerModels.emoteFolder, filename);

			if(file.exists()) {//save backup
				if (!MorePlayerModels.emoteArchiveFolder.exists()) MorePlayerModels.emoteArchiveFolder.mkdirs();

				String filenamenew = emoteName + "-" + System.currentTimeMillis() + ".dat";
				File filenew = new File(MorePlayerModels.emoteArchiveFolder, filenamenew);

				file.renameTo(filenew);
			}

			FileOutputStream out = new FileOutputStream(file);
			ByteBuf filedata = Unpooled.buffer();
			try {
				Emote.writeEmote(filedata, emote);
				byte[] rawdata = filedata.array();
				out.write(rawdata);
			} finally {
				filedata.release();
			}
		} else if (type == EnumPackets.EMOTE_REMOVE) {
			String emoteName = MorePlayerModels.validateFileName(Server.readString(buffer));
			if(emoteName == null) return;

			String filename = emoteName + ".dat";

			if (!MorePlayerModels.emoteFolder.exists()) return;

			File file = new File(MorePlayerModels.emoteFolder, filename);
			if(file.exists()) {//save copy
				if (!MorePlayerModels.emoteArchiveFolder.exists()) MorePlayerModels.emoteArchiveFolder.mkdirs();

				String filenamenew = emoteName + "-" + System.currentTimeMillis() + ".dat";
				File filenew = new File(MorePlayerModels.emoteArchiveFolder, filenamenew);

				boolean succ = file.renameTo(filenew);
			}
		} else if (type == EnumPackets.ENTITY_SCALE_MULT) {
			String name = Server.readString(buffer);

			Float mult = EntityScaleManagerServer.getScaleMult(name);

			NBTTagCompound compound = new NBTTagCompound();
			compound.setString("name", name);
			compound.setFloat("mult", mult);

			Server.sendData(player, EnumPackets.ENTITY_SCALE_MULT, compound);
		}
	}
}
