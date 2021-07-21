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
import noppes.mpm.MorePlayerModels;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;

public class CommandSkinLoad extends CommandBase {

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {

		if (args.length == 0) {
			MorePlayerModels.syncSkinFileNames((EntityPlayerMP) icommandsender);
			Server.sendData((EntityPlayerMP) icommandsender, EnumPackets.SKIN_LOAD_GUI);
			return;
		}

		String filename = args[0].toLowerCase() + ".dat";
		File file;

        try {
    		 File dir = null;

    		 dir = new File(dir, ".." + File.separator + "moreplayermodels" + File.separator + "skins" + File.separator + "unrestricted");

             if (!dir.exists()) {
                  dir.mkdirs();
              }

             file = new File(dir, filename);

             NBTTagCompound compound = new NBTTagCompound();

             if (!file.exists()) {
            	 dir = null;
            	 dir = new File(dir, ".." + File.separator + "moreplayermodels" + File.separator + "skins");

                 if (!dir.exists()) {
                      dir.mkdirs();
                  }

                 file = new File(dir, filename);

                 if (file.exists()) {
                	 NBTTagCompound temp = CompressedStreamTools.readCompressed(new FileInputStream(file));

                     if (!temp.getString("EntityClass").equals("") && MorePlayerModels.playersEntityDenied.contains(((EntityPlayer) icommandsender).getUniqueID()))
                    	 return;
                 }
             }

             if (!file.exists() && MorePlayerModels.playersEntityDenied.contains(((EntityPlayer) icommandsender).getUniqueID())) {
            	 dir = null;
            	 dir = new File(dir, ".." + File.separator + "moreplayermodels" + File.separator + "skins" + File.separator + "restricted");

                 if (!dir.exists()) {
                      dir.mkdirs();
                  }

                 file = new File(dir, filename);
             }

             if (!file.exists()) {
            	 icommandsender.addChatMessage(new TextComponentTranslation("The skin " + args[0] + " was not found on the server."));
            	 return;
             } else {
            	 compound = CompressedStreamTools.readCompressed(new FileInputStream(file));
             }

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
