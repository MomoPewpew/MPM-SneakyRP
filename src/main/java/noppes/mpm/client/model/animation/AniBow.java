package noppes.mpm.client.model.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import noppes.mpm.ModelData;

public class AniBow {
  public static void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity, ModelBiped model, ModelData data) {
    float ticks = (entity.field_70173_aa - data.animationStart) / 10.0F;
    if (ticks > 1.0F)
      ticks = 1.0F;
    float ticks2 = (entity.field_70173_aa + 1 - data.animationStart) / 10.0F;
    if (ticks2 > 1.0F)
      ticks2 = 1.0F;
    ticks += (ticks2 - ticks) * Minecraft.getMinecraft().func_184121_ak();
    model.field_78115_e.rotateAngleX = ticks;
    model.bipedHead.rotateAngleX = ticks;
    model.field_178724_i.rotateAngleX = ticks;
    model.field_178723_h.rotateAngleX = ticks;
    model.field_78115_e.field_78798_e = -ticks * 10.0F;
    model.field_78115_e.field_78797_d = ticks * 6.0F;
    model.bipedHead.field_78798_e = -ticks * 10.0F;
    model.bipedHead.field_78797_d = ticks * 6.0F;
    model.field_178724_i.field_78798_e = -ticks * 10.0F;
    model.field_178724_i.field_78797_d += ticks * 6.0F;
    model.field_178723_h.field_78798_e = -ticks * 10.0F;
    model.field_178723_h.field_78797_d += ticks * 6.0F;
  }
}
