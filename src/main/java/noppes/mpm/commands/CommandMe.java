package noppes.mpm.commands;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;

public class CommandMe extends CommandBase {

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {
		if (args.length == 0) throw new WrongUsageException(this.getCommandUsage(icommandsender));

		String string = "";

		for (String s : args) {
			string += s + " ";
		}
		string = StringUtils.chop(string);

		EntityPlayer player  = (EntityPlayer) icommandsender;
		Server.sendAssociatedData(player, EnumPackets.ME, player.getUniqueID(), string);
	}

	@Override
	public String getCommandName() {
		return "me";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/me <text>";
	}

}
