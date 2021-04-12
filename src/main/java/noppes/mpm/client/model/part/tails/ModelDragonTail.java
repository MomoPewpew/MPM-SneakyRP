package noppes.mpm.client.model.part.tails;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import noppes.mpm.client.model.ModelPlaneRenderer;

public class ModelDragonTail extends ModelRenderer {
  public ModelDragonTail(ModelBiped base) {
    super((ModelBase)base);
    int x = 52;
    int y = 16;
    ModelRenderer dragon = new ModelRenderer((ModelBase)base, x, y);
    dragon.func_78793_a(0.0F, 0.0F, 3.0F);
    func_78792_a(dragon);
    ModelRenderer DragonTail2 = new ModelRenderer((ModelBase)base, x, y);
    DragonTail2.func_78793_a(0.0F, 2.0F, 2.0F);
    ModelRenderer DragonTail3 = new ModelRenderer((ModelBase)base, x, y);
    DragonTail3.func_78793_a(0.0F, 4.5F, 4.0F);
    ModelRenderer DragonTail4 = new ModelRenderer((ModelBase)base, x, y);
    DragonTail4.func_78793_a(0.0F, 7.0F, 5.75F);
    ModelRenderer DragonTail5 = new ModelRenderer((ModelBase)base, x, y);
    DragonTail5.func_78793_a(0.0F, 9.0F, 8.0F);
    ModelPlaneRenderer planeLeft = new ModelPlaneRenderer((ModelBase)base, x, y);
    planeLeft.addSidePlane(-1.5F, -1.5F, -1.5F, 3, 3);
    ModelPlaneRenderer planeRight = new ModelPlaneRenderer((ModelBase)base, x, y);
    planeRight.addSidePlane(-1.5F, -1.5F, -1.5F, 3, 3);
    setRotation((ModelRenderer)planeRight, 3.1415927F, 3.1415927F, 0.0F);
    ModelPlaneRenderer planeTop = new ModelPlaneRenderer((ModelBase)base, x, y);
    planeTop.addTopPlane(-1.5F, -1.5F, -1.5F, 3, 3);
    setRotation((ModelRenderer)planeTop, 0.0F, -1.5707964F, 0.0F);
    ModelPlaneRenderer planeBottom = new ModelPlaneRenderer((ModelBase)base, x, y);
    planeBottom.addTopPlane(-1.5F, -1.5F, -1.5F, 3, 3);
    setRotation((ModelRenderer)planeBottom, 0.0F, -1.5707964F, 3.1415927F);
    ModelPlaneRenderer planeBack = new ModelPlaneRenderer((ModelBase)base, x, y);
    planeBack.addBackPlane(-1.5F, -1.5F, -1.5F, 3, 3);
    setRotation((ModelRenderer)planeBack, 0.0F, 0.0F, 1.5707964F);
    ModelPlaneRenderer planeFront = new ModelPlaneRenderer((ModelBase)base, x, y);
    planeFront.addBackPlane(-1.5F, -1.5F, -1.5F, 3, 3);
    setRotation((ModelRenderer)planeFront, 0.0F, 3.1415927F, -1.5707964F);
    dragon.func_78792_a((ModelRenderer)planeLeft);
    dragon.func_78792_a((ModelRenderer)planeRight);
    dragon.func_78792_a((ModelRenderer)planeTop);
    dragon.func_78792_a((ModelRenderer)planeBottom);
    dragon.func_78792_a((ModelRenderer)planeFront);
    dragon.func_78792_a((ModelRenderer)planeBack);
    DragonTail2.func_78792_a((ModelRenderer)planeLeft);
    DragonTail2.func_78792_a((ModelRenderer)planeRight);
    DragonTail2.func_78792_a((ModelRenderer)planeTop);
    DragonTail2.func_78792_a((ModelRenderer)planeBottom);
    DragonTail2.func_78792_a((ModelRenderer)planeFront);
    DragonTail2.func_78792_a((ModelRenderer)planeBack);
    DragonTail3.func_78792_a((ModelRenderer)planeLeft);
    DragonTail3.func_78792_a((ModelRenderer)planeRight);
    DragonTail3.func_78792_a((ModelRenderer)planeTop);
    DragonTail3.func_78792_a((ModelRenderer)planeBottom);
    DragonTail3.func_78792_a((ModelRenderer)planeFront);
    DragonTail3.func_78792_a((ModelRenderer)planeBack);
    DragonTail4.func_78792_a((ModelRenderer)planeLeft);
    DragonTail4.func_78792_a((ModelRenderer)planeRight);
    DragonTail4.func_78792_a((ModelRenderer)planeTop);
    DragonTail4.func_78792_a((ModelRenderer)planeBottom);
    DragonTail4.func_78792_a((ModelRenderer)planeFront);
    DragonTail4.func_78792_a((ModelRenderer)planeBack);
    dragon.func_78792_a(DragonTail2);
    dragon.func_78792_a(DragonTail3);
    dragon.func_78792_a(DragonTail4);
  }

  public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {}

  private void setRotation(ModelRenderer model, float x, float y, float z) {
    model.field_78795_f = x;
    model.field_78796_g = y;
    model.field_78808_h = z;
  }
}
