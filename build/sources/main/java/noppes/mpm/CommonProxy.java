package noppes.mpm;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler {
     public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
          return null;
     }

     public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
          return null;
     }

     public void load() {
          MorePlayerModels.Channel.register(new PacketHandlerServer());
     }

     public void postLoad() {
     }
}
