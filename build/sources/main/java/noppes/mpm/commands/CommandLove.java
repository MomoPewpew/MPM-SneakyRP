package noppes.mpm.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;

public class CommandLove extends MpmCommandInterface {

	@Override
	public String getCommandName() {
		return "love";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] var2) {
		if(icommandsender instanceof EntityPlayerMP == false)
			return;
		EntityPlayerMP player = (EntityPlayerMP) icommandsender;
		Server.sendAssociatedData(player, EnumPackets.PARTICLE, 0, player.getUniqueID());
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/love to show your love";
	}
}
