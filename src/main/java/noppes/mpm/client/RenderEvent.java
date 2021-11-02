package noppes.mpm.client;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.MPMRenderEntityUtil;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderLivingEvent.Post;
import net.minecraftforge.client.event.RenderLivingEvent.Pre;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noppes.mpm.LogWriter;
import noppes.mpm.ModelData;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.client.layer.LayerPreRender;
import noppes.mpm.util.PixelmonHelper;

public class RenderEvent {
	public static RenderEvent Instance;
	public EntityRenderer orignalRenderer;
	public static long lastSkinTick = -30L;
	public static final int MaxUrlTicks = 6;
	private static ResourceLocation skinResource = null;
	private static ITextureObject textureObject = null;

	public RenderEvent() {
		Instance = this;
		Minecraft mc = Minecraft.getMinecraft();
		this.orignalRenderer = mc.entityRenderer;
	}

	@SubscribeEvent
	public void post(Post event) {
		if (textureObject != null) {
			TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
			texturemanager.loadTexture(skinResource, textureObject);
			skinResource = null;
			textureObject = null;
		}

		if (event.getEntity() instanceof AbstractClientPlayer) {
			AbstractClientPlayer player = (AbstractClientPlayer)event.getEntity();
			ModelData data = ModelData.get(player);
			// if (data.isSleeping()) {
			// 	player.renderYawOffset = player.prevRenderYawOffset = player.rotationYaw;
			// }

			GlStateManager.popMatrix();
		}
	}

	@SubscribeEvent
	public void pre(Pre event) {
		if (event.getEntity() instanceof AbstractClientPlayer && !event.isCanceled()) {
			AbstractClientPlayer player = (AbstractClientPlayer)event.getEntity();
			Minecraft mc = Minecraft.getMinecraft();
			GlStateManager.pushMatrix();
			if (ClientEventHandler.camera.enabled && player == mc.thePlayer) {
				player.rotationPitch -= ClientEventHandler.camera.cameraPitch + ClientEventHandler.camera.playerPitch;
				player.prevRotationPitch -= ClientEventHandler.camera.cameraPitch + ClientEventHandler.camera.playerPitch;
				mc.entityRenderer.getMouseOver(Animation.getPartialTickTime());
			}

			ModelData data = ModelData.get(player);

			float offset = data.getOffsetCamera(player);
			player.eyeHeight = player.getDefaultEyeHeight() - offset;
			if (!data.resourceInit && lastSkinTick > 6L) {
				this.loadPlayerResource(player, data);
				lastSkinTick = 0L;
				data.resourceInit = true;
			}

			Entity entity = data.getEntity(player);
			if (entity != null) {
				if (ClientEventHandler.camera.enabled && player == mc.thePlayer) {
					entity.rotationPitch = player.rotationPitch;
					entity.prevRotationPitch = player.prevRotationPitch;
				}

				event.setCanceled(true);
				if (PixelmonHelper.isPixelmon(entity)) {
					entity.setSneaking(true);
				}

				if (entity instanceof EntityTameable) {
					if (player.isSneaking()) {
						((EntityTameable) entity).setSitting(true);
					} else {
						((EntityTameable) entity).setSitting(false);
					}
				}

				if (data.textureObject != null) {
					TextureManager texturemanager = mc.getTextureManager();
					if (textureObject != null) {
						texturemanager.loadTexture(skinResource, textureObject);
					}

					skinResource = MPMRenderEntityUtil.getResource(entity);
					textureObject = texturemanager.getTexture(skinResource);
					if (textureObject == null) {
						skinResource = null;
					} else {
						texturemanager.loadTexture(skinResource, (ITextureObject)data.textureObject);
					}
				}

				Entity renderViewEntity = mc.getRenderViewEntity();

				GlStateManager.translate(
						((player.posX - renderViewEntity.posX) * (1.0F - data.entityScaleX)),
						((player.posY - renderViewEntity.posY) * (1.0F - data.entityScaleY)),
						((player.posZ - renderViewEntity.posZ) * (1.0F - data.entityScaleX))
					);

				//These rotate functions were neccesary when we had separate X and Z sliders, but that feature was cut. Too many bugs, and even when it worked it looked 20 fps
				//GlStateManager.rotate(player.renderYawOffset, 0.0F, -1.0F, 0.0F);
				GlStateManager.scale(data.entityScaleX, data.entityScaleY, data.entityScaleX);
				//GlStateManager.rotate(-player.renderYawOffset, 0.0F, -1.0F, 0.0F);

				mc.getRenderManager().renderEntityStatic(entity, Animation.getPartialTickTime(), false);
				GlStateManager.popMatrix();
			} else {
				offset = 0.0F;
				if (!MorePlayerModels.DisableFlyingAnimation && player.capabilities.isFlying && player.worldObj.isAirBlock(player.getPosition())) {
					offset = MathHelper.cos((float)player.ticksExisted * 0.1F) * -0.06F;
				}

				GlStateManager.translate(0.0F, -offset, 0.0F);
				List layers = event.getRenderer().layerRenderers;
				Iterator var8 = layers.iterator();

				while(var8.hasNext()) {
					LayerRenderer layer = (LayerRenderer)var8.next();
					if (layer instanceof LayerPreRender) {
						((LayerPreRender)layer).preRender(player);
					}
				}

			}
		}
	}

