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

public class CommandSkinDel extends CommandBase {

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {

		if (args.length == 0) return;

		NBTTagCompound compound = ModelData.get((EntityPlayer) icommandsender).writeToNBT();

        File dir = null;
        dir = new File(dir, ".." + File.separator + "moreplayermodels" + File.separator + "skins");
        if (!dir.exists()) {
             dir.mkdirs();
        }

        String filename = args[0].toLowerCase() + ".dat";

        try {
             File file = new File(dir, filename);

             if (!file.exists()) {
            	 icommandsender.addChatMessage(new TextComponentTranslation("The skin " + args[0] + " was not found on the server."));
            	 return;
             }

        	 file.delete();
        } catch (Exception var6) {
             LogWriter.except(var6);
             var6.printStackTrace();
        }
	}

	@Override
	public String getCommandName() {
		return "skindel";
	}

	@Override
	public String getCommandUsage(ICommandSender arg0) {
		return "/skindel <name>";
	}

}
