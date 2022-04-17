package noppes.mpm;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import noppes.mpm.util.EntityScaleManagerServer;

public class CommonProxy implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	public void load() {
		MorePlayerModels.Channel.register(new PacketHandlerServer());
		try {
			EntityScaleManagerServer.buildMap();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void postLoad() {
	}
}
