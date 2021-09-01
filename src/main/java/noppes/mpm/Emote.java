package noppes.mpm;

import aurelienribon.tweenengine.*;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;
import noppes.mpm.LogWriter;

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
	//NOTE: All of these enums have precisely chosen values, consult their usage before attempting to change any
	//PART ENUMS
	public static final int HEAD = 0;
	public static final int BODY = 1;
	public static final int RIGHT_ARM = 2;
	public static final int LEFT_ARM = 3;
	public static final int RIGHT_LEG = 4;
	public static final int LEFT_LEG = 5;
	public static final int MODEL = 6;

	//AXIS ENUMS
	public static final int OFF_X = 0;
	public static final int OFF_Y = 1;
	public static final int OFF_Z = 2;
	public static final int ROT_X = 3;
	public static final int ROT_Y = 4;
	public static final int ROT_Z = 5;

	//COMMAND LIST ENUMS
	public static final int INTRO_OFFSET = 0;
	public static final int LOOP_OFFSET  = 1;
	public static final int OUTRO_OFFSET = 2;
	public static final int INTRO_ROTATE = 3;
	public static final int LOOP_ROTATE  = 4;
	public static final int OUTRO_ROTATE = 5;

	//PART USAGE FLAGS
	public static final int FLAG_USED = 1<<0;
	public static final int FLAG_LOOP_ONLY_STOPS_AT_BOUNDARY = 1<<1;
	public static final int FLAG_LOOP_PAUSES_WHEN_STILL = 1<<2;
	public static final int FLAG_OUTRO_PLAYS_WHEN_STILL = 1<<3;
	public static final int FLAG_LOOP_ONLY_PAUSES_AT_BOUNDARY = 1<<4;
	public static final int FLAG_INVERT_MOVEMENT = 1<<5;
	public static final int FLAG_FOLLOWS_HEAD_ROTATION = 1<<6;
	public static final int FLAG_CAMERA_FOLLOWS_MODEL_OFFSET = 1<<6;

	public static final int FIRST_ANIMFLAG = 1<<24;
	public static final int ANIMFLAG_END_EMOTE = 1<<24;

	//META ENUM NUMBERS
	public static final int PART_COUNT = 7;
	public static final int AXIS_COUNT = 6;
	public static final int SECTION_LIST_COUNT = 6;
	public static final int STATE_COUNT = AXIS_COUNT*PART_COUNT;
	public static final int FAKE_EASING_ACTUALLY_A_CONSOLE_COMMAND = 100;

	public static final String[] BODY_PARTS = {
		"head", "body", "rightarm", "leftarm", "rightleg", "leftleg", "model"
	};


	//SANITY CHECK NUMBERS
	public static final float maxCoordRange = 2000F;
	public static final float maxDuration = 60*60;
	public static final int maxConsoleCommandSize = 2000;


	///////////////////
	// data format:
	// commands = {head_intro_offset, head_intro_rotate, head_loop_offset, head_loop_rotate, head_outro_offset, head_outro_rotate, body_intro_offset, ...}
	// head_intro_offset = {PartCommand0, PartCommand1, ...}
	// partUsages = {head_offset, head_rotate, body_offset, ...}

	public ArrayList<ArrayList<PartCommand>> commands = createCommandsList();
	public final int[] partUsages = new int[2*Emote.PART_COUNT];

	public static final ArrayList<ArrayList<PartCommand>> createCommandsList() {
		ArrayList<ArrayList<PartCommand>> commands = new ArrayList<ArrayList<PartCommand>>(SECTION_LIST_COUNT*PART_COUNT);
		for(int i = 0; i < SECTION_LIST_COUNT*PART_COUNT; i++) {
			commands.add(null);
		}
		return commands;
	}
	public static class PartCommand {
		public boolean disabled = false;
		public String consoleCommand = null;//if this is non-null, the rest of this struct is irrelevant

		public float x = 0.0F;
		public float y = 0.0F;
		public float z = 0.0F;
		public float duration = 0.5F;
		public int easing = TweenUtils.QUAD_INOUT;

		public PartCommand clone() {
			PartCommand command = new PartCommand();
			command.consoleCommand = consoleCommand;
			command.x = x;
			command.y = y;
			command.z = z;
			command.duration = duration;
			command.easing = easing;
			command.disabled = disabled;
			return command;
		}
	}

	public Emote clone() {
		Emote emote = new Emote();
		for(int i = 0; i < SECTION_LIST_COUNT*PART_COUNT; i++) {
			ArrayList<PartCommand> thisSectionList = this.commands.get(i);
			if(thisSectionList != null) {
				ArrayList<PartCommand> newSectionList = new ArrayList<PartCommand>(thisSectionList.size());
				for(int j = 0; j < thisSectionList.size(); j += 1) {
					newSectionList.add(thisSectionList.get(j).clone());
				}
				emote.commands.set(i, newSectionList);
			}
		}
		for(int meta_i = 0; meta_i < 2*Emote.PART_COUNT; meta_i += 1) {
			emote.partUsages[meta_i] = this.partUsages[meta_i];
		}
		return emote;
	}



	public static void writeEmote(ByteBuf buffer, Emote emote) {
		writeEmoteV2(buffer, emote);
	}
	public static Emote readEmote(ByteBuf buffer) {
		int version_no = buffer.readInt();
		if(version_no == 2) {
			return readEmoteV2(buffer);
		} else if(version_no == 1) {
			return readEmoteV1(buffer);
		} else {
			return null;
		}
	}

	public static boolean isValidEmote(Emote emote) {
		for(int partId = 0; partId < PART_COUNT; partId += 1) {
			for(int isRotate = 0; isRotate < 2; isRotate += 1) {
				int meta_i = 2*partId + isRotate;
				int intro_i = Emote.SECTION_LIST_COUNT*partId + 3*isRotate;
				int loop_i = intro_i + 1;
				int outro_i = loop_i + 1;
				if(emote.partUsages[meta_i] < 0 || emote.partUsages[meta_i] > FIRST_ANIMFLAG) return false;
				if(!isValidPartCommandList(emote.commands.get(intro_i))) return false;
				if(!isValidPartCommandList(emote.commands.get(loop_i))) return false;
				if(!isValidPartCommandList(emote.commands.get(outro_i))) return false;
			}
		}
		return true;
	}
	public static boolean isValidPartCommandList(ArrayList<PartCommand> list) {
		if(list == null) return true;
		if(list.size() == 0) return false;
		for(int i = 0; i < list.size(); i++) {
			PartCommand command = list.get(i);
			if(command.consoleCommand == null) {
				if(Math.abs(command.x) > maxCoordRange) return false;
				if(Math.abs(command.y) > maxCoordRange) return false;
				if(Math.abs(command.z) > maxCoordRange) return false;
				if(command.duration < 0 || command.duration > maxDuration) return false;
				if(command.easing < 0 || command.easing >= TweenUtils.easings.length) return false;
			} else if(command.consoleCommand.length() > maxConsoleCommandSize) {
				return false;
			}
		}
		return true;
	}


	public String toString() {
		String str = "{";
		for(int partId = 0; partId < PART_COUNT; partId += 1) {
			for(int isRotate = 0; isRotate < 2; isRotate += 1) {
				int meta_i = 2*partId + isRotate;

				if(this.partUsages[meta_i] > 0) {
					int intro_i = Emote.SECTION_LIST_COUNT*partId + 3*isRotate;
					int loop_i = intro_i + 1;
					int outro_i = loop_i + 1;
					str += "(" + partId + ", " + isRotate + ") = {" + this.partUsages[meta_i] + ", ";
					str += sectionListToString(this.commands.get(intro_i)) + ", ";
					str += sectionListToString(this.commands.get(loop_i)) + ", ";
					str += sectionListToString(this.commands.get(outro_i));
					str += "}, ";
				}
			}
		}
		return str.substring(0, Math.max(1, str.length() - 2)) + "}";
	}
	public static String sectionListToString(ArrayList<PartCommand> list) {
		if(list == null) {
			return "[]";
		} else {
			String str = "[";
			for(int i = 0; i < list.size(); i++) {
				PartCommand command = list.get(i);
				if(command.consoleCommand == null) {
					str += "{(" + command.x + "," + command.y + "," + command.z + ")," + command.duration + "}";
					// buffer.writeInt(2*command.easing + (command.disabled ? 1 : 0));
				} else {
					str += "{" + command.consoleCommand + "}";
				}
				if(i < list.size() - 1) {
					str += ", ";
				}
			}
			return str + "]";
		}
	}

	public static void writeEmoteV2(ByteBuf buffer, Emote emote) {
		buffer.writeInt(2);//encoding version number
		int size = 0;
		for(int partId = 0; partId < PART_COUNT; partId += 1) {
			for(int isRotate = 0; isRotate < 2; isRotate += 1) {
				final int meta_i = 2*partId + isRotate;

				if(emote.partUsages[meta_i] > 0) {
					size += 1;
				}
			}
		}
		buffer.writeInt(size);

		for(int partId = 0; partId < PART_COUNT; partId += 1) {
			for(int isRotate = 0; isRotate < 2; isRotate += 1) {
				int meta_i = 2*partId + isRotate;

				if(emote.partUsages[meta_i] > 0) {
					int intro_i = Emote.SECTION_LIST_COUNT*partId + 3*isRotate;
					int loop_i = intro_i + 1;
					int outro_i = loop_i + 1;
					buffer.writeInt(meta_i);
					buffer.writeInt(emote.partUsages[meta_i]);
					writeSectionListV2(buffer, emote.commands.get(intro_i));
					writeSectionListV2(buffer, emote.commands.get(loop_i));
					writeSectionListV2(buffer, emote.commands.get(outro_i));
				}
			}
		}
	}
	public static void writeSectionListV2(ByteBuf buffer, ArrayList<PartCommand> list) {
		if(list == null) {
			buffer.writeInt(0);
		} else {
			buffer.writeInt(list.size());
			for(int i = 0; i < list.size(); i++) {
				PartCommand command = list.get(i);
				if(command.consoleCommand == null) {
					buffer.writeInt(2*command.easing + (command.disabled ? 1 : 0));
					buffer.writeFloat(command.x);
					buffer.writeFloat(command.y);
					buffer.writeFloat(command.z);
					buffer.writeFloat(command.duration);
				} else {
					buffer.writeInt(2*FAKE_EASING_ACTUALLY_A_CONSOLE_COMMAND + (command.disabled ? 1 : 0));
					Server.writeString(buffer, command.consoleCommand);
				}
			}
		}
	}

	public static Emote readEmoteV2(ByteBuf buffer) {
		//NOTE: I don't like conflating different error conditions together, but I especially don't like throwing exceptions, any error condition will cause null to be returned
		try {
			Emote emote = new Emote();
			int size = buffer.readInt();
			for(int i = 0; i < size; i++) {
				int meta_i = buffer.readInt();

				if(meta_i >= 2*PART_COUNT) return null;
				int partId = meta_i/2;
				int isRotate = meta_i%2;
				int intro_i = Emote.SECTION_LIST_COUNT*partId + 3*isRotate;
				int loop_i = intro_i + 1;
				int outro_i = loop_i + 1;

				emote.partUsages[meta_i] = buffer.readInt();
				emote.commands.set(intro_i, readPartCommandListV2(buffer));
				emote.commands.set(loop_i, readPartCommandListV2(buffer));
				emote.commands.set(outro_i,  readPartCommandListV2(buffer));
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
	public static ArrayList<PartCommand> readPartCommandListV2(ByteBuf buffer) {
		int size = buffer.readInt();
		if(size == 0) return null;
		ArrayList<PartCommand> list = new ArrayList<PartCommand>(size);
		for(int i = 0; i < size; i++) {
			PartCommand command = new PartCommand();
			int a = buffer.readInt();
			command.disabled = (a%2 == 1);
			if(a/2 == FAKE_EASING_ACTUALLY_A_CONSOLE_COMMAND) {
				command.consoleCommand = Server.readString(buffer);
			} else {
				command.easing = a/2;
				command.x = buffer.readFloat();
				command.y = buffer.readFloat();
				command.z = buffer.readFloat();
				command.duration = buffer.readFloat();
			}
			list.add(command);
		}
		return list;
	}

	public static Emote readEmoteV1(ByteBuf buffer) {
		try {
			Emote emote = new Emote();
			int size = buffer.readInt();
			for(int i = 0; i < size; i++) {
				int partId = buffer.readInt();
				if(partId >= 7) return null;

				int section_i = SECTION_LIST_COUNT*partId;
				emote.commands.set(section_i + INTRO_OFFSET, readPartCommandListV1(buffer));
				emote.commands.set(section_i + INTRO_ROTATE, readPartCommandListV1(buffer));
				emote.commands.set(section_i + LOOP_OFFSET, readPartCommandListV1(buffer));
				emote.commands.set(section_i + LOOP_ROTATE, readPartCommandListV1(buffer));

				if(emote.commands.get(section_i + INTRO_OFFSET) != null || emote.commands.get(section_i + LOOP_OFFSET) != null) {
					emote.partUsages[2*partId + 0] = FLAG_USED;
				}
				if(emote.commands.get(section_i + INTRO_ROTATE) != null || emote.commands.get(section_i + LOOP_ROTATE) != null) {
					emote.partUsages[2*partId + 1] = FLAG_USED;
				}
			}
			if(isValidEmote(emote)) {
				return emote;
			} else {
				return null;
			}
		} catch (IndexOutOfBoundsException var2) {
			LogWriter.except(var2);
			return null;
		}
	}
	public static ArrayList<PartCommand> readPartCommandListV1(ByteBuf buffer) {
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
}
