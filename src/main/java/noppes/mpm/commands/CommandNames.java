package noppes.mpm.commands;

import io.netty.buffer.ByteBuf;
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
		if(!(icommandsender instanceof EntityPlayerMP)) return;

		EntityPlayerMP player = (EntityPlayerMP)icommandsender;
		EnumPackets buf = EnumPackets.NAMES_TOGGLE;

		if (args.length > 0) {
			if (args[0].contains("on") || args[0].contains("show")) {
				buf = EnumPackets.NAMES_ON;
			} else if (args[0].contains("off") || args[0].contains("hide")) {
				buf = EnumPackets.NAMES_OFF;
			}
		}

		Server.sendData(player, buf);
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
