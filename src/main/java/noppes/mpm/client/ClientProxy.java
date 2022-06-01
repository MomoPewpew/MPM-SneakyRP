package noppes.mpm.client;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import noppes.mpm.CommonProxy;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.client.layer.LayerArms;
import noppes.mpm.client.layer.LayerBackItem;
import noppes.mpm.client.layer.LayerBipedArmorAlt;
import noppes.mpm.client.layer.LayerBody;
import noppes.mpm.client.layer.LayerCapeMPM;
import noppes.mpm.client.layer.LayerChatbubble;
import noppes.mpm.client.layer.LayerElytraAlt;
import noppes.mpm.client.layer.LayerEyes;
import noppes.mpm.client.layer.LayerHead;
import noppes.mpm.client.layer.LayerHeadwear;
import noppes.mpm.client.layer.LayerHeldItemAlt;
import noppes.mpm.client.layer.LayerInterface;
import noppes.mpm.client.layer.LayerLegs;
import noppes.mpm.client.layer.LayerProp;
import noppes.mpm.client.model.ModelBipedAlt;
import noppes.mpm.client.model.ModelPlayerAlt;

public class ClientProxy extends CommonProxy {
	public static KeyBinding Screen;
	public static KeyBinding Camera;
	public static KeyBinding Names;
	public static KeyBinding autoWalk;

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Override
	public void load() {
		MorePlayerModels.Channel.register(new PacketHandlerClient());
		MorePlayerModels var10002 = MorePlayerModels.instance;
		new PresetController(MorePlayerModels.dir);
		ClientRegistry.registerKeyBinding(Screen = new KeyBinding("CharacterScreen", 88, "key.categories.gameplay"));
		ClientRegistry.registerKeyBinding(Camera = new KeyBinding("MPM Camera", 71, "key.categories.gameplay"));
		ClientRegistry.registerKeyBinding(Names = new KeyBinding("Show Names", 49, "key.categories.gameplay"));
		ClientRegistry.registerKeyBinding(autoWalk = new KeyBinding("Auto Walk", 41, "key.categories.gameplay"));
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
		if (MorePlayerModels.EnableUpdateChecker) {
			VersionChecker checker = new VersionChecker();
			checker.start();
		}

		MinecraftForge.EVENT_BUS.register(new RenderEvent());
	}

	@Override
	public void postLoad() {
		fixModels(true);
	}

	public static void fixModels(boolean init) {
		Map map = Minecraft.getMinecraft().getRenderManager().getSkinMap();
		Iterator var2 = map.keySet().iterator();

		while(var2.hasNext()) {
			String type = (String)var2.next();
			RenderPlayer render = (RenderPlayer)map.get(type);
			fixModels(render, type.equals("slim"), !init);
			boolean hasMPMLayers = false;
			List list = render.layerRenderers;
			Iterator var7 = list.iterator();

			while(var7.hasNext()) {
				LayerRenderer layer = (LayerRenderer)var7.next();
				if (layer instanceof LayerInterface) {
					((LayerInterface)layer).setModel(render.getMainModel());
					hasMPMLayers = true;
				}
			}

			if (!hasMPMLayers) {
				addLayers(render);
			}
		}

	}

	private static void fixModels(RenderPlayer render, boolean slim, boolean fix) {
		render.mainModel = new ModelPlayerAlt(0.0F, slim);

		Iterator ita = render.layerRenderers.iterator();

		while(ita.hasNext()) {
			LayerRenderer layer = (LayerRenderer)ita.next();
			if (layer instanceof LayerArmorBase) {
				LayerArmorBase l = (LayerArmorBase)layer;
				if (!MorePlayerModels.Compatibility) {
					ObfuscationReflectionHelper.setPrivateValue(LayerArmorBase.class, l, new ModelBipedAlt(0.5F), 1);
					ObfuscationReflectionHelper.setPrivateValue(LayerArmorBase.class, l, new ModelBipedAlt(1.0F), 2);
				} else if (fix) {
					ObfuscationReflectionHelper.setPrivateValue(LayerArmorBase.class, l, new ModelBiped(0.5F), 1);
					ObfuscationReflectionHelper.setPrivateValue(LayerArmorBase.class, l, new ModelBiped(1.0F), 2);
				}
			}

			if (layer instanceof LayerCustomHead) {
				ObfuscationReflectionHelper.setPrivateValue(LayerCustomHead.class, (LayerCustomHead)layer, render.getMainModel().bipedHead, 0);
			}

			if (layer instanceof LayerElytra) {
				ita.remove();
			}
		}

		LayerRenderer layer = new LayerElytraAlt(render);
		render.layerRenderers.add(layer);
	}

	private static void addLayers(RenderPlayer playerRender) {
		List list = playerRender.layerRenderers;
		list.removeIf((layer) -> {
			return layer instanceof LayerCape || layer.getClass() == LayerBipedArmor.class || layer.getClass() == LayerHeldItem.class;
		});
		list.add(1, new LayerEyes(playerRender));
		list.add(2, new LayerHead(playerRender));
		list.add(3, new LayerBody(playerRender));
		list.add(4, new LayerArms(playerRender));
		list.add(5, new LayerLegs(playerRender));
		list.add(6, new LayerHeadwear(playerRender));
		list.add(new LayerCapeMPM(playerRender));
		list.add(new LayerChatbubble(playerRender));
		//list.add(new LayerBackItem(playerRender));
		list.add(new LayerProp(playerRender));
		list.add(new LayerBipedArmorAlt(playerRender));
		list.add(new LayerHeldItemAlt(playerRender));
	}

	public static void bindTexture(ResourceLocation location) {
		if (location != null) {
			TextureManager manager = Minecraft.getMinecraft().getTextureManager();
			ITextureObject textureObject = manager.getTexture(location);
			if (textureObject == null) {
				textureObject = new SimpleTexture(location);
				manager.loadTexture(location, (ITextureObject)textureObject);
			}

			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.bindTexture(((ITextureObject)textureObject).getGlTextureId());
			GlStateManager.popMatrix();
		}
	}
}
