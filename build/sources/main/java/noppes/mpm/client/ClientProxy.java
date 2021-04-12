package noppes.mpm.client;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import micdoodle8.mods.galacticraft.api.client.tabs.AbstractTab;
import micdoodle8.mods.galacticraft.api.client.tabs.InventoryTabMPM;
import micdoodle8.mods.galacticraft.api.client.tabs.InventoryTabVanilla;
import micdoodle8.mods.galacticraft.api.client.tabs.TabRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ICommand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import noppes.mpm.CommonProxy;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.client.commands.CommandBow;
import noppes.mpm.client.commands.CommandCrawl;
import noppes.mpm.client.commands.CommandCry;
import noppes.mpm.client.commands.CommandDance;
import noppes.mpm.client.commands.CommandHug;
import noppes.mpm.client.commands.CommandNo;
import noppes.mpm.client.commands.CommandPoint;
import noppes.mpm.client.commands.CommandSit;
import noppes.mpm.client.commands.CommandSleep;
import noppes.mpm.client.commands.CommandWag;
import noppes.mpm.client.commands.CommandWave;
import noppes.mpm.client.commands.CommandYes;
import noppes.mpm.client.layer.LayerArms;
import noppes.mpm.client.layer.LayerBackItem;
import noppes.mpm.client.layer.LayerBody;
import noppes.mpm.client.layer.LayerCapeMPM;
import noppes.mpm.client.layer.LayerChatbubble;
import noppes.mpm.client.layer.LayerElytraAlt;
import noppes.mpm.client.layer.LayerEyes;
import noppes.mpm.client.layer.LayerHead;
import noppes.mpm.client.layer.LayerHeadwear;
import noppes.mpm.client.layer.LayerInterface;
import noppes.mpm.client.layer.LayerLegs;
import noppes.mpm.client.model.ModelBipedAlt;
import noppes.mpm.client.model.ModelPlayerAlt;

public class ClientProxy extends CommonProxy{
	public static KeyBinding Screen;
	public static KeyBinding Sleep;
	public static KeyBinding Sit;
	public static KeyBinding Dance;
	public static KeyBinding Hug;
	public static KeyBinding Crawl;
	public static KeyBinding Camera;

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void load() {
		MorePlayerModels.Channel.register(new PacketHandlerClient());
		new PresetController(MorePlayerModels.instance.dir);

		ClientRegistry.registerKeyBinding(Screen = new KeyBinding("CharacterScreen", 88, "key.categories.gameplay"));
		ClientRegistry.registerKeyBinding(Sleep = new KeyBinding("MPM 1", 44, "key.categories.gameplay"));
		ClientRegistry.registerKeyBinding(Sit = new KeyBinding("MPM 2", 0, "key.categories.gameplay"));
		ClientRegistry.registerKeyBinding(Dance = new KeyBinding("MPM 3", 0, "key.categories.gameplay"));
		ClientRegistry.registerKeyBinding(Hug = new KeyBinding("MPM 4", 0, "key.categories.gameplay"));
		ClientRegistry.registerKeyBinding(Crawl = new KeyBinding("MPM 5", 0, "key.categories.gameplay"));
		ClientRegistry.registerKeyBinding(Camera = new KeyBinding("MPM Camera", 0, "key.categories.gameplay"));

	    MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
	    if (MorePlayerModels.EnableUpdateChecker) {
	      VersionChecker checker = new VersionChecker();
	      checker.start();
	    }

		MinecraftForge.EVENT_BUS.register(new RenderEvent());
		ClientCommandHandler.instance.registerCommand(new CommandBow());
		ClientCommandHandler.instance.registerCommand(new CommandCrawl());
		ClientCommandHandler.instance.registerCommand(new CommandCry());
		ClientCommandHandler.instance.registerCommand(new CommandDance());
		ClientCommandHandler.instance.registerCommand(new CommandHug());
		ClientCommandHandler.instance.registerCommand(new CommandNo());
		ClientCommandHandler.instance.registerCommand(new CommandPoint());
		ClientCommandHandler.instance.registerCommand(new CommandSit());
		ClientCommandHandler.instance.registerCommand(new CommandSleep());
		ClientCommandHandler.instance.registerCommand(new CommandWag());
		ClientCommandHandler.instance.registerCommand(new CommandWave());
		ClientCommandHandler.instance.registerCommand(new CommandYes());
	}

	@Override
	public void postload() {
	    fixModels(true);
	    if (MorePlayerModels.InventoryGuiEnabled) {
	      MinecraftForge.EVENT_BUS.register(new TabRegistry());
	      if (TabRegistry.getTabList().size() < 2)
	        TabRegistry.registerTab((AbstractTab)new InventoryTabVanilla());
	      TabRegistry.registerTab((AbstractTab)new InventoryTabMPM());
	    }
	}

	  public static void fixModels(boolean init) {
		    Map<String, RenderPlayer> map = Minecraft.getMinecraft().getRenderManager().getSkinMap();
		    for (String type : map.keySet()) {
		      RenderPlayer render = map.get(type);
		      fixModels(render, type.equals("slim"), !init);
		      boolean hasMPMLayers = false;
		      List<? extends LayerRenderer> list = render.layerRenderers;
		      for (LayerRenderer layer : list) {
		        if (layer instanceof LayerInterface) {
		          ((LayerInterface)layer).setModel(render.getMainModel());
		          hasMPMLayers = true;
		        }
		      }
		      if (!hasMPMLayers)
		        addLayers(render);
		    }
		  }

	  private static void fixModels(RenderPlayer render, boolean slim, boolean fix) {
		    if (!MorePlayerModels.Compatibility) {
		      render.mainModel = (ModelBase)new ModelPlayerAlt(0.0F, slim);
		    } else if (fix) {
		      render.mainModel = (ModelBase)new ModelPlayer(0.0F, slim);
		    }
		    Iterator<? extends LayerRenderer> ita = render.layerRenderers.iterator();
		    while (ita.hasNext()) {
		      LayerRenderer layer = ita.next();
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
		      if (layer instanceof LayerCustomHead)
		        ObfuscationReflectionHelper.setPrivateValue(LayerCustomHead.class, layer, (render.getMainModel()).bipedHead, 0);
		      if (layer instanceof net.minecraft.client.renderer.entity.layers.LayerElytra)
		        ita.remove();
		    }
		    LayerElytraAlt layerElytraAlt = new LayerElytraAlt(render);
		    render.layerRenderers.add(layerElytraAlt);
		  }

	private static void addLayers(RenderPlayer playerRender){
		List<LayerRenderer<AbstractClientPlayer>> list = playerRender.layerRenderers;
	    list.removeIf(layer -> layer instanceof net.minecraft.client.renderer.entity.layers.LayerCape);
	    list.add(1, new LayerEyes(playerRender));
	    list.add(2, new LayerHead(playerRender));
	    list.add(3, new LayerBody(playerRender));
	    list.add(4, new LayerArms(playerRender));
	    list.add(5, new LayerLegs(playerRender));
	    list.add(6, new LayerHeadwear(playerRender));
	    list.add(new LayerCapeMPM(playerRender));
	    list.add(new LayerChatbubble(playerRender));
	    list.add(new LayerBackItem(playerRender));
	}

	public static void bindTexture(ResourceLocation location){
		if(location == null)
			return;
        TextureManager manager = Minecraft.getMinecraft().getTextureManager();
    	ITextureObject ob = manager.getTexture(location);
    	if(ob == null){
    		ob = new SimpleTexture(location);
    		manager.loadTexture(location, ob);
    	}
        GlStateManager.bindTexture(ob.getGlTextureId());

	}
}
