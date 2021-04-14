package noppes.mpm.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;

public class CommandSing extends MpmCommandInterface {
     public String getName() {
          return "sing";
     }

     public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
          if (sender instanceof EntityPlayerMP) {
               EntityPlayerMP player = (EntityPlayerMP)sender;
               int note = player.getRNG().nextInt(25);
               if (args.length > 0) {
                    try {
                         int n = Integer.parseInt(args[0]);
                         if (n >= 0 && n < 25) {
                              note = n;
                         }
                    } catch (NumberFormatException var7) {
                    }
               }

               float pitch = (float)Math.pow(2.0D, (double)(note - 12) / 12.0D);
               player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_NOTE_HARP, SoundCategory.PLAYERS, 3.0F, pitch);
               Server.sendAssociatedData(player, EnumPackets.PARTICLE, 1, player.posX, player.posY + 2.0D, player.posZ, (double)note / 24.0D);
          }
     }

     public String getUsage(ICommandSender sender) {
          return "/sing [0-24] to sing";
     }
}
