package noppes.mpm.commands;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

import noppes.mpm.LogWriter;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.Emote;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;


public class CommandEmote extends CommandBase {

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {
		if (args.length == 0) {
			Server.sendToAll(server, EnumPackets.EMOTE_END, icommandsender.getName());
			return;
		}
		String emoteName = MorePlayerModels.validateFileName(args[0]);
		if(emoteName == null) {
			icommandsender.addChatMessage(new TextComponentTranslation("Invalid Emote Name."));
			return;
		}
		float speed = 1.0f;
		if (args.length >= 2) {
			try {
				speed = Float.parseFloat(args[1].replace(',', '.'));
				speed = Math.max(.0001F, Math.min(10000F, speed));
			} catch (NumberFormatException e) {
				speed = 1.0f;
			}
		}

		if(!Emote.serverDoEmote(icommandsender.getServer(), emoteName, icommandsender.getName(), speed)) {
			//TODO: check for and report corrupted emotes
			icommandsender.addChatMessage(new TextComponentTranslation("The Emote " + emoteName + " was not found on the server."));
		}
	}

	@Override
	public String getCommandName() {
		return "e";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/e [<name>]";
	}


}
