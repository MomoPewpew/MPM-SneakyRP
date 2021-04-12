package noppes.mpm.client.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import noppes.mpm.client.ClientEventHandler;
import noppes.mpm.commands.MpmCommandInterface;
import noppes.mpm.constants.EnumAnimation;

public class CommandHug extends MpmCommandInterface {

  @Override
  public String getCommandName() {
    return "hug";
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
    ClientEventHandler.processAnimation(EnumAnimation.HUG.ordinal());
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return "/hug to hug";
  }
}
