package noppes.mpm.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;

public class CommandSing extends MpmCommandInterface {
  public String getCommandName() {
    return "sing";
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
    if (!(sender instanceof EntityPlayerMP))
      return;
    EntityPlayerMP player = (EntityPlayerMP)sender;
    int note = player.getRNG().nextInt(25);
    if (args.length > 0)
      try {
        int n = Integer.parseInt(args[0]);
        if (n >= 0 && n < 25)
          note = n;
      } catch (NumberFormatException numberFormatException) {}
    float pitch = (float)Math.pow(2.0D, (note - 12) / 12.0D);
    player.worldObj.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_NOTE_HARP, SoundCategory.PLAYERS, 3.0F, pitch);
    Server.sendAssociatedData((Entity)player, EnumPackets.PARTICLE, new Object[] { Integer.valueOf(1), Double.valueOf(player.posX), Double.valueOf(player.posY + 2.0D), Double.valueOf(player.posZ), Double.valueOf(note / 24.0D) });
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return "/sing [0-24] to sing";
  }
}
