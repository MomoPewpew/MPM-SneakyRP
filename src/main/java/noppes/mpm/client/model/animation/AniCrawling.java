package noppes.mpm.client.model.animation;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class AniCrawling {
  public static void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity, ModelBiped model) {
    model.bipedHead.rotateAngleZ = -netHeadYaw / 57.295776F;
    model.bipedHead.rotateAngleY = 0.0F;
    model.bipedHead.rotateAngleX = -0.95993114F;
    model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;
    model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
    model.bipedHeadwear.rotateAngleZ = model.bipedHead.rotateAngleZ;
    if (limbSwingAmount > 0.25D)
      limbSwingAmount = 0.25F;
    float movement = MathHelper.func_76134_b(limbSwing * 0.8F + 3.1415927F) * limbSwingAmount;
    model.field_178724_i.rotateAngleX = 3.1415927F - movement * 0.25F;
    model.field_178724_i.rotateAngleY = movement * -0.46F;
    model.field_178724_i.rotateAngleZ = movement * -0.2F;
    model.field_178724_i.field_78797_d = 2.0F - movement * 9.0F;
    model.field_178723_h.rotateAngleX = 3.1415927F + movement * 0.25F;
    model.field_178723_h.rotateAngleY = movement * -0.4F;
    model.field_178723_h.rotateAngleZ = movement * -0.2F;
    model.field_178723_h.field_78797_d = 2.0F + movement * 9.0F;
    model.field_78115_e.rotateAngleY = movement * 0.1F;
    model.field_78115_e.rotateAngleX = 0.0F;
    model.field_78115_e.rotateAngleZ = movement * 0.1F;
    model.field_178722_k.rotateAngleX = movement * 0.1F;
    model.field_178722_k.rotateAngleY = movement * 0.1F;
    model.field_178722_k.rotateAngleZ = -0.122173056F - movement * 0.25F;
    model.field_178722_k.field_78797_d = 10.4F + movement * 9.0F;
    model.field_178722_k.field_78798_e = movement * 0.6F;
    model.field_178721_j.rotateAngleX = movement * -0.1F;
    model.field_178721_j.rotateAngleY = movement * 0.1F;
    model.field_178721_j.rotateAngleZ = 0.122173056F - movement * 0.25F;
    model.field_178721_j.field_78797_d = 10.4F - movement * 9.0F;
    model.field_178721_j.field_78798_e = movement * -0.6F;
  }
}
