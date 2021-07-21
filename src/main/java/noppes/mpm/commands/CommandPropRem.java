package noppes.mpm.commands;

import java.io.File;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.mpm.LogWriter;
import noppes.mpm.ModelData;

public class CommandPropRem extends CommandBase {

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {

		if (args.length == 0) return;

        File dir = null;
        dir = new File(dir, ".." + File.separator + "moreplayermodels" + File.separator + "propGroupsNamed");
        if (!dir.exists()) {
             dir.mkdirs();
        }

        String filename = args[0].toLowerCase() + ".dat";

        try {
             File file = new File(dir, filename);

             if (!file.exists()) {
            	 icommandsender.addChatMessage(new TextComponentTranslation("The PropGroup " + args[0] + " was not found on the server."));
            	 return;
             }

             File dirnew = null;
             dirnew = new File(dirnew, ".." + File.separator + "moreplayermodels" + File.separator + "propGroupsNamed" + File.separator + "archive");

             if (!dirnew.exists()) {
            	 dirnew.mkdirs();
            }

             String filenamenew = args[0].toLowerCase() + "-" + System.currentTimeMillis() + ".dat";
             File filenew = new File(dirnew, filenamenew);

        	 file.renameTo(filenew);
        } catch (Exception var6) {
             LogWriter.except(var6);
             var6.printStackTrace();
        }
	}

	@Override
	public String getCommandName() {
		return "proprem";
	}

	@Override
	public String getCommandUsage(ICommandSender arg0) {
		return "/proprem <name>";
	}

}
