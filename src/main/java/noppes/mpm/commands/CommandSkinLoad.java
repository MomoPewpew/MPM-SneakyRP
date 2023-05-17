package noppes.mpm.commands;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.mpm.LogWriter;
import noppes.mpm.ModelData;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;

public class CommandSkinLoad extends MpmCommandInterface {

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {

		if (args.length == 0) {
			MorePlayerModels.syncSkinFileNames((EntityPlayerMP) icommandsender);
			Server.sendData((EntityPlayerMP) icommandsender, EnumPackets.SKIN_LOAD_GUI);
			return;
		}

		File dir = null;

		String filename = args[0].toLowerCase() + ".dat";

		try {
			dir = new File(dir, MorePlayerModels.assetRootFolder + File.separator + "skins" + File.separator + "listed" + File.separator + "unrestricted");
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File file = new File(dir, filename);

			if (!file.exists()) {
				dir = null;
				dir = new File(dir, MorePlayerModels.assetRootFolder + File.separator + "skins" + File.separator + "listed" + File.separator + "restricted");
				if (!dir.exists()) {
					dir.mkdirs();
				}

				file = new File(dir, filename);
			}

			if (!file.exists()) {
				dir = null;
				dir = new File(dir, MorePlayerModels.assetRootFolder + File.separator + "skins" + File.separator + "listed");
				if (!dir.exists()) {
					dir.mkdirs();
				}

				file = new File(dir, filename);
			}

			if (!file.exists()) {
				dir = null;
				dir = new File(dir, MorePlayerModels.assetRootFolder + File.separator + "skins" + File.separator + "unlisted" + File.separator + "unrestricted");
				if (!dir.exists()) {
					dir.mkdirs();
				}

				file = new File(dir, filename);
			}

			if (!file.exists()) {
				dir = null;
				dir = new File(dir, MorePlayerModels.assetRootFolder + File.separator + "skins" + File.separator + "unlisted" + File.separator + "restricted");
				if (!dir.exists()) {
					dir.mkdirs();
				}

				file = new File(dir, filename);
			}

			if (!file.exists()) {
				dir = null;
				dir = new File(dir, MorePlayerModels.assetRootFolder + File.separator + "skins" + File.separator + "unlisted");
				if (!dir.exists()) {
					dir.mkdirs();
				}

				file = new File(dir, filename);
			}

			NBTTagCompound compound = new NBTTagCompound();

			if (!file.exists()) {
				icommandsender.addChatMessage(new TextComponentTranslation("The skin " + args[0] + " was not found on the server."));
				return;
			} else {
				compound = CompressedStreamTools.readCompressed(new FileInputStream(file));
			}

			ModelData data = ModelData.get((EntityPlayer) icommandsender);
			data.readFromNBT(compound);
			Server.sendAssociatedData((Entity) icommandsender, EnumPackets.SEND_PLAYER_DATA, ((Entity) icommandsender).getUniqueID(), compound);
		} catch (Exception var4) {
			LogWriter.except(var4);
		}
	}

	@Override
	public String getCommandName() {
		return "skinload";
	}

	@Override
	public String getCommandUsage(ICommandSender arg0) {
		return "/skinload <name>";
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getCommandAliases()
	{
		return new ArrayList<String>(Arrays.asList(
		"skinload",
		"sl"
		));
	}
}
