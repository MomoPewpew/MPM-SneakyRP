package noppes.mpm.client.model.part.horns;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

public class ModelAntennasBack extends ModelRenderer {
  public ModelAntennasBack(ModelBiped base) {
    super((ModelBase)base);
    ModelRenderer rightantenna1 = new ModelRenderer((ModelBase)base, 60, 27);
    rightantenna1.func_78789_a(-1.0F, 0.0F, 0.0F, 1, 4, 1);
    rightantenna1.func_78793_a(3.0F, -10.9F, 0.0F);
    setRotation(rightantenna1, -0.7504916F, 0.0698132F, 0.0698132F);
    func_78792_a(rightantenna1);
    ModelRenderer leftantenna1 = new ModelRenderer((ModelBase)base, 56, 27);
    leftantenna1.field_78809_i = true;
    leftantenna1.func_78789_a(0.0F, 0.0F, 0.0F, 1, 4, 1);
    leftantenna1.func_78793_a(-3.0F, -10.9F, 0.0F);
    setRotation(leftantenna1, -0.7504916F, -0.0698132F, -0.0698132F);
    func_78792_a(leftantenna1);
    ModelRenderer rightantenna2 = new ModelRenderer((ModelBase)base, 60, 27);
    rightantenna2.func_78789_a(-1.0F, 0.0F, 0.0F, 1, 4, 1);
    rightantenna2.func_78793_a(4.6F, -12.2F, 3.4F);
    setRotation(rightantenna2, -1.22173F, 0.4363323F, 0.0698132F);
    func_78792_a(rightantenna2);
    ModelRenderer leftantenna2 = new ModelRenderer((ModelBase)base, 56, 27);
    leftantenna2.field_78809_i = true;
    leftantenna2.func_78789_a(0.0F, 0.0F, 0.0F, 1, 4, 1);
    leftantenna2.func_78793_a(-4.6F, -12.2F, 3.4F);
    setRotation(leftantenna2, -1.22173F, -0.4363323F, -0.0698132F);
    func_78792_a(leftantenna2);
  }

  private void setRotation(ModelRenderer model, float x, float y, float z) {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
}
