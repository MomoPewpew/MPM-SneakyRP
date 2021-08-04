package noppes.mpm.commands;

import java.io.File;
import java.io.FileOutputStream;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.mpm.LogWriter;
import noppes.mpm.ModelData;
import noppes.mpm.PropGroup;

public class CommandPropSave extends CommandBase {

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {

		if (args.length == 0) return;

		ModelData data = ModelData.get((EntityPlayer) icommandsender);

		String filename = args[0].toLowerCase() + ".dat";

		try {
			PropGroup propGroup = new PropGroup((EntityPlayer) icommandsender);

			for (PropGroup propGroupTemp : data.propGroups) {
				if (propGroupTemp.name.toLowerCase().equals(args[0].toLowerCase())) {
					if (propGroup.name.equals("")) {
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
			dir = new File(dir, ".." + File.separator + "moreplayermodels" + File.separator + "propGroupsNamed" + File.separator + "restricted");
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File file = new File(dir, filename);

			if (!file.exists()) {
				dir = null;
				dir = new File(dir, ".." + File.separator + "moreplayermodels" + File.separator + "propGroupsNamed");
				if (!dir.exists()) {
					dir.mkdirs();
				}

				file = new File(dir, filename);
			}

			if (file.exists()) {
				icommandsender.addChatMessage(new TextComponentTranslation("The prop " + args[0] + " already exists. Either /proprem the old version first, or give this one a new name"));
				return;
			}

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
		return "/propsave <Name of Prop Group on your current model>";
	}
}
