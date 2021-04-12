package noppes.mpm.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;

public class MPMEntityUtil {

	public static void Copy(EntityLivingBase copied, EntityLivingBase entity){
		entity.worldObj = copied.worldObj;
		
		entity.deathTime = copied.deathTime;
		entity.distanceWalkedModified = copied.distanceWalkedModified;
		entity.prevDistanceWalkedModified = copied.distanceWalkedModified;
		entity.distanceWalkedOnStepModified = copied.distanceWalkedOnStepModified;

		entity.moveForward = copied.moveForward;
		entity.moveStrafing = copied.moveStrafing;
		entity.onGround = copied.onGround;
		entity.fallDistance = copied.fallDistance;
		entity.isJumping = copied.isJumping;
		entity.setSneaking(copied.isSneaking());
		
		entity.prevPosX = copied.prevPosX;
		entity.prevPosY = copied.prevPosY;
		entity.prevPosZ = copied.prevPosZ;

		entity.posX = copied.posX;
		entity.posY = copied.posY;
		entity.posZ = copied.posZ;
		//entity.setEntityBoundingBox(copied.getEntityBoundingBox());

		entity.lastTickPosX = copied.lastTickPosX;
		entity.lastTickPosY = copied.lastTickPosY;
		entity.lastTickPosZ = copied.lastTickPosZ;
		
		entity.motionX = copied.motionX;
		entity.motionY = copied.motionY;
		entity.motionZ = copied.motionZ;
		
		entity.rotationYaw = copied.rotationYaw;
		entity.rotationPitch = copied.rotationPitch;
		entity.prevRotationYaw = copied.prevRotationYaw;
		entity.prevRotationPitch = copied.prevRotationPitch;
		entity.rotationYawHead = copied.rotationYawHead;
		entity.prevRotationYawHead = copied.prevRotationYawHead;
		entity.renderYawOffset = copied.renderYawOffset;
		entity.prevRenderYawOffset = copied.prevRenderYawOffset;
		entity.cameraPitch = copied.cameraPitch;
		entity.prevCameraPitch = copied.prevCameraPitch;

		entity.limbSwingAmount = copied.limbSwingAmount;
		entity.prevLimbSwingAmount = copied.prevLimbSwingAmount;
		entity.limbSwing = copied.limbSwing;

		entity.swingProgress = copied.swingProgress;
		entity.prevSwingProgress = copied.prevSwingProgress;
		entity.isSwingInProgress = copied.isSwingInProgress;
		entity.swingProgressInt = copied.swingProgressInt;
		
		entity.ticksExisted = copied.ticksExisted;

		if(entity.getRidingEntity() != copied.getRidingEntity())
			entity.ridingEntity = copied.ridingEntity;
		
		if(entity instanceof EntityPlayer && copied instanceof EntityPlayer){
			EntityPlayer ePlayer = (EntityPlayer) entity;
			EntityPlayer cPlayer = (EntityPlayer) copied;

			ePlayer.cameraYaw = cPlayer.cameraYaw;
			ePlayer.prevCameraYaw = cPlayer.prevCameraYaw;

			ePlayer.prevChasingPosX = cPlayer.prevChasingPosX;
			ePlayer.prevChasingPosY = cPlayer.prevChasingPosY;
			ePlayer.prevChasingPosZ = cPlayer.prevChasingPosZ;
			ePlayer.chasingPosX = cPlayer.chasingPosX;
			ePlayer.chasingPosY = cPlayer.chasingPosY;
			ePlayer.chasingPosZ = cPlayer.chasingPosZ;
		}
		for(EntityEquipmentSlot slot : EntityEquipmentSlot.values()){
			entity.setItemStackToSlot(slot, copied.getItemStackFromSlot(slot));
		}
		
		if(entity instanceof EntityDragon){
			entity.rotationYaw += 180;
		}
	}
}
