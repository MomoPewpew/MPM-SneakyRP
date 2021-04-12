package noppes.mpm.client.layer;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import noppes.mpm.ModelData;
import noppes.mpm.ModelPartConfig;
import noppes.mpm.ModelPartData;
import noppes.mpm.client.model.part.legs.ModelDigitigradeLegs;
import noppes.mpm.client.model.part.legs.ModelHorseLegs;
import noppes.mpm.client.model.part.legs.ModelMermaidLegs;
import noppes.mpm.client.model.part.legs.ModelMermaidLegs2;
import noppes.mpm.client.model.part.legs.ModelNagaLegs;
import noppes.mpm.client.model.part.legs.ModelSpiderLegs;
import noppes.mpm.client.model.part.tails.ModelCanineTail;
import noppes.mpm.client.model.part.tails.ModelDragonTail;
import noppes.mpm.client.model.part.tails.ModelFeatherTail;
import noppes.mpm.client.model.part.tails.ModelRodentTail;
import noppes.mpm.client.model.part.tails.ModelSquirrelTail;
import noppes.mpm.client.model.part.tails.ModelTailFin;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.constants.EnumParts;

public class LayerLegs extends LayerInterface implements LayerPreRender {
  private ModelSpiderLegs spiderLegs;

  private ModelHorseLegs horseLegs;

  private ModelNagaLegs naga;

  private ModelDigitigradeLegs digitigrade;

  private ModelMermaidLegs mermaid;

  private ModelMermaidLegs2 mermaid2;

  private ModelRenderer tail;

  private ModelCanineTail fox;

  private ModelRenderer dragon;

  private ModelRenderer squirrel;

  private ModelRenderer horse;

  private ModelRenderer fin;

  private ModelRenderer rodent;

  private ModelRenderer feathers;

  float rotationPointZ;

  float rotationPointY;

  public LayerLegs(RenderPlayer render) {
    super(render);
  }

  protected void createParts() {
    this.spiderLegs = new ModelSpiderLegs((ModelBiped)this.model);
    this.horseLegs = new ModelHorseLegs((ModelBiped)this.model);
    this.naga = new ModelNagaLegs((ModelBase)this.model);
    this.mermaid = new ModelMermaidLegs((ModelBase)this.model);
    this.mermaid2 = new ModelMermaidLegs2((ModelBase)this.model);
    this.digitigrade = new ModelDigitigradeLegs((ModelBiped)this.model);
    this.fox = new ModelCanineTail((ModelBiped)this.model);
    this.tail = new ModelRenderer((ModelBase)this.model, 56, 21);
    this.tail.func_78789_a(-1.0F, 0.0F, 0.0F, 2, 9, 2);
    this.tail.func_78793_a(0.0F, 0.0F, 1.0F);
    setRotation(this.tail, 0.87F, 0.0F, 0.0F);
    this.horse = new ModelRenderer((ModelBase)this.model);
    this.horse.func_78787_b(32, 32);
    this.horse.func_78793_a(0.0F, -1.0F, 1.0F);
    ModelRenderer tailBase = new ModelRenderer((ModelBase)this.model, 0, 26);
    tailBase.func_78787_b(32, 32);
    tailBase.func_78789_a(-1.0F, -1.0F, 0.0F, 2, 2, 3);
    setRotation(tailBase, -1.134464F, 0.0F, 0.0F);
    this.horse.func_78792_a(tailBase);
    ModelRenderer tailMiddle = new ModelRenderer((ModelBase)this.model, 0, 13);
    tailMiddle.func_78787_b(32, 32);
    tailMiddle.func_78789_a(-1.5F, -2.0F, 3.0F, 3, 4, 7);
    setRotation(tailMiddle, -1.134464F, 0.0F, 0.0F);
    this.horse.func_78792_a(tailMiddle);
    ModelRenderer tailTip = new ModelRenderer((ModelBase)this.model, 0, 0);
    tailTip.func_78787_b(32, 32);
    tailTip.func_78789_a(-1.5F, -4.5F, 9.0F, 3, 4, 7);
    setRotation(tailTip, -1.40215F, 0.0F, 0.0F);
    this.horse.func_78792_a(tailTip);
    this.horse.rotateAngleX = 0.5F;
    this.dragon = (ModelRenderer)new ModelDragonTail((ModelBiped)this.model);
    this.squirrel = (ModelRenderer)new ModelSquirrelTail((ModelBiped)this.model);
    this.fin = (ModelRenderer)new ModelTailFin((ModelBiped)this.model);
    this.rodent = (ModelRenderer)new ModelRodentTail((ModelBiped)this.model);
    this.feathers = (ModelRenderer)new ModelFeatherTail((ModelBiped)this.model);
  }

