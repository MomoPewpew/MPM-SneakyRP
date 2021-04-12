package noppes.mpm.client.layer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import noppes.mpm.ModelPartData;
import noppes.mpm.client.model.Model2DRenderer;
import noppes.mpm.constants.EnumParts;

public class LayerArms extends LayerInterface {
  private Model2DRenderer lClaw;

  private Model2DRenderer rClaw;

  public LayerArms(RenderPlayer render) {
    super(render);
  }

  protected void createParts() {
    this.lClaw = new Model2DRenderer((ModelBase)this.model, 0.0F, 16.0F, 4, 4);
    this.lClaw.func_78793_a(3.0F, 14.0F, -2.0F);
    this.lClaw.rotateAngleY = -1.5707964F;
    this.lClaw.setScale(0.25F);
    this.rClaw = new Model2DRenderer((ModelBase)this.model, 0.0F, 16.0F, 4, 4);
    this.rClaw.func_78793_a(-2.0F, 14.0F, -2.0F);
    this.rClaw.rotateAngleY = -1.5707964F;
    this.rClaw.setScale(0.25F);
  }

  public void render(float par2, float par3, float par4, float par5, float par6, float par7) {
    ModelPartData data = this.playerdata.getPartData(EnumParts.CLAWS);
    if (data == null)
      return;
    preRender(data);
    if (data.pattern == 0 || data.pattern == 1) {
      GlStateManager.pushMatrix();
      this.model.field_178724_i.func_78794_c(0.0625F);
      this.lClaw.render(par7);
      GlStateManager.popMatrix();
    }
    if (data.pattern == 0 || data.pattern == 2) {
      GlStateManager.pushMatrix();
      this.model.field_178723_h.func_78794_c(0.0625F);
      this.rClaw.render(par7);
      GlStateManager.popMatrix();
    }
  }

  public void rotate(float par2, float par3, float par4, float par5, float par6, float par7) {}
}
