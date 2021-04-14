package noppes.mpm.client.model.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import noppes.mpm.ModelData;

public class AniYes {
     public static void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity, ModelBiped model, ModelData data) {
          float ticks = (float)(entity.ticksExisted - data.animationStart) / 8.0F;
          float ticks2 = (float)(entity.ticksExisted + 1 - data.animationStart) / 8.0F;
          ticks += (ticks2 - ticks) * Minecraft.getMinecraft().getRenderPartialTicks();
          ticks %= 2.0F;
          float ani = ticks - 0.5F;
          if (ticks > 1.0F) {
               ani = 1.5F - ticks;
          }

          model.bipedHead.rotateAngleX = ani;
     }
}
