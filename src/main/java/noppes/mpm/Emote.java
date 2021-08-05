package noppes.mpm;

import aurelienribon.tweenengine.*;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;

import net.minecraft.server.MinecraftServer;
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
	public static final int HEAD = 0;
	public static final int BODY = 1;
	public static final int RIGHT_ARM = 2;
	public static final int LEFT_ARM = 3;
	public static final int RIGHT_LEG = 4;
	public static final int LEFT_LEG = 5;
	public static final int MODEL = 6;

	public static final int OFF_X = 0;
	public static final int OFF_Y = 1;
	public static final int OFF_Z = 2;
	public static final int ROT_X = 3;
	public static final int ROT_Y = 4;
	public static final int ROT_Z = 5;

	public static final int INTRO_OFFSET = 0;
	public static final int INTRO_ROTATE = 1;
	public static final int LOOP_OFFSET = 2;
	public static final int LOOP_ROTATE = 3;

	public static final int AXIS_COUNT = 6;
	public static final int PART_COUNT = 7;
	public static final int STATE_COUNT = AXIS_COUNT*PART_COUNT;
	public static final int COMMAND_LIST_COUNT = 4;

	public static final String[] BODY_PARTS = {
		"head", "body", "rightarm", "leftarm", "rightleg", "leftleg", "model"
	};

	public static final float maxOffset = 1000F;
	public static final float maxRotate = (float)Math.toRadians(36000F);
	public static final int infDuration = 7*24*60*60;
	public static final float maxDuration = 60*60;
	public static final float minDuration = .000001F;//NOTE: we can't enforce this just yet for backwards compatibility

	public ArrayList<ArrayList<PartCommand>> commands;

	public Emote() {
		this.commands = new ArrayList<ArrayList<PartCommand>>(COMMAND_LIST_COUNT*PART_COUNT);
		for(int i = 0; i < COMMAND_LIST_COUNT*PART_COUNT; i++) {
			this.commands.add(null);
		}
	}
	///////////////////
	// commands = {head_intro_offset, head_intro_rotate, head_loop_offset, head_loop_rotate, body_intro_offset, ...}

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

	public boolean partIsUsed(int partId) {
		partId *= COMMAND_LIST_COUNT;
		return this.commands.get(partId + 0) != null || this.commands.get(partId + 1) != null || this.commands.get(partId + 2) != null || this.commands.get(partId + 3) != null;
	}
	public boolean partIsRotate(int partId) {
		partId *= COMMAND_LIST_COUNT;
		return this.commands.get(partId + INTRO_ROTATE) != null || this.commands.get(partId + LOOP_ROTATE) != null;
	}
	public boolean partIsOffset(int partId) {
		partId *= COMMAND_LIST_COUNT;
		return this.commands.get(partId + INTRO_OFFSET) != null || this.commands.get(partId + LOOP_OFFSET) != null;
	}

	public static void writeEmote(ByteBuf buffer, Emote emote) {
		buffer.writeInt(1);//encoding version number
		int size = 0;
		for(int i = 0; i < PART_COUNT; i++) {
			if(emote.partIsUsed(i)) {
				size += 1;
			}
		}
		buffer.writeInt(size);
		for(int i = 0; i < PART_COUNT; i++) {
			if(emote.partIsUsed(i)) {
				buffer.writeInt(i);
				writePartCommandList(buffer, emote.commands.get(COMMAND_LIST_COUNT*i + INTRO_OFFSET));
				writePartCommandList(buffer, emote.commands.get(COMMAND_LIST_COUNT*i + INTRO_ROTATE));
				writePartCommandList(buffer, emote.commands.get(COMMAND_LIST_COUNT*i + LOOP_OFFSET));
				writePartCommandList(buffer, emote.commands.get(COMMAND_LIST_COUNT*i + LOOP_ROTATE));
			}
		}
	}
	public static void writePartCommandList(ByteBuf buffer, ArrayList<PartCommand> list) {
		if(list == null) {
			buffer.writeInt(0);
		} else {
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
	}
	public static Emote readEmote(ByteBuf buffer) {
		try {
			if(buffer.readInt() != 1) return null;//verify version number
			Emote emote = new Emote();
			int size = buffer.readInt();
			if(size > PART_COUNT) return null;//verify parts total
			for(int i = 0; i < size; i++) {
				int partId = buffer.readInt();
				if(partId >= PART_COUNT) return null;

				partId *= COMMAND_LIST_COUNT;
				emote.commands.set(partId + INTRO_OFFSET, readPartCommandList(buffer));
				emote.commands.set(partId + INTRO_ROTATE, readPartCommandList(buffer));
				emote.commands.set(partId + LOOP_OFFSET, readPartCommandList(buffer));
				emote.commands.set(partId + LOOP_ROTATE, readPartCommandList(buffer));
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
		if(size == 0) return null;
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

	public static boolean isValidEmote(Emote emote) {
		for(int i = 0; i < PART_COUNT; i++) {
			if(!isValidPartCommandList(emote.commands.get(COMMAND_LIST_COUNT*i + INTRO_OFFSET), maxOffset)) return false;
			if(!isValidPartCommandList(emote.commands.get(COMMAND_LIST_COUNT*i + INTRO_ROTATE), maxRotate)) return false;
			if(!isValidPartCommandList(emote.commands.get(COMMAND_LIST_COUNT*i + LOOP_OFFSET), maxOffset)) return false;
			if(!isValidPartCommandList(emote.commands.get(COMMAND_LIST_COUNT*i + LOOP_ROTATE), maxRotate)) return false;
		}
		return true;
	}
	public static boolean isValidPartCommandList(ArrayList<PartCommand> list, float maxCoordRange) {
		if(list == null) return true;
		if(list.size() == 0) return false;
		for(int i = 0; i < list.size(); i++) {
			PartCommand command = list.get(i);
			if(Math.abs(command.x) >= maxCoordRange) return false;
			if(Math.abs(command.y) >= maxCoordRange) return false;
			if(Math.abs(command.z) >= maxCoordRange) return false;
			if(command.duration < 0 || command.duration >= maxDuration) return false;
			if(command.easing < 0 || command.easing >= TweenUtils.easings.length) return false;
		}
		return true;
	}

	public static boolean serverDoEmote(MinecraftServer server, String emoteName, String playerName, float emoteSpeed) {
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
			sendBuffer.writeFloat(emoteSpeed);
			Server.writeString(sendBuffer, playerName);
			sendBuffer.writeBytes(new FileInputStream(file), (int)file.length());

			Server.sendToAll(server, sendBuffer);
			return true;
		} catch (Exception var4) {
			return false;
		}
	}
}
