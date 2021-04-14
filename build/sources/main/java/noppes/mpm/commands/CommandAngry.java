package noppes.mpm.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;

public class CommandAngry extends MpmCommandInterface {
     public String getName() {
          return "angry";
     }

     public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
          if (sender instanceof EntityPlayerMP) {
               EntityPlayerMP player = (EntityPlayerMP)sender;
               Server.sendAssociatedData(player, EnumPackets.PARTICLE, 2, player.getUniqueID());
          }
     }

     public String getUsage(ICommandSender sender) {
          return "/angry to show you're angry";
     }
}
