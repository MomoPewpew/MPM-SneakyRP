package noppes.mpm.client.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import noppes.mpm.client.ClientEventHandler;
import noppes.mpm.commands.MpmCommandInterface;
import noppes.mpm.constants.EnumAnimation;

public class CommandYes extends MpmCommandInterface {
     public String getName() {
          return "yes";
     }

     public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
          ClientEventHandler.processAnimation(EnumAnimation.YES.ordinal());
     }

     public String getUsage(ICommandSender sender) {
          return "/yes to yes";
     }
}
