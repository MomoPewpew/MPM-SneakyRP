package noppes.mpm.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;

public class CommandAngry extends MpmCommandInterface {
  public String func_71517_b() {
    return "angry";
  }

  public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) {
    if (!(sender instanceof EntityPlayerMP))
      return;
    EntityPlayerMP player = (EntityPlayerMP)sender;
    Server.sendAssociatedData((Entity)player, EnumPackets.PARTICLE, new Object[] { Integer.valueOf(2), player.func_110124_au() });
  }

  public String func_71518_a(ICommandSender sender) {
    return "/angry to show you're angry";
  }
}
