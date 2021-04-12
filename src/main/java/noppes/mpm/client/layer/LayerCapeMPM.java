package noppes.mpm.client.layer;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import noppes.mpm.ModelData;
import noppes.mpm.ModelPartConfig;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.constants.EnumParts;

public class LayerCapeMPM extends LayerCape {
  private RenderPlayer render;

  public LayerCapeMPM(RenderPlayer render) {
    super(render);
    this.render = render;
  }

  public void func_177141_a(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
    ModelPlayer modelPlayer = this.render.func_177087_b();
    ModelData data = ModelData.get((EntityPlayer)player);
    ModelPartConfig config = data.getPartConfig(EnumParts.BODY);
    GlStateManager.pushMatrix();
    if (player.func_70093_af() && !data.animationEquals(EnumAnimation.CRAWLING))
      GlStateManager.translate(0.0F, 0.0F, (-2.0F + config.scaleZ) * scale);
    GlStateManager.translate(config.transX, config.transY, config.transZ + (-1.0F + config.scaleZ) * scale);
    GlStateManager.translate(config.scaleX, config.scaleY, 1.0F);
    if (data.animationEquals(EnumAnimation.CRAWLING)) {
      int rotation = 78;
      if (player.func_70093_af())
        GlStateManager.rotate(-25.0F, 1.0F, 0.0F, 0.0F);
    }
    if (player.field_70737_aN > 0 || player.field_70725_aQ > 0)
      GlStateManager.color(1.0F, 0.0F, 0.0F, 0.3F);
    super.func_177141_a(player, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
    GlStateManager.popMatrix();
  }
}
