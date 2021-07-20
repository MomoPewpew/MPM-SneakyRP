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
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.mpm.LogWriter;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;

public class CommandSkinLoad extends CommandBase {

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {

		if (args.length == 0) return;

		String filename = args[0] + ".dat";
		File file;

		File dir = null;
		dir = new File(dir, ".." + File.separator + "moreplayermodels" + File.separator + "skins");

        if (!dir.exists()) {
              return;
         }

        try {
             file = new File(dir, filename);

             if (!file.exists()) {
            	 icommandsender.addChatMessage(new TextComponentTranslation("The skin " + args[0] + " was not found on the server."));
            	 return;
             }

             NBTTagCompound compound = new NBTTagCompound();

             compound = CompressedStreamTools.readCompressed(new FileInputStream(file));

             Server.sendAssociatedData((Entity) icommandsender, EnumPackets.SEND_PLAYER_DATA, ((Entity) icommandsender).getUniqueID(), compound);
        } catch (Exception var4) {
             LogWriter.except(var4);
        }
	}

	@Override
	public String getCommandName() {
		return "skinload";
	}

	@Override
	public String getCommandUsage(ICommandSender arg0) {
		return "/skinload <name>";
	}



	@SuppressWarnings("unchecked")
	@Override
	public List getCommandAliases()
		{
			return new ArrayList<String>(Arrays.asList(
				     "skinload",
				     "sl"
				));
		}
}
