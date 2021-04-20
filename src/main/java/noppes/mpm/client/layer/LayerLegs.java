package noppes.mpm.client.layer;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
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
     @Override
     protected void createParts() {
          this.spiderLegs = new ModelSpiderLegs(this.model);
          this.horseLegs = new ModelHorseLegs(this.model);
          this.naga = new ModelNagaLegs(this.model);
          this.mermaid = new ModelMermaidLegs(this.model);
          this.mermaid2 = new ModelMermaidLegs2(this.model);
          this.digitigrade = new ModelDigitigradeLegs(this.model);
          this.fox = new ModelCanineTail(this.model);
          this.tail = new ModelRenderer(this.model, 56, 21);
          this.tail.addBox(-1.0F, 0.0F, 0.0F, 2, 9, 2);
          this.tail.setRotationPoint(0.0F, 0.0F, 1.0F);
          this.setRotation(this.tail, 0.87F, 0.0F, 0.0F);
          this.horse = new ModelRenderer(this.model);
          this.horse.setTextureSize(32, 32);
          this.horse.setRotationPoint(0.0F, -1.0F, 1.0F);
          ModelRenderer tailBase = new ModelRenderer(this.model, 0, 26);
          tailBase.setTextureSize(32, 32);
          tailBase.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 3);
          this.setRotation(tailBase, -1.134464F, 0.0F, 0.0F);
          this.horse.addChild(tailBase);
          ModelRenderer tailMiddle = new ModelRenderer(this.model, 0, 13);
          tailMiddle.setTextureSize(32, 32);
          tailMiddle.addBox(-1.5F, -2.0F, 3.0F, 3, 4, 7);
          this.setRotation(tailMiddle, -1.134464F, 0.0F, 0.0F);
          this.horse.addChild(tailMiddle);
          ModelRenderer tailTip = new ModelRenderer(this.model, 0, 0);
          tailTip.setTextureSize(32, 32);
          tailTip.addBox(-1.5F, -4.5F, 9.0F, 3, 4, 7);
          this.setRotation(tailTip, -1.40215F, 0.0F, 0.0F);
          this.horse.addChild(tailTip);
          this.horse.rotateAngleX = 0.5F;
          this.dragon = new ModelDragonTail(this.model);
          this.squirrel = new ModelSquirrelTail(this.model);
          this.fin = new ModelTailFin(this.model);
          this.rodent = new ModelRodentTail(this.model);
          this.feathers = new ModelFeatherTail(this.model);
     }

     @Override
     public void render(float par2, float par3, float par4, float par5, float par6, float par7) {
    	 GlStateManager.translate(this.model.bipedLeftLeg.offsetX, this.model.bipedLeftLeg.offsetY, this.model.bipedLeftLeg.offsetZ);
          GlStateManager.pushMatrix();
          this.renderLegs(par7);
          GlStateManager.popMatrix();
          GlStateManager.pushMatrix();
          this.renderTails(par7);
          GlStateManager.popMatrix();
          GlStateManager.translate(-this.model.bipedLeftLeg.offsetX, -this.model.bipedLeftLeg.offsetY, -this.model.bipedLeftLeg.offsetZ);

     }

     private void renderTails(float par7) {
          ModelPartData data = this.playerdata.getPartData(EnumParts.TAIL);
          if (data != null) {
               ModelPartConfig config = this.playerdata.getPartConfig(EnumParts.LEG_LEFT);
               GlStateManager.translate(config.transX * par7, config.transY + this.rotationPointY * par7, config.transZ * par7 + this.rotationPointZ * par7);
               GlStateManager.scale(config.scaleX, config.scaleY, config.scaleZ);
               this.preRender(data);
               if (data.type == 0) {
                    if (data.pattern == 1) {
                         this.tail.rotationPointX = -0.5F;
                         ModelRenderer var10000 = this.tail;
                         var10000.rotateAngleY = (float)((double)var10000.rotateAngleY - 0.2D);
                         this.tail.render(par7);
                         ++this.tail.rotationPointX;
                         var10000 = this.tail;
                         var10000.rotateAngleY = (float)((double)var10000.rotateAngleY + 0.4D);
                         this.tail.render(par7);
                         this.tail.rotationPointX = 0.0F;
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
     }

     private void renderLegs(float par7) {
          ModelPartData data = this.playerdata.getPartData(EnumParts.LEGS);
          if (data.type > 0) {
               ModelPartConfig config = this.playerdata.getPartConfig(EnumParts.LEG_LEFT);
               this.preRender(data);
               if (data.type == 1) {
                    GlStateManager.translate(0.0F, config.transY * 2.0F, config.transZ * par7 + 0.04F);
                    GlStateManager.scale(config.scaleX, config.scaleY, config.scaleZ);
                    this.naga.render(par7);
               } else if (data.type == 2) {
                    GlStateManager.translate(0.0D, (double)(config.transY * 1.76F) - 0.1D * (double)config.scaleY, (double)(config.transZ * par7));
                    GlStateManager.scale(1.06F, 1.06F, 1.06F);
                    GlStateManager.scale(config.scaleX, config.scaleY, config.scaleZ);
                    this.spiderLegs.render(par7);
               } else if (data.type == 3) {
                    if (config.scaleY >= 1.0F) {
                         GlStateManager.translate(0.0F, config.transY * 1.76F, config.transZ * par7);
                    } else {
                         GlStateManager.translate(0.0F, config.transY * 1.86F, config.transZ * par7);
                    }

                    GlStateManager.scale(0.79F, 0.9F - config.scaleY / 10.0F, 0.79F);
                    GlStateManager.scale(config.scaleX, config.scaleY, config.scaleZ);
                    this.horseLegs.render(par7);
               } else if (data.type == 4) {
                    GlStateManager.translate(0.0F, config.transY * 1.86F, config.transZ * par7);
                    GlStateManager.scale(config.scaleX, config.scaleY, config.scaleZ);
                    if (data.pattern == 1) {
                         this.mermaid2.render(par7);
                    } else {
                         this.mermaid.render(par7);
                    }
               } else if (data.type == 5) {
                    GlStateManager.translate(0.0F, config.transY * 1.86F, config.transZ * par7);
                    GlStateManager.scale(config.scaleX, config.scaleY, config.scaleZ);
                    this.digitigrade.render(par7);
               }

          }
     }

     @Override
     public void rotate(float par1, float par2, float par3, float par4, float par5, float par6) {
          this.rotateLegs(par1, par2, par3, par4, par5, par6);
          this.rotateTail(par1, par2, par3, par4, par5, par6);
     }

     public void rotateLegs(float par1, float par2, float par3, float par4, float par5, float par6) {
          ModelPartData part = this.playerdata.getPartData(EnumParts.LEGS);
          if (part.type == 2) {
               this.spiderLegs.setRotationAngles(this.playerdata, par1, par2, par3, par4, par5, par6, this.player);
          } else if (part.type == 3) {
               this.horseLegs.setRotationAngles(this.playerdata, par1, par2, par3, par4, par5, par6, this.player);
          } else if (part.type == 1) {
               this.naga.isRiding = this.model.isRiding;
               this.naga.isSleeping = this.player.isPlayerSleeping() || this.playerdata.isSleeping();
               this.naga.isCrawling = this.playerdata.animation == EnumAnimation.CRAWLING;
               this.naga.isSneaking = this.model.isSneak;
               this.naga.setRotationAngles(par1, par2, par3, par4, par5, par6, this.player);
          } else if (part.type == 4) {
               this.mermaid.setRotationAngles(par1, par2, par3, par4, par5, par6, this.player, this.playerdata);
               this.mermaid2.setRotationAngles(par1, par2, par3, par4, par5, par6, this.player, this.playerdata);
          } else if (part.type == 5) {
               this.digitigrade.setRotationAngles(par1, par2, par3, par4, par5, par6, this.player);
          }

     }

     public void rotateTail(float par1, float par2, float par3, float par4, float par5, float par6) {
          ModelPartData part = this.playerdata.getPartData(EnumParts.LEGS);
          ModelPartData partTail = this.playerdata.getPartData(EnumParts.TAIL);
          ModelPartConfig config = this.playerdata.getPartConfig(EnumParts.LEG_LEFT);
          float rotateAngleY = MathHelper.cos(par1 * 0.6662F) * 0.2F * par2 + this.model.bipedBody.rotateAngleY;
          float rotateAngleX = MathHelper.sin(par3 * 0.067F) * 0.05F + this.model.bipedBody.rotateAngleX;
          this.rotationPointZ = 0.0F;
          this.rotationPointY = 11.0F;
          if (this.playerdata.animation == EnumAnimation.WAG) {
               rotateAngleY = (float)(Math.sin((double)((float)this.player.ticksExisted * 0.55F)) * 0.44999998807907104D + this.model.bipedBody.rotateAngleY);
          }

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
               if (partTail.type == 2) {
                    rotateAngleX = (float)((double)rotateAngleX + 0.5D);
               }

               if (partTail.type == 0) {
                    rotateAngleX += 0.87F;
               }

               if (partTail.type == 7) {
                    this.fox.setRotationAngles(par1, par2, par3, par4, par5, par6, this.player);
               }
          }

          this.rotationPointZ += this.model.bipedRightLeg.rotationPointZ + 0.5F;
          this.fox.rotateAngleX = this.tail.rotateAngleX = this.feathers.rotateAngleX = this.dragon.rotateAngleX = this.squirrel.rotateAngleX = this.horse.rotateAngleX = this.fin.rotateAngleX = this.rodent.rotateAngleX = rotateAngleX;
          this.fox.rotateAngleY = this.tail.rotateAngleY = this.feathers.rotateAngleY = this.dragon.rotateAngleY = this.squirrel.rotateAngleY = this.horse.rotateAngleY = this.fin.rotateAngleY = this.rodent.rotateAngleY = rotateAngleY;
     }

     @Override
     public void preRender(AbstractClientPlayer player) {
          this.player = player;
          this.playerdata = ModelData.get(player);
          ModelPartData data = this.playerdata.getPartData(EnumParts.LEGS);
          this.model.bipedLeftLeg.isHidden = this.model.bipedRightLeg.isHidden = this.model.bipedLeftLegwear.isHidden = this.model.bipedRightLegwear.isHidden = data == null || data.type != 0;
     }
}
