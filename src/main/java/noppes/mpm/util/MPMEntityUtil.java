package noppes.mpm.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;

public class MPMEntityUtil {
  public static void Copy(EntityLivingBase copied, EntityLivingBase entity) {
    entity.worldObj = copied.worldObj;
    entity.deathTime = copied.deathTime;
    entity.field_70140_Q = copied.field_70140_Q;
    entity.field_70141_P = copied.field_70140_Q;
    entity.field_82151_R = copied.field_82151_R;
    entity.field_191988_bg = copied.field_191988_bg;
    entity.field_70702_br = copied.field_70702_br;
    entity.isOnLadder = copied.isOnLadder;
    entity.fallDistance  = copied.fallDistance ;
    entity.field_70703_bu = copied.field_70703_bu;
    entity.func_70095_a(copied.isSneaking());
    entity.posX = copied.posX;
    entity.posY = copied.posY;
    entity.posZ  = copied.posZ ;
    entity.posX = copied.posX;
    entity.posY = copied.posY;
    entity.posZ = copied.posZ;
    entity.field_70142_S = copied.field_70142_S;
    entity.field_70137_T = copied.field_70137_T;
    entity.field_70136_U = copied.field_70136_U;
    entity.field_70159_w = copied.field_70159_w;
    entity.field_70181_x = copied.field_70181_x;
    entity.field_70179_y = copied.field_70179_y;
    entity.rotationYaw = copied.rotationYaw;
    entity.rotationPitch = copied.rotationPitch;
    entity.prevRotationYaw  = copied.prevRotationYaw ;
    entity.prevRotationPitch = copied.prevRotationPitch;
    entity.rotationYawHead = copied.rotationYawHead;
    entity.prevRotationYawHead = copied.prevRotationYawHead;
    entity.renderYawOffset = copied.renderYawOffset;
    entity.prevRenderYawOffset = copied.prevRenderYawOffset;
    entity.field_70726_aT = copied.field_70726_aT;
    entity.field_70727_aS = copied.field_70727_aS;
    entity.field_70721_aZ = copied.field_70721_aZ;
    entity.field_184618_aE = copied.field_184618_aE;
    entity.field_184619_aG = copied.field_184619_aG;
    entity.field_70733_aJ = copied.field_70733_aJ;
    entity.field_70732_aI = copied.field_70732_aI;
    entity.field_82175_bq = copied.field_82175_bq;
    entity.field_110158_av = copied.field_110158_av;
    entity.ticksExisted = copied.ticksExisted;
    entity.func_70606_j(Math.min(copied.getHealth(), entity.func_110138_aP()));
    entity.getEntityData().func_179237_a(copied.getEntityData());
    if (entity.getRidingEntity() != copied.getRidingEntity())
      entity.field_184239_as = copied.field_184239_as;
    if (entity instanceof EntityPlayer && copied instanceof EntityPlayer) {
      EntityPlayer ePlayer = (EntityPlayer)entity;
      EntityPlayer cPlayer = (EntityPlayer)copied;
      ePlayer.field_71109_bG = cPlayer.field_71109_bG;
      ePlayer.field_71107_bF = cPlayer.field_71107_bF;
      ePlayer.field_71091_bM = cPlayer.field_71091_bM;
      ePlayer.field_71096_bN = cPlayer.field_71096_bN;
      ePlayer.field_71097_bO = cPlayer.field_71097_bO;
      ePlayer.field_71094_bP = cPlayer.field_71094_bP;
      ePlayer.field_71095_bQ = cPlayer.field_71095_bQ;
      ePlayer.field_71085_bR = cPlayer.field_71085_bR;
    }
    for (EntityEquipmentSlot slot : EntityEquipmentSlot.values())
      entity.setItemStackToSlot(slot, copied.getItemStackFromSlot(slot));
    if (entity instanceof net.minecraft.entity.boss.EntityDragon)
      entity.rotationYaw += 180.0F;
  }
}
