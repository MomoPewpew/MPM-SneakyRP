package noppes.mpm.commands;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandBase;
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
import noppes.mpm.MorePlayerModels;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;

public class CommandPropRestore extends CommandBase {

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {

		if (args.length == 0) return;

		String filename = args[0].toLowerCase() + ".dat";
		File file;

		try {
			File dir = null;

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

			if (file.exists()) {
				icommandsender.addChatMessage(new TextComponentTranslation("The prop " + args[0] + " is still present on the server."));
				return;
			}

			dir = null;
			dir = new File(dir, ".." + File.separator + "moreplayermodels" + File.separator + "propGroupsNamed" + File.separator + "archive");

			long timeStamp = 0;

			for (final File fileEntry : dir.listFiles()) {
				if (fileEntry.isDirectory()) {
					continue;
				} else {
					if (fileEntry.getName().toLowerCase().startsWith(args[0].toLowerCase() + "-")) {
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
				dirnew = new File(dirnew, ".." + File.separator + "moreplayermodels" + File.separator + "propGroupsNamed");
				File filenew = new File(dirnew, filename);
				file.renameTo(filenew);
				icommandsender.addChatMessage(new TextComponentTranslation("The prop " + args[0] + " was restored. If this was not the right prop, please contact a developer."));
			} else {
				icommandsender.addChatMessage(new TextComponentTranslation("The prop " + args[0] + " was not found in the archive. Please check the spelling or contact a developer."));
			}

		} catch (Exception var4) {
			LogWriter.except(var4);
		}
	}

	@Override
	public String getCommandName() {
		return "proprestore";
	}

	@Override
	public String getCommandUsage(ICommandSender arg0) {
		return "/proprestore <name>";
	}
}
