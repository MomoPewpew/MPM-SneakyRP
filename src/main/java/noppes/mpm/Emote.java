package noppes.mpm;

import aurelienribon.tweenengine.*;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;
import noppes.mpm.client.model.ModelAccessor;

import net.minecraft.server.MinecraftServer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Emote {

	public static class PartCommand {
		public float x = 0.0F;
		public float y = 0.0F;
		public float z = 0.0F;
		public float duration = 1.0F;
		public int easing = TweenUtils.LINEAR_INOUT;
		public boolean disabled = false;

		public PartCommand clone() {
			PartCommand command = new PartCommand();
			command.x = x;
			command.y = y;
			command.z = z;
			command.duration = duration;
			command.easing = easing;
			command.disabled = disabled;
			return command;
		}
	}

	public static class PartCommands {
		public ArrayList<PartCommand> intro_offset = new ArrayList<PartCommand>();
		public ArrayList<PartCommand> intro_rotate = new ArrayList<PartCommand>();
		public ArrayList<PartCommand> loop_offset = new ArrayList<PartCommand>();
		public ArrayList<PartCommand> loop_rotate = new ArrayList<PartCommand>();

		public ArrayList<PartCommand> getCommandList(boolean isintro, boolean isoffset) {//in c I could do this trivially and efficiently with a union
			if(isintro) {
				if(isoffset) {
					return this.intro_offset;
				} else {
					return this.intro_rotate;
				}
			} else {
				if(isoffset) {
					return this.loop_offset;
				} else {
					return this.loop_rotate;
				}
			}
		}
	}

	public static final float maxOffset = 1000F;
	public static final float maxRotate = (float)Math.toRadians(36000F);
	// public static final float maxTotalDuration = 60*60;
	public static final float maxDuration = 60*60;
	public static final int maxPartNameLength = 500;

	public HashMap<String, PartCommands> commands = new HashMap<String, PartCommands>();

	public static final String[] bipedParts = {
		"leftarm", "rightarm", "head", "body", "leftleg", "rightleg", "model"
	};

	public static void writeEmote(ByteBuf buffer, Emote emote) {
		buffer.writeInt(1);//encoding version number
		buffer.writeInt(emote.commands.size());
		for(Map.Entry<String, PartCommands> entry : emote.commands.entrySet()) {
			Server.writeString(buffer, entry.getKey());
			PartCommands partCommands = entry.getValue();
			writePartCommandList(buffer, partCommands.intro_offset);
			writePartCommandList(buffer, partCommands.intro_rotate);
			writePartCommandList(buffer, partCommands.loop_offset);
			writePartCommandList(buffer, partCommands.loop_rotate);
		}
	}
	public static void writePartCommandList(ByteBuf buffer, ArrayList<PartCommand> list) {
		buffer.writeInt(list.size());
		for(int i = 0; i < list.size(); i++) {
			PartCommand command = list.get(i);
			buffer.writeFloat(command.x);
			buffer.writeFloat(command.y);
			buffer.writeFloat(command.z);
			buffer.writeFloat(command.duration);
			buffer.writeInt(2*command.easing + (command.disabled ? 1 : 0));
		}
	}
	public static Emote readEmote(ByteBuf buffer) {
		try {
			if(buffer.readInt() != 1) return null;//verify version number
			Emote emote = new Emote();
			int size = buffer.readInt();
			for(int i = 0; i < size; i++) {
				String key = Server.readString(buffer);
				if(key == null) return null;
				PartCommands partCommands = new PartCommands();
				partCommands.intro_offset = readPartCommandList(buffer);
				partCommands.intro_rotate = readPartCommandList(buffer);
				partCommands.loop_offset = readPartCommandList(buffer);
				partCommands.loop_rotate = readPartCommandList(buffer);
				emote.commands.put(key, partCommands);
			}
			if(isValidEmote(emote)) {
				return emote;
			} else {
				return null;
			}
		} catch (IndexOutOfBoundsException var2) {
			return null;
		}
	}
	public static ArrayList<PartCommand> readPartCommandList(ByteBuf buffer) {
		int size = buffer.readInt();
		ArrayList<PartCommand> list = new ArrayList<PartCommand>(size);
		for(int i = 0; i < size; i++) {
			PartCommand command = new PartCommand();
			command.x = buffer.readFloat();
			command.y = buffer.readFloat();
			command.z = buffer.readFloat();
			command.duration = buffer.readFloat();
			int a = buffer.readInt();
			command.easing = a/2;
			command.disabled = (a%2 == 1);
			list.add(command);
		}
		return list;
	}

	public static boolean isValidEmote(Emote emote) {//TODO: make this a mandatory check
		for(Map.Entry<String, PartCommands> entry : emote.commands.entrySet()) {
			if(entry.getKey().length() >= maxPartNameLength) return false;
			PartCommands partCommands = entry.getValue();
			if(!isValidPartCommandList(partCommands.intro_offset, maxOffset)) return false;
			if(!isValidPartCommandList(partCommands.intro_rotate, maxRotate)) return false;
			if(!isValidPartCommandList(partCommands.loop_offset, maxOffset)) return false;
			if(!isValidPartCommandList(partCommands.loop_rotate, maxRotate)) return false;
		}
		return true;
	}
	public static boolean isValidPartCommandList(ArrayList<PartCommand> list, float maxCoordRange) {
		// float totalDuration = 0;
		for(int i = 0; i < list.size(); i++) {
			PartCommand command = list.get(i);
			if(Math.abs(command.x) >= maxCoordRange) return false;
			if(Math.abs(command.y) >= maxCoordRange) return false;
			if(Math.abs(command.z) >= maxCoordRange) return false;
			if(command.duration < 0 || command.duration >= maxDuration) return false;
			if(command.easing < 0 || command.easing >= TweenUtils.easings.length) return false;
			// totalDuration += command.duration;
		}
		// return (totalDuration <= maxTotalDuration);
		return true;
	}

	public static boolean serverDoEmote(MinecraftServer server, String emoteName, String playerName) {
		String filename = emoteName + ".dat";

		try {
			File dir = null;
			dir = new File(dir, ".." + File.separator + "moreplayermodels" + File.separator + "emotes");

			if (!dir.exists()) {
				dir.mkdirs();
				return false;
			}

			File file = new File(dir, filename);
			if (!file.exists()) return false;

			ByteBuf sendBuffer = Unpooled.buffer();
			sendBuffer.writeInt(EnumPackets.EMOTE_DO.ordinal());
			Server.writeString(sendBuffer, playerName);
			sendBuffer.writeBytes(new FileInputStream(file), (int)file.length());

			Server.sendToAll(server, sendBuffer);
			return true;
		} catch (Exception var4) {
			// LogWriter.except(var4);
			return false;
		}
	}
}
