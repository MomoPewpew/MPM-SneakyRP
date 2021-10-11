package noppes.mpm.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;


public class CommandNames extends CommandBase {

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {
		if(!(icommandsender instanceof EntityPlayerMP))
		return;
		EntityPlayerMP player = (EntityPlayerMP)icommandsender;

		Server.sendData(player, EnumPackets.NAMES_TOGGLE);
	}

	@Override
	public String getCommandName() {
		return "names";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/names";
	}


}
