package noppes.mpm.commands;

import java.io.File;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

// import noppes.mpm.LogWriter;
import noppes.mpm.MorePlayerModels;


public class CommandUnvault extends MpmCommandInterface {

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {
		if(args.length == 0) {
			icommandsender.addChatMessage(new TextComponentTranslation("No Emote Name."));
			return;
		}
		String emoteName = MorePlayerModels.validateFileName(args[0]);
		if(emoteName == null) {
			icommandsender.addChatMessage(new TextComponentTranslation("Invalid Emote Name."));
			return;
		}

		String filename = emoteName + ".dat";

		try {
			File vaultfile = new File(MorePlayerModels.emoteVaultFolder, filename);

			if(vaultfile.exists()) {
				File file = new File(MorePlayerModels.emoteVaultFolder, filename);

				if (!MorePlayerModels.emoteVaultFolder.exists()) MorePlayerModels.emoteVaultFolder.mkdirs();

				if(file.exists()) {//save backup
					if (!MorePlayerModels.emoteArchiveFolder.exists()) MorePlayerModels.emoteArchiveFolder.mkdirs();

					String filenamenew = emoteName + "-" + System.currentTimeMillis() + ".dat";
					File filenew = new File(MorePlayerModels.emoteArchiveFolder, filenamenew);

					file.renameTo(filenew);
				}

				if(!vaultfile.renameTo(file)) {
					icommandsender.addChatMessage(new TextComponentTranslation("Failed to transfer emote file."));
				}
			} else {
				icommandsender.addChatMessage(new TextComponentTranslation("The Emote " + emoteName + " does not exist."));
			}
		} catch (Exception var4) {
			icommandsender.addChatMessage(new TextComponentTranslation("The Emote " + emoteName + " could not be opened."));
		}
	}

	@Override
	public String getCommandName() {
		return "unvault";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/unvault [<emote name>]";
	}


}
