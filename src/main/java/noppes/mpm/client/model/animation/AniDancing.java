package noppes.mpm.client.model.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;

public class AniDancing {
  public static void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity, ModelBiped model) {
    float dancing = entity.field_70173_aa / 4.0F;
    float dancing2 = (entity.field_70173_aa + 1) / 4.0F;
    dancing += (dancing2 - dancing) * Minecraft.getMinecraft().func_184121_ak();
    float x = (float)Math.sin(dancing);
    float y = (float)Math.abs(Math.cos(dancing));
    model.bipedHead.field_78800_c = x * 0.75F;
    model.bipedHead.field_78797_d = y * 1.25F - 0.02F;
    model.bipedHead.field_78798_e = -y * 0.75F;
    model.field_178724_i.field_78800_c += x * 0.25F;
    model.field_178724_i.field_78797_d += y * 1.25F;
    model.field_178723_h.field_78800_c += x * 0.25F;
    model.field_178723_h.field_78797_d += y * 1.25F;
    model.field_78115_e.field_78800_c = x * 0.25F;
  }
}
