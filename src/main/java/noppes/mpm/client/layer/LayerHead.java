package noppes.mpm.client.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import noppes.mpm.ModelData;
import noppes.mpm.ModelPartData;
import noppes.mpm.client.gui.util.GuiNPCInterface;
import noppes.mpm.client.model.Model2DRenderer;
import noppes.mpm.client.model.part.head.ModelDuckBeak;
import noppes.mpm.client.model.part.head.ModelHalo;
import noppes.mpm.client.model.part.horns.ModelAntennasBack;
import noppes.mpm.client.model.part.horns.ModelAntennasFront;
import noppes.mpm.client.model.part.horns.ModelAntlerHorns;
import noppes.mpm.client.model.part.horns.ModelBullHorns;
import noppes.mpm.constants.EnumParts;

public class LayerHead extends LayerInterface implements LayerPreRender  {
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

     @Override
     protected void createParts() {
          this.small = new ModelRenderer(this.model, 24, 0);
          this.small.addBox(0.0F, 0.0F, 0.0F, 4, 3, 1);
          this.small.setRotationPoint(-2.0F, -3.0F, -5.0F);
          this.medium = new ModelRenderer(this.model, 24, 0);
          this.medium.addBox(0.0F, 0.0F, 0.0F, 4, 3, 2);
          this.medium.setRotationPoint(-2.0F, -3.0F, -6.0F);
          this.large = new ModelRenderer(this.model, 24, 0);
          this.large.addBox(0.0F, 0.0F, 0.0F, 4, 3, 3);
          this.large.setRotationPoint(-2.0F, -3.0F, -7.0F);
          this.bunnySnout = new ModelRenderer(this.model, 24, 0);
          this.bunnySnout.addBox(1.0F, 1.0F, 0.0F, 4, 2, 1);
          this.bunnySnout.setRotationPoint(-3.0F, -4.0F, -5.0F);
          ModelRenderer tooth = new ModelRenderer(this.model, 24, 3);
          tooth.addBox(2.0F, 3.0F, 0.0F, 2, 1, 1);
          tooth.setRotationPoint(0.0F, 0.0F, 0.0F);
          this.bunnySnout.addChild(tooth);
          this.beak = new ModelDuckBeak(this.model);
          this.beak.setRotationPoint(0.0F, 0.0F, -4.0F);
          this.beard = new Model2DRenderer(this.model, 56.0F, 20.0F, 8, 12);
          this.beard.setRotationOffset(-3.99F, 11.8F, -4.0F);
          this.beard.setScale(0.74F);
          this.hair = new Model2DRenderer(this.model, 56.0F, 20.0F, 8, 12);
          this.hair.setRotationOffset(-3.99F, 11.8F, 3.0F);
          this.hair.setScale(0.75F);
          this.mohawk = new Model2DRenderer(this.model, 0.0F, 0.0F, 64, 64);
          this.mohawk.setTextureSize(64, 64);
          this.mohawk.setRotationOffset(-9.0F, 0.1F, -0.5F);
          this.setRotation(this.mohawk, 0.0F, 1.5707964F, 0.0F);
          this.mohawk.setScale(0.825F);
          this.bull = new ModelBullHorns(this.model);
          this.antlers = new ModelAntlerHorns(this.model);
          this.antennasBack = new ModelAntennasBack(this.model);
          this.antennasFront = new ModelAntennasFront(this.model);
          this.ears = new ModelRenderer(this.model);
          Model2DRenderer right = new Model2DRenderer(this.model, 56.0F, 0.0F, 8, 4);
          right.setRotationPoint(-7.44F, -7.3F, -0.0F);
          right.setScale(0.234F, 0.234F);
          right.setThickness(1.16F);
          this.ears.addChild(right);
          Model2DRenderer left = new Model2DRenderer(this.model, 56.0F, 0.0F, 8, 4);
          left.setRotationPoint(7.44F, -7.3F, 1.15F);
          left.setScale(0.234F, 0.234F);
          this.setRotation(left, 0.0F, 3.1415927F, 0.0F);
          left.setThickness(1.16F);
          this.ears.addChild(left);
          Model2DRenderer right2 = new Model2DRenderer(this.model, 56.0F, 4.0F, 8, 4);
          right2.setRotationPoint(-7.44F, -7.3F, 1.14F);
          right2.setScale(0.234F, 0.234F);
          right2.setThickness(1.16F);
          this.ears.addChild(right2);
          Model2DRenderer left2 = new Model2DRenderer(this.model, 56.0F, 4.0F, 8, 4);
          left2.setRotationPoint(7.44F, -7.3F, 2.31F);
          left2.setScale(0.234F, 0.234F);
          this.setRotation(left2, 0.0F, 3.1415927F, 0.0F);
          left2.setThickness(1.16F);
          this.ears.addChild(left2);
          this.bunnyEars = new ModelRenderer(this.model);
          ModelRenderer earleft = new ModelRenderer(this.model, 56, 0);
          earleft.mirror = true;
          earleft.addBox(-1.466667F, -4.0F, 0.0F, 3, 7, 1);
          earleft.setRotationPoint(2.533333F, -11.0F, 0.0F);
          this.bunnyEars.addChild(earleft);
          ModelRenderer earright = new ModelRenderer(this.model, 56, 0);
          earright.addBox(-1.5F, -4.0F, 0.0F, 3, 7, 1);
          earright.setRotationPoint(-2.466667F, -11.0F, 0.0F);
          this.bunnyEars.addChild(earright);
          this.halo = new ModelHalo();
     }

