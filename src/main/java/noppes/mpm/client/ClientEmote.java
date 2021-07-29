package noppes.mpm.client;

import aurelienribon.tweenengine.*;
import noppes.mpm.Server;
import noppes.mpm.Emote;
import noppes.mpm.constants.EnumPackets;
import noppes.mpm.client.model.ModelAccessor;
import noppes.mpm.LogWriter;
import noppes.mpm.client.model.ModelPlayerAlt;

import net.minecraft.server.MinecraftServer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ClientEmote {//clientside only

	public static ArrayList<String> cachedEmoteFileNames = new ArrayList<String>();
	public static HashMap<String, ClientEmote> playerEmotes = new HashMap<String, ClientEmote>();
	public static long lastTime = 0;

	public static Timeline createTimeline(Emote emote, ModelPlayerAlt model) {
		Timeline intro = null;
		Timeline loop = null;
		for(Map.Entry<String, Emote.PartCommands> entry : emote.commands.entrySet()) {
			String partName = entry.getKey();
			Emote.PartCommands partCommands = entry.getValue();

			int partId;
			if(partName.equals("head")) {
				partId = ModelAccessor.HEAD;
			} else if(partName.equals("body")) {
				partId = ModelAccessor.BODY;
			} else if(partName.equals("leftarm")) {
				partId = ModelAccessor.LEFT_ARM;
			} else if(partName.equals("rightarm")) {
				partId = ModelAccessor.RIGHT_ARM;
			} else if(partName.equals("leftleg")) {
				partId = ModelAccessor.LEFT_LEG;
			} else if(partName.equals("rightleg")) {
				partId = ModelAccessor.RIGHT_LEG;
			} else if(partName.equals("model")) {
				partId = ModelAccessor.MODEL;
			} else {
				continue;
			}

			Timeline t;
			t = createTimelineFromEmotePartCommandList(partCommands.intro_offset, partId + ModelAccessor.OFF_X, model);
			if(t != null) {
				if(intro == null) intro = Timeline.createParallel();
				intro.push(t);
			}
			t = createTimelineFromEmotePartCommandList(partCommands.loop_offset, partId + ModelAccessor.OFF_X, model);
			if(t != null) {
				if(loop == null) loop = Timeline.createParallel();
				loop.push(t);
			}
			t = createTimelineFromEmotePartCommandList(partCommands.intro_rotate, partId + ModelAccessor.ROT_X, model);
			if(t != null) {
				if(intro == null) intro = Timeline.createParallel();
				intro.push(t);
			}

			t = createTimelineFromEmotePartCommandList(partCommands.loop_rotate, partId + ModelAccessor.ROT_X, model);
			if(t != null) {
				if(loop == null) loop = Timeline.createParallel();
				loop.push(t);
			}
		}
		if(loop == null) {
			if(intro == null) {
				return Timeline.createParallel();
			} else {
				return intro;
			}
		} else if(intro == null) {
			return loop.repeat(60*60*24, 0);
		} else {
			Timeline timeline = Timeline.createSequence();
			timeline.push(intro);
			timeline.push(loop.repeat(60*60*24, 0));
			return timeline;
		}
	}
	public static Timeline createTimelineFromEmotePartCommandList(ArrayList<Emote.PartCommand> list, int partId, ModelPlayerAlt model) {
		float prex = Float.MAX_VALUE;
		float prey = Float.MAX_VALUE;
		float prez = Float.MAX_VALUE;
		if(list.size() == 0) return null;
		int totalDisabled = 0;
		Timeline timeline = Timeline.createSequence();
		for(int i = 0; i < list.size(); i++) {
			Emote.PartCommand command = list.get(i);
			if(command.disabled) {
				totalDisabled += 1;
			} else {
				boolean isx = Math.abs(command.x - prex) > 0.0001F;
				boolean isy = Math.abs(command.y - prey) > 0.0001F;
				boolean isz = Math.abs(command.z - prez) > 0.0001F;
				prex = command.x;
				prey = command.y;
				prez = command.z;
				int total = (isx ? 1 : 0) + (isy ? 1 : 0) + (isz ? 1 : 0);
				if(total == 0) {
					timeline.pushPause(command.duration);
				} else if(total == 1) {
					Tween tween;
					//NOTE: this is highly specific to ModelAccessor
					if(isx) {
						tween = Tween.to(model, partId + 0, command.duration).target(command.x);
					} else if(isy) {
						tween = Tween.to(model, partId + 1, command.duration).target(command.y);
					} else {
						tween = Tween.to(model, partId + 2, command.duration).target(command.z);
					}
					tween.ease(TweenUtils.easings[command.easing]);
					timeline.push(tween);
				} else {
					Timeline par = Timeline.createParallel();
					TweenEquation eq = TweenUtils.easings[command.easing];
					if(isx) {
						Tween tween = Tween.to(model, partId + 0, command.duration).target(command.x);
						tween.ease(eq);
						par.push(tween);
					}
					if(isy) {
						Tween tween = Tween.to(model, partId + 1, command.duration).target(command.y);
						tween.ease(eq);
						par.push(tween);
					}
					if(isz) {
						Tween tween = Tween.to(model, partId + 2, command.duration).target(command.z);
						tween.ease(eq);
						par.push(tween);
					}
					timeline.push(par);
				}
			}
		}
		if(list.size() == totalDisabled) return null;
		return timeline;
	}


	static {//TODO: check if this can be changed
		Tween.registerAccessor(ModelPlayerAlt.class, ModelAccessor.INSTANCE);
	}

	public static boolean attemptEmote(EntityPlayer player, Emote emote) {
		ModelPlayerAlt model = null;
		{//get player model
			Minecraft mc = Minecraft.getMinecraft();
			RenderManager manager = mc.getRenderManager();
			RenderPlayer render = manager.getSkinMap().get(((AbstractClientPlayer) player).getSkinType());
			if(render.mainModel instanceof ModelPlayerAlt) {
				model = (ModelPlayerAlt)render.mainModel;//this could be any kind of ModelBase class; an excellent point of injection for entity animations
			}
		}
		// ModelData data = ModelData.get(player);

		if(model == null) return false;
			// resetPlayer(player);


		model.startEmote(emote, player);
		return true;
	}
	public static void endEmote(EntityPlayer player) {
		ModelPlayerAlt model = null;
		{//get player model
			Minecraft mc = Minecraft.getMinecraft();
			RenderManager manager = mc.getRenderManager();
			RenderPlayer render = manager.getSkinMap().get(((AbstractClientPlayer) player).getSkinType());
			if(render.mainModel instanceof ModelPlayerAlt) {
				model = (ModelPlayerAlt)render.mainModel;//this could be any kind of ModelBase class; an excellent point of injection for entity animations
			}
		}
		// ModelData data = ModelData.get(player);

		if(model == null) return;

		model.endCurEmote();
		return;
	}
}
