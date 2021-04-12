package noppes.mpm.client.model.animation;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;

public class AniPoint {
  public static void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity, ModelBiped base) {
    base.field_178723_h.field_78795_f = -1.570796F;
    base.field_178723_h.field_78796_g = netHeadYaw / 57.295776F;
    base.field_178723_h.field_78808_h = 0.0F;
  }
}
