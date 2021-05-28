package noppes.mpm.commands;

import java.awt.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.mpm.ModelData;
import noppes.mpm.Server;
import noppes.mpm.client.layer.LayerProp;
import noppes.mpm.constants.EnumPackets;

public class CommandProp extends MpmCommandInterface {

	@Override
	public String getCommandName() {
		return "prop";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) {
		if(icommandsender instanceof EntityPlayerMP == false)
			return;
		EntityPlayerMP player = (EntityPlayerMP) icommandsender;
		ModelData data = ModelData.get(player);

		String itemName = (args.length > 0) ? args[0] : "null";
		String bodyPartName = (args.length > 1) ? args[1] : "leftHand";
		Float propScaleX = (args.length > 2) ? Float.valueOf(args[2]) : 1.0F;
		Float propScaleY = (args.length > 3) ? Float.valueOf(args[3]) : 1.0F;
		Float propScaleZ = (args.length > 4) ? Float.valueOf(args[4]) : 1.0F;
		Float propOffsetX = (args.length > 5) ? Float.valueOf(args[5]) : 1.0F;
		Float propOffsetY = (args.length > 6) ? Float.valueOf(args[6]) : 1.0F;
		Float propOffsetZ = (args.length > 7) ? Float.valueOf(args[7]) : 1.0F;
		Float propRotateX = (args.length > 8) ? Float.valueOf(args[8]) : 1.0F;
		Float propRotateY = (args.length > 9) ? Float.valueOf(args[9]) : 1.0F;
		Float propRotateZ = (args.length > 10) ? Float.valueOf(args[10]) : 1.0F;

		data.newProp(itemName, bodyPartName, propScaleX, propScaleY, propScaleZ, propOffsetX, propOffsetY, propOffsetZ, propRotateX, propRotateY, propRotateZ);
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/prop [<itemname>] [<bodypart>] [<scaleX>] [<scaleY>] [<scaleZ>] [<offsetX>] [<offsetY>] [<offsetZ>] [<rotateX>] [<rotateY>] [<rotateZ>]";
	}

}