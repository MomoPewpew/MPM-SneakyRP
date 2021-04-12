package noppes.mpm;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import noppes.mpm.client.AnalyticsTracking;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.constants.EnumPackets;

public class ServerTickHandler {
  @SubscribeEvent
  public void onPlayerTick(TickEvent.PlayerTickEvent event) {
    if (event.side == Side.CLIENT || event.phase == TickEvent.Phase.START)
      return;
    EntityPlayerMP player = (EntityPlayerMP)event.player;
    ModelData data = ModelData.get((EntityPlayer)player);
    ItemStack item = (ItemStack)player.field_71071_by.field_70462_a.get(0);
    if (data.backItem != item) {
      if (item.func_190926_b()) {
        Server.sendAssociatedData((Entity)player, EnumPackets.BACK_ITEM_REMOVE, new Object[] { player.func_110124_au() });
      } else {
        NBTTagCompound tag = item.func_77955_b(new NBTTagCompound());
        Server.sendAssociatedData((Entity)player, EnumPackets.BACK_ITEM_UPDATE, new Object[] { player.func_110124_au(), tag });
      }
      data.backItem = item;
    }
    data.eyes.update((EntityPlayer)player);
    if (data.animation != EnumAnimation.NONE)
      checkAnimation((EntityPlayer)player, data);
    data.prevPosX = player.field_70165_t;
    data.prevPosY = player.field_70163_u;
    data.prevPosZ = player.field_70161_v;
  }

  public static void checkAnimation(EntityPlayer player, ModelData data) {
    if (data.prevPosY <= 0.0D || player.field_70173_aa < 40)
      return;
    double motionX = data.prevPosX - player.field_70165_t;
    double motionY = data.prevPosY - player.field_70163_u;
    double motionZ = data.prevPosZ - player.field_70161_v;
    double speed = motionX * motionX + motionZ * motionZ;
    boolean isJumping = (motionY * motionY > 0.08D);
    if (data.animationTime > 0)
      data.animationTime--;
    if (player.func_70608_bn() || player.func_184218_aH() || data.animationTime == 0 || (data.animation == EnumAnimation.BOW && player.func_70093_af()))
      data.setAnimation(EnumAnimation.NONE);
    if (!isJumping && player.func_70093_af() && (data.animation == EnumAnimation.HUG || data.animation == EnumAnimation.CRAWLING || data.animation == EnumAnimation.SITTING || data.animation == EnumAnimation.DANCING))
      return;
    if (speed > 0.01D || isJumping || player.func_70608_bn() || player.func_184218_aH() || (data.isSleeping() && speed > 0.001D))
      data.setAnimation(EnumAnimation.NONE);
  }

  @SubscribeEvent
  public void playerLogin(PlayerEvent.PlayerLoggedInEvent event) {
    MinecraftServer server = event.player.func_184102_h();
    if (!server.func_70002_Q())
      return;
    String serverName = null;
    if (server.func_71262_S()) {
      serverName = "server";
    } else {
      serverName = ((IntegratedServer)server).func_71344_c() ? "lan" : "local";
    }
    ModelData data = ModelData.get(event.player);
    AnalyticsTracking.sendData(data.analyticsUUID, "join", serverName);
  }
}
