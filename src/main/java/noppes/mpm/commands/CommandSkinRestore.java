package noppes.mpm.commands;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.mpm.LogWriter;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;

public class CommandSkinRestore extends CommandBase {

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {

		if (args.length == 0) throw new WrongUsageException(this.getCommandUsage(icommandsender));

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

			if (file.exists()) {
				icommandsender.addChatMessage(new TextComponentTranslation("The skin " + args[0] + " is still present on the server."));
				return;
			}

			dir = null;
			dir = new File(dir, MorePlayerModels.assetRootFolder + File.separator + "skins" + File.separator + "archive");

			long timeStamp = 0;

			for (final File fileEntry : dir.listFiles()) {
				if (fileEntry.isDirectory()) {
					continue;
				} else {
					if (fileEntry.getName().startsWith(args[0].toLowerCase() + "-")) {
						String fileName = new String(fileEntry.getName());
						long timeStampTemp = Long.valueOf(fileName.replace(args[0].toLowerCase() + "-", "").replace(".dat", ""));

						if (timeStampTemp > timeStamp) {
							timeStamp = timeStampTemp;
							file = fileEntry;
						}
					}
				}
			}

			if (file.exists()) {
				File dirnew = null;
				dirnew = new File(dirnew, MorePlayerModels.assetRootFolder + File.separator + "skins" + File.separator + "unlisted");
				File filenew = new File(dirnew, filename);
				file.renameTo(filenew);
				icommandsender.addChatMessage(new TextComponentTranslation("The skin " + args[0] + " was restored. It is currently not listed in the skin load menu."));
			} else {
				icommandsender.addChatMessage(new TextComponentTranslation("The skin " + args[0] + " was not found in the archive. Please check the spelling or contact a developer."));
			}

		} catch (Exception var4) {
			LogWriter.except(var4);
		}
	}

	@Override
	public String getCommandName() {
		return "skinrestore";
	}

	@Override
	public String getCommandUsage(ICommandSender arg0) {
		return "/skinrestore <name>";
	}
}
