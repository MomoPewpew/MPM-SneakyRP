package noppes.mpm.client;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent.Pre;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import noppes.mpm.ModelData;
import noppes.mpm.ModelPartData;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.client.fx.EntityEnderFX;
import noppes.mpm.client.gui.GuiCreationScreenInterface;
import noppes.mpm.client.gui.GuiMPM;
import noppes.mpm.commands.MpmCommandInterface;
import noppes.mpm.constants.EnumPackets;
import noppes.mpm.constants.EnumParts;
import noppes.mpm.util.MPMEntityUtil;

import org.lwjgl.input.Keyboard;

public class ClientEventHandler {
	private long lastAltClick = 0L;
	private boolean altIsPressed = false;
	private World prevWorld;
	private static final Predicate playerSelector = Predicates.and(new Predicate[]{new Predicate<EntityPlayer>() {
		final double range = 6400.0D;

		public boolean apply(EntityPlayer entity) {
			return entity != Minecraft.getMinecraft().thePlayer && entity.getDistanceSqToEntity(Minecraft.getMinecraft().thePlayer) <= 6400.0D;
		}
	}});
	// public static List playerList;
	private boolean slashPressed = false;
	public static Camera camera = new Camera();

	@SubscribeEvent
	public void onKey(KeyInputEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc != null && mc.thePlayer != null) {
			if (ClientProxy.Screen.isPressed()) {
				if (mc.currentScreen == null) {
					mc.displayGuiScreen(new GuiMPM());
				}
			}

			if (mc.currentScreen == null) {
				this.slashPressed = Keyboard.getEventCharacter() == '/';
			}

			if (mc.inGameHasFocus) {

				if (ClientProxy.Camera.isKeyDown() && mc.gameSettings.thirdPersonView == 1) {
					long time = System.currentTimeMillis();
					if (!this.altIsPressed) {
						if (camera.closeupenabled == true) {
							camera.closeupdisabled();
						} else if (camera.enabled == true) {
							camera.closeupenabled();
						} else {
							camera.enabled();
							this.lastAltClick = time;
						}
					}

					this.altIsPressed = true;
				} else {
					this.altIsPressed = false;
				}

			}
		}
	}

	@SubscribeEvent
	public void keyEvent(Pre event) {
		if (event.getGui() instanceof GuiChat) {
			if (!this.slashPressed && Keyboard.getEventCharacter() == '/') {
				this.slashPressed = true;
			}
		} else {
			this.slashPressed = false;
		}

	}

	@SubscribeEvent
	public void onCommand(CommandEvent event) {
		if (event.getCommand() instanceof MpmCommandInterface && event.getSender().getServer() == null && !this.slashPressed) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onMouse(MouseEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		if (event.getDwheel() != 0 && mc.inGameHasFocus && mc.gameSettings.thirdPersonView == 1 && camera.enabled && this.altIsPressed) {
			Camera var10000 = camera;
			var10000.cameraDistance -= (float)event.getDwheel() / 100.0F;
			if (camera.cameraDistance > 14.0F) {
				camera.cameraDistance = 14.0F;
			} else if (camera.cameraDistance < 1.0F) {
				camera.cameraDistance = 1.0F;
			}

			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onRenderTick(RenderTickEvent event) {
		camera.update(event.phase == Phase.START);
	}

	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		if (event.side != Side.SERVER && event.phase != Phase.START) {
			Minecraft mc = Minecraft.getMinecraft();
			if (mc.theWorld != null) {
				if (this.prevWorld != mc.theWorld) {
					MorePlayerModels.HasServerSide = false;
					GuiCreationScreenInterface.Message = "message.noserver";
					ModelData data = ModelData.get(mc.thePlayer);
					Client.sendData(EnumPackets.PING, MorePlayerModels.Version, data.writeToNBT());
					this.prevWorld = mc.theWorld;
					ClientProxy.fixModels(false);
				}

				++RenderEvent.lastSkinTick;
				// if (mc.theWorld.getWorldInfo().getWorldTotalTime() % 20L == 0L) {
					// playerList = mc.theWorld.getPlayers(EntityPlayer.class, playerSelector);
				// }

			}
		}
	}

	@SubscribeEvent
	public void onCamera(CameraSetup event) {
		Minecraft mc = Minecraft.getMinecraft();
		Entity entity = event.getEntity();
		if ((!(entity instanceof EntityLivingBase) || !((EntityLivingBase)entity).isPlayerSleeping()) && mc.gameSettings.thirdPersonView == 1) {
			float f = entity.getEyeHeight();
			double partialTicks = event.getRenderPartialTicks();
			double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks;
			double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks + (double)f;
			double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks;
			double d3 = (double)(camera.cameraDistance - 4.0F);
			float f1 = entity.rotationYaw;
			float f2 = entity.rotationPitch;
			double d4 = (double)(-MathHelper.sin(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F)) * d3;
			double d5 = (double)(MathHelper.cos(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F)) * d3;
			double d6 = (double)(-MathHelper.sin(f2 * 0.017453292F)) * d3;

			for(int i = 0; i < 8; ++i) {
				float f3 = (float)((i & 1) * 2 - 1);
				float f4 = (float)((i >> 1 & 1) * 2 - 1);
				float f5 = (float)((i >> 2 & 1) * 2 - 1);
				f3 *= 0.1F;
				f4 *= 0.1F;
				f5 *= 0.1F;
				RayTraceResult raytraceresult = mc.theWorld.rayTraceBlocks(new Vec3d(d0 + (double)f3, d1 + (double)f4, d2 + (double)f5), new Vec3d(d0 - d4 + (double)f3 + (double)f5, d1 - d6 + (double)f4, d2 - d5 + (double)f5));
				if (raytraceresult != null) {
					double d7 = raytraceresult.hitVec.distanceTo(new Vec3d(d0, d1, d2));
					if (d7 < d3) {
						d3 = d7;
					}
				}
			}

			GlStateManager.rotate(entity.rotationPitch - f2, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(entity.rotationYaw - f1, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(0.0F, 0.0F, (float)(-d3));
			GlStateManager.rotate(f1 - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(f2 - entity.rotationPitch, 1.0F, 0.0F, 0.0F);
		}
	}

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event) {
		if (event.side != Side.SERVER && event.phase != Phase.START) {
			EntityPlayer player = event.player;
			ModelData data = ModelData.get(player);
			EntityLivingBase entity = data.getEntity(player);
			Minecraft mc = Minecraft.getMinecraft();
			if (entity != null) {
				entity.onUpdate();
				MPMEntityUtil.Copy(player, entity);
			} else {
				if (!MorePlayerModels.HasServerSide) {
					data.eyes.update(player);
				}

				ModelPartData particles = data.getPartData(EnumParts.PARTICLES);
				if (particles != null) {
					this.spawnParticles(player, data, particles);
				}
			}
		}
	}

	private void spawnParticles(EntityPlayer player, ModelData data, ModelPartData particles) {
		if (MorePlayerModels.EnableParticles) {
			Minecraft minecraft = Minecraft.getMinecraft();
			double height = player.getYOffset() + (double)data.getBodyY();
			Random rand = player.getRNG();

			for(int i = 0; i < 2; ++i) {
				EntityEnderFX fx = new EntityEnderFX((AbstractClientPlayer)player, (rand.nextDouble() - 0.5D) * (double)player.width, rand.nextDouble() * (double)player.height - height - 0.25D, (rand.nextDouble() - 0.5D) * (double)player.width, (rand.nextDouble() - 0.5D) * 2.0D, -rand.nextDouble(), (rand.nextDouble() - 0.5D) * 2.0D, particles);
				minecraft.effectRenderer.addEffect(fx);
			}

		}
	}

	@SubscribeEvent
	public void onRenderLiving(RenderLivingEvent.Specials.Pre event) {
		EntityLivingBase entity = event.getEntity();

		if (event.isCancelable()) {
			event.setCanceled(true);
			Minecraft minecraft = Minecraft.getMinecraft();
			if (entity instanceof EntityPlayer) {
				ModelData data = ModelData.get((EntityPlayer) entity);
				float height = ((Entity)entity).getEyeHeight() + 0.25F + (0.5F * data.getPartConfig(EnumParts.HEAD).scaleY) - (entity.isSneaking() ? 0.25F : 0.0F);
				renderName(entity, height);
			}
		}
	}

	public static void renderName(EntityLivingBase entity, float height) {
		if (MorePlayerModels.HidePlayerNames || entity == Minecraft.getMinecraft().thePlayer) return;

		String name = null;

		if (Loader.isModLoaded("multicharacter")) {
			name = ((EntityPlayer) entity).getDisplayName().getFormattedText() + " [" + entity.getName() + "]";
		} else {
			name = entity.getName();
		}

		Float animTime = Animation.getPartialTickTime();

		Entity rendewViewEntity = Minecraft.getMinecraft().getRenderViewEntity();
		double renderX = rendewViewEntity.lastTickPosX + (rendewViewEntity.posX - rendewViewEntity.lastTickPosX) * animTime;
		double renderY = rendewViewEntity.lastTickPosY + (rendewViewEntity.posY - rendewViewEntity.lastTickPosY) * animTime;
		double renderZ = rendewViewEntity.lastTickPosZ + (rendewViewEntity.posZ - rendewViewEntity.lastTickPosZ) * animTime;

		double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * animTime;
		double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * animTime;
		double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * animTime;

		renderLivingLabel(entity, name, x - renderX, (float) (y - renderY + height), z - renderZ, 64);
	}

	public static void renderLivingLabel(EntityLivingBase entity, String name, double x, float height, double z, int maxDistance) {
		RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();

		double distanceSq = entity.getDistanceSqToEntity(renderManager.renderViewEntity);

		if (distanceSq > (maxDistance * maxDistance)) {
			return;
		}

		float viewY = renderManager.playerViewY;
		float viewX = renderManager.playerViewX;
		boolean backwardsCam = (renderManager.options.thirdPersonView == 2);
		int lvt_17_1_ = "deadmau5".equals(name) ? -10 : 0;
		EntityRenderer.func_189692_a(renderManager.getFontRenderer(), name, (float)x, height, (float)z, lvt_17_1_, viewY, viewX, backwardsCam, false);
	}
}
