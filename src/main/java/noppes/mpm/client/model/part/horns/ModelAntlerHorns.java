package noppes.mpm.client.model.part.horns;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

public class ModelAntlerHorns extends ModelRenderer {
  public ModelAntlerHorns(ModelBiped base) {
    super((ModelBase)base);
    ModelRenderer right_base_horn = new ModelRenderer((ModelBase)base, 58, 20);
    right_base_horn.addBox(0.0F, -5.0F, 0.0F, 1, 6, 1);
    right_base_horn.setRotationPoint(-2.5F, -6.0F, -1.0F);
    setRotation(right_base_horn, 0.0F, 0.0F, -0.2F);
    addChild(right_base_horn);
    ModelRenderer right_horn1 = new ModelRenderer((ModelBase)base, 58, 20);
    right_horn1.addBox(0.0F, -5.0F, 0.0F, 1, 5, 1);
    right_horn1.setRotationPoint(0.0F, -4.0F, 0.0F);
    setRotation(right_horn1, 1.0F, 0.0F, -1.0F);
    right_base_horn.addChild(right_horn1);
    ModelRenderer right_horn2 = new ModelRenderer((ModelBase)base, 58, 20);
    right_horn2.addBox(0.0F, -4.0F, 0.0F, 1, 5, 1);
    right_horn2.setRotationPoint(-0.0F, -6.0F, -0.0F);
    setRotation(right_horn2, -0.5F, -0.5F, 0.0F);
    right_base_horn.addChild(right_horn2);
    ModelRenderer things1 = new ModelRenderer((ModelBase)base, 58, 20);
    things1.addBox(0.0F, -5.0F, 0.0F, 1, 5, 1);
    things1.setRotationPoint(0.0F, -3.0F, 1.0F);
    setRotation(things1, 2.0F, 0.5F, 0.5F);
    right_horn2.addChild(things1);
    ModelRenderer things2 = new ModelRenderer((ModelBase)base, 58, 20);
    things2.addBox(0.0F, -5.0F, 0.0F, 1, 5, 1);
    things2.setRotationPoint(0.0F, -3.0F, 1.0F);
    setRotation(things2, 2.0F, -0.5F, -0.5F);
    right_horn2.addChild(things2);
    ModelRenderer left_base_horn = new ModelRenderer((ModelBase)base, 58, 20);
    left_base_horn.addBox(0.0F, -5.0F, 0.0F, 1, 6, 1);
    left_base_horn.setRotationPoint(1.5F, -6.0F, -1.0F);
    setRotation(left_base_horn, 0.0F, 0.0F, 0.2F);
    addChild(left_base_horn);
    ModelRenderer left_horn1 = new ModelRenderer((ModelBase)base, 58, 20);
    left_horn1.addBox(0.0F, -5.0F, 0.0F, 1, 5, 1);
    left_horn1.setRotationPoint(0.0F, -5.0F, 0.0F);
    setRotation(left_horn1, 1.0F, 0.0F, 1.0F);
    left_base_horn.addChild(left_horn1);
    ModelRenderer left_horn2 = new ModelRenderer((ModelBase)base, 58, 20);
    left_horn2.addBox(0.0F, -4.0F, 0.0F, 1, 5, 1);
    left_horn2.setRotationPoint(0.0F, -6.0F, 1.0F);
    setRotation(left_horn2, -0.5F, 0.5F, 0.0F);
    left_base_horn.addChild(left_horn2);
    ModelRenderer things8 = new ModelRenderer((ModelBase)base, 58, 20);
    things8.addBox(0.0F, -5.0F, 0.0F, 1, 5, 1);
    things8.setRotationPoint(0.0F, -3.0F, 1.0F);
    setRotation(things8, 2.0F, -0.5F, -0.5F);
    left_horn2.addChild(things8);
    ModelRenderer things4 = new ModelRenderer((ModelBase)base, 58, 20);
    things4.addBox(0.0F, -5.0F, 0.0F, 1, 5, 1);
    things4.setRotationPoint(0.0F, -3.0F, 1.0F);
    setRotation(things4, 2.0F, 0.5F, 0.5F);
    left_horn2.addChild(things4);
  }

  private void setRotation(ModelRenderer model, float x, float y, float z) {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
}
