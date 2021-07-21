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

public class CommandSkinSave extends CommandBase {

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {

		if (args.length == 0) return;

		NBTTagCompound compound = ModelData.get((EntityPlayer) icommandsender).writeToNBT();

        File dir = null;

        String filename = args[0].toLowerCase() + ".dat";

        try {
            dir = new File(dir, ".." + File.separator + "moreplayermodels" + File.separator + "skins" + File.separator + "unrestricted");
            if (!dir.exists()) {
                 dir.mkdirs();
            }

             File file = new File(dir, filename);

             if (!file.exists()) {
            	 dir = null;
                 dir = new File(dir, ".." + File.separator + "moreplayermodels" + File.separator + "skins" + File.separator + "restricted");
                 if (!dir.exists()) {
                      dir.mkdirs();
                 }

                  file = new File(dir, filename);
             }

              if (!file.exists()) {
            	  dir = null;
            	  dir = new File(dir, ".." + File.separator + "moreplayermodels" + File.separator + "skins");
                  if (!dir.exists()) {
                       dir.mkdirs();
                  }

                   file = new File(dir, filename);
              }

               if (file.exists()) {
              	 icommandsender.addChatMessage(new TextComponentTranslation("The skin " + args[0] + " already exists. Either /skindel the old version first, or give this one a new name"));
              	 return;
               }

               CompressedStreamTools.writeCompressed(compound, new FileOutputStream(file));
        } catch (Exception var6) {
             LogWriter.except(var6);
             var6.printStackTrace();
        }
	}

	@Override
	public String getCommandName() {
		return "skinsave";
	}

	@Override
	public String getCommandUsage(ICommandSender arg0) {
		return "/skinsave <name>";
	}
}
