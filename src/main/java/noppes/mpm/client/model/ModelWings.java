package noppes.mpm.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelWings extends ModelBase {
     public ModelRenderer body;
     public ModelRenderer head;
     public ModelRenderer left_wing_1;
     public ModelRenderer right_wing_1;
     public ModelRenderer left_wing_2;
     public ModelRenderer left_wing_0;
     public ModelRenderer left_wing_3;
     public ModelRenderer left_wing_4;
     public ModelRenderer right_wing_2;
     public ModelRenderer right_wing_0;
     public ModelRenderer right_wing_3;
     public ModelRenderer right_wing_4;

     public ModelWings() {
          this.textureWidth = 81;
          this.textureHeight = 34;
          this.right_wing_2 = new ModelRenderer(this, 42, 0);
          this.right_wing_2.setRotationPoint(0.0F, 4.0F, -1.0F);
          this.right_wing_2.addBox(-1.0F, 0.0F, 0.0F, 2, 7, 2, 0.0F);
          this.setRotateAngle(this.right_wing_2, 1.2292354F, 0.0F, 0.0F);
          this.left_wing_3 = new ModelRenderer(this, 26, 0);
          this.left_wing_3.setRotationPoint(0.0F, 7.0F, 2.0F);
          this.left_wing_3.addBox(-1.0F, 0.0F, -2.0F, 2, 5, 2, 0.0F);
          this.setRotateAngle(this.left_wing_3, -1.2292354F, 0.0F, 0.0F);
          this.right_wing_1 = new ModelRenderer(this, 8, 0);
          this.right_wing_1.setRotationPoint(-2.4F, 2.0F, 1.5F);
          this.right_wing_1.addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2, 0.0F);
          this.setRotateAngle(this.right_wing_1, 1.5358897F, -0.9424778F, 0.0F);
          this.left_wing_0 = new ModelRenderer(this, 6, 0);
          this.left_wing_0.setRotationPoint(2.4F, 2.0F, 1.5F);
          this.left_wing_0.addBox(-3.4F, -2.0F, -15.0F, 1, 11, 18, 0.0F);
          this.right_wing_3 = new ModelRenderer(this, 50, 0);
          this.right_wing_3.setRotationPoint(0.0F, 7.0F, 2.0F);
          this.right_wing_3.addBox(-1.0F, 0.0F, -2.0F, 2, 5, 2, 0.0F);
          this.setRotateAngle(this.right_wing_3, -1.2292354F, 0.0F, 0.0F);
          this.left_wing_2 = new ModelRenderer(this, 16, 0);
          this.left_wing_2.setRotationPoint(0.0F, 4.0F, -1.0F);
          this.left_wing_2.addBox(-1.0F, 0.0F, 0.0F, 2, 7, 2, 0.0F);
          this.setRotateAngle(this.left_wing_2, 1.2292354F, 0.0F, 0.0F);
          this.body = new ModelRenderer(this, 0, 0);
          this.body.setRotationPoint(0.0F, 0.0F, 0.0F);
          this.body.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
          this.head = new ModelRenderer(this, 0, 34);
          this.head.setRotationPoint(0.0F, 0.0F, 0.0F);
          this.head.addBox(-4.0F, -8.0F, -4.0F, 0, 0, 0, 0.0F);
          this.left_wing_1 = new ModelRenderer(this, 0, 0);
          this.left_wing_1.setRotationPoint(2.4F, 2.0F, 1.5F);
          this.left_wing_1.addBox(-1.0F, 0.0F, -1.0F, 2, 4, 2, 0.0F);
          this.setRotateAngle(this.left_wing_1, 1.5358897F, 0.9424778F, 0.0F);
          this.right_wing_4 = new ModelRenderer(this, 64, 0);
          this.right_wing_4.setRotationPoint(0.0F, 5.0F, 0.0F);
          this.right_wing_4.addBox(-1.0F, 0.0F, -2.0F, 2, 5, 2, 0.0F);
          this.setRotateAngle(this.right_wing_4, -1.1383038F, 0.0F, 0.0F);
          this.left_wing_4 = new ModelRenderer(this, 34, 0);
          this.left_wing_4.setRotationPoint(0.0F, 5.0F, 0.0F);
          this.left_wing_4.addBox(-1.0F, 0.0F, -2.0F, 2, 5, 2, 0.0F);
          this.setRotateAngle(this.left_wing_4, -1.1383038F, 0.0F, 0.0F);
          this.right_wing_0 = new ModelRenderer(this, 44, 0);
          this.right_wing_0.setRotationPoint(-2.4F, 2.0F, 1.5F);
          this.right_wing_0.addBox(2.4F, -2.0F, -15.0F, 1, 11, 18, 0.0F);
          this.right_wing_1.addChild(this.right_wing_2);
          this.left_wing_2.addChild(this.left_wing_3);
          this.body.addChild(this.right_wing_1);
          this.left_wing_1.addChild(this.left_wing_0);
          this.right_wing_2.addChild(this.right_wing_3);
          this.left_wing_1.addChild(this.left_wing_2);
          this.body.addChild(this.left_wing_1);
          this.right_wing_3.addChild(this.right_wing_4);
          this.left_wing_3.addChild(this.left_wing_4);
          this.right_wing_1.addChild(this.right_wing_0);
     }

     @Override
     public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float f5) {
          this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, f5, entityIn);
          this.isChild = false;
          GlStateManager.pushMatrix();
          if (this.isChild) {
               GlStateManager.scale(0.75F, 0.75F, 0.75F);
               GlStateManager.translate(0.0F, 16.0F * f5, 0.0F);
               if (entityIn.isSneaking()) {
                    GlStateManager.translate(0.0F, 0.2F, 0.0F);
               }

               GlStateManager.popMatrix();
               GlStateManager.pushMatrix();
               GlStateManager.scale(0.5F, 0.5F, 0.5F);
               GlStateManager.translate(0.0F, 24.0F * f5, 0.0F);
               if (entityIn.isSneaking()) {
                    GlStateManager.translate(0.0F, 0.2F, 0.0F);
               }

               this.renderWings(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, f5);
          } else {
               if (entityIn.isSneaking()) {
                    GlStateManager.translate(0.0F, 0.2F, 0.0F);
               }

               this.renderWings(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, f5);
          }

          GlStateManager.popMatrix();
     }

     public void renderWings(Entity player, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float f5) {
          float motion = Math.abs(MathHelper.sin(limbSwing * 0.033F + 3.1415927F) * 0.4F) * limbSwingAmount;
          boolean flapWings = player.worldObj.isAirBlock(player.getPosition().down());
          float speed = 0.55F + 0.5F * motion;
          float y = MathHelper.sin(ageInTicks * 0.35F);
          float flap = y * 0.5F * speed;
          GlStateManager.pushMatrix();
          if (flapWings) {
               GlStateManager.rotate(flap * 20.0F, 0.0F, 1.0F, 0.0F);
          }

          this.left_wing_1.render(f5);
          GlStateManager.popMatrix();
          GlStateManager.pushMatrix();
          if (flapWings) {
               GlStateManager.rotate(-flap * 20.0F, 0.0F, 1.0F, 0.0F);
          }

          this.right_wing_1.render(f5);
          GlStateManager.popMatrix();
     }

     public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
          modelRenderer.rotateAngleX = x;
          modelRenderer.rotateAngleY = y;
          modelRenderer.rotateAngleZ = z;
     }
}