  public void render(float par2, float par3, float par4, float par5, float par6, float par7) {
    GlStateManager.pushMatrix();
    renderLegs(par7);
    GlStateManager.popMatrix();
    GlStateManager.pushMatrix();
    renderTails(par7);
    GlStateManager.popMatrix();
  }

  private void renderTails(float par7) {
    ModelPartData data = this.playerdata.getPartData(EnumParts.TAIL);
    if (data == null)
      return;
    ModelPartConfig config = this.playerdata.getPartConfig(EnumParts.LEG_LEFT);
    GlStateManager.translate(config.transX * par7, config.transY + this.rotationPointY * par7, config.transZ * par7 + this.rotationPointZ * par7);
    GlStateManager.translate(config.scaleX, config.scaleY, config.scaleZ);
    preRender(data);
    if (data.type == 0) {
      if (data.pattern == 1) {
        this.tail.field_78800_c = -0.5F;
        this.tail.rotateAngleY = (float)(this.tail.rotateAngleY - 0.2D);
        this.tail.render(par7);
        this.tail.field_78800_c++;
        this.tail.rotateAngleY = (float)(this.tail.rotateAngleY + 0.4D);
        this.tail.render(par7);
        this.tail.field_78800_c = 0.0F;
      } else {
        this.tail.render(par7);
      }
    } else if (data.type == 1) {
      this.dragon.render(par7);
    } else if (data.type == 2) {
      this.horse.render(par7);
    } else if (data.type == 3) {
      this.squirrel.render(par7);
    } else if (data.type == 4) {
      this.fin.render(par7);
    } else if (data.type == 5) {
      this.rodent.render(par7);
    } else if (data.type == 6) {
      this.feathers.render(par7);
    } else if (data.type == 7) {
      this.fox.render(par7);
    }
  }

  private void renderLegs(float par7) {
    ModelPartData data = this.playerdata.getPartData(EnumParts.LEGS);
    if (data.type <= 0)
      return;
    ModelPartConfig config = this.playerdata.getPartConfig(EnumParts.LEG_LEFT);
    preRender(data);
    if (data.type == 1) {
      GlStateManager.translate(0.0F, config.transY * 2.0F, config.transZ * par7 + 0.04F);
      GlStateManager.translate(config.scaleX, config.scaleY, config.scaleZ);
      this.naga.render(par7);
    } else if (data.type == 2) {
      GlStateManager.func_179137_b(0.0D, (config.transY * 1.76F) - 0.1D * config.scaleY, (config.transZ * par7));
      GlStateManager.translate(1.06F, 1.06F, 1.06F);
      GlStateManager.translate(config.scaleX, config.scaleY, config.scaleZ);
      this.spiderLegs.render(par7);
    } else if (data.type == 3) {
      if (config.scaleY >= 1.0F) {
        GlStateManager.translate(0.0F, config.transY * 1.76F, config.transZ * par7);
      } else {
        GlStateManager.translate(0.0F, config.transY * 1.86F, config.transZ * par7);
      }
      GlStateManager.translate(0.79F, 0.9F - config.scaleY / 10.0F, 0.79F);
      GlStateManager.translate(config.scaleX, config.scaleY, config.scaleZ);
      this.horseLegs.render(par7);
    } else if (data.type == 4) {
      GlStateManager.translate(0.0F, config.transY * 1.86F, config.transZ * par7);
      GlStateManager.translate(config.scaleX, config.scaleY, config.scaleZ);
      if (data.pattern == 1) {
        this.mermaid2.render(par7);
      } else {
        this.mermaid.render(par7);
      }
    } else if (data.type == 5) {
      GlStateManager.translate(0.0F, config.transY * 1.86F, config.transZ * par7);
      GlStateManager.translate(config.scaleX, config.scaleY, config.scaleZ);
      this.digitigrade.render(par7);
    }
  }

