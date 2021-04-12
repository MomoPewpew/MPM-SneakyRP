package noppes.mpm.client.model.part.horns;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

public class ModelAntlerHorns extends ModelRenderer {
  public ModelAntlerHorns(ModelBiped base) {
    super((ModelBase)base);
    ModelRenderer right_base_horn = new ModelRenderer((ModelBase)base, 58, 20);
    right_base_horn.func_78789_a(0.0F, -5.0F, 0.0F, 1, 6, 1);
    right_base_horn.func_78793_a(-2.5F, -6.0F, -1.0F);
    setRotation(right_base_horn, 0.0F, 0.0F, -0.2F);
    func_78792_a(right_base_horn);
    ModelRenderer right_horn1 = new ModelRenderer((ModelBase)base, 58, 20);
    right_horn1.func_78789_a(0.0F, -5.0F, 0.0F, 1, 5, 1);
    right_horn1.func_78793_a(0.0F, -4.0F, 0.0F);
    setRotation(right_horn1, 1.0F, 0.0F, -1.0F);
    right_base_horn.func_78792_a(right_horn1);
    ModelRenderer right_horn2 = new ModelRenderer((ModelBase)base, 58, 20);
    right_horn2.func_78789_a(0.0F, -4.0F, 0.0F, 1, 5, 1);
    right_horn2.func_78793_a(-0.0F, -6.0F, -0.0F);
    setRotation(right_horn2, -0.5F, -0.5F, 0.0F);
    right_base_horn.func_78792_a(right_horn2);
    ModelRenderer things1 = new ModelRenderer((ModelBase)base, 58, 20);
    things1.func_78789_a(0.0F, -5.0F, 0.0F, 1, 5, 1);
    things1.func_78793_a(0.0F, -3.0F, 1.0F);
    setRotation(things1, 2.0F, 0.5F, 0.5F);
    right_horn2.func_78792_a(things1);
    ModelRenderer things2 = new ModelRenderer((ModelBase)base, 58, 20);
    things2.func_78789_a(0.0F, -5.0F, 0.0F, 1, 5, 1);
    things2.func_78793_a(0.0F, -3.0F, 1.0F);
    setRotation(things2, 2.0F, -0.5F, -0.5F);
    right_horn2.func_78792_a(things2);
    ModelRenderer left_base_horn = new ModelRenderer((ModelBase)base, 58, 20);
    left_base_horn.func_78789_a(0.0F, -5.0F, 0.0F, 1, 6, 1);
    left_base_horn.func_78793_a(1.5F, -6.0F, -1.0F);
    setRotation(left_base_horn, 0.0F, 0.0F, 0.2F);
    func_78792_a(left_base_horn);
    ModelRenderer left_horn1 = new ModelRenderer((ModelBase)base, 58, 20);
    left_horn1.func_78789_a(0.0F, -5.0F, 0.0F, 1, 5, 1);
    left_horn1.func_78793_a(0.0F, -5.0F, 0.0F);
    setRotation(left_horn1, 1.0F, 0.0F, 1.0F);
    left_base_horn.func_78792_a(left_horn1);
    ModelRenderer left_horn2 = new ModelRenderer((ModelBase)base, 58, 20);
    left_horn2.func_78789_a(0.0F, -4.0F, 0.0F, 1, 5, 1);
    left_horn2.func_78793_a(0.0F, -6.0F, 1.0F);
    setRotation(left_horn2, -0.5F, 0.5F, 0.0F);
    left_base_horn.func_78792_a(left_horn2);
    ModelRenderer things8 = new ModelRenderer((ModelBase)base, 58, 20);
    things8.func_78789_a(0.0F, -5.0F, 0.0F, 1, 5, 1);
    things8.func_78793_a(0.0F, -3.0F, 1.0F);
    setRotation(things8, 2.0F, -0.5F, -0.5F);
    left_horn2.func_78792_a(things8);
    ModelRenderer things4 = new ModelRenderer((ModelBase)base, 58, 20);
    things4.func_78789_a(0.0F, -5.0F, 0.0F, 1, 5, 1);
    things4.func_78793_a(0.0F, -3.0F, 1.0F);
    setRotation(things4, 2.0F, 0.5F, 0.5F);
    left_horn2.func_78792_a(things4);
  }

  private void setRotation(ModelRenderer model, float x, float y, float z) {
    model.field_78795_f = x;
    model.field_78796_g = y;
    model.field_78808_h = z;
  }
}
