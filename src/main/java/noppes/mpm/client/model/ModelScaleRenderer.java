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
    func_78784_a(par2, par3);
  }

  public void setRotation(ModelRenderer model, float x, float y, float z) {
    model.field_78795_f = x;
    model.field_78796_g = y;
    model.field_78808_h = z;
  }

  public void func_78785_a(float par1) {
    if (!this.field_78806_j || this.field_78807_k)
      return;
    if (!this.isCompiled)
      compile(par1);
    GlStateManager.func_179094_E();
    func_78794_c(par1);
    GlStateManager.func_179148_o(this.field_78811_r);
    if (this.field_78805_m != null)
      for (int i = 0; i < this.field_78805_m.size(); i++)
        ((ModelRenderer)this.field_78805_m.get(i)).func_78785_a(par1);
    GlStateManager.func_179121_F();
  }

  public void func_78794_c(float par1) {
    if (this.config != null)
      GlStateManager.func_179109_b(this.config.transX, this.config.transY, this.config.transZ);
    super.func_78794_c(par1);
    if (this.config != null)
      GlStateManager.func_179152_a(this.config.scaleX, this.config.scaleY, this.config.scaleZ);
  }

  public void postRenderNoScale(float par1) {
    GlStateManager.func_179109_b(this.config.transX, this.config.transY, this.config.transZ);
    super.func_78794_c(par1);
  }

  public void parentRender(float par1) {
    super.func_78785_a(par1);
  }

  public void compile(float par1) {
    this.field_78811_r = GLAllocation.func_74526_a(1);
    GL11.glNewList(this.field_78811_r, 4864);
    BufferBuilder worldrenderer = Tessellator.func_178181_a().func_178180_c();
    for (int i = 0; i < this.field_78804_l.size(); i++)
      ((ModelBox)this.field_78804_l.get(i)).func_178780_a(worldrenderer, par1);
    GL11.glEndList();
    this.isCompiled = true;
  }
}
