package noppes.mpm.client.model.part.head;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import noppes.mpm.client.model.Model2DRenderer;
import noppes.mpm.client.model.ModelScaleRenderer;
import noppes.mpm.constants.EnumParts;

public class ModelHeadwear extends ModelScaleRenderer {
  public ModelHeadwear(ModelBase base) {
    super(base, EnumParts.HEAD);
    Model2DRenderer right = new Model2DRenderer(base, 32.0F, 8.0F, 8, 8);
    right.setRotationPoint(-4.641F, 0.8F, 4.64F);
    right.setScale(0.58F);
    right.setThickness(0.65F);
    setRotation((ModelRenderer)right, 0.0F, 1.5707964F, 0.0F);
    addChild((ModelRenderer)right);
    Model2DRenderer left = new Model2DRenderer(base, 48.0F, 8.0F, 8, 8);
    left.setRotationPoint(4.639F, 0.8F, -4.64F);
    left.setScale(0.58F);
    left.setThickness(0.65F);
    setRotation((ModelRenderer)left, 0.0F, -1.5707964F, 0.0F);
    addChild((ModelRenderer)left);
    Model2DRenderer front = new Model2DRenderer(base, 40.0F, 8.0F, 8, 8);
    front.setRotationPoint(-4.64F, 0.801F, -4.641F);
    front.setScale(0.58F);
    front.setThickness(0.65F);
    setRotation((ModelRenderer)front, 0.0F, 0.0F, 0.0F);
    addChild((ModelRenderer)front);
    Model2DRenderer back = new Model2DRenderer(base, 56.0F, 8.0F, 8, 8);
    back.setRotationPoint(4.64F, 0.801F, 4.639F);
    back.setScale(0.58F);
    back.setThickness(0.65F);
    setRotation((ModelRenderer)back, 0.0F, 3.1415927F, 0.0F);
    addChild((ModelRenderer)back);
    Model2DRenderer top = new Model2DRenderer(base, 40.0F, 0.0F, 8, 8);
    top.setRotationPoint(-4.64F, -8.5F, -4.64F);
    top.setScale(0.5799F);
    top.setThickness(0.65F);
    setRotation((ModelRenderer)top, -1.5707964F, 0.0F, 0.0F);
    addChild((ModelRenderer)top);
    Model2DRenderer bottom = new Model2DRenderer(base, 48.0F, 0.0F, 8, 8);
    bottom.setRotationPoint(-4.64F, 0.0F, -4.64F);
    bottom.setScale(0.5799F);
    bottom.setThickness(0.65F);
    setRotation((ModelRenderer)bottom, -1.5707964F, 0.0F, 0.0F);
    addChild((ModelRenderer)bottom);
  }

  public void setRotation(ModelRenderer model, float x, float y, float z) {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
}
