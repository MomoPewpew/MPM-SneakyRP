package noppes.mpm.client;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import java.io.File;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.entity.MPMRenderEntityUtil;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noppes.mpm.LogWriter;
import noppes.mpm.ModelData;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.client.layer.LayerPreRender;
import noppes.mpm.constants.EnumAnimation;
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
  public void post(RenderLivingEvent.Post event) {
    if (textureObject != null) {
      TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
      texturemanager.loadTexture(skinResource, textureObject);
      skinResource = null;
      textureObject = null;
    }
    if (!(event.getEntity() instanceof AbstractClientPlayer))
      return;
    AbstractClientPlayer player = (AbstractClientPlayer)event.getEntity();
    ModelData data = ModelData.get((EntityPlayer)player);
    if (data.isSleeping())
      player.renderYawOffset = player.prevRenderYawOffset = player.rotationYaw;
    GlStateManager.popMatrix();
  }

  @SubscribeEvent
  public void pre(RenderLivingEvent.Pre event) {
    if (!(event.getEntity() instanceof AbstractClientPlayer))
      return;
    AbstractClientPlayer player = (AbstractClientPlayer)event.getEntity();
    Minecraft mc = Minecraft.getMinecraft();
    GlStateManager.pushMatrix();
    if (ClientEventHandler.camera.enabled && player == mc.thePlayer) {
      player.rotationPitch -= ClientEventHandler.camera.cameraPitch + ClientEventHandler.camera.playerPitch;
      player.prevRotationPitch -= ClientEventHandler.camera.cameraPitch + ClientEventHandler.camera.playerPitch;
      mc.entityRenderer.getMouseOver(Animation.getPartialTickTime());
    }
    ModelData data = ModelData.get((EntityPlayer)player);
    if (data.isSleeping()) {
      if (data.animation == EnumAnimation.SLEEPING_EAST)
        player.renderYawOffset = player.prevRenderYawOffset = -90.0F;
      if (data.animation == EnumAnimation.SLEEPING_WEST)
        player.renderYawOffset = player.prevRenderYawOffset = 90.0F;
      if (data.animation == EnumAnimation.SLEEPING_NORTH)
        player.renderYawOffset = player.prevRenderYawOffset = 180.0F;
      if (data.animation == EnumAnimation.SLEEPING_SOUTH)
        player.renderYawOffset = player.prevRenderYawOffset = 0.0F;
    }
    float offset = data.getOffsetCamera((EntityPlayer)player);
    player.eyeHeight = player.getDefaultEyeHeight() - offset;
    if (!data.resourceInit && lastSkinTick > 6L) {
      loadPlayerResource((EntityPlayer)player, data);
      lastSkinTick = 0L;
      data.resourceInit = true;
    }
    EntityLivingBase entityLivingBase = data.getEntity((EntityPlayer)player);
    if (entityLivingBase != null) {
      if (ClientEventHandler.camera.enabled && player == mc.thePlayer) {
        ((Entity)entityLivingBase).rotationPitch = player.rotationPitch;
        ((Entity)entityLivingBase).prevRotationPitch = player.prevRotationPitch;
      }
      event.setCanceled(true);
      if (PixelmonHelper.isPixelmon((Entity)entityLivingBase))
        entityLivingBase.setSneaking(true);
      if (data.textureObject != null) {
        TextureManager texturemanager = mc.getTextureManager();
        if (textureObject != null)
          texturemanager.loadTexture(skinResource, textureObject);
        skinResource = MPMRenderEntityUtil.getResource((Entity)entityLivingBase);
        textureObject = texturemanager.getTexture(skinResource);
        if (textureObject == null) {
          skinResource = null;
        } else {
          texturemanager.loadTexture(skinResource, (ITextureObject)data.textureObject);
        }
      }
      mc.getRenderManager().renderEntityStatic((Entity)entityLivingBase, Animation.getPartialTickTime(), false);
      GlStateManager.popMatrix();
      return;
    }
    offset = 0.0F;
    if (!MorePlayerModels.DisableFlyingAnimation && player.capabilities.isFlying && player.worldObj.isAirBlock(player.getPosition()))
      offset = MathHelper.cos(player.ticksExisted * 0.1F) * -0.06F;
    if (data.animation == EnumAnimation.SITTING)
      offset = (float)(offset + 0.5D - data.getLegsY() * 0.8D);
    GlStateManager.translate(0.0F, -offset, 0.0F);
    List<LayerRenderer> layers = (event.getRenderer()).layerRenderers;
    for (LayerRenderer layer : layers) {
      if (layer instanceof LayerPreRender)
        ((LayerPreRender)layer).preRender(player);
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
        } catch (Exception e) {
          location = DefaultPlayerSkin.getDefaultSkinLegacy();
        }
        setPlayerTexture(player, location);
      } else {
        boolean hasUrl = (data.getEntity(pl) == null);
        ResourceLocation location = new ResourceLocation("skins/" + (data.url + hasUrl).hashCode());
        if (!player.hasPlayerInfo()) {
          location = player.getLocationSkin();
        } else {
          setPlayerTexture(player, location);
        }
        data.textureObject = loadTexture(null, location, DefaultPlayerSkin.getDefaultSkinLegacy(), data.url, hasUrl);
      }
      return;
    }
    if (!data.resourceLoaded) {
      Map map = minecraft.getSkinManager().loadSkinFromCache(pl.getGameProfile());
      if (map.isEmpty())
        map = minecraft.getSessionService().getTextures(minecraft.getSessionService().fillProfileProperties(player.getGameProfile(), false), false);
      if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
        MinecraftProfileTexture profile = (MinecraftProfileTexture)map.get(MinecraftProfileTexture.Type.SKIN);
        File dir = new File((File)ObfuscationReflectionHelper.getPrivateValue(SkinManager.class, minecraft.getSkinManager(), 2), profile.getHash().substring(0, 2));
        File file = new File(dir, profile.getHash());
        if (file.exists())
          file.delete();
        ResourceLocation location = new ResourceLocation("skins/" + profile.getHash());
        loadTexture(file, location, DefaultPlayerSkin.getDefaultSkinLegacy(), profile.getUrl(), true);
        setPlayerTexture(player, location);
        data.resourceLoaded = true;
        return;
      }
    }
    setPlayerTexture(player, null);
  }

  private void setPlayerTexture(AbstractClientPlayer player, ResourceLocation texture) {
    NetworkPlayerInfo playerInfo = (NetworkPlayerInfo)ObfuscationReflectionHelper.getPrivateValue(AbstractClientPlayer.class, player, 0);
    if (playerInfo == null)
      return;
    Map<MinecraftProfileTexture.Type, ResourceLocation> playerTextures = (Map<MinecraftProfileTexture.Type, ResourceLocation>)ObfuscationReflectionHelper.getPrivateValue(NetworkPlayerInfo.class, playerInfo, 1);
    playerTextures.put(MinecraftProfileTexture.Type.SKIN, texture);
    if (texture == null)
      ObfuscationReflectionHelper.setPrivateValue(NetworkPlayerInfo.class, playerInfo, Boolean.valueOf(false), 4);
  }

  private ITextureObject loadTexture(File file, ResourceLocation resource, ResourceLocation def, String par1Str, boolean fix64) {
    ImageDownloadAlt imageDownloadAlt;
    TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
    ITextureObject object = texturemanager.getTexture(resource);
    if (object == null) {
      imageDownloadAlt = new ImageDownloadAlt(file, par1Str, def, (IImageBuffer)new ImageBufferDownloadAlt(fix64));
      texturemanager.loadTexture(resource, (ITextureObject)imageDownloadAlt);
    }
    return (ITextureObject)imageDownloadAlt;
  }

  @SubscribeEvent
  public void hand(RenderHandEvent event) {
    Minecraft mc = Minecraft.getMinecraft();
    ModelData data = ModelData.get((EntityPlayer)mc.thePlayer);
    mc.thePlayer.eyeHeight = mc.thePlayer.getDefaultEyeHeight() - data.getOffsetCamera((EntityPlayer)mc.thePlayer);
    EntityLivingBase entityLivingBase = data.getEntity((EntityPlayer)mc.thePlayer);
    if (entityLivingBase != null || data.isSleeping() || data.animation == EnumAnimation.CRAWLING || (data.animation == EnumAnimation.BOW && mc.thePlayer.getHeldItemMainhand() == null)) {
      event.setCanceled(true);
      return;
    }
    if (!data.resourceInit && lastSkinTick > 6L) {
      loadPlayerResource((EntityPlayer)mc.thePlayer, data);
      lastSkinTick = 0L;
      data.resourceInit = true;
    }
  }

  @SubscribeEvent
  public void chat(ClientChatReceivedEvent event) {
    if (MorePlayerModels.HasServerSide)
      return;
    try {
      ChatMessages.parseMessage(event.getMessage().getFormattedText());
    } catch (Exception ex) {
      LogWriter.warn("Cant handle chatmessage: " + event.getMessage() + ":" + ex.getMessage());
    }
  }

  @SubscribeEvent
  public void selectionBox(DrawBlockHighlightEvent event) {
    if (MorePlayerModels.HideSelectionBox)
      event.setCanceled(true);
  }

  @SubscribeEvent
  public void overlay(RenderGameOverlayEvent.Post event) {
    if (event.getType() != RenderGameOverlayEvent.ElementType.ALL)
      return;
    Minecraft mc = Minecraft.getMinecraft();
    if (mc.currentScreen != null || MorePlayerModels.Tooltips == 0)
      return;
    ItemStack item = mc.thePlayer.getHeldItemMainhand();
    if (item.isEmpty())
      return;
    String name = item.getDisplayName();
    int x = event.getResolution().getScaledWidth() - mc.fontRendererObj.getStringWidth(name);
    int posX = 4;
    int posY = 4;
    if (MorePlayerModels.Tooltips % 2 == 0)
      posX = x - 4;
    if (MorePlayerModels.Tooltips > 2)
      posY = event.getResolution().getScaledHeight() - 24;
    mc.fontRendererObj.drawStringWithShadow(name, posX, posY, 16777215);
    if (item.isItemStackDamageable()) {
      int max = item.getMaxDamage();
      String dam = (max - item.getItemDamage()) + "/" + max;
      x = event.getResolution().getScaledWidth() - mc.fontRendererObj.getStringWidth(dam);
      if (MorePlayerModels.Tooltips == 2 || MorePlayerModels.Tooltips == 4)
        posX = x - 4;
      mc.fontRendererObj.drawStringWithShadow(dam, posX, (posY + 12), 16777215);
    }
  }
}
