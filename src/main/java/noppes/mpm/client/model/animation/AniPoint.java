package noppes.mpm.client.model.animation;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;

public class AniPoint {
     public static void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity, ModelBiped base) {
          base.bipedRightArm.rotateAngleX = (headPitch / 57.295776F) - 1.57F;
          base.bipedRightArm.rotateAngleY = netHeadYaw / 57.295776F;
          base.bipedRightArm.rotateAngleZ = 0.0F;
     }
}
