package noppes.mpm.client.model.animation;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class AniHug {
  public static void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity, ModelBiped base) {
    float f6 = MathHelper.sin(base.field_78095_p * 3.141593F);
    float f7 = MathHelper.sin((1.0F - (1.0F - base.field_78095_p) * (1.0F - base.field_78095_p)) * 3.141593F);
    base.field_178723_h.rotateAngleZ = 0.0F;
    base.field_178724_i.rotateAngleZ = 0.0F;
    base.field_178723_h.rotateAngleY = -(0.1F - f6 * 0.6F);
    base.field_178724_i.rotateAngleY = 0.1F;
    base.field_178723_h.rotateAngleX = -1.570796F;
    base.field_178724_i.rotateAngleX = -1.570796F;
    base.field_178723_h.rotateAngleX -= f6 * 1.2F - f7 * 0.4F;
    base.field_178723_h.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
    base.field_178724_i.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
    base.field_178723_h.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
    base.field_178724_i.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
  }
}
