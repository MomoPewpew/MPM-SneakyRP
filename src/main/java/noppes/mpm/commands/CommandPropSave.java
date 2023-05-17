package noppes.mpm.commands;

import java.io.File;
import java.io.FileOutputStream;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.mpm.LogWriter;
import noppes.mpm.ModelData;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.PropGroup;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;

public class CommandPropSave extends MpmCommandInterface {

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {

		if (args.length == 0) throw new WrongUsageException(this.getCommandUsage(icommandsender));

		EntityPlayerMP player = (EntityPlayerMP) icommandsender;

		ModelData data = ModelData.get(player);

		EntityLivingBase entity = data.getEntity(player);
		String newName = args[0].toLowerCase();

		if (entity != null) {
			if (!args[0].startsWith(entity.getName().replace(" ", "").toLowerCase() + "_")) {
				newName = (entity.getName().replace(" ", "") + "_" + args[0]).toLowerCase();
			}
		}

		String filename = newName + ".dat";

		try {
			PropGroup propGroup = new PropGroup(player);
			PropGroup propGroupOld = null;

			for (PropGroup propGroupTemp : data.propGroups) {
				if (propGroupTemp.name.toLowerCase().equals(args[0].toLowerCase())) {
					if (propGroup.name.equals("")) {
						propGroupOld = propGroupTemp;
						propGroup.readFromNBT(propGroupTemp.writeToNBT());
					} else {
						icommandsender.addChatMessage(new TextComponentTranslation("Multiple PropGroups named " + args[0] + " were found on your model. Please delete or rename one of them."));
						return;
					}
				}
			}

			if (propGroup.name.equals("")) {
				icommandsender.addChatMessage(new TextComponentTranslation("No PropGroup named " + args[0] + " was found on your model. Please make sure that the prop is grouped and check the group name."));
				return;
			}

			File dir = null;
			dir = new File(dir, MorePlayerModels.assetRootFolder + File.separator + "propGroupsNamed" + File.separator + "restricted");
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File file = new File(dir, filename);

			if (!file.exists()) {
				dir = null;
				dir = new File(dir, MorePlayerModels.assetRootFolder + File.separator + "propGroupsNamed");
				if (!dir.exists()) {
					dir.mkdirs();
				}

				file = new File(dir, filename);
			}

			if (file.exists()) {
				icommandsender.addChatMessage(new TextComponentTranslation("The prop " + newName + " already exists. Either /proprem the old version first, or give this one a new name"));
				return;
			}

			if (!args[0].toLowerCase().equals(newName)) {
				propGroupOld.name = newName;
				propGroup.name = newName;

				Server.sendAssociatedData(player, EnumPackets.SEND_PLAYER_DATA, player.getUniqueID(), data.writeToNBT());
			}

			icommandsender.addChatMessage(new TextComponentTranslation("The prop " + args[0] + " was succesfully saved to the system"));
			CompressedStreamTools.writeCompressed(propGroup.writeToNBT(), new FileOutputStream(file));
		} catch (Exception var6) {
			LogWriter.except(var6);
			var6.printStackTrace();
		}
	}

	@Override
	public String getCommandName() {
		return "propsave";
	}

	@Override
	public String getCommandUsage(ICommandSender arg0) {
		return "/propsave <Name of Prop Group on your current model>. Be sure to move the prop into a group first, and then name the group!";
	}
}
