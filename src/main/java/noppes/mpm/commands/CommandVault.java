package noppes.mpm.commands;

import java.io.File;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

// import noppes.mpm.LogWriter;
import noppes.mpm.MorePlayerModels;


public class CommandVault extends MpmCommandInterface {

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
			File file = new File(MorePlayerModels.emoteFolder, filename);

			if(file.exists()) {
				File vaultfile = new File(MorePlayerModels.emoteVaultFolder, filename);

				if (!MorePlayerModels.emoteVaultFolder.exists()) MorePlayerModels.emoteVaultFolder.mkdirs();

				if(vaultfile.exists()) {//save backup
					if (!MorePlayerModels.emoteArchiveFolder.exists()) MorePlayerModels.emoteArchiveFolder.mkdirs();

					String filenamenew = emoteName + "-" + System.currentTimeMillis() + ".dat";
					File filenew = new File(MorePlayerModels.emoteArchiveFolder, filenamenew);

					vaultfile.renameTo(filenew);
				}

				if(!file.renameTo(vaultfile)) {
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
		return "vault";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/vault [<emote name>]";
	}


}
