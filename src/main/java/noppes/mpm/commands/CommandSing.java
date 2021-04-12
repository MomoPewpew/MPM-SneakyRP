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
  public String func_71517_b() {
    return "sing";
  }

  public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) {
    if (!(sender instanceof EntityPlayerMP))
      return;
    EntityPlayerMP player = (EntityPlayerMP)sender;
    int note = player.func_70681_au().nextInt(25);
    if (args.length > 0)
      try {
        int n = Integer.parseInt(args[0]);
        if (n >= 0 && n < 25)
          note = n;
      } catch (NumberFormatException numberFormatException) {}
    float pitch = (float)Math.pow(2.0D, (note - 12) / 12.0D);
    player.field_70170_p.func_184148_a(null, player.field_70165_t, player.field_70163_u, player.field_70161_v, SoundEvents.field_187682_dG, SoundCategory.PLAYERS, 3.0F, pitch);
    Server.sendAssociatedData((Entity)player, EnumPackets.PARTICLE, new Object[] { Integer.valueOf(1), Double.valueOf(player.field_70165_t), Double.valueOf(player.field_70163_u + 2.0D), Double.valueOf(player.field_70161_v), Double.valueOf(note / 24.0D) });
  }

  public String func_71518_a(ICommandSender sender) {
    return "/sing [0-24] to sing";
  }
}
