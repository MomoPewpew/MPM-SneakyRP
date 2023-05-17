package noppes.mpm.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import noppes.mpm.ModelData;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;

public class CommandCE extends MpmCommandInterface {

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {
		if(!(icommandsender instanceof EntityPlayerMP))
		return;
		EntityPlayerMP player = (EntityPlayerMP)icommandsender;
		ModelData data = ModelData.get(player);

		boolean override_instead_of_outro = false;
		if (args.length >= 1) {
			override_instead_of_outro = args[0].toLowerCase().equals("true") || args[0].equals("1");
		}

		data.updateEmote();
		data.endEmotes(override_instead_of_outro);
		Server.sendAssociatedData(player, EnumPackets.EMOTE_END, player.getUniqueID(), override_instead_of_outro);
	}

	@Override
	public String getCommandName() {
		return "ce";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/ce [override instead of playing outro <true/false>]";
	}


}