     @Override
     public void render(float par2, float par3, float par4, float par5, float par6, float scale) {

    	 GlStateManager.translate(this.model.bipedHead.offsetX, this.model.bipedHead.offsetY, this.model.bipedHead.offsetZ);

          this.model.bipedHead.postRender(0.0625F);
          this.renderSnout(scale);
          this.renderBeard(scale);
          this.renderHair(scale);
          this.renderMohawk(scale);
          this.renderHorns(scale);
          this.renderEars(scale);
          this.renderHalo(scale);

          GlStateManager.translate(-this.model.bipedHead.offsetX, -this.model.bipedHead.offsetY, -this.model.bipedHead.offsetZ);
     }

     private void renderHalo(float scale) {
          ModelPartData data = this.playerdata.getPartData(EnumParts.HALO);
          if (data != null) {
               this.preRender(data);
               this.halo.render(scale, this.player);
          }
     }

     private void renderSnout(float scale) {
          ModelPartData data = this.playerdata.getPartData(EnumParts.SNOUT);
          if (data != null) {
               this.preRender(data);
               if (data.type == 0) {
                    this.small.render(scale);
               } else if (data.type == 1) {
                    this.medium.render(scale);
               } else if (data.type == 2) {
                    this.large.render(scale);
               } else if (data.type == 3) {
                    this.bunnySnout.render(scale);
               } else if (data.type == 4) {
                    this.beak.render(scale);
               }

          }
     }

     private void renderBeard(float scale) {
          ModelPartData data = this.playerdata.getPartData(EnumParts.BEARD);
          if (data != null) {
               this.preRender(data);
               this.beard.render(scale);
          }
     }

     private void renderHair(float scale) {
          ModelPartData data = this.playerdata.getPartData(EnumParts.HAIR);
          if (data != null) {
               this.preRender(data);
               this.hair.render(scale);
          }
     }

     private void renderMohawk(float scale) {
          ModelPartData data = this.playerdata.getPartData(EnumParts.MOHAWK);
          if (data != null) {
               this.preRender(data);
               this.mohawk.render(scale);
          }
     }

     private void renderHorns(float scale) {
          ModelPartData data = this.playerdata.getPartData(EnumParts.HORNS);
          if (data != null) {
               this.preRender(data);
               if (data.type == 0) {
                    this.bull.render(scale);
               } else if (data.type == 1) {
                    this.antlers.render(scale);
               } else if (data.type == 2 && data.pattern == 0) {
                    this.antennasBack.render(scale);
               } else if (data.type == 2 && data.pattern == 1) {
                    this.antennasFront.render(scale);
               }

          }
     }

     private void renderEars(float scale) {
          ModelPartData data = this.playerdata.getPartData(EnumParts.EARS);
          if (data != null) {
               this.preRender(data);
               if (data.type == 0) {
                    this.ears.render(scale);
               } else if (data.type == 1) {
                    this.bunnyEars.render(scale);
               }

          }
     }

     @Override
     public void rotate(float par2, float par3, float par4, float par5, float par6, float scale) {
          ModelRenderer head = this.model.bipedHead;
          if (head.rotateAngleX < 0.0F) {
               this.beard.rotateAngleX = 0.0F;
               this.hair.rotateAngleX = -head.rotateAngleX * 1.2F;
               if (head.rotateAngleX > -1.0F) {
                    this.hair.rotationPointY = -head.rotateAngleX * 1.5F;
                    this.hair.rotationPointZ = -head.rotateAngleX * 1.5F;
               }
          } else {
               this.hair.rotateAngleX = 0.0F;
               this.hair.rotationPointY = 0.0F;
               this.hair.rotationPointZ = 0.0F;
               this.beard.rotateAngleX = -head.rotateAngleX;
          }

     }

     @Override
     public void preRender(AbstractClientPlayer player) {
          this.player = player;
          this.playerdata = ModelData.get(player);
          ModelPartData data = this.playerdata.getOrCreatePart(EnumParts.HEAD);
          this.model.bipedHead.isHidden = this.model.bipedHeadwear.isHidden = data == null || data.type != 0;
          //The following code will fix compatibility with RealRender
          //this.model.bipedHead.isHidden = this.model.bipedHeadwear.isHidden = data == null || data.type != 0 || (this.playerdata.player == Minecraft.getMinecraft().thePlayer && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 && !(Minecraft.getMinecraft().currentScreen instanceof GuiNPCInterface));
     }
}
