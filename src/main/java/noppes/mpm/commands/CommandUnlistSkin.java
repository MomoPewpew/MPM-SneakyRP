package noppes.mpm.commands;

import java.io.File;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.mpm.LogWriter;
import noppes.mpm.MorePlayerModels;

public class CommandUnlistSkin extends CommandBase {

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {

		if (args.length == 0) throw new WrongUsageException(this.getCommandUsage(icommandsender));

		File dir = null;

		String filename = args[0].toLowerCase() + ".dat";

		try {
			dir = new File(dir, MorePlayerModels.assetRootFolder + File.separator + "skins" + File.separator + "unlisted");
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File file = new File(dir, filename);

			if (!file.exists()) {
				dir = null;
				dir = new File(dir, MorePlayerModels.assetRootFolder + File.separator + "skins" + File.separator + "listed");
				if (!dir.exists()) {
					dir.mkdirs();
				}

				file = new File(dir, filename);
			}

			if (!file.exists()) {
				icommandsender.addChatMessage(new TextComponentTranslation("The skin " + args[0] + " was not found on the server."));
				return;
			}

			File dirnew = null;
			dirnew = new File(dirnew, MorePlayerModels.assetRootFolder + File.separator + "skins" + File.separator + "unlisted");

			if (!dirnew.exists()) {
				dirnew.mkdirs();
			}

			File filenew = new File(dirnew, filename);

			file.renameTo(filenew);
			icommandsender.addChatMessage(new TextComponentTranslation("The skin " + args[0] + " is no longer listed in the skin load menu. Use '/listskin <name>' to list it again."));
		} catch (Exception var6) {
			LogWriter.except(var6);
			var6.printStackTrace();
		}
	}

	@Override
	public String getCommandName() {
		return "unlistskin";
	}

	@Override
	public String getCommandUsage(ICommandSender arg0) {
		return "/unlistskin <name>";
	}

}
