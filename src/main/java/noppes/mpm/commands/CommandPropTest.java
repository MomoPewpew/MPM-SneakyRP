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

public class CommandPropTest extends MpmCommandInterface {

	@Override
	public String getCommandName() {
		return "ptest";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) {
		if(icommandsender instanceof EntityPlayerMP == false)
			return;
		EntityPlayerMP player = (EntityPlayerMP) icommandsender;
		ModelData data = ModelData.get(player);

		for (int i = 0; i < data.propItemStack.size(); i++) {
			player.addChatMessage(new TextComponentTranslation(data.propBodyPartName.get(i)));
		}
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/prop [<itemname>] [<bodypart>] [<scaleX>] [<scaleY>] [<scaleZ>] [<offsetX>] [<offsetY>] [<offsetZ>] [<rotateX>] [<rotateY>] [<rotateZ>]";
	}

}