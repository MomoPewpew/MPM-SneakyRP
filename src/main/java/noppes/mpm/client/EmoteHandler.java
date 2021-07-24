package noppes.mpm.client;
/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [26/03/2016, 21:37:17 (GMT)]
 */

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import aurelienribon.tweenengine.*;
import noppes.mpm.client.ModelAccessor;

import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public final class EmoteHandler {
	public static enum ASTType {
		SEQUENCE,
		PARALLEL,
		MOVE,
		MOVE_DELAY,
		MOVE_YOYO,
		MOVE_REPEAT,
		MOVE_EASE,
		RESET,
		PAUSE,
		YOYO,
		REPEAT
	}
	public static class AST {
		public ASTType type;
		public String str;
		public int i;
		public Float numeral0;
		public Float numeral1;
		public AST child0;//NOTE: we can optimize this by compiling to bytecode instead of an ast (java makes this very hard to do efficiently)
		public AST child1;
	}

	public static class EmoteDescriptor {
		public String name;
		public int pid;

		public float animSpeed;
		public ArrayList<Integer> usedParts;
		public AST root;
	}

	public static final Map<String, EmoteDescriptor> emoteMap = new LinkedHashMap<>();
	private static final Map<String, TweenEquation> equationMap = new HashMap<>();
	private static final Map<String, Integer> tweenableMap = new HashMap<>();
	private static final Map<String, Integer> partMap = new HashMap<>();
	// private static final Map<String, EmoteBase> playerEmotes = new HashMap<>();
	// private static final Map<String, EmoteBase> playerWalks = new HashMap<>();

	private static int count;

	static {
		Class<?> clazz = ModelAccessor.class;
		Field[] fields = clazz.getDeclaredFields();
		for(Field f : fields) {
			if(f.getType() != int.class)
				continue;

			int modifiers = f.getModifiers();
			if(Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
				try {
					int val = f.getInt(null);
					String name = f.getName().toLowerCase();
					if(name.matches("^.+?_[xyz]$"))
						tweenableMap.put(name, val);
					else
						partMap.put(name, val);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}

		clazz = TweenEquations.class;
		fields = clazz.getDeclaredFields();
		for(Field f : fields) {
			String name = f.getName().replaceAll("[A-Z]", "_$0").substring(5).toLowerCase();
			try {
				TweenEquation eq = (TweenEquation) f.get(null);
				equationMap.put(name, eq);
				if(name.equals("none")) {
					equationMap.put("linear", eq);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean addEmote(String name) {
		EmoteDescriptor desc = new EmoteDescriptor();
		desc.name = name;
		desc.pid = count++;

		ArrayList<String> emoteTokens = new ArrayList<>();
		{//read and lex file
			File emoteFile = new File("/assets/quark/emotes/", name + ".emote");
			if(emoteFile.exists()) {
				try (BufferedReader br = new BufferedReader(new FileReader(emoteFile))) {
					String s;
					while ((s = br.readLine()) != null) {
						if(!s.startsWith("#") && !s.isEmpty()) {
							String[] tokens = s.trim().split(" ");
							for(int i = 0; i < tokens.length; i += 1) {
								emoteTokens.add(tokens[i]);
							}
							emoteTokens.add("\n");
						}
					}
					emoteTokens.add("EOF\n");//EOF token
				} catch (IOException e) {
					//TODO: add error logging
					return false;
				}
			} else {
				//TODO: add error logging
				return false;
			}
		}

		int[] token_i = {0};
		String[] error_msg = {null};
		desc.root = parse_top_level(emoteTokens, token_i, error_msg, desc);
		if(error_msg[0] != null) {
			//TODO: add error logging
			return false;
		}

		emoteMap.put(name, desc);
		return true;
	}

	public static String msg_unexpected(String unexpected, String expected) {
		if(unexpected.equals("\n")) {
			unexpected = "End of Expression";
		} else if(unexpected.charAt(0) == '_') {
			unexpected = unexpected.substring(1, unexpected.length());
		}
		return "Quark emote syntax error: Unexpected token, expected " + expected + "; got " + unexpected.trim();
	}

	public static boolean eat_token(ArrayList<String> tokens, int[] token_i, String str) {
		if(str.equals(tokens.get(token_i[0]))) {
			token_i[0] += 1;
			return true;
		} else {
			return false;
		}
	}

	public static AST parse_top_level(ArrayList<String> tokens, int[] token_i, String[] error_msg, EmoteDescriptor desc) {
		if(eat_token(tokens, token_i, "use")) {
			String token = tokens.get(token_i[0]++);
			if(partMap.containsKey(token)) {
				desc.usedParts.add(partMap.get(token));
			} else {
				error_msg[0] = msg_unexpected(token, "part name");
				return null;
			}

			eat_token(tokens, token_i, "\n");
			return parse_top_level(tokens, token_i, error_msg, desc);
		} else if(eat_token(tokens, token_i, "unit")) {
			Float speed;
			String token = tokens.get(token_i[0]++);
			try {
				speed = Float.parseFloat(token);
			} catch(NumberFormatException e) {
				error_msg[0] = msg_unexpected(token, "number");
				return null;
			}
			desc.animSpeed = speed;

			eat_token(tokens, token_i, "\n");
			return parse_top_level(tokens, token_i, error_msg, desc);
		} else {
			AST ast = parse_animation(tokens, token_i, error_msg);
			if(ast == null) {
				error_msg[0] = msg_unexpected(tokens.get(token_i[0]), "keyword: 'use', 'unit' or 'animation'");
				return null;
			}
			return ast;
		}
	}

	public static AST parse_animation(ArrayList<String> tokens, int[] token_i, String[] error_msg) {
		if(eat_token(tokens, token_i, "section") || eat_token(tokens, token_i, "section")) {
			AST ast = new AST();
			if(eat_token(tokens, token_i, "sequence")) {
				ast.type = ASTType.SEQUENCE;
			} else if(eat_token(tokens, token_i, "parallel")) {
				ast.type = ASTType.PARALLEL;
			} else {
				error_msg[0] = msg_unexpected(tokens.get(token_i[0]), "animation type ('sequence' or 'parallel')");
				return null;
			}

			eat_token(tokens, token_i, "\n");
			ast.child0 = parse_section(tokens, token_i, error_msg);
			if(error_msg[0] != null) return null;

			if(eat_token(tokens, token_i, "end")) {
				eat_token(tokens, token_i, "\n");
				return ast;
			} else {
				error_msg[0] = msg_unexpected(tokens.get(token_i[0]), "keyword: 'section', 'move', 'reset', 'pause', 'yoyo', 'repeat' or 'end'");
				return null;
			}
		} else {
			return null;
		}
	}

	public static AST parse_section(ArrayList<String> tokens, int[] token_i, String[] error_msg) {
		if(eat_token(tokens, token_i, "move")) {
			AST ast = new AST();
			ast.type = ASTType.MOVE;

			String token = tokens.get(token_i[0]++);
			if(tweenableMap.containsKey(token)) {
				ast.i = tweenableMap.get(token);
			} else {
				error_msg[0] = msg_unexpected(token, "part name");
				return null;
			}

			token = tokens.get(token_i[0]++);
			try {
				ast.numeral0 = Float.parseFloat(token);
			} catch(NumberFormatException e) {
				error_msg[0] = msg_unexpected(token, "time duration");
				return null;
			}

			token = tokens.get(token_i[0]++);
			try {
				ast.numeral1 = Float.parseFloat(token);
			} catch(NumberFormatException e) {
				error_msg[0] = msg_unexpected(token, "target rotation");
				return null;
			}

			AST parent = ast;
			while(true) {
				if(eat_token(tokens, token_i, "delay")) {
					AST cmd_ast = new AST();
					cmd_ast.type = ASTType.MOVE_DELAY;
					token = tokens.get(token_i[0]++);
					try {
						cmd_ast.numeral0 = Float.parseFloat(token);
					} catch(NumberFormatException e) {
						error_msg[0] = msg_unexpected(token, "delay duration");
						return null;
					}
					parent.child1 = cmd_ast;
					parent = cmd_ast;
				} else if(eat_token(tokens, token_i, "yoyo") || eat_token(tokens, token_i, "repeat")) {
					AST cmd_ast = new AST();
					if(tokens.get(token_i[0] - 1).equals("yoyo")) {
						cmd_ast.type = ASTType.MOVE_YOYO;
					} else {
						cmd_ast.type = ASTType.MOVE_REPEAT;
					}
					token = tokens.get(token_i[0]++);
					try {
						cmd_ast.i = Integer.parseInt(token);
					} catch(NumberFormatException e) {
						error_msg[0] = msg_unexpected(token, "number of times to repeat");
						return null;
					}
					token = tokens.get(token_i[0]++);
					try {
						cmd_ast.numeral0 = Float.parseFloat(token);
					} catch(NumberFormatException e) {
						error_msg[0] = msg_unexpected(token, "delay duration");
						return null;
					}
					parent.child1 = cmd_ast;
					parent = cmd_ast;
				} else if(eat_token(tokens, token_i, "ease")) {
					AST cmd_ast = new AST();
					cmd_ast.type = ASTType.MOVE_EASE;
					cmd_ast.str = tokens.get(token_i[0]++);
					if(!equationMap.containsKey(cmd_ast.str)) {
						error_msg[0] = msg_unexpected(cmd_ast.str, "valid easing type");
						return null;
					}

					parent.child1 = cmd_ast;
					parent = cmd_ast;
				} else if(eat_token(tokens, token_i, "\n")) {
					break;
				} else {
					error_msg[0] = msg_unexpected(tokens.get(token_i[0]), "'delay', 'yoyo', 'repeat', 'ease' or newline");
					return null;
				}
			}

			ast.child0 = parse_section(tokens, token_i, error_msg);
			return ast;
		} else if(eat_token(tokens, token_i, "reset")) {
			AST ast = new AST();
			ast.type = ASTType.RESET;

			String token = tokens.get(token_i[0]++);
			if(token.equals("all")) {
				ast.i = 0;
			} else if(tweenableMap.containsKey(token)) {
				ast.i = tweenableMap.get(token);
			} else {
				error_msg[0] = msg_unexpected(token, "part name");
				return null;
			}

			token = tokens.get(token_i[0]++);
			if(token.equals("all")) {
				ast.numeral0 = 1.0f;
			} else if(token.equals("rotation")) {
				ast.numeral0 = 2.0f;
			} else if(token.equals("offset")) {
				ast.numeral0 = 3.0f;
			} else {
				error_msg[0] = msg_unexpected(token, "keyword 'all', 'rotation' or 'offset'");
				return null;
			}

			token = tokens.get(token_i[0]++);
			try {
				ast.numeral1 = Float.parseFloat(token);
			} catch(NumberFormatException e) {
				error_msg[0] = msg_unexpected(token, "reset duration");
				return null;
			}

			eat_token(tokens, token_i, "\n");
			ast.child0 = parse_section(tokens, token_i, error_msg);
			return ast;
		} else if(eat_token(tokens, token_i, "pause")) {
			AST ast = new AST();
			ast.type = ASTType.PAUSE;
			String token = tokens.get(token_i[0]++);
			try {
				ast.numeral0 = Float.parseFloat(token);
			} catch(NumberFormatException e) {
				error_msg[0] = msg_unexpected(token, "pause duration");
				return null;
			}

			eat_token(tokens, token_i, "\n");
			ast.child0 = parse_section(tokens, token_i, error_msg);
			return ast;
		} else if(eat_token(tokens, token_i, "yoyo") || eat_token(tokens, token_i, "repeat")) {
			AST ast = new AST();
			if(tokens.get(token_i[0] - 1).equals("yoyo")) {
				ast.type = ASTType.YOYO;
			} else {
				ast.type = ASTType.REPEAT;
			}
			String token = tokens.get(token_i[0]++);
			try {
				ast.i = Integer.parseInt(token);
			} catch(NumberFormatException e) {
				error_msg[0] = msg_unexpected(token, "number of times to repeat");
				return null;
			}
			token = tokens.get(token_i[0]++);
			try {
				ast.numeral0 = Float.parseFloat(token);
			} catch(NumberFormatException e) {
				error_msg[0] = msg_unexpected(token, "delay duration");
				return null;
			}

			eat_token(tokens, token_i, "\n");
			ast.child0 = parse_section(tokens, token_i, error_msg);
			return ast;
		} else {
			return parse_animation(tokens, token_i, error_msg);
		}
	}

	public static Timeline creatTimeline(EmoteDescriptor desc, ModelBiped model) {
		if(desc.root.type == ASTType.SEQUENCE) {
			Timeline timeline = Timeline.createSequence();
			buildTimeline(timeline, desc.root.child0, desc, model);
			return timeline;
		} else if(desc.root.type == ASTType.PARALLEL) {
			Timeline timeline = Timeline.createParallel();
			buildTimeline(timeline, desc.root.child0, desc, model);
			return timeline;
		} else {
			//TODO: assertion logging
			Timeline timeline = Timeline.createParallel();
			return timeline;
		}
	}
	public static void buildTimeline(Timeline timeline, AST ast, EmoteDescriptor desc, ModelBiped model) {
		if(ast.type == ASTType.SEQUENCE) {
			Timeline newTimeline = Timeline.createSequence();
			buildTimeline(newTimeline, ast.child0, desc, model);
			timeline.push(newTimeline);
		} else if(ast.type == ASTType.PARALLEL) {
			Timeline newTimeline = Timeline.createParallel();
			buildTimeline(newTimeline, ast.child0, desc, model);
			timeline.push(newTimeline);
		} else if(ast.type == ASTType.MOVE) {
			int part = tweenableMap.get(ast.str);
			Tween tween = Tween.to(model, part, ast.numeral0*desc.animSpeed).target(ast.numeral1);
			buildTween(tween, ast.child1, desc.animSpeed);
			timeline.push(tween);
		} else if(ast.type == ASTType.RESET) {
			int part = ast.i;
			float time = ast.numeral1*desc.animSpeed;
			boolean all = ast.numeral0 == 1.0f;
			boolean rot = all || ast.numeral0 == 2.0f;
			boolean off = all || ast.numeral0 == 3.0f;

			Timeline parallel = Timeline.createParallel();
			int lower = (part == 0) ? 0 : part + (rot ? 0 : 3);
			int upper = (part == 0) ? ModelAccessor.STATE_COUNT : part + (off ? ModelAccessor.STATE_COUNT : 3);

			for(int i = lower; i < upper; i++) {
				int piece = (i / ModelAccessor.MODEL_PROPS) * ModelAccessor.MODEL_PROPS;
				if(desc.usedParts.contains(piece)) parallel.push(Tween.to(model, i, time));
			}

			timeline.push(parallel);
		} else if(ast.type == ASTType.PAUSE) {
			timeline.pushPause(ast.numeral0*desc.animSpeed);
		} else if(ast.type == ASTType.YOYO) {
			timeline.repeatYoyo(ast.i, ast.numeral0*desc.animSpeed);
		} else if(ast.type == ASTType.REPEAT) {
			timeline.repeat(ast.i, ast.numeral0*desc.animSpeed);
		} else {
			//TODO: assertion logging
		}
	}

	public static void buildTween(Tween tween, AST ast, Float speed) {
		if(ast == null) return;

		if(ast.type == ASTType.MOVE_DELAY) {
			tween.delay(ast.numeral0*speed);
			buildTween(tween, ast.child1, speed);
		} else if(ast.type == ASTType.MOVE_YOYO) {
			tween.repeatYoyo(ast.i, ast.numeral0*speed);
			buildTween(tween, ast.child1, speed);
		} else if(ast.type == ASTType.MOVE_REPEAT) {
			tween.repeat(ast.i, ast.numeral0*speed);
			buildTween(tween, ast.child1, speed);
		} else if(ast.type == ASTType.MOVE_EASE) {
			tween.ease(equationMap.get(ast.str));
			buildTween(tween, ast.child1, speed);
		} else {
			//TODO: assertion logging
		}
	}


	public static boolean attemptEmote(EntityPlayer player, String name) {
		// String name = player.getName();
		EmoteDescriptor desc = emoteMap.get(name);
		if(desc == null) return false;

		ModelBiped model;
		{//get player model
			Minecraft mc = Minecraft.getMinecraft();
			RenderManager manager = mc.getRenderManager();
			RenderPlayer render = manager.getSkinMap().get(((AbstractClientPlayer) player).getSkinType());
			model = render.getMainModel();
		}

		if(model != null) {
			// resetPlayer(player);

			Timeline timeline = creatTimeline(desc, model);

			timeline.start(player);
			// lastMs = System.currentTimeMillis();
			// playerEmotes.put(name, emote);
			// if (desc.getWalkstyle() == true)
			// 	playerWalks.put(name, emote);
			return true;
		} else {
			return false;
		}
	}
}
