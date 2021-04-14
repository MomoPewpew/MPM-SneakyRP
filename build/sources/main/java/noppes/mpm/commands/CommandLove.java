package noppes.mpm.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;

public class CommandLove extends MpmCommandInterface {
     public String getName() {
          return "love";
     }

     public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
          if (sender instanceof EntityPlayerMP) {
               EntityPlayerMP player = (EntityPlayerMP)sender;
               Server.sendAssociatedData(player, EnumPackets.PARTICLE, 0, player.getUniqueID());
          }
     }

     public String getUsage(ICommandSender sender) {
          return "/love to show your love";
     }
}
