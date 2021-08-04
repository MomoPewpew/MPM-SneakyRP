package noppes.mpm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.ValueType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import noppes.mpm.commands.CommandAngry;
import noppes.mpm.commands.CommandLove;
import noppes.mpm.commands.CommandMPM;
import noppes.mpm.commands.CommandProp;
import noppes.mpm.commands.CommandPropLoad;
import noppes.mpm.commands.CommandPropRem;
import noppes.mpm.commands.CommandPropRestore;
import noppes.mpm.commands.CommandPropSave;
import noppes.mpm.commands.CommandSing;
import noppes.mpm.commands.CommandSkinDel;
import noppes.mpm.commands.CommandSkinLoad;
import noppes.mpm.commands.CommandSkinRestore;
import noppes.mpm.commands.CommandSkinSave;
import noppes.mpm.config.ConfigLoader;
import noppes.mpm.config.ConfigProp;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.constants.EnumPackets;
import noppes.mpm.util.PixelmonHelper;

@Mod(
modid = "moreplayermodels",
name = "MorePlayerModels",
version = "1.12.2",
acceptedMinecraftVersions = "1.12, 1.12.1, 1.12.2"
)
public class MorePlayerModels {
	public static final String MODID = "moreplayermodels";
	public static final String VERSION = "1.12.2";
	@ConfigProp
	public static boolean InventoryGuiEnabled = true;
	@ConfigProp
	public static int Tooltips = 2;
	@SidedProxy(
	clientSide = "noppes.mpm.client.ClientProxy",
	serverSide = "noppes.mpm.CommonProxy"
	)
	public static CommonProxy proxy;
	public static FMLEventChannel Channel;
	public static MorePlayerModels instance;
	public static int Version = 8;
	public static File dir;
	public static boolean HasServerSide = false;
	@ConfigProp(
	info = "Enable different perspective heights for different model sizes"
	)
	public static boolean EnablePOV = true;
	@ConfigProp(
	info = "Enables the item on your back"
	)
	public static boolean EnableBackItem = false;
	@ConfigProp(
	info = "Enables chat bubbles"
	)
	public static boolean EnableChatBubbles = true;
	@ConfigProp(
	info = "Enables MorePlayerModels startup update message"
	)
	public static boolean EnableUpdateChecker = true;
	@ConfigProp(
	info = "Set to false if you dont want to see player particles"
	)
	public static boolean EnableParticles = true;
	@ConfigProp(
	info = "Set to true if you dont want to see hide player names"
	)
	public static boolean HidePlayerNames = false;
	@ConfigProp(
	info = "Set to true if you dont want to see hide selection boxes when pointing to blocks"
	)
	public static boolean HideSelectionBox = false;
	@ConfigProp(
	info = "Set to true if you want no flying animation"
	)
	public static boolean DisableFlyingAnimation = false;
	@ConfigProp(
	info = "Type 0 = Normal, Type 1 = Solid"
	)
	public static int HeadWearType = 1;
	@ConfigProp(
	info = "Disables scaling and animations from more compatibilty with other mods"
	)
	public static boolean Compatibility = false;
	@ConfigProp(
	info = "Used to register buttons to animations"
	)
	public static int button1;
	@ConfigProp(
	info = "Used to register buttons to animations"
	)
	public static int button2;
	@ConfigProp(
	info = "Used to register buttons to animations"
	)
	public static int button3;
	@ConfigProp(
	info = "Used to register buttons to animations"
	)
	public static int button4;
	@ConfigProp(
	info = "Used to register buttons to animations"
	)
	public static int button5;
	public static boolean hasEntityPermission;
	public static List<UUID> playersEntityDenied;
	public static List<String> fileNamesSkins;
	public static List<String> fileNamesPropGroups;
	public static List<String> entityNamesRemovedFromGui;
	public static List<String> blacklistedPropStrings;
	public ConfigLoader configLoader;

	public MorePlayerModels() {
		instance = this;
	}

	@EventHandler
	public void load(FMLPreInitializationEvent ev) {
		LogWriter.info("Loading");
		Channel = NetworkRegistry.INSTANCE.newEventDrivenChannel("MorePlayerModels");
		File dir = new File(ev.getModConfigurationDirectory(), "..");
		MorePlayerModels.dir = new File(dir, "moreplayermodels");
		if (!MorePlayerModels.dir.exists()) {
			MorePlayerModels.dir.mkdir();
		}

		entityNamesRemovedFromGui = new ArrayList<String>();
		blacklistedPropStrings = new ArrayList<String>();

		this.configLoader = new ConfigLoader(this.getClass(), new File(dir, "config"), "MorePlayerModels");
		this.configLoader.loadConfig();
		if (Loader.isModLoaded("Morph")) {
			EnablePOV = false;
		}

		PixelmonHelper.load();
		proxy.load();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
		MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
		MinecraftForge.EVENT_BUS.register(new ServerTickHandler());
		CapabilityManager.INSTANCE.register(ModelData.class, new IStorage() {
			public NBTBase writeNBT(Capability capability, Object instance, EnumFacing side) {
				return null;
			}

			public void readNBT(Capability capability, Object instance, EnumFacing side, NBTBase nbt) {
			}
		}, ModelData.class);

		hasEntityPermission = true;
		fileNamesSkins = new ArrayList<String>();
		playersEntityDenied = new ArrayList<UUID>();
	}

