package noppes.mpm.client.layer;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import noppes.mpm.ModelData;
import noppes.mpm.ModelPartData;
import noppes.mpm.client.ClientProxy;

public abstract class LayerInterface implements LayerRenderer {
  protected RenderPlayer render;

  protected AbstractClientPlayer player;

  protected ModelData playerdata;

  protected ModelPlayer model;

  public LayerInterface(RenderPlayer render) {
    this.render = render;
    setModel(render.getMainModel());
  }

  public void setModel(ModelPlayer model) {
    this.model = model;
    createParts();
  }

  public void setColor(ModelPartData data, EntityLivingBase entity) {}

  protected void createParts() {}

  public void preRender(ModelPartData data) {
    if (data.playerTexture) {
      ClientProxy.bindTexture(this.player.getLocationSkin());
    } else {
      ClientProxy.bindTexture(data.getResource());
    }
    if (this.player.hurtTime > 0 || this.player.deathTime > 0) {
      GlStateManager.color(1.0F, 0.0F, 0.0F, 0.3F);
      return;
    }
    int color = data.color;
    float red = (color >> 16 & 0xFF) / 255.0F;
    float green = (color >> 8 & 0xFF) / 255.0F;
    float blue = (color & 0xFF) / 255.0F;
    GlStateManager.color(red, green, blue, 0.99F);
  }

  @Override
  public void doRenderLayer(EntityLivingBase entity, float par2, float par3, float par8, float par4, float par5, float par6, float par7) {
    if (entity.isInvisible())
      return;
    this.player = (AbstractClientPlayer)entity;
    this.playerdata = ModelData.get((EntityPlayer)this.player);
    ModelPlayer model = this.render.getMainModel();
    rotate(par2, par3, par4, par5, par6, par7);
    GlStateManager.pushMatrix();
    if (this.player.isSneaking())
      GlStateManager.translate(0.0F, 0.2F, 0.0F);
    GlStateManager.enableRescaleNormal();
    render(par2, par3, par4, par5, par6, par7);
    GlStateManager.disableRescaleNormal();
    GlStateManager.popMatrix();
  }

  public void setRotation(ModelRenderer model, float x, float y, float z) {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }

  @Override
  public boolean shouldCombineTextures() {
    return true;
  }

  public abstract void render(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6);

  public abstract void rotate(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6);
}
