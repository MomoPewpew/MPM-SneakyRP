package noppes.mpm.client;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.io.File;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.MPMRenderEntityUtil;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
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
import noppes.mpm.client.layer.LayerProp;
import noppes.mpm.constants.EnumParts;
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
			AbstractClientPlayer renderPlayer = (AbstractClientPlayer)event.getEntity();
			ModelData data = ModelData.get(renderPlayer);
			float animTime = Animation.getPartialTickTime();

			if (!data.meMessages.isEmpty()) {
				Long systemTime = System.currentTimeMillis();

				//calculate length of line between camera and entity
				Entity renderViewEntity = Minecraft.getMinecraft().getRenderViewEntity();
				double camX = renderViewEntity.posX + ActiveRenderInfo.getCameraPosition().xCoord;
				double camY = renderViewEntity.posY + ActiveRenderInfo.getCameraPosition().yCoord - renderViewEntity.getEyeHeight();
				double camZ = renderViewEntity.posZ + ActiveRenderInfo.getCameraPosition().zCoord;

				Float meHeight = ((Entity)renderPlayer).getEyeHeight() - 0.25F - data.modelOffsetY - (renderPlayer.isSneaking() ? 0.25F : 0.0F);

				double entityX = renderPlayer.lastTickPosX + (renderPlayer.posX - renderPlayer.lastTickPosX) * animTime;
				double entityY = renderPlayer.lastTickPosY + (renderPlayer.posY - renderPlayer.lastTickPosY) * animTime + meHeight;
				double entityZ = renderPlayer.lastTickPosZ + (renderPlayer.posZ - renderPlayer.lastTickPosZ) * animTime;

				double newLength = Math.sqrt(((Math.pow((entityX - camX), 2) + Math.pow((entityZ - camZ), 2))) + Math.pow((entityY - camY), 2)) - 0.5D;

				//calculate yaw between pure north and entity
				float yaw = (float) -Math.atan2((entityX - camX), (entityZ - camZ));
				//Apply that yaw to create temporary Z coordinates
				double tempZ = camZ * Math.cos(yaw);
				//calculate the pitch between temp reference and entity
				float pitch = (float) (Math.atan2((entityZ - tempZ), (entityY - camY)) - (Math.PI / 2));

				//renderPlayer.addChatMessage(new TextComponentTranslation(Double.toString(pitch) + ", " + Double.toString(yaw)));

				//Use this pitch and yaw to calculate the plate coordinates of the new distance
				//Apply yaw
				double Xyaw = (float) (Math.sin(yaw) * -newLength);
				double Zyaw = (float) (Math.cos(yaw) * newLength);
				//Apply pitch
				double Xmodified = Math.cos(pitch) * Xyaw;
				double Ymodified = Math.sin(pitch) * Math.sqrt(Math.pow((Xyaw), 2) + Math.pow((Zyaw), 2));
				double Zmodified = Math.cos(pitch) * Zyaw;

				//Add these deltas to the camera coordinates to find the nameplate coordinate
				double nameplateX = camX + Xmodified;
				double nameplateY = camY + Ymodified;
				double nameplateZ = camZ + Zmodified;

				renderPlayer.addChatMessage(new TextComponentTranslation(Double.toString(nameplateX) + ", " + Double.toString(nameplateY) + ", " + Double.toString(nameplateZ)));

				//Calculate distance between renderviewentity and nameplate coordinates
				double renderX = renderViewEntity.lastTickPosX + (renderViewEntity.posX - renderViewEntity.lastTickPosX) * animTime;
				double renderY = renderViewEntity.lastTickPosY + (renderViewEntity.posY - renderViewEntity.lastTickPosY) * animTime;
				double renderZ = renderViewEntity.lastTickPosZ + (renderViewEntity.posZ - renderViewEntity.lastTickPosZ) * animTime;

				double xdist = nameplateX - renderX;
				double ydist = nameplateY - renderY;
				double zdist = nameplateZ - renderZ;

				//Render nameplates for /me's
                try {
    				for (Long l : data.meMessages.keySet()) {
    					for (String s : data.meMessages.get(l)) {
    						ClientEventHandler.renderLivingLabel(renderPlayer, s, xdist, ydist, zdist, 64);
    						meHeight -= 0.25F;
    					}

    					if (systemTime > l) data.meMessages.remove(l);
    				}
                }
                catch (ConcurrentModificationException e) {
                }
			}

			if (renderPlayer.isSpectator()) {
				float height = ((Entity)renderPlayer).getEyeHeight() + 0.25F + (0.5F * data.getPartConfig(EnumParts.HEAD).scaleY) - (renderPlayer.isSneaking() ? 0.25F : 0.0F);
				ClientEventHandler.renderName(renderPlayer, height);
				return;
			}

			Minecraft mc = Minecraft.getMinecraft();
			GlStateManager.pushMatrix();
			if (ClientEventHandler.camera.enabled && renderPlayer == mc.thePlayer) {
				renderPlayer.rotationPitch -= ClientEventHandler.camera.cameraPitch + ClientEventHandler.camera.playerPitch;
				renderPlayer.prevRotationPitch -= ClientEventHandler.camera.cameraPitch + ClientEventHandler.camera.playerPitch;
				mc.entityRenderer.getMouseOver(animTime);
			}

			GlStateManager.translate(0.0F, data.modelOffsetY, 0.0F);

			float offset = data.getOffsetCamera(renderPlayer);
			renderPlayer.eyeHeight = renderPlayer.getDefaultEyeHeight() - offset;
			if (!data.resourceInit && lastSkinTick > 6L) {
				this.loadPlayerResource(renderPlayer, data);
				lastSkinTick = 0L;
				data.resourceInit = true;
			}

            List<LayerRenderer<?>> layers = event.getRenderer().layerRenderers;
            Iterator<LayerRenderer<?>> var8 = layers.iterator();
            LayerRenderer layer = null;

			Entity entity = data.getEntity(renderPlayer);
			if (entity != null) {
				if (ClientEventHandler.camera.enabled && renderPlayer == mc.thePlayer) {
					entity.rotationPitch = renderPlayer.rotationPitch;
					entity.prevRotationPitch = renderPlayer.prevRotationPitch;
				}

				event.setCanceled(true);
				if (PixelmonHelper.isPixelmon(entity)) {
					entity.setSneaking(true);
				}

				if (entity instanceof EntityTameable) {
					if (renderPlayer.isSneaking()) {
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

	            Double x = (((renderPlayer.posX - renderPlayer.lastTickPosX) * animTime + renderPlayer.lastTickPosX) - ((renderViewEntity.posX - renderViewEntity.lastTickPosX) * animTime + renderViewEntity.lastTickPosX));
	           	Double y = (((renderPlayer.posY - renderPlayer.lastTickPosY) * animTime + renderPlayer.lastTickPosY) - ((renderViewEntity.posY - renderViewEntity.lastTickPosY) * animTime + renderViewEntity.lastTickPosY));
	           	Double z = (((renderPlayer.posZ - renderPlayer.lastTickPosZ) * animTime + renderPlayer.lastTickPosZ) - ((renderViewEntity.posZ - renderViewEntity.lastTickPosZ) * animTime + renderViewEntity.lastTickPosZ));

				GlStateManager.translate(
						(x * (1.0F - data.entityScaleX)),
						(y * (1.0F - data.entityScaleY)),
						(z * (1.0F - data.entityScaleX))
					);

				//These rotate functions were neccesary when we had separate X and Z sliders, but that feature was cut. Too many bugs, and even when it worked it looked 20 fps
				//GlStateManager.rotate(player.renderYawOffset, 0.0F, -1.0F, 0.0F);
				GlStateManager.scale(data.entityScaleX, data.entityScaleY, data.entityScaleX);
				//GlStateManager.rotate(-player.renderYawOffset, 0.0F, -1.0F, 0.0F);

				mc.getRenderManager().renderEntityStatic(entity, animTime, false);

				GlStateManager.popMatrix();

				ClientEventHandler.renderName(renderPlayer, data.offsetY() + 2.8F);

                while(var8.hasNext()) {
                    try {
                    	layer = var8.next();
                    }
                    catch (ConcurrentModificationException e) {
                    	return;
                    }
                    if (layer instanceof LayerProp) {
                   	 GlStateManager.translate(x, y, z);
                        ((LayerProp) layer).doRenderLayer(renderPlayer, 0, 0, 0, 0, 0, 0, 0);
                        GlStateManager.translate(-x, -y, -z);
                    }
               }

			} else {
				offset = 0.0F;
				if (!MorePlayerModels.DisableFlyingAnimation && renderPlayer.capabilities.isFlying && renderPlayer.worldObj.isAirBlock(renderPlayer.getPosition())) {
					offset = MathHelper.cos((float)renderPlayer.ticksExisted * 0.1F) * -0.06F;
				}

				GlStateManager.translate(0.0F, -offset, 0.0F);

				while(var8.hasNext()) {
                    try {
                    	layer = var8.next();
                    }
                    catch (ConcurrentModificationException e) {
                    	return;
                    }
					if (layer instanceof LayerPreRender) {
						((LayerPreRender)layer).preRender(renderPlayer);
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
