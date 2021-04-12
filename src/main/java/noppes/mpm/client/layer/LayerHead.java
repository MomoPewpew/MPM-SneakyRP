package noppes.mpm.client.layer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import noppes.mpm.ModelPartData;
import noppes.mpm.client.model.Model2DRenderer;
import noppes.mpm.client.model.part.head.ModelDuckBeak;
import noppes.mpm.client.model.part.head.ModelHalo;
import noppes.mpm.client.model.part.horns.ModelAntennasBack;
import noppes.mpm.client.model.part.horns.ModelAntennasFront;
import noppes.mpm.client.model.part.horns.ModelAntlerHorns;
import noppes.mpm.client.model.part.horns.ModelBullHorns;
import noppes.mpm.constants.EnumParts;

public class LayerHead extends LayerInterface {
  private ModelRenderer small;

  private ModelRenderer medium;

  private ModelRenderer large;

  private ModelRenderer bunnySnout;

  private ModelRenderer beak;

  private Model2DRenderer beard;

  private Model2DRenderer hair;

  private Model2DRenderer mohawk;

  private ModelRenderer bull;

  private ModelRenderer antlers;

  private ModelRenderer antennasBack;

  private ModelRenderer antennasFront;

  private ModelRenderer ears;

  private ModelRenderer bunnyEars;

  private ModelHalo halo;

  public LayerHead(RenderPlayer render) {
    super(render);
  }

  protected void createParts() {
    this.small = new ModelRenderer((ModelBase)this.model, 24, 0);
    this.small.func_78789_a(0.0F, 0.0F, 0.0F, 4, 3, 1);
    this.small.func_78793_a(-2.0F, -3.0F, -5.0F);
    this.medium = new ModelRenderer((ModelBase)this.model, 24, 0);
    this.medium.func_78789_a(0.0F, 0.0F, 0.0F, 4, 3, 2);
    this.medium.func_78793_a(-2.0F, -3.0F, -6.0F);
    this.large = new ModelRenderer((ModelBase)this.model, 24, 0);
    this.large.func_78789_a(0.0F, 0.0F, 0.0F, 4, 3, 3);
    this.large.func_78793_a(-2.0F, -3.0F, -7.0F);
    this.bunnySnout = new ModelRenderer((ModelBase)this.model, 24, 0);
    this.bunnySnout.func_78789_a(1.0F, 1.0F, 0.0F, 4, 2, 1);
    this.bunnySnout.func_78793_a(-3.0F, -4.0F, -5.0F);
    ModelRenderer tooth = new ModelRenderer((ModelBase)this.model, 24, 3);
    tooth.func_78789_a(2.0F, 3.0F, 0.0F, 2, 1, 1);
    tooth.func_78793_a(0.0F, 0.0F, 0.0F);
    this.bunnySnout.func_78792_a(tooth);
    this.beak = (ModelRenderer)new ModelDuckBeak((ModelBiped)this.model);
    this.beak.func_78793_a(0.0F, 0.0F, -4.0F);
    this.beard = new Model2DRenderer((ModelBase)this.model, 56.0F, 20.0F, 8, 12);
    this.beard.setRotationOffset(-3.99F, 11.8F, -4.0F);
    this.beard.setScale(0.74F);
    this.hair = new Model2DRenderer((ModelBase)this.model, 56.0F, 20.0F, 8, 12);
    this.hair.setRotationOffset(-3.99F, 11.8F, 3.0F);
    this.hair.setScale(0.75F);
    this.mohawk = new Model2DRenderer((ModelBase)this.model, 0.0F, 0.0F, 64, 64);
    this.mohawk.func_78787_b(64, 64);
    this.mohawk.setRotationOffset(-9.0F, 0.1F, -0.5F);
    setRotation((ModelRenderer)this.mohawk, 0.0F, 1.5707964F, 0.0F);
    this.mohawk.setScale(0.825F);
    this.bull = (ModelRenderer)new ModelBullHorns((ModelBiped)this.model);
    this.antlers = (ModelRenderer)new ModelAntlerHorns((ModelBiped)this.model);
    this.antennasBack = (ModelRenderer)new ModelAntennasBack((ModelBiped)this.model);
    this.antennasFront = (ModelRenderer)new ModelAntennasFront((ModelBiped)this.model);
    this.ears = new ModelRenderer((ModelBase)this.model);
    Model2DRenderer right = new Model2DRenderer((ModelBase)this.model, 56.0F, 0.0F, 8, 4);
    right.func_78793_a(-7.44F, -7.3F, -0.0F);
    right.setScale(0.234F, 0.234F);
    right.setThickness(1.16F);
    this.ears.func_78792_a((ModelRenderer)right);
    Model2DRenderer left = new Model2DRenderer((ModelBase)this.model, 56.0F, 0.0F, 8, 4);
    left.func_78793_a(7.44F, -7.3F, 1.15F);
    left.setScale(0.234F, 0.234F);
    setRotation((ModelRenderer)left, 0.0F, 3.1415927F, 0.0F);
    left.setThickness(1.16F);
    this.ears.func_78792_a((ModelRenderer)left);
    Model2DRenderer right2 = new Model2DRenderer((ModelBase)this.model, 56.0F, 4.0F, 8, 4);
    right2.func_78793_a(-7.44F, -7.3F, 1.14F);
    right2.setScale(0.234F, 0.234F);
    right2.setThickness(1.16F);
    this.ears.func_78792_a((ModelRenderer)right2);
    Model2DRenderer left2 = new Model2DRenderer((ModelBase)this.model, 56.0F, 4.0F, 8, 4);
    left2.func_78793_a(7.44F, -7.3F, 2.31F);
    left2.setScale(0.234F, 0.234F);
    setRotation((ModelRenderer)left2, 0.0F, 3.1415927F, 0.0F);
    left2.setThickness(1.16F);
    this.ears.func_78792_a((ModelRenderer)left2);
    this.bunnyEars = new ModelRenderer((ModelBase)this.model);
    ModelRenderer earleft = new ModelRenderer((ModelBase)this.model, 56, 0);
    earleft.field_78809_i = true;
    earleft.func_78789_a(-1.466667F, -4.0F, 0.0F, 3, 7, 1);
    earleft.func_78793_a(2.533333F, -11.0F, 0.0F);
    this.bunnyEars.func_78792_a(earleft);
    ModelRenderer earright = new ModelRenderer((ModelBase)this.model, 56, 0);
    earright.func_78789_a(-1.5F, -4.0F, 0.0F, 3, 7, 1);
    earright.func_78793_a(-2.466667F, -11.0F, 0.0F);
    this.bunnyEars.func_78792_a(earright);
    this.halo = new ModelHalo();
  }

