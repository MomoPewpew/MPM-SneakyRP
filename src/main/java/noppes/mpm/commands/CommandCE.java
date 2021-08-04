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


public class CommandCE extends CommandBase {

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {
		Server.sendToAll(server, EnumPackets.EMOTE_END, icommandsender.getName());
		return;
	}

	@Override
	public String getCommandName() {
		return "ce";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/ce";
	}


}
