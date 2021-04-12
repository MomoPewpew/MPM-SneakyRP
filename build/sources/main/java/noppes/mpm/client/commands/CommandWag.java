package noppes.mpm.client.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import noppes.mpm.client.ClientEventHandler;
import noppes.mpm.commands.MpmCommandInterface;
import noppes.mpm.constants.EnumAnimation;

public class CommandWag extends MpmCommandInterface {
  @Override
  public String getCommandName() {
    return "wag";
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
    ClientEventHandler.processAnimation(EnumAnimation.WAG.ordinal());
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return "/wag to wag";
  }
}
