package noppes.mpm.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import noppes.mpm.ModelPartConfig;
import noppes.mpm.constants.EnumParts;
import org.lwjgl.opengl.GL11;

public class ModelScaleRenderer extends ModelRenderer {
  public boolean isCompiled;

  public int field_78811_r;

  public ModelPartConfig config;

  public EnumParts part;

  public ModelScaleRenderer(ModelBase modelBase, EnumParts part) {
    super(modelBase);
    this.part = part;
  }

  public ModelScaleRenderer(ModelBase modelBase, int par2, int par3, EnumParts part) {
    this(modelBase, part);
    setTextureOffset(par2, par3);
  }

  public void setRotation(ModelRenderer model, float x, float y, float z) {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }

  @Override
  public void render(float par1) {
    if (!this.showModel || this.isHidden)
      return;
    if (!this.isCompiled)
      compile(par1);
    GlStateManager.pushMatrix();
    postRender(par1);
    GlStateManager.callList(this.field_78811_r);
    if (this.childModels != null)
      for (int i = 0; i < this.childModels.size(); i++)
        ((ModelRenderer)this.childModels.get(i)).render(par1);
    GlStateManager.popMatrix();
  }

  @Override
  public void postRender(float par1) {
    if (this.config != null)
      GlStateManager.translate(this.config.transX, this.config.transY, this.config.transZ);
    super.postRender(par1);
    if (this.config != null)
      GlStateManager.translate(this.config.scaleX, this.config.scaleY, this.config.scaleZ);
  }

  public void postRenderNoScale(float par1) {
    GlStateManager.translate(this.config.transX, this.config.transY, this.config.transZ);
    super.postRender(par1);
  }

  public void parentRender(float par1) {
    super.render(par1);
  }

  public void compile(float par1) {
    this.field_78811_r = GLAllocation.generateDisplayLists(1);
    GL11.glNewList(this.field_78811_r, 4864);
    BufferBuilder worldrenderer = Tessellator.getInstance().getBuffer();
    for (int i = 0; i < this.cubeList.size(); i++)
      ((ModelBox)this.cubeList.get(i)).render(worldrenderer, par1);
    GL11.glEndList();
    this.isCompiled = true;
  }
}