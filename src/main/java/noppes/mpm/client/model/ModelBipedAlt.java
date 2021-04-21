package noppes.mpm.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import noppes.mpm.ModelData;
import noppes.mpm.ModelPartConfig;
import noppes.mpm.client.model.animation.AniBow;
import noppes.mpm.client.model.animation.AniCrawling;
import noppes.mpm.client.model.animation.AniDancing;
import noppes.mpm.client.model.animation.AniHug;
import noppes.mpm.client.model.animation.AniNo;
import noppes.mpm.client.model.animation.AniPoint;
import noppes.mpm.client.model.animation.AniWaving;
import noppes.mpm.client.model.animation.AniYes;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.constants.EnumParts;

public class ModelBipedAlt extends ModelBiped {
     private Map map = new HashMap();

     public ModelBipedAlt(float scale) {
          super(scale);
          this.bipedLeftArm = this.createScale(this.bipedLeftArm, EnumParts.ARM_LEFT);
          this.bipedRightArm = this.createScale(this.bipedRightArm, EnumParts.ARM_RIGHT);
          this.bipedLeftLeg = this.createScale(this.bipedLeftLeg, EnumParts.LEG_LEFT);
          this.bipedRightLeg = this.createScale(this.bipedRightLeg, EnumParts.LEG_RIGHT);
          this.bipedHead = this.createScale(this.bipedHead, EnumParts.HEAD);
          this.bipedHeadwear = this.createScale(this.bipedHeadwear, EnumParts.HEAD);
          this.bipedBody = this.createScale(this.bipedBody, EnumParts.BODY);
     }

     private ModelScaleRenderer createScale(ModelRenderer renderer, EnumParts part) {
          int textureX = (Integer)ObfuscationReflectionHelper.getPrivateValue(ModelRenderer.class, renderer, 2);
          int textureY = (Integer)ObfuscationReflectionHelper.getPrivateValue(ModelRenderer.class, renderer, 3);
          ModelScaleRenderer model = new ModelScaleRenderer(this, textureX, textureY, part);
          model.textureHeight = renderer.textureHeight;
          model.textureWidth = renderer.textureWidth;
          model.childModels = renderer.childModels;
          model.cubeList = renderer.cubeList;
          copyModelAngles(renderer, model);
          List list = (List)this.map.get(part);
          if (list == null) {
               this.map.put(part, list = new ArrayList());
          }

          ((List)list).add(model);
          return model;
     }

     @Override
     public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
          EntityPlayer player = (EntityPlayer)entity;
          ModelData data = ModelData.get(player);
          Iterator var10 = this.map.keySet().iterator();

          while(var10.hasNext()) {
               EnumParts part = (EnumParts)var10.next();
               ModelPartConfig config = data.getPartConfig(part);

               ModelScaleRenderer model;
               for(Iterator var13 = ((List)this.map.get(part)).iterator(); var13.hasNext(); model.config = config) {
                    model = (ModelScaleRenderer)var13.next();
               }
          }

          this.bipedLeftLeg.isHidden = this.bipedRightLeg.isHidden = data.getPartData(EnumParts.LEGS).type != 0;
          if (!this.isRiding) {
               this.isRiding = data.animation == EnumAnimation.SITTING;
          }

          if (this.isSneak && (data.animation == EnumAnimation.CRAWLING || data.isSleeping())) {
               this.isSneak = false;
          }

