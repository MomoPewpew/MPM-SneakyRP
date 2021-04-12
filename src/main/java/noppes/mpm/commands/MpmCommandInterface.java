package noppes.mpm.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public abstract class MpmCommandInterface extends CommandBase {
  public boolean func_184882_a(MinecraftServer server, ICommandSender par1ICommandSender) {
    return true;
  }

  public int func_82362_a() {
    return 0;
  }

  public boolean isPlayerOp(ICommandSender player) {
    return player.func_70003_b(2, "mpm");
  }
}
