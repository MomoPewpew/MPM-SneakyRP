package noppes.mpm.commands;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

// import noppes.mpm.LogWriter;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.Emote;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;


public class CommandEmote extends CommandBase {

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {
		if(args.length == 0) {
			Server.sendToAll(server, EnumPackets.EMOTE_END, icommandsender.getName(), false);
			return;
		}
		String emoteName = MorePlayerModels.validateFileName(args[0]);
		if(emoteName == null) {
			icommandsender.addChatMessage(new TextComponentTranslation("Invalid Emote Name."));
			return;
		}
		float emoteSpeed = 1.0f;
		if (args.length >= 2) {
			try {
				emoteSpeed = Float.parseFloat(args[1].replace(',', '.'));
				emoteSpeed = Math.max(.0001F, Math.min(10000F, emoteSpeed));
			} catch (NumberFormatException e) {
				emoteSpeed = 1.0f;
			}
		}

		boolean cancel_if_conflicting = false;
		boolean outro_all_playing_first = false;
		boolean override_instead_of_outro = false;
		if (args.length >= 3) {
			outro_all_playing_first = args[2].toLowerCase().equals("true") || args[2].equals("1");
		}
		if (args.length >= 4) {
			override_instead_of_outro = args[3].toLowerCase().equals("true") || args[3].equals("1");
		}
		if (args.length >= 5) {
			cancel_if_conflicting = args[4].toLowerCase().equals("true") || args[4].equals("1");
		}

		String filename = emoteName + ".dat";

		try {
			File file = new File(MorePlayerModels.emoteVaultFolder, filename);
			if (!file.exists()) {
				file = new File(MorePlayerModels.emoteFolder, filename);
				if (!file.exists()) {
					icommandsender.addChatMessage(new TextComponentTranslation("The Emote " + emoteName + " was not found on the server."));
					return;
				}
			}

			//NOTE: maybe we should open a seperate thread to handle sending the emote data
			ByteBuf sendBuffer = Unpooled.buffer();
			try {
				sendBuffer.writeInt(EnumPackets.EMOTE_DO.ordinal());
				sendBuffer.writeFloat(emoteSpeed);
				sendBuffer.writeBoolean(cancel_if_conflicting);
				sendBuffer.writeBoolean(outro_all_playing_first);
				sendBuffer.writeBoolean(override_instead_of_outro);
				sendBuffer.writeBoolean(false);
				Server.writeString(sendBuffer, icommandsender.getName());

				sendBuffer.writeBytes(new FileInputStream(file), (int)file.length());
				Server.sendToAll(server, sendBuffer);
			} catch(Exception e) {//i do not like exceptions
				icommandsender.addChatMessage(new TextComponentTranslation("The Emote " + emoteName + " could not be opened."));
				sendBuffer.release();
			}
		} catch (Exception var4) {
			icommandsender.addChatMessage(new TextComponentTranslation("The Emote " + emoteName + " could not be opened."));
		}
	}

	@Override
	public String getCommandName() {
		return "e";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/e [<emote name>] [<speed>] [end all emotes first <true/false>] [override instead of playing outro <true/false>] [cancel if conflicting <true/false>]";
	}


}
