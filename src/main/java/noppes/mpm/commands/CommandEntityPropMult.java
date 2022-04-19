package noppes.mpm.commands;

import java.io.IOException;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import noppes.mpm.ModelData;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;
import noppes.mpm.util.EntityScaleManagerServer;

public class CommandEntityPropMult extends MpmCommandInterface {
	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {
		ModelData data = ModelData.get((EntityPlayer) icommandsender);
		EntityLivingBase entity = data.getEntity((EntityPlayer) icommandsender);

		if (args.length == 0 || entity == null) throw new WrongUsageException(this.getCommandUsage(icommandsender));

		String name = EntityScaleManagerServer.getName(entity);

		Float mult;
		try {
			mult = Float.valueOf(args[0]);
		} catch (NumberFormatException e) {
			throw new WrongUsageException(this.getCommandUsage(icommandsender));
		}

		try {
			EntityScaleManagerServer.setScaleMult(name, mult);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		NBTTagCompound compound = new NBTTagCompound();
		compound.setString("name", name);
		compound.setFloat("mult", mult);

		Server.sendToAll(server, EnumPackets.ENTITY_SCALE_MULT, compound);
	}

	@Override
	public String getCommandName() {
		return "entitypropmult";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/entitypropmult <base scale multiplier>. Use this command while in an entity form. If you do not know what this is, please do not use it at all.";
	}
}
