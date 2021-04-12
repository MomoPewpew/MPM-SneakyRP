package noppes.mpm.client.model.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import noppes.mpm.ModelData;

public class AniNo {
  public static void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity, ModelBiped model, ModelData data) {
    float ticks = (entity.field_70173_aa - data.animationStart) / 8.0F;
    float ticks2 = (entity.field_70173_aa + 1 - data.animationStart) / 8.0F;
    ticks += (ticks2 - ticks) * Minecraft.getMinecraft().func_184121_ak();
    ticks %= 2.0F;
    float ani = ticks - 0.5F;
    if (ticks > 1.0F)
      ani = 1.5F - ticks;
    model.bipedHead.rotateAngleY = ani;
  }
}
