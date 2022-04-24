package noppes.mpm.commands;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.EntitySelectors;

import noppes.mpm.LogWriter;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.Emote;
import noppes.mpm.ModelData;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;


public class CommandENear extends CommandBase {

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {
		if(!(icommandsender instanceof EntityPlayerMP)) return;

		EntityPlayerMP player = getClosestPlayer((EntityPlayerMP)icommandsender);
		if(player == null) {
			icommandsender.addChatMessage(new TextComponentTranslation("No target found."));
			return;
		}

		ModelData data = ModelData.get(player);

		if(args.length == 0) {
			data.updateEmote();
			data.endEmotes(false);
			Server.sendAssociatedData(player, EnumPackets.EMOTE_END, player.getUniqueID(), false);
			return;
		}
		String emoteName = MorePlayerModels.validateFileName(args[0]);
		if(emoteName == null) {
			icommandsender.addChatMessage(new TextComponentTranslation("Invalid Emote Name."));
			return;
		}
		float emoteSpeed = 1.0f;
		if (args.length > 1) {
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
		if (args.length > 2) {
			outro_all_playing_first = args[2].toLowerCase().equals("true") || args[2].equals("1");
		}
		if (args.length > 3) {
			override_instead_of_outro = args[3].toLowerCase().equals("true") || args[3].equals("1");
		}
		if (args.length > 4) {
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
				sendBuffer.writeBytes(new FileInputStream(file), (int)file.length());
				Emote emote = Emote.readEmote(sendBuffer);
				if(emote != null) {
					LogWriter.info("DO COMMAND " + emote.toString());

					data.updateEmote();
					data.startEmote(emote, emoteSpeed, cancel_if_conflicting, outro_all_playing_first, override_instead_of_outro);

					sendBuffer.clear();
					sendBuffer.writeInt(EnumPackets.EMOTE_DO.ordinal());

					UUID uuid = player.getUniqueID();
					sendBuffer.writeLong(uuid.getMostSignificantBits());
					sendBuffer.writeLong(uuid.getLeastSignificantBits());

					Emote.writeEmote(sendBuffer, emote);

					sendBuffer.writeFloat(emoteSpeed);
					sendBuffer.writeBoolean(cancel_if_conflicting);
					sendBuffer.writeBoolean(outro_all_playing_first);
					sendBuffer.writeBoolean(override_instead_of_outro);

					Server.sendAssociatedData(player, sendBuffer);
					LogWriter.info("DO COMMAND SENT ");
				} else {
					icommandsender.addChatMessage(new TextComponentTranslation("The Emote " + emoteName + " is corrupted."));
					sendBuffer.release();
				}
			} catch(Exception e) {//i do not like exceptions
				icommandsender.addChatMessage(new TextComponentTranslation("The Emote " + emoteName + " could not be opened."));
				sendBuffer.release();
			}
		} catch (Exception var4) {
			icommandsender.addChatMessage(new TextComponentTranslation("The Emote " + emoteName + " could not be opened."));
		}
	}

	private static EntityPlayerMP getClosestPlayer(final EntityPlayerMP player) {
		EntityPlayerMP closest = null;
		double closestDistance = 0;

		for (EntityPlayerMP entity : player.getEntityWorld().getEntities(EntityPlayerMP.class, EntitySelectors.NOT_SPECTATING)) {
			if (entity == player || !(entity instanceof EntityPlayerMP)) {
				continue;
			}

			double distance = entity.getPosition().distanceSq(player.getPosition());
			if ((closest == null || distance < closestDistance) && Math.sqrt(distance) <= 3) {
				closest = entity;
				closestDistance = distance;
			}
		}

		return closest;
	}


	@Override
	public String getCommandName() {
		return "enear";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/enear [<emote name>] [<speed>] [end all emotes first <true/false>] [override instead of playing outro <true/false>] [cancel if conflicting <true/false>]";
	}


}
