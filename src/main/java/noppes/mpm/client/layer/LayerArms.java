package noppes.mpm.client.layer;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import noppes.mpm.ModelData;
import noppes.mpm.ModelPartData;
import noppes.mpm.client.model.Model2DRenderer;
import noppes.mpm.constants.EnumParts;

public class LayerArms extends LayerInterface implements LayerPreRender  {
     private Model2DRenderer lClaw;
     private Model2DRenderer rClaw;

     public LayerArms(RenderPlayer render) {
          super(render);
     }

     protected void createParts() {
          this.lClaw = new Model2DRenderer(this.model, 0.0F, 16.0F, 4, 4);
          this.lClaw.setRotationPoint(3.0F, 14.0F, -2.0F);
          this.lClaw.rotateAngleY = -1.5707964F;
          this.lClaw.setScale(0.25F);
          this.rClaw = new Model2DRenderer(this.model, 0.0F, 16.0F, 4, 4);
          this.rClaw.setRotationPoint(-2.0F, 14.0F, -2.0F);
          this.rClaw.rotateAngleY = -1.5707964F;
          this.rClaw.setScale(0.25F);
     }

     public void render(float par2, float par3, float par4, float par5, float par6, float par7) {
          ModelPartData data = this.playerdata.getPartData(EnumParts.CLAWS);
          if (data != null) {

               this.preRender(data);
               if (data.pattern == 0 || data.pattern == 1) {
            	   GlStateManager.translate(this.model.bipedLeftArm.offsetX, this.model.bipedLeftArm.offsetY, this.model.bipedLeftArm.offsetZ);
                    GlStateManager.pushMatrix();
                    this.model.bipedLeftArm.postRender(0.0625F);
                    this.lClaw.render(par7);
                    GlStateManager.popMatrix();
                    GlStateManager.translate(-this.model.bipedLeftArm.offsetX, -this.model.bipedLeftArm.offsetY, -this.model.bipedLeftArm.offsetZ);
               }

               if (data.pattern == 0 || data.pattern == 2) {
            	   GlStateManager.translate(this.model.bipedRightArm.offsetX, this.model.bipedRightArm.offsetY, this.model.bipedRightArm.offsetZ);
                    GlStateManager.pushMatrix();
                    this.model.bipedRightArm.postRender(0.0625F);
                    this.rClaw.render(par7);
                    GlStateManager.popMatrix();
                    GlStateManager.translate(-this.model.bipedRightArm.offsetX, -this.model.bipedRightArm.offsetY, -this.model.bipedRightArm.offsetZ);
               }
          }
     }

     public void rotate(float par2, float par3, float par4, float par5, float par6, float par7) {
     }

	@Override
	public void preRender(AbstractClientPlayer player) {
		this.player = player;
        this.playerdata = ModelData.get(player);
        ModelPartData data = this.playerdata.getOrCreatePart(EnumParts.ARMS);
        this.model.bipedLeftArm.isHidden = this.model.bipedRightArm.isHidden = this.model.bipedLeftArmwear.isHidden = this.model.bipedRightArmwear.isHidden = data == null || data.type != 0;
    }
}
