package noppes.mpm.client.model.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import noppes.mpm.ModelData;

public class AniBow {
  public static void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity, ModelBiped model, ModelData data) {
    float ticks = (entity.ticksExisted - data.animationStart) / 10.0F;
    if (ticks > 1.0F)
      ticks = 1.0F;
    float ticks2 = (entity.ticksExisted + 1 - data.animationStart) / 10.0F;
    if (ticks2 > 1.0F)
      ticks2 = 1.0F;
    ticks += (ticks2 - ticks) * Minecraft.getMinecraft().getRenderPartialTicks();
    model.bipedBody.rotateAngleX = ticks;
    model.bipedHead.rotateAngleX = ticks;
    model.bipedLeftArm.rotateAngleX = ticks;
    model.bipedRightArm.rotateAngleX = ticks;
    model.bipedBody.rotationPointZ = -ticks * 10.0F;
    model.bipedBody.rotationPointY = ticks * 6.0F;
    model.bipedHead.rotationPointZ = -ticks * 10.0F;
    model.bipedHead.rotationPointY = ticks * 6.0F;
    model.bipedLeftArm.rotationPointZ = -ticks * 10.0F;
    model.bipedLeftArm.rotationPointY += ticks * 6.0F;
    model.bipedRightArm.rotationPointZ = -ticks * 10.0F;
    model.bipedRightArm.rotationPointY += ticks * 6.0F;
  }
}
