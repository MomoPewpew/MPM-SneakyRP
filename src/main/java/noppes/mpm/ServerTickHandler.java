package noppes.mpm;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import noppes.mpm.constants.EnumPackets;

public class ServerTickHandler {
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event) {
		if (event.side != Side.CLIENT && event.phase != Phase.START) {
			EntityPlayerMP player = (EntityPlayerMP)event.player;
			ModelData data = ModelData.get(player);
			ItemStack item = (ItemStack)player.inventory.mainInventory.get(0);
			if (data.backItem != item) {
				if (item != null) {
					Server.sendAssociatedData(player, EnumPackets.BACK_ITEM_REMOVE, player.getUniqueID());
				} else {
					NBTTagCompound tag = item.writeToNBT(new NBTTagCompound());
					Server.sendAssociatedData(player, EnumPackets.BACK_ITEM_UPDATE, player.getUniqueID(), tag);
				}

				data.backItem = item;
			}

			data.eyes.update(player);
		}
	}


	@SubscribeEvent
	public void playerLogin(PlayerLoggedInEvent event) {
		MinecraftServer server = event.player.getServer();
		if (server.isSnooperEnabled()) {
			// String serverName = null;
			// if (server.isDedicatedServer()) {
			// 	serverName = "server";
			// } else {
			// 	serverName = ((IntegratedServer)server).getPublic() ? "lan" : "local";
			// }

			ModelData data = ModelData.get(event.player);
			data.loadPlayerData(event.player);
		}
	}
}
