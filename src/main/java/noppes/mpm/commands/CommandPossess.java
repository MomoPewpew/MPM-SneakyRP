package noppes.mpm.commands;

import java.lang.reflect.Modifier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import noppes.mpm.ModelData;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;

public class CommandPossess extends MpmCommandInterface {

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {
		if (icommandsender == null) return;

		EntityPlayerMP player = (EntityPlayerMP) icommandsender;

		EntityCreature nearestEntity = null;
		double shortestDistanceSq = 0;

		for (EntityLivingBase e : player.getEntityWorld().getEntities(EntityLivingBase.class, EntitySelectors.NOT_SPECTATING)) {
			if (!(e instanceof EntityCreature)) continue;

			double distanceSq = player.getPosition().distanceSq(e.getPosition());

			if (distanceSq < 25 && (nearestEntity == null || distanceSq < shortestDistanceSq)) {
				nearestEntity = (EntityCreature) e;
				shortestDistanceSq = distanceSq;
			}
		};

		if (nearestEntity == null) {
			icommandsender.addChatMessage(new TextComponentTranslation("There are no NPC's within 5 blocks of you."));
		} else {
			ModelData newData = new ModelData();
			if (nearestEntity instanceof EntityNPCInterface) {
				newData.readFromCNPCsNBT(((EntityCustomNpc) nearestEntity).modelData.writeToNBT());
				newData.url = ((EntityNPCInterface) nearestEntity).display.getSkinUrl();
			} else {
				try {
					Class<? extends EntityCreature> c = nearestEntity.getClass();
					if (EntityLiving.class.isAssignableFrom(c) && c.getConstructor(World.class) != null && !Modifier.isAbstract(c.getModifiers()) && Minecraft.getMinecraft().getRenderManager().getEntityClassRenderObject(c) instanceof RenderLivingBase) {
						newData.setEntityClass(c);
					}
				} catch (SecurityException var5) {
					var5.printStackTrace();
				} catch (Exception var6) {
				}
			}
			NBTTagCompound compound = newData.writeToNBT();
			ModelData data = ModelData.get(player);
			data.readFromNBT(compound);
			data.save();
			Server.sendAssociatedData(player, EnumPackets.SEND_PLAYER_DATA, player.getUniqueID(), compound);

			player.rotationYaw = nearestEntity.rotationYaw;
			player.renderYawOffset = nearestEntity.renderYawOffset;
			player.rotationPitch  = nearestEntity.rotationPitch;
			player.setPositionAndUpdate(nearestEntity.posX, nearestEntity.posY, nearestEntity.posZ);

			nearestEntity.setPosition(nearestEntity.posX, -100, nearestEntity.posZ);

			player.setGameType(GameType.SURVIVAL);
		}
	}

	@Override
	public String getCommandName() {
		return "possess";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/possess";
	}


}