	@EventHandler
	public void load(FMLPostInitializationEvent ev) {
		proxy.postLoad();
	}

	@EventHandler
	public void serverstart(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandLove());
		event.registerServerCommand(new CommandSing());
		event.registerServerCommand(new CommandAngry());
		event.registerServerCommand(new CommandMPM());
		event.registerServerCommand(new CommandProp());
		event.registerServerCommand(new CommandSkinLoad());
		event.registerServerCommand(new CommandSkinSave());
		event.registerServerCommand(new CommandSkinDel());
		event.registerServerCommand(new CommandSkinRestore());
		event.registerServerCommand(new CommandPropLoad());
		event.registerServerCommand(new CommandPropSave());
		event.registerServerCommand(new CommandPropRem());
		event.registerServerCommand(new CommandPropRestore());
		GameRules rules = event.getServer().worldServerForDimension(0).getGameRules();
		if (!rules.hasRule("mpmAllowEntityModels")) {
			rules.addGameRule("mpmAllowEntityModels", "true", ValueType.BOOLEAN_VALUE);
		}

	}

	static {
		button1 = EnumAnimation.SLEEPING_SOUTH.ordinal();
		button2 = EnumAnimation.SITTING.ordinal();
		button3 = EnumAnimation.CRAWLING.ordinal();
		button4 = EnumAnimation.HUG.ordinal();
		button5 = EnumAnimation.DANCING.ordinal();
	}

	public static void syncSkinFileNames(EntityPlayerMP player) {
		File dir = null;
		dir = new File(dir, ".." + File.separator + "moreplayermodels" + File.separator + "skins");

		if (!dir.exists()) dir.mkdirs();

		NBTTagCompound compound = new NBTTagCompound();
		int i = 0;

		for (final File fileEntry : dir.listFiles()) {
			if (fileEntry.isDirectory() || !fileEntry.getName().contains(".dat")) {
				continue;
			} else {
				NBTTagCompound skinCompound = new NBTTagCompound();

				try {
					skinCompound = CompressedStreamTools.readCompressed(new FileInputStream(fileEntry));

					if (!skinCompound.getString("EntityClass").equals("") && playersEntityDenied.contains(player.getUniqueID()))
					continue;
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
				}

				String skinName = new String(fileEntry.getName());
				skinName = skinName.replace(".dat", "");

				compound.setString(("skinName" + String.valueOf(i)), skinName);
				i++;
			}
		}

		dir = null;
		dir = new File(dir, ".." + File.separator + "moreplayermodels" + File.separator + "skins" + File.separator + "unrestricted");

		if (!dir.exists()) dir.mkdirs();

		for (final File fileEntry : dir.listFiles()) {
			if (fileEntry.isDirectory() || !fileEntry.getName().contains(".dat")) {
				continue;
			} else {
				NBTTagCompound skinCompound = new NBTTagCompound();

				try {
					skinCompound = CompressedStreamTools.readCompressed(new FileInputStream(fileEntry));
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
				}

				String skinName = new String(fileEntry.getName());
				skinName = skinName.replace(".dat", "");

				compound.setString(("skinName" + String.valueOf(i)), skinName);
				i++;
			}
		}

		if (!playersEntityDenied.contains(player.getUniqueID())) {
			dir = null;
			dir = new File(dir, ".." + File.separator + "moreplayermodels" + File.separator + "skins" + File.separator + "restricted");

			if (!dir.exists()) dir.mkdirs();

			for (final File fileEntry : dir.listFiles()) {
				if (fileEntry.isDirectory() || !fileEntry.getName().contains(".dat")) {
					continue;
				} else {
					String skinName = new String(fileEntry.getName());
					skinName = skinName.replace(".dat", "");

					compound.setString(("skinName" + String.valueOf(i)), skinName);
					i++;
				}
			}
		}

		Server.sendData(player, EnumPackets.SKIN_FILENAME_UPDATE, compound);
	}

	public static void syncPropGroupFileNames(EntityPlayerMP player) {
		File dir = null;
		dir = new File(dir, ".." + File.separator + "moreplayermodels" + File.separator + "propGroupsNamed");

		if (!dir.exists()) dir.mkdirs();

		NBTTagCompound compound = new NBTTagCompound();
		int i = 0;

		for (final File fileEntry : dir.listFiles()) {
			if (fileEntry.isDirectory() || !fileEntry.getName().contains(".dat")) {
				continue;
			} else {
				String propGroupName = new String(fileEntry.getName());
				propGroupName = propGroupName.replace(".dat", "");

				compound.setString(("propGroupName" + String.valueOf(i)), propGroupName);
				i++;
			}
		}

		if (!playersEntityDenied.contains(player.getUniqueID())) {
			dir = null;
			dir = new File(dir, ".." + File.separator + "moreplayermodels" + File.separator + "propGroupsNamed" + File.separator + "restricted");

			if (!dir.exists()) dir.mkdirs();

			for (final File fileEntry : dir.listFiles()) {
				if (fileEntry.isDirectory() || !fileEntry.getName().contains(".dat")) {
					continue;
				} else {
					String propGroupName = new String(fileEntry.getName());
					propGroupName = propGroupName.replace(".dat", "");

					compound.setString(("propGroupName" + String.valueOf(i)), propGroupName);
					i++;
				}
			}
		}

		Server.sendData(player, EnumPackets.PROPGROUPS_FILENAME_UPDATE, compound);
	}
}
