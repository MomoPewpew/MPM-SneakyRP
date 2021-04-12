package noppes.mpm.client.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import noppes.mpm.client.ClientEventHandler;
import noppes.mpm.commands.MpmCommandInterface;
import noppes.mpm.constants.EnumAnimation;

public class CommandSleep extends MpmCommandInterface {
  @Override
  public String getCommandName() {
    return "sleep";
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
    ClientEventHandler.processAnimation(EnumAnimation.SLEEPING_SOUTH.ordinal());
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return "/sleep to lie down";
  }
}
