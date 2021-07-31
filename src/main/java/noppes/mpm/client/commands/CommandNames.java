package noppes.mpm.client.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import noppes.mpm.MorePlayerModels;

public class CommandNames extends CommandBase {

	@Override
	public void execute(MinecraftServer arg0, ICommandSender arg1, String[] arg2) throws CommandException {
		MorePlayerModels.HidePlayerNames = !MorePlayerModels.HidePlayerNames;
	}

	@Override
	public String getCommandName() {
		return "names";
	}

	@Override
	public String getCommandUsage(ICommandSender arg0) {
		return "/names";
	}

}
