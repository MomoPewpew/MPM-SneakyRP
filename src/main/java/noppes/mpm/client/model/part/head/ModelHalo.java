package noppes.mpm.client.model.part.head;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class ModelHalo extends ModelBase {
  private static ResourceLocation wingTexture = new ResourceLocation("moreplayermodels", "textures/wings/4.png");

  private ModelRenderer head;

  private ModelRenderer halo;

  private ModelRenderer halo_1;

  private ModelRenderer halo_2;

  private ModelRenderer halo_3;

  private ModelRenderer halo_4;

  private ModelRenderer halo_5;

  private ModelRenderer halo_6;

  private ModelRenderer halo_7;

  private ModelRenderer halo_8;

  private ModelRenderer halo_9;

  private ModelRenderer halo_10;

  private ModelRenderer halo_11;

  public ModelHalo() {
    this.textureWidth = 81;
    this.textureHeight = 34;
    this.halo_1 = new ModelRenderer(this, 0, 32);
    this.halo_1.setRotationPoint(2.0F, 0.0F, 0.0F);
    this.halo_1.addBox(0.0F, -1.0F, 0.0F, 2, 1, 1, 0.0F);
    setRotateAngle(this.halo_1, 0.0F, -0.5235988F, 0.0F);
    this.halo_3 = new ModelRenderer(this, 0, 32);
    this.halo_3.setRotationPoint(2.0F, 0.0F, 0.0F);
    this.halo_3.addBox(0.0F, -1.0F, 0.0F, 2, 1, 1, 0.0F);
    setRotateAngle(this.halo_3, 0.0F, -0.5235988F, 0.0F);
    this.halo_11 = new ModelRenderer(this, 0, 32);
    this.halo_11.setRotationPoint(2.0F, 0.0F, 0.0F);
    this.halo_11.addBox(0.0F, -1.0F, 0.0F, 2, 1, 1, 0.0F);
    setRotateAngle(this.halo_11, 0.0F, -0.5235988F, 0.0F);
    this.halo = new ModelRenderer(this, 0, 32);
    this.halo.setRotationPoint(0.0F, -9.0F, -3.85F);
    this.halo.addBox(0.0F, -1.0F, 0.0F, 2, 1, 1, 0.0F);
    setRotateAngle(this.halo, 0.0F, -0.2617994F, 0.0F);
    this.halo_10 = new ModelRenderer(this, 0, 32);
    this.halo_10.setRotationPoint(2.0F, 0.0F, 0.0F);
    this.halo_10.addBox(0.0F, -1.0F, 0.0F, 2, 1, 1, 0.0F);
    setRotateAngle(this.halo_10, 0.0F, -0.5235988F, 0.0F);
    this.halo_5 = new ModelRenderer(this, 0, 32);
    this.halo_5.setRotationPoint(2.0F, 0.0F, 0.0F);
    this.halo_5.addBox(0.0F, -1.0F, 0.0F, 2, 1, 1, 0.0F);
    setRotateAngle(this.halo_5, 0.0F, -0.5235988F, 0.0F);
    this.head = new ModelRenderer(this, 0, 34);
    this.head.setRotationPoint(0.0F, 0.0F, 0.0F);
    this.head.addBox(-4.0F, -8.0F, -4.0F, 0, 0, 0, 0.0F);
    this.halo_7 = new ModelRenderer(this, 0, 32);
    this.halo_7.setRotationPoint(2.0F, 0.0F, 0.0F);
    this.halo_7.addBox(0.0F, -1.0F, 0.0F, 2, 1, 1, 0.0F);
    setRotateAngle(this.halo_7, 0.0F, -0.5235988F, 0.0F);
    this.halo_4 = new ModelRenderer(this, 0, 32);
    this.halo_4.setRotationPoint(2.0F, 0.0F, 0.0F);
    this.halo_4.addBox(0.0F, -1.0F, 0.0F, 2, 1, 1, 0.0F);
    setRotateAngle(this.halo_4, 0.0F, -0.5235988F, 0.0F);
    this.halo_6 = new ModelRenderer(this, 0, 32);
    this.halo_6.setRotationPoint(2.0F, 0.0F, 0.0F);
    this.halo_6.addBox(0.0F, -1.0F, 0.0F, 2, 1, 1, 0.0F);
    setRotateAngle(this.halo_6, 0.0F, -0.5235988F, 0.0F);
    this.halo_8 = new ModelRenderer(this, 0, 32);
    this.halo_8.setRotationPoint(2.0F, 0.0F, 0.0F);
    this.halo_8.addBox(0.0F, -1.0F, 0.0F, 2, 1, 1, 0.0F);
    setRotateAngle(this.halo_8, 0.0F, -0.5235988F, 0.0F);
    this.halo_9 = new ModelRenderer(this, 0, 32);
    this.halo_9.setRotationPoint(2.0F, 0.0F, 0.0F);
    this.halo_9.addBox(0.0F, -1.0F, 0.0F, 2, 1, 1, 0.0F);
    setRotateAngle(this.halo_9, 0.0F, -0.5235988F, 0.0F);
    this.halo_2 = new ModelRenderer(this, 0, 32);
    this.halo_2.setRotationPoint(2.0F, 0.0F, 0.0F);
    this.halo_2.addBox(0.0F, -1.0F, 0.0F, 2, 1, 1, 0.0F);
    setRotateAngle(this.halo_2, 0.0F, -0.5235988F, 0.0F);
    this.halo.addChild(this.halo_1);
    this.halo_2.addChild(this.halo_3);
    this.halo_10.addChild(this.halo_11);
    this.head.addChild(this.halo);
    this.halo_9.addChild(this.halo_10);
    this.halo_4.addChild(this.halo_5);
    this.halo_6.addChild(this.halo_7);
    this.halo_3.addChild(this.halo_4);
    this.halo_5.addChild(this.halo_6);
    this.halo_7.addChild(this.halo_8);
    this.halo_8.addChild(this.halo_9);
    this.halo_1.addChild(this.halo_2);
  }

  public void render(float f5, EntityPlayer entityIn) {
    this.isRiding = false;
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0.0F, 240.0F);
    (Minecraft.getMinecraft()).renderEngine.bindTexture(wingTexture);
    GlStateManager.pushMatrix();
    GlStateManager.rotate((float)entityIn.worldObj.getTotalWorldTime(), 0.0F, 1.0F, 0.0F);
    float f = entityIn.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks();
    float f1 = MathHelper.sin(f * 0.2F) / 2.0F + 0.5F;
    f1 = f1 * f1 + f1;
    GlStateManager.translate(0.0F, -0.2F + f1 * 0.05F, 0.0F);
    if (this.isRiding) {
      GlStateManager.translate(0.75F, 0.75F, 0.75F);
      GlStateManager.translate(0.0F, 16.0F * f5, 0.0F);
      if (entityIn.isSneaking())
        GlStateManager.translate(0.0F, 0.2F, 0.0F);
      GlStateManager.disableLighting();
      this.head.render(f5);
      GlStateManager.enableLighting();
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0.0F, 240.0F);
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.5F, 0.5F, 0.5F);
      GlStateManager.translate(0.0F, 24.0F * f5, 0.0F);
      if (entityIn.isSneaking())
        GlStateManager.translate(0.0F, 0.2F, 0.0F);
    } else {
      if (entityIn.isSneaking())
        GlStateManager.translate(0.0F, 0.2F, 0.0F);
      GlStateManager.disableLighting();
      this.head.render(f5);
      GlStateManager.enableLighting();
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0.0F, 240.0F);
    }
    GlStateManager.popMatrix();
  }

  private void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
    modelRenderer.rotateAngleX = x;
    modelRenderer.rotateAngleY = y;
    modelRenderer.rotateAngleZ = z;
  }
}
