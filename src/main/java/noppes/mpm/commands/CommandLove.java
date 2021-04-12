package noppes.mpm.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;

public class CommandLove extends MpmCommandInterface {
  public String getCommandName() {
    return "love";
  }

  public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
    if (!(sender instanceof EntityPlayerMP))
      return;
    EntityPlayerMP player = (EntityPlayerMP)sender;
    Server.sendAssociatedData((Entity)player, EnumPackets.PARTICLE, new Object[] { Integer.valueOf(0), player.getUniqueID() });
  }

  public String getCommandUsage(ICommandSender sender) {
    return "/love to show your love";
  }
}
