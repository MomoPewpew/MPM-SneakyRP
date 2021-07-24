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
package main.java.noppes.mpm.client;

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
import vazkii.quark.base.client.ModKeybinds;
import vazkii.quark.base.lib.LibObfuscation;
import vazkii.quark.base.network.message.MessageRequestEmote;
import vazkii.quark.vanity.feature.EmoteSystem;
import vazkii.aurelienribon.tweenengine.*;

import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;

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
		public AST ast;
	}

	public static final Map<String, EmoteDescriptor> emoteMap = new LinkedHashMap<>();
	// private static final Map<String, EmoteBase> playerEmotes = new HashMap<>();
	// private static final Map<String, EmoteBase> playerWalks = new HashMap<>();

	private static int count;

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
		desc.ast = parse_top_level(emoteTokens, token_i, error_msg, desc);
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
			String part = tokens.get(token_i[0]++);
			if(part.equals("EOF\n")) {
				error_msg[0] = msg_unexpected("EOF", "string");
				return null;
			}
			//TODO: add to parts list
			// desc.usedParts.add(part);

			eat_token(tokens, token_i, "\n");
			return parse_top_level(tokens, token_i, error_msg, desc);
		} else if(eat_token(tokens, token_i, "unit")) {
			Float speed;
			String token = tokens.get(token_i[0]++);
			try {
				speed = Float.parseFloat(token);
			} catch(NumberFormatException e) {
				error_msg[0] = msg_unexpected(tokens.get(token_i[0] - 1), "number");
				return null;
			}
			desc.animSpeed = speed;

			eat_token(tokens, token_i, "\n");
			return parse_top_level(tokens, token_i, error_msg, desc);
		} else {
			AST ast = parse_animation(tokens, token_i, error_msg);
			if(ast == null) {
				error_msg[0] = msg_unexpected(tokens.get(token_i[0]), "keyword: 'use', 'unit' or 'animation'");
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
				error_msg[0] = msg_unexpected(tokens.get(token_i[0] - 1), "animation type ('sequence' or 'parallel')");
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

			ast.str = tokens.get(token_i[0]++);
			//TODO: check tweenable parts
			if(ast.str.equals("EOF\n")) {
				error_msg[0] = msg_unexpected("EOF", "part name");
				return null;
			}

			String token = tokens.get(token_i[0]++);
			try {
				ast.numeral0 = Float.parseFloat(token);
			} catch(NumberFormatException e) {
				error_msg[0] = msg_unexpected(tokens.get(token_i[0] - 1), "time duration");
				return null;
			}

			token = tokens.get(token_i[0]++);
			try {
				ast.numeral1 = Float.parseFloat(token);
			} catch(NumberFormatException e) {
				error_msg[0] = msg_unexpected(tokens.get(token_i[0] - 1), "target rotation");
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
						error_msg[0] = msg_unexpected(tokens.get(token_i[0] - 1), "delay duration");
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
						cmd_ast.numeral0 = 1.0f*Integer.parseInt(token);
					} catch(NumberFormatException e) {
						error_msg[0] = msg_unexpected(tokens.get(token_i[0] - 1), "number of times to repeat");
						return null;
					}
					token = tokens.get(token_i[0]++);
					try {
						cmd_ast.numeral1 = Float.parseFloat(token);
					} catch(NumberFormatException e) {
						error_msg[0] = msg_unexpected(tokens.get(token_i[0] - 1), "delay duration");
						return null;
					}
					parent.child1 = cmd_ast;
					parent = cmd_ast;
				} else if(eat_token(tokens, token_i, "ease")) {
					AST cmd_ast = new AST();
					cmd_ast.type = ASTType.MOVE_EASE;
					cmd_ast.str = tokens.get(token_i[0]++);
					//TODO: add equation checking
					// if(!equations.containsKey(cmd_ast.str)) {
					// 	error_msg[0] = msg_unexpected(tokens.get(token_i[0] - 1), "valid easing type");
					// 	return null;
					// }

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

			ast.str = tokens.get(token_i[0]++);
			//TODO: check tweenable parts or if str == all
			if(ast.str.equals("EOF\n")) {
				error_msg[0] = msg_unexpected("EOF", "part name");
				return null;
			}
			String token = tokens.get(token_i[0]++);
			if(token.equals("all")) {
				ast.numeral0 = 1.0f;
			} else if(token.equals("rotation")) {
				ast.numeral0 = 2.0f;
			} else if(token.equals("offset")) {
				ast.numeral0 = 3.0f;
			} else {
				error_msg[0] = msg_unexpected(tokens.get(token_i[0] - 1), "keyword 'all', 'rotation' or 'offset'");
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
				error_msg[0] = msg_unexpected(tokens.get(token_i[0] - 1), "pause duration");
				return null;
			}

			eat_token(tokens, token_i, "\n");
			ast.child0 = parse_section(tokens, token_i, error_msg);
			return ast;
		} else if(eat_token(tokens, token_i, "yoyo") || eat_token(tokens, token_i, "repeat")) {
			AST ast = new AST();
			if(tokens.get(token_i[0] - 1).equals("yoyo")) {
				ast.type = ASTType.MOVE_YOYO;
			} else {
				ast.type = ASTType.MOVE_REPEAT;
			}
			String token = tokens.get(token_i[0]++);
			try {
				ast.numeral0 = 1.0f*Integer.parseInt(token);
			} catch(NumberFormatException e) {
				error_msg[0] = msg_unexpected(tokens.get(token_i[0] - 1), "number of times to repeat");
				return null;
			}
			token = tokens.get(token_i[0]++);
			try {
				ast.numeral1 = Float.parseFloat(token);
			} catch(NumberFormatException e) {
				error_msg[0] = msg_unexpected(tokens.get(token_i[0] - 1), "delay duration");
				return null;
			}

			eat_token(tokens, token_i, "\n");
			ast.child0 = parse_section(tokens, token_i, error_msg);
			return ast;
		} else {
			return parse_animation(tokens, token_i, error_msg);
		}
	}
}
