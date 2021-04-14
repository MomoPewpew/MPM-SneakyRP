package noppes.mpm.client.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import noppes.mpm.client.ClientEventHandler;
import noppes.mpm.commands.MpmCommandInterface;
import noppes.mpm.constants.EnumAnimation;

public class CommandBow extends MpmCommandInterface {
     public String getName() {
          return "bow";
     }

     public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
          ClientEventHandler.processAnimation(EnumAnimation.BOW.ordinal());
     }

     public String getUsage(ICommandSender sender) {
          return "/bow to bow";
     }
}
