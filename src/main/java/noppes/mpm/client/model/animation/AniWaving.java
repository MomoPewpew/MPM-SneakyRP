package noppes.mpm.client.model.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class AniWaving {
  public static void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity, ModelBiped base) {
    float f = MathHelper.func_76126_a(entity.field_70173_aa * 0.27F);
    float f2 = MathHelper.func_76126_a((entity.field_70173_aa + 1) * 0.27F);
    f += (f2 - f) * Minecraft.func_71410_x().func_184121_ak();
    base.field_178723_h.field_78795_f = -0.1F;
    base.field_178723_h.field_78796_g = 0.0F;
    base.field_178723_h.field_78808_h = (float)(2.141592653589793D - (f * 0.5F));
  }
}