  public void render(float par2, float par3, float par4, float par5, float par6, float scale) {
    this.model.field_78116_c.func_78794_c(0.0625F);
    renderSnout(scale);
    renderBeard(scale);
    renderHair(scale);
    renderMohawk(scale);
    renderHorns(scale);
    renderEars(scale);
    renderHalo(scale);
  }

  private void renderHalo(float scale) {
    ModelPartData data = this.playerdata.getPartData(EnumParts.HALO);
    if (data == null)
      return;
    preRender(data);
    this.halo.render(scale, (EntityPlayer)this.player);
  }

  private void renderSnout(float scale) {
    ModelPartData data = this.playerdata.getPartData(EnumParts.SNOUT);
    if (data == null)
      return;
    preRender(data);
    if (data.type == 0) {
      this.small.func_78785_a(scale);
    } else if (data.type == 1) {
      this.medium.func_78785_a(scale);
    } else if (data.type == 2) {
      this.large.func_78785_a(scale);
    } else if (data.type == 3) {
      this.bunnySnout.func_78785_a(scale);
    } else if (data.type == 4) {
      this.beak.func_78785_a(scale);
    }
  }

  private void renderBeard(float scale) {
    ModelPartData data = this.playerdata.getPartData(EnumParts.BEARD);
    if (data == null)
      return;
    preRender(data);
    this.beard.func_78785_a(scale);
  }

  private void renderHair(float scale) {
    ModelPartData data = this.playerdata.getPartData(EnumParts.HAIR);
    if (data == null)
      return;
    preRender(data);
    this.hair.func_78785_a(scale);
  }

  private void renderMohawk(float scale) {
    ModelPartData data = this.playerdata.getPartData(EnumParts.MOHAWK);
    if (data == null)
      return;
    preRender(data);
    this.mohawk.func_78785_a(scale);
  }

  private void renderHorns(float scale) {
    ModelPartData data = this.playerdata.getPartData(EnumParts.HORNS);
    if (data == null)
      return;
    preRender(data);
    if (data.type == 0) {
      this.bull.func_78785_a(scale);
    } else if (data.type == 1) {
      this.antlers.func_78785_a(scale);
    } else if (data.type == 2 && data.pattern == 0) {
      this.antennasBack.func_78785_a(scale);
    } else if (data.type == 2 && data.pattern == 1) {
      this.antennasFront.func_78785_a(scale);
    }
  }

  private void renderEars(float scale) {
    ModelPartData data = this.playerdata.getPartData(EnumParts.EARS);
    if (data == null)
      return;
    preRender(data);
    if (data.type == 0) {
      this.ears.func_78785_a(scale);
    } else if (data.type == 1) {
      this.bunnyEars.func_78785_a(scale);
    }
  }

  public void rotate(float par2, float par3, float par4, float par5, float par6, float scale) {
    ModelRenderer head = this.model.field_78116_c;
    if (head.field_78795_f < 0.0F) {
      this.beard.field_78795_f = 0.0F;
      this.hair.field_78795_f = -head.field_78795_f * 1.2F;
      if (head.field_78795_f > -1.0F) {
        this.hair.field_78797_d = -head.field_78795_f * 1.5F;
        this.hair.field_78798_e = -head.field_78795_f * 1.5F;
      }
    } else {
      this.hair.field_78795_f = 0.0F;
      this.hair.field_78797_d = 0.0F;
      this.hair.field_78798_e = 0.0F;
      this.beard.field_78795_f = -head.field_78795_f;
    }
  }
}
