package noppes.mpm.client.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import noppes.mpm.client.ClientEventHandler;
import noppes.mpm.commands.MpmCommandInterface;
import noppes.mpm.constants.EnumAnimation;

public class CommandDance extends MpmCommandInterface {
     public String getName() {
          return "dance";
     }

     public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
          ClientEventHandler.processAnimation(EnumAnimation.DANCING.ordinal());
     }

     public String getUsage(ICommandSender sender) {
          return "/dance to dance";
     }
}
