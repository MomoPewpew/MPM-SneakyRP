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
		//TODO: add error messages
		if (args.length == 0) {
			Server.sendToAll(server, EnumPackets.EMOTE_END, icommandsender.getName());
			return;
		}
		String emoteName = MorePlayerModels.validateFileName(args[0]);
		if(emoteName == null) {
			icommandsender.addChatMessage(new TextComponentTranslation("Invalid Emote Name."));
			return;
		}

		if(!Emote.serverDoEmote(icommandsender.getServer(), emoteName, icommandsender.getName())) {
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
