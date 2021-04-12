package noppes.mpm.client.model.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class AniWaving {
  public static void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity, ModelBiped base) {
    float f = MathHelper.sin(entity.ticksExisted * 0.27F);
    float f2 = MathHelper.sin((entity.ticksExisted + 1) * 0.27F);
    f += (f2 - f) * Minecraft.getMinecraft().func_184121_ak();
    base.field_178723_h.rotateAngleX = -0.1F;
    base.field_178723_h.rotateAngleY = 0.0F;
    base.field_178723_h.rotateAngleZ = (float)(2.141592653589793D - (f * 0.5F));
  }
}
