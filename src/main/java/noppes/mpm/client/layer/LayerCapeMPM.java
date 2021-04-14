package noppes.mpm.client.layer;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerCape;
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

     @Override
     public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
          ModelBiped model = this.render.getMainModel();
          ModelData data = ModelData.get(player);
          ModelPartConfig config = data.getPartConfig(EnumParts.BODY);
          GlStateManager.pushMatrix();
          if (player.isSneaking() && !data.animationEquals(EnumAnimation.CRAWLING)) {
               GlStateManager.translate(0.0F, 0.0F, (-2.0F + config.scaleZ) * scale);
          }

          GlStateManager.translate(config.transX, config.transY, config.transZ + (-1.0F + config.scaleZ) * scale);
          GlStateManager.scale(config.scaleX, config.scaleY, 1.0F);
          if (data.animationEquals(EnumAnimation.CRAWLING)) {
               int rotation = true;
               if (player.isSneaking()) {
                    GlStateManager.rotate(-25.0F, 1.0F, 0.0F, 0.0F);
               }
          }

          if (player.hurtTime > 0 || player.deathTime > 0) {
               GlStateManager.color(1.0F, 0.0F, 0.0F, 0.3F);
          }

          super.doRenderLayer(player, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
          GlStateManager.popMatrix();
     }
}
