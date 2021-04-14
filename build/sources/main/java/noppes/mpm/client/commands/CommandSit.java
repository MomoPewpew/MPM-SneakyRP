package noppes.mpm.client.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import noppes.mpm.client.ClientEventHandler;
import noppes.mpm.commands.MpmCommandInterface;
import noppes.mpm.constants.EnumAnimation;

public class CommandSit extends MpmCommandInterface {
     public String getName() {
          return "sit";
     }

     public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) {
          ClientEventHandler.processAnimation(EnumAnimation.SITTING.ordinal());
     }

     public String getUsage(ICommandSender sender) {
          return "/sit to sit down";
     }
}