	public void loadPlayerResource(EntityPlayer pl, ModelData data) {
		Minecraft minecraft = Minecraft.getMinecraft();
		data.textureObject = null;
		AbstractClientPlayer player = (AbstractClientPlayer)pl;
		if (data.url != null && !data.url.isEmpty()) {
			if (!data.url.startsWith("http://") && !data.url.startsWith("https://")) {
				ResourceLocation location = new ResourceLocation(data.url);

				try {
					minecraft.getTextureManager().bindTexture(location);
					data.textureObject = minecraft.getTextureManager().getTexture(location);
				} catch (Exception var10) {
					location = DefaultPlayerSkin.getDefaultSkinLegacy();
				}

				this.setPlayerTexture(player, location);
			} else {
				boolean hasUrl = data.getEntity(pl) == null;
				ResourceLocation location = new ResourceLocation("skins/" + (data.url + hasUrl).hashCode());
				if (!player.hasPlayerInfo()) {
					location = player.getLocationSkin();
				} else {
					this.setPlayerTexture(player, location);
				}

				data.textureObject = this.loadTexture((File)null, location, DefaultPlayerSkin.getDefaultSkinLegacy(), data.url, hasUrl);
			}

		} else {
			if (!data.resourceLoaded) {
				Map map = minecraft.getSkinManager().loadSkinFromCache(pl.getGameProfile());
				if (map.isEmpty()) {
					map = minecraft.getSessionService().getTextures(minecraft.getSessionService().fillProfileProperties(player.getGameProfile(), false), false);
				}

				if (map.containsKey(Type.SKIN)) {
					MinecraftProfileTexture profile = (MinecraftProfileTexture)map.get(Type.SKIN);
					File dir = new File((File)ObfuscationReflectionHelper.getPrivateValue(SkinManager.class, minecraft.getSkinManager(), 2), profile.getHash().substring(0, 2));
					File file = new File(dir, profile.getHash());
					if (file.exists()) {
						file.delete();
					}

					ResourceLocation location = new ResourceLocation("skins/" + profile.getHash());
					this.loadTexture(file, location, DefaultPlayerSkin.getDefaultSkinLegacy(), profile.getUrl(), true);
					this.setPlayerTexture(player, location);
					data.resourceLoaded = true;
					return;
				}
			}

			this.setPlayerTexture(player, (ResourceLocation)null);
		}
	}

	private void setPlayerTexture(AbstractClientPlayer player, ResourceLocation texture) {
		NetworkPlayerInfo playerInfo = (NetworkPlayerInfo)ObfuscationReflectionHelper.getPrivateValue(AbstractClientPlayer.class, player, 0);
		if (playerInfo != null) {
			Map playerTextures = (Map)ObfuscationReflectionHelper.getPrivateValue(NetworkPlayerInfo.class, playerInfo, 1);
			playerTextures.put(Type.SKIN, texture);
			if (texture == null) {
				ObfuscationReflectionHelper.setPrivateValue(NetworkPlayerInfo.class, playerInfo, false, 4);
			}

		}
	}

	private ITextureObject loadTexture(File file, ResourceLocation resource, ResourceLocation def, String par1Str, boolean fix64) {
		TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
		ITextureObject object = texturemanager.getTexture(resource);
		if (object == null) {
			object = new ImageDownloadAlt(file, par1Str, def, new ImageBufferDownloadAlt(fix64));
			texturemanager.loadTexture(resource, (ITextureObject)object);
		}

		return (ITextureObject)object;
	}

	@SubscribeEvent
	public void hand(RenderHandEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		ModelData data = ModelData.get(mc.thePlayer);
		mc.thePlayer.eyeHeight = mc.thePlayer.getDefaultEyeHeight() - data.getOffsetCamera(mc.thePlayer);
		Entity entity = data.getEntity(mc.thePlayer);
		if (entity != null) {
			event.setCanceled(true);
		} else {
			if (!data.resourceInit && lastSkinTick > 6L) {
				this.loadPlayerResource(mc.thePlayer, data);
				lastSkinTick = 0L;
				data.resourceInit = true;
			}

		}
	}

	@SubscribeEvent
	public void chat(ClientChatReceivedEvent event) {
		if (!MorePlayerModels.HasServerSide) {
			try {
				ChatMessages.parseMessage(event.getMessage().getFormattedText());
			} catch (Exception var3) {
				LogWriter.warn("Cant handle chatmessage: " + event.getMessage() + ":" + var3.getMessage());
			}

		}
	}

	@SubscribeEvent
	public void selectionBox(DrawBlockHighlightEvent event) {
		if (MorePlayerModels.HideSelectionBox) {
			event.setCanceled(true);
		}

	}

	@SubscribeEvent
	public void overlay(net.minecraftforge.client.event.RenderGameOverlayEvent.Post event) {
		if(event.getType() != ElementType.ALL)
		return;

		Minecraft mc = Minecraft.getMinecraft();
		if(mc.currentScreen != null || MorePlayerModels.Tooltips == 0)
		return;
		ItemStack item = mc.thePlayer.getHeldItemMainhand();
		if(item == null)
		return;

		String name = item.getDisplayName();
		int x = event.getResolution().getScaledWidth() - mc.fontRendererObj.getStringWidth(name);

		int posX = 4;
		int posY = 4;
		if(MorePlayerModels.Tooltips % 2 == 0)
		posX = x - 4;

		if(MorePlayerModels.Tooltips > 2)
		posY = event.getResolution().getScaledHeight() - 24;

		mc.fontRendererObj.drawStringWithShadow(name, posX, posY, 0xffffff);
		if(item.isItemStackDamageable()){
			int max = item.getMaxDamage();

			String dam = (max - item.getItemDamage()) + "/" + max;

			x = event.getResolution().getScaledWidth() - mc.fontRendererObj.getStringWidth(dam);

			if(MorePlayerModels.Tooltips == 2 || MorePlayerModels.Tooltips == 4)
			posX = x - 4;

			mc.fontRendererObj.drawStringWithShadow(dam, posX, posY + 12, 0xffffff);
		}
	}
}