  public void rotate(float par1, float par2, float par3, float par4, float par5, float par6) {
    rotateLegs(par1, par2, par3, par4, par5, par6);
    rotateTail(par1, par2, par3, par4, par5, par6);
  }

  public void rotateLegs(float par1, float par2, float par3, float par4, float par5, float par6) {
    ModelPartData part = this.playerdata.getPartData(EnumParts.LEGS);
    if (part.type == 2) {
      this.spiderLegs.setRotationAngles(this.playerdata, par1, par2, par3, par4, par5, par6, (Entity)this.player);
    } else if (part.type == 3) {
      this.horseLegs.setRotationAngles(this.playerdata, par1, par2, par3, par4, par5, par6, (Entity)this.player);
    } else if (part.type == 1) {
      this.naga.isRiding = this.model.field_78093_q;
      this.naga.isSleeping = (this.player.isPlayerSleeping() || this.playerdata.isSleeping());
      this.naga.isCrawling = (this.playerdata.animation == EnumAnimation.CRAWLING);
      this.naga.isSneaking = this.model.field_78117_n;
      this.naga.setRotationAngles(par1, par2, par3, par4, par5, par6, (Entity)this.player);
    } else if (part.type == 4) {
      this.mermaid.setRotationAngles(par1, par2, par3, par4, par5, par6, (Entity)this.player, this.playerdata);
      this.mermaid2.setRotationAngles(par1, par2, par3, par4, par5, par6, (Entity)this.player, this.playerdata);
    } else if (part.type == 5) {
      this.digitigrade.setRotationAngles(par1, par2, par3, par4, par5, par6, (Entity)this.player);
    }
  }

  public void rotateTail(float par1, float par2, float par3, float par4, float par5, float par6) {
    ModelPartData part = this.playerdata.getPartData(EnumParts.LEGS);
    ModelPartData partTail = this.playerdata.getPartData(EnumParts.TAIL);
    ModelPartConfig config = this.playerdata.getPartConfig(EnumParts.LEG_LEFT);
    float rotateAngleY = MathHelper.func_76134_b(par1 * 0.6662F) * 0.2F * par2;
    float rotateAngleX = MathHelper.func_76126_a(par3 * 0.067F) * 0.05F;
    this.rotationPointZ = 0.0F;
    this.rotationPointY = 11.0F;
    if (this.playerdata.animation == EnumAnimation.WAG)
      rotateAngleY = (float)(Math.sin((this.player.ticksExisted * 0.55F)) * 0.44999998807907104D);
    if (part.type == 2) {
      this.rotationPointY = 12.0F + (config.scaleY - 1.0F) * 3.0F;
      this.rotationPointZ = 15.0F + (config.scaleZ - 1.0F) * 10.0F;
      if (this.playerdata.isSleeping() || this.player.isPlayerSleeping() || this.playerdata.animation == EnumAnimation.CRAWLING) {
        this.rotationPointY = 12.0F + 16.0F * config.scaleZ;
        this.rotationPointZ = 1.0F * config.scaleY;
        rotateAngleX = -0.7853982F;
      }
    } else if (part.type == 3) {
      this.rotationPointY = 10.0F;
      this.rotationPointZ = 16.0F + (config.scaleZ - 1.0F) * 12.0F;
    } else {
      this.rotationPointZ = (1.0F - config.scaleZ) * 1.0F;
    }
    if (partTail != null) {
      if (partTail.type == 2)
        rotateAngleX = (float)(rotateAngleX + 0.5D);
      if (partTail.type == 0)
        rotateAngleX += 0.87F;
      if (partTail.type == 7)
        this.fox.setRotationAngles(par1, par2, par3, par4, par5, par6, (Entity)this.player);
    }
    this.rotationPointZ += this.model.field_178721_j.field_78798_e + 0.5F;
    this.fox.rotateAngleX = this.rodent.rotateAngleX = rotateAngleX;
    this.fox.rotateAngleY = this.rodent.rotateAngleY = rotateAngleY;
  }

  public void preRender(AbstractClientPlayer player) {
    this.player = player;
    this.playerdata = ModelData.get((EntityPlayer)player);
    ModelPartData data = this.playerdata.getPartData(EnumParts.LEGS);
    this.model.field_178731_d.field_78807_k = (data == null || data.type != 0);
  }
}
