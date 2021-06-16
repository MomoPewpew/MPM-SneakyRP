package noppes.mpm.client.layer;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.mpm.MorePlayerModels;

public class LayerProp extends LayerInterface {

	public LayerProp(RenderPlayer render) {
		super(render);
	}

	@Override
	public void render(float par2, float par3, float par4, float par5, float par6, float par7) {
		Minecraft minecraft = Minecraft.getMinecraft();

		for (int i = 0; i < this.playerdata.propItemStack.size(); i++) {
			ItemStack propItemStack = this.playerdata.propItemStack.get(i);

			if (propItemStack != null) {
				ModelRenderer propBodyPart = null;
				ModelRenderer propRenderer = new ModelRenderer(this.model);
				Float propScaleX = this.playerdata.propScaleX.get(i);
				Float propScaleY = this.playerdata.propScaleY.get(i);
				Float propScaleZ = this.playerdata.propScaleZ.get(i);
				Float propOffsetX = this.playerdata.propOffsetX.get(i);
				Float propOffsetY = this.playerdata.propOffsetY.get(i);
				Float propOffsetZ = this.playerdata.propOffsetZ.get(i);
				Float propRotateX = (float) Math.toRadians(this.playerdata.propRotateX.get(i));
				Float propRotateY = (float) Math.toRadians(this.playerdata.propRotateY.get(i));
				Float propRotateZ = (float) Math.toRadians(this.playerdata.propRotateZ.get(i));

				Float partModifierX = null;
				Float partModifierY = null;

	    		 switch(this.playerdata.propBodyPartName.get(i)) {
	    		     case "hat":
		    		 case "head":
		    			 propBodyPart = this.model.bipedHead;
		    			 break;
		    		 case "model":
	    			 	 propBodyPart = this.model.bipedHeadwear;
	    			 	 break;
		    		 case "body":
		    		 case "torso":
		    			 propBodyPart = this.model.bipedBody;
		    			 break;
		    		 case "arm":
		    		 case "hand":
		    		 case "armleft":
		    		 case "handleft":
		    		 case "leftarm":
		    		 case "lefthand":
		    			 propBodyPart = this.model.bipedLeftArm;
		    			 partModifierX = -0.325F;
		    			 partModifierY = -0.125F;
		    			 break;
		    		 case "armright":
		    		 case "handright":
		    		 case "rightarm":
		    		 case "righthand":
		    			 propBodyPart = this.model.bipedRightArm;
		    			 break;
		    		 case "leg":
		    		 case "foot":
		    		 case "legleft":
		    		 case "footlef":
		    		 case "leftleg":
		    		 case "leftfoot":
		    			 propBodyPart = this.model.bipedLeftLeg;
		    			 break;
		    		 case "legright":
		    		 case "footright":
		    		 case "rightleg":
		    		 case "rightfoot":
		    			 propBodyPart = this.model.bipedRightLeg;
		    			 break;
	    		 }


	    		//Calculate prop offset
	    		Float anglePrev;
	    		Float hyp;
	    		//Apply pitch
	    		if (propOffsetZ == 0) {
	    			if (propOffsetY <= 0) {
	    				anglePrev = 0.0F;
		    			hyp = propOffsetY;
	    			} else {
	    				anglePrev = (float) Math.PI;
	    				hyp = -propOffsetY;
	    			}
	    		} else {
	    			anglePrev = (float) Math.atan2(propOffsetZ, propOffsetY);
	    			hyp = (float) (propOffsetZ / Math.sin(anglePrev));
	    		}

	    		Float Zpitch = (float) (Math.sin(anglePrev + propBodyPart.rotateAngleX) * hyp);
	    		Float Ypitch = (float) (Math.cos(anglePrev + propBodyPart.rotateAngleX) * hyp);

	    		//Apply yaw
	    		if (propOffsetX == 0) {
	    			if (Zpitch >= 0) {
	    				anglePrev = 0.0F;
	    				hyp = Zpitch;
	    			} else {
	    				anglePrev = (float) Math.PI;
	    				hyp = -Zpitch;
	    			}
	    		} else {
	    			anglePrev = (float) Math.atan2(propOffsetX, Zpitch);
	    			hyp = (float) (propOffsetX / Math.sin(anglePrev));
	    		}

	    		Float Xyaw = (float) (Math.sin(anglePrev + propBodyPart.rotateAngleY) * hyp);
	    		Float propOffsetZCorrected = (float) (Math.cos(anglePrev + propBodyPart.rotateAngleY) * hyp);

	    		//Apply roll
	    		if (Xyaw > -0.0001 && Xyaw < 0.0001) {
	    			if (Ypitch <= 0) {
	    				anglePrev = 0.0F;
		    			hyp = Ypitch;
	    			} else {
	    				anglePrev = (float) Math.PI;
		    			hyp = -Ypitch;
	    			}
	    		} else {
	    			anglePrev = (float) Math.atan2(Xyaw, Ypitch);
		    		hyp = (float) (Xyaw / Math.sin(anglePrev));
	    		}

	    		Float propOffsetXCorrected = (float) (Math.sin(anglePrev - propBodyPart.rotateAngleZ) * hyp);
	    		Float propOffsetYCorrected = (float) (Math.cos(anglePrev - propBodyPart.rotateAngleZ) * hyp);

	    		//Calculate prop facing
	    		//First we convert our requested angles to a vector offset that our prop would theoretically be facing
	    		//After rotating our limbs we will take this offset and convert it back into angles
	    		//We do this because all other methods fail for some reason
	    		//Apply pitch
				anglePrev = (float) Math.PI;
				hyp = -1.0F;

	    		Float ZFacingpitch = (float) (Math.sin(anglePrev + propRotateX) * hyp);
	    		Float YFacingpitch = (float) (Math.cos(anglePrev + propRotateX) * hyp);

	    		//Apply yaw
    			if (ZFacingpitch >= 0) {
    				anglePrev = 0.0F;
    				hyp = ZFacingpitch;
    			} else {
    				anglePrev = (float) Math.PI;
    				hyp = -ZFacingpitch;
    			}

	    		Float XFacingyaw = (float) (Math.sin(anglePrev + propRotateY) * hyp);
	    		Float propOffsetZFacingCorrected = (float) (Math.cos(anglePrev + propRotateY) * hyp);

	    		//Apply roll
	    		if (XFacingyaw > -0.0001 && XFacingyaw < 0.0001) {
	    			if (YFacingpitch <= 0) {
	    				anglePrev = 0.0F;
		    			hyp = YFacingpitch;
	    			} else {
	    				anglePrev = (float) Math.PI;
		    			hyp = -YFacingpitch;
	    			}
	    		} else {
	    			anglePrev = (float) Math.atan2(XFacingyaw, YFacingpitch);
		    		hyp = (float) (XFacingyaw / Math.sin(anglePrev));
	    		}

	    		Float propOffsetXFacingCorrected = (float) (Math.sin(anglePrev - propRotateZ) * hyp);
	    		Float propOffsetYFacingCorrected = (float) (Math.cos(anglePrev - propRotateZ) * hyp);


	    		//Here we convert our offset at 0 position into new offsets at different limb rotations.
	    		Float propOffsetXRotatedFacingVector = propOffsetX + propOffsetXFacingCorrected;
	    		Float propOffsetYRotatedFacingVector = propOffsetY + propOffsetYFacingCorrected;
	    		Float propOffsetZRotatedFacingVector = propOffsetZ + propOffsetZFacingCorrected;
	    		//Apply pitch
	    		if (propOffsetZRotatedFacingVector == 0) {
	    			if (propOffsetYRotatedFacingVector <= 0) {
	    				anglePrev = 0.0F;
		    			hyp = propOffsetYRotatedFacingVector;
	    			} else {
	    				anglePrev = (float) Math.PI;
	    				hyp = -propOffsetYRotatedFacingVector;
	    			}
	    		} else {
	    			anglePrev = (float) Math.atan2(propOffsetZRotatedFacingVector, propOffsetYRotatedFacingVector);
	    			hyp = (float) (propOffsetZRotatedFacingVector / Math.sin(anglePrev));
	    		}

	    		Float ZRotatedFacingVectorpitch = (float) (Math.sin(anglePrev + propBodyPart.rotateAngleX) * hyp);
	    		Float YRotatedFacingVectorpitch = (float) (Math.cos(anglePrev + propBodyPart.rotateAngleX) * hyp);

	    		//Apply yaw
	    		if (propOffsetXRotatedFacingVector == 0) {
	    			if (ZRotatedFacingVectorpitch >= 0) {
	    				anglePrev = 0.0F;
	    				hyp = ZRotatedFacingVectorpitch;
	    			} else {
	    				anglePrev = (float) Math.PI;
	    				hyp = -ZRotatedFacingVectorpitch;
	    			}
	    		} else {
	    			anglePrev = (float) Math.atan2(propOffsetXRotatedFacingVector, ZRotatedFacingVectorpitch);
	    			hyp = (float) (propOffsetXRotatedFacingVector / Math.sin(anglePrev));
	    		}

	    		Float XRotatedFacingVectoryaw = (float) (Math.sin(anglePrev + propBodyPart.rotateAngleY) * hyp);
	    		Float propOffsetZRotatedFacingVectorCorrected = (float) (Math.cos(anglePrev + propBodyPart.rotateAngleY) * hyp);

	    		//Apply roll
	    		if (XRotatedFacingVectoryaw > -0.0001 && XRotatedFacingVectoryaw < 0.0001) {
	    			if (YRotatedFacingVectorpitch <= 0) {
	    				anglePrev = 0.0F;
		    			hyp = YRotatedFacingVectorpitch;
	    			} else {
	    				anglePrev = (float) Math.PI;
		    			hyp = -YRotatedFacingVectorpitch;
	    			}
	    		} else {
	    			anglePrev = (float) Math.atan2(XRotatedFacingVectoryaw, YRotatedFacingVectorpitch);
		    		hyp = (float) (XRotatedFacingVectoryaw / Math.sin(anglePrev));
	    		}

	    		Float propOffsetXRotatedFacingVectorCorrected = (float) (Math.sin(anglePrev - propBodyPart.rotateAngleZ) * hyp);
	    		Float propOffsetYRotatedFacingVectorCorrected = (float) (Math.cos(anglePrev - propBodyPart.rotateAngleZ) * hyp);

	    		Float targetVectorOffsetX = propOffsetXRotatedFacingVectorCorrected - propOffsetXCorrected;
	    		Float targetVectorOffsetY = propOffsetYRotatedFacingVectorCorrected - propOffsetYCorrected;
	    		Float targetVectorOffsetZ = propOffsetZRotatedFacingVectorCorrected - propOffsetZCorrected;


	    		//propRenderer.setRotationPoint(propOffsetXCorrected, propOffsetYCorrected, propOffsetZCorrected);

				GlStateManager.pushMatrix();
				GlStateManager.translate((propBodyPart.offsetX - propOffsetXCorrected - partModifierX), (propBodyPart.offsetY - propOffsetYCorrected - partModifierY), (propBodyPart.offsetZ - propOffsetZCorrected));

				propRenderer.rotateAngleX = propBodyPart.rotateAngleX;
				propRenderer.rotateAngleY = propBodyPart.rotateAngleY;
				propRenderer.rotateAngleZ = propBodyPart.rotateAngleZ;
				propRenderer.postRender(par7);

				IBakedModel model = minecraft.getRenderItem().getItemModelMesher().getItemModel(propItemStack);
				ItemTransformVec3f transformVec = model.getItemCameraTransforms().thirdperson_right;
				GlStateManager.scale((-propScaleX * (transformVec.scale.x + ItemCameraTransforms.offsetScaleX)), (-propScaleY * (transformVec.scale.y + ItemCameraTransforms.offsetScaleY)), (propScaleZ * (transformVec.scale.z + ItemCameraTransforms.offsetScaleZ)));
				minecraft.getItemRenderer().renderItem(this.player, propItemStack, TransformType.NONE);

				GlStateManager.popMatrix();
	          }
		}
     }

     @Override
     public void rotate(float par1, float par2, float par3, float par4, float par5, float par6) {
     }
}
