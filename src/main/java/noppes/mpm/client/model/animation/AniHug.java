package noppes.mpm.client.model.animation;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class AniHug {
  public static void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity, ModelBiped base) {
    float f6 = MathHelper.sin(base.swingProgress * 3.141593F);
    float f7 = MathHelper.sin((1.0F - (1.0F - base.swingProgress) * (1.0F - base.swingProgress)) * 3.141593F);
    base.bipedRightArm.rotateAngleZ = 0.0F;
    base.bipedLeftArm.rotateAngleZ = 0.0F;
    base.bipedRightArm.rotateAngleY = -(0.1F - f6 * 0.6F);
    base.bipedLeftArm.rotateAngleY = 0.1F;
    base.bipedRightArm.rotateAngleX = -1.570796F;
    base.bipedLeftArm.rotateAngleX = -1.570796F;
    base.bipedRightArm.rotateAngleX -= f6 * 1.2F - f7 * 0.4F;
    base.bipedRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
    base.bipedLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
    base.bipedRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
    base.bipedLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
  }
}
