package noppes.mpm.commands;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.mpm.LogWriter;
import noppes.mpm.ModelData;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.PropGroup;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;

public class CommandPropLoad extends CommandBase {

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {

		if (args.length == 0) {
			MorePlayerModels.syncPropGroupFileNames((EntityPlayerMP) icommandsender);
			Server.sendData((EntityPlayerMP) icommandsender, EnumPackets.PROPGROUPS_LOAD_GUI);
			return;
		}

		String filename = args[0].toLowerCase() + ".dat";

		try {
			File file;

			File dir = null;
			dir = new File(dir, MorePlayerModels.assetRootFolder + File.separator + "propGroupsNamed");

			if (!dir.exists()) {
				dir.mkdirs();
			}

			file = new File(dir, filename);

			if (!file.exists() && MorePlayerModels.playersEntityDenied.contains(((EntityPlayer) icommandsender).getUniqueID())) {
				dir = null;
				dir = new File(dir, MorePlayerModels.assetRootFolder + File.separator + "propGroupsNamed" + File.separator + "restricted");

				if (!dir.exists()) {
					dir.mkdirs();
				}

				file = new File(dir, filename);
			}

			if (!file.exists()) {
				icommandsender.addChatMessage(new TextComponentTranslation("The PropGroup " + args[0] + " was not found on the server."));
				return;
			}

			ModelData data = ModelData.get((EntityPlayer) icommandsender);

			PropGroup propGroup = new PropGroup((EntityPlayer) icommandsender);

			NBTTagCompound compound = CompressedStreamTools.readCompressed(new FileInputStream(file));

			propGroup.readFromNBT(compound);

			data.addPropGroupServer(propGroup);
		} catch (Exception var4) {
			LogWriter.except(var4);
		}
	}

	@Override
	public String getCommandName() {
		return "propload";
	}

	@Override
	public String getCommandUsage(ICommandSender arg0) {
		return "/propload <name>";
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getCommandAliases()
	{
		return new ArrayList<String>(Arrays.asList(
		"propload",
		"pl"
		));
	}
}