          this.bipedBody.rotationPointX = this.bipedBody.rotationPointY = this.bipedBody.rotationPointZ = 0.0F;
          this.bipedBody.rotateAngleX = this.bipedBody.rotateAngleY = this.bipedBody.rotateAngleZ = 0.0F;
          this.bipedHeadwear.rotateAngleX = this.bipedHead.rotateAngleX = 0.0F;
          this.bipedHeadwear.rotateAngleZ = this.bipedHead.rotateAngleZ = 0.0F;
          this.bipedHeadwear.rotationPointX = this.bipedHead.rotationPointX = 0.0F;
          this.bipedHeadwear.rotationPointY = this.bipedHead.rotationPointY = 0.0F;
          this.bipedHeadwear.rotationPointZ = this.bipedHead.rotationPointZ = 0.0F;
          this.bipedLeftLeg.rotateAngleX = 0.0F;
          this.bipedLeftLeg.rotateAngleY = 0.0F;
          this.bipedLeftLeg.rotateAngleZ = 0.0F;
          this.bipedRightLeg.rotateAngleX = 0.0F;
          this.bipedRightLeg.rotateAngleY = 0.0F;
          this.bipedRightLeg.rotateAngleZ = 0.0F;
          this.bipedLeftArm.rotationPointX = 0.0F;
          this.bipedLeftArm.rotationPointY = 2.0F;
          this.bipedLeftArm.rotationPointZ = 0.0F;
          this.bipedRightArm.rotationPointX = 0.0F;
          this.bipedRightArm.rotationPointY = 2.0F;
          this.bipedRightArm.rotationPointZ = 0.0F;
          super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);
          if (!data.isSleeping() && !player.isPlayerSleeping()) {
               if (data.animation == EnumAnimation.CRY) {
                    this.bipedHeadwear.rotateAngleX = this.bipedHead.rotateAngleX = 0.7F;
               } else if (data.animation == EnumAnimation.HUG) {
                    AniHug.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity, this);
               } else if (data.animation == EnumAnimation.CRAWLING) {
                    AniCrawling.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity, this);
               } else if (data.animation == EnumAnimation.WAVING) {
                    AniWaving.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity, this);
               } else if (data.animation == EnumAnimation.DANCING) {
                    AniDancing.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity, this);
               } else if (data.animation == EnumAnimation.BOW) {
                    AniBow.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity, this, data);
               } else if (data.animation == EnumAnimation.YES) {
                    AniYes.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity, this, data);
               } else if (data.animation == EnumAnimation.NO) {
                    AniNo.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity, this, data);
               } else if (data.animation == EnumAnimation.POINT) {
                    AniPoint.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity, this);
               } else if (this.isSneak) {
                    this.bipedBody.rotateAngleX = 0.5F / data.getPartConfig(EnumParts.BODY).scaleY;
               }
          } else if (this.bipedHead.rotateAngleX < 0.0F) {
               this.bipedHead.rotateAngleX = 0.0F;
               this.bipedHeadwear.rotateAngleX = 0.0F;
          }

     }

     public static float getPartConfigScale (Entity entity, int bodyPart, int axis) {
    	 EntityPlayer player;
         ModelData data;

    	 if (entity instanceof EntityPlayer) {
    		 player = (EntityPlayer)entity;
             data = ModelData.get(player);
    	 } else {
    		 return 1.0F;
    	 }

         switch(bodyPart) {
			case 0 : {
				switch(axis) {
					case 3:
						return data.getPartConfig(EnumParts.HEAD).scaleX;
					case 4:
						return data.getPartConfig(EnumParts.HEAD).scaleY;
					case 5:
						return data.getPartConfig(EnumParts.HEAD).scaleZ;
				}
			}; case 6 : {
				switch(axis) {
					case 3:
						return data.getPartConfig(EnumParts.BODY).scaleX;
					case 4:
						return data.getPartConfig(EnumParts.BODY).scaleY;
					case 5:
						return data.getPartConfig(EnumParts.BODY).scaleZ;
				}
			}; case 12 : {
				switch(axis) {
					case 3:
						return data.getPartConfig(EnumParts.ARM_RIGHT).scaleX;
					case 4:
						return data.getPartConfig(EnumParts.ARM_RIGHT).scaleY;
					case 5:
						return data.getPartConfig(EnumParts.ARM_RIGHT).scaleZ;
				}
			}; case 18 : {
				switch(axis) {
					case 3:
						return data.getPartConfig(EnumParts.ARM_LEFT).scaleX;
					case 4:
						return data.getPartConfig(EnumParts.ARM_LEFT).scaleY;
					case 5:
						return data.getPartConfig(EnumParts.ARM_LEFT).scaleZ;
				}
			}; case 24 : {
				switch(axis) {
					case 3:
						return data.getPartConfig(EnumParts.LEG_RIGHT).scaleX;
					case 4:
						return data.getPartConfig(EnumParts.LEG_RIGHT).scaleY;
					case 5:
						return data.getPartConfig(EnumParts.LEG_RIGHT).scaleZ;
				}
			}; case 30 : {
					switch(axis) {
					case 3:
						return data.getPartConfig(EnumParts.LEG_LEFT).scaleX;
					case 4:
						return data.getPartConfig(EnumParts.LEG_LEFT).scaleY;
					case 5:
						return data.getPartConfig(EnumParts.LEG_LEFT).scaleZ;
				}
			}; default : {
				return 1.0F;
			}
		}
     }
}
