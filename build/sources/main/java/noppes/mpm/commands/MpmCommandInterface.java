package noppes.mpm.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public abstract class MpmCommandInterface extends CommandBase {
     public boolean checkPermission(MinecraftServer server, ICommandSender par1ICommandSender) {
          return true;
     }

     public int getRequiredPermissionLevel() {
          return 0;
     }

     public boolean isPlayerOp(ICommandSender player) {
          return player.canUseCommand(2, "mpm");
     }
}
