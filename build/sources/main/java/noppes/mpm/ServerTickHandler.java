package noppes.mpm;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import noppes.mpm.client.AnalyticsTracking;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.constants.EnumPackets;

public class ServerTickHandler {
     @SubscribeEvent
     public void onPlayerTick(PlayerTickEvent event) {
          if (event.side != Side.CLIENT && event.phase != Phase.START) {
               EntityPlayerMP player = (EntityPlayerMP)event.player;
               ModelData data = ModelData.get(player);
               ItemStack item = (ItemStack)player.inventory.mainInventory.get(0);
               if (data.backItem != item) {
                    if (item.isEmpty()) {
                         Server.sendAssociatedData(player, EnumPackets.BACK_ITEM_REMOVE, player.getUniqueID());
                    } else {
                         NBTTagCompound tag = item.writeToNBT(new NBTTagCompound());
                         Server.sendAssociatedData(player, EnumPackets.BACK_ITEM_UPDATE, player.getUniqueID(), tag);
                    }

                    data.backItem = item;
               }

               data.eyes.update(player);
               if (data.animation != EnumAnimation.NONE) {
                    checkAnimation(player, data);
               }

               data.prevPosX = player.posX;
               data.prevPosY = player.posY;
               data.prevPosZ = player.posZ;
          }
     }

     public static void checkAnimation(EntityPlayer player, ModelData data) {
          if (data.prevPosY > 0.0D && player.ticksExisted >= 40) {
               double motionX = data.prevPosX - player.posX;
               double motionY = data.prevPosY - player.posY;
               double motionZ = data.prevPosZ - player.posZ;
               double speed = motionX * motionX + motionZ * motionZ;
               boolean isJumping = motionY * motionY > 0.08D;
               if (data.animationTime > 0) {
                    --data.animationTime;
               }

               if (player.isPlayerSleeping() || player.isRiding() || data.animationTime == 0 || data.animation == EnumAnimation.BOW && player.isSneaking()) {
                    data.setAnimation(EnumAnimation.NONE);
               }

               if (isJumping || !player.isSneaking() || data.animation != EnumAnimation.HUG && data.animation != EnumAnimation.CRAWLING && data.animation != EnumAnimation.SITTING && data.animation != EnumAnimation.DANCING) {
                    if (speed > 0.01D || isJumping || player.isPlayerSleeping() || player.isRiding() || data.isSleeping() && speed > 0.001D) {
                         data.setAnimation(EnumAnimation.NONE);
                    }

               }
          }
     }

     @SubscribeEvent
     public void playerLogin(PlayerLoggedInEvent event) {
          MinecraftServer server = event.player.getServer();
          if (server.isSnooperEnabled()) {
               String serverName = null;
               if (server.isDedicatedServer()) {
                    serverName = "server";
               } else {
                    serverName = ((IntegratedServer)server).getPublic() ? "lan" : "local";
               }

               ModelData data = ModelData.get(event.player);
               AnalyticsTracking.sendData(data.analyticsUUID, "join", serverName);
          }
     }
}
