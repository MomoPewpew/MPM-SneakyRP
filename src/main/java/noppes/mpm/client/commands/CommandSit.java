package noppes.mpm.client.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import noppes.mpm.client.ClientEventHandler;
import noppes.mpm.commands.MpmCommandInterface;
import noppes.mpm.constants.EnumAnimation;

public class CommandSit extends MpmCommandInterface {
  public String func_71517_b() {
    return "sit";
  }

  public void func_184881_a(MinecraftServer server, ICommandSender icommandsender, String[] args) {
    ClientEventHandler.processAnimation(EnumAnimation.SITTING.ordinal());
  }

  public String func_71518_a(ICommandSender sender) {
    return "/sit to sit down";
  }
}
